package utility.ExcelUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import databaseConnection.MySqlConnection;
import utility.CustomUtility;
import com.monitorjbl.xlsx.StreamingReader;

public class ExcelFileUpload {
	private static Workbook workbook = null;
	private static List<String> firstRow = null;
	private static List<Object[]> excelDataList = null;
	private static CustomUtility utility = null;
	private static boolean splitFlag = false;
	private static String fileDelimiter = null;
	private static String fileName = null;
	public static Connection conn = null;

	static Logger log = Logger.getLogger(ExcelFileUpload.class);

	@SuppressWarnings("unused")
	public static List<String> sheetFirstRow(Sheet dataSheet) {
		firstRow = new ArrayList<String>();
		if (dataSheet != null) {
			String dataSheetName = dataSheet.getSheetName();
			log.info("datasheetName   " + dataSheetName);
			for (Row row : dataSheet) {
				ArrayList<String> dataList = new ArrayList<String>();
				for (Cell cell : row) {
					if (cell != null) {
						DataFormatter formatter = new DataFormatter();
						String cellValue = formatter.formatCellValue(cell);
						if (splitFlag)
							firstRow.add(cellValue);
						else
							firstRow.add("[" + cellValue + "]");

					}
				}
				break;
			}
		}
		if (splitFlag)
			firstRow = convertDataToColumnData(firstRow);
		// log.info("Data list {} "+ firstRow);
		return firstRow;
	}

