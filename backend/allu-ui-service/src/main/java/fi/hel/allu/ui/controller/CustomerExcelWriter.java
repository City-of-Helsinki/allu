package fi.hel.allu.ui.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fi.hel.allu.servicecore.domain.CustomerJson;

public class CustomerExcelWriter extends CustomerExport {

  private OutputStream outputStream;
  private List<CustomerJson> customers;

  private XSSFWorkbook workbook;
  private XSSFSheet sheet;

  public CustomerExcelWriter(OutputStream outputStream, List<CustomerJson> customers) {
    this.outputStream = outputStream;
    this.customers = customers;
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("Customers");
  }

  public void write() throws IOException {
    int rowNumber = 0;
    createRow(rowNumber, getHeaders());
    for (CustomerJson customer : customers) {
      createRow(++rowNumber, getValues(customer));
    }
    autoSizeColumns();
    workbook.write(outputStream);
  }

  private void autoSizeColumns() {
    for (int i = 0; i <= getHeaders().size(); i++) {
      sheet.autoSizeColumn(i);
    }
  }

  private void createRow(int rowNumber, List<String> cellValues) {
    XSSFRow row = sheet.createRow(rowNumber);
    int columnIndex = 0;
    for (String cellValue : cellValues) {
      XSSFCell cell = row.createCell(columnIndex++);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(cellValue);
    }
  }
}
