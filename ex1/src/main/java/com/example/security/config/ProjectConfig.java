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
 */
@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置密码编码器
     * 使用 BCrypt 算法对密码进行加密
     * 
     * @return PasswordEncoder 密码编码器实例
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }
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

