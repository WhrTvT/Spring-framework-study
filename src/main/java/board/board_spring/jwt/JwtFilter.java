package board.board_spring.jwt;

import board.board_spring.jwt.token.TokenProvider;
import board.board_spring.jwt.token.TokenStatus;
import board.board_spring.jwt.token.dto.TokenValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
@Component // filter로 등록하기 위해
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization"; //header에서 token 정보를 추천해준다.
    private static final String BEARER_REGEX = "Bearer ([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_.\\-\\+\\/=]*)"; //jwt token은 .을 기준으로 header, payload, signature로 나뉘어져있어 정규표현식을 통해 1차적으로 jwt token이 유효한지 확인한다.
    private static final Pattern BEARER_PATTERN = Pattern.compile(BEARER_REGEX);

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = resolveToken(request);

        /*헤더에 값이 없다면 토큰 확인*/
        if (token == null) {
            Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
            if (cookies != null) {
                for (Cookie c : cookies) {
                    String name = c.getName(); // 쿠키 이름 가져오기

                    String value = c.getValue(); // 쿠키 값 가져오기
                    if (name.equals("Authorization")) {
                        token = value;
                    } else { // 쿠키에도 없다면
                        if (session != null) {
                            token = (String) session.getAttribute("Authorization");
                            if (token != null) {
                                token = token.split(" ")[1].trim(); // 앞에 'Bearer ' 붙인거 때문에 에러남.. https://stackoverflow.com/questions/65782480/illegal-base64url-character-when-getting-claims-decode-from-token-java-jwt

                            }
                        }

//                        if (token != null && tokenProvider.validateToken(token).isValid()) {
//                            Authentication authentication = tokenProvider.getAuthentication(token, null);
//                            SecurityContextHolder.getContext().setAuthentication(authentication);
//                        }
                    }
                }
            }
        }

        //비정상 토큰 처리
//        if (!StringUtils.hasText(token)) { //토큰 값이 존재하지 않는 경우
//            //request에 tokenValidationResult를 담아 JwtAuthenticationEntryPoint에서 예외 처리가 가능하게 한다.
//            handleMissingToken(request, response, filterChain);
//            return;
//        }

        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(token);
        if (!tokenValidationResult.isValid()) { //토큰이 유효하지 않는 경우
            handleWrongToken(request, response, filterChain, tokenValidationResult);
            return;
        }

        // blackList 검증
        if (tokenProvider.isAccessTokenBlackList(token)) {
            handleBlackListToken(request, response, filterChain);
            return;
        }

        //정상 토큰 처리
        handlerValidToken(token, tokenValidationResult);
        filterChain.doFilter(request, response);

    }

    private void handlerValidToken(String token, TokenValidationResult tokenValidationResult) {
        //사용자 인증을 처리하기 위해 securityContext에 authentication을 넣어줘야 한다.
        Authentication authentication = tokenProvider.getAuthentication(token, tokenValidationResult.getClaims());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("AUTH SUCCESS : {}", authentication.getName());
    }

    private static void handleWrongToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, TokenValidationResult tokenValidationResult) throws IOException, ServletException {
        request.setAttribute("result", tokenValidationResult);
        filterChain.doFilter(request, response);
    }

    private static void handleMissingToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.WRONG_AUTH_HEADER, null, null, null));
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); //Authorization header에서 value를 꺼내준다.

        if(bearerToken != null && BEARER_PATTERN.matcher(bearerToken).matches()) { //header에 토큰 값이 존재하고, 정규표현식으로 검사했을 때 토큰에 문제가 없는 경우
            return bearerToken.substring(7); //토큰 값에 bearer만 떼준 다음 return
        }

        return null;
    }

    private void handleBlackListToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.TOKEN_IS_BLACKLIST, null, null, null));
        filterChain.doFilter(request, response);
    }
}