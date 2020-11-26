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
        List<TemplateExportPara> paras = new ArrayList<>();

        List<Project> projects = DataGenerator.genProjects(200);
        paras.add(new TemplateExportPara("BusinessUnit", DataGenerator.genBusinessUnit()));
        paras.add(new TemplateExportPara("Contract", DataGenerator.genContracts()));
        paras.add(new TemplateExportPara("Project", projects));

        List<Product> products = DataGenerator.genProducts(2);
        TemplateExportPara para3 = new TemplateExportPara("Product", products);
        para3.setInserted(true);
        paras.add(para3);

        TemplateExportPara para5 = new TemplateExportPara("Product2", products);
        para5.setDataDirection(DataDirection.Right);
        paras.add(para5);

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
        paras.add(new DirectExportPara(DataGenerator.genContracts()));
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

        List<Map<String, Object>> products = dataSet.get("Product");
        List<Map<String, Object>> projects = dataSet.get("Project");
//        or:
//        List<Product> products = dataSet.get(0, Product.class);
//        List<Project> projects= dataSet.get(1, Project.class);
//        or:
//        List<Product> products = dataSet.get("Product", Product.class);
//        List<Project> projects = dataSet.get("Project", Project.class);
    }
}
