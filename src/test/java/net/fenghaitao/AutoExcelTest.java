package net.fenghaitao;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import net.fenghaitao.model.Product;
import net.fenghaitao.parameters.DirectExportPara;
import net.fenghaitao.parameters.FieldSetting;
import net.fenghaitao.parameters.ImportPara;
import net.fenghaitao.parameters.TemplateExportPara;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class AutoExcelTest {
    @Test
    public void exportWithTemplate() throws Exception {
        List<TemplateExportPara> paras = new ArrayList<>();

        paras.add(new TemplateExportPara("BusinessUnit", DataGenerator.genBusinessUnit()));
        paras.add(new TemplateExportPara("Contract", DataGenerator.genContracts()));
        paras.add(new TemplateExportPara("Project", DataGenerator.genProjects()));

        List<Product> products = DataGenerator.genProducts();
        TemplateExportPara para3 = new TemplateExportPara("Product", products);
        para3.setInserted(true);
        paras.add(para3);

        TemplateExportPara para5 = new TemplateExportPara("Product2", products);
        para5.setDataDirection(DataDirection.Right);
        paras.add(para5);

        ExcelSetting excelSetting = new ExcelSetting();
        excelSetting.setRemovedSheets(Arrays.asList("will be removed"));

        AutoExcel.save(this.getClass().getResource("/template/Common.xlsx").getPath(),
                this.getClass().getResource("/").getPath() + "ExportWithTemplate.xlsx",
                paras,
                excelSetting);
    }

    @Test
    public void exportDirectly() throws Exception {
        String outputPath = this.getClass().getResource("/").getPath() + "Export.xlsx";
        List<DirectExportPara> paras = new ArrayList<>();
        paras.add(new DirectExportPara(DataGenerator.genProjects(), "Projects", DataGenerator.genProjectFieldSettings()));
        paras.add(new DirectExportPara(DataGenerator.genContracts()));
        AutoExcel.save(outputPath, paras);
    }

    @Test
    public void importExcel() throws Exception {
        List<ImportPara> importParas = new ArrayList<ImportPara>() {{
            add(new ImportPara("BusinessUnit"));
            add(new ImportPara("Contract", DataDirection.Down));
            add(new ImportPara("Project", DataDirection.Down));
//            add(new ImportPara("Product", DataDirection.Down));   not supported now
        }};
        String fileName = this.getClass().getResource("/").getPath() + "ExportWithTemplate.xlsx";
        HashMap<String, List<HashMap<String, Object>>> datas = AutoExcel.read(fileName, importParas);
    }
}
