package net.fenghaitao;

import net.fenghaitao.model.BusinessUnit;
import net.fenghaitao.model.Contract;
import net.fenghaitao.model.Product;
import net.fenghaitao.model.Project;
import net.fenghaitao.parameters.FieldSetting;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static BusinessUnit genBusinessUnit() throws ParseException {
        BusinessUnit businessUnit = new BusinessUnit("Zhongshan FHT Co., LTD", "ZS", "www.fenghaitao.com",
                "Zhongshan city, Guangdong Province", format.parse("2020-08-14 17:32"), "冯海涛");
        return businessUnit;
    }

    public static List<Contract> genContracts() throws ParseException {
        List<Contract> contracts = new ArrayList<>();
        contracts.add(new Contract("C0000-0001","Expected contract", format.parse("2020-08-14 17:32"), new BigDecimal("28500.00"), new BigDecimal("20000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0002","Expected non-contract", format.parse("2019-08-14 07:32"), new BigDecimal("1028500.00"), new BigDecimal("28500"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0003","Expected contract", format.parse("2018-10-01 00:00"), new BigDecimal("2000000.00"), new BigDecimal("2000000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0004","Expected non-contract", format.parse("2020-01-14 19:32"), new BigDecimal("10031500.00"), new BigDecimal("1000000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0005","Expected contract", format.parse("2020-07-15 18:10"), new BigDecimal("28500.00"), new BigDecimal("0"), new BigDecimal("0")));
        return contracts;
    }

    public static List<Project> genProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("Project Name 01", new BigDecimal("109600.00"), new BigDecimal("175140.00"), new BigDecimal("219200.00"), new BigDecimal("91280.00"), new BigDecimal("17485.91"), new BigDecimal("3412200000.00")));
        projects.add(new Project("Project Name 02", new BigDecimal("169328.12"), new BigDecimal("300241.92"), new BigDecimal("369786.28"), new BigDecimal("0.00"), new BigDecimal("537.74"), new BigDecimal("195150000.00")));
        projects.add(new Project("Project Name 03", new BigDecimal("3000.00"), new BigDecimal("3500.00"), new BigDecimal("4000.00"), new BigDecimal("3000.00"), new BigDecimal("11142.86"), new BigDecimal("39000000.00")));
        projects.add(new Project("Project Name 04", new BigDecimal("1400.00"), new BigDecimal("480.00"), new BigDecimal("2010.00"), new BigDecimal("950.00"), new BigDecimal("1437.50"), new BigDecimal("690000.00")));
        projects.add(new Project("Project Name 05", new BigDecimal("136230.00"), new BigDecimal("260.00"), new BigDecimal("610.00"), new BigDecimal("1130.00"), new BigDecimal("43242.00"), new BigDecimal("11242920.00")));
        return projects;
    }

    public static List<Product> genProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Product Name 01", new BigDecimal("43029.03"), new BigDecimal("64192.05"), new BigDecimal("71655.89"), new BigDecimal("71645.89")));
        products.add(new Product("Product Name 02", new BigDecimal("43029.03"), new BigDecimal("64192.05"), new BigDecimal("71645.89"), new BigDecimal("71645.89")));
        products.add(new Product("Product Name 03", new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("111.00"), new BigDecimal("0.00")));
        products.add(new Product("Product Name 04", new BigDecimal("21023.00"), new BigDecimal("40006.00"), new BigDecimal("74235.00"), new BigDecimal("10332.00")));
        products.add(new Product("Product Name 05", new BigDecimal("500.00"), new BigDecimal("500.00"), new BigDecimal("950.00"), new BigDecimal("400.00")));
        return products;
    }

    public static List<FieldSetting> genProjectFieldSettings() {
        List<FieldSetting> fieldSettings = new ArrayList<>();
        fieldSettings.add(new FieldSetting("projName", "Project Name"));
        fieldSettings.add(new FieldSetting("basalArea", "Basal Area"));
        fieldSettings.add(new FieldSetting("buildingArea", "Building Area"));
        fieldSettings.add(new FieldSetting("insideArea", "Inside Area"));
        fieldSettings.add(new FieldSetting("availableArea", "Available Area"));
        fieldSettings.add(new FieldSetting("availablePrice", "Available Price"));
        fieldSettings.add(new FieldSetting("availableAmount", "Available Amount"));
        return fieldSettings;
    }
}
