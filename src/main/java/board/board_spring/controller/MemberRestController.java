package board.board_spring.controller;

import board.board_spring.Exception.ApiResponseJson;
import board.board_spring.dto.MemberLoginDto;
import board.board_spring.jwt.token.dto.TokenInfo;
import board.board_spring.principle.UserPrinciple;
import board.board_spring.service.MemberService;
import board.board_spring.dto.MemberPatchDto;
import board.board_spring.dto.MemberPostDto;
import board.board_spring.dto.MemberResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    // CREATE: 사용자 생성
    @PostMapping("/signup")
    public ResponseEntity<Long> createMember(@RequestBody @Validated MemberPostDto memberPostDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다");
        }

        Long memberId = memberService.createMember(memberPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId); // 응답상태 body 값
    }

    // UPDATE: 사용자 수정
    @PatchMapping("/{memberId}")
    public ResponseEntity patchMember(
            @PathVariable("memberId") Long memberId,
            @RequestBody MemberPatchDto memberPatchDto) {
        memberService.updateMember(memberPatchDto, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(memberId);
    }

    // DELETE: 사용자 삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // READ: 특정 사용자 조회 (ID로 조회)
    @GetMapping("/userinfo")
    public ResponseEntity getMember(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        String username = userPrinciple.getUsername();
        MemberResponseDto memberResponseDto = memberService.getUserInfo(username);
        return ResponseEntity.status(HttpStatus.OK).body(memberResponseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponseJson login(@Validated @RequestBody MemberLoginDto memberLoginDto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        TokenInfo tokenInfo = memberService.loginMember(memberLoginDto.getUsername(), memberLoginDto.getPassword());

        log.info("Token issued: {}", tokenInfo);

        session.setAttribute("Authorization", "Bearer " + tokenInfo.getAccessToken());

        return new ApiResponseJson(HttpStatus.OK, tokenInfo);
    }

    @PostMapping("/logout")
    public ApiResponseJson logout(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestHeader("Authorization") String authHeader) {
        String username = userPrinciple.getUsername();

        log.info("로그아웃 사용자: {}", username);

        // Bearer 를 문자열에서 제외하기 위해 substring을 사용
        memberService.logout(authHeader.substring(7), username);

        return new ApiResponseJson(HttpStatus.OK, "로그아웃 성공");
    }
}