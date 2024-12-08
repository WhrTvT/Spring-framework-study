package board.board_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {
    /**
     * @NotNull: null 비허용, "" 허용, " " 허용
     * @NotEmpty: null 비허용, "" 비허용, " " 허용
     * @NotBlank: null 비허용, "" 비허용, " " 비허용
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;
//    @Column(unique = true)
//    private String email;

    @NotNull
    private String password;

    @NotEmpty
    private String username;

    @Enumerated(EnumType.STRING)
    private Grade grade; // 권한처리

//    @Email
//    private String email;

    @Builder
    public Member(String password, String username, Grade grade) {
//        this.email = email;
        this.password = password;
        this.username = username;
        this.grade = grade;
    }

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    List<Board> boards = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
//    List<Reply> replies = new ArrayList<>();
}