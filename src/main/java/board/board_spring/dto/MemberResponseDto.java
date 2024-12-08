package board.board_spring.dto;

import board.board_spring.entity.Grade;
import board.board_spring.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MemberResponseDto {

    private Long memberId;

    @NotEmpty
    private String username;

    private Grade grade;

    //정적 팩토리 메서드 추가
    public static MemberResponseDto FindFromMember(Member member) {
        return new MemberResponseDto(
                member.getMemberId(),
                member.getUsername(),
                member.getGrade()
        );
    }
}
