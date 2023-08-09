package com.playstudy.dividend.security;

import com.playstudy.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    // 상수값 설정 (변하지 않는 상수값일 경우 변수를 생성해서 넣어주는것이 좋음!)
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;   // = 1시간 (1000ms 에 초,분을 곱해서)


    // 토큰 인증정보 추출
    private final MemberService memberService;

    @Value("{spring.jwt.secret}")   // application.properties 에 설정한 비밀키값 가져오기
    private String secretKey;

    /**
     * 토큰 생성(발급) 메소드
     * @param username
     * @param roles
     * @return
     */
    // JWT 토큰 생성 메소드
    public String generateToken(String username, List<String> roles) {

        // 1) 사용자의 권한 정보 저장
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES,roles);  // Claims 데이터 저장방식 : key(=명칭)-value(값)

        // 2) 토큰이 생성된 시간
        Date now = new Date();  // 현재시간 넣기

        // 3) 토큰 만료 시간
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);     // 1시간후 만료

        // 4) 토큰에 1~3의 정보를 넣기
        return Jwts.builder()
                .setClaims(claims)              // 1번값 : 사용자의 권한 정보
                .setIssuedAt(now)               // 2번값 : 토큰 생성 시간
                .setExpiration(expiredDate)     // 3번값 : 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey)    // 사용할 암호화 알고리즘, 비밀키
                .compact();     // 문자열형태의 JWT토큰으로 반환하는 기능

    }


    // 토큰에서 id가져오기
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }


    // 토큰의 유효기간 만료여부 확인 (만료 : false / 사용가능 : true)
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;  // 토큰의 값이 빈값이면 유효하지 않음

        Claims claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());   // 만료시간이 현재시간보다 이전이면 false 리턴
    }


    // 토큰이 유효한지 확인하는 기능 (토큰으로부터 Claims의 정보(= 권한정보)를 가져옴)
    //      ㄴ 외부에서 사용하지않기 때문에 private로 설정
    private Claims parseClaims(String token) {

        // 토큰에서 Claims 정보 파싱
        //  ㄴ 토큰 만료기간이 지난 토큰을 파싱하려고하면 ExpiredException이 발생할 수 있음 -> 예외처리!
        try {
            return Jwts.parser().setSigningKey(this.secretKey)  // 해당 비밀키로 파싱
                    .parseClaimsJws(token)  // 토큰에서 서명을 검증하고, Claims 정보 파싱
                    .getBody();     // 파싱된 JWS토큰에서 클레임정보 가져오기
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰으로부터 인증 정보를 가져오는 기능
    public Authentication getAuthentication(String jwt) {
        // 토큰에서 username을 가져와서 -> 해당 데이터 찾기 
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));

        // username , 비밀번호, 권한정보 순의 토큰 정보
        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
    }


}
