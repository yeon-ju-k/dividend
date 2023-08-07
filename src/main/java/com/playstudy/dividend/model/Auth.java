package com.playstudy.dividend.model;

import lombok.Data;

import java.util.List;

public class Auth {

    @Data
    public static class SignIn {    // 로그인할 때 필요한 model
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {    // 회원가입에 필요한 model
        private String username;
        private String password;
        private List<String> roles;     // ex. 관리자 계정, 일반 계정의 권한을 해당 계정에 부여

        public MemberEntity toEntity() {    // 회원가입한 정보를 MemberEntity 타입으로 변경

            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

}
