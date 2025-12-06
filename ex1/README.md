# Spring Security 示例项目 (ex1)

这是 Spring Security 的第一个学习示例，演示了 Spring Boot 3.x 中 Spring Security 的基本配置和使用方法。

## 📋 项目概述

本项目展示了 Spring Security 的核心功能：
- ✅ 用户认证（Authentication）
- ✅ 权限授权（Authorization）
- ✅ 内存用户管理
- ✅ 密码加密
- ✅ HTTP 安全配置

## 🛠 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: 集成在 Spring Boot Starter Security 中
- **Maven**: 项目管理工具

## 📁 项目结构

```
ex1/
├── src/main/java/com/example/security/
│   ├── config/
│   │   ├── ProjectConfig.java          # 项目配置类（用户认证 Bean）
│   │   └── SecurityConfig.java         # 安全配置类（HTTP 安全规则）
│   ├── controller/
│   │   └── HomeController.java         # REST 控制器
│   └── SpringSecurityExampleApplication.java  # 启动类
├── src/main/resources/
│   └── application.properties          # 应用配置
├── pom.xml                             # Maven 配置
└── README.md                           # 本文件
```

## 🔑 核心配置说明

### 1. ProjectConfig.java - 用户认证配置

提供认证相关的 Bean：

- **PasswordEncoder Bean**: 使用 `BCryptPasswordEncoder` 进行密码加密
- **UserDetailsService Bean**: 使用 `InMemoryUserDetailsManager` 管理用户

**配置的用户：**

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| `user` | `password` | USER | 普通用户 |
| `admin` | `admin` | ADMIN | 管理员 |

### 2. SecurityConfig.java - HTTP 安全配置

定义 `SecurityFilterChain` Bean，配置 HTTP 请求的安全规则：

- **公开访问**: `/` 和 `/public` - 无需认证
- **用户权限**: `/user` - 需要 USER 或 ADMIN 角色
- **管理员权限**: `/admin` - 需要 ADMIN 角色
- **其他路径**: 需要认证后才能访问
- **登录配置**: 使用表单登录，登录页面为 `/login`
- **登出功能**: 支持登出

### 3. HomeController.java - REST 端点

提供测试端点：

- `GET /` - 欢迎页面（公开）
- `GET /public` - 公开页面（公开）
- `GET /user` - 用户页面（需要认证，USER 或 ADMIN 角色）
- `GET /admin` - 管理员页面（需要认证，ADMIN 角色）

## 🔗 配置类关系

### ProjectConfig 与 SecurityConfig 的关系

本项目采用**组合**的设计方式，将不同职责分离到不同的配置类：

```
ProjectConfig (提供 Bean)
    ├── UserDetailsService Bean  ───┐
    └── PasswordEncoder Bean         │
                                    │ Spring Security 自动注入
SecurityConfig (使用 Bean)          │
    └── SecurityFilterChain Bean  ←─┘
```

**工作原理：**

1. `ProjectConfig` 注册 `UserDetailsService` 和 `PasswordEncoder` Bean
2. `SecurityConfig` 定义 `SecurityFilterChain` Bean
3. Spring Security 自动将 `ProjectConfig` 的 Bean 注入到 `SecurityFilterChain` 中使用

**重要概念：**
- ❌ `ProjectConfig` **不是** `SecurityFilterChain`
- ✅ `ProjectConfig` **提供**认证相关的 Bean
- ✅ `SecurityConfig` **定义** `SecurityFilterChain`，配置 HTTP 安全规则

## 🚀 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+ 或 IDE（如 IntelliJ IDEA、Eclipse）

### 运行步骤

1. **进入项目目录**
   ```bash
   cd ex1
   ```

2. **使用 Maven 运行**
   ```bash
   mvn spring-boot:run
   ```

   或使用 IDE 直接运行 `SpringSecurityExampleApplication.java`

3. **访问应用**
   - 默认端口: `http://localhost:8080`

## 🧪 测试步骤

### 测试公开端点

1. 访问 `http://localhost:8080/` - 无需登录，直接访问
2. 访问 `http://localhost:8080/public` - 无需登录，直接访问

