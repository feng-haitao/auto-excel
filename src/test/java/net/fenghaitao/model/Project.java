package net.fenghaitao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private String projName;

    private String projInfo;

    private double basalArea;

    private Double availableArea;

    private Float buildingArea;

    private int buildingsNumber;

    private Date saleStartDate;

    private Date landAcquisitionTime;

    private BigDecimal availablePrice;

    private BigDecimal availableAmount;

}
