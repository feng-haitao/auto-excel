中文 | [English](https://github.com/feng-haitao/auto-excel/blob/master/README.md) | [文档](https://juejin.cn/post/6903170257574166536)

## 为什么使用AutoExcel？

Excel导入导出在软件开发中非常常见，只要你接触过开发，就一定会遇到。相信很多人会跟我一样选择用Apache POI来完成这项工作，在感受到POI功能强大的同时，我的团队也遇到了以下问题：

1. 直接使用POI操作Excel将产生大量硬编码，你会在编码中写死行索引和列索引
2. 大量不可复用的格式控制编码，如背景色、对齐方式、单元格样式等
3. 实施顾问明明提供了现成的模板，却还要开发用代码实现一遍，开发效率低下
4. 模板调整时不得不动用开发资源
5. 简单的导出也需要写特定的代码

**AutoExcel**解决了上述问题，它非常简单，只需要少量的代码即可完成复杂的导入导出；使用它时，程序员对导入导出无感，即不需要直接操作POI；与此同时，实施顾问提供的Excel即是导入导出模板，除非新增数据源或字段，否则模板更新不需要动用开发资源。

**AutoExcel**并没有对POI进行过重的封装，而是充分利用了Excel本身具有的特性——名称管理器，通过一些小技巧，将单元格与数据源产生映射，从而解耦程序员与POI，避免产生硬编码，让导入导出工作变得愉快而不再是枯燥乏味。

## 特点

- 模板导出
  - 支持多个sheet
  - 支持基础对象和表格数据
  - 单个sheet支持多个不定长数据源
  - 支持横向填充数据
  - 自动应用单元格样式
  - 自动填充行号
  - 自动填充公式
  - 自动合计
- 直接导出
  - 支持多个sheet
  - 导出带基本样式
  - 自动列宽
- 导入
  - 支持多个sheet
  - 数据类型自动转换
- 支持百万数据秒级导入导出

## 功能预览

| 导出前                                                       | 导出后                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/basic_object.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/basic_object_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/single_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/single_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/multi_table.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/multi_table_result.png) |
| ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/fill_data_to_the_right.png) | ![image](http://www.fenghaitao.net/wp-content/uploads/2020/12/fill_data_to_the_right_result.png) |

实现以上所有导出只需要编写以下少量代码（你需要额外的代码来准备数据源，例如从数据库中获取。示例中使用DataGenerator生成demo数据）

```java
// 设置导出参数，如数据源名称、数据源等
List<TemplateExportPara> paras = new ArrayList<>();
paras.add(new TemplateExportPara("BusinessUnit", DataGenerator.genBusinessUnit()));
paras.add(new TemplateExportPara("Contract", DataGenerator.genContracts()));
paras.add(new TemplateExportPara("Project", DataGenerator.genProjects(1)));

List<Product> products = DataGenerator.genProducts(1);
TemplateExportPara para3 = new TemplateExportPara("Product", products);
// 单个sheet有多个数据源时，上方数据源应设置为插入
para3.setInserted(true);
paras.add(para3);

TemplateExportPara para5 = new TemplateExportPara("Product2", products);
// 横向填充
para5.setDataDirection(DataDirection.Right);
paras.add(para5);

//（可选操作）移除不需要的sheet
ExcelSetting excelSetting = new ExcelSetting();
excelSetting.setRemovedSheets(Arrays.asList("will be removed"));

AutoExcel.save(this.getClass().getResource("/template/Export.xlsx").getPath(),
               this.getClass().getResource("/").getPath() + "AutoExcel.xlsx",
               paras,
               excelSetting);
```

## 百万数据耗时测试

单位：毫秒
|           | 10W行10列数据 | 100W行10列数据 |
| --------- | ------------- | -------------- |
| 模板导出  | 6,258         | 23,540         |
| 直接导出  | 5,711         | 24,952         |
| 导入      | 4,466         | 21,595         |
| 导入+转换 | 4,823         | 26,279         |

## Maven
```xml
<dependency>
  <groupId>net.fenghaitao</groupId>
  <artifactId>auto-excel</artifactId>
  <version>2.0.0</version>
</dependency>
```

更多功能请前往[文档](https://juejin.cn/post/6903170257574166536)
