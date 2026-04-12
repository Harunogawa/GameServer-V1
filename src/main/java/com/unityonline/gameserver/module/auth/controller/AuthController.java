package com.unityonline.gameserver.module.auth.controller;

import com.unityonline.gameserver.common.auth.AuthTokenPayload;
import com.unityonline.gameserver.common.config.GameSecurityProperties;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.common.util.TokenUtils;
import com.unityonline.gameserver.module.auth.dto.AuthResultDto;
import com.unityonline.gameserver.module.auth.dto.LoginCommand;
import com.unityonline.gameserver.module.auth.dto.RegisterCommand;
import com.unityonline.gameserver.module.auth.service.AuthService;
import com.unityonline.gameserver.proto.game.auth.LoginRequest;
import com.unityonline.gameserver.proto.game.auth.LoginResponse;
import com.unityonline.gameserver.proto.game.auth.LogoutRequest;
import com.unityonline.gameserver.proto.game.auth.LogoutResponse;
import com.unityonline.gameserver.proto.game.auth.RegisterRequest;
import com.unityonline.gameserver.proto.game.auth.RegisterResponse;
import com.unityonline.gameserver.proto.game.auth.ValidateTokenRequest;
import com.unityonline.gameserver.proto.game.auth.ValidateTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final AuthService authService;
    private final ProtoResponseBuilder protoResponseBuilder;
    private final TokenUtils tokenUtils;
    private final GameSecurityProperties securityProperties;

    public AuthController(AuthService authService,
                          ProtoResponseBuilder protoResponseBuilder,
                          TokenUtils tokenUtils,
                          GameSecurityProperties securityProperties) {
        this.authService = authService;
        this.protoResponseBuilder = protoResponseBuilder;
        this.tokenUtils = tokenUtils;
        this.securityProperties = securityProperties;
    }

    /**
     * 注册账号并初始化玩家。
     */
    @PostMapping(value = "/register", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public RegisterResponse register(@RequestBody RegisterRequest request, HttpServletRequest httpServletRequest) {
        AuthResultDto result = authService.register(new RegisterCommand(
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                resolveClientIp(httpServletRequest)
        ));
        return RegisterResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setAccountId(result.accountId())
                .setPlayerId(result.playerId())
                .build();
    }

    /**
     * 账号登录并签发 token。
     */
    @PostMapping(value = "/login", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        AuthResultDto result = authService.login(new LoginCommand(
                request.getUsername(),
                request.getPassword(),
                resolveClientIp(httpServletRequest)
        ));
        return LoginResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setAccountId(result.accountId())
                .setPlayerId(result.playerId())
                .setToken(result.token())
                .build();
    }

    /**
     * 主动校验 token 是否有效。
     */
    @PostMapping(value = "/validate", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public ValidateTokenResponse validate(@RequestBody ValidateTokenRequest request, HttpServletRequest httpServletRequest) {
        String token = tokenUtils.extractRawToken(request.getToken(), securityProperties.getTokenPrefix());
        if (token == null) {
            token = tokenUtils.extractRawToken(httpServletRequest.getHeader(securityProperties.getHeaderName()), securityProperties.getTokenPrefix());
        }
        Optional<AuthTokenPayload> payload = authService.validateToken(token);
        ValidateTokenResponse.Builder builder = ValidateTokenResponse.newBuilder();
        if (payload.isPresent()) {
            return builder
                    .setResult(protoResponseBuilder.success())
                    .setValid(true)
                    .setAccountId(payload.get().accountId())
                    .setPlayerId(payload.get().playerId())
                    .build();
        }
        return builder
                .setResult(protoResponseBuilder.fail(401, "invalid token"))
                .setValid(false)
                .build();
    }

    /**
     * 执行登出并删除 Redis token。
     */
    @PostMapping(value = "/logout", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public LogoutResponse logout(@RequestBody LogoutRequest request, HttpServletRequest httpServletRequest) {
        String token = tokenUtils.extractRawToken(request.getToken(), securityProperties.getTokenPrefix());
        if (token == null) {
            token = tokenUtils.extractRawToken(httpServletRequest.getHeader(securityProperties.getHeaderName()), securityProperties.getTokenPrefix());
        }
        authService.logout(token);
        return LogoutResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .build();
    }

    /**
     * 解析客户端 IP，便于记录登录日志。
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
