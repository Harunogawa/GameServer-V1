package com.unityonline.gameserver.common.proto;

import com.unityonline.gameserver.proto.common.CommonResponse;
import org.springframework.stereotype.Component;

@Component
public class ProtoResponseBuilder {

    /**
     * 构建成功响应对象。
     */
    public CommonResponse success() {
        return CommonResponse.newBuilder()
                .setCode(0)
                .setMessage("success")
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 构建失败响应对象。
     */
    public CommonResponse fail(int code, String message) {
        return CommonResponse.newBuilder()
                .setCode(code)
                .setMessage(message)
                .setTimestamp(System.currentTimeMillis())
                .build();
    }
}
