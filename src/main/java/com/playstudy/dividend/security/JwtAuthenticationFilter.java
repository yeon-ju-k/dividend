package com.playstudy.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor    // final 키워드가 붙은 필드의 생성자 자동 생성
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 어떤 키를 주고받을 지에 대한 키값
    public static final String TOKEN_HEADER = "Authorization";  // 토큰의 header
    public static final String TOKEN_PREFIX = "Bearer ";    // 인증타입 (ex. JWT토큰을 사용하는 경우 : 토큰 앞에 Bearer를 붙임)

    // 토큰 유효성 검사 클래스 인스턴스 생성
    private final TokenProvider tokenProvider;

    @Override   // 요청이 들어올 때마다 해당 메소드가 실행됨!
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1) header 추출 (리턴값 : 토큰)
        String token = this.resolveTokenFromRequest(request);

        // 2) 토큰이 있는지 여부확인 & 유효성 검증
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {    // 토큰 유효성 검증 완료
            // 토큰 정보를 context에 담기
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }


    // Request로 부터 토큰의 header를 추출
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER); // header값 추출

        // 토큰이 있고, 토큰이 "Bearer " 로 시작할 때 (=정상 토큰인지는 아직 알 수 없지만 형태는 갖춘 토큰)
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());  // TOKEN_PREFIX 이후의 값 (실제 토큰값) 만 출력
        }

        return null;
    }


}
