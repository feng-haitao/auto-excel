package net.fenghaitao.model;

import java.math.BigDecimal;
import java.util.Date;

public class Contract {
    private String contractCode;
    private String contractProperties;
    private Date signDate;
    private BigDecimal contractAmount;
    private BigDecimal settlementAmount;
    private BigDecimal sumPayAmount;

    public Contract(String contractCode, String contractProperties, Date signDate, BigDecimal contractAmount, BigDecimal settlementAmount, BigDecimal sumPayAmount) {
        this.contractCode = contractCode;
        this.contractProperties = contractProperties;
        this.signDate = signDate;
        this.contractAmount = contractAmount;
        this.settlementAmount = settlementAmount;
        this.sumPayAmount = sumPayAmount;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractProperties() {
        return contractProperties;
    }

    public void setContractProperties(String contractProperties) {
        this.contractProperties = contractProperties;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public BigDecimal getSumPayAmount() {
        return sumPayAmount;
    }

    public void setSumPayAmount(BigDecimal sumPayAmount) {
        this.sumPayAmount = sumPayAmount;
    }
}
