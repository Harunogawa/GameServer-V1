package com.unityonline.gameserver.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class GameConfigJsonLoader {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    public GameConfigJsonLoader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public List<Map<String, Object>> loadList(String classpathLocation) {
        Resource resource = resourceLoader.getResource(classpathLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("config resource not found: " + classpathLocation);
        }
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException("failed to load config resource: " + classpathLocation, e);
        }
    }

    public LocalDateTime toDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return Instant.ofEpochSecond(number.longValue()).atZone(zoneId).toLocalDateTime();
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : LocalDateTime.parse(text.replace(" ", "T"));
    }

    public Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Long.parseLong(text);
    }

    public Integer toInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Integer.parseInt(text);
    }

    public String toText(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return text.isBlank() ? null : text;
    }
}
