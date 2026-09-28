package com.mounir.learn.qverse.data;

import com.mounir.learn.qverse.core.exception.TestDataException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel test data (Apache POI) - the format manual testers already use.
 * Row 1 = column headers, every following row = one test iteration.
 */
public final class ExcelDataReader implements TestDataReader {

    @Override
    public boolean supports(String source) {
        String s = source.toLowerCase();
        return s.endsWith(".xlsx") || s.endsWith(".xls");
    }

    @Override
    public List<Map<String, Object>> readRows(String source, String sheetName) {
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = DataStreams.open(source); Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = (sheetName == null || sheetName.isBlank()) ? wb.getSheetAt(0) : wb.getSheet(sheetName);
            if (sheet == null) {
                throw new TestDataException("Sheet '" + sheetName + "' not found in " + source);
            }
            Row header = sheet.getRow(sheet.getFirstRowNum());
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, Object> values = new LinkedHashMap<>();
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    values.put(formatter.formatCellValue(header.getCell(c)), formatter.formatCellValue(row.getCell(c)));
                }
                if (values.values().stream().anyMatch(v -> !String.valueOf(v).isBlank())) {
                    rows.add(values);
                }
            }
            return rows;
        } catch (IOException e) {
            throw new TestDataException("Cannot read Excel test data " + source, e);
        }
    }
}
