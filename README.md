[中文](https://github.com/feng-haitao/auto-excel/blob/master/README.zh.md) | English | [Documentation](https://github.com/feng-haitao/auto-excel/wiki)

## Why AutoExcel?

Excel import and export is very common in software development, as long as you are a programmer, you have met. I believe that many people will choose to use Apache POI to complete this work like me. While feeling the power of POI, my team also encountered the following problems:

1. Directly use POI to operate Excel will generate a lot of hard code, you will hardly write row index and column index in the code.
2. A large number of non-reusable format control codes, such as background color, alignment, cell style, etc.
3. The implementation consultant clearly provided a ready-made template, but had to develop the code to implement it again, resulting in low development efficiency.
4. Development resources have to be used when the template is adjusted.
5. Simple export also requires specific code.

**AutoExcel** solves the above problems. It is very simple and only requires a small amount of code to complete complex import and export. When using it, programmers have no sense of import and export, that is, there is no need to directly manipulate POI. At the same time, the implementation consultant provides Excel is the import and export template, unless new data sources or fields are added, the template update does not need to use development resources.

**AutoExcel** does not over-encapsulate the POI, but makes full use of Excel's own feature-the name manager, through some tricks, the cell and the data source are mapped, thereby decoupling the programmer and the POI, and avoid hard code, so that import and export work becomes enjoyable and no longer boring.

## Features

- Export with template
  - Support basic object and table data
  - A single sheet supports multiple data sources of variable length
  - Support horizontal filling of data
  - Automatically apply cell style
  - Auto fill in line number
  - Auto fill formula
  - Automatic summary
- Export directly
  - Export with basic style
  - Automatically adjust column width
- Import
  - Automatic data type conversion
- Support millions of data import and export in seconds

## Function preview

| Before export                                                | After export                                                 |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/basic_object.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/basic_object_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/single_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/single_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/multi_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/multi_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/fill_data_to_the_right.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/fill_data_to_the_right_result.png) |

To achieve the above export, you only need to write the following small amount of code (you need additional code to prepare the data source, for example, from the database)

```java
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
```

For more functions, please go to [Documentation](https://github.com/feng-haitao/auto-excel/wiki).
