package board.board_spring.service;

import board.board_spring.Exception.BusinessLogicException;
import board.board_spring.Exception.ExceptionCode;
import board.board_spring.entity.Board;
import board.board_spring.dto.ReplyPatchDto;
import board.board_spring.dto.ReplyPostDto;
import board.board_spring.dto.ReplyResponseDto;
import board.board_spring.entity.Reply;
import board.board_spring.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardService boardService;

    public Long createReply(ReplyPostDto replyPostDto, Long boardId) {
        Board board = boardService.findBoardId(boardId);
        Reply reply = new Reply();
        reply.setBoard(board);
        reply.setReContent(replyPostDto.getReContent());

        return replyRepository.save(reply).getReplyId();
    }

    public Long updateReply(ReplyPatchDto replyPatchDto, Long replyId) {
        Reply reply = findReplyId(replyId);
        reply.setReContent(replyPatchDto.getReContent());

        return replyRepository.save(reply).getReplyId();
    }

    public Reply findReplyId(Long replyId) {
        return replyRepository.findById(replyId).orElseThrow(()->new BusinessLogicException(ExceptionCode.REPLY_NOT_FOUND));
    }

    public void deleteReply(Long replyId) {
        findReplyId(replyId);
        replyRepository.deleteById(replyId);
    }

    public ReplyResponseDto findByReplyId(Long replyId) {
        Reply reply = findReplyId(replyId);
        return ReplyResponseDto.FindFromReply(reply);
    }

    public Page<ReplyResponseDto> findAllReply(Pageable pageable, Long boardId) {
        Board board = boardService.findBoardId(boardId);
        Page<Reply> replies = replyRepository.findByBoard(board, pageable);
        return replies.map(ReplyResponseDto::FindFromReply);
    }
}
