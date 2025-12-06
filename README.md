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

## 配置类关系说明

### ProjectConfig 与 SecurityConfig 的关系

本项目采用了配置分离的设计，将不同的职责分配到不同的配置类中：

#### 配置类职责划分

- **ProjectConfig**：
  - 提供 `UserDetailsService` Bean（用户认证服务）
  - 提供 `PasswordEncoder` Bean（密码编码器）
  - 使用 `InMemoryUserDetailsManager` 管理内存中的用户信息

- **SecurityConfig**：
  - 提供 `SecurityFilterChain` Bean（HTTP 安全过滤器链）
  - 配置 URL 访问规则和权限
  - 配置登录/登出行为

#### 重要概念

**ProjectConfig 不是 SecurityFilterChain**

- `ProjectConfig` 本身不是 `SecurityFilterChain`，它只是提供认证相关的 Bean
- `SecurityConfig` 中定义了 `SecurityFilterChain` Bean，用于配置 HTTP 安全规则
- Spring Security 会自动将 `ProjectConfig` 的 Bean 注入到 `SecurityFilterChain` 中使用

#### 工作流程

当用户发起请求时，Spring Security 的执行流程如下：

```
用户请求
    ↓
SecurityFilterChain (SecurityConfig) - 拦截请求
    ↓
需要认证？
    ↓ 是
使用 UserDetailsService (ProjectConfig) - 验证用户名
    ↓
使用 PasswordEncoder (ProjectConfig) - 验证密码
    ↓
授权检查 - 验证角色和权限
    ↓
允许/拒绝访问
```

#### 组合优于继承

这是一种**组合**的设计方式：
- `SecurityFilterChain` 组合使用其他配置类提供的 Bean
- 不同于旧版本的继承 `WebSecurityConfigurerAdapter` 方式
- 更加灵活，符合现代 Spring 的最佳实践

#### 为什么使用新方式？

- **避免类继承**：更符合组合优于继承的原则
- **支持多个 SecurityFilterChain**：可以为不同的 URL 模式配置不同的安全规则
- **配置更加现代化**：使用 Lambda 表达式，代码更简洁
- **Spring Boot 3.x 要求**：`WebSecurityConfigurerAdapter` 已被移除

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

### 1. 配置分离与组合设计

- `ProjectConfig`: 负责用户管理和密码编码器配置，提供认证相关的 Bean
- `SecurityConfig`: 负责 HTTP 安全规则配置，定义 SecurityFilterChain Bean
- Spring Security 会自动将 `ProjectConfig` 的 Bean 注入到 `SecurityFilterChain` 中使用

这种分离使得配置更加清晰，便于维护。使用组合而非继承，符合现代 Spring 的最佳实践。

### 2. 内存用户管理

使用 `InMemoryUserDetailsManager` 是学习和开发阶段常用的方式，但在生产环境中应该使用数据库或其他持久化存储。

### 3. 角色和权限

通过 `roles()` 方法为用户分配角色，然后使用 `hasRole()` 或 `hasAnyRole()` 在安全配置中控制访问权限。

### 4. 密码加密

使用 `BCryptPasswordEncoder` 确保密码在存储时是加密的，提高安全性。

### 5. 新式配置方式（SecurityFilterChain vs WebSecurityConfigurerAdapter）

#### 🔄 SecurityFilterChain 替代了 configure() 方法

**核心概念：**
- `SecurityFilterChain` Bean 方法**完全替代**了 `WebSecurityConfigurerAdapter.configure(HttpSecurity http)` 方法
- **功能完全相同**：都是配置 HTTP 安全规则（授权、认证、登录等）
- **实现方式不同**：一个是通过继承重写方法，一个是返回 Bean

**旧方式（已弃用，Spring Security 5.7+）：**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/public").permitAll()
            .anyRequest().authenticated();
    }
}
```

**新方式（当前使用，Spring Boot 3.x）：**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/public").permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

**对比说明：**
- **旧方式**：继承 `WebSecurityConfigurerAdapter`，重写 `configure()` 方法
- **新方式**：定义 `@Bean` 方法返回 `SecurityFilterChain`
- **功能相同**：都是配置 HTTP 安全规则
- **新方式优势**：避免类继承，支持多个 SecurityFilterChain，使用 Lambda 表达式，更灵活

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
