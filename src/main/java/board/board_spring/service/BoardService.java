package board.board_spring.service;

import board.board_spring.Exception.BusinessLogicException;
import board.board_spring.Exception.ExceptionCode;
import board.board_spring.entity.Board;
import board.board_spring.dto.BoardPostDto;
import board.board_spring.dto.BoardPatchDto;
import board.board_spring.dto.BoardResponseDto;
import board.board_spring.entity.Member;
import board.board_spring.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    //아이디 찾고 없으면 Exception
    public Board findBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        // 값이 없으면 예외로 던지고 람다식을 사용해서 BusinessLogicException을 호출 (https://reeeemind.tistory.com/109)
    }

    public void isPermission(Member member, String username) {
        if (!member.getUsername().equals(username)) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
        }
    }

    public Long createBoard(BoardPostDto boardPostDto) {
        Board board = new Board();
        board.setTitle(boardPostDto.getTitle());
        board.setContent(boardPostDto.getContent());
        board.setMember(memberService.displayUsername(boardPostDto.getAuthor()));
        board.setAuthor(boardPostDto.getAuthor());
        return boardRepository.save(board).getBoardId();
    }

    public Long updateBoard(BoardPatchDto boardPatchDto, Long boardId) {
        Board board = findBoardId(boardId);
        isPermission(board.getMember(), boardPatchDto.getAuthor());
        board.setTitle(boardPatchDto.getTitle());
        board.setContent(boardPatchDto.getContent());
        board.setMember(memberService.displayUsername(boardPatchDto.getAuthor()));

        return boardRepository.save(board).getBoardId();
    }

    public BoardResponseDto findByBoardId(Long boardId) {
        Board board = findBoardId(boardId);
        board.setBoardCount(board.getBoardCount() + 1);
        boardRepository.save(board); // 게시글의 조회수를 증가시킨 후 저장하는 용도 -> Update 문 사용으로, updatedAt까지 같이 업데이트하는 문제 발생
        return BoardResponseDto.FindFromBoard(board);
    }

    public Page<BoardResponseDto> findAllBoards(Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        return boards.map(BoardResponseDto::FindFromBoard);
    }

    public void deleteBoard(Long boardId) {
        findBoardId(boardId);
        boardRepository.deleteById(boardId);
    }
}