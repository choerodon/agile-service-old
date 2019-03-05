package io.choerodon.agile.infra.common.utils;

import io.choerodon.core.exception.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/8/17
 */
public class ExcelUtil {

    public enum Mode {
        SXSSF("SXSSF"), HSSF("HSSF"),XSSF("XSSF");
        private String value;

        Mode(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    private ExcelUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);
    private static final String EXCEPTION = "Exception:{}";
    private static final String ERROR_IO_WORKBOOK_WRITE_OUTPUTSTREAM = "error.io.workbook.write.output.stream";

    public static Workbook generateExcelAwesome(Workbook generateExcel, List<Integer> errorRows, Map<Integer, List<Integer>> errorMapList, String[] FIELDS_NAME, List<String> priorityList, List<String> issueTypeList, List<String> versionList, String sheetName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet resultSheet = workbook.createSheet(sheetName);
        resultSheet.setDefaultColumnWidth(25);
        Row titleRow = resultSheet.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(workbook);
        CatalogExcelUtil.initCell(titleRow.createCell(0), style, FIELDS_NAME[0]);
        CatalogExcelUtil.initCell(titleRow.createCell(1), style, FIELDS_NAME[1]);
        CatalogExcelUtil.initCell(titleRow.createCell(2), style, FIELDS_NAME[2]);
        CatalogExcelUtil.initCell(titleRow.createCell(3), style, FIELDS_NAME[3]);
        CatalogExcelUtil.initCell(titleRow.createCell(4), style, FIELDS_NAME[4]);
        CatalogExcelUtil.initCell(titleRow.createCell(5), style, FIELDS_NAME[5]);
        CatalogExcelUtil.initCell(titleRow.createCell(6), style, FIELDS_NAME[6]);

        workbook = dropDownList2007(workbook, resultSheet, priorityList, 1, 500, 2, 2, "hidden_priority", 1);
        workbook = dropDownList2007(workbook, resultSheet, issueTypeList, 1, 500, 3, 3, "hidden_issue_type", 2);
        workbook = dropDownList2007(workbook, resultSheet, versionList, 1, 500, 6, 6, "hidden_fix_version", 3);

        Sheet sheet = generateExcel.getSheetAt(0);
        int size = sheet.getPhysicalNumberOfRows();
        XSSFCellStyle ztStyle = workbook.createCellStyle();
        Font ztFont = workbook.createFont();
        ztFont.setColor(Font.COLOR_RED);
        ztStyle.setFont(ztFont);
        int index = 1;
        for (int i = 1; i <= size; i++) {
            if (errorRows.contains(i)) {
                Row row = sheet.getRow(i);
                Row newRow = resultSheet.createRow(index++);
                for (int j = 0; j < FIELDS_NAME.length; j++) {
                    Cell cell = newRow.createCell(j);
                    if (row.getCell(j) != null) {
                        cell.setCellValue(row.getCell(j).toString());
                    }
                    if (errorMapList.get(i) != null) {
                        List<Integer> errList = errorMapList.get(i);
                        if (errList.contains(j)) {
                            cell.setCellStyle(ztStyle);
                        }
                    }
                }
            }
        }
        return workbook;
    }

    public static <T> SXSSFWorkbook generateExcel(List<T> list, Class<T> clazz, String[] fieldsName, String[] fields, String sheetName) {
        //1、创建工作簿
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        if (list != null && !list.isEmpty()) {
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
        }
        return workbook;
    }

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

    public static Workbook getWorkbookFromMultipartFile(Mode mode, MultipartFile excelFile) {
        try {
            switch (mode) {
                case HSSF:
                    return new HSSFWorkbook(excelFile.getInputStream());
                case XSSF:
                    return new XSSFWorkbook(excelFile.getInputStream());
                default:
                    return null;
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }


    public static byte[] getBytes(Workbook workbook) {
        try (ByteArrayOutputStream workbookOutputStream = new ByteArrayOutputStream()) {
            workbook.write(workbookOutputStream);
            return workbookOutputStream.toByteArray();
        } catch (IOException e) {
            throw new CommonException(ERROR_IO_WORKBOOK_WRITE_OUTPUTSTREAM, e);
        }
    }

    /**
     * @param wb HSSFWorkbook对象
     * @param realSheet 需要操作的sheet对象
     * @param datas 下拉的列表数据
     * @param startRow 开始行
     * @param endRow 结束行
     * @param startCol 开始列
     * @param endCol 结束列
     * @param hiddenSheetName 隐藏的sheet名
     * @param hiddenSheetIndex 隐藏的sheet索引
     * @return
     * @throws Exception
     */
    public static XSSFWorkbook dropDownList2007(Workbook wb, Sheet realSheet, List<String> datas, int startRow, int endRow,
                                                int startCol, int endCol, String hiddenSheetName, int hiddenSheetIndex) {

        XSSFWorkbook workbook = (XSSFWorkbook) wb;
        // 创建一个数据源sheet
        XSSFSheet hidden = workbook.createSheet(hiddenSheetName);
        // 数据源sheet页不显示
        workbook.setSheetHidden(hiddenSheetIndex, true);
        // 将下拉列表的数据放在数据源sheet上
        XSSFRow row = null;
        XSSFCell cell = null;
        for (int i = 0; i < datas.size(); i++) {
            row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(datas.get(i));
        }
        //2016-12-15更新，遇到问题：生成的excel下拉框还是可以手动编辑，不满足
        //HSSFName namedCell = workbook.createName();
        //namedCell.setNameName(hiddenSheetName);
        // A1 到 Adatas.length 表示第一列的第一行到datas.length行，需要与前一步生成的隐藏的数据源sheet数据位置对应
        //namedCell.setRefersToFormula(hiddenSheetName + "!$A$1:$A" + datas.length);
        // 指定下拉数据时，给定目标数据范围 hiddenSheetName!$A$1:$A5   隐藏sheet的A1到A5格的数据
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet)realSheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hiddenSheetName + "!$A$1:$A" + datas.size());
        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
        row = null;
        cell = null;
        // 单元格样式
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 循环指定单元格下拉数据
        for (int i = startRow; i <= endRow; i++) {
            row = (XSSFRow) realSheet.createRow(i);
            cell = row.createCell(startCol);
            cell.setCellStyle(style);
            addressList = new CellRangeAddressList(i, i, startCol, endCol);
            validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            realSheet.addValidationData(validation);
        }

        return workbook;
    }
}
