# 一站式创业者综合服务平台

前后端分离架构，Spring Boot + Vue.js，支持创业者、投资人、服务商、管理员四类角色。

## 技术栈

- **后端**：Spring Boot 2.7、MyBatis Plus、Spring Security、JWT、MySQL 8、Redis
- **前端**：Vue 2、Vue Router、Vuex、Element UI、Axios

## 环境要求

- JDK 1.8、Maven 3.6+
- Node.js 14+、npm 6+
- MySQL 8.0（账号密码均为 root 时无需改配置）
- Redis（可选，未装时可注释掉后端 Redis 依赖或配置）

## 快速开始

### 1. 数据库

```bash
# 使用 root 登录 MySQL 后执行
mysql -u root -proot < database/create_database.sql
mysql -u root -proot entrepreneur_platform < database/create_table.sql
mysql -u root -proot entrepreneur_platform < database/init_data.sql
```

或在 MySQL 客户端中依次执行：

- `database/create_database.sql`（创建库）
- `database/create_table.sql`（建表）
- `database/init_data.sql`（初始化数据）

### 2. 后端

```bash
cd backend
mvn spring-boot:run
```

默认端口：8080，上下文路径：`/api`。  
数据库连接在 `src/main/resources/application.yml` 中，默认 `url` 使用 `root/root`。

**默认管理员**：首次启动后自动创建  
- 用户名：`admin`  
- 密码：`Admin@1234`  

测试账号（执行 init_data 后，首次启动会将其密码同步为 Admin@1234）：  
- 创业者：`founder1`  
- 投资人：`investor1`  
- 服务商：`provider1`  

### 3. 前端

```bash
cd frontend
npm install
npm run dev
```

默认端口：8081。访问 http://localhost:8081 ，使用 admin / Admin@1234 登录。

## 配置说明

- **后端**：`backend/src/main/resources/application.yml`  
  - 数据库：`spring.datasource.url/username/password`  
  - JWT：`jwt.secret`、`jwt.expire-hours`  
  - 文件上传目录：`file.upload-dir`  
  - 跨域：`cors.allowed-origins`（默认含 http://localhost:8081）

- **前端**：`frontend/vue.config.js` 中 devServer 代理 `/api` 到后端，端口 8080。

## 目录结构

```
lunwen/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 前端
├── database/         # 建表与初始化 SQL
│   ├── create_database.sql
│   ├── create_table.sql
│   └── init_data.sql
└── README.md
```

## 常见问题

1. **数据库连接失败**：检查 MySQL 已启动，库已创建，`application.yml` 中用户名密码正确（默认 root/root）。
2. **前端 401**：先登录获取 token；或检查后端已启动、端口 8080、跨域允许 8081。
3. **Redis 报错**：若未安装 Redis，可在 `application.yml` 中关闭或排除 Redis 自动配置，或安装 Redis 后启动。

## 接口说明

- 登录：`POST /api/auth/login`，body `{ "account": "admin", "password": "Admin@1234" }`
- 注册：`POST /api/auth/register`，body 见开发文档（角色、用户名、密码等）
- 其余接口需在请求头携带：`Authorization: Bearer <token>`

项目按开发文档实现用户管理、项目展示、投资对接、资源服务、消息通知、管理员管理等核心功能，无验证码、无账号锁定、无第三方支付/短信/邮件依赖，可在本地完整运行。
