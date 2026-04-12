package com.unityonline.gameserver.module.rank.service;

import com.unityonline.gameserver.common.constant.RedisKeyConstants;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.player.entity.PlayerEntity;
import com.unityonline.gameserver.module.player.mapper.PlayerMapper;
import com.unityonline.gameserver.module.rank.dto.RankEntryDto;
import com.unityonline.gameserver.module.rank.entity.RankRecordEntity;
import com.unityonline.gameserver.module.rank.mapper.RankRecordMapper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RankService {

    private final RankRecordMapper rankRecordMapper;
    private final PlayerMapper playerMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public RankService(RankRecordMapper rankRecordMapper,
                       PlayerMapper playerMapper,
                       StringRedisTemplate stringRedisTemplate) {
        this.rankRecordMapper = rankRecordMapper;
        this.playerMapper = playerMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 上报排行榜分数，MySQL 先落地，Redis 在事务提交后刷新。
     */
    @Transactional
    public RankEntryDto reportScore(Integer rankType, Long playerId, Long score) {
        if (score == null || score < 0) {
            throw new BizException(4601, "score must be greater than or equal to 0");
        }
        rankRecordMapper.upsert(rankType, playerId, score);
        long finalScore = rankRecordMapper.findByRankTypeAndPlayerId(rankType, playerId)
                .map(RankRecordEntity::getScore)
                .orElse(score);
        int rankNo = rankRecordMapper.calculateRankNo(rankType, playerId, finalScore);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            /**
             * 事务提交后刷新 Redis 排行榜，避免数据库回滚时出现缓存脏写。
             */
            @Override
            public void afterCommit() {
                stringRedisTemplate.opsForZSet().add(buildKey(rankType), String.valueOf(playerId), (double) finalScore);
            }
        });
        String nickname = playerMapper.findByPlayerId(playerId).map(PlayerEntity::getNickname).orElse("");
        return new RankEntryDto(rankNo, playerId, nickname, finalScore);
    }

    /**
     * 查询排行榜分页数据。
     */
    public List<RankEntryDto> queryRankList(Integer rankType, Integer pageNo, Integer pageSize) {
        ensureRankCacheLoaded(rankType);
        long start = (long) Math.max(pageNo - 1, 0) * pageSize;
        long end = start + pageSize - 1;
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(buildKey(rankType), start, end);
        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }
        List<ZSetOperations.TypedTuple<String>> orderedTuples = new java.util.ArrayList<>(tuples);
        Map<Long, String> nicknameMap = queryNicknameMap(orderedTuples.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .toList());
        int rankCursor = (int) start + 1;
        return java.util.stream.IntStream.range(0, orderedTuples.size())
                .mapToObj(index -> {
                    ZSetOperations.TypedTuple<String> tuple = orderedTuples.get(index);
                    long playerId = Long.parseLong(tuple.getValue());
                    return new RankEntryDto(
                        rankCursor + index,
                        playerId,
                        nicknameMap.getOrDefault(playerId, ""),
                        tuple.getScore() == null ? 0L : tuple.getScore().longValue());
                })
                .toList();
    }

    /**
     * 查询玩家自己的当前排名。
     */
    public RankEntryDto queryMyRank(Integer rankType, Long playerId) {
        ensureRankCacheLoaded(rankType);
        int rankNo = getRankNo(rankType, playerId).orElse(0);
        long score = rankRecordMapper.findByRankTypeAndPlayerId(rankType, playerId)
                .map(RankRecordEntity::getScore)
                .orElse(0L);
        String nickname = playerMapper.findByPlayerId(playerId).map(PlayerEntity::getNickname).orElse("");
        return new RankEntryDto(rankNo, playerId, nickname, score);
    }

    /**
     * 如果 Redis 排行榜为空，则从 MySQL 回填一次。
     */
    public void ensureRankCacheLoaded(Integer rankType) {
        Long size = stringRedisTemplate.opsForZSet().zCard(buildKey(rankType));
        if (size != null && size > 0) {
            return;
        }
        for (RankRecordEntity entity : rankRecordMapper.findByRankType(rankType)) {
            stringRedisTemplate.opsForZSet().add(buildKey(rankType), String.valueOf(entity.getPlayerId()), entity.getScore().doubleValue());
        }
    }

    /**
     * 获取玩家在排行榜中的名次。
     */
    private java.util.Optional<Integer> getRankNo(Integer rankType, Long playerId) {
        ensureRankCacheLoaded(rankType);
        Long rank = stringRedisTemplate.opsForZSet().reverseRank(buildKey(rankType), String.valueOf(playerId));
        return rank == null ? java.util.Optional.empty() : java.util.Optional.of(rank.intValue() + 1);
    }

    /**
     * 批量查询排行榜玩家昵称。
     */
    private Map<Long, String> queryNicknameMap(Collection<Long> playerIds) {
        return playerMapper.findByPlayerIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerEntity::getPlayerId, PlayerEntity::getNickname));
    }

    private String buildKey(Integer rankType) {
        return RedisKeyConstants.RANK_ZSET_PREFIX + rankType;
    }
}
