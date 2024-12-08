package board.board_spring.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardPatchDto {

    @NotEmpty // 빈값 -> 에러
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String author;
}
