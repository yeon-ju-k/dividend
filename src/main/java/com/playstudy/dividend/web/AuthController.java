package com.playstudy.dividend.web;

import com.playstudy.dividend.model.Auth;
import com.playstudy.dividend.model.MemberEntity;
import com.playstudy.dividend.security.TokenProvider;
import com.playstudy.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")     // 회원가입 API
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {

        // 회원 가입을 위한 API
        MemberEntity result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/signin")     // 로그인 API
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {

        // 로그인 검증 기능 호출 -> MemberEntity 데이터 가져오기
        MemberEntity member = this.memberService.authenticate(request);

        // 가져온 회원정보로 토큰 연결 
        String token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());

        return ResponseEntity.ok(token);
    }

}
