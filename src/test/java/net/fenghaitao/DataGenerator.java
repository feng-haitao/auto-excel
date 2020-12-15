package net.fenghaitao;

import java.util.Date;
import net.fenghaitao.model.BusinessUnit;
import net.fenghaitao.model.Contract;
import net.fenghaitao.model.Product;
import net.fenghaitao.model.Project;
import net.fenghaitao.parameters.FieldSetting;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static BusinessUnit genBusinessUnit() throws ParseException {
        BusinessUnit businessUnit = new BusinessUnit("Zhongshan FHT Co., LTD", "ZS", "www.fenghaitao.com",
                "Zhongshan city, Guangdong Province", new Date(), "冯海涛");
        return businessUnit;
    }

    public static List<Contract> genContracts() throws ParseException {
        List<Contract> contracts = new ArrayList<>();
        contracts.add(new Contract("C0000-0001", "Expected contract", format.parse("2020-08-14 17:32"), new BigDecimal("28500.00"), new BigDecimal("20000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0002", "Expected non-contract", format.parse("2019-08-14 07:32"), new BigDecimal("1028500.00"), new BigDecimal("28500"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0003", "Expected contract", format.parse("2018-10-01 00:00"), new BigDecimal("2000000.00"), new BigDecimal("2000000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0004", "Expected non-contract", format.parse("2020-01-14 19:32"), new BigDecimal("10031500.00"), new BigDecimal("1000000"), new BigDecimal("0")));
        contracts.add(new Contract("C0000-0005", "Expected contract", format.parse("2020-07-15 18:10"), new BigDecimal("28500.00"), new BigDecimal("0"), new BigDecimal("0")));
        return contracts;
    }

    public static List<Project> genProjects(int times) throws ParseException {
        List<Project> projects = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < times; ++i) {
            projects.add(new Project("Project Name 01", "Some text here. Some text here. 1", 109600.00, 91280.00, 175140.00f, 20, dateFormat.parse("2019-01-13"), dateFormat.parse("2010-03-13"), new BigDecimal("17485.91"), new BigDecimal("3412200000.00")));
            projects.add(new Project("Project Name 02", "Some text here. Some text here. 2", 169328.12, 0.00, 300241.92f, 25, dateFormat.parse("2018-11-12"), dateFormat.parse("2009-01-30"), new BigDecimal("537.74"), new BigDecimal("195150000.00")));
            projects.add(new Project("Project Name 03", "Some text here. Some text here. 3", 3000.00, 3000.00, 3500.00f, 30, dateFormat.parse("2019-5-6"), dateFormat.parse("2009-01-23"), new BigDecimal("11142.86"), new BigDecimal("39000000.00")));
            projects.add(new Project("Project Name 04", "Some text here. Some text here. 4", 1400.00, 950.00, 480.00f, 28, dateFormat.parse("2017-2-10"), dateFormat.parse("2007-11-05"), new BigDecimal("1437.50"), new BigDecimal("690000.00")));
            projects.add(new Project("Project Name 05", "Some text here. Some text here. 5", 136230.00, 1130.00, 260.00f, 15, dateFormat.parse("2020-1-6"), dateFormat.parse("2010-04-23"), new BigDecimal("43242.00"), new BigDecimal("11242920.00")));

        }
        return projects;
    }

    public static List<Product> genProducts(int times) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < times; ++i) {
            products.add(new Product("Product Name 01", 43029.03, 64192.05, new BigDecimal("71655.89"), 30));
            products.add(new Product("Product Name 02", 43029.03, 64192.05, new BigDecimal("71645.89"), 22));
            products.add(new Product("Product Name 03", 0.00, 0.00, new BigDecimal("111.00"), 20));
            products.add(new Product("Product Name 04", 21023.00, 40006.00, new BigDecimal("74235.00"), 18));
            products.add(new Product("Product Name 05", 500.00, 500.00, new BigDecimal("950.00"), 15));
        }
        return products;
    }

    public static List<FieldSetting> genProjectFieldSettings() {
        List<FieldSetting> fieldSettings = new ArrayList<>();
        fieldSettings.add(new FieldSetting("projName", "Project Name"));
        fieldSettings.add(new FieldSetting("projInfo", "Project Info."));
        fieldSettings.add(new FieldSetting("basalArea", "Basal Area"));
        fieldSettings.add(new FieldSetting("availableArea", "Available Area"));
        fieldSettings.add(new FieldSetting("buildingArea", "Building Area"));
        fieldSettings.add(new FieldSetting("buildingsNumber", "Buildings Number"));
        fieldSettings.add(new FieldSetting("saleStartDate", "Sales Start Date"));
        fieldSettings.add(new FieldSetting("landAcquisitionTime", "Land Acquisition Time"));
        fieldSettings.add(new FieldSetting("availablePrice", "Available Price"));
        fieldSettings.add(new FieldSetting("availableAmount", "Available Amount"));
        fieldSettings.add(new FieldSetting("insideArea", "Inside Area"));
        return fieldSettings;
    }

    public static List<FieldSetting> genProductFieldSettings() {
        List<FieldSetting> fieldSettings = new ArrayList<FieldSetting>() {{
            add(new FieldSetting("projName", "Project Name"));
            add(new FieldSetting("basalArea", "Basal Area"));
            add(new FieldSetting("availableArea", "Available Area"));
            add(new FieldSetting("buildingArea", "Building Area"));
            add(new FieldSetting("buildingsNumber", "Buildings Number"));
        }};
        return fieldSettings;
    }
}
