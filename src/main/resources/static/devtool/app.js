const storageKey = "gameserver.devtool.session";

const sessionSummary = document.getElementById("session-summary");
const logOutput = document.getElementById("log-output");
const inventoryList = document.getElementById("inventory-list");
const inventoryEmpty = document.getElementById("inventory-empty");

document.getElementById("register-form").addEventListener("submit", handleRegister);
document.getElementById("login-form").addEventListener("submit", handleLogin);
document.getElementById("add-item-form").addEventListener("submit", handleAddItem);
document.getElementById("load-inventory-btn").addEventListener("click", loadInventory);
document.getElementById("refresh-session-btn").addEventListener("click", refreshSession);

restoreSession();

/**
 * 处理注册请求，并把登录态缓存到本地。
 */
async function handleRegister(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        username: form.username.value.trim(),
        password: form.password.value,
        nickname: form.nickname.value.trim(),
    };
    const response = await request("/api/devtool/register", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    if (!response) {
        return;
    }
    saveSession(response.data);
    renderSession(response.data);
    appendLog("注册成功", response);
    await loadInventory();
}

/**
 * 处理登录请求，并把登录态缓存到本地。
 */
async function handleLogin(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        username: form.username.value.trim(),
        password: form.password.value,
    };
    const response = await request("/api/devtool/login", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    if (!response) {
        return;
    }
    saveSession(response.data);
    renderSession(response.data);
    appendLog("登录成功", response);
    await loadInventory();
}

/**
 * 处理添加道具请求。
 */
async function handleAddItem(event) {
    event.preventDefault();
    const session = readSession();
    if (!session?.token) {
        appendLog("添加道具失败", { message: "请先注册或登录" });
        return;
    }
    const form = event.currentTarget;
    const payload = {
        itemId: Number(form.itemId.value),
        itemType: Number(form.itemType.value),
        quantity: Number(form.quantity.value),
        extJson: form.extJson.value.trim() || "{}",
    };
    const response = await request("/api/devtool/inventory/add", {
        method: "POST",
        headers: buildAuthHeaders(session.token),
        body: JSON.stringify(payload),
    });
    if (!response) {
        return;
    }
    appendLog("添加道具成功", response);
    await loadInventory();
}

/**
 * 拉取当前会话的背包数据。
 */
async function loadInventory() {
    const session = readSession();
    if (!session?.token) {
        inventoryList.innerHTML = "";
        inventoryEmpty.style.display = "block";
        inventoryEmpty.textContent = "请先注册或登录后再查看背包";
        return;
    }
    const response = await request("/api/devtool/inventory", {
        method: "GET",
        headers: buildAuthHeaders(session.token),
    });
    if (!response) {
        return;
    }
    renderInventory(response.data);
    appendLog("背包刷新成功", response);
}

/**
 * 使用当前 token 刷新会话信息，确认登录态是否仍有效。
 */
async function refreshSession() {
    const session = readSession();
    if (!session?.token) {
        renderSession(null);
        appendLog("会话刷新", { message: "当前没有本地会话" });
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
    appendLog("会话刷新成功", response);
}

/**
 * 发起 JSON 请求并统一处理错误。
 */
async function request(url, options) {
    try {
        const response = await fetch(url, {
            headers: {
                "Content-Type": "application/json",
                ...options.headers,
            },
            ...options,
        });
        const data = await response.json();
        if (!response.ok || data.code !== 0) {
            appendLog("请求失败", data);
            return null;
        }
        return data;
    } catch (error) {
        appendLog("请求异常", { message: error.message });
        return null;
    }
}

/**
 * 根据 token 构建认证请求头。
 */
function buildAuthHeaders(token) {
    return {
        "X-Game-Token": token,
    };
}

/**
 * 将会话保存到浏览器本地存储。
 */
function saveSession(session) {
    localStorage.setItem(storageKey, JSON.stringify(session));
}

/**
 * 从浏览器本地存储读取会话。
 */
function readSession() {
    const raw = localStorage.getItem(storageKey);
    return raw ? JSON.parse(raw) : null;
}

/**
 * 恢复页面中的会话状态。
 */
function restoreSession() {
    const session = readSession();
    renderSession(session);
    if (session?.token) {
        loadInventory();
    }
}

/**
 * 将当前登录玩家渲染到状态卡片中。
 */
function renderSession(session) {
    if (!session) {
        sessionSummary.className = "session-summary empty";
        sessionSummary.textContent = "暂未登录";
        return;
    }
    sessionSummary.className = "session-summary";
    sessionSummary.innerHTML = `
        <div><strong>${escapeHtml(session.nickname)}</strong> <span class="session-meta">(${escapeHtml(session.username)})</span></div>
        <div class="session-meta">accountId: ${session.accountId}</div>
        <div class="session-meta">playerId: ${session.playerId}</div>
        <div class="session-meta">token:</div>
        <code class="token-code">${escapeHtml(session.token)}</code>
    `;
}

/**
 * 渲染背包道具列表。
 */
function renderInventory(inventory) {
    inventoryList.innerHTML = "";
    const items = inventory?.items ?? [];
    if (items.length === 0) {
        inventoryEmpty.style.display = "block";
        inventoryEmpty.textContent = "当前背包为空";
        return;
    }
    inventoryEmpty.style.display = "none";
    for (const item of items) {
        const card = document.createElement("article");
        card.className = "inventory-card";
        card.innerHTML = `
            <div class="inventory-title">
                <span>Item #${item.itemId}</span>
                <span>x${item.quantity}</span>
            </div>
            <div class="inventory-meta">类型: ${item.itemType}</div>
            <div class="inventory-meta">扩展: ${escapeHtml(item.extJson || "{}")}</div>
        `;
        inventoryList.appendChild(card);
    }
}

/**
 * 追加调试日志，方便观察每次交互结果。
 */
function appendLog(title, payload) {
    const text = `[${new Date().toLocaleString()}] ${title}\n${JSON.stringify(payload, null, 2)}\n\n`;
    logOutput.textContent = text + logOutput.textContent;
}

/**
 * 对文本进行简单转义，避免直接插入 HTML。
 */
function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}
