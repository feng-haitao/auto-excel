package net.fenghaitao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    private String contractCode;
    private String contractProperties;
    private Date signDate;
    private BigDecimal contractAmount;
    private BigDecimal settlementAmount;
    private BigDecimal sumPayAmount;
}
