package com.unityonline.gameserver.web.devtool;

import com.unityonline.gameserver.common.api.ApiResponse;
import com.unityonline.gameserver.common.auth.AuthTokenPayload;
import com.unityonline.gameserver.common.config.GameSecurityProperties;
import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.util.TokenUtils;
import com.unityonline.gameserver.module.auth.dto.AuthResultDto;
import com.unityonline.gameserver.module.auth.dto.LoginCommand;
import com.unityonline.gameserver.module.auth.dto.RegisterCommand;
import com.unityonline.gameserver.module.auth.service.AuthService;
import com.unityonline.gameserver.module.inventory.dto.InventoryItemDto;
import com.unityonline.gameserver.module.inventory.service.InventoryService;
import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import com.unityonline.gameserver.module.notice.service.NoticeService;
import com.unityonline.gameserver.module.player.dto.PlayerProfileDto;
import com.unityonline.gameserver.module.player.service.PlayerService;
import com.unityonline.gameserver.module.quest.dto.QuestDto;
import com.unityonline.gameserver.module.quest.service.QuestService;
import com.unityonline.gameserver.module.rank.dto.RankEntryDto;
import com.unityonline.gameserver.module.rank.service.RankService;
import com.unityonline.gameserver.module.save.dto.SaveGameDto;
import com.unityonline.gameserver.module.save.service.SaveService;
import com.unityonline.gameserver.module.stage.dto.StageResultDto;
import com.unityonline.gameserver.module.stage.service.StageService;
import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import com.unityonline.gameserver.module.version.service.VersionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/api/devtool")
public class DevToolController {

    private final AuthService authService;
    private final PlayerService playerService;
    private final SaveService saveService;
    private final InventoryService inventoryService;
    private final QuestService questService;
    private final StageService stageService;
    private final NoticeService noticeService;
    private final VersionService versionService;
    private final RankService rankService;
    private final TokenUtils tokenUtils;
    private final GameSecurityProperties securityProperties;

    public DevToolController(AuthService authService,
                             PlayerService playerService,
                             SaveService saveService,
                             InventoryService inventoryService,
                             QuestService questService,
                             StageService stageService,
                             NoticeService noticeService,
                             VersionService versionService,
                             RankService rankService,
                             TokenUtils tokenUtils,
                             GameSecurityProperties securityProperties) {
        this.authService = authService;
        this.playerService = playerService;
        this.saveService = saveService;
        this.inventoryService = inventoryService;
        this.questService = questService;
        this.stageService = stageService;
        this.noticeService = noticeService;
        this.versionService = versionService;
        this.rankService = rankService;
        this.tokenUtils = tokenUtils;
        this.securityProperties = securityProperties;
    }

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

    @GetMapping("/auth/validate")
    public ApiResponse<DevToolDtos.TokenValidateView> validateToken(
            @RequestHeader(name = "X-Game-Token", required = false) String tokenValue) {
        String rawToken = tokenUtils.extractRawToken(tokenValue, securityProperties.getTokenPrefix());
        Optional<AuthTokenPayload> payload = authService.validateToken(rawToken);
        return ApiResponse.success(new DevToolDtos.TokenValidateView(payload.isPresent(), payload.orElse(null)));
    }

    @PostMapping("/auth/logout")
    public ApiResponse<String> logout(@RequestHeader(name = "X-Game-Token", required = false) String tokenValue) {
        String rawToken = tokenUtils.extractRawToken(tokenValue, securityProperties.getTokenPrefix());
        authService.logout(rawToken);
        return ApiResponse.success("logout success", "ok");
    }

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

    @GetMapping("/player/profile")
    public ApiResponse<PlayerProfileDto> profile() {
        return ApiResponse.success(playerService.getPlayerProfile(requireLoginPlayerId()));
    }

    @PostMapping("/save/upload")
    public ApiResponse<SaveGameDto> uploadSave(@RequestBody DevToolDtos.SaveUploadRequest request) {
        return ApiResponse.success(saveService.uploadSave(
                requireLoginPlayerId(),
                defaultSlot(request.slot()),
                request.saveDataJson(),
                request.saveVersion()
        ));
    }

