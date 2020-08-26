中文 | [English](https://github.com/feng-haitao/auto-excel/blob/master/README.md) | [文档](http://www.fenghaitao.net/autoexcel-user-manual)

## 为什么使用AutoExcel？

Excel导入导出在软件开发中非常常见，只要你接触过开发，就一定会遇到。相信很多人会跟我一样选择用Apache POI来完成这项工作，在感受到POI功能强大的同时，我的团队也遇到了以下问题：

1. 直接使用POI操作Excel将产生大量硬编码，你会在编码中写死行索引和列索引
2. 大量不可复用的格式控制编码，如背景色、对齐方式、单元格样式等
3. 实施顾问明明提供了现成的模板，却还要开发用代码实现一遍，开发效率低下
4. 模板调整时不得不动用开发资源
5. 简单的导出也需要写特定的代码

**AutoExcel**解决了上述问题，它非常简单，只需要少量的代码即可完成复杂的导入导出；使用它时，程序员对导入导出无感，即不需要直接操作POI；与此同时，实施顾问提供的Excel即是导入导出模板，除非新增数据源或字段，否则模板更新不需要动用开发资源。

**AutoExcel**并没有对POI进行过重的封装，而是充分利用了Excel本身具有的特性——名称管理器，通过一些小技巧，将单元格与数据源产生映射，从而解耦程序员与POI，避免产生硬编码，让导入导出工作变得愉快而不再是枯燥乏味。

## 功能预览

| 导出前                                                       | 导出后                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/basic_object.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/basic_object_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/single_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/single_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/multi_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/multi_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/fill_data_to_the_right.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/08/fill_data_to_the_right_result.png) |

实现以上导出只需要编写以下少量代码（你需要额外的代码来准备数据源，例如从数据库中获取）

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

更多功能请前往[文档](http://www.fenghaitao.net/autoexcel-user-manual)
