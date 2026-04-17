const storageKey = "gameserver.devtool.session";
const localeStorageKey = "gameserver.devtool.locale";
const fallbackTexts = new Map();

const translations = {
    zh: {
        "language.toggle": "Switch to English",
        "hero.eyebrow": "GameServer 本地调试工具",
        "hero.title": "网页调试控制台",
        "hero.text": "这个页面会直接调用当前 Spring Boot 服务里的 JSON 调试接口，覆盖 auth、player、save、inventory、quest、stage、notice、version、rank 和 overview 流程。",
        "session.current": "当前会话",
        "session.refresh": "刷新会话",
        "session.logout": "退出登录",
        "session.notLoggedIn": "尚未登录",
        "session.accountId": "账号 ID",
        "session.playerId": "玩家 ID",
        "session.token": "Token",
        "quick.title": "快捷操作",
        "quick.desc": "用预设按钮快速构造测试数据，避免手动填 JSON 和逐个点接口。",
        "quick.fillAccount": "填充随机账号",
        "quick.registerAndLogin": "注册并登录测试号",
        "quick.batchAccounts": "后台生成 3 个测试号",
        "quick.bootstrap": "一键准备当前账号",
        "quick.refreshAll": "刷新全部面板",
        "quick.rankBots": "生成 5 个排行机器人",
        "auth.desc": "注册、登录、校验 token。",
        "auth.registerAndLogin": "注册并登录",
        "auth.login": "登录",
        "auth.validateToken": "校验 Token",
        "auth.fillRandom": "填入随机账号",
        "auth.createThree": "生成 3 个账号",
        "player.desc": "加载当前玩家资料和整体概览。",
        "player.loadProfile": "加载资料",
        "player.loadOverview": "加载总览",
        "player.bootstrap": "准备当前玩家",
        "save.desc": "上传和拉取存档，默认槽位为 1。",
        "save.upload": "上传存档",
        "save.pull": "拉取存档",
        "save.fillDefault": "填充默认存档",
        "save.uploadDefault": "上传默认存档",
        "save.uploadAdvanced": "上传高级存档",
        "inventory.desc": "查看、增加、消耗背包道具。",
        "inventory.addItem": "添加道具",
        "inventory.refresh": "刷新背包",
        "inventory.consumeItem": "消耗道具",
        "inventory.grantStarter": "发放新手包",
        "inventory.grantResource": "发放资源包",
        "inventory.notLoaded": "背包尚未加载。",
        "inventory.empty": "背包目前是空的。",
        "inventory.loginFirst": "请先登录后再操作背包。",
        "inventory.item": "道具",
        "inventory.type": "类型",
        "inventory.ext": "扩展",
        "quest.desc": "查看任务、推进进度、领取奖励。",
        "quest.refresh": "刷新任务",
        "quest.updateProgress": "更新进度",
        "quest.claimReward": "领取奖励",
        "quest.completeAll": "完成全部任务",
        "quest.claimAll": "领取全部奖励",
        "stage.desc": "查看关卡并提交结算结果。",
        "stage.refresh": "刷新关卡",
        "stage.submitReport": "提交结算",
        "stage.clearOne": "通关 10001",
        "stage.clearBatch": "通关前两关",
        "notice.desc": "加载当前公告。",
        "notice.load": "加载公告",
        "version.desc": "加载客户端版本和资源版本。",
        "version.client": "客户端版本",
        "version.resource": "资源版本",
        "rank.desc": "上报分数、查询排行榜、查询我的排名。",
        "rank.reportScore": "上报分数",
        "rank.list": "排行榜",
        "rank.myRank": "我的排名",
        "rank.reportStarter": "上报 12345",
        "rank.reportHigh": "上报 88888",
        "rank.seedBots": "生成 5 个机器人",
        "log.desc": "最新请求与响应快照会显示在这里。",
        "log.waiting": "等待操作...",
        "field.username": "用户名",
        "field.password": "密码",
        "field.nickname": "昵称",
        "field.slot": "槽位",
        "field.saveVersion": "存档版本",
        "field.saveJson": "存档 JSON",
        "field.itemId": "道具 ID",
        "field.itemType": "道具类型",
        "field.quantity": "数量",
        "field.extJson": "扩展 JSON",
        "field.consumeQty": "消耗数量",
        "field.questId": "任务 ID",
        "field.progressDelta": "进度增量",
        "field.stageId": "关卡 ID",
        "field.starCount": "星级",
        "field.score": "分数",
        "field.cleared": "已通关",
        "field.platform": "平台",
        "field.rankType": "榜单类型",
        "field.pageNo": "页码",
        "field.pageSize": "每页数量",
        "placeholder.username": "testuser1001",
        "placeholder.password": "abc123456",
        "placeholder.nickname": "hero_01",
        "placeholder.saveJson": "{\"chapter\":1,\"stage\":10001,\"level\":12,\"hp\":1880,\"mp\":260,\"gold\":8888,\"diamond\":188,\"items\":[{\"itemId\":5001,\"qty\":3},{\"itemId\":5002,\"qty\":5}],\"team\":[1001,1002,1003],\"flags\":{\"tutorial\":true,\"dailyReward\":false}}",
        "placeholder.extJson": "{\"source\":\"devtool\",\"slot\":\"weapon\"}",
        "common.waiting": "等待中...",
        "flash.loginRequired": "请先登录后再执行这个操作。",
        "flash.randomFilled": "已经帮你填好一组随机测试账号。",
        "flash.bootstrapDone": "当前账号的一键准备已经完成。",
        "flash.batchAccountsDone": "测试账号已创建完成。",
        "flash.rankBotsDone": "排行榜测试机器人已生成完成。",
        "flash.defaultSaveReady": "默认存档内容已经填充到表单里。",
        "flash.actionRunning": "正在执行批量操作，请稍等...",
        "flash.refreshDone": "所有可用面板已刷新。",
        "flash.requestFailed": "请求失败，请查看下方日志。",
        "flash.requestError": "请求异常，请查看下方日志。",
        "log.registerSuccess": "注册成功",
        "log.loginSuccess": "登录成功",
        "log.validateToken": "校验 Token",
        "log.validateTokenSuccess": "校验 Token 成功",
        "log.logout": "退出登录",
        "log.sessionCleared": "本地会话已清除",
        "log.refreshSession": "刷新会话",
        "log.noLocalSession": "本地没有会话",
        "log.refreshSessionSuccess": "刷新会话成功",
        "log.loadProfileSuccess": "加载资料成功",
        "log.loadOverviewSuccess": "加载总览成功",
        "log.uploadSaveSuccess": "上传存档成功",
        "log.pullSaveSuccess": "拉取存档成功",
        "log.addItemSuccess": "添加道具成功",
        "log.consumeItemSuccess": "消耗道具成功",
        "log.loadInventorySuccess": "加载背包成功",
        "log.loadQuestsSuccess": "加载任务成功",
        "log.updateQuestProgressSuccess": "更新任务进度成功",
        "log.claimQuestRewardSuccess": "领取任务奖励成功",
        "log.loadStagesSuccess": "加载关卡成功",
        "log.stageReportSuccess": "提交关卡结算成功",
        "log.loadNoticesSuccess": "加载公告成功",
        "log.loadClientVersionSuccess": "加载客户端版本成功",
        "log.loadResourceVersionSuccess": "加载资源版本成功",
        "log.reportRankSuccess": "上报排行榜成功",
        "log.loadRankListSuccess": "加载排行榜成功",
        "log.loadMyRankSuccess": "加载我的排名成功",
        "log.requestBlocked": "请求被拦截",
        "log.pleaseLoginFirst": "请先登录",
        "log.requestFailed": "请求失败",
        "log.requestError": "请求异常",
        "log.quickCreateAccount": "批量创建测试账号",
        "log.quickBootstrap": "一键准备当前账号",
        "log.quickRankBots": "生成排行榜机器人"
    },
    en: {
        "language.toggle": "切换到中文"
    }
};

