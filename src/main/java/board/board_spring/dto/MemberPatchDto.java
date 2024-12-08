package board.board_spring.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberPatchDto {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
