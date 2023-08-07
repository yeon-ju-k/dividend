package com.playstudy.dividend.scheduler;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.persist.entity.CompanyEntity;
import com.playstudy.dividend.persist.entity.DividendEntity;
import com.playstudy.dividend.repository.CompanyRepository;
import com.playstudy.dividend.repository.DividendRepository;
import com.playstudy.dividend.scraper.Scraper;
import com.playstudy.dividend.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j  // lombok 라이브러리의 로깅 기능 (= 스케줄링이 정상적으로 실행되는지 확인하기 위해)
@Component
@AllArgsConstructor
@EnableCaching  // 캐싱 기능 활성화
public class ScraperScheduler {

    // [객체] Repository 객체 생성
    private final CompanyRepository companyRepository;  // 저장된 회사 목록 조회 기능을 사용하기 위해
    private final DividendRepository dividendRepository;    // 배당금 데이터가 있는지 확인하기 위해

    // [객체] 야후 파이낸스 스크래핑을 하는 객체 생성
    private final Scraper yahooFinanceScraper;


    // 일정 주기마다 배당금 정보 가져오기 수행
    @CacheEvict(value = "finance", allEntries = true)   // Redis 캐시에 있는 finance의 데이터는 모두 비우기
    @Scheduled(cron = "${scheduler.scrap.yahoo}")    // 매일 0시에 실행
    public void yahooFinanceScheduling() {

        // 1) 스케줄러(해당 메소드)가 실행될 때 마다 로그남기기
        log.info("scraping scheduler is started");

        // 2) 저장된 모든 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 3) 회사마다 배당금 정보를 새로 스크래핑
        for (CompanyEntity company : companies) {   // 전체 회사목록을 순회
            log.info("scraping scheduler is started -> " + company.getName());  // 회사목록 로그
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(new Company(company.getName(), company.getTicker()));


            // 4) 스크래핑 한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // Dividend 모델을 DividendEntity 로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리멘트를 하나씩 Dividend 리포지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());

                        if (!exists) {  // 해당 배당금 정보가 DB에 없을 경우 -> 저장
                            this.dividendRepository.save(e);
                        }

                    });

            // 5) 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지 (하나의 for문을 돌때 마다)
            // ㄴ 대상 사이트에 부화를 일으킬 수 있기 때문에!
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();    // 출력만 하기 때문에 적절한 예외처리가 아님!
                Thread.currentThread().interrupt();     // 현재스레드의 인터럽트(스레드멈춤)를 거는걸로 예외처리하기!
            }


        }

    }
}