const sessionSummary = document.getElementById("session-summary");
const logOutput = document.getElementById("log-output");
const inventoryList = document.getElementById("inventory-list");
const inventoryEmpty = document.getElementById("inventory-empty");
const flashBanner = document.getElementById("flash-banner");
const languageToggleButton = document.getElementById("language-toggle-btn");

const outputs = {
    profile: document.getElementById("profile-output"),
    save: document.getElementById("save-output"),
    quest: document.getElementById("quest-output"),
    stage: document.getElementById("stage-output"),
    notice: document.getElementById("notice-output"),
    version: document.getElementById("version-output"),
    rank: document.getElementById("rank-output"),
};

const registerForm = document.getElementById("register-form");
const loginForm = document.getElementById("login-form");
const saveUploadForm = document.getElementById("save-upload-form");
const addItemForm = document.getElementById("add-item-form");
const consumeItemForm = document.getElementById("consume-item-form");
const questProgressForm = document.getElementById("quest-progress-form");
const questClaimForm = document.getElementById("quest-claim-form");
const stageReportForm = document.getElementById("stage-report-form");
const versionForm = document.getElementById("version-form");
const rankReportForm = document.getElementById("rank-report-form");
const rankQueryForm = document.getElementById("rank-query-form");

registerForm.addEventListener("submit", handleRegister);
loginForm.addEventListener("submit", handleLogin);
saveUploadForm.addEventListener("submit", uploadSave);
addItemForm.addEventListener("submit", handleAddItem);
consumeItemForm.addEventListener("submit", handleConsumeItem);
questProgressForm.addEventListener("submit", updateQuestProgress);
questClaimForm.addEventListener("submit", claimQuestReward);
stageReportForm.addEventListener("submit", reportStage);
rankReportForm.addEventListener("submit", reportRank);

