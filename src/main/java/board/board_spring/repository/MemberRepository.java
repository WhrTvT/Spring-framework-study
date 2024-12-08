package board.board_spring.repository;

import board.board_spring.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

//    Optional<Member> findByEmail(String email);
//    boolean existsByEmail(String email);

    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
}
