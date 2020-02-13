package com.thed.zapi.cloud.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelManager {

	XSSFWorkbook workbook;
	XSSFSheet Sheet;
	XSSFRow row;
	XSSFCell cell;
	HSSFWorkbook hworkbook;
	HSSFSheet hSheet;
	HSSFRow hrow;
	HSSFCell hcell;

	FileInputStream fis;
	int iIndex;

	/**
	 * Purpose- Constructor to pass Excel file name
	 * 
	 * @param sFileName
	 */

	public ExcelManager() {
	}

	public ExcelManager(String sFilePath) {
		try {
			File file = new File(sFilePath);
			System.setProperty("ExcelFilePath", sFilePath);
			if (file.exists()) {
				if (isXLSX()) {
					fis = new FileInputStream(sFilePath);
					workbook = new XSSFWorkbook(fis);
					fis.close();
				} else {
					fis = new FileInputStream(sFilePath);
					hworkbook = new HSSFWorkbook(fis);
					fis.close();
				}
			} else {
				/*
				 * log.error("File -'" + sFilePath +
				 * "' does not exist in Data Folder, Please Re-check given file"
				 * );
				 */
				Assert.fail("File -'"
						+ sFilePath
						+ "' does not exist in Data Folder, Please Re-check given file");
			}

		} catch (Exception e) {
			/*
			 * log.error("Exception occurred while accessing file - " +
			 * sFilePath + "\n" + e.getCause());
			 */

			Assert.fail("Exception occurred while accessing file - "
					+ sFilePath + "\n" + e.getCause());
		}
	}

	/**
	 * Purpose- To check if the sheet with given name exists or not
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @return - if sheet with specified name exists it returns true else it
	 *         returns false
	 * @throws Exception
	 */
	public boolean isSheetExist(String sheetName) throws Exception {
		if (isXLSX())
			iIndex = workbook.getSheetIndex(sheetName);
		else
			iIndex = hworkbook.getSheetIndex(sheetName);
		if (iIndex == -1)

		{
			/*
			 * Assert.fail("Sheet with name-'" + sheetName +
			 * "' doesn't exists in '" + app.getProperty("Excel.File") +
			 * "' excel file");
			 */
			return false;
		} else
			return true;
	}

	public boolean isStringNull(String stringvalue) throws Exception {
		if (stringvalue.isEmpty())
			return true;
		else
			return false;

	}

	/**
	 * Purpose- To get the row count of specified sheet
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @return- Returns value of row count
	 * @throws Exception
	 */
	public int getRowCount(String sheetName) throws Exception {
		int number = 0;
		if (isSheetExist(sheetName)) {
			if (isXLSX()) {
				Sheet = workbook.getSheetAt(iIndex);
				number = Sheet.getLastRowNum() + 1;
			} else {
				hSheet = hworkbook.getSheetAt(iIndex);
				number = hSheet.getLastRowNum() + 1;
			}
		}
		return number;

	}

	/**
	 * Purpose- To get column count of specified sheet
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @return- Returns value of column count
	 * @throws Exception
	 */
	public int getColumnCount(String sheetName) throws Exception {
		int count = 0;
		if (isSheetExist(sheetName)) {
			if (isXLSX()) {
				Sheet = workbook.getSheet(sheetName);
				row = Sheet.getRow(0);
				if (row == null)
					return -1;
				count = row.getLastCellNum();
			} else {
				hSheet = hworkbook.getSheet(sheetName);
				hrow = hSheet.getRow(0);
				if (hrow == null)
					return -1;
				count = hrow.getLastCellNum();
			}
			return count;
		}
		return 0;
	}

	/**
	 * Purpose- Returns the value from Excel based on Sheetname, column name,
	 * row value
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @param colName
	 *            - Column Name should be provided
	 * @param rowNum
	 *            - Row value should be provided
	 * @return
	 */
	public String getCellDataXLSX(String sheetName, String colName, int rowNum) {
		try {
			if (isSheetExist(sheetName)) {
				if (rowNum <= 0) {
					Assert.fail("Row number should be greater than 0");
					return "";
				}
				int col_Num = -1;
				Sheet = workbook.getSheetAt(iIndex);
				row = Sheet.getRow(0);
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim()
							.equals(colName.trim()))
						col_Num = i;
				}
				if (col_Num == -1) {
					Assert.fail("Column with specified name is not being displayed");
					return "";
				}

				row = Sheet.getRow(rowNum - 1);
				if (row == null)
					return "";
				cell = row.getCell(col_Num);

				if (cell == null)
					return "";
				if (cell.getCellType() == Cell.CELL_TYPE_STRING)
					return cell.getStringCellValue();
				else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
						|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					String cellText = String
							.valueOf(cell.getNumericCellValue());
					if (DateUtil.isCellDateFormatted(cell)) {
						// format in form of D/M/YY
						double d = cell.getNumericCellValue();
						Calendar cal = Calendar.getInstance();
						cal.setTime(DateUtil.getJavaDate(d));
						int Year = cal.get(Calendar.YEAR);
						int Day = cal.get(Calendar.DAY_OF_MONTH);
						int Month = cal.get(Calendar.MONTH) + 1;
						cellText = Day + "/" + Month + "/"
								+ (String.valueOf(Year)).substring(2);
					}
					return cellText;
				} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
					return "";
				else
					return String.valueOf(cell.getBooleanCellValue());
			}
			return "";
		} catch (Exception e) {
			System.out.println("Exceptione is =" + e.getMessage());
			Assert.fail("row " + rowNum + " or column " + colName
					+ " does not exist in xls");
			return "row " + rowNum + " or column " + colName
					+ " does not exist in xls";
		}
	}

	/**
	 * Purpose- Returns the value from Excel based on Sheetname, column number,
	 * row number
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @param colNum
	 *            - Column number should be provided
	 * @param rowNum
	 *            - Row number should be provided
	 * @return
	 */
	public String getCellDataXLSX(String sheetName, int colNum, int rowNum) {
		String cellData = null;
		try {

			if (isSheetExist(sheetName)) {
				if (rowNum <= 0) {
					Assert.fail("Row number should be greater than 0");
				}
				Sheet = workbook.getSheetAt(iIndex);
				row = Sheet.getRow(rowNum - 1);
				if (row == null) {
					return "";
				}
				cell = row.getCell(colNum);
				if (cell == null) {
					return "";
				}
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

					return cell.getStringCellValue();
				} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
						|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					String cellText = String
							.valueOf(cell.getNumericCellValue());

					if (DateUtil.isCellDateFormatted(cell)) {
						// format in form of D/M/YY

						double d = cell.getNumericCellValue();
						Calendar cal = Calendar.getInstance();
						cal.setTime(DateUtil.getJavaDate(d));
						int Year = cal.get(Calendar.YEAR);
						int Day = cal.get(Calendar.DAY_OF_MONTH);
						int Month = cal.get(Calendar.MONTH) + 1;
						cellText = Day + "/" + Month + "/"
								+ (String.valueOf(Year)).substring(2);
					}
					return cellText;
				} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {

					return "";
				} else {
					cellData = String.valueOf(cell.getBooleanCellValue());

				}
			}
		} catch (Exception e) {
			/*
			 * log.error("row " + rowNum + " or column " + colNum +
			 * " does not exist in xls"); log.error("Exception is =" +
			 * e.getMessage()); Assert.fail("row " + rowNum + " or column " +
			 * colNum + " does not exist in xls" + "\n" + "Exception is =" +
			 * e.getMessage()); return "row " + rowNum + " or column " + colNum
			 * + " does not exist in xls";
			 */
		}
		return cellData;
	}

	/**
	 * Purpose- Returns the value from Excel based on Sheetname, column name,
	 * row value
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @param colName
	 *            - Column Name should be provided
	 * @param rowNum
	 *            - Row value should be provided
	 * @return
	 */
	public String getCellDataXLS(String sheetName, String colName, int rowNum) {
		try {
			if (isSheetExist(sheetName)) {
				if (rowNum <= 0) {
					Assert.fail("Row number should be greater than 0");
					return "";
				}
				int col_Num = -1;
				hSheet = hworkbook.getSheetAt(iIndex);
				hrow = hSheet.getRow(0);
				for (int i = 0; i < hrow.getLastCellNum(); i++) {
					if (hrow.getCell(i).getStringCellValue().trim()
							.equals(colName.trim()))
						col_Num = i;
				}
				if (col_Num == -1) {
					Assert.fail("Column with specified name is not being displayed");
					return "";
				}

				hrow = hSheet.getRow(rowNum - 1);
				if (hrow == null)
					return "";
				hcell = hrow.getCell(col_Num);

				if (hcell == null)
					return "";
				if (hcell.getCellType() == Cell.CELL_TYPE_STRING)
					return hcell.getStringCellValue();
				else if (hcell.getCellType() == Cell.CELL_TYPE_NUMERIC
						|| hcell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					String cellText = String.valueOf(hcell
							.getNumericCellValue());
					if (DateUtil.isCellDateFormatted(hcell)) {
						// format in form of D/M/YY
						double d = hcell.getNumericCellValue();
						Calendar cal = Calendar.getInstance();
						cal.setTime(DateUtil.getJavaDate(d));
						int Year = cal.get(Calendar.YEAR);
						int Day = cal.get(Calendar.DAY_OF_MONTH);
						int Month = cal.get(Calendar.MONTH) + 1;
						cellText = Day + "/" + Month + "/"
								+ (String.valueOf(Year)).substring(2);
					}
					return cellText;
				} else if (hcell.getCellType() == Cell.CELL_TYPE_BLANK)
					return "";
				else
					return String.valueOf(hcell.getBooleanCellValue());
			}
			return "";
		} catch (Exception e) {
			System.out.println("Exceptione is =" + e.getMessage());
			Assert.fail("row " + rowNum + " or column " + colName
					+ " does not exist in xls");
			return "row " + rowNum + " or column " + colName
					+ " does not exist in xls";
		}
	}

	/**
	 * Purpose- Returns the value from Excel based on Sheetname, column number,
	 * row number
	 * 
	 * @param sheetName
	 *            - Sheet name should be provided
	 * @param colNum
	 *            - Column number should be provided
	 * @param rowNum
	 *            - Row number should be provided
	 * @return
	 */
	public String getCellDataXLS(String sheetName, int colNum, int rowNum) {
		String cellData = null;
		try {

			if (isSheetExist(sheetName)) {
				if (rowNum <= 0) {
					Assert.fail("Row number should be greater than 0");
				}
				hSheet = hworkbook.getSheetAt(iIndex);
				hrow = hSheet.getRow(rowNum - 1);

				if (hrow == null) {
					return cellData;
				}
				hcell = hrow.getCell(colNum);
				if (hcell == null) {
					return cellData;
				}
				if (hcell.getCellType() == Cell.CELL_TYPE_STRING) {
					return hcell.getStringCellValue();
				} else if (hcell.getCellType() == Cell.CELL_TYPE_NUMERIC
						|| hcell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					String cellText = String.valueOf(hcell
							.getNumericCellValue());
					if (DateUtil.isCellDateFormatted(hcell)) {
						// format in form of D/M/YY
						double d = hcell.getNumericCellValue();
						Calendar cal = Calendar.getInstance();
						cal.setTime(DateUtil.getJavaDate(d));
						int Year = cal.get(Calendar.YEAR);
						int Day = cal.get(Calendar.DAY_OF_MONTH);
						int Month = cal.get(Calendar.MONTH) + 1;
						cellText = Day + "/" + Month + "/"
								+ (String.valueOf(Year)).substring(2);
					}
					return cellText;
				} else if (hcell.getCellType() == Cell.CELL_TYPE_BLANK) {
					return cellData;
				} else {
					cellData = String.valueOf(hcell.getBooleanCellValue());
				}
			}
		} catch (Exception e) {
			/*
			 * log.error("row " + rowNum + " or column " + colNum +
			 * " does not exist in xls"); //log.error("Exception is =" +
			 * e.getMessage());
			 */
			Assert.fail("row " + rowNum + " or column " + colNum
					+ " does not exist in xls" + "\n" + "Exception is ="
					+ e.getMessage());
			return "row " + rowNum + " or column " + colNum
					+ " does not exist in xls";
		}
		return cellData;
	}

	/**
	 * Purpose - To find given spredsheet extension is xlsx
	 * 
	 * @param sheetName
	 *            - Name of the work sheet in the Excel
	 * @param colName
	 *            - column name in the Sheet
	 * @param rowNum
	 *            - Row numbet in the sheet
	 * @return - String (value of the cell based on excel format)
	 */
	public String getCellData(String sheetName, String colName, int rowNum) {
		if (isXLSX()) {
			return getCellDataXLSX(sheetName, colName, rowNum);
		} else {
			return getCellDataXLS(sheetName, colName, rowNum);
		}
	}

	/**
	 * Purpose - To find given spredsheet extension is xlsx
	 * 
	 * @param sheetName
	 *            - Name of the work sheet in the Excel
	 * @param colNum
	 *            - column number in the sheet
	 * @param rowNum
	 *            - Row numbet in the sheet
	 * @return - String (value of the cell based on excel format)
	 */
	public String getCellData(String sheetName, int colNum, int rowNum) {
		if (isXLSX()) {
			return getCellDataXLSX(sheetName, colNum, rowNum);
		} else {
			return getCellDataXLS(sheetName, colNum, rowNum);
		}
	}

	/**
	 * Purpose - To find given spredsheet extension is xlsx
	 * 
	 * @return - boolean(if the the given spreadsheet extension is .xlsx returns
	 *         true else return false)
	 */
	public static boolean isXLSX() {
		return System.getProperty("ExcelFilePath").endsWith(".xlsx");
	}

	/**
	 * Purpose - This method will all the data present in the sheet(rows and
	 * columns) and stored in in object list
	 * 
	 * @param ScenarioName
	 */
	public Object[][] getSheetData(String sheetName) {
		Object[][] sheetData = null;
		Hashtable<String, String> table = null;

		try {
			if (isSheetExist(sheetName)) {
				int rowCount = getRowCount(sheetName);
				int colCount = getColumnCount(sheetName);
				sheetData = new Object[rowCount - 1][colCount];
				for (int i = 2; i <= rowCount; i++)
					for (int j = 0; j < colCount; j++) {
						sheetData[i - 2][j] = getCellData(sheetName, j, i);

					}
			}

		} catch (Exception e) {

			// log.error("Exception is =" + e.getMessage());
		}
		return sheetData;
	}

	/**
	 * Purpose - This method will get the data for particular keyword mentioned
	 * in excel first column columns) and stored in in object list
	 * 
	 * @param ScenarioName
	 */
	// Sandeep
	public Object[][] getSheetData(String sheetName, String keyword) {
		Object[][] sheetData = null;
		try {
			if (isSheetExist(sheetName)) {
				int rowCount = getRowCount(sheetName);
				int colCount = getColumnCount(sheetName);

				sheetData = new Object[1][colCount];
				for (int i = 2; i <= rowCount; i++) {
					String value = getCellData(sheetName, 0, i);
					if (value.equals(keyword)) {
						for (int j = 0; j < colCount; j++) {
							sheetData[0][j] = getCellData(sheetName, j, i);

						}
					}
				}
			}

		} catch (Exception e) {

			System.out.println(e.toString());
			// log.error("Exception is =" + e.getMessage());
		}
		return sheetData;
	}

	public List<String> getExcelColumnData(String sheetName, String columnName)
			throws Exception {
		List<String> cellValue = new ArrayList<String>();
		if (isSheetExist(sheetName)) {
			Sheet = workbook.getSheetAt(iIndex);
			for (int i = 2; i <= getRowCount(sheetName); i++)
				cellValue.add(getCellData(sheetName, columnName, i));
		}
		return cellValue;
	}

	// Sandeep

	public void createTestResultReport(String testcaseid, String testcasename,
			String testresult, String inputdata, String outputresult) {
		try {
			// FileInputStream xls=new
			// FileInputStream("D:\\atnt-master\\ATnTWebAutomation\\src\\test\\resources\\WebServiceTestResult.xlsx");

			String[] arr = { testcaseid, testcasename, testresult, inputdata,
					outputresult };
			// ExcelManager xls=new ExcelManager("D:\\workbook1.xlsx");
			// workbook = new XSSFWorkbook(xls);

			Sheet = workbook.getSheet("sheet1");
			int colvalue = getColumnCount("sheet1");

			int k = getRowCount("sheet1");
			XSSFRow newrow = Sheet.createRow(k);

			for (int j = 0; j < colvalue; j++) {
				XSSFCell cell3 = newrow.createCell(j);
				cell3.setCellValue(arr[j]);
			}

		} catch (Exception e) {

		}

	}

	// Sandeep
	public void updateExcelFile(String filename) throws FileNotFoundException {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
		}
	}

	// Sandeep

	public Object[][] loadExcelLines(String sheetname) {
		// Used the LinkedHashMap and LikedList to maintain the order
		Object[][] sheetData1 = null;
		HashMap<String, LinkedHashMap<Integer, List>> outerMap = new LinkedHashMap<String, LinkedHashMap<Integer, List>>();
		Object[][] sheetData = null;
		LinkedHashMap<Integer, List> hashMap = new LinkedHashMap<Integer, List>();

		String sheetName = null;
		// Create an ArrayList to store the data read from excel sheet.
		// List sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(System.getProperty("user.dir")
					+ "//src//test//resources//testdata.xlsx");
			// Create an excel workbook from the file system
			XSSFWorkbook workBook = new XSSFWorkbook(fis);
			// Get the first sheet on the workbook.
			for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workBook.getSheetAt(i);
				// XSSFSheet sheet = workBook.getSheetAt(0);
				sheetName = workBook.getSheetName(i);
				// if(sheetName.equals(sheetname)){
				Iterator rows = sheet.rowIterator();
				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					Iterator cells = row.cellIterator();

					List data = new LinkedList();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						data.add(cell);
					}
					hashMap.put(row.getRowNum(), data);

					// sheetData.add(data);
				}
				outerMap.put(sheetName, hashMap);
				sheetData1 = new Object[1][1];
				sheetData1[0][0] = outerMap.get("address");
				hashMap = new LinkedHashMap<Integer, List>();
				// }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sheetData1;

	}

	public  Object[][] getData1(String testName,String sheetname){
		// find the row num from which test starts
		// number of cols in the test
		// number of rows
		// put the data in hastable and put hastable in array
		Object[][] data=null;
		int testStartRowNum=0;
		try{
			// find the row num from which test starts
			for(int rNum=1;rNum<=getRowCount(sheetname);rNum++){
				if(getCellData(sheetname, 0, rNum).equals(testName)){
					testStartRowNum=rNum;
					break;
				}
			}
			//System.out.println("Test "+ testName +" starts from "+ testStartRowNum);

			int colStartRowNum=testStartRowNum+1;
			int totalCols=0;

			while(!getCellData(sheetname, totalCols, colStartRowNum).equals("")){
				totalCols++;
			}
			//System.out.println("Total Cols in test "+ testName + " are "+ totalCols);

			//rows
			int dataStartRowNum=testStartRowNum+2;
			int totalRows=0;
			while(!getCellData("Test Data", 0, dataStartRowNum+totalRows).equals("")){
				totalRows++;
			}
			//System.out.println("Total Rows in test "+ testName + " are "+ totalRows);

			//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// extract data
			data = new Object[totalRows][1];
			int index=0;
			Hashtable<String,String> table=null;
			for(int rNum=dataStartRowNum;rNum<(dataStartRowNum+totalRows);rNum++){
				table = new Hashtable<String,String>();
				for(int cNum=0;cNum<totalCols;cNum++){
					table.put(getCellData(sheetname, cNum, colStartRowNum), getCellData(sheetname, cNum, rNum));
					System.out.print(getCellData(sheetname, cNum, rNum) +" -- ");
				}
				data[index][0]= table;
				index++;
				System.out.println();
			}

			System.out.println("Data Read done");

			return data;
		}
		catch(Exception ee){
			System.out.println(ee.toString());
		}
		return data;

	}

	public  Hashtable<String,String>  getEnvData1(String testName,String sheetname){
		// find the row num from which test starts
		// number of cols in the test
		// number of rows
		// put the data in hastable and put hastable in array
		Object[][] data=null;
		Hashtable<String,String> table=null;
		int testStartRowNum=0;
		try{
			// find the row num from which test starts
			for(int rNum=1;rNum<=getRowCount(sheetname);rNum++){
				if(getCellData(sheetname, 0, rNum).equals(testName)){
					testStartRowNum=rNum;
					break;
				}
			}
			//	System.out.println("Test "+ testName +" starts from "+ testStartRowNum);

			int colStartRowNum=1;
			int totalCols=0;

			while(!getCellData(sheetname, totalCols, colStartRowNum).equals("")){
				totalCols++;
			}
			//	System.out.println("Total Cols in test "+ testName + " are "+ totalCols);

			//rows
			int dataStartRowNum=testStartRowNum;
			int totalRows=0;
			while(!getCellData(sheetname, 0, dataStartRowNum+totalRows).equals("")){
				totalRows++;
			}
			//System.out.println("Total Rows in test "+ testName + " are "+ totalRows);

			//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// extract data
			data = new Object[0][1];
			//for(int rNum=dataStartRowNum;rNum<(dataStartRowNum+totalRows);rNum++){
			table = new Hashtable<String,String>();
			for(int cNum=0;cNum<totalCols;cNum++){
				table.put(getCellData(sheetname, cNum, colStartRowNum), getCellData(sheetname, cNum, dataStartRowNum));
				System.out.print(getCellData(sheetname, cNum, dataStartRowNum) +" -- ");
			}
			//data[0][0]= table;
			//	index++;
			System.out.println();
			//}

			System.out.println("done");

			return table;
		}
		catch(Exception ee){
			System.out.println(ee.toString());
		}
		return table;
	}






	public HashMap<Integer, String> readExcel(String GlobalDataFileName,String SheetName,String Header, String plantname) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + GlobalDataFileName + ".xls";
		System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);

		// TO get the access to the sheet
		jxl.Sheet sh = wb.getSheet(SheetName);

		// To get the number of rows present in sheet
		int totalNoOfRows = sh.getRows();

		// To get the number of columns present in sheet
		int totalNoOfCols = sh.getColumns();
		int rownumber=0;
		int colnumber=0;
		for (int row = 0; row < totalNoOfRows; row++) {

			for (int col = 0; col < totalNoOfCols; col++) {
				System.out.print(sh.getCell(col, row).getContents() + "\t");
				if(sh.getCell(col, row).getContents().trim().equals(Header)) {
					rownumber=row;
					colnumber=col;
					//break;
				}
			}
			System.out.println();
		}

		for(int rowHeader=rownumber;rowHeader<totalNoOfRows;rowHeader++) {
			if(sh.getCell(colnumber, rowHeader).getContents().trim().equals(plantname)) {
				rownumber=rowHeader;
				System.out.println("row Number for header:"+rownumber);
				break;

			}
		}
		System.out.println("global data read done");
		System.out.println();
		HashMap<Integer,String> hm1=new HashMap<Integer,String>();
		int mapindex=1;
		for(int col=colnumber+1;col<totalNoOfCols;col++) {

			if(!sh.getCell(col, rownumber).getContents().isEmpty()) {
				System.out.println("data sheet details: "+sh.getCell(col, rownumber).getContents());
				hm1.put(mapindex, sh.getCell(col, rownumber).getContents());
				mapindex++;
			}
		}

		return hm1;
	}

	public int calculateRowCount(String GlobalDataFileName,String SheetName,String Header, String plantname) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + GlobalDataFileName + ".xls";
		System.out.println("file name : " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);

		// TO get the access to the sheet
		jxl.Sheet sh = wb.getSheet(SheetName);

		// To get the number of rows present in sheet
		int totalNoOfRows = sh.getRows();

		// To get the number of columns present in sheet
		int totalNoOfCols = sh.getColumns();
		int rownumber=0;
		int colnumber=0;
		for (int row = 0; row < totalNoOfRows; row++) {

			for (int col = 0; col < totalNoOfCols; col++) {
				if(sh.getCell(col, row).getContents().trim().equals(Header)) {
					rownumber=row;
					colnumber=col;
					//break;
				}
			}
		}

		for(int rowHeader=rownumber;rowHeader<totalNoOfRows;rowHeader++) {
			if(sh.getCell(colnumber, rowHeader).getContents().trim().contains(plantname)) {
				rownumber=rowHeader;
				break;

			}
		}
		System.out.println();
		//HashMap<Integer,String> hm1=new HashMap<Integer,String>();
		String arr[][] = new String[totalNoOfRows][totalNoOfCols];
		int rowindex=0;
		int totalrow=0;
		try {

			for(int r=rownumber;r<totalNoOfRows;r++) {				

				if(!sh.getCell(1, r).getContents().isEmpty()) {
				}else {
				}
			}

			for(int r=rownumber;r<totalNoOfRows;r++) {
				int colindex=0;
				if(!sh.getCell(1, r).getContents().isEmpty()) {
					for(int col=colnumber+1;col<totalNoOfCols;col++) {

						if(!sh.getCell(col, r).getContents().isEmpty()) {
							if(!sh.getCell(col, r).getContents().isEmpty()) {
								arr[rowindex][colindex]=sh.getCell(col, r).getContents();
							}
							colindex++;
						}
					}
				}else {
					totalrow=rowindex;
					break;
				}

				rowindex++;
			}

		}catch(Exception e1){
			Assert.fail(e1.getMessage());
		}

		return totalrow ;

	}
	public String[][] readExcelTwoDimension(String GlobalDataFileName,String SheetName) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + GlobalDataFileName + ".xls";
		System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);

		// TO get the access to the sheet
		jxl.Sheet sh = wb.getSheet(SheetName);

		// To get the number of rows present in sheet
		int totalNoOfRows = sh.getRows();

		// To get the number of columns present in sheet
		int totalNoOfCols = sh.getColumns();
		int rownumber=0;
		int colnumber=0;
		String arr[][] = new String[totalNoOfRows][totalNoOfCols];
		int rowindex=0;

		try {

			for(int r=0;r<totalNoOfRows;r++) {
				int colindex=0;
				if(!sh.getCell(1, r).getContents().isEmpty()) {
					for(int col=0;col<totalNoOfCols;col++) {
						if(!sh.getCell(col, r).getContents().isEmpty()) {
							if((!sh.getCell(col, r).getContents().isEmpty())&&(sh.getCell(col, r).getContents()!=null)&&(sh.getCell(col, r).getContents()!="")) {
								arr[rowindex][colindex]=sh.getCell(col, r).getContents();
							}
							colindex++;
						}
					}
				}else {
					break;
				}

				rowindex++;
			}

		}catch(Exception e1){

			Assert.fail(e1.getMessage());
		}

		return arr ;

	}

	public String getCellIndex(String DataFileName,String SheetName,String Header) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";
		System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);

		// TO get the access to the sheet
		jxl.Sheet sh = wb.getSheet(SheetName);

		// To get the number of rows present in sheet
		int totalNoOfRows = sh.getRows();

		// To get the number of columns present in sheet
		int totalNoOfCols = sh.getColumns();
		int rownumber=0;
		int colnumber=0;


		for (int col = 0; col < totalNoOfCols; col++) {
			for (int row = 0; row < totalNoOfRows; row++) {
				System.out.print(sh.getCell(col, row).getContents() + "\t");
				if(sh.getCell(col, row).getContents().trim().contains(Header)) {
					rownumber=row;
					colnumber=col;
					break;
				}
			}
			System.out.println();
		}

		String pos=rownumber+"_"+colnumber;

		return pos;
	}

	public int getRowIndex(String DataFileName,String SheetName,String Header, int colnumber) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";
		//System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);
		jxl.Sheet sh = wb.getSheet(SheetName);

		int totalNoOfRows = sh.getRows();
		int rownumber=0;
		for (int row = 0; row < totalNoOfRows; row++) {
			if(sh.getCell(colnumber, row).getContents().trim().equalsIgnoreCase(Header)) {
				rownumber=row;
				break;
			}

			//System.out.println();
		}


		return rownumber;
	}

	public String getCellData(String DataFileName,String SheetName,String Header,int rowNo, int colNo) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";
		//System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);
		jxl.Sheet sh = wb.getSheet(SheetName);
		System.out.print(sh.getCell(colNo, rowNo).getContents() + "\t");
		return sh.getCell(colNo, rowNo).getContents();
	}

	public HashMap<Integer, String>  readExcelRows(String DataFileName,String SheetName,String Header) throws BiffException, IOException {
		String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";
		//System.out.println("file name: " +FilePath );

		FileInputStream fs = new FileInputStream(FilePath);
		Workbook wb = Workbook.getWorkbook(fs);

		// TO get the access to the sheet
		jxl.Sheet sh = wb.getSheet(SheetName);

		// To get the number of rows present in sheet
		int totalNoOfRows = sh.getRows();

		// To get the number of columns present in sheet
		int totalNoOfCols = sh.getColumns();
		int rownumber=0;
		int colnumber=0;
		for (int col = 0; col < totalNoOfCols; col++) {
			for (int row = 0; row < totalNoOfRows; row++) {
				System.out.print(sh.getCell(col, row).getContents() + "\t");
				if(sh.getCell(col, row).getContents().trim().contains(Header)) {
					rownumber=row;
					colnumber=col;
					System.out.println("Header position:: " + rownumber +" >> "+colnumber);
					break;
				}
			}
			System.out.println();
		}
		HashMap<Integer,String> hm1=new HashMap<Integer,String>();
		int mapindex=1;
		for(int row=rownumber+1;row<totalNoOfRows;row++) {

			if((!sh.getCell(colnumber, row).getContents().isEmpty())) {
				System.out.println("data sheet details: "+sh.getCell(colnumber, row).getContents());
				hm1.put(mapindex, sh.getCell(colnumber, row).getContents());
				mapindex++;
			}
		}
		return hm1;
	}
	///write to Excel :: Arpana Kumari
	public boolean UpdateExcel(String DataFileName,String SheetName,String Header, int rowPos, int colPos, String val) throws BiffException, IOException {
		System.out.println("updating excel file");
		String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";

		FileInputStream file = new FileInputStream(FilePath);
		HSSFWorkbook wb = new HSSFWorkbook(file);

		// TO get the access to the sheet
		HSSFSheet sheet = wb.getSheet(SheetName);
		//HSSFCell cell = null; // declare a Cell object

		Cell cell = sheet.getRow(rowPos).getCell(colPos);
		if(cell.getStringCellValue()==""){
			cell= sheet.getRow(rowPos).createCell(colPos);
		}

		if(val.length()>0) {
			cell.setCellValue(val);
		}
		System.out.println("Updated Excel file with details: Header >>" + Header +" >>Value with:: "+ val);
		file.close();

		FileOutputStream outFile =new FileOutputStream(FilePath);
		wb.write(outFile);
		wb.close();
		outFile.close();

		return true;
	}
	
	///clean Excel data :: Arpana Kumari
		public boolean cleanSheet(String DataFileName,String SheetName) throws BiffException, IOException {
			System.out.println("cleaning excel file");
			String FilePath = System.getProperty("user.dir") + "\\data\\" + DataFileName + ".xls";

			FileInputStream file = new FileInputStream(FilePath);
			HSSFWorkbook wb = new HSSFWorkbook(file);
			HSSFSheet sheet = wb.getSheet(SheetName);


			for (int i = sheet.getLastRowNum(); i >= 0; i--) {
				  sheet.removeRow(sheet.getRow(i));
				}
			file.close();

			FileOutputStream outFile =new FileOutputStream(FilePath);
			wb.write(outFile);
			wb.close();
			outFile.close();

			return true;
		}

}
