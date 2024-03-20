package net.fenghaitao;

import net.fenghaitao.enums.DataDirection;
import net.fenghaitao.imports.DataSet;
import net.fenghaitao.model.Product;
import net.fenghaitao.model.Project;
import net.fenghaitao.parameters.DirectExportPara;
import net.fenghaitao.parameters.ExcelSetting;
import net.fenghaitao.parameters.ImportPara;
import net.fenghaitao.parameters.TemplateExportPara;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AutoExcelTest {
    /**
     * Comprehensive example of template export
     *
     * @throws ParseException
     */
    @Test
    public void exportWithTemplate() throws ParseException {
        // Set export parameters, such as data source name, data source, etc.
        List<TemplateExportPara> paras = new ArrayList<>();
        paras.add(new TemplateExportPara("BusinessUnit", DataGenerator.genBusinessUnit()));
        paras.add(new TemplateExportPara("Contract", DataGenerator.genContracts()));
        paras.add(new TemplateExportPara("Project", DataGenerator.genProjects(1)));

        List<Product> products = DataGenerator.genProducts(1);
        TemplateExportPara para3 = new TemplateExportPara("Product", products);
        // When a single sheet has multiple data sources, the data source above should be set to inserted
        para3.setInserted(true);
        paras.add(para3);

        TemplateExportPara para5 = new TemplateExportPara("Product2", products);
        // Horizontal fill
        para5.setDataDirection(DataDirection.Right);
        paras.add(para5);

        // (Optional operation) Remove unnecessary sheets
        ExcelSetting excelSetting = new ExcelSetting();
        excelSetting.setRemovedSheets(Arrays.asList("will be removed"));

        AutoExcel.save(this.getClass().getResource("/template/Export.xlsx").getPath(),
                this.getClass().getResource("/").getPath() + "AutoExcel.xlsx",
                paras,
                excelSetting);
    }

    /**
     * Export two sheets directly
     *
     * @throws ParseException
     */
    @Test
    public void exportDirectly() throws ParseException {
        String outputPath = this.getClass().getResource("/").getPath() + "Export Directly.xlsx";
        List<DirectExportPara> paras = new ArrayList<>();
        paras.add(new DirectExportPara(DataGenerator.genProjects(200), "Projects",
                DataGenerator.genProjectFieldSettings()));
        paras.add(new DirectExportPara(DataGenerator.genSingleContracts()));
        paras.add(new DirectExportPara(DataGenerator.genListPorjects(10),"lists",
                DataGenerator.genListFieldSettings()));
        AutoExcel.save(outputPath, paras);
    }

    @Test
    public void importExcel() {
        List<ImportPara> importParas = new ArrayList<ImportPara>() {{
            add(new ImportPara(0, DataGenerator.genProductFieldSettings()));
            add(new ImportPara(1, DataGenerator.genProjectFieldSettings(), 1, 5));
        }};
        String fileName = this.getClass().getResource("/template/Import.xlsx").getPath();
        DataSet dataSet = AutoExcel.read(fileName, importParas);
        // Method 1: Obtain the original data without type conversion, you can
        //           check whether the data meets the requirements in this way
        List<Map<String, Object>> products = dataSet.get("Product");
        List<Map<String, Object>> projects = dataSet.get("Project");
        // Method 2: Obtain the data of the specified class through the sheet index, the type is
        //           automatically converted, and an exception will be thrown if the conversion fails
        // List<Product> products = dataSet.get(0, Product.class);
        // List<Project> projects= dataSet.get(1, Project.class);
        // Method 3: Obtain the data of the specified class through the sheet name, the type is
        //           automatically converted, and an exception will be thrown if the conversion fails
        // List<Product> products = dataSet.get("Product", Product.class);
        // List<Project> projects = dataSet.get("Project", Project.class);
    }
}
