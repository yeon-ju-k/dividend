package com.playstudy.dividend.repository;

import com.playstudy.dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    // ticker 명으로 데이터 존재 여부 확인
    boolean existsByTicker(String ticker);


    // 회사명으로 데이터 조회
    Optional<CompanyEntity> findByName(String Name);

    // Like 자동완성 기능 - 회사으로 조회
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable limit);

    // ticker 명으로 회사 데이터 조회
    Optional<CompanyEntity> findByTicker(String ticker);

}


