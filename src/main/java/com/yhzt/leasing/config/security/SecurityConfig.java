package com.yhzt.leasing.config.security;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private SysUserDetailsService sysUserDetailsService;

    @Resource
    private DataSource myRoutingDataSource;

    private JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl;

    @Resource
    private JwtAuthenticationFliter jwtAuthenticationFliter;

    @Resource
    private MyAuthenticationProvider myAuthenticationProvider;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sysUserDetailsService).passwordEncoder(passwordEncoder());
    }

    // 开启密码验证
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 静态资源的 访问权限
     * 
     */
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css", "/image", "/js");

    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                // 由于使用的是JWT，我们这里不需要csrf 只能防止 POST 方法 家 X-
                .csrf().disable()
                // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // .ignoringAntMatchers("/authentication")

                // .and()
                // 跨站共享配置
                .cors().and()
                // 基于 JWT 不需要 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().authorizeRequests()

                // 所有用户可以访问"/resources"目录下的资源以及访问"/home"和favicon.ico
                .antMatchers("/resources/**", "/home", "/**/favicon.ico", "/auth/*", "/login", "/kaptcha",
                        "/authentication", "/refreshToken")
                .permitAll()
                // 以"/admin"开始的URL，并需拥有 "ROLE_ADMIN" 角色权限,这里用hasRole不需要写"ROLE_"前缀
                .antMatchers("/admin/**").hasRole("admin")

                // persistent_logins
                /** 验证码 **/
                // .authenticationProvider(authenticationProvider)
                // .and()
                // .logout()
                // .logoutUrl("/logout")
                // .logoutSuccessUrl("/login.html") //不能同时 和 logoutSuccessHandler 使用
                // .logoutSuccessHandler(myLogouSuccessHandler)
                // .deleteCookies("JwtToken","JSESSIONID")

        ;

        // 禁用缓存
        http.headers().cacheControl();

        // 配置 jwtAuthenticationFliter 在 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(jwtAuthenticationFliter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public PersistentTokenRepository psistenTokenRepository() {
        jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        jdbcTokenRepositoryImpl.setDataSource(myRoutingDataSource);

        return jdbcTokenRepositoryImpl;
    }

    /**
     * 跨站配置 CORS 跨站资源共享 CSRF 跨站攻击防御
     * 
     * @return
     */
    @Bean
    public CorsConfigurationSource configurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.applyPermitDefaultValues();

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}