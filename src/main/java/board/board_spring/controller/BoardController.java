package board.board_spring.controller;

import board.board_spring.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Getter @Setter
@Controller // 일반 컨트롤러
@RequestMapping("/board") // URL
@RequiredArgsConstructor
public class BoardController { // VIEW 페이지를 렌더링하는 Controller를 분리함. 장점 : 유지보수성/역할 분리 , 단점 : 복잡도/속도
    private final BoardService boardService;

    @GetMapping
    public String list(@RequestParam(value = "page",defaultValue = "1")int page,
                       @RequestParam(value = "size",defaultValue = "5")int size,
                       Model model) {
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "board/list";
    }

    @GetMapping("/post")
    public String create(){
        return "board/post";
    }

    @GetMapping("/{boardId}")
    public String detail(@PathVariable("boardId") Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/detail";
    }

    @GetMapping("/edit/{boardId}")
    public String edit(@PathVariable("boardId") Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/edit";
    }

    @GetMapping("/delete/{boardId}")
    public String delete(@PathVariable("boardId") Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/delete";
    }
}
