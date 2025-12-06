package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 项目配置类
 * 负责配置项目级别的 Bean，包括用户详情服务和密码编码器
 * 
 * <p><strong>Spring Security 如何识别这个配置类中的 Bean：</strong></p>
 * 
 * <p><strong>1. @Configuration 注解的作用：</strong></p>
 * <ul>
 *   <li>@Configuration 告诉 Spring 容器这是一个配置类</li>
 *   <li>Spring Boot 启动时会自动扫描所有带 @Configuration 的类</li>
 *   <li>扫描路径由 @SpringBootApplication 注解控制（默认扫描主类所在包及其子包）</li>
 * </ul>
 * 
 * <p><strong>2. @Bean 注解的作用：</strong></p>
 * <ul>
 *   <li>@Bean 注解的方法会被 Spring 容器调用</li>
 *   <li>方法返回的对象会被注册为 Spring Bean，存储在 Spring 容器中</li>
 *   <li>Bean 的名称默认是方法名（如 "passwordEncoder", "userDetailsService"）</li>
 *   <li>Bean 的类型是方法返回类型（如 PasswordEncoder, UserDetailsService）</li>
 * </ul>
 * 
 * <p><strong>3. Spring Security 的自动发现机制：</strong></p>
 * <ul>
 *   <li>Spring Security 启动时会自动在 Spring 容器中查找特定类型的 Bean</li>
 *   <li>查找 UserDetailsService 类型的 Bean → 用于用户认证</li>
 *   <li>查找 PasswordEncoder 类型的 Bean → 用于密码验证</li>
 *   <li>如果找到，会自动注入并使用；如果没找到，使用默认实现</li>
 * </ul>
 * 
 * <p><strong>4. 工作流程：</strong></p>
 * <pre>
 * Spring Boot 启动
 *     ↓
 * 扫描 @Configuration 类（包括 ProjectConfig）
 *     ↓
 * 调用 @Bean 方法，注册 Bean 到容器
 *     ↓
 * Spring Security 初始化
 *     ↓
 * 在容器中查找 UserDetailsService 类型 Bean → 找到 userDetailsService()
 *     ↓
 * 在容器中查找 PasswordEncoder 类型 Bean → 找到 passwordEncoder()
 *     ↓
 * 自动注入到 SecurityFilterChain 中使用
 * </pre>
 * 
 * <p><strong>5. 为什么不需要显式注入：</strong></p>
 * <ul>
 *   <li>Spring Security 使用类型匹配（Type-based）的自动装配</li>
 *   <li>不需要在 SecurityConfig 中显式声明依赖</li>
 *   <li>Spring Security 会自动从容器中获取需要的 Bean</li>
 *   <li>这是 Spring 的依赖注入（Dependency Injection）机制</li>
 * </ul>
 * 
 * <p><strong>配置类之间的关系：</strong></p>
 * <ul>
 *   <li><strong>ProjectConfig</strong>：提供认证相关的 Bean（UserDetailsService, PasswordEncoder）</li>
 *   <li><strong>SecurityConfig</strong>：提供 SecurityFilterChain Bean，配置 HTTP 安全规则</li>
 *   <li>Spring Security 自动将 ProjectConfig 的 Bean 注入到 SecurityFilterChain 中使用</li>
 * </ul>
 * 
 * <p><strong>注意：</strong>此配置类使用新式 Bean 配置方式，不再继承 WebSecurityConfigurerAdapter</p>
 * <p>在 Spring Boot 3.x 中，WebSecurityConfigurerAdapter 已被移除，必须使用 Bean 方式配置</p>
 */
@Configuration
public class ProjectConfig {

    /**
     * 配置密码编码器
     * 使用 BCrypt 算法对密码进行加密
     * 
     * @return PasswordEncoder 密码编码器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户详情服务
     * 
     * <p>使用 InMemoryUserDetailsManager 在内存中管理用户信息。</p>
     * 
     * <p><strong>InMemoryUserDetailsManager 的作用：</strong></p>
     * <ul>
     *   <li>实现 UserDetailsService 接口，提供用户认证服务</li>
     *   <li>将用户信息存储在内存中（非持久化）</li>
     *   <li>适用于开发、测试和学习阶段</li>
     *   <li>应用重启后用户信息会丢失</li>
     * </ul>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>快速原型开发</li>
     *   <li>单元测试和集成测试</li>
     *   <li>学习 Spring Security 概念</li>
     * </ul>
     * 
     * <p><strong>生产环境：</strong>应使用基于数据库的 UserDetailsService 实现</p>
     * 
     * <p>当前配置包含两个测试用户：</p>
     * <ul>
     *   <li>普通用户：username="user", password="password", role="USER"</li>
     *   <li>管理员：username="admin", password="admin", role="ADMIN"</li>
     * </ul>
     * 
     * @return UserDetailsService 用户详情服务实例（InMemoryUserDetailsManager）
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // 创建普通用户，具有 USER 角色
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

        // 创建管理员用户，具有 ADMIN 角色
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
            .build();

        // 使用 InMemoryUserDetailsManager 管理内存中的用户
        // 注意：用户信息存储在内存中，应用重启后会丢失
        return new InMemoryUserDetailsManager(user, admin);
    }

}

