package com.playstudy.dividend.repository;

import com.playstudy.dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

    // 회사 id 값으로 배당금 데이터 출력
    List<DividendEntity> findAllByCompanyId(Long companyId);

    // 해당 배당금 정보가 있는지 확인하는 메소드 (회사 id , 배당금데이터 날짜)
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime dateTime);
        // ㄴ 두 컬럼 (회사 id, 배당금지급 날짜) 을 unique key 로 설정해놨기 때문에

}


