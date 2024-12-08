package board.board_spring.jwt.token;

import board.board_spring.jwt.blacklist.AccessTokenBlackList;
import board.board_spring.jwt.token.dto.TokenValidationResult;
import board.board_spring.entity.Member;
import board.board_spring.jwt.token.dto.TokenInfo;
import board.board_spring.principle.UserPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * 토큰을 생성하고 검증하는 클래스
 */
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_ID_KEY = "tokenId";
    private static final String USERNAME_KEY = "username";

    private final Key hashKey;
    private final long accessTokenValidationInMilliseconds;

    private final AccessTokenBlackList accessTokenBlackList;

    public TokenProvider(String secrete, long accessTokenValidationInSeconds, AccessTokenBlackList accessTokenBlackList) {
        byte[] keyBytes = Decoders.BASE64.decode(secrete);
        this.hashKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidationInMilliseconds = accessTokenValidationInSeconds * 1000;
        this.accessTokenBlackList = accessTokenBlackList;
    }

    //토큰 생성
    public TokenInfo createToken(Member member) {
        long currentTime = (new Date()).getTime();
        Date accessTokenExpireTime = new Date(currentTime + this.accessTokenValidationInMilliseconds);
        String tokenId = UUID.randomUUID().toString();

        //Access 토큰
        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim(AUTHORITIES_KEY, member.getGrade())
                .claim(USERNAME_KEY, member.getUsername())
                .claim(TOKEN_ID_KEY, tokenId)
                .signWith(hashKey, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenExpireTime)
                .compact();

        return TokenInfo.builder()
                .ownerEmail(member.getUsername())
                .tokenId(tokenId)
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .build();
    }

    //토큰 검증 후 TokenValidationResult로 검증 결과 return
    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(hashKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new TokenValidationResult(TokenStatus.TOKEN_VALID, TokenType.ACCESS, claims.get(TOKEN_ID_KEY, String.class), claims);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
            return getExpiredTokenValidationResult(e);

        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명");
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 서명");
            return new TokenValidationResult(TokenStatus.TOKEN_HASH_NOT_SUPPORTED, null, null, null);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰");
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        }
    }

    private static TokenValidationResult getExpiredTokenValidationResult(ExpiredJwtException e) {
        //만료된 토큰의 경우 토큰 자체는 정상이므로 claim들은 가져올 수 있다.
        Claims claims = e.getClaims();
        return new TokenValidationResult(TokenStatus.TOKEN_EXPIRED, TokenType.ACCESS,
                claims.get(TOKEN_ID_KEY, String.class), null);
    }

    public Authentication getAuthentication(String token, Claims claims) {
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString() //claims에서 권한 정보를 받아와
                        .split(",")) //파싱
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserPrinciple principle = new UserPrinciple(claims.getSubject(), claims.get(USERNAME_KEY, String.class), authorities);

        return new UsernamePasswordAuthenticationToken(principle, token, authorities);
    }

    public boolean isAccessTokenBlackList(String accessToken) {
        if (accessTokenBlackList.isTokenBlackList(accessToken)) {
            log.info("BlackListed Access Token");
            return true;
        }
        return false;
    }
}