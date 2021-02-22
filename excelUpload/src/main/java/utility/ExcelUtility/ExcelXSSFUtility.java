package utility.ExcelUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import utility.CustomUtility;

public class ExcelXSSFUtility {

	public static SXSSFWorkbook workbook;
	public static SXSSFSheet sheet;
	public static String filePath;

	public static Sheet createSheet(String sheetName) {
		sheet = workbook.createSheet(sheetName);
		sheet.setRandomAccessWindowSize(100);
		return sheet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static SXSSFSheet writeSheetData(Collection<Map> data) {
		System.out.println("Sheet data");
		int noRows = 0;
		if (sheet != null) {
			noRows = sheet.getLastRowNum();
		}
		System.out.println("No of Rows " + noRows);
		if (noRows <= 0) {
			writeHeaderData(data);
		}
		noRows++;
		System.out.println("No of Rows " + noRows);
		for (Map map : data) {

			int noColumn = 0;
			SXSSFRow createRow = sheet.createRow(noRows);
			Set<String> keySet = map.keySet();
			for (String keyVal : keySet) {
				SXSSFCell createCell = createRow.createCell(noColumn);
				createCell.setCellValue((String) map.get(keyVal));
				noColumn++;
			}
			noRows++;

		}
		return sheet;

	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private static void writeHeaderData(Collection<Map> data) {
		SXSSFRow createRow = sheet.createRow(0);
		for (Map map : data) {
			int noColumn = 0;
			Set<String> keySet = map.keySet();
			for (String keyVal : keySet) {
				SXSSFCell createCell = createRow.createCell(noColumn);
				createCell.setCellValue(keyVal);
				noColumn++;

			}
			break;
		}

	}

	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		long startTime = System.currentTimeMillis();
		filePath = System.getenv("JAVA_HOME") + "" + "text.xlsx";

		System.out.println("filePath  " + filePath);
		workbook = new SXSSFWorkbook();
		sheet = (SXSSFSheet) createSheet("testData");
		writeSheetData(getData());

		try {
			FileOutputStream file = new FileOutputStream(new File(filePath));
			workbook.write(file);
		} catch (Exception ex) {

		}

		long endTime = System.currentTimeMillis();
		System.out.println("LOGGER for tie taken to complete " + (endTime - startTime) + " ms");
	}

	@SuppressWarnings("rawtypes")
	public static Collection<Map> getData() throws ClassNotFoundException, SQLException {
		String selQuery = "SELECT TOP 10000 * FROM DYN_0001_1005";
		CustomUtility utility = new CustomUtility();
		Collection<Map> resultMap = utility.executeQuery(selQuery, new ArrayList<String>());
		return resultMap;

	}

}
