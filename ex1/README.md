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
    // configure() 方法配置 HTTP 安全规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/public").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin();
    }
}
```

**新方式（当前使用，Spring Boot 3.x）：**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // SecurityFilterChain Bean 方法替代了 configure() 方法
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/public").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin();
        return http.build();
    }
}
```

#### 对比说明

| 特性 | 旧方式 (configure) | 新方式 (SecurityFilterChain) |
|------|-------------------|----------------------------|
| **实现方式** | 继承 `WebSecurityConfigurerAdapter` | 定义 `@Bean` 方法 |
| **方法签名** | `configure(HttpSecurity http)` | `SecurityFilterChain filterChain(HttpSecurity http)` |
| **返回值** | void | `SecurityFilterChain` |
| **配置方式** | 链式调用 `.and()` | Lambda 表达式，直接返回 |
| **灵活性** | 只能有一个配置类 | 支持多个 SecurityFilterChain |
| **Spring Boot 3.x** | ❌ 不支持 | ✅ 必需 |

#### 工作方式对比

**旧方式工作流程：**
```
Spring Security 启动
    ↓
发现继承了 WebSecurityConfigurerAdapter 的类
    ↓
自动调用 configure(HttpSecurity http) 方法
    ↓
根据方法内的配置构建安全过滤器链
```

**新方式工作流程：**
```
Spring Security 启动
    ↓
发现 SecurityFilterChain Bean
    ↓
调用 Bean 方法（如 filterChain()）
    ↓
使用返回的 SecurityFilterChain 构建安全过滤器链
```

**新方式的优势：**
- ✅ 避免类继承，更符合组合优于继承的原则
- ✅ 支持多个 SecurityFilterChain，为不同 URL 模式配置不同规则
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

#### PasswordEncoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // 返回 BCryptPasswordEncoder 实例
    // BCrypt 是一种安全的密码哈希算法，会自动生成盐值
    return new BCryptPasswordEncoder();
}
```

#### UserDetailsService Bean

```java
@Bean
public UserDetailsService userDetailsService() {
    // 创建普通用户详情
    // User.builder() 是 Spring Security 提供的用户构建器
    UserDetails user = User.builder()
        .username("user")                              // 设置用户名
        .password(passwordEncoder().encode("password")) // 使用 PasswordEncoder 加密密码
        .roles("USER")                                 // 分配 USER 角色（实际存储为 ROLE_USER）
        .build();                                      // 构建 UserDetails 对象
    
    // 创建管理员用户详情
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN")                                // 分配 ADMIN 角色（实际存储为 ROLE_ADMIN）
        .build();
    
    // 使用 InMemoryUserDetailsManager 在内存中管理用户
    // 应用重启后数据会丢失，仅用于开发和测试
    return new InMemoryUserDetailsManager(user, admin);
}
```

### SecurityConfig.java

#### SecurityFilterChain Bean 配置详解

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ========== 1. 配置 HTTP 请求授权规则 ==========
        // authorizeHttpRequests: 配置哪些 HTTP 请求需要什么样的权限
        // authz: Lambda 表达式的参数，代表授权配置器
        .authorizeHttpRequests(authz -> authz
            // requestMatchers: 匹配特定的 URL 路径
            // permitAll(): 允许所有用户访问（无需认证）
            // 这里配置根路径 "/" 和 "/public" 路径为公开访问
            .requestMatchers("/", "/public").permitAll()
            
            // hasRole("ADMIN"): 只有具有 ADMIN 角色的用户才能访问
            // 注意：Spring Security 会自动在角色名前添加 "ROLE_" 前缀
            // 所以这里实际检查的是 "ROLE_ADMIN"
            .requestMatchers("/admin").hasRole("ADMIN")
            
            // hasAnyRole("USER", "ADMIN"): 具有 USER 或 ADMIN 任一角色即可访问
            // 实际检查的是 "ROLE_USER" 或 "ROLE_ADMIN"
            .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
            
            // anyRequest(): 匹配所有其他请求
            // authenticated(): 需要认证（登录）后才能访问
            // 这个规则会应用到所有未在上面明确配置的 URL
            .anyRequest().authenticated()
        )
        
        // ========== 2. 配置表单登录 ==========
        // formLogin: 启用基于表单的登录
        // form: Lambda 表达式的参数，代表表单登录配置器
        .formLogin(form -> form
            // loginPage("/login"): 指定自定义登录页面路径
            // 如果不配置，Spring Security 会使用默认的登录页面
            // 访问受保护资源时，会自动重定向到此页面
            .loginPage("/login")
            
            // permitAll(): 登录页面本身允许所有用户访问
            // 未登录用户也需要能访问登录页面才能进行登录
            .permitAll()
        )
        
        // ========== 3. 配置登出功能 ==========
        // logout: 启用登出功能
        // logout: Lambda 表达式的参数，代表登出配置器
        .logout(logout -> logout
            // permitAll(): 允许所有用户访问登出功能
            // 默认登出 URL 是 "/logout"
            // 用户访问此 URL 后会登出并清除认证信息
            .permitAll()
        );
    
    // build(): 构建并返回 SecurityFilterChain 对象
    // 这个方法会将所有配置组合成一个完整的安全过滤器链
    return http.build();
}
```

#### 配置项详细说明

**授权规则（authorizeHttpRequests）：**

| 配置 | 说明 | 示例 |
|------|------|------|
| `permitAll()` | 允许所有用户访问，无需认证 | `/public` 路径 |
| `authenticated()` | 需要登录才能访问 | 默认规则 |
| `hasRole("ADMIN")` | 需要特定角色（实际检查 ROLE_ADMIN） | `/admin` 路径 |
| `hasAnyRole("USER", "ADMIN")` | 需要多个角色中的任意一个 | `/user` 路径 |
| `hasAuthority("READ")` | 需要特定权限 | 更细粒度的权限控制 |

**角色名称说明：**

- 使用 `roles("USER")` 时，Spring Security 会自动添加 `ROLE_` 前缀
- 实际存储和检查的是 `ROLE_USER`，而不是 `USER`
- 使用 `hasRole("USER")` 时，会自动检查 `ROLE_USER`

**匹配顺序：**

Spring Security 按照配置顺序从上到下检查规则，**第一个匹配的规则会被应用**：

1. 先检查 `/` 和 `/public` → 允许所有访问
2. 再检查 `/admin` → 需要 ADMIN 角色
3. 再检查 `/user` → 需要 USER 或 ADMIN 角色
4. 最后检查 `anyRequest()` → 需要认证

**登录流程：**

1. 用户访问受保护资源（如 `/user`）
2. Spring Security 发现用户未认证
3. 自动重定向到 `/login` 登录页面
4. 用户输入用户名和密码
5. Spring Security 使用 `UserDetailsService` 验证用户
6. 验证成功后重定向到原本想访问的页面

**登出流程：**

1. 用户访问 `/logout`（默认登出 URL）
2. Spring Security 清除用户的认证信息（Session）
3. 重定向到登录页面或首页

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

