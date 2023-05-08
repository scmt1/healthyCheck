package com.scmt.healthy.utils;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liubingcheng
 */
@Component
public class ExcelUtil {

    /**
     * @Title: createExcelTemplate
     * @Description: 生成Excel导入模板
     * @param @param filePath  Excel文件路径
     * @param @param handers   Excel列标题(数组)
     * @param @param downData  下拉框数据(数组)
     * @param @param downRows  下拉列的序号(数组,序号从0开始)
     * @return void
     * @throws
     */
    public static void createExcelTemplate(String filePath, String[] title, List<String[]> downData, String[] downRows){
        HSSFWorkbook wb = new HSSFWorkbook();//创建工作薄
        //表头样式
        HSSFCellStyle style = wb.createCellStyle();
        // 创建一个居中格式
        // style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //字体样式
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontName("微软雅黑");
        fontStyle.setFontHeightInPoints((short)12);
        // fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(fontStyle);
        style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // 新建sheet
        HSSFSheet sheet1 = wb.createSheet("人员信息表");
        HSSFSheet sheet2 = wb.createSheet("在岗状态");
        HSSFSheet sheet3 = wb.createSheet("工种名称");
        HSSFSheet sheet4 = wb.createSheet("危害因素");

        /*生成sheet1内容*/
        // 第一个sheet的第一行为标题
        HSSFRow rowFirst = sheet1.createRow(0);
        // 写标题
        for(int i=0;i<title.length;i++){
            // 获取第一行的每个单元格
            HSSFCell cell = rowFirst.createCell(i);
            // 设置每列的列宽
            sheet1.setColumnWidth(i, 4000);
            //加样式
            cell.setCellStyle(style);
            // 往单元格里写数据
            cell.setCellValue(title[i]);
        }

        // 设置下拉框数据
        String[] arr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int index = 0;
        HSSFRow row = null;
        for(int r=0;r<downRows.length;r++){
            // 获取下拉对象
            String[] dlData = downData.get(r);
            int rownum = Integer.parseInt(downRows[r]);
            // 255以内的下拉
            if(dlData.length<5){
                // 255以内的下拉,参数分别是：作用的sheet、下拉内容数组、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidation(sheet1, dlData, 1, 500, rownum ,rownum)); //超过255个报错
            } else {
                // 255以上的下拉，即下拉列表元素很多的情况
                // 1、设置有效性
                // Sheet2第A1到A5000作为下拉列表来源数据
                // String strFormula = "Sheet2!$A$1:$A$5000" ;
                // Sheet2第A1到A5000作为下拉列表来源数据
                String strFormula = "Sheet2!$"+arr[index]+"$1:$"+arr[index]+"$5000";
                // 设置每列的列宽
                sheet2.setColumnWidth(r, 4000);
                // 设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
                //下拉列表元素很多的情况
                sheet1.addValidationData(SetDataValidation(strFormula, 1, 50000, rownum, rownum));

                //2、生成sheet2内容
                for(int j=0;j<dlData.length;j++){
                    if(index==0){ //第1个下拉选项，直接创建行、列
                        // 创建数据行
                        row = sheet2.createRow(j);
                        // 设置每列的列宽
                        sheet2.setColumnWidth(j, 4000);
                        // 设置对应单元格的值
                        row.createCell(0).setCellValue(dlData[j]);

                    } else { //非第1个下拉选项

                        int rowCount = sheet2.getLastRowNum();
                        //System.out.println("========== LastRowNum =========" + rowCount);
                        // 前面创建过的行，直接获取行，创建列
                        if(j<=rowCount){
                            // 获取行，创建列
                            // 设置对应单元格的值
                            sheet2.getRow(j).createCell(index).setCellValue(dlData[j]);

                        } else { //未创建过的行，直接创建行、创建列
                            // 设置每列的列宽
                            sheet2.setColumnWidth(j, 4000);
                            // 创建行、创建列
                            // 设置对应单元格的值
                            sheet2.createRow(j).createCell(index).setCellValue(dlData[j]);
                        }
                    }
                }
                index++;
            }
        }


        /*生成sheet2内容*/
        List<String[]> strings = new ArrayList<>();
//        String[] Sheet2title0 = {"上岗前","","男","0","未婚","0"};
//        strings.add(Sheet2title0);
        strings.add(new String[]{"上岗前","","男","0","未婚","0"});
        strings.add(new String[]{"上岗前","","男","0","未婚","0"});
        strings.add(new String[]{"上岗前","","男","0","未婚","0"});
        strings.add(new String[]{"在岗期间","","女","1","已婚","1"});
        strings.add(new String[]{"离岗时","","性别","性别id","离异","2"});
        strings.add(new String[]{"离岗后","","","","丧偶","3"});
        strings.add(new String[]{"应急健康检查","","","","其他","4"});
        sheetDataSet(strings,sheet2,style);


        /*生成sheet3内容*/




        /*生成sheet4内容*/



        try {

            File f = new File(filePath); //写文件

            //不存在则新增
            if(!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            if(!f.exists()){
                f.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(f);
            out.flush();
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title: SetDataValidation
     * @Description: 下拉列表元素很多的情况 (255以上的下拉)
     * @param @param strFormula
     * @param @param firstRow   起始行
     * @param @param endRow     终止行
     * @param @param firstCol   起始列
     * @param @param endCol     终止列
     * @param @return
     * @return HSSFDataValidation
     * @throws
     */
    private static HSSFDataValidation SetDataValidation(String strFormula, int firstRow, int endRow, int firstCol, int endCol) {
        // 设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(regions,constraint);

        dataValidation.createErrorBox("Error", "Error");
        dataValidation.createPromptBox("", null);
        return dataValidation;
    }

    /**
     *
     * @Title: setDataValidation
     * @Description: 下拉列表元素不多的情况(255以内的下拉)
     * @param @param sheet
     * @param @param textList
     * @param @param firstRow
     * @param @param endRow
     * @param @param firstCol
     * @param @param endCol
     * @param @return
     * @return DataValidation
     * @throws
     */
    private static DataValidation setDataValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {

        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        //DVConstraint constraint = new DVConstraint();
        constraint.setExplicitListValues(textList);

        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow,firstCol, endCol);
        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        //DataValidation data_validation = new DataValidation(regions, constraint);
        return data_validation;
    }

    /**
     * @Title: delFile
     * @Description: 删除文件
     * @param @param filePath  文件路径
     * @return void
     * @throws
     */
    public static void delFile(String filePath) {
        java.io.File delFile = new java.io.File(filePath);
        delFile.delete();
    }

    /**
     * 处理sheet的内容(设置数据集)
     */
    public static void sheetDataSet(List<String[]> strings,HSSFSheet sheet, HSSFCellStyle style) {
        if(strings!=null && strings.size()>0){
            for(int i = 0;i < strings.size();i ++){
                HSSFRow row = sheet.createRow(i);
                String[] title = strings.get(i);
                sheetRowset(sheet,row,title,style);
            }
        }
    }

    /**
     * 处理sheet的内容(创建单行数据)
     */
    public static void sheetRowset(HSSFSheet sheet,HSSFRow row,String[] title,HSSFCellStyle style) {
        for(int i=0;i<title.length;i++){
            // 获取第一行的每个单元格
            HSSFCell cell = row.createCell(i);
            // 设置每列的列宽
            sheet.setColumnWidth(i, 4000);
            //加样式
            cell.setCellStyle(style);
            // 往单元格里写数据
            cell.setCellValue(title[i]);
        }
    }
}