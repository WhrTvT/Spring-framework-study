package board.board_spring.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReplyPatchDto {

    @NotEmpty
    private String reContent;
}
