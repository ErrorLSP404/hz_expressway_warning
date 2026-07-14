# hz_expressway_warning

高速公路预警后台服务。对接前端管理端与算法侧告警推送，提供公路/路段、摄像头、告警核验、工单流转与数据统计等能力。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 / 框架 | Java 11、Spring Boot 2.6.6 |
| Web / 安全 | Spring Web、Spring Security、JWT |
| 持久化 | MySQL、Druid、MyBatis-Plus、PageHelper |
| 缓存 / 消息 | Redis、RabbitMQ |
| 工具库 | Hutool、Fastjson、Lombok |

## 功能模块

- **公路 / 路段**：高速公路与路段的增删改查
- **摄像头**：品牌、设备管理及路段绑定（`/rlsectioncamera`）
- **告警**：算法告警接收（`/highway/receiveAlarm`）、告警查询与人工核验
- **工单**：告警工单创建、流转、撤销、办结
- **统计**：事件量、实时与算法维度、类型分布、趋势、工单耗时等
- **用户 / 组织**：登录鉴权、密码修改、部门 / 办公区查询

## 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 5.7+ / 8.x
- Redis
- RabbitMQ（延迟队列等场景）

## 配置说明

主配置：`src/main/resources/application.yml`

通过 `spring.profiles.active` 切换环境：

| Profile | 说明 |
|---------|------|
| `dev` | 开发环境（`application-dev.yml`） |
| `prod` | 生产环境（`application-prod.yml`） |
| `zjhw` | 镇江公路局环境（`application-zjhw.yml`） |

仓库内配置为占位值，**请勿提交真实密码 / 证书**。推荐用环境变量覆盖：

| 变量 | 说明 |
|------|------|
| `MYSQL_HOST` / `MYSQL_USERNAME` / `MYSQL_PASSWORD` | MySQL |
| `REDIS_HOST` / `REDIS_PASSWORD` | Redis |
| `RABBITMQ_HOST` / `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | RabbitMQ |
| `JWT_SECRET` | JWT 密钥 |
| `SSL_KEYSTORE_PASSWORD` | SSL 证书密码（启用 SSL 时） |
| `UPLOAD_FILE_URL` | 文件上传地址 |
| `DRUID_CONFIG_DECRYPT` | 是否解密 Druid 密文密码（`true`/`false`） |

SSL 默认关闭；本地证书放 `src/main/resources/*.pfx`（已 gitignore）。JWT / 上传等见 `application.yml`。

默认端口：`8765`，默认 profile：`dev`。

## 快速启动

```bash
# 1. 准备库表，创建数据库 hz_expressway_warning

# 2. 通过环境变量或本地改写配置注入 MySQL / Redis / RabbitMQ（勿提交真实凭据）

# 3. 启动
mvn spring-boot:run
# 或打包后运行
mvn clean package -DskipTests
java -jar target/hz_expressway_warning-0.0.1-SNAPSHOT.jar
```

启动时会执行摄像头参数初始化（`InitDataService.initCamera`）。

## 主要接口前缀

| 前缀 | 说明 |
|------|------|
| `/login/**` | 登录（JWT 白名单） |
| `/user` | 用户信息、改密 |
| `/highway` | 公路 / 路段；含告警接收 |
| `/camera` | 摄像头 |
| `/rlsectioncamera` | 路段与摄像头关系 |
| `/alarm` | 告警查询与核验 |
| `/workOrder` | 工单 |
| `/statistics` | 统计看板 |
| `/dept` | 部门 / 办公区 |

Token 请求头：`Authorization: Hz-<token>`。

## 项目结构（简要）

```
src/main/java/net/huizhu/
├── controller/     # REST 接口
├── core/           # entity / dao / service
├── security/       # Spring Security、JWT 过滤器
├── rabbit/         # RabbitMQ 收发
└── common/         # 配置、工具、枚举、统一响应
```

## 许可证

内部项目，未声明开源协议前请勿对外分发。
