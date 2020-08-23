package net.fenghaitao.model;

import java.util.Date;

public class BusinessUnit {
    private String buName;
    private String buCode;
    private String webSite;
    private String companyAddr;
    private Date createdOn;
    private String createdBy;

    public BusinessUnit(String buName, String buCode, String webSite, String companyAddr, Date createdOn, String createdBy) {
        this.buName = buName;
        this.buCode = buCode;
        this.webSite = webSite;
        this.companyAddr = companyAddr;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public String getBuCode() {
        return buCode;
    }

    public void setBuCode(String buCode) {
        this.buCode = buCode;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
