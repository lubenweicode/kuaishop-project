# kuaishop-back (快购商城后端)
基于 Spring Boot 3.x 开发的多模块电商后端项目，包含用户认证、商品管理、订单系统、秒杀活动等核心功能，遵循分层设计与模块化开发规范。

## 项目结构
### 模块划分（单向依赖，无循环）
```
kuaishop-back/
├── springbootdemo (父模块)
│   ├── model/          # 数据模型层（最底层，无依赖）
│   ├── common/         # 通用工具层（仅依赖 model）
│   ├── repository/     # 数据访问层（依赖 model + common）
│   ├── service/        # 业务逻辑层（依赖 repository + model + common）
│   ├── controller/     # 接口控制层（依赖 service + model + common）
│   └── app/            # 主程序入口（依赖所有业务模块，可选）
```

### 各模块核心职责
| 模块名       | 核心职责                                                                 | 关键依赖                     |
|--------------|--------------------------------------------------------------------------|------------------------------|
| `model`      | 数据模型定义（Entity/DTO/VO/枚举/常量）| 无                           |
| `common`     | 通用工具类、全局配置、分布式锁、JWT工具、Lua脚本加载等                    | model                        |
| `repository` | 数据访问层（MyBatis-Plus Mapper接口/XML、数据库交互）| model + common               |
| `service`    | 业务逻辑实现（用户认证、商品管理、订单处理、秒杀限流等）| repository + model + common  |
| `controller` | REST接口定义、请求参数校验、响应封装、拦截器配置                         | service + model + common     |
| `app`        | 项目启动入口、包扫描配置、全局Bean注册（独立模块，避免循环依赖）| controller + service + 所有模块 |

## 依赖关系分析
### 1. 核心依赖原则
- **单向依赖**：严格遵循 `controller → service → repository → common → model` 链路，禁止反向依赖
- **无循环依赖**：主程序入口独立（app模块），避免service/controller循环依赖
- **分层解耦**：禁止跨层依赖（如controller直接依赖repository）

### 2. 依赖树查看（Maven命令）
```bash
# 查看单个模块依赖（如service模块）
mvn dependency:tree -pl service

# 查看所有模块依赖
mvn dependency:tree

# 生成依赖分析报告（HTML格式）
mvn dependency:analyze-report
```

### 3. 关键第三方依赖
| 依赖名称                | 版本       | 用途                     | 引入模块       |
|-------------------------|------------|--------------------------|----------------|
| spring-boot-starter-web | 3.2.10      | Web接口、RESTful开发     | controller/app |
| mybatis-plus-spring-boot3-starter | 3.5.5 | 数据库CRUD、分页、条件查询 | repository     |
| spring-boot-starter-data-redis | 3.2.10 | Redis缓存、分布式锁       | service/common |
| lombok                  | 1.18.30     | 简化POJO代码（Getter/Setter） | 所有模块       |
| commons-lang3           | 3.14.0     | 通用字符串/集合工具       | common         |

## 快速启动
### 1. 环境要求
- JDK: 17+
- Maven: 3.8+
- Redis: 6.0+（分布式锁/缓存）
- MySQL: 8.0+（业务数据存储）

### 2. 启动步骤
```bash
# 1. 克隆代码
git clone https://github.com/lubenweicode/kuaishop-back.git
cd kuaishop-back/springbootdemo

# 2. 编译打包
mvn clean install

# 3. 启动主程序（app模块）
cd app
mvn spring-boot:run
```

### 3. 核心接口示例
| 接口路径                | 请求方式 | 功能描述       | 是否需要登录 |
|-------------------------|----------|----------------|--------------|
| `/api/v1/auth/login`    | POST     | 用户登录       | 否           |
| `/api/v1/auth/register` | POST     | 用户注册       | 否           |
| `/api/v1/product/list`  | GET      | 商品列表查询   | 是           |
| `/api/v1/seckill/start` | POST     | 秒杀活动启动   | 是（管理员） |

## 核心功能
### 1. 基础功能
- 用户认证：JWT令牌生成/校验、密码加密（SHA256）
- 商品管理：商品CRUD、分类查询、库存扣减
- 订单系统：分布式锁防超卖、订单创建/取消/查询
- 秒杀活动：限流控制、Redis缓存预热、Lua脚本原子操作

### 2. 核心技术亮点
- **分布式锁**：基于Redis + Lua脚本实现订单防超卖，避免并发问题
- **JWT拦截器**：统一token校验，放行免登录接口
- **秒杀限流**：基于Redis实现用户级/接口级限流，防止接口过载
- **多模块解耦**：严格分层，避免循环依赖，便于维护扩展

## 开发规范
### 1. 代码规范
- 包名统一：`com.kuaishop.模块名.功能包`（如 `com.kuaishop.controller.auth`）
- 命名规范：
  - Controller：XXController（如 AuthController）
  - Service接口：XXService（如 AuthService）
  - Service实现：XXServiceImpl（如 AuthServiceImpl）
  - Mapper接口：XXMapper（如 UserMapper）

### 2. 提交规范
```bash
# 提交格式：类型: 描述（模块名）
git commit -m "feat: 新增秒杀活动限流功能（service）"
git commit -m "fix: 修复分布式锁Lua脚本读取问题（common）"
git commit -m "refactor: 拆解service与controller循环依赖（父模块）"
```
- 提交类型：
  - `feat`：新增功能
  - `fix`：修复bug
  - `refactor`：代码重构（无功能变更）
  - `docs`：文档修改
  - `style`：格式调整（不影响逻辑）

### 3. 忽略文件规范
项目根目录 `.gitignore` 已排除以下文件，避免提交敏感/无用内容：
- Maven编译产物（target/、*.jar）
- IDE配置文件（.idea/、*.iml）
- 敏感配置（application*.yml、application*.properties）
- 日志/临时文件（logs/、*.log）
