package com.unityonline.gameserver.web;

import com.unityonline.gameserver.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicPingController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("gameserver skeleton is running");
    }
}
