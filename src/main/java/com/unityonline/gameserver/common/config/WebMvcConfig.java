package com.unityonline.gameserver.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unityonline.gameserver.common.auth.AuthTokenService;
import com.unityonline.gameserver.common.filter.AuthTokenFilter;
import com.unityonline.gameserver.common.util.TokenUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebMvcConfig {

    @Bean
    public AuthTokenFilter authTokenFilter(GameSecurityProperties gameSecurityProperties,
                                           AuthTokenService authTokenService,
                                           TokenUtils tokenUtils,
                                           ObjectMapper objectMapper) {
        return new AuthTokenFilter(gameSecurityProperties, authTokenService, tokenUtils, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authTokenFilterRegistration(AuthTokenFilter authTokenFilter) {
        FilterRegistrationBean<AuthTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(authTokenFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
