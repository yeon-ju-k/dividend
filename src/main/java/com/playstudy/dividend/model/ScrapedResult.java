package com.playstudy.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {     // 스크랩한 결과를 주고 받는 클래스 (스크랩한 결과의 리턴값:아웃풋값)

    private Company company;    // 회사 (Company) 클래스의 정보

    private List<Dividend> dividends;  // 한 회사의 배당금 정보 (분기별로 배당금이 나오므로)

    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
