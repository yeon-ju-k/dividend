package com.playstudy.dividend.scraper;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.Dividend;
import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.model.constants.Month;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component  // 빈 등록
public class YahooFinanceScraper implements Scraper {

    // # 1. [변수] 스크래핑할 url 을 변수로 빼주기
    //   ㄴ (URL 에 검색할 부분에 %s(ticker 값) + %d(조회시작날짜, 끝날짜) 변수지정)
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";

    // # 1. [변수] 조회하려는 시작시간, 끝시간
    private static final long START_TIME = 86400;   // (= 0시0분) 60(초) * 60(분) * 24(시간) = 하루의 시간(초단위)

    // # 2. [변수] ticker 명으로 회사 정보를 찾는 url 설정
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s";

    // # 1. [메소드] ticker 명으로 배당금 스크랩기능
    @Override
    public ScrapedResult scrap(Company company) {

        // ScrapResult 인스턴스 생성 (리턴값)
        ScrapedResult scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);    // 전달받은 회사정보 저장

        // 스크래핑 동작
        try {

            // 0. URL 의 변수값 지정 ( 조회 시작시간 / 끝 시간 / ticker값 )
            long start = START_TIME;
            long end = System.currentTimeMillis() / 1000;   // 현재시간을 ms(밀리세컨드)로 받아서 / 1000으로 나눠서 => 초단위로 변경
            String url = String.format(STATISTICS_URL, company.getTicker(), start, end);

            // 1. Jsoup 라이브러리로 url 연결
            Connection connection = Jsoup.connect(url);

            // 2. 연결된 url 의 데이터를 Document 타입으로 저장
            Document document = connection.get();

            // 3. 스크래핑 할 데이터의 key, value 값을 찾아서 해당 데이터 가져오기
            // 		ㄴ ex ) key : "data-test" / value : "historical-prices"
            //  ㄴ 결과값 저장 ) Elements or Element (해당 value 값이 list 형식으로 여러데이터를 가지고 있기 때문에 = Elements)
            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");

            // 4. 저장한 결과값에서 0번째 인덱스 값 가져오기
            Element tableEle = parsingDivs.get(0);    // table 전체 가져오기

            // 5. tbody의 데이터만 가져오기
            Element tbody = tableEle.children().get(1);    // 0:thead, 1:tbody, 2:tfoot 의 정보

            List<Dividend> dividendList = new ArrayList<>();   // 배당금 결과값을 출력하는 리스트

            for ( Element e :tbody.children() ) {    // tbody의 모든데이터를 순회하면서 배당금 데이터만 가져오기
                String txt = e.text();

                // 해당 데이터가 Dividend으로 끝나는 데이터만 저장
                if (!txt.endsWith("Dividend")) {
                    continue;
                } else {    // 배당금 데이터 저장

                    // 6. 월, 일, 년, 배당금 퍼센트를 각각 나눠서 저장하기
                    String[] splits = txt.split(" ");    // 공백을 기준으로 나누기
                    int month = Month.strToNumber(splits[0]);   // month의 string값을 int형태로 변환
                        // ㄴ Month의 객체를 생성하지않고 바로 사용할 수 있는 이유? ) Month의 해당 메소드가 static이기 때문에!
                    int day = Integer.parseInt(splits[1].replace(",", ""));    // 콤마 없애기
                    int year = Integer.parseInt(splits[2]);
                    String dividend = splits[3];

                    // month값을 잘못 반환한 경우 (month값이 -1일 때) -> 오류 발생시키기
                    if (month < 0) {
                        throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                    }

                    // 위의 변수를 원하는 형식으로 저장하기 (배당금 (Dividend) 정보 저장)
                    dividendList.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));

                }

                // 리턴값인 ScrapedResult 클래스타입으로 저장
                scrapResult.setDividends(dividendList);
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        return scrapResult;
    }

    // # 2. [메소드] ticker 명으로 회사 정보를 스크랩하기
    @Override
    public Company scrapCompanyByTicker(String ticker) {

        // 리턴값 생성
        Company company;

        try {

            // 0. url 변수 지정
            String url = String.format(SUMMARY_URL, ticker);

            // 1. 스크래핑
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            Element parsingCompany = document.getElementsByTag("h1").get(0);
            String comName = parsingCompany.text().split(" - " )[1].trim();

            // 회사이름, ticker 저장
            company = new Company(ticker, comName);   // build

            return company;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

