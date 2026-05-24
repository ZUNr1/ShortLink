package org.zunr1.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //上面是强制无状态，不会创建HttpSession，完全依赖Token认证
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/login").permitAll()//登录接口允许匿名访问
                        .requestMatchers("/api/**").authenticated()//api接口需要认证
                        .requestMatchers("/admin/**").hasAuthority("SCOPE_ADMIN")//admin接口需要SCOPE_ADMIN权限
                        .anyRequest().permitAll()//其他接口允许匿名访问
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {}));
        return httpSecurity.build();
    }
}
