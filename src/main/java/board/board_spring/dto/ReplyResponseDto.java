package board.board_spring.dto;

import board.board_spring.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
public class ReplyResponseDto {

    private Long replyId;
    private String reContent;
    private Timestamp UpdatedAt;

    //정적 팩토리 메서드 추가
    public static ReplyResponseDto FindFromReply(Reply reply) {
        return new ReplyResponseDto(
                reply.getReplyId(),
                reply.getReContent(),
                reply.getUpdatedAt()
        );
    }
}