document.getElementById("validate-token-btn").addEventListener("click", validateToken);
document.getElementById("logout-btn").addEventListener("click", logout);
document.getElementById("refresh-session-btn").addEventListener("click", refreshSession);
document.getElementById("load-profile-btn").addEventListener("click", loadProfile);
document.getElementById("load-overview-btn").addEventListener("click", loadOverview);
document.getElementById("player-bootstrap-btn").addEventListener("click", quickBootstrapCurrentPlayer);
document.getElementById("fill-default-save-btn").addEventListener("click", () => fillSavePreset("default"));
document.getElementById("upload-default-save-btn").addEventListener("click", () => uploadSavePreset("default"));
document.getElementById("upload-advanced-save-btn").addEventListener("click", () => uploadSavePreset("advanced"));
document.getElementById("save-pull-btn").addEventListener("click", pullSave);
document.getElementById("grant-starter-pack-btn").addEventListener("click", () => grantPack("starter"));
document.getElementById("grant-resource-pack-btn").addEventListener("click", () => grantPack("resource"));
document.getElementById("load-inventory-btn").addEventListener("click", loadInventory);
document.getElementById("load-quests-btn").addEventListener("click", loadQuests);
document.getElementById("complete-all-quests-btn").addEventListener("click", completeAllQuests);
document.getElementById("claim-all-quests-btn").addEventListener("click", claimAllQuestRewards);
document.getElementById("load-stages-btn").addEventListener("click", loadStages);
document.getElementById("clear-stage-1-btn").addEventListener("click", () => clearPresetStages([{ stageId: 10001, starCount: 3, score: 9999 }]));
document.getElementById("clear-stage-batch-btn").addEventListener("click", () => clearPresetStages([
    { stageId: 10001, starCount: 3, score: 9999 },
    { stageId: 10002, starCount: 3, score: 18888 }
]));
document.getElementById("load-notices-btn").addEventListener("click", loadNotices);
document.getElementById("load-client-version-btn").addEventListener("click", loadClientVersion);
document.getElementById("load-resource-version-btn").addEventListener("click", loadResourceVersion);
document.getElementById("report-starter-score-btn").addEventListener("click", () => reportPresetScore(12345));
document.getElementById("report-high-score-btn").addEventListener("click", () => reportPresetScore(88888));
document.getElementById("seed-rank-bots-btn").addEventListener("click", () => seedRankBots(5));
document.getElementById("rank-list-btn").addEventListener("click", loadRankList);
document.getElementById("rank-my-btn").addEventListener("click", loadMyRank);

document.getElementById("quick-fill-account-btn").addEventListener("click", fillRandomAccountForms);
document.getElementById("quick-register-account-btn").addEventListener("click", quickRegisterAndLogin);
document.getElementById("quick-batch-accounts-btn").addEventListener("click", () => createDemoAccounts(3, false));
document.getElementById("quick-bootstrap-btn").addEventListener("click", quickBootstrapCurrentPlayer);
document.getElementById("quick-refresh-all-btn").addEventListener("click", refreshAllPanels);
document.getElementById("quick-rank-bots-btn").addEventListener("click", () => seedRankBots(5));
document.getElementById("auth-fill-random-btn").addEventListener("click", fillRandomAccountForms);
document.getElementById("auth-create-three-btn").addEventListener("click", () => createDemoAccounts(3, false));
languageToggleButton.addEventListener("click", toggleLanguage);

captureFallbackTexts();
applyLanguage(getLocale());
applyDefaultFormValues();
restoreSession();

async function handleRegister(event) {
    event.preventDefault();
    const response = await request("/api/devtool/register", {
        method: "POST",
        body: JSON.stringify(readForm(registerForm)),
    });
    if (!response) {
        return;
    }
    saveSession(response.data);
    syncLoginFormFromRegister();
    renderSession(response.data);
    appendLog(t("log.registerSuccess"), response);
    showFlash(t("log.registerSuccess"), "success");
    await hydrateAfterLogin();
}

async function handleLogin(event) {
    event.preventDefault();
    const response = await request("/api/devtool/login", {
        method: "POST",
        body: JSON.stringify(readForm(loginForm)),
    });
    if (!response) {
        return;
    }
    saveSession(response.data);
    renderSession(response.data);
    appendLog(t("log.loginSuccess"), response);
    showFlash(t("log.loginSuccess"), "success");
    await hydrateAfterLogin();
}

