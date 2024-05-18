package com.example.capstone.config;

import com.example.capstone.security.filter.LoginFilter;
import com.example.capstone.security.filter.RefreshTokenFilter;
import com.example.capstone.security.filter.TokenCheckFilter;
import com.example.capstone.security.handler.LoginSuccessHandler;
import com.example.capstone.security.service.MemberDetailsService;
import com.example.capstone.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final MemberDetailsService memberDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("------------ web configure-------------");

        //Spring Securit에게 정적 리소스 요청이 왔을 때, 보안 검사를 수행하지 않도록 설정
        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(memberDetailsService)
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        LoginFilter loginFilter = new LoginFilter("/generateToken");
        loginFilter.setAuthenticationManager(authenticationManager);

        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(jwtUtil);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);



        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                /*
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers(
                                        "/seller"
                                ).hasRole("SELLER"))
                */
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .anyRequest().permitAll())
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenCheckFilter(jwtUtil,memberDetailsService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil),TokenCheckFilter.class)
                .headers((headers)->
                        headers.contentTypeOptions(contentTypeOptionsConfig ->
                                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // disable session


        return httpSecurity.build();

    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, MemberDetailsService memberDetailsService){
        return new TokenCheckFilter(jwtUtil, memberDetailsService);
    }


}
