package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 
 * <p><strong>核心概念：SecurityFilterChain 替代了 configure() 方法</strong></p>
 * <p>本类中的 {@code filterChain()} 方法返回的 {@code SecurityFilterChain} Bean，</p>
 * <p>完全替代了旧版本中 {@code WebSecurityConfigurerAdapter.configure(HttpSecurity http)} 方法的功能。</p>
 * 
 * <p><strong>关于 WebSecurityConfigurerAdapter：</strong></p>
 * <p>WebSecurityConfigurerAdapter 是 Spring Security 旧版本中用于配置 Web 安全的主要类。</p>
 * 
 * <p><strong>WebSecurityConfigurerAdapter.configure() 方法的作用：</strong></p>
 * <ul>
 *   <li>通过继承 WebSecurityConfigurerAdapter 并重写 configure(HttpSecurity http) 方法</li>
 *   <li>在 configure() 方法中配置 HTTP 安全规则（授权、认证、登录等）</li>
 *   <li>Spring Security 会自动调用这个 configure() 方法来构建安全配置</li>
 * </ul>
 * 
 * <p><strong>SecurityFilterChain Bean 的作用：</strong></p>
 * <ul>
 *   <li>完全替代了 configure(HttpSecurity http) 方法的功能</li>
 *   <li>同样是配置 HTTP 安全规则（授权、认证、登录等）</li>
 *   <li>但不再需要继承，而是通过 @Bean 方法返回 SecurityFilterChain</li>
 *   <li>Spring Security 会自动发现并使用这个 SecurityFilterChain Bean</li>
 * </ul>
 * 
 * <p><strong>对比说明：</strong></p>
 * 
 * <p><strong>旧方式（已弃用，Spring Security 5.7+）：</strong></p>
 * <pre>
 * {@code
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig extends WebSecurityConfigurerAdapter {
 *     // configure() 方法配置 HTTP 安全规则
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
 * <pre>
 * {@code
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 *     // SecurityFilterChain Bean 方法替代了 configure() 方法
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http.authorizeHttpRequests(authz -> authz
 *             .requestMatchers("/public").permitAll()
 *             .anyRequest().authenticated()
 *         )
 *         .formLogin();
 *         return http.build();
 *     }
 * }
 * }
 * </pre>
 * 
 * <p><strong>总结：</strong></p>
 * <ul>
 *   <li>✅ SecurityFilterChain Bean 方法 = configure(HttpSecurity http) 方法的替代品</li>
 *   <li>✅ 功能完全相同：都是配置 HTTP 安全规则</li>
 *   <li>✅ 实现方式不同：一个是继承重写方法，一个是返回 Bean</li>
 *   <li>✅ 新方式更灵活：支持多个 SecurityFilterChain，避免继承限制</li>
 * </ul>
 * 
 * <p><strong>为什么改用新方式：</strong></p>
 * <ul>
 *   <li>避免类继承，更符合组合优于继承的原则</li>
 *   <li>支持多个 SecurityFilterChain，更灵活</li>
 *   <li>配置更加现代化，使用 Lambda 表达式</li>
 *   <li>Spring Security 5.7+ 已弃用 WebSecurityConfigurerAdapter</li>
 *   <li>Spring Boot 3.x 已完全移除 WebSecurityConfigurerAdapter</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 HTTP 安全过滤器链
     * 
     * <p><strong>重要：此方法完全替代了 WebSecurityConfigurerAdapter.configure(HttpSecurity http) 方法</strong></p>
     * 
     * <p><strong>功能对比：</strong></p>
     * <ul>
     *   <li><strong>旧方式</strong>：继承 WebSecurityConfigurerAdapter，重写 configure(HttpSecurity http) 方法</li>
     *   <li><strong>新方式</strong>：定义 @Bean 方法，返回 SecurityFilterChain（当前方法）</li>
     *   <li><strong>功能相同</strong>：都是配置 HTTP 安全规则（授权、认证、登录等）</li>
     * </ul>
     * 
     * <p><strong>工作方式：</strong></p>
     * <ul>
     *   <li>Spring Security 会自动发现此 SecurityFilterChain Bean</li>
     *   <li>在构建安全过滤器链时使用此方法返回的配置</li>
     *   <li>与旧版本的 configure() 方法被自动调用类似</li>
     * </ul>
     * 
     * @param http HttpSecurity 对象，用于配置 HTTP 请求的安全规则
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中的异常
     */
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
                
                // permitAll(): 登录页面本身允许所有用户访问（未登录用户也需要能访问登录页面）
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

}