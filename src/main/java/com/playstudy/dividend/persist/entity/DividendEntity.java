package com.playstudy.dividend.persist.entity;

import com.playstudy.dividend.model.Dividend;
import com.playstudy.dividend.model.ScrapedResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "DIVIDEND")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(  // 유니크키 설정 (중복을 막기 위해)
    uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = { "companyId", "date" }
            )
    }
)
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private LocalDateTime date;
    private String dividend;

    public DividendEntity(Long companyId, Dividend dividend) {
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}

