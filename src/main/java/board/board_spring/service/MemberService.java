package board.board_spring.service;

import board.board_spring.Exception.BusinessLogicException;
import board.board_spring.Exception.ExceptionCode;
import board.board_spring.entity.Grade;
import board.board_spring.entity.Member;
import board.board_spring.dto.MemberPatchDto;
import board.board_spring.dto.MemberPostDto;
import board.board_spring.dto.MemberResponseDto;
import board.board_spring.jwt.blacklist.AccessTokenBlackList;
import board.board_spring.jwt.token.TokenProvider;
import board.board_spring.jwt.token.dto.TokenInfo;
import board.board_spring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$"; //해당 정규표현식을 만족하기 위해서는 최소 8자리 + 영어, 숫자, 특수문자를 모두 포함해야함.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccessTokenBlackList accessTokenBlackList;

    public Member findMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public Member displayUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    // 사용자명 중복 체크
    public void checkDuplicateUsername(String username) {
        if (!memberRepository.existsByUsername(username)) {
            return;
        }

        throw new IllegalArgumentException("이미 등록된 사용자명입니다.");
    }

    /*
    // 이메일 중복 체크
    public void checkDuplicateEmail(String email) {
        if (!memberRepository.existsByEmail(email)) {
            return;
        }

        throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }
    */

    // 비밀번호 정책에 맞는지 체크
    private void checkPasswordStrength(String password) {
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return;
        }

        throw new IllegalArgumentException("비밀번호는 최소 8자리여야하고 영어, 숫자, 특수문자를 포함해야 합니다.");
    }

    // 등록된 비밀번호와 입력된 비밀번호가 일치하는지 체크
    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("비밀번호가 일치하지 않음.");
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 등록된 사용자명과 입력된 사용자명이 일치하는지 체크
    private Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> {
            log.info("계정이 존재하지 않음.");
            return new IllegalArgumentException("계정이 존재하지 않습니다.");
        });
    }

    // CREATE : 사용자 생성
    @Transactional
    public Long createMember(MemberPostDto memberPostDto) {
        checkDuplicateUsername(memberPostDto.getUsername());
        checkPasswordStrength(memberPostDto.getPassword());
        Member member = new Member();
        member.setUsername(memberPostDto.getUsername());
        member.setPassword(passwordEncoder.encode(memberPostDto.getPassword()));
        member.setGrade(Grade.USER);
//        member.setEmail(memberPostDto.getEmail());

        return memberRepository.save(member).getMemberId();

    }

    // UPDATE : 사용자 수정
    public Long updateMember(MemberPatchDto memberPatchDto, Long memberId) {
//        checkDuplicateUsername(MemberPatchDto.getUsername());
//        checkPasswordStrength(MemberPatchDto.getPassword());
        Member member = findMemberId(memberId);
        member.setUsername(memberPatchDto.getUsername());
        member.setPassword(passwordEncoder.encode(memberPatchDto.getPassword()));

        return memberRepository.save(member).getMemberId();
    }

    // DELETE : 사용자 삭제
    public void deleteMember(Long memberId) {
        findMemberId(memberId);
        memberRepository.deleteById(memberId);
    }

    // READ : 특정 사용자 조회 (ID로 조회)
    public MemberResponseDto getUserInfo(String username) {
        Member member = findMemberByUsername(username);

        return MemberResponseDto.FindFromMember(member);
    }

    //로그인
    public TokenInfo loginMember(String username, String password) {
        try {
            Member member = findMemberByUsername(username);

            checkPassword(password, member);

            return tokenProvider.createToken(member);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //로그아웃
    public void logout(String accessToken, String username) {
        accessTokenBlackList.setBlackList(accessToken, username);
    }
}