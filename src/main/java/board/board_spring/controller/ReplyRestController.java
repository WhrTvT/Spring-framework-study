package board.board_spring.controller;

import board.board_spring.service.ReplyService;
import board.board_spring.dto.ReplyPatchDto;
import board.board_spring.dto.ReplyPostDto;
import board.board_spring.dto.ReplyResponseDto;
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

@Getter
@Setter
@RestController
@RequestMapping("/api/reply") // URL
@RequiredArgsConstructor
public class ReplyRestController {
    private final ReplyService replyService;

    // CREATE : 댓글 생성
    @PostMapping("/{boardId}") // POST 요청
    public ResponseEntity postReply(@PathVariable("boardId")Long boardId,
                                    @RequestBody @Validated ReplyPostDto replyPostDto) {
        Long replyId = replyService.createReply(replyPostDto, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyId);
    }

    // UPDATE : 댓글 수정
    @PutMapping("/{boardId}/{replyId}")
    public ResponseEntity patchReply(@PathVariable("boardId")Long boardId,
                                     @PathVariable("replyId")Long replyId,
                                     @RequestBody @Validated ReplyPatchDto replyPatchDto) {
        replyService.updateReply(replyPatchDto, replyId);
        return ResponseEntity.status(HttpStatus.OK).body(replyId);
    }

    // DELETE : 댓글 삭제
    @DeleteMapping("/{boardId}/{replyId}")
    public ResponseEntity deleteReply(@PathVariable("boardId") Long boardId,
                                      @PathVariable("replyId") Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // READ : 게시글에 등록된 댓글 모두 출력
    @GetMapping("{boardId}")
    public ResponseEntity<Page<ReplyResponseDto>> getAllReply(
            @PathVariable("boardId") Long boardId,
            @RequestParam(value = "page",defaultValue = "1")int page,
            @RequestParam(value = "size",defaultValue = "5")int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ReplyResponseDto> replies = replyService.findAllReply(pageable,boardId);
        return ResponseEntity.status(HttpStatus.OK).body(replies);
    }
}
