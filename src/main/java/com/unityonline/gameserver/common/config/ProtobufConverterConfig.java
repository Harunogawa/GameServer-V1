package com.unityonline.gameserver.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration
@EnableConfigurationProperties({GameSecurityProperties.class, ProtobufProperties.class})
public class ProtobufConverterConfig {

    /**
     * 注册 protobuf 消息转换器，支持 HTTP 二进制 protobuf 请求与响应。
     */
    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