### 测试受保护端点

1. 访问 `http://localhost:8080/user` 或 `http://localhost:8080/admin`
2. 系统会自动重定向到登录页面
3. 使用以下凭据登录：

**普通用户登录：**
- 用户名: `user`
- 密码: `password`
- 可访问: `/`, `/public`, `/user`
- 不可访问: `/admin`（会返回 403 Forbidden）

**管理员登录：**
- 用户名: `admin`
- 密码: `admin`
- 可访问: `/`, `/public`, `/user`, `/admin`

## 📚 学习要点

### 1. 新式配置方式（SecurityFilterChain）

**旧方式（已弃用）：**
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

**新方式的优势：**
- ✅ 避免类继承，更灵活
- ✅ 支持多个 SecurityFilterChain
- ✅ 使用 Lambda 表达式，代码更简洁
- ✅ Spring Boot 3.x 要求（WebSecurityConfigurerAdapter 已被移除）

### 2. InMemoryUserDetailsManager

**特点：**
- 用户信息存储在内存中
- 应用重启后数据会丢失
- 适用于开发、测试和学习

**生产环境：** 应使用基于数据库的 `UserDetailsService` 实现

### 3. 密码加密

使用 `BCryptPasswordEncoder`：
- 自动生成盐值
- 每次加密结果不同
- 安全性高，适合生产环境

### 4. 角色和权限控制

- 通过 `roles()` 方法分配角色
- 使用 `hasRole()` 检查单个角色
- 使用 `hasAnyRole()` 检查多个角色

## 🔄 请求处理流程

```
用户请求 → SecurityFilterChain (SecurityConfig)
                ↓
            拦截请求
                ↓
        检查是否需要认证
                ↓
        需要认证？→ 使用 UserDetailsService (ProjectConfig) 验证用户
                ↓              ↓
                ↓        使用 PasswordEncoder (ProjectConfig) 验证密码
                ↓              ↓
                ←──────────────┘
                ↓
        授权检查（验证角色）
                ↓
        允许/拒绝访问
```

## 📖 关键代码说明

### ProjectConfig.java

```java
@Bean
public UserDetailsService userDetailsService() {
    // 创建用户详情
    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("password"))
        .roles("USER")
        .build();
    
    // 使用 InMemoryUserDetailsManager 管理用户
    return new InMemoryUserDetailsManager(user, admin);
}
```

### SecurityConfig.java

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authz -> authz
        .requestMatchers("/", "/public").permitAll()
        .requestMatchers("/admin").hasRole("ADMIN")
        .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
        .anyRequest().authenticated()
    )
    .formLogin(form -> form
        .loginPage("/login")
        .permitAll()
    );
    
    return http.build();
}
```

## 🔍 常见问题

### Q: ProjectConfig 是 SecurityFilterChain 吗？

**A:** 不是。`ProjectConfig` 只提供认证相关的 Bean（`UserDetailsService` 和 `PasswordEncoder`），而 `SecurityFilterChain` 是在 `SecurityConfig` 中定义的。

### Q: 为什么使用两个配置类？

**A:** 为了职责分离：
- `ProjectConfig`: 负责用户认证相关配置
- `SecurityConfig`: 负责 HTTP 安全规则配置

这种分离使代码更清晰，便于维护。

### Q: 可以使用 WebSecurityConfigurerAdapter 吗？

**A:** 不可以。在 Spring Boot 3.x 中，`WebSecurityConfigurerAdapter` 已被移除，必须使用 `SecurityFilterChain` Bean 方式。

## 📝 扩展学习

完成本示例后，可以继续学习：

1. 数据库用户管理（JPA + UserDetailsService）
2. JWT 认证
3. Remember Me 功能
4. OAuth2 认证
5. 方法级安全（@PreAuthorize）
6. CSRF 保护配置
7. 自定义登录页面

## 🔗 参考资源

- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [Spring Boot Security 指南](https://spring.io/guides/topicals/spring-security-architecture)
- [SecurityFilterChain 文档](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html)

## 📄 许可证

本项目仅用于学习目的。

