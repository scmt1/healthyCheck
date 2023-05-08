package com.scmt.core.utis;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
public class PoiExUtil
{

	/**
	 * 设置某些列的值只能sheet中某列输入预制的数据,显示下拉框.
	 *
	 * @param sheet     模板sheet页（需要设置下拉框的sheet）
	 * @param sheetName 隐藏的sheet页，用于存放下拉框的值 （下拉框值对应一列）
	 * @param lastRow   存放下拉框值的最后一行
	 * @param col       存放下拉框值的列名 "A"
	 * @param firstRow  添加下拉框对应开始行
	 * @param endRow    添加下拉框对应结束行
	 * @param firstCol  添加下拉框对应开始列
	 * @param endCol    添加下拉框对应结束列
	 * @return HSSFSheet 设置好的sheet.
	 */
	public static XSSFSheet setXSSFValidation(XSSFSheet sheet, String sheetName, int lastRow, String col, int firstRow, int endRow, int firstCol, int endCol)
	{
		//设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		String cell = "\"" + sheetName + "!$" + col + "$1:$" + col + "$" + lastRow + "\"";
		log.info("下拉框列：" + cell);
		// 这句话是关键 引用ShtDictionary 的单元格

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
		DataValidationConstraint constraint = dvHelper.createFormulaListConstraint("INDIRECT(" + cell + ")");
		DataValidation validation = dvHelper.createValidation(constraint, regions);
		sheet.addValidationData(validation);

		return sheet;
	}

	/**
	 * 往隐藏sheet里面添加数据
	 *
	 * @param dataList
	 * @param sheet
	 */
	public static void addDataToSheet(List<String> dataList, Sheet sheet)
	{

		for (int i = 0; i < dataList.size(); i++)
		{
			Row row = sheet.createRow(i);
			Cell cell = row.createCell(0);
			cell.setCellValue(dataList.get(i));
		}
	}

	/**
	 * 得到sheet页
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Sheet getSheet(MultipartFile file) throws IOException
	{
		/**
		 *
		 * 判断文件版本
		 */
		String fileName = file.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

		InputStream ins = file.getInputStream();

		Workbook wb = null;

		if (suffix.equals("xlsx") || suffix.equals("xlsm"))
		{

			wb = new XSSFWorkbook(ins);

		} else
		{
			wb = new HSSFWorkbook(ins);
		}
		/**
		 * 获取excel表单
		 */
		Sheet sheet = wb.getSheetAt(0);

		return sheet;
	}

	/**
	 * 获取单元格的值
	 *
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell)
	{

		if (cell == null)
		{
			return "";
		}
		return cell.toString();
	}

	/**
	 * 解析POI导入Excel中日期格式数据
	 *
	 * @param currentCell
	 * @return currentCellValue
	 */
	public static String importDate(Cell currentCell, DateFormat forMater)
	{
		String currentCellValue = "";
		String dataFormatString = currentCell.getCellStyle().getDataFormatString();
		// 判断单元格数据是否是日期
		if ("yyyy/mm;@".equals(dataFormatString) || "m/d/yy".equals(dataFormatString) || "yy/m/d".equals(dataFormatString) || "mm/dd/yy".equals(dataFormatString) || "dd-mmm-yy".equals(dataFormatString) || "yyyy/m/d".equals(dataFormatString) || "m/d/yy h:mm".equals(dataFormatString))
		{
			if (DateUtil.isCellDateFormatted(currentCell))
			{
				// 用于转化为日期格式
				Date d = currentCell.getDateCellValue();

				currentCellValue = forMater.format(d);
			}
		} else
		{
			// 不是日期原值返回
			currentCellValue = currentCell.toString();
		}
		return currentCellValue;
	}

	/**
	 * 设置文列样式和边框
	 *
	 * @param book
	 * @param sheet
	 */
	public static void setColumnStyleAndBorder(Workbook book, Sheet sheet,Integer colNum)
	{
		//设置列样式(文本)：
		CellStyle textStyle = book.createCellStyle();
		DataFormat format = book.createDataFormat();
		textStyle.setDataFormat(format.getFormat("@"));
		//边框
		textStyle.setBorderBottom(BorderStyle.THIN);
		textStyle.setBorderLeft(BorderStyle.THIN);
		textStyle.setBorderRight(BorderStyle.THIN);
		textStyle.setBorderTop(BorderStyle.THIN);

		sheet.setDefaultColumnStyle(colNum, textStyle);
	}

}
