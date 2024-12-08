package board.board_spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA
@Getter @Setter // Lombok
@NoArgsConstructor // Lombok
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;
    private String content;

    @CreationTimestamp // 생성 시에만 now()
    private Timestamp createdAt;

    @UpdateTimestamp // 생성/수정 시, now()
    private Timestamp updatedAt;

    private Long boardCount = 0L; // 초기값은 0
    private String author;

    @OneToMany(mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}) // 1:N 게시글에 댓글은 여러 개
    private List<Reply> reply = new ArrayList<>();

    @ManyToOne // N:1 member.member_id는 primary key
    @JoinColumn(name = "member_id") // member 테이블 board 테이블 join
    private Member member; // join 대상

    /*
    @NoArgsConstructor 어노테이션으로 생성자 생략 가능
    public Board(Long boardID, String title, String content, Timestamp createdAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.boardID = boardID;
        this.title = title;
        this.content = content;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        DeletedAt = deletedAt;
    }
    */

    /*
    @Getter @Setter 어노테이션으로 생략 가능
    public Long getBoardID() {
        return boardID;
    }

    public void setBoardID(Long boardID) {
        this.boardID = boardID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }

    public Timestamp getDeletedAt() {
        return DeletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        DeletedAt = deletedAt;
    }
     */
}
