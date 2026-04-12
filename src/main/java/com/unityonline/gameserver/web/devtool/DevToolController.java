package com.unityonline.gameserver.web.devtool;

import com.unityonline.gameserver.common.api.ApiResponse;
import com.unityonline.gameserver.common.config.GameSecurityProperties;
import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.auth.dto.AuthResultDto;
import com.unityonline.gameserver.module.auth.dto.LoginCommand;
import com.unityonline.gameserver.module.auth.dto.RegisterCommand;
import com.unityonline.gameserver.module.auth.service.AuthService;
import com.unityonline.gameserver.module.inventory.dto.InventoryItemDto;
import com.unityonline.gameserver.module.inventory.service.InventoryService;
import com.unityonline.gameserver.module.player.dto.PlayerProfileDto;
import com.unityonline.gameserver.module.player.service.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devtool")
public class DevToolController {

    private final AuthService authService;
    private final PlayerService playerService;
    private final InventoryService inventoryService;
    private final GameSecurityProperties securityProperties;

    public DevToolController(AuthService authService,
                             PlayerService playerService,
                             InventoryService inventoryService,
                             GameSecurityProperties securityProperties) {
        this.authService = authService;
        this.playerService = playerService;
        this.inventoryService = inventoryService;
        this.securityProperties = securityProperties;
    }

    /**
     * 使用 JSON 请求注册账号，并直接返回登录态信息，方便本地可视化调试。
     */
    @PostMapping("/register")
    public ApiResponse<DevToolDtos.SessionResponse> register(@RequestBody DevToolDtos.RegisterRequest request,
                                                             HttpServletRequest httpServletRequest) {
        AuthResultDto result = authService.register(new RegisterCommand(
                request.username(),
                request.password(),
                request.nickname(),
                resolveClientIp(httpServletRequest)
        ));
        PlayerProfileDto profile = playerService.getPlayerProfile(result.playerId());
        return ApiResponse.success(buildSession(result, profile));
    }

    /**
     * 使用 JSON 请求登录账号，并返回登录态信息，方便本地可视化调试。
     */
    @PostMapping("/login")
    public ApiResponse<DevToolDtos.SessionResponse> login(@RequestBody DevToolDtos.LoginRequest request,
                                                          HttpServletRequest httpServletRequest) {
        AuthResultDto result = authService.login(new LoginCommand(
                request.username(),
                request.password(),
                resolveClientIp(httpServletRequest)
        ));
        PlayerProfileDto profile = playerService.getPlayerProfile(result.playerId());
        return ApiResponse.success(buildSession(result, profile));
    }

    /**
     * 获取当前登录玩家信息，页面刷新后可以重新确认会话状态。
     */
    @GetMapping("/me")
    public ApiResponse<DevToolDtos.SessionResponse> me() {
        Long playerId = requireLoginPlayerId();
        PlayerProfileDto profile = playerService.getPlayerProfile(playerId);
        String token = LoginPlayerContext.get() == null ? "" : LoginPlayerContext.get().token();
        DevToolDtos.SessionResponse response = new DevToolDtos.SessionResponse(
                profile.accountId(),
                profile.playerId(),
                profile.username(),
                profile.nickname(),
                token
        );
        return ApiResponse.success(response);
    }

    /**
     * 为当前登录玩家添加背包装备或道具。
     */
    @PostMapping("/inventory/add")
    public ApiResponse<DevToolDtos.InventoryItemView> addItem(@RequestBody DevToolDtos.AddItemRequest request) {
        Long playerId = requireLoginPlayerId();
        InventoryItemDto item = inventoryService.addItem(
                playerId,
                request.itemId(),
                request.itemType(),
                request.quantity(),
                request.extJson()
        );
        return ApiResponse.success(toView(item));
    }

    /**
     * 获取当前登录玩家的背包列表。
     */
    @GetMapping("/inventory")
    public ApiResponse<DevToolDtos.InventoryView> inventory() {
        Long playerId = requireLoginPlayerId();
        List<DevToolDtos.InventoryItemView> items = inventoryService.listInventory(playerId).stream()
                .map(this::toView)
                .toList();
        return ApiResponse.success(new DevToolDtos.InventoryView(playerId, items));
    }

    /**
     * 将业务登录结果和玩家资料拼成页面使用的会话对象。
     */
    private DevToolDtos.SessionResponse buildSession(AuthResultDto result, PlayerProfileDto profile) {
        return new DevToolDtos.SessionResponse(
                result.accountId(),
                result.playerId(),
                profile.username(),
                profile.nickname(),
                securityProperties.getTokenPrefix() + " " + result.token()
        );
    }

    /**
     * 将背包 DTO 转成页面展示对象。
     */
    private DevToolDtos.InventoryItemView toView(InventoryItemDto item) {
        return new DevToolDtos.InventoryItemView(
                item.itemId(),
                item.itemType(),
                item.quantity(),
                item.extJson()
        );
    }

    /**
     * 获取当前登录玩家 ID，不存在时抛出未登录异常。
     */
    private Long requireLoginPlayerId() {
        Long playerId = LoginPlayerContext.getPlayerId();
        if (playerId == null) {
            throw new BizException(4011, "login required");
        }
        return playerId;
    }

    /**
     * 解析客户端 IP，便于和主业务链路保持一致。
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
