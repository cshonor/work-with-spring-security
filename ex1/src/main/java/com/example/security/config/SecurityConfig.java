package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 
 * <p><strong>关于 WebSecurityConfigurerAdapter：</strong></p>
 * <p>WebSecurityConfigurerAdapter 是 Spring Security 旧版本中用于配置 Web 安全的主要类。</p>
 * 
 * <p><strong>WebSecurityConfigurerAdapter 的作用：</strong></p>
 * <ul>
 *   <li>提供了一种继承的方式来配置 Spring Security</li>
 *   <li>通过重写 configure() 方法来配置 HTTP 安全规则</li>
 *   <li>可以配置用户认证、授权规则、登录/登出等</li>
 *   <li>简化了 Spring Security 的配置流程</li>
 * </ul>
 * 
 * <p><strong>旧方式（已弃用，Spring Security 5.7+）：</strong></p>
 * <pre>
 * {@code
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig extends WebSecurityConfigurerAdapter {
 *     @Override
 *     protected void configure(HttpSecurity http) throws Exception {
 *         http.authorizeRequests()
 *             .antMatchers("/public").permitAll()
 *             .anyRequest().authenticated()
 *             .and()
 *             .formLogin();
 *     }
 * }
 * }
 * </pre>
 * 
 * <p><strong>新方式（当前使用，Spring Boot 3.x 推荐）：</strong></p>
 * <ul>
 *   <li>不再继承 WebSecurityConfigurerAdapter</li>
 *   <li>使用 @Bean 方法返回 SecurityFilterChain</li>
 *   <li>配置更加函数式和灵活</li>
 *   <li>避免了继承带来的限制</li>
 * </ul>
 * 
 * <p><strong>为什么改用新方式：</strong></p>
 * <ul>
 *   <li>避免类继承，更符合组合优于继承的原则</li>
 *   <li>支持多个 SecurityFilterChain，更灵活</li>
 *   <li>配置更加现代化，使用 Lambda 表达式</li>
 *   <li>Spring Security 5.7+ 已弃用 WebSecurityConfigurerAdapter</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 HTTP 安全过滤器链
     * 
     * <p>这是 Spring Security 的新式配置方法，替代了继承 WebSecurityConfigurerAdapter 的方式。</p>
     * 
     * @param http HttpSecurity 对象，用于配置 HTTP 请求的安全规则
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/public").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
        
        return http.build();
    }

}