package com.playstudy.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }

    @Bean   // 패스워드 암호화 Bean 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


