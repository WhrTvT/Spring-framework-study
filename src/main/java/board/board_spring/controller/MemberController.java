package board.board_spring.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter @Setter
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "auth/signup";
    }
}
