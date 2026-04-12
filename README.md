# GameServer 可运行单体服务

这是一个基于以下技术栈的单体游戏服务端示例项目：

- JDK 21
- Spring Boot 3
- Maven
- MySQL 8
- Redis 7
- Proto3

项目保持单体结构，没有拆成微服务，也没有引入额外 ORM 或消息队列框架。当前已经实现：

- `auth`：注册、登录、token 校验、登出
- `player`：初始化玩家、获取玩家基础信息
- `save`：上传存档、拉取存档
- `inventory`：获取背包、增加道具、消耗道具
- `quest`：获取任务列表、更新任务进度、领取任务奖励
- `stage`：上报关卡结算、更新最高分和星级、发放首次通关奖励、推进任务进度
- `notice`：获取公告列表
- `version`：获取客户端版本、资源版本
- `rank`：上报分数、查询排行榜、查询玩家自己的排名

## 目录结构

```text
GameServer
├── docker-compose.yml
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/unityonline/gameserver
    │   │   ├── common
    │   │   ├── module/auth
    │   │   ├── module/player
    │   │   ├── module/save
    │   │   ├── module/inventory
    │   │   ├── module/quest
    │   │   ├── module/stage
    │   │   ├── module/notice
    │   │   ├── module/version
    │   │   └── module/rank
    │   └── resources
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── db/schema.sql
    │       └── proto
    └── test
```

## 启动步骤

### 1. 启动基础依赖

```bash
docker compose up -d
```

### 2. 初始化数据库

```bash
mysql -h127.0.0.1 -uroot -proot123456 < src/main/resources/db/schema.sql
```

### 3. 生成 protobuf 代码并编译

```bash
mvn clean compile
```

### 4. 启动服务

```bash
mvn spring-boot:run
```

### 5. 健康检查

```bash
curl http://127.0.0.1:8080/actuator/health
curl http://127.0.0.1:8080/api/public/ping
```

### 6. 打开本地可视化调试工具

浏览器访问：

```text
http://127.0.0.1:8080/devtool/index.html
```

这个页面可以直接：

- 注册新账号并自动拿到 token
- 登录已有账号
- 给当前角色添加装备或道具
- 查看当前角色背包

## 默认配置

- MySQL：`127.0.0.1:3306/game_server`
- Redis：`127.0.0.1:6379`
- Token Header：`X-Game-Token`
- Token 前缀：`Bearer`
- Profile：`dev`

公开接口已经加入白名单：

- `/api/auth/register`
- `/api/auth/login`
- `/api/auth/validate`
- `/api/notice/**`
- `/api/version/**`

登录态请求头示例：

```text
X-Game-Token: Bearer your_token_here
```

## Proto 接口

所有业务接口统一使用：

```text
Content-Type: application/x-protobuf
Accept: application/x-protobuf
```

### auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/validate`
- `POST /api/auth/logout`

### player

- `POST /api/player/init`
- `POST /api/player/profile`

### save

- `POST /api/save/upload`
- `POST /api/save/pull`

### inventory

- `POST /api/inventory/list`
- `POST /api/inventory/add`
- `POST /api/inventory/consume`

### quest

- `POST /api/quest/list`
- `POST /api/quest/progress/update`
- `POST /api/quest/reward/claim`

### stage

- `POST /api/stage/list`
- `POST /api/stage/report`

### notice / version

- `POST /api/notice/list`
- `POST /api/version/client`
- `POST /api/version/resource`

### rank

- `POST /api/rank/report`
- `POST /api/rank/list`
- `POST /api/rank/my`

### devtool

- `GET /devtool/index.html`
- `POST /api/devtool/register`
- `POST /api/devtool/login`
- `GET /api/devtool/me`
- `POST /api/devtool/inventory/add`
- `GET /api/devtool/inventory`

## 当前实现说明

### auth

- 密码使用 `PBKDF2WithHmacSHA256` 哈希存储，不明文入库
- token 存入 Redis
- 登录过滤器会从 Redis 校验 token，并写入线程上下文

### save

- 存档内容按 JSON 字符串上传
- MySQL 中使用 `JSON` 字段落地

### quest

- 任务由 `t_quest_config` 配置
- 玩家任务进度落在 `t_player_quest`
- 初始化玩家时会自动初始化任务状态

### stage

- 关卡配置来自 `t_stage_config`
- 玩家关卡结果落在 `t_player_stage`
- 首次通关时发放配置奖励
- 关卡结算会推进任务进度

### notice / version

- 公告走 `t_notice`
- 客户端版本和资源版本走 `t_version_config`

### rank

- Redis ZSet 作为实时排行榜
- MySQL `t_rank_record` 作为必要落地
- 上报分数时先写 MySQL，再在事务提交后刷新 Redis

### devtool

- 本地调试页放在 `src/main/resources/static/devtool`
- 调试页接口放在 `com.unityonline.gameserver.web.devtool`
- 调试接口使用 JSON，方便浏览器直接调用
- 调试接口内部仍然复用现有 `AuthService` 和 `InventoryService`

## 设计取舍

### 排行榜一致性

排行榜采用：

- MySQL 先落地分数
- Redis ZSet 提供实时查询
- Redis 更新放在事务提交后执行

这样做的原因是：

- 可以避免数据库事务回滚时 Redis 已经提前写入，出现脏榜单
- 查询时优先读 Redis，性能更好
- 启动后如果 Redis 为空，会从 MySQL 回填

这套方案更偏向“工程上实用且容易理解”，适合当前阶段学习和直接运行。

### 关卡奖励

当前关卡奖励和任务奖励都统一发到背包，避免在第一版里同时扩展金币、钻石、邮件等多个发奖通道。这样代码路径更简单，也更适合后面继续扩展。

### 可视化调试工具

主业务接口仍然保持 Proto3，不额外改协议。

之所以单独加一层本地 `devtool` JSON 调试接口，是因为：

- 浏览器和普通接口调试工具直接操作 protobuf 二进制不方便
- 你当前最常见的需求是验证“注册角色”和“加装备是否入库”
- 调试页内部复用业务服务，可以尽量贴近真实逻辑，又能大幅降低联调成本

## 补充
未完，待续...
