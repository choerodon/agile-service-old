package io.choerodon.agile.infra.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/28.
 * Email: fuqianghuang01@gmail.com
 */
public class CatalogExcelUtil {

    /**
     * 创建Workbook
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static Workbook createWorkBook(InputStream in) throws Exception {
        try {
            return new HSSFWorkbook(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * 获取单单元格字符串值
     *
     * @param cell
     * @return
     */
    public static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        RichTextString str = cell.getRichStringCellValue();
        return str.getString();
    }

    /**
     * 初始化Excel单元格, 设置单元格值和样式
     *
     * @param cell
     * @param style
     * @param value
     */
    public static void initCell(Cell cell, CellStyle style, String value) {
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    /**
     * 初始化Excel单元格, 设置单元格值、样式和备注
     *
     * @param cell
     * @param style
     * @param value
     * @param comment
     */
    public static void initCell(Cell cell, CellStyle style, String value, Comment comment) {
        cell.setCellStyle(style);
        cell.setCellValue(value);
        cell.setCellComment(comment);
    }

    /**
     * 获取Excel单元格备注
     *
     * @param drawing
     * @param anchor
     * @param content
     * @return
     */
    public static Comment getCellComment(Drawing drawing, HSSFClientAnchor anchor, String content) {
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(new HSSFRichTextString(content));
        return comment;
    }

    /**
     * 获取Excel标题单元格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle getHeadStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        Font font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 粗体
        style.setFont(font);
        style.setLocked(true);
        return style;
    }

    /**
     * 获取Excel数据单元格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle getBodyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        return style;
    }

    /**
     * 获取Excel错误单元格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle getErrorStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setColor(HSSFColor.RED.index);

        style.setFont(font);
        return style;
    }
}
