package com.wztc.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

@Slf4j
public class ExcelUtils {

    /**
     * 安全地读取单元格内容并返回字符串形式。
     * @param cell Excel单元格对象
     * @param dataFormatter 用于格式化单元格内容的DataFormatter实例
     * @return 单元格的内容转换为字符串，如果单元格为null，则返回空字符串
     */
    public static String getSafeCellContent(Cell cell, DataFormatter dataFormatter) {
        if (cell == null) {
            return "";
        }

        // 对于公式类型的单元格，尝试计算其值
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                cell = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell);
            } catch (Exception e) {
                log.error("Error evaluating formula: " + e.getMessage());
                return "";
            }
        }
        return dataFormatter.formatCellValue(cell);
    }

    /**
     * 一律按照 字符串类型 读取
     */
    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        // 强制转换单元格类型为字符串类型
        cell.setCellType(CellType.STRING);
        // 直接获取文本内容，这里不需要使用DataFormatter，因为我们已确保所有内容都被视为文本
        return cell.getStringCellValue();
    }
}