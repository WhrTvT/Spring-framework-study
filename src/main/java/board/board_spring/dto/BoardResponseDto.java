package board.board_spring.dto;

import board.board_spring.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
public class BoardResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private String author;
    private Timestamp UpdatedAt;

    //정적 팩토리 메서드 추가
    public static BoardResponseDto FindFromBoard(Board board) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthor(),
                board.getUpdatedAt()
        );
    }
}