async function validateToken() {
    const session = readSession();
    if (!session?.token) {
        appendLog(t("log.validateToken"), { message: t("log.pleaseLoginFirst") });
        showFlash(t("flash.loginRequired"), "error");
        return;
    }
    const response = await request("/api/devtool/auth/validate", {
        method: "GET",
        headers: buildAuthHeaders(session.token),
    });
    if (!response) {
        return;
    }
    renderOutput("profile", response.data);
    appendLog(t("log.validateTokenSuccess"), response);
}

async function logout() {
    const session = readSession();
    if (session?.token) {
        await request("/api/devtool/auth/logout", {
            method: "POST",
            headers: buildAuthHeaders(session.token),
        });
    }
    clearSession();
    renderSession(null);
    clearProtectedPanels();
    appendLog(t("log.logout"), { message: t("log.sessionCleared") });
    showFlash(t("log.sessionCleared"), "info");
}

async function refreshSession() {
    const session = readSession();
    if (!session?.token) {
        renderSession(null);
        appendLog(t("log.refreshSession"), { message: t("log.noLocalSession") });
        return;
    }
    const response = await request("/api/devtool/me", {
        method: "GET",
        headers: buildAuthHeaders(session.token),
    });
    if (!response) {
        return;
    }
    const nextSession = { ...response.data, token: session.token };
    saveSession(nextSession);
    renderSession(nextSession);
    appendLog(t("log.refreshSessionSuccess"), response);
}

async function loadProfile() {
    const response = await authedRequest("/api/devtool/player/profile", { method: "GET" });
    if (!response) {
        return;
    }
    renderOutput("profile", response.data);
    appendLog(t("log.loadProfileSuccess"), response);
}

async function loadOverview() {
    const response = await authedRequest("/api/devtool/overview", { method: "GET" });
    if (!response) {
        return;
    }
    renderOutput("profile", response.data.profile);
    renderOutput("save", response.data.save);
    renderOutput("quest", response.data.quests);
    renderOutput("stage", response.data.stages);
    renderOutput("notice", response.data.notices);
    renderOutput("version", response.data.version);
    renderOutput("rank", response.data.myRank);
    renderInventory(response.data.inventory);
    appendLog(t("log.loadOverviewSuccess"), response);
}

async function uploadSave(event) {
    event.preventDefault();
    const response = await authedRequest("/api/devtool/save/upload", {
        method: "POST",
        body: JSON.stringify(buildSavePayload()),
    });
    if (!response) {
        return;
    }
    renderOutput("save", response.data);
    appendLog(t("log.uploadSaveSuccess"), response);
}

async function pullSave() {
    const response = await authedRequest("/api/devtool/save/pull", {
        method: "POST",
        body: JSON.stringify({ slot: Number(saveUploadForm.slot.value) || 1 }),
    });
    if (!response) {
        return;
    }
    renderOutput("save", response.data);
    appendLog(t("log.pullSaveSuccess"), response);
}

async function handleAddItem(event) {
    event.preventDefault();
    const response = await addInventoryItem(
        Number(addItemForm.itemId.value),
        Number(addItemForm.itemType.value),
        Number(addItemForm.quantity.value),
        addItemForm.extJson.value.trim() || "{}"
    );
    if (!response) {
        return;
    }
    appendLog(t("log.addItemSuccess"), response);
    await loadInventory();
}

async function handleConsumeItem(event) {
    event.preventDefault();
    const response = await authedRequest("/api/devtool/inventory/consume", {
        method: "POST",
        body: JSON.stringify({
            itemId: Number(consumeItemForm.itemId.value),
            quantity: Number(consumeItemForm.quantity.value),
        }),
    });
    if (!response) {
        return;
    }
    appendLog(t("log.consumeItemSuccess"), response);
    await loadInventory();
}

async function loadInventory() {
    const response = await authedRequest("/api/devtool/inventory", { method: "GET" }, false);
    if (!response) {
        inventoryList.innerHTML = "";
        inventoryEmpty.style.display = "block";
        inventoryEmpty.textContent = t("inventory.loginFirst");
        return;
    }
    renderInventory(response.data);
    appendLog(t("log.loadInventorySuccess"), response);
}

async function loadQuests() {
    const response = await authedRequest("/api/devtool/quest", { method: "GET" });
    if (!response) {
        return null;
    }
    renderOutput("quest", response.data);
    appendLog(t("log.loadQuestsSuccess"), response);
    return response.data;
}

async function updateQuestProgress(event) {
    event.preventDefault();
    const response = await authedRequest("/api/devtool/quest/progress", {
        method: "POST",
        body: JSON.stringify({
            questId: Number(questProgressForm.questId.value),
            progressDelta: Number(questProgressForm.progressDelta.value),
        }),
    });
    if (!response) {
        return;
    }
    appendLog(t("log.updateQuestProgressSuccess"), response);
    await loadQuests();
    await loadInventory();
}

