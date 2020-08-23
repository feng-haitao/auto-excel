package net.fenghaitao.model;

import java.math.BigDecimal;

public class Project {
    private String projName;
    private BigDecimal basalArea;
    private BigDecimal buildingArea;
    private BigDecimal insideArea;
    private BigDecimal availableArea;
    private BigDecimal availablePrice;
    private BigDecimal availableAmount;

    public Project(String projName, BigDecimal basalArea, BigDecimal buildingArea, BigDecimal insideArea, BigDecimal saleArea, BigDecimal availablePrice, BigDecimal availableAmount) {
        this.projName = projName;
        this.basalArea = basalArea;
        this.buildingArea = buildingArea;
        this.insideArea = insideArea;
        this.availableArea = saleArea;
        this.availablePrice = availablePrice;
        this.availableAmount = availableAmount;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public BigDecimal getBasalArea() {
        return basalArea;
    }

    public void setBasalArea(BigDecimal basalArea) {
        this.basalArea = basalArea;
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

    public BigDecimal getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(BigDecimal availableArea) {
        this.availableArea = availableArea;
    }

    public BigDecimal getAvailablePrice() {
        return availablePrice;
    }

    public void setAvailablePrice(BigDecimal availablePrice) {
        this.availablePrice = availablePrice;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }
}
