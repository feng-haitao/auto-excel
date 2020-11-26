package net.fenghaitao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnit {
    private String buName;
    private String buCode;
    private String webSite;
    private String companyAddr;
    private Date createdOn;
    private String createdBy;
}
