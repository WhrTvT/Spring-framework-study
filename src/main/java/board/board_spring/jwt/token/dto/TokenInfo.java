package board.board_spring.jwt.token.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 토큰의 정보를 담고 있는 클래스
 */
@Data
@ToString(exclude = {"accessToken"}) //accessToken이 너무 길기때문에 출력시 로그가 너무 길어져 accessToken은 제외
public class TokenInfo {
    private String accessToken;
    private Date accessTokenExpireTime; //만료 시간
    private String ownerEmail; //소유자의 이메일
    private String tokenId; //토큰 id

    @Builder
    public TokenInfo(String accessToken, Date accessTokenExpireTime, String ownerEmail, String tokenId) {
        this.accessToken = accessToken;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.ownerEmail = ownerEmail;
        this.tokenId = tokenId;
    }
}
