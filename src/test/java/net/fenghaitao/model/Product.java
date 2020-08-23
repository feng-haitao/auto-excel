package net.fenghaitao.model;

import java.math.BigDecimal;

public class Product {
    private String productName;
    private BigDecimal basalArea;
    private BigDecimal availableArea;
    private BigDecimal buildingArea;
    private BigDecimal insideArea;

    public Product(String productName, BigDecimal basalArea, BigDecimal availableArea, BigDecimal buildingArea, BigDecimal insideArea) {
        this.productName = productName;
        this.basalArea = basalArea;
        this.availableArea = availableArea;
        this.buildingArea = buildingArea;
        this.insideArea = insideArea;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getBasalArea() {
        return basalArea;
    }

    public void setBasalArea(BigDecimal basalArea) {
        this.basalArea = basalArea;
    }

    public BigDecimal getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(BigDecimal availableArea) {
        this.availableArea = availableArea;
    }

    public BigDecimal getBuildingArea() {
        return buildingArea;
    }

    public void setBuildingArea(BigDecimal buildingArea) {
        this.buildingArea = buildingArea;
    }

    public BigDecimal getInsideArea() {
        return insideArea;
    }

    public void setInsideArea(BigDecimal insideArea) {
        this.insideArea = insideArea;
    }
}
