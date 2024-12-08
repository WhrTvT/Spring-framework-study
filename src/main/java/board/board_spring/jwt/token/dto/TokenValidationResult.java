package board.board_spring.jwt.token.dto;

import board.board_spring.jwt.token.TokenStatus;
import board.board_spring.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import lombok.*;

/**
 * 토큰의 검증 결과를 담고 있는 클래스
 */
@Getter @Setter
@ToString
@Data
@AllArgsConstructor
public class TokenValidationResult {
    private TokenStatus tokenStatus; //토큰 상태
    private TokenType tokenType; //토큰 타입
    private String tokenId; //토큰 id
    private Claims claims;

    public String getEmail() {
        if (claims == null) {
            throw new IllegalStateException("Claim value is null");
        }
        return claims.getSubject(); //토큰 소유자의 이메일 return
    }

    public boolean isValid() {
        return TokenStatus.TOKEN_VALID == this.tokenStatus; //toeknStauts가 정상 토큰이면 true return
    }
}