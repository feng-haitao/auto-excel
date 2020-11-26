package net.fenghaitao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productName;
    private double basalArea;
    private Double availableArea;
    private BigDecimal buildingArea;
    private int buildingsNumber;
}
