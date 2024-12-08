package board.board_spring.controller;

import board.board_spring.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter @Setter
@Controller
@RequestMapping("/board/{boardId}/reply")
@RequiredArgsConstructor
public class RelyController {
    private final BoardService boardService;

    @GetMapping
    public String getReplies(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/detail";
    }
}
