package com.playstudy.dividend.scraper;

import com.playstudy.dividend.model.Company;
import com.playstudy.dividend.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);

}
