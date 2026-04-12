package com.unityonline.gameserver.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unityonline.gameserver.common.auth.AuthTokenPayload;
import com.unityonline.gameserver.common.auth.AuthTokenService;
import com.unityonline.gameserver.common.api.ApiResponse;
import com.unityonline.gameserver.common.config.GameSecurityProperties;
import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.context.LoginUser;
import com.unityonline.gameserver.common.util.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final GameSecurityProperties securityProperties;
    private final AuthTokenService authTokenService;
    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthTokenFilter(GameSecurityProperties securityProperties,
                           AuthTokenService authTokenService,
                           TokenUtils tokenUtils,
                           ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.authTokenService = authTokenService;
        this.tokenUtils = tokenUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!securityProperties.isEnabled()) {
            return true;
        }

        String path = request.getRequestURI();
        List<String> whitelist = securityProperties.getWhitelist();
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = request.getHeader(securityProperties.getHeaderName());
        String token = tokenUtils.extractRawToken(tokenValue, securityProperties.getTokenPrefix());
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response);
            return;
        }

        AuthTokenPayload payload = authTokenService.parseToken(token).orElse(null);
        if (payload == null) {
            writeUnauthorized(response);
            return;
        }

        authTokenService.refreshToken(token, securityProperties.getTokenTtlSeconds());
        LoginPlayerContext.set(new LoginUser(payload.accountId(), payload.playerId(), payload.username(), payload.token()));
        try {
            filterChain.doFilter(request, response);
        } finally {
            LoginPlayerContext.clear();
        }
    }

    /**
     * 返回未授权响应。
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, "missing auth token")));
    }
}
