package com.playstudy.dividend.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "MEMBER")
public class MemberEntity implements UserDetails {  // spring boot security 라이브러리를 사용하기 위해 상속받기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // List 로 설정하는 이유? ) READWRITE : Read 와 Write 두개의 권한을 가질 수도 있기 때문
    private List<String> roles;


    @Override   // 권한 받기
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()      // 설정한 권한을
                .map(SimpleGrantedAuthority::new)   // spring-security 라이브러리에서 지원하는 authority 정보
                .collect(Collectors.toList());      // List 값으로 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}



