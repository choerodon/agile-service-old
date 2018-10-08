package io.choerodon.agile.infra.common.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/17
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);
    private static final String EXCEPTION = "Exception:{}";

    /**
     * 通过类导出
     */
    public static <T> void export(List<T> list, Class<T> clazz, String[] fieldsName, String[] fields, String sheetName, HttpServletResponse response) {
        if (list != null && !list.isEmpty()) {
            //1、创建工作簿
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            //1.3、列标题样式
            CellStyle style2 = createCellStyle(workbook, (short) 13, CellStyle.ALIGN_LEFT, true);
            //1.4、强制换行
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            //2、创建工作表
            SXSSFSheet sheet = workbook.createSheet(sheetName);
            //设置默认列宽
            sheet.setDefaultColumnWidth(13);
            SXSSFRow row2 = sheet.createRow(0);
            row2.setHeight((short) 260);
            for (int j = 0; j < list.size(); j++) {
                SXSSFRow row = sheet.createRow(j + 1);
                row.setHeight((short) 260);
                for (int i = 0; i < fieldsName.length; i++) {
                    //3.3设置列标题
                    SXSSFCell cell2 = row2.createCell(i);
                    //加载单元格样式
                    cell2.setCellStyle(style2);
                    cell2.setCellValue(fieldsName[i]);
                    //4、操作单元格；将数据写入excel
                    handleWriteCell(row, i, j, list, cellStyle, fields, clazz);
                }
            }
            //5、输出
            try {
                String disposition = String.format("attachment;filename=\"%s-%s.xlsx\"", "Choerodon", System.currentTimeMillis());
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.addHeader("Content-Disposition", disposition);
                workbook.write(response.getOutputStream());
            } catch (Exception e) {
                LOGGER.error(EXCEPTION, e);
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    LOGGER.error(EXCEPTION, e);
                }
            }
        }
    }

    private static <T> void handleWriteCell(SXSSFRow row, int i, int j, List<T> list, CellStyle cellStyle, String[] fields, Class<T> clazz) {
        SXSSFCell cell = row.createCell(i);
        try {
            cell.setCellStyle(cellStyle);
            if (list.get(j) != null) {
                Object invoke = clazz.getMethod(createGetter(fields[i])).invoke(list.get(j));
                if (invoke instanceof Date) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cell.setCellValue(formatter.format(invoke));
                } else {
                    String str = invoke == null ? null : invoke.toString();
                    cell.setCellValue(str);
                }
            } else {
                cell.setCellValue("");
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.error(EXCEPTION, e);
        }
    }


    /**
     * 创建单元格样式
     *
     * @param workbook 工作簿
     * @param fontSize 字体大小
     * @return 单元格样式
     */
    private static CellStyle createCellStyle(SXSSFWorkbook workbook, short fontSize, short aligment, Boolean bold) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(aligment);
        //垂直居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        if (bold) {
            //加粗字体
            font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        }
        font.setFontHeightInPoints(fontSize);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 通过属性名称拼凑getter方法
     *
     * @param fieldName fieldName
     * @return String
     */
    private static String createGetter(String fieldName) {
        if (fieldName == null || fieldName.length() == 0) {
            return null;
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
