package board.board_spring.configuation;

import board.board_spring.jwt.JwtAccessDeniedHandler;
import board.board_spring.jwt.JwtAuthenticationEntryPoint;
import board.board_spring.jwt.JwtProperties;
import board.board_spring.jwt.blacklist.AccessTokenBlackList;
import board.board_spring.jwt.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
    private final AccessTokenBlackList accessTokenBlackList;

    @Bean
    public TokenProvider tokenProvider(JwtProperties jwtProperties) {
        //return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds());
        return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds(), accessTokenBlackList);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }
}