async function claimQuestReward(event) {
    event.preventDefault();
    const response = await claimQuestById(Number(questClaimForm.questId.value));
    if (!response) {
        return;
    }
    appendLog(t("log.claimQuestRewardSuccess"), response);
    await loadQuests();
    await loadInventory();
}

async function loadStages() {
    const response = await authedRequest("/api/devtool/stage", { method: "GET" });
    if (!response) {
        return null;
    }
    renderOutput("stage", response.data);
    appendLog(t("log.loadStagesSuccess"), response);
    return response.data;
}

async function reportStage(event) {
    event.preventDefault();
    const response = await reportStagePayload({
        stageId: Number(stageReportForm.stageId.value),
        starCount: Number(stageReportForm.starCount.value),
        cleared: stageReportForm.cleared.checked,
        score: Number(stageReportForm.score.value),
    });
    if (!response) {
        return;
    }
    appendLog(t("log.stageReportSuccess"), response);
    await loadStages();
    await loadQuests();
    await loadInventory();
}

async function loadNotices() {
    const response = await authedRequest("/api/devtool/notice", { method: "GET" });
    if (!response) {
        return;
    }
    renderOutput("notice", response.data);
    appendLog(t("log.loadNoticesSuccess"), response);
}

async function loadClientVersion() {
    await loadVersion("/api/devtool/version/client", t("log.loadClientVersionSuccess"));
}

async function loadResourceVersion() {
    await loadVersion("/api/devtool/version/resource", t("log.loadResourceVersionSuccess"));
}

async function loadVersion(url, title) {
    const response = await authedRequest(url, {
        method: "POST",
        body: JSON.stringify({ platform: versionForm.platform.value.trim() || "all" }),
    });
    if (!response) {
        return;
    }
    renderOutput("version", response.data);
    appendLog(title, response);
}

async function reportRank(event) {
    event.preventDefault();
    const response = await reportRankScore(Number(rankReportForm.score.value));
    if (!response) {
        return;
    }
    renderOutput("rank", response.data);
    appendLog(t("log.reportRankSuccess"), response);
}

async function loadRankList() {
    const response = await authedRequest("/api/devtool/rank/list", {
        method: "POST",
        body: JSON.stringify({
            rankType: Number(rankQueryForm.rankType.value),
            pageNo: Number(rankQueryForm.pageNo.value),
            pageSize: Number(rankQueryForm.pageSize.value),
        }),
    });
    if (!response) {
        return;
    }
    renderOutput("rank", response.data);
    appendLog(t("log.loadRankListSuccess"), response);
}

async function loadMyRank() {
    const response = await authedRequest("/api/devtool/rank/my", {
        method: "POST",
        body: JSON.stringify({
            rankType: Number(rankQueryForm.rankType.value),
        }),
    });
    if (!response) {
        return;
    }
    renderOutput("rank", response.data);
    appendLog(t("log.loadMyRankSuccess"), response);
}

async function quickRegisterAndLogin() {
    fillRandomAccountForms();
    const response = await request("/api/devtool/register", {
        method: "POST",
        body: JSON.stringify(readForm(registerForm)),
    });
    if (!response) {
        return;
    }
    saveSession(response.data);
    syncLoginFormFromRegister();
    renderSession(response.data);
    appendLog(t("log.registerSuccess"), response);
    showFlash(t("log.registerSuccess"), "success");
    await hydrateAfterLogin();
}

async function createDemoAccounts(count, withRankSeed) {
    showFlash(t("flash.actionRunning"), "info");
    const currentSession = readSession();
    const created = [];
    for (let index = 0; index < count; index += 1) {
        const account = buildDemoAccount(index);
        const response = await request("/api/devtool/register", {
            method: "POST",
            body: JSON.stringify(account),
        });
        if (!response) {
            break;
        }
        created.push({
            ...account,
            session: response.data,
        });
        if (withRankSeed) {
            const score = 6000 + Math.floor(Math.random() * 50000);
            await request("/api/devtool/rank/report", {
                method: "POST",
                headers: buildAuthHeaders(response.data.token),
                body: JSON.stringify({
                    rankType: Number(rankQueryForm.rankType.value) || 1,
                    score,
                }),
            });
        }
    }
    if (currentSession) {
        saveSession(currentSession);
        renderSession(currentSession);
    }
    appendLog(withRankSeed ? t("log.quickRankBots") : t("log.quickCreateAccount"), created);
    showFlash(withRankSeed ? t("flash.rankBotsDone") : t("flash.batchAccountsDone"), "success");
    if (withRankSeed) {
        await loadRankList();
    }
    return created;
}

async function seedRankBots(count) {
    await createDemoAccounts(count, true);
}

async function quickBootstrapCurrentPlayer() {
    if (!ensureLoggedIn()) {
        return;
    }
    showFlash(t("flash.actionRunning"), "info");
    fillSavePreset("default");
    await uploadSavePreset("default");
    await grantPack("starter");
    await clearPresetStages([
        { stageId: 10001, starCount: 3, score: 9999 },
        { stageId: 10002, starCount: 3, score: 18888 }
    ], false);
    await completeAllQuests(false);
    await claimAllQuestRewards(false);
    await reportPresetScore(12345, false);
    await refreshAllPanels(false);
    appendLog(t("log.quickBootstrap"), { playerId: readSession()?.playerId });
    showFlash(t("flash.bootstrapDone"), "success");
}

async function refreshAllPanels(showMessage = true) {
    if (!ensureLoggedIn()) {
        return;
    }
    await refreshSession();
    await loadOverview();
    await loadProfile();
    await loadInventory();
    await loadQuests();
    await loadStages();
    await loadNotices();
    await loadClientVersion();
    await loadMyRank();
    await loadRankList();
    if (showMessage) {
        showFlash(t("flash.refreshDone"), "success");
    }
}

async function uploadSavePreset(kind) {
    if (!ensureLoggedIn()) {
        return;
    }
    fillSavePreset(kind);
    const response = await authedRequest("/api/devtool/save/upload", {
        method: "POST",
        body: JSON.stringify(buildSavePayload()),
    });
    if (!response) {
        return;
    }
    renderOutput("save", response.data);
    appendLog(t("log.uploadSaveSuccess"), response);
}

function fillSavePreset(kind) {
    const session = readSession();
    saveUploadForm.slot.value = "1";
    saveUploadForm.saveVersion.value = kind === "advanced" ? "1.0.0-advanced" : "1.0.0-dev";
    saveUploadForm.saveDataJson.value = JSON.stringify(buildSavePreset(kind, session), null, 2);
    showFlash(t("flash.defaultSaveReady"), "info");
}

async function grantPack(kind) {
    if (!ensureLoggedIn()) {
        return;
    }
    const packs = {
        starter: [
            { itemId: 5001, itemType: 1, quantity: 10, extJson: "{\"source\":\"starter-pack\",\"slot\":\"weapon\"}" },
            { itemId: 5002, itemType: 1, quantity: 5, extJson: "{\"source\":\"starter-pack\",\"slot\":\"armor\"}" },
            { itemId: 9001, itemType: 2, quantity: 8888, extJson: "{\"source\":\"starter-pack\",\"type\":\"gold\"}" },
            { itemId: 9002, itemType: 2, quantity: 188, extJson: "{\"source\":\"starter-pack\",\"type\":\"diamond\"}" }
        ],
        resource: [
            { itemId: 7001, itemType: 3, quantity: 20, extJson: "{\"source\":\"resource-pack\",\"tag\":\"exp\"}" },
            { itemId: 7002, itemType: 3, quantity: 12, extJson: "{\"source\":\"resource-pack\",\"tag\":\"stamina\"}" },
            { itemId: 8001, itemType: 4, quantity: 3, extJson: "{\"source\":\"resource-pack\",\"tag\":\"key\"}" }
        ]
    };
    for (const item of packs[kind] || []) {
        const response = await addInventoryItem(item.itemId, item.itemType, item.quantity, item.extJson);
        if (!response) {
            return;
        }
    }
    await loadInventory();
}

async function completeAllQuests(showMessage = true) {
    if (!ensureLoggedIn()) {
        return;
    }
    const quests = await loadQuests();
    if (!quests) {
        return;
    }
    for (const quest of quests) {
        const remaining = Math.max((quest.targetValue ?? 0) - (quest.progress ?? 0), 0);
        if (remaining > 0) {
            await authedRequest("/api/devtool/quest/progress", {
                method: "POST",
                body: JSON.stringify({
                    questId: quest.questId,
                    progressDelta: remaining,
                }),
            });
        }
    }
    await loadQuests();
    if (showMessage) {
        showFlash(t("quest.completeAll"), "success");
    }
}

async function claimAllQuestRewards(showMessage = true) {
    if (!ensureLoggedIn()) {
        return;
    }
    const quests = await loadQuests();
    if (!quests) {
        return;
    }
    for (const quest of quests) {
        if (quest.status === 1 && !quest.rewardClaimed) {
            await claimQuestById(quest.questId);
        }
    }
    await loadQuests();
    await loadInventory();
    if (showMessage) {
        showFlash(t("quest.claimAll"), "success");
    }
}

async function clearPresetStages(stagePayloads, showMessage = true) {
    if (!ensureLoggedIn()) {
        return;
    }
    for (const payload of stagePayloads) {
        await reportStagePayload({
            stageId: payload.stageId,
            starCount: payload.starCount,
            cleared: true,
            score: payload.score,
        });
    }
    await loadStages();
    await loadQuests();
    await loadInventory();
    if (showMessage) {
        showFlash(t("stage.clearBatch"), "success");
    }
}

