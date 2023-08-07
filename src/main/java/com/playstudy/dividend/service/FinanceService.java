package com.playstudy.dividend.service;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.Dividend;
import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.persist.entity.CompanyEntity;
import com.playstudy.dividend.persist.entity.DividendEntity;
import com.playstudy.dividend.repository.CompanyRepository;
import com.playstudy.dividend.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FinanceService {

    // [객체] Repository 객체 생성
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    // 회사명으로 배당금 정보 조회 메소드
    @Cacheable(key = "#companyName", value = "finance")
    public ScrapedResult getDividendByCompanyName(String companyName) {

        // +) 캐싱된 데이터를 가져오면 로그 출력
        log.info("search company -> " + companyName);

        // 1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                                            .orElseThrow( () -> new RuntimeException("존재하지 않는 회사명입니다. : " + companyName) );

        // 2. 조회된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());


        // 3. 결과 조합 후 변환
        Company companyResult = new Company(company.getTicker(), company.getName());
        List<Dividend> dividendResult = dividendEntities.stream()
                                                        .map( e -> new Dividend(e.getDate(), e.getDividend()))
                                                        .collect(Collectors.toList());


        return new ScrapedResult(companyResult, dividendResult);
    }

}