	public static List<String> convertDataToColumnData(List<String> firstRow2) {
		String[] rowArr = null;
		for (String rowData : firstRow2) {
			if (StringUtils.isNoneBlank(rowData)) {
				if (rowData.startsWith(fileDelimiter))
					rowData = rowData.substring(1, rowData.length());
				rowArr = rowData.split(fileDelimiter);
			}
		}
		// log.info("Column Header Data Array {} "+ Arrays.toString(rowArr));
		List al = Arrays.asList(rowArr);
		// log.info("Column Header Data list {} "+ al);
		return al;
		// return (ArrayList<String>) al;
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	public static Object getCellValue(Cell cell) {

		DataFormatter dataFormatter = new DataFormatter();

		Object cellStringValue = null;
		// System.out.println(" cellStringValue "+cellStringValue);

		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				cellStringValue = dataFormatter.formatCellValue(cell);
				break;
			case Cell.CELL_TYPE_NUMERIC:
				cellStringValue = dataFormatter.formatCellValue(cell);
				break;
			case Cell.CELL_TYPE_STRING:
				cellStringValue = dataFormatter.formatCellValue(cell);
				break;
			case Cell.CELL_TYPE_BLANK:
				break;
			case Cell.CELL_TYPE_ERROR:
				cellStringValue = null;// cell.getErrorCellValue();
				break;

			// CELL_TYPE_FORMULA will never occur
			case Cell.CELL_TYPE_FORMULA:
				break;
			}
		}
		return cellStringValue;
	}

	public static void uploadSheetData(Sheet dataSheet, int fetchSize) {
		excelDataList = new ArrayList<>();
		excelDataList.clear();
		int exceptionRowIndex = 0;
		List<String> columnList = sheetFirstRow(dataSheet);
		log.info("column list of sheet " + columnList);
		String sheetName = dataSheet.getSheetName();
		for (Row row : dataSheet) {
			List<String> dataList = new ArrayList<>();
			if (row.getRowNum() > 0) {
				for (Cell cell : row) {
					String cellValue = (String) getCellValue(cell);
					// System.out.println(row.getRowNum() + cell.getColumnIndex() +" "+cellValue);
					dataList.add(cellValue);

				}
				System.out.println(dataList);
				if (splitFlag)
					dataList = convertDataToColumnData(dataList);

				Object[] dataVal = dataList.toArray();
				excelDataList.add(dataVal);

				if (excelDataList != null && excelDataList.size() == fetchSize) {
					String insertQuery = getExcelQuery(sheetName, columnList);
					try {
						log.info("File " + fileName + "Number of insertion list " + excelDataList.size());
						System.out.println("File " + fileName + "Number of insertion list " + excelDataList.size());
						CustomUtility.executeUpdateBatch(insertQuery, excelDataList, conn);
						excelDataList.clear();
						exceptionRowIndex = exceptionRowIndex + fetchSize;

					} catch (BatchUpdateException e) {
						log.error(" Exception {} ", e);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						log.error(" Exception {} ", e);
						// break;
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						log.error(" Exception {} ", e);
					}
				}
			}

		}

		System.out.println("No processed ");
		if (excelDataList != null && excelDataList.size() > 0) {
			String insertQuery = getExcelQuery(sheetName, columnList);
			try {
				CustomUtility.executeUpdateBatch(insertQuery, excelDataList, conn);
			} catch (BatchUpdateException e) {
				log.info("Batch update exception occured maintained logs for excel in system");
				System.out.println("Batch update exception occured maintained logs for excel in system");
				// ExcelUploadLogger.excelUploadLogger(e,exceptionRowIndex);
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception occured in system {}" + e);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(" Exception {} " + e);
			}

		}
	}

	public static String getExcelQuery(String sheetName, List<String> columnList) {
		System.out.println(columnList.size());
		String insertQuery = "INSERT INTO [" + sheetName + "] ( ";
		String fieldList = String.join(",", columnList);
		String getNumberofParam = CustomUtility.getNumberOfInput(columnList);

		insertQuery = insertQuery + fieldList + " ) VALUES ( " + getNumberofParam + " )";

		log.info("insertQuery " + insertQuery);
		return insertQuery;

	}

	public static void excelExceutor(String filePath, int fetchSize) {
		FileInputStream inp = null;
		try {
			inp = new FileInputStream(filePath);
			if (inp != null) {
				Workbook wb = getWorkbook(inp, fetchSize);
				getSheets(wb, fetchSize);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
	}

	public static void sheetExceutor(Sheet sheet, int fetchSize) {
		if (sheet != null) {
			uploadSheetData(sheet, fetchSize);

		}

	}

	public static void getSheets(Workbook wb, int fetchSize) {
		if (wb != null) {
			int numberSheet = wb.getNumberOfSheets();
			System.out.println("Total Number of sheet in excel " + fileName + " file " + numberSheet);
			for (int i = 0; i < numberSheet; i++) {
				Sheet dataSheet = wb.getSheetAt(i);
				log.info("Currently in sheet number " + i + " name " + dataSheet.getSheetName()
						+ " and calling sheet Executor");
				System.out.println("Currently in sheet number " + i + " name " + dataSheet.getSheetName()
						+ " and calling sheet Executor");
				sheetExceutor(dataSheet, fetchSize);
			}
		}

	}

	public static Workbook getWorkbook(InputStream inp, int fetchSize) {
		workbook = StreamingReader.builder().rowCacheSize(fetchSize).bufferSize(128).open(inp);
		return workbook;

	}

	public static void main(String args[]) {
		splitFlag = false;
		fileDelimiter = "~";
		int lineItem = 7000;
		MySqlConnection mysql = new MySqlConnection();
		try {
			conn = mysql.getConnection();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connection Id " + conn);
		fileName = "PD_HERSMATERIAL_05022021_2.xlsx";
		long startTime = System.currentTimeMillis();
		String systemPath = System.getenv("JAVA_HOME");
		systemPath = "D:\\Local D\\MasterDataOnline";
		String filePath = systemPath + File.separator + "DataLoad" + File.separator + fileName;
		excelExceutor(filePath, lineItem);
		long endTime = System.currentTimeMillis();
		log.info("Total time taken " + (endTime - startTime) + " milliseconds");
		System.out.println("Total time taken " + fileName + "  " + (endTime - startTime) + " milliseconds");
		log.info("Total time taken " + ((endTime - startTime) / 1000) + " sec");
	}
}