async function reportPresetScore(score, showMessage = true) {
    if (!ensureLoggedIn()) {
        return;
    }
    rankReportForm.score.value = String(score);
    const response = await reportRankScore(score);
    if (!response) {
        return;
    }
    renderOutput("rank", response.data);
    await loadMyRank();
    await loadRankList();
    if (showMessage) {
        showFlash(`${t("rank.reportScore")}: ${score}`, "success");
    }
}

async function hydrateAfterLogin() {
    fillSavePreset("default");
    await refreshAllPanels(false);
}

async function authedRequest(url, options, logIfMissing = true) {
    const session = readSession();
    if (!session?.token) {
        if (logIfMissing) {
            appendLog(t("log.requestBlocked"), { message: t("log.pleaseLoginFirst") });
            showFlash(t("flash.loginRequired"), "error");
        }
        return null;
    }
    return request(url, {
        ...options,
        headers: buildAuthHeaders(session.token, options?.headers),
    });
}

async function request(url, options) {
    try {
        const requestOptions = {
            ...(options ?? {}),
            headers: {
                "Content-Type": "application/json",
                ...(options?.headers ?? {}),
            },
        };
        const response = await fetch(url, requestOptions);
        const text = await response.text();
        const data = text ? safeJsonParse(text) : null;
        if (!response.ok || !data || data.code !== 0) {
            appendLog(t("log.requestFailed"), data ?? { status: response.status, body: text });
            showFlash(t("flash.requestFailed"), "error");
            return null;
        }
        return data;
    } catch (error) {
        appendLog(t("log.requestError"), { message: error.message });
        showFlash(t("flash.requestError"), "error");
        return null;
    }
}

function buildAuthHeaders(token, extraHeaders = {}) {
    return {
        "X-Game-Token": token,
        ...extraHeaders,
    };
}

function saveSession(session) {
    localStorage.setItem(storageKey, JSON.stringify(session));
}

function readSession() {
    const raw = localStorage.getItem(storageKey);
    return raw ? JSON.parse(raw) : null;
}

function clearSession() {
    localStorage.removeItem(storageKey);
}

function restoreSession() {
    const session = readSession();
    renderSession(session);
    if (session?.token) {
        hydrateAfterLogin();
    }
}

function renderSession(session) {
    if (!session) {
        sessionSummary.className = "session-summary empty";
        sessionSummary.textContent = t("session.notLoggedIn");
        return;
    }
    sessionSummary.className = "session-summary";
    sessionSummary.innerHTML = `
        <div><strong>${escapeHtml(session.nickname)}</strong> <span class="session-meta">(${escapeHtml(session.username)})</span></div>
        <div class="session-meta">${escapeHtml(t("session.accountId"))}: ${session.accountId}</div>
        <div class="session-meta">${escapeHtml(t("session.playerId"))}: ${session.playerId}</div>
        <div class="session-meta">${escapeHtml(t("session.token"))}:</div>
        <code class="token-code">${escapeHtml(session.token)}</code>
    `;
}

function renderInventory(inventory) {
    inventoryList.innerHTML = "";
    const items = inventory?.items ?? [];
    if (items.length === 0) {
        inventoryEmpty.style.display = "block";
        inventoryEmpty.textContent = t("inventory.empty");
        return;
    }
    inventoryEmpty.style.display = "none";
    for (const item of items) {
        const card = document.createElement("article");
        card.className = "inventory-card";
        card.innerHTML = `
            <div class="inventory-title">
                <span>${escapeHtml(t("inventory.item"))} #${item.itemId}</span>
                <span>x${item.quantity}</span>
            </div>
            <div class="inventory-meta">${escapeHtml(t("inventory.type"))}: ${item.itemType}</div>
            <div class="inventory-meta">${escapeHtml(t("inventory.ext"))}: ${escapeHtml(item.extJson || "{}")}</div>
        `;
        inventoryList.appendChild(card);
    }
}

function renderOutput(key, payload) {
    outputs[key].textContent = JSON.stringify(payload ?? null, null, 2);
}

function clearProtectedPanels() {
    inventoryList.innerHTML = "";
    inventoryEmpty.style.display = "block";
    inventoryEmpty.textContent = t("inventory.notLoaded");
    Object.values(outputs).forEach(output => {
        output.textContent = t("common.waiting");
    });
}

function appendLog(title, payload) {
    const text = `[${new Date().toLocaleString()}] ${title}\n${JSON.stringify(payload, null, 2)}\n\n`;
    logOutput.textContent = text + logOutput.textContent;
}

