package com.unityonline.gameserver.common.context;

public final class LoginPlayerContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private LoginPlayerContext() {
    }

    /**
     * 设置当前线程的登录玩家信息。
     */
    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    /**
     * 获取当前线程的登录玩家信息。
     */
    public static LoginUser get() {
        return HOLDER.get();
    }

    /**
     * 获取当前线程的玩家 ID。
     */
    public static Long getPlayerId() {
        LoginUser loginUser = HOLDER.get();
        return loginUser == null ? null : loginUser.playerId();
    }

    /**
     * 清理当前线程上下文，避免线程复用带来的脏数据。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
