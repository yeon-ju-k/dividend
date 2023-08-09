package com.playstudy.dividend.service;

import com.playstudy.dividend.exception.impl.NoCompanyException;
import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.Dividend;
import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.persist.entity.CompanyEntity;
import com.playstudy.dividend.persist.entity.DividendEntity;
import com.playstudy.dividend.repository.CompanyRepository;
import com.playstudy.dividend.repository.DividendRepository;
import com.playstudy.dividend.scraper.Scraper;
import com.playstudy.dividend.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {


    // [객체] Trie 라이브러리 객체 생성
    private final Trie trie;    // 스트링부트의 빈으로 등록하기 때문에 싱글톤으로 관리됨

    // [객체] 스크래핑 클래스 객체 생성
    private final Scraper scraper;  // 스크래핑 클래스에 @Component 애노테이션이 작성되어있는 클래스(=빈 등록되어있는) 를 가져옴

    // [객체] Repository 클래스 객체 생성
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    // # 2. [public:외부에서 호출] DB에 해당 회사의 정보가 없으면 스크래핑해서 저장 후 출력 , 있으면 오류 출력
    public Company save(String ticker) {

        // 1) DB에 회사정보가 있는지 조회
        boolean exists = companyRepository.existsByTicker(ticker);
        if (exists) {  // DB에 회사정보가 이미 있을 경우
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        // DB에 회사정보가 없을 경우
        Company company = storeCompanyAndDividend(ticker);

        return company;
    }


    // # 1. [private:내부에서만 호출]스크래핑하는 기능 (스크래핑 클래스 호출)
    private Company storeCompanyAndDividend(String ticker) {

        // 1. ticker 를 기준으로 회사를 스크래핑
        Company company = this.scraper.scrapCompanyByTicker(ticker);    // ticker 명으로 회사이름 찾기
        if (ObjectUtils.isEmpty(company)) {     // null 값이면 회사정보가 없거나 잘못된 ticker 정보임!
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 2. 해당 회사가 존재할 경우 (검색이 될 경우) -> 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.scraper.scrap(company);

        // 3. 스크래핑 결과를 COMPANY 테이블에 저장 (DB에 저장)
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
            // ㄴ CompanyEntity 에 Company 타입을 매개변수로 받는 생성자를 만들어야함!


        // 4. 스크래핑 결과를 Dividend 테이블에 저장 (DB에 저장)
        //      ㄴ Dividend 테이블에는 : 해당 회사의 정보가 저장되어있는 company 테이블의 id 값을 저장해야함
        List<Dividend> dividendList = scrapedResult.getDividends();    // 배당금 정보를 리스트로 저장

        for ( Dividend a : dividendList ) {     // 배당금정보를 DividendEntity 에 저장
            DividendEntity dividendEntity = new DividendEntity(); // DividendEntity 생성 및 초기화 (반복마다 새로운 객체 생성)
            dividendEntity.setCompanyId(companyEntity.getId());
            dividendEntity.setDividend(a.getDividend());
            dividendEntity.setDate(a.getDate());
            this.dividendRepository.save(dividendEntity); // DIVIDEND 테이블에 저장
        }

        return company;
    }


    // 3. 회사 리스트 전체 조회 기능
    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);     // Page의 엔티티로 결과가 출력됨
    }


    // 4. 자동완성 Trie에 회사명 저장
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);   // key-value 형태로 값을 넣을 수 있음
    }


    // 5. 자동완성 Trie에서 회사명 조회 (자동완성 기능)
    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());     // 리스트 형태로 반환
    }

    // 6. 자동완성 Trie에 저장되어있는 회사명 삭제
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    // 7. Like 쿼리를 활용한 회사명 자동완성 기능
    public List<String> getCompanyNamesByKeyword(String keyword) {

        // 페이징 처리
        Pageable limit = PageRequest.of(0, 10);  // 한번에 10개씩만 가져오기
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                    .map( e -> e.getName() )    // CompanyEntity타입에서 회사명가져와서
                    .collect(Collectors.toList());  // list로 출력
    }

    // 8. 회사 + 배당금 정보 삭제 -> 리턴값 회사명
    public String deleteCompany(String ticker) {

        // 1) ticker 명으로 회사데이터 찾기
        CompanyEntity company = this.companyRepository.findByTicker(ticker)
                .orElseThrow( () -> new NoCompanyException());

        // 2) 찾은 데이터 삭제하기 (company + dividend 정보 모두 삭제)
        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        // 3) 자동완성기능을 위한 trie에서도 회사정보 삭제
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }


}


