package com.clt.ops.util;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CustomExcelFileReader<T> extends CustomFileReader<T> {

    public CustomExcelFileReader(Class<T> type, MultipartFile file, boolean hasHeader) {
        super(type, file, hasHeader);
    }

    // The separator is not typically used for Excel files, but keeping the constructor
    // for consistency if your CustomFileReader expects it.
    public CustomExcelFileReader(Class<T> type, MultipartFile file, boolean hasHeader, String separator) {
        super(type, file, hasHeader, separator);
    }
    public List<String> getHeaders() {
        return this.headers;
    }
    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = getCellValueAsString(cell).trim();
                if (!cellValue.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void readData() throws InstantiationException, IllegalAccessException {
        try (InputStream is = multipartFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0); 

            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();

            // Handle header row
            if (this.hasHeader && firstRow <= lastRow) {
                Row headerRow = sheet.getRow(firstRow);
                if (headerRow != null) {
                    List<String> headers = getRowValues(headerRow);
                    setHeaders(headers);
                    this.hasHeader = false;
                    firstRow++; // Start reading data from the next row
                }
            }

            // Read data rows
            for (int r = firstRow; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue; // Skip empty rows
                }

                T refObject = genericType.newInstance();
                List<String> rowData = getRowValues(row);

                List<String> listOfFieldNames = (null != getOrder()) ? getOrder() : new ArrayList<>(privateFields.keySet());

                int cellIndex = 0;
                for (String fieldName : listOfFieldNames) {
                    if (cellIndex >= rowData.size()) {
                        break; // No more data in this row
                    }
                    assign(refObject, privateFields.get(toCamelCaseWithStringBuilder(fieldName)), rowData.get(cellIndex++));
                }
                getData().add(refObject);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle specific Excel-related exceptions
        }
    }

    private List<String> getRowValues(Row row) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(this::getCellValueAsString)
                .collect(Collectors.toList());
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString(); // Or format as needed
                } else {
                	yield String.valueOf(cell.getNumericCellValue());
                	//yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula(); // You might want to evaluate formulas
            case BLANK -> "";
            default -> "";
        };
    }

    private String toCamelCaseWithStringBuilder(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        char firstChar = sb.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            sb.setCharAt(0, Character.toLowerCase(firstChar));
        }
        return sb.toString();
    }

    private Field assign(T refObject, Field field, String value)
            throws IllegalArgumentException, IllegalAccessException {
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        field.set(refObject, !value.isBlank() ? value.trim() : value);
        field.setAccessible(false);
        return field;
    }
}