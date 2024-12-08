package board.board_spring.configuation;

import board.board_spring.jwt.JwtAccessDeniedHandler;
import board.board_spring.jwt.JwtAuthenticationEntryPoint;
import board.board_spring.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;

    //권한별 url
    private final String[] adminUrl = {"/admin/**"};
    private final String[] permitAllUrl = {"/error", "/api/auth/login", "/auth/login", "/assets/**", "/webjars/**"};
    private final String[] anonymousUrl = {"/api/auth/signup", "/auth/signup", "/assets/**", "/webjars/**"};


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth //접근 url 권한 관리
                        .requestMatchers(adminUrl).hasAnyRole("ADMIN")
                        .requestMatchers(permitAllUrl).permitAll()
                        .requestMatchers(anonymousUrl).anonymous()
                        .anyRequest().authenticated() //이 외의 url은 인증받은 사용자만 접근 가능
                );
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { //비밀번호 암호화
        return new BCryptPasswordEncoder();
    }
}