    @PostMapping("/save/pull")
    public ApiResponse<SaveGameDto> pullSave(@RequestBody DevToolDtos.SavePullRequest request) {
        return ApiResponse.success(saveService.pullSave(
                requireLoginPlayerId(),
                defaultSlot(request.slot())
        ));
    }

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
        return ApiResponse.success(DevToolDtos.toView(item));
    }

    @PostMapping("/inventory/consume")
    public ApiResponse<DevToolDtos.InventoryItemView> consumeItem(@RequestBody DevToolDtos.ConsumeItemRequest request) {
        InventoryItemDto item = inventoryService.consumeItem(requireLoginPlayerId(), request.itemId(), request.quantity());
        return ApiResponse.success(DevToolDtos.toView(item));
    }

    @GetMapping("/inventory")
    public ApiResponse<DevToolDtos.InventoryView> inventory() {
        Long playerId = requireLoginPlayerId();
        List<DevToolDtos.InventoryItemView> items = inventoryService.listInventory(playerId).stream()
                .map(DevToolDtos::toView)
                .toList();
        return ApiResponse.success(new DevToolDtos.InventoryView(playerId, items));
    }

    @GetMapping("/quest")
    public ApiResponse<List<QuestDto>> questList() {
        return ApiResponse.success(questService.listQuests(requireLoginPlayerId()));
    }

    @PostMapping("/quest/progress")
    public ApiResponse<QuestDto> questProgress(@RequestBody DevToolDtos.QuestProgressRequest request) {
        return ApiResponse.success(questService.updateQuestProgress(
                requireLoginPlayerId(),
                request.questId(),
                request.progressDelta()
        ));
    }

    @PostMapping("/quest/claim")
    public ApiResponse<QuestDto> questClaim(@RequestBody DevToolDtos.QuestRewardRequest request) {
        return ApiResponse.success(questService.claimReward(requireLoginPlayerId(), request.questId()));
    }

    @GetMapping("/stage")
    public ApiResponse<List<StageResultDto>> stageList() {
        return ApiResponse.success(stageService.listStages(requireLoginPlayerId()));
    }

    @PostMapping("/stage/report")
    public ApiResponse<StageResultDto> reportStage(@RequestBody DevToolDtos.StageReportRequest request) {
        return ApiResponse.success(stageService.reportStage(
                requireLoginPlayerId(),
                request.stageId(),
                request.starCount(),
                request.cleared(),
                request.score()
        ));
    }

    @GetMapping("/notice")
    public ApiResponse<List<NoticeEntity>> noticeList() {
        return ApiResponse.success(noticeService.listNotices());
    }

    @PostMapping("/version/client")
    public ApiResponse<VersionConfigEntity> clientVersion(@RequestBody(required = false) DevToolDtos.PlatformRequest request) {
        return ApiResponse.success(versionService.getVersionInfo(request == null ? null : request.platform()));
    }

    @PostMapping("/version/resource")
    public ApiResponse<VersionConfigEntity> resourceVersion(@RequestBody(required = false) DevToolDtos.PlatformRequest request) {
        return ApiResponse.success(versionService.getResourceVersion(request == null ? null : request.platform()));
    }

    @PostMapping("/rank/report")
    public ApiResponse<RankEntryDto> reportRank(@RequestBody DevToolDtos.RankReportRequest request) {
        return ApiResponse.success(rankService.reportScore(request.rankType(), requireLoginPlayerId(), request.score()));
    }

    @PostMapping("/rank/list")
    public ApiResponse<List<RankEntryDto>> rankList(@RequestBody DevToolDtos.RankListRequest request) {
        return ApiResponse.success(rankService.queryRankList(
                request.rankType(),
                request.pageNo() == null ? 1 : request.pageNo(),
                request.pageSize() == null ? 10 : request.pageSize()
        ));
    }

    @PostMapping("/rank/my")
    public ApiResponse<RankEntryDto> myRank(@RequestBody DevToolDtos.RankMyRequest request) {
        return ApiResponse.success(rankService.queryMyRank(request.rankType(), requireLoginPlayerId()));
    }

    @GetMapping("/overview")
    public ApiResponse<DevToolDtos.OverviewResponse> overview() {
        Long playerId = requireLoginPlayerId();
        PlayerProfileDto profile = playerService.getPlayerProfile(playerId);
        List<DevToolDtos.InventoryItemView> items = inventoryService.listInventory(playerId).stream()
                .map(DevToolDtos::toView)
                .toList();
        SaveGameDto save = tryPullSave(playerId, 1);
        RankEntryDto myRank = rankService.queryMyRank(1, playerId);
        return ApiResponse.success(new DevToolDtos.OverviewResponse(
                profile,
                new DevToolDtos.InventoryView(playerId, items),
                questService.listQuests(playerId),
                stageService.listStages(playerId),
                save,
                myRank,
                noticeService.listNotices(),
                versionService.getVersionInfo("all")
        ));
    }

    private SaveGameDto tryPullSave(Long playerId, Integer slot) {
        try {
            return saveService.pullSave(playerId, slot);
        } catch (BizException ignored) {
            return null;
        }
    }

    private DevToolDtos.SessionResponse buildSession(AuthResultDto result, PlayerProfileDto profile) {
        return new DevToolDtos.SessionResponse(
                result.accountId(),
                result.playerId(),
                profile.username(),
                profile.nickname(),
                securityProperties.getTokenPrefix() + " " + result.token()
        );
    }

    private Integer defaultSlot(Integer slot) {
        return slot == null || slot <= 0 ? 1 : slot;
    }

    private Long requireLoginPlayerId() {
        Long playerId = LoginPlayerContext.getPlayerId();
        if (playerId == null) {
            throw new BizException(4011, "login required");
        }
        return playerId;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
