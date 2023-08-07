package com.playstudy.dividend;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.ScrapedResult;
import com.playstudy.dividend.scraper.Scraper;
import com.playstudy.dividend.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

@SpringBootApplication
@EnableScheduling
@EnableCaching	// 캐싱 어노테이션
public class DividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendApplication.class, args);




	}
}
