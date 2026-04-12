package com.unityonline.gameserver.module.auth.service;

import com.unityonline.gameserver.common.auth.AuthTokenPayload;
import com.unityonline.gameserver.common.auth.AuthTokenService;
import com.unityonline.gameserver.common.config.GameSecurityProperties;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.util.IdGenerator;
import com.unityonline.gameserver.common.util.PasswordUtils;
import com.unityonline.gameserver.common.util.TokenUtils;
import com.unityonline.gameserver.module.auth.dto.AuthResultDto;
import com.unityonline.gameserver.module.auth.dto.LoginCommand;
import com.unityonline.gameserver.module.auth.dto.RegisterCommand;
import com.unityonline.gameserver.module.auth.entity.AuthAccountEntity;
import com.unityonline.gameserver.module.auth.mapper.AuthAccountMapper;
import com.unityonline.gameserver.module.player.service.PlayerService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final IdGenerator idGenerator;
    private final PasswordUtils passwordUtils;
    private final TokenUtils tokenUtils;
    private final AuthTokenService authTokenService;
    private final GameSecurityProperties securityProperties;
    private final AuthAccountMapper authAccountMapper;
    private final PlayerService playerService;

    public AuthService(IdGenerator idGenerator,
                       PasswordUtils passwordUtils,
                       TokenUtils tokenUtils,
                       AuthTokenService authTokenService,
                       GameSecurityProperties securityProperties,
                       AuthAccountMapper authAccountMapper,
                       PlayerService playerService) {
        this.idGenerator = idGenerator;
        this.passwordUtils = passwordUtils;
        this.tokenUtils = tokenUtils;
        this.authTokenService = authTokenService;
        this.securityProperties = securityProperties;
        this.authAccountMapper = authAccountMapper;
        this.playerService = playerService;
    }

    /**
     * 注册账号并初始化玩家数据。
     */
    @Transactional
    public AuthResultDto register(RegisterCommand command) {
        validateUsernameAndPassword(command.username(), command.password());
        if (!StringUtils.hasText(command.nickname())) {
            throw new BizException(4003, "nickname cannot be empty");
        }
        authAccountMapper.findByUsername(command.username().trim()).ifPresent(account -> {
            throw new BizException(4004, "username already exists");
        });

        long accountId = idGenerator.nextAccountId();
        long playerId = playerService.initializePlayer(accountId, command.nickname().trim());
        String salt = passwordUtils.generateSalt();
        String passwordHash = passwordUtils.hashPassword(command.password(), salt);

        AuthAccountEntity entity = new AuthAccountEntity();
        entity.setAccountId(accountId);
        entity.setPlayerId(playerId);
        entity.setUsername(command.username().trim());
        entity.setPasswordHash(passwordHash);
        entity.setSalt(salt);
        entity.setLastLoginTime(null);
        entity.setLastLoginIp(command.clientIp());
        entity.setStatus(1);
        authAccountMapper.insert(entity);

        String token = tokenUtils.generateToken();
        authTokenService.storeToken(token, entity, securityProperties.getTokenTtlSeconds());
        return new AuthResultDto(accountId, playerId, token);
    }

    /**
     * 执行登录并将 token 写入 Redis。
     */
    public AuthResultDto login(LoginCommand command) {
        validateUsernameAndPassword(command.username(), command.password());
        AuthAccountEntity account = authAccountMapper.findByUsername(command.username().trim())
                .orElseThrow(() -> new BizException(4005, "username or password is incorrect"));
        if (!passwordUtils.matches(command.password(), account.getSalt(), account.getPasswordHash())) {
            throw new BizException(4005, "username or password is incorrect");
        }
        if (account.getStatus() == null || account.getStatus() != 1) {
            throw new BizException(4006, "account is disabled");
        }

        String token = tokenUtils.generateToken();
        authTokenService.storeToken(token, account, securityProperties.getTokenTtlSeconds());
        authAccountMapper.updateLoginInfo(account.getAccountId(), LocalDateTime.now(), command.clientIp());
        playerService.markLogin(account.getPlayerId());
        return new AuthResultDto(account.getAccountId(), account.getPlayerId(), token);
    }

    /**
     * 校验 token 是否有效。
     */
    public Optional<AuthTokenPayload> validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        Optional<AuthTokenPayload> payload = authTokenService.parseToken(token);
        payload.ifPresent(value -> authTokenService.refreshToken(token, securityProperties.getTokenTtlSeconds()));
        return payload;
    }

    /**
     * 执行登出并删除 Redis token。
     */
    public void logout(String token) {
        Optional<AuthTokenPayload> payload = validateToken(token);
        payload.ifPresent(value -> playerService.markLogout(value.playerId()));
        authTokenService.deleteToken(token);
    }

    /**
     * 校验注册和登录场景中的账号密码格式。
     */
    private void validateUsernameAndPassword(String username, String password) {
        if (!StringUtils.hasText(username) || username.trim().length() < 4) {
            throw new BizException(4007, "username length must be at least 4");
        }
        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw new BizException(4008, "password length must be at least 6");
        }
    }
}
