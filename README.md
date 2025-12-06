# Spring Security 学习项目

这是一个用于学习 Spring Security 的示例项目，演示了 Spring Security 的基本配置和使用方法。

## 项目简介

本项目通过实际的代码示例，帮助你理解 Spring Security 的核心概念和配置方式，包括：
- 用户认证（Authentication）
- 权限授权（Authorization）
- 密码加密
- 内存用户管理
- HTTP 安全配置

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: 集成在 Spring Boot Starter Security 中
- **Maven**: 项目管理工具

## 项目结构

```
ex1/
├── src/main/java/com/example/security/
│   ├── config/
│   │   ├── ProjectConfig.java          # 项目配置类（用户详情服务、密码编码器）
│   │   └── SecurityConfig.java         # 安全配置类（HTTP 安全配置）
│   ├── controller/
│   │   └── HomeController.java         # 控制器类
│   └── SpringSecurityExampleApplication.java
└── pom.xml
```

## 核心功能

### 1. 用户认证配置（ProjectConfig.java）

使用 `InMemoryUserDetailsManager` 在内存中管理用户信息：

- **普通用户**
  - 用户名: `user`
  - 密码: `password`
  - 角色: `USER`

- **管理员用户**
  - 用户名: `admin`
  - 密码: `admin`
  - 角色: `ADMIN`

### 2. 密码加密（ProjectConfig.java）

使用 `BCryptPasswordEncoder` 对用户密码进行加密存储。

### 3. HTTP 安全配置（SecurityConfig.java）

配置了以下安全规则：

- `/` 和 `/public`: 允许所有用户访问（无需认证）
- `/admin`: 仅允许具有 `ADMIN` 角色的用户访问
- `/user`: 允许具有 `USER` 或 `ADMIN` 角色的用户访问
- 其他路径: 需要认证后才能访问

### 4. 登录/登出配置

- 使用表单登录（formLogin）
- 登录页面: `/login`
- 支持登出功能

## 运行项目

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+

### 启动步骤

1. 克隆或下载项目到本地

2. 进入项目目录：
   ```bash
   cd ex1
   ```

3. 使用 Maven 运行项目：
   ```bash
   mvn spring-boot:run
   ```

4. 访问应用：
   - 默认端口: `http://localhost:8080`

## 测试端点

启动项目后，你可以访问以下端点来测试不同的安全配置：

| 端点 | 访问权限 | 说明 |
|------|---------|------|
| `/` | 公开 | 欢迎页面 |
| `/public` | 公开 | 公开页面 |
| `/user` | 需要 USER 或 ADMIN 角色 | 用户页面 |
| `/admin` | 需要 ADMIN 角色 | 管理员页面 |

## 测试用户

### 测试用户登录

1. 访问任何受保护的端点（如 `/user` 或 `/admin`）
2. 系统会自动重定向到登录页面
3. 使用以下凭据登录：

**普通用户：**
- 用户名: `user`
- 密码: `password`

**管理员用户：**
- 用户名: `admin`
- 密码: `admin`

## 学习要点

### 1. 配置分离

- `ProjectConfig`: 负责用户管理和密码编码器配置
- `SecurityConfig`: 负责 HTTP 安全规则配置

这种分离使得配置更加清晰，便于维护。

### 2. 内存用户管理

使用 `InMemoryUserDetailsManager` 是学习和开发阶段常用的方式，但在生产环境中应该使用数据库或其他持久化存储。

### 3. 角色和权限

通过 `roles()` 方法为用户分配角色，然后使用 `hasRole()` 或 `hasAnyRole()` 在安全配置中控制访问权限。

### 4. 密码加密

使用 `BCryptPasswordEncoder` 确保密码在存储时是加密的，提高安全性。

## 扩展学习

在掌握基本概念后，可以尝试：

1. 集成数据库用户管理（JPA + UserDetailsService）
2. 实现 JWT 认证
3. 添加记住我（Remember Me）功能
4. 配置 OAuth2 认证
5. 实现方法级安全（@PreAuthorize）
6. 添加 CSRF 保护配置

## 参考资源

- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [Spring Boot Security 指南](https://spring.io/guides/topicals/spring-security-architecture)

## 许可证

本项目仅用于学习目的。
