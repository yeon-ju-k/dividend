package com.playstudy.dividend.web;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.persist.entity.CompanyEntity;
import com.playstudy.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")     // 경로에 공통되는 부분 (패키지 같은것)
@AllArgsConstructor     // Service 객체를 위해 작성!
public class CompanyController {


    // [객체] Service 클래스 객체
    private final CompanyService companyService;    // 해당 클래스의 모든값을 매개변수로하는 생성자를 만들어야하기 때문에 @AllArgs~ 애노테이션 작성 필수!

    // 회사명 자동완성 검색 기능
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String keyword) {
        List<String> autocomplete = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(autocomplete);
    }

    // 회사 리스트 전체 조회 기능 - 전체 회사 list 가 출력
    @GetMapping
    public Page<CompanyEntity> searchCompany(final Pageable pageable) {
        return companyService.getAllCompany(pageable);
    }

    /**
     * 회사 및 배당금 정보 추가
     */
    // 관리자 기능 - 회사 정보 + 배당금 정보 추가
    @PostMapping
    public ResponseEntity<Company> addCompany(@RequestBody Company request) {

        // 1) ticker 값 가져오기
        String ticker = request.getTicker().trim();

        // 2) ticker 값이 null 일 경우
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        // 3) DB에 저장하기
        Company companySave = this.companyService.save(ticker);

        // 4) 자동완성 기능 구현을 위한 회사명 저장
        this.companyService.addAutocompleteKeyword(companySave.getName());

        return ResponseEntity.ok(companySave);
    }

    // 관리자 기능 - 회사 정보 + 배당금 정보 삭제
    @DeleteMapping("/{ticker}")
    public void delCompany(@PathVariable String ticker) {

        // 자동완성 기능 구현을 위한 trie에 회사명 삭제
        //this.companyService.deleteAutocompleteKeyword(company);
    }

}
