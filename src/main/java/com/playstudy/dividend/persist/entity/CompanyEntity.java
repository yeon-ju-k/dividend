package com.playstudy.dividend.persist.entity;

import com.playstudy.dividend.model.Company;
import lombok.*;

import javax.persistence.*;

@Entity(name="COMPANY")
@Getter
@Setter
@ToString   // 해당 인스턴스를 출력하기 위한 toString 기능을 오버라이드해서 생성함!
@NoArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에 맡김 (auto-increment)
    private Long id;

    @Column(unique = true)  // 해당 컬럼의 중복 X (불가)
    private String ticker;

    private String name;

    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }

}
