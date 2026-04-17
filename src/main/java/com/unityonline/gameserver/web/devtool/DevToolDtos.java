package com.unityonline.gameserver.web.devtool;

import com.unityonline.gameserver.common.auth.AuthTokenPayload;
import com.unityonline.gameserver.module.inventory.dto.InventoryItemDto;
import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import com.unityonline.gameserver.module.player.dto.PlayerProfileDto;
import com.unityonline.gameserver.module.quest.dto.QuestDto;
import com.unityonline.gameserver.module.rank.dto.RankEntryDto;
import com.unityonline.gameserver.module.save.dto.SaveGameDto;
import com.unityonline.gameserver.module.stage.dto.StageResultDto;
import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import java.util.List;

public final class DevToolDtos {

    private DevToolDtos() {
    }

    public record RegisterRequest(
            String username,
            String password,
            String nickname
    ) {
    }

    public record LoginRequest(
            String username,
            String password
    ) {
    }

    public record SessionResponse(
            Long accountId,
            Long playerId,
            String username,
            String nickname,
            String token
    ) {
    }

    public record TokenValidateView(
            boolean valid,
            AuthTokenPayload payload
    ) {
    }

    public record AddItemRequest(
            Long itemId,
            Integer itemType,
            Long quantity,
            String extJson
    ) {
    }

    public record ConsumeItemRequest(
            Long itemId,
            Long quantity
    ) {
    }

    public record InventoryItemView(
            Long itemId,
            Integer itemType,
            Long quantity,
            String extJson
    ) {
    }

    public record InventoryView(
            Long playerId,
            List<InventoryItemView> items
    ) {
    }

    public record SaveUploadRequest(
            Integer slot,
            String saveDataJson,
            String saveVersion
    ) {
    }

    public record SavePullRequest(
            Integer slot
    ) {
    }

    public record QuestProgressRequest(
            Long questId,
            Integer progressDelta
    ) {
    }

    public record QuestRewardRequest(
            Long questId
    ) {
    }

    public record StageReportRequest(
            Long stageId,
            Integer starCount,
            boolean cleared,
            Long score
    ) {
    }

    public record PlatformRequest(
            String platform
    ) {
    }

    public record RankReportRequest(
            Integer rankType,
            Long score
    ) {
    }

    public record RankListRequest(
            Integer rankType,
            Integer pageNo,
            Integer pageSize
    ) {
    }

    public record RankMyRequest(
            Integer rankType
    ) {
    }

    public record OverviewResponse(
            PlayerProfileDto profile,
            InventoryView inventory,
            List<QuestDto> quests,
            List<StageResultDto> stages,
            SaveGameDto save,
            RankEntryDto myRank,
            List<NoticeEntity> notices,
            VersionConfigEntity version
    ) {
    }

    public static InventoryItemView toView(InventoryItemDto item) {
        return new InventoryItemView(
                item.itemId(),
                item.itemType(),
                item.quantity(),
                item.extJson()
        );
    }
}
