package com.playstudy.dividend.service;

import com.playstudy.dividend.exception.impl.AlreadyExistUserException;
import com.playstudy.dividend.model.Auth;
import com.playstudy.dividend.model.MemberEntity;
import com.playstudy.dividend.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j  // log
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {


    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  // 패스워드 암호화

    @Override   // 회원 id 찾기
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    // 회원 가입 기능
    public MemberEntity register(Auth.SignUp member) {

        // id 중복 여부 확인
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());

        if (exists) {   // 이미 존재하는 id 일 경우 -> 오류
            throw new AlreadyExistUserException();
        }

        // 없는 id 일 경우 -> 회원가입
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));  // 패스워드 인코딩 (암호화)
        // Auth클래스의 SignUp클래스의 toEntity 메소드를 활용 -> MemberEntity 타입으로 변경 -> DB에 저장
        MemberEntity result = this.memberRepository.save(member.toEntity());

        return result;
    }

    // 로그인 검증
    public MemberEntity authenticate(Auth.SignIn member) {

        // 1) id값으로 데이터 가져오기 (없으면 -> 오류발생)
        MemberEntity user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        // 2) 가져온 데이터의 비밀번호와 같은지 확인
        //      ㄴ 매개변수로 받은 member의 password는 암호화하지않은 문자열이므로 암호화를 한 뒤 비교!
        if ( !this.passwordEncoder.matches(member.getPassword(), user.getPassword()) ) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

}


