package com.playstudy.dividend.web;

import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")     // 공통 경로
@AllArgsConstructor
public class FinanceController {

    // [객체] Service 객체 생성
    private final FinanceService financeService;

    // 회사명으로 배당금 정보 조회 - 검색된 회사의 주식정보가 출력
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {

        // service 클래스의 해당 기능 호출
        ScrapedResult result = financeService.getDividendByCompanyName(companyName);

        return ResponseEntity.ok(result);
    }

}
