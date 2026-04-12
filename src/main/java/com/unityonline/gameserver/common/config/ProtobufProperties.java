package com.unityonline.gameserver.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.protobuf")
public class ProtobufProperties {

    private boolean writeJsonDefaultValues = true;

    public boolean isWriteJsonDefaultValues() {
        return writeJsonDefaultValues;
    }

    public void setWriteJsonDefaultValues(boolean writeJsonDefaultValues) {
        this.writeJsonDefaultValues = writeJsonDefaultValues;
    }
}
