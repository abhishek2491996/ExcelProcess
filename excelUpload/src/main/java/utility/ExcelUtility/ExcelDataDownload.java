package utility.ExcelUtility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utility.CustomUtility;

public class ExcelDataDownload {

	public static XSSFSheet excelSheet;
	public static XSSFWorkbook workbook ;
	
	static Logger log = Logger.getLogger(ExcelDataDownload.class);

	public static void main(String args[]){
		
		CustomUtility utility = null;
		try {
			utility = new CustomUtility();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<Map> dataMap =utility.executeQuery("SELECT objectnumber,ABCINDIC,ACQDATE,ACQUISVAL,ACTUAL_SUPFLOC,ALTERNATE_NO_CLFMAS,ASSET_NO,ATTACH_MAT_FLOC,BUS_AREA,CATEGORY,CLASS_VIEW_FUNC_LOC,CLEAN_STAT,COM1,COM2,COM3,COM4,CONSTMONTH,CONSTTYPE,CONSTYEAR,COSTCENTER,COUNTR_ISO,CSTCENREM,CURR_ISO,DELETE_FLAG,DEMO_FIELD,DIFF_LOC_SUPFL,E_UNIT_ISO,EDITMASK,EQ_EQUIPMENTCAT,EQINSTALL,EQSINGLE,EQUIP_DESC_FLOC,EQUIP_FLOC_COMPCODE,EQUIP_FLOC_MAT,EQUIP_STATUS_FLOC,EVENT_FLOC,F_AUTHGRP,F_CATPROFILE,F_COMP_CODE,F_CURRENCY,F_DESC,F_DISTR_CHAN,F_DIVISION,F_EDCCODE,F_EQMT,F_H_DESC,F_HEADER_ACT,F_INHERITANCEGRD,F_INVENTORY,F_NOTETYPE,F_NUM,F_PART,F_PLANT,F_REPORT,F_REQMTS,F_SALES_GRP,F_SALES_OFF,F_SALES_ORG,F_USR_STATUS FROM DYN_234_1008", new ArrayList<String>(),0,10);
		System.out.println(dataMap.size());
		dataWriteInSheet();
	}
	
	public static XSSFSheet getSheetObject(XSSFWorkbook workbook, String sheetName){
		excelSheet = workbook.createSheet(sheetName);
		return excelSheet;
	}
	
	public static void dataWriteInSheet(){
		LinkedList<LinkedList<String>> rowData= new LinkedList<LinkedList<String>>();
		 LinkedList<String> dummyData=new LinkedList<String>();
		dummyData.add("A");
		dummyData.add("B");
		dummyData.add("C");
		dummyData.add("D");
		dummyData.add("E");
		dummyData.add("F");
		 LinkedList<String> dummyData1=new LinkedList<String>();
		 dummyData1.add("A");
		 dummyData1.add("B");
		 dummyData1.add("C");
		 dummyData1.add("D");
		 dummyData1.add("E");
		 dummyData1.add("F");
		 rowData.add(dummyData);
		 rowData.add(dummyData1);
		 
		 createWorkBook();
		 getSheetObject(workbook,"ANC");
		 excelSheetDataDwnld(excelSheet,rowData);
	}
	
	public static XSSFWorkbook createWorkBook(){
		workbook =new XSSFWorkbook();
		return workbook;
	}
	
	public static void excelSheetDataDwnld(XSSFSheet sheet,LinkedList<LinkedList<String>> sheetData){
		int rowCount=0;
		 int columnCount = 0;
		for(LinkedList<String> rowDataList : sheetData)
		{
			XSSFRow excelRow=sheet.createRow(++rowCount);
			log.info(rowDataList);
			for(String columnData : rowDataList){
				  columnCount = 0;
				  XSSFCell excelColumn = excelRow.createCell(++columnCount);
				  excelColumn.setCellValue(columnData);
				log.info("columnData  "+columnData);
			}
		}
		
		log.info("rowCount  "+rowCount);
		log.info("columnCount  "+columnCount);
	}
}