function readForm(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function captureFallbackTexts() {
    document.querySelectorAll("[data-i18n]").forEach(element => {
        fallbackTexts.set(element.dataset.i18n, element.textContent.trim());
    });
    document.querySelectorAll("[data-i18n-placeholder]").forEach(element => {
        fallbackTexts.set(element.dataset.i18nPlaceholder, element.getAttribute("placeholder") ?? "");
    });
}

function applyDefaultFormValues() {
    fillRandomAccountForms();
    fillSavePreset("default");
    addItemForm.quantity.value = "10";
    rankReportForm.score.value = "12345";
}

function getLocale() {
    const saved = localStorage.getItem(localeStorageKey);
    if (saved) {
        return saved;
    }
    return navigator.language.toLowerCase().startsWith("zh") ? "zh" : "en";
}

function setLocale(locale) {
    localStorage.setItem(localeStorageKey, locale);
}

function toggleLanguage() {
    const nextLocale = getLocale() === "zh" ? "en" : "zh";
    setLocale(nextLocale);
    applyLanguage(nextLocale);
    renderSession(readSession());
}

function applyLanguage(locale) {
    document.documentElement.lang = locale === "zh" ? "zh-CN" : "en";
    document.querySelectorAll("[data-i18n]").forEach(element => {
        element.textContent = t(element.dataset.i18n, locale);
    });
    document.querySelectorAll("[data-i18n-placeholder]").forEach(element => {
        element.setAttribute("placeholder", t(element.dataset.i18nPlaceholder, locale));
    });
}

function t(key, locale = getLocale()) {
    return translations[locale]?.[key] ?? fallbackTexts.get(key) ?? key;
}

function showFlash(message, type = "info") {
    flashBanner.className = `flash-banner ${type}`;
    flashBanner.textContent = message;
    flashBanner.classList.remove("hidden");
}

function ensureLoggedIn() {
    if (readSession()?.token) {
        return true;
    }
    showFlash(t("flash.loginRequired"), "error");
    return false;
}

function fillRandomAccountForms() {
    const account = buildDemoAccount();
    registerForm.username.value = account.username;
    registerForm.password.value = account.password;
    registerForm.nickname.value = account.nickname;
    loginForm.username.value = account.username;
    loginForm.password.value = account.password;
    showFlash(t("flash.randomFilled"), "info");
}

function syncLoginFormFromRegister() {
    loginForm.username.value = registerForm.username.value;
    loginForm.password.value = registerForm.password.value;
}

function buildDemoAccount(offset = 0) {
    const seed = `${Date.now().toString().slice(-6)}${Math.floor(Math.random() * 900 + 100) + offset}`;
    return {
        username: `dev${seed}`,
        password: "abc123456",
        nickname: `hero_${seed.slice(-4)}`,
    };
}

function buildSavePreset(kind, session) {
    const playerId = session?.playerId ?? 0;
    if (kind === "advanced") {
        return {
            playerId,
            chapter: 3,
            stage: 10002,
            level: 25,
            hp: 3880,
            mp: 480,
            gold: 28888,
            diamond: 888,
            inventoryPreview: [
                { itemId: 5001, qty: 6 },
                { itemId: 5002, qty: 8 },
                { itemId: 9001, qty: 28888 }
            ],
            team: [1101, 1102, 1103],
            options: {
                autoBattle: false,
                tutorial: false,
                music: 70,
                sfx: 80
            }
        };
    }
    return {
        playerId,
        chapter: 1,
        stage: 10001,
        level: 12,
        hp: 1880,
        mp: 260,
        gold: 8888,
        diamond: 188,
        inventoryPreview: [
            { itemId: 5001, qty: 3 },
            { itemId: 5002, qty: 5 }
        ],
        team: [1001, 1002, 1003],
        flags: {
            tutorial: true,
            dailyReward: false
        }
    };
}

function buildSavePayload() {
    return {
        slot: Number(saveUploadForm.slot.value) || 1,
        saveVersion: saveUploadForm.saveVersion.value.trim() || "1.0.0-dev",
        saveDataJson: saveUploadForm.saveDataJson.value.trim(),
    };
}

async function addInventoryItem(itemId, itemType, quantity, extJson) {
    return authedRequest("/api/devtool/inventory/add", {
        method: "POST",
        body: JSON.stringify({
            itemId,
            itemType,
            quantity,
            extJson,
        }),
    });
}

async function claimQuestById(questId) {
    return authedRequest("/api/devtool/quest/claim", {
        method: "POST",
        body: JSON.stringify({ questId }),
    });
}

async function reportStagePayload(payload) {
    return authedRequest("/api/devtool/stage/report", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

async function reportRankScore(score) {
    return authedRequest("/api/devtool/rank/report", {
        method: "POST",
        body: JSON.stringify({
            rankType: Number(rankReportForm.rankType.value) || 1,
            score,
        }),
    });
}

function safeJsonParse(text) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}
