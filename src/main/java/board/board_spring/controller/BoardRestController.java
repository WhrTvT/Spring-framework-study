package board.board_spring.controller;

import board.board_spring.dto.BoardPatchDto;
import board.board_spring.dto.BoardPostDto;
import board.board_spring.dto.BoardResponseDto;
import board.board_spring.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Getter @Setter // getter setter 생략
@RestController // RestAPI로 설계된 Controller -> ResponseEntity 사용에 자유로움
@RequestMapping("/api/board") // 기본 URL
@RequiredArgsConstructor // 생성자 생략
public class BoardRestController {
    private final BoardService boardService;

    // CREATE : 게시글 생성
    @PostMapping // POST 요청
    public ResponseEntity<Long> postBoard(@RequestBody @Validated BoardPostDto boardPostDto) {
        Long boardId = boardService.createBoard(boardPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardId); // 응답 status와 body 리턴
    }

    // UPDATE : 게시글 수정
    @PutMapping("/{boardId}") // PatchMapping하면 "Request method 'PUT' is not supported PutMapping" 에러발생 -> PutMapping으로 해야함. (https://m.blog.naver.com/hyoun1202/222058520474)
    public ResponseEntity<Long> patchBoard(@PathVariable("boardId")Long boardId,
                                     @RequestBody @Validated BoardPatchDto boardPatchDto) {
        boardService.updateBoard(boardPatchDto, boardId);
        return ResponseEntity.status(HttpStatus.OK).body(boardId);
    }

    // DELETE : 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Long> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // READ : 게시글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("boardId") Long boardId) {
        BoardResponseDto boardResponseDto = boardService.findByBoardId(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(boardResponseDto);
    }

    // readALl : 전체 게시글 조회
    @GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getAllBoards(
            @RequestParam(value = "page",defaultValue = "1")int page,
            @RequestParam(value = "size",defaultValue = "5")int size,
            @RequestParam(value = "sort",defaultValue = "updatedAt")String sort,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction) {

        // 정렬 방향 설정 (default는 DESC)
        Sort.Direction dic = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(dic, sort));
        Page<BoardResponseDto> boards = boardService.findAllBoards(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(boards);
    }
}
