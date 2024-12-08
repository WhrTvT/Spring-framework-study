package board.board_spring.repository;

import board.board_spring.entity.Board;
import board.board_spring.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findByBoard(Board board, Pageable pageable);
}
