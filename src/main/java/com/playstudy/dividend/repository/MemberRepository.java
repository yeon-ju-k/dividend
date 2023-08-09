package com.playstudy.dividend.repository;

import com.playstudy.dividend.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // id값으로 회원정보 조회
    Optional<MemberEntity> findByUsername(String username);

    // id값 중복 존재유무 (회원가입할 때 id값 중복확인 기능)
    boolean existsByUsername(String username);

}


