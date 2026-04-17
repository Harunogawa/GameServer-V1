# GameServer 单体后端示例

这是一个基于 Spring Boot 的 Unity 游戏后端示例项目。

## 技术栈

- JDK 21
- Spring Boot 3
- Maven
- MySQL 8
- Redis 7
- Proto3

## 当前模块

- `auth`：注册、登录、token 校验、登出
- `player`：玩家资料查询
- `save`：上传存档、拉取存档
- `inventory`：背包列表、添加道具、消耗道具
- `quest`：任务列表、推进进度、领取奖励
- `stage`：关卡列表、结算上报
- `notice`：有效公告查询
- `version`：客户端版本、资源版本
- `rank`：分数上报、排行榜、我的排名
- `devtool`：本地网页调试工具，仅开发环境可用

## 目录结构

```text
GameServer-V1
├── docker-compose.yml
├── pom.xml
├── README.md
├── luban
│   ├── Datas
│   ├── Defines
│   ├── gen.bat
│   ├── gen.ps1
│   └── luban.conf
└── src
    └── main
        ├── java/com/unityonline/gameserver
        └── resources
            ├── application.yml
            ├── application-dev.yml
            ├── config
            ├── db/schema.sql
            ├── devtool-static
            └── proto
```

## 快速启动

### 1. 启动依赖服务

```bash
docker compose up -d
```

### 2. 初始化数据库结构

```bash
mysql -h127.0.0.1 -uroot -p3069 < src/main/resources/db/schema.sql
```

### 3. 生成 protobuf 代码并编译

```bash
mvn clean compile
```

### 4. 启动服务

开发环境：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

模拟正式环境：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 5. 健康检查

```bash
curl http://127.0.0.1:8080/actuator/health
curl http://127.0.0.1:8080/api/public/ping
```

## 默认配置

- MySQL：`127.0.0.1:3306/game_server`
- Redis：`127.0.0.1:6379`
- Token Header：`X-Game-Token`
- Token 前缀：`Bearer`
- `application.yml` 默认 profile：`dev`

公开接口：

- `/api/auth/register`
- `/api/auth/login`
- `/api/auth/validate`
- `/api/notice/**`
- `/api/version/**`

鉴权请求头示例：

```text
X-Game-Token: Bearer your_token_here
```

## DevTool 使用限制

`devtool` 现在只允许在 `dev` profile 下使用。

具体行为如下：

- `dev` profile：
  - `/devtool/index.html` 可访问
  - `/api/devtool/**` 会注册并可调用
- 非 `dev` profile，例如 `prod`：
  - `/devtool/index.html` 返回 `404`
  - `/api/devtool/**` 返回 `404`

这个限制现在是从 3 层同时生效的：

- `DevToolController` 仅在 `dev` profile 下注册
- `/devtool/**` 静态资源仅在 `dev` profile 下映射
- devtool 白名单仅存在于 `application-dev.yml`

### 如何查看当前启用的 profile

1. 看 `src/main/resources/application.yml` 里的 `spring.profiles.active`
2. 如果启动命令里显式传了 profile，则以命令行为准

例如：

- `-Dspring-boot.run.profiles=dev`
- `--spring.profiles.active=prod`

都会覆盖默认配置。

### 如何在开发环境启用 DevTool

以 `dev` profile 启动：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

如果你是打包成 jar 后启动：

```bash
java -jar target/gameserver.jar --spring.profiles.active=dev
```

然后访问：

```text
http://127.0.0.1:8080/devtool/index.html
```

### 如何在服务器上关闭 DevTool

不要使用 `dev` profile，改为 `prod` 或其他正式环境 profile：

```bash
java -jar target/gameserver.jar --spring.profiles.active=prod
```

预期结果：

- `/devtool/index.html` 无法访问
- `/api/devtool/**` 无法访问

### 常见误区

- 不要把“注释掉某一行配置”当成环境切换方式
- 不要只隐藏前端页面而保留后端接口
- 不要把 devtool 白名单继续放在默认共享配置里

## Proto 接口说明

业务接口统一使用：

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

## Luban 接入方式

当前项目采用：

- Excel 配表源文件放在 `luban/Datas/#*.xlsx`
- 使用 Luban 导出 JSON
- 后端直接读取 `src/main/resources/config/*.json`

当前已切到 JSON 读取的配置包括：

- `quest`
- `stage`
- `notice`
- `version`

## 如何生成 Luban JSON

### 环境准备

需要本机安装：

- `.NET SDK 8.0+`
- Luban 工具目录，并能拿到 `Luban.dll`

参考资料：

- `https://www.datable.cn/docs/intro`
- `https://www.datable.cn/docs/beginner/quickstart`
- `https://www.datable.cn/docs/beginner/importtable`
- `https://www.datable.cn/en/docs/3.x/manual/commandtools`
- `https://github.com/focus-creative-games/luban_examples`

### 生成命令

```powershell
.\luban\gen.bat -LubanDll E:\Tools\Luban\Luban.dll
```

或者：

```powershell
$env:LUBAN_DLL = "E:\Tools\Luban\Luban.dll"
.\luban\gen.bat
```

生成结果：

- `src/main/resources/config/tbquestconfig.json`
- `src/main/resources/config/tbstageconfig.json`
- `src/main/resources/config/tbnotice.json`
- `src/main/resources/config/tbversionconfig.json`

## 推荐日常流程

1. 修改 `luban/Datas/#*.xlsx`
2. 执行 `.\luban\gen.bat`
3. 按需要使用 `dev` 或 `prod` profile 启动后端
4. 开发环境可用 devtool 联调，非开发环境只走正式接口验证
