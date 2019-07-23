package io.choerodon.agile.infra.utils;

import io.choerodon.core.exception.CommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.lang.reflect.Method;
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
        SXSSF("SXSSF"), HSSF("HSSF"), XSSF("XSSF");
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

    private static void initGuideSheetByRow(Workbook workbook, Sheet sheet, int rowNum, String fieldName, String requestStr, Boolean hasStyle) {
        CellStyle ztStyle = workbook.createCellStyle();
        Font ztFont = workbook.createFont();
        ztFont.setColor(Font.COLOR_RED);
        ztStyle.setFont(ztFont);
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(fieldName);
        Cell cell = row.createCell(1);
        cell.setCellValue(requestStr);
        if (hasStyle) {
            cell.setCellStyle(ztStyle);
        }
    }

    private static void initGuideSheetRemind(Workbook workbook, Sheet sheet, String remindInfo) {
        CellStyle ztStyle = workbook.createCellStyle();
        Font ztFont = workbook.createFont();
        ztFont.setColor(Font.COLOR_RED);
        ztStyle.setFont(ztFont);
        ztStyle.setAlignment(CellStyle.ALIGN_CENTER);
        ztStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.addMergedRegion(new CellRangeAddress(0, 9, 2, 4));
        Row row = sheet.getRow(0);
        Cell cell = row.createCell(2);
        cell.setCellValue(remindInfo);
        cell.setCellStyle(ztStyle);
    }

    public static void createGuideSheet(Workbook wb) {
        Sheet sheet = wb.createSheet("要求");
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 15000);
        initGuideSheetByRow(wb, sheet, 0, "概要", "必输项，限制44个字符", true);
        initGuideSheetByRow(wb, sheet, 1, "描述", "非必输", false);
        initGuideSheetByRow(wb, sheet, 2, "优先级", "必输项", true);
        initGuideSheetByRow(wb, sheet, 3, "问题类型", "必输项", true);
        initGuideSheetByRow(wb, sheet, 4, "故事点", "非必输，仅支持3位整数或者0.5", false);
        initGuideSheetByRow(wb, sheet, 5, "剩余时间", "非必输，仅支持3位整数或者0.5", false);
        initGuideSheetByRow(wb, sheet, 6, "修复版本", "非必输", false);
        initGuideSheetByRow(wb, sheet, 7, "史诗名称", "如果问题类型选择史诗，此项必填, 限制10个字符", true);
        initGuideSheetByRow(wb, sheet, 8, "模块", "非必输", false);
        initGuideSheetByRow(wb, sheet, 9, "冲刺", "非必输", false);
        sheet.setColumnWidth(2, 3000);
        initGuideSheetRemind(wb, sheet, "请至下一页，填写信息");
    }

    public static Workbook generateExcelAwesome(Workbook generateExcel, List<Integer> errorRows, Map<Integer, List<Integer>> errorMapList, String[] FIELDS_NAME, List<String> priorityList, List<String> issueTypeList, List<String> versionList, String sheetName, List<String> componentList, List<String> sprintList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        // create guide sheet
        createGuideSheet(workbook);
        Sheet resultSheet = workbook.createSheet(sheetName);
        for (int i = 0; i < 10; i++) {
            resultSheet.setColumnWidth(i, 3500);
        }
        Row titleRow = resultSheet.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(workbook);
        CatalogExcelUtil.initCell(titleRow.createCell(0), style, FIELDS_NAME[0]);
        CatalogExcelUtil.initCell(titleRow.createCell(1), style, FIELDS_NAME[1]);
        CatalogExcelUtil.initCell(titleRow.createCell(2), style, FIELDS_NAME[2]);
        CatalogExcelUtil.initCell(titleRow.createCell(3), style, FIELDS_NAME[3]);
        CatalogExcelUtil.initCell(titleRow.createCell(4), style, FIELDS_NAME[4]);
        CatalogExcelUtil.initCell(titleRow.createCell(5), style, FIELDS_NAME[5]);
        CatalogExcelUtil.initCell(titleRow.createCell(6), style, FIELDS_NAME[6]);
        CatalogExcelUtil.initCell(titleRow.createCell(7), style, FIELDS_NAME[7]);
        CatalogExcelUtil.initCell(titleRow.createCell(8), style, FIELDS_NAME[8]);
        CatalogExcelUtil.initCell(titleRow.createCell(9), style, FIELDS_NAME[9]);

        workbook = dropDownList2007(workbook, resultSheet, priorityList, 1, 500, 2, 2, "hidden_priority", 2);
        workbook = dropDownList2007(workbook, resultSheet, issueTypeList, 1, 500, 3, 3, "hidden_issue_type", 3);
        if (!versionList.isEmpty()) {
            workbook = dropDownList2007(workbook, resultSheet, versionList, 1, 500, 6, 6, "hidden_fix_version", 4);
        }
        if (!componentList.isEmpty()) {
            workbook = dropDownList2007(workbook, resultSheet, componentList, 1, 500, 8, 8, "hidden_component", 5);
        }
        if (!sprintList.isEmpty()) {
            workbook = dropDownList2007(workbook, resultSheet, sprintList, 1, 500, 9, 9, "hidden_sprint", 6);
        }
        Sheet sheet = generateExcel.getSheetAt(1);
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
    public static <T> void export(List<T> list, Class<T> clazz, String[] fieldsName, String[] fields, String sheetName, List<String> autoSizeColumn, HttpServletResponse response) {
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
            //创建标题列
            SXSSFRow row2 = sheet.createRow(0);
            row2.setHeight((short) 260);
            for (int i = 0; i < fieldsName.length; i++) {
                //3.3设置列标题
                SXSSFCell cell2 = row2.createCell(i);
                //加载单元格样式
                cell2.setCellStyle(style2);
                cell2.setCellValue(fieldsName[i]);
            }
            for (int j = 0; j < list.size(); j++) {
                SXSSFRow row = sheet.createRow(j + 1);
                row.setHeight((short) 260);
                for (int i = 0; i < fieldsName.length; i++) {
                    ;
                    //4、操作单元格；将数据写入excel
                    handleWriteCell(row, i, j, list, cellStyle, fields, clazz);
                }
            }
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < fieldsName.length; i++) {
                //设置列宽度自适应
                if (autoSizeColumn.contains(fields[i])) {
                    sheet.autoSizeColumn(i);
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
        cell.setCellStyle(cellStyle);
        if (list.get(j) != null) {
            Method method = null;
            try {
                method = clazz.getMethod(createGetter(fields[i]));
            } catch (NoSuchMethodException e) {
                LOGGER.debug(e.getMessage());
                try {
                    method = clazz.getMethod("getFoundationFieldValue");
                } catch (NoSuchMethodException e1) {
                    LOGGER.error(EXCEPTION, e1);
                }
            }
            Object invoke = new Object();
            try {
                invoke = method.invoke(list.get(j));
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.error(EXCEPTION, e);
            }
            if (invoke instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cell.setCellValue(formatter.format(invoke));
            } else if (invoke instanceof Map) {
                ObjectMapper m = new ObjectMapper();
                Map<String, String> foundationFieldValue = m.convertValue(invoke, Map.class);

                String str = foundationFieldValue.get(fields[i]) != null ? foundationFieldValue.get(fields[i]) : "";
                cell.setCellValue(str);
            } else {
                String str = invoke == null ? null : invoke.toString();
                cell.setCellValue(str);
            }
        } else {
            cell.setCellValue("");
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
     * @param wb               HSSFWorkbook对象
     * @param realSheet        需要操作的sheet对象
     * @param datas            下拉的列表数据
     * @param startRow         开始行
     * @param endRow           结束行
     * @param startCol         开始列
     * @param endCol           结束列
     * @param hiddenSheetName  隐藏的sheet名
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
        if (datas == null || datas.isEmpty()) {
            return workbook;
        }
        // 将下拉列表的数据放在数据源sheet上
        XSSFRow row = null;
        XSSFCell cell = null;
        for (int i = 0; i < datas.size(); i++) {
            row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(datas.get(i));
        }
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) realSheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hiddenSheetName + "!$A$1:$A" + datas.size());
        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
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
