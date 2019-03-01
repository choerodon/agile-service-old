package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.ExcelService;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
import io.choerodon.agile.domain.agile.repository.FileOperationHistoryRepository;
import io.choerodon.agile.infra.common.utils.*;
import io.choerodon.agile.infra.dataobject.FileOperationHistoryDO;
import io.choerodon.agile.infra.dataobject.ProductVersionCommonDO;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.agile.infra.mapper.FileOperationHistoryMapper;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private static final String[] FIELDS_NAME = {"概要", "描述", "优先级", "问题类型","故事点", "剩余时间", "修复版本"};
    private static final String[] FIELDS = {"summary", "description", "priorityName", "typeName", "storyPoints", "remainTime", "version"};
    private static final String BACKETNAME = "agile-service";

    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private FileOperationHistoryRepository fileOperationHistoryRepository;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    @Autowired
    private FileOperationHistoryMapper fileOperationHistoryMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private ProductVersionMapper productVersionMapper;

    @Autowired
    private IssueService issueService;


    @Override
    public void download(Long projectId, Long organizationId, HttpServletRequest request, HttpServletResponse response) {
        List<PriorityDTO> priorityDTOList = issueFeignClient.queryByOrganizationIdList(organizationId).getBody();
        List<IssueTypeDTO> issueTypeDTOList = issueFeignClient.queryIssueTypesByProjectId(projectId, "agile").getBody();
        List<ProductVersionCommonDO> productVersionCommonDOList = productVersionMapper.listByProjectId(projectId);
        List<String> priorityList = new ArrayList<>();
        for (PriorityDTO priorityDTO : priorityDTOList) {
            priorityList.add(priorityDTO.getName());
        }
        List<String> issueTypeList = new ArrayList<>();
        for (IssueTypeDTO issueTypeDTO : issueTypeDTOList) {
            issueTypeList.add(issueTypeDTO.getName());
        }
        List<String> versionList = new ArrayList<>();
        for (ProductVersionCommonDO productVersionCommonDO : productVersionCommonDOList) {
            if ("version_planning".equals(productVersionCommonDO.getStatusCode())) {
                versionList.add(productVersionCommonDO.getName());
            }
        }
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("导入模板");
        Row row = sheet.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(wb);

        CatalogExcelUtil.initCell(row.createCell(0), style, FIELDS_NAME[0]);
        CatalogExcelUtil.initCell(row.createCell(1), style, FIELDS_NAME[1]);
        CatalogExcelUtil.initCell(row.createCell(2), style, FIELDS_NAME[2]);
        CatalogExcelUtil.initCell(row.createCell(3), style, FIELDS_NAME[3]);
        CatalogExcelUtil.initCell(row.createCell(4), style, FIELDS_NAME[4]);
        CatalogExcelUtil.initCell(row.createCell(5), style, FIELDS_NAME[5]);
        CatalogExcelUtil.initCell(row.createCell(6), style, FIELDS_NAME[6]);

        try {
            wb = ExcelUtil.dropDownList2007(wb, sheet, priorityList, 1, 100, 2, 2, "hidden_priority", 1);
            wb = ExcelUtil.dropDownList2007(wb, sheet, issueTypeList, 1, 100, 3, 3, "hidden_issue_type", 2);
            wb = ExcelUtil.dropDownList2007(wb, sheet, versionList, 1, 100, 6, 6, "hidden_fix_version", 3);
//            FileOutputStream stream = new FileOutputStream("/Users/huangfuqiang/Downloads/success10.xlsx");
            wb.write(response.getOutputStream());
//            stream.close();
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    private Boolean setIssueCreateInfo(IssueCreateDTO issueCreateDTO, Long projectId, Row row, Map<String, IssueTypeDTO> issueTypeMap, Map<String, Long> priorityMap, Map<String, Long> versionMap, Long reporterId) {
        String summary = row.getCell(0).toString();
        if (summary == null) {
            throw new CommonException("error.summary.null");
        }
        String description = null;
        if (!(row.getCell(1)==null || row.getCell(1).equals("") || row.getCell(1).getCellType() ==XSSFCell.CELL_TYPE_BLANK)){
            description = row.getCell(1).toString();
        }
        String priorityName = row.getCell(2).toString();
        String typeName = row.getCell(3).toString();
        if (priorityMap.get(priorityName) == null) {
            return false;
        }
        if (issueTypeMap.get(typeName) == null) {
            return false;
        }
        BigDecimal storyPoint = null;
        if(!(row.getCell(4)==null || row.getCell(4).equals("") || row.getCell(4).getCellType() ==XSSFCell.CELL_TYPE_BLANK) && "故事".equals(typeName)) {
            storyPoint = new BigDecimal(row.getCell(4).toString());
        }
        BigDecimal remainTime = null;
        if(!(row.getCell(5)==null || row.getCell(5).equals("") || row.getCell(5).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
            remainTime = new BigDecimal(row.getCell(5).toString());
        }
        String versionName = null;
        if(!(row.getCell(6)==null || row.getCell(6).equals("") || row.getCell(6).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
            versionName = row.getCell(6).toString();
        }
        List<VersionIssueRelDTO> versionIssueRelDTOList = null;
        if (versionName != null) {
            VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
            versionIssueRelDTO.setVersionId(versionMap.get(versionName));
            versionIssueRelDTO.setRelationType("fix");
        }
        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setDescription(description);
        issueCreateDTO.setPriorityCode("priority" + priorityMap.get(priorityName));
        issueCreateDTO.setPriorityId(priorityMap.get(priorityName));
        issueCreateDTO.setIssueTypeId(issueTypeMap.get(typeName).getId());
        issueCreateDTO.setTypeCode(issueTypeMap.get(typeName).getTypeCode());
        issueCreateDTO.setStoryPoints(storyPoint);
        issueCreateDTO.setRemainingTime(remainTime);
        issueCreateDTO.setVersionIssueRelDTOList(versionIssueRelDTOList);
        issueCreateDTO.setReporterId(reporterId);
        return true;
    }

    private void updateFinalRecode(FileOperationHistoryE fileOperationHistoryE, Long successcount, Long failCount, String status) {
        FileOperationHistoryE update = new FileOperationHistoryE();
        update.setId(fileOperationHistoryE.getId());
        update.setSuccessCount(successcount);
        update.setFailCount(failCount);
        update.setStatus(status);
        update.setFileUrl(fileOperationHistoryE.getFileUrl());
        update.setObjectVersionNumber(fileOperationHistoryE.getObjectVersionNumber());
        FileOperationHistoryE result = fileOperationHistoryRepository.updateBySeletive(update);
        sendProcess(result, result.getUserId(), 1.0);
    }

    private void setIssueTypeAndPriorityMap(Long organizationId, Map<String, IssueTypeDTO> issueTypeMap, Map<String, Long> priorityMap, List<String> issueTypeList, List<String> priorityList) {
        List<PriorityDTO> priorityDTOList = issueFeignClient.queryByOrganizationIdList(organizationId).getBody();
        List<IssueTypeDTO> issueTypeDTOList = issueFeignClient.queryByOrgId(organizationId).getBody();
        for (PriorityDTO priorityDTO : priorityDTOList) {
            priorityMap.put(priorityDTO.getName(), priorityDTO.getId());
            priorityList.add(priorityDTO.getName());
        }
        for (IssueTypeDTO issueTypeDTO : issueTypeDTOList) {
            if (!"agile_subtask".equals(issueTypeDTO.getTypeCode())) {
                issueTypeMap.put(issueTypeDTO.getName(), issueTypeDTO);
                issueTypeList.add(issueTypeDTO.getName());
            }
        }
    }

    private void sendProcess(FileOperationHistoryE fileOperationHistoryE, Long userId, Double process) {
        fileOperationHistoryE.setProcess(process);
        notifyFeignClient.postWebSocket("agile-import-issues", userId.toString(), JSON.toJSONString(fileOperationHistoryE));
    }


    private SXSSFWorkbook generateErrorExcel(Sheet sheet, List<Integer> errorRows) {
        List<ExcelExportDTO> res = new ArrayList<>();
        for (int i = 0; i < errorRows.size(); i++) {
            Row row = sheet.getRow(errorRows.get(i));
            ExcelExportDTO excelExportDTO = new ExcelExportDTO();
            excelExportDTO.setSummary(row.getCell(0).toString());
            if (!(row.getCell(1)==null || row.getCell(1).equals("") || row.getCell(1).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
                excelExportDTO.setDescription(row.getCell(1).toString());
            }
            excelExportDTO.setPriorityName(row.getCell(2).toString());
            excelExportDTO.setTypeName(row.getCell(3).toString());
            if (!(row.getCell(4)==null || row.getCell(4).equals("") || row.getCell(4).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
                excelExportDTO.setStoryPoints(row.getCell(4).toString());
            }
            if (!(row.getCell(5)==null || row.getCell(5).equals("") || row.getCell(5).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
                excelExportDTO.setRemainTime(row.getCell(5).toString());
            }
            if (!(row.getCell(6)==null || row.getCell(6).equals("") || row.getCell(6).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) {
                excelExportDTO.setVersion(row.getCell(6).toString());
            }
            res.add(excelExportDTO);
        }
        return ExcelUtil.generateExcel(res, ExcelExportDTO.class, FIELDS_NAME, FIELDS, "123");
    }

    private String uploadErrorExcel(Workbook errorWorkbook) {
        // 上传错误的excel
        ResponseEntity<String> response =  fileFeignClient.uploadFile(BACKETNAME, "error.xlsx", new MultipartExcelUtil("file", ".xlsx", errorWorkbook));
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.errorWorkbook.upload");
        }
        return response.getBody();
    }

    private Map<Integer, String> checkRule(Row row, List<String> issueTypeList, List<String> priorityList, List<String> versionList) {
        Map<Integer, String> errorMessage = new HashMap<>();
        // check summary
        if (row.getCell(0)==null || row.getCell(0).equals("") || row.getCell(0).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(0, "概要不能为空");
        } else if (row.getCell(0).toString().length() > 44){
            errorMessage.put(0, "概要过长");
        }
        // check priority
        if (row.getCell(2) == null || row.getCell(2).equals("") || row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(2, "优先级不能为空");
        } else if (!priorityList.contains(row.getCell(2).toString())){
            errorMessage.put(2, "优先级输入错误");
        }
        // check issue type
        if (row.getCell(3) == null || row.getCell(3).equals("") || row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(3, "问题类型不能为空");
        } else if (!issueTypeList.contains(row.getCell(3).toString())) {
            errorMessage.put(3, "问题类型输入错误");
        }
        // check story point
        if (!(row.getCell(4) == null || row.getCell(4).equals("") || row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            String storyPointStr = row.getCell(4).toString();
            if (!NumberUtil.isNumeric(storyPointStr)) {
                errorMessage.put(4, "请输入数字");
            } else {
                if (NumberUtil.isInteger(storyPointStr)  || NumberUtil.canParseInteger(storyPointStr)) {
                    if (storyPointStr.trim().length() > 3) {
                        errorMessage.put(4, "最大支持3位整数");
                    } else if (storyPointStr.trim().length() > 1 && "0".equals(storyPointStr.trim().substring(0,0))){
                        errorMessage.put(4, "请输入正确的整数");
                    }
                } else if (!"0.5".equals(storyPointStr)){
                    errorMessage.put(4, "小数只支持0.5");
                }
            }
        }
        // check remain time
        if (!(row.getCell(5) == null || row.getCell(5).equals("") || row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            String remainTime = row.getCell(5).toString();
            if (!NumberUtil.isNumeric(remainTime)) {
                errorMessage.put(5, "请输入数字");
            } else {
                if (NumberUtil.isInteger(remainTime) || NumberUtil.canParseInteger(remainTime)) {
                    if (remainTime.trim().length() > 3) {
                        errorMessage.put(5, "最大支持3位整数");
                    } else if (remainTime.trim().length() > 1 && "0".equals(remainTime.trim().substring(0,0))){
                        errorMessage.put(5, "请输入正确的整数");
                    }
                } else if (!"0.5".equals(remainTime)){
                    errorMessage.put(5, "小数只支持0.5");
                }
            }
        }
        // check version
        if (!(row.getCell(6) == null || row.getCell(6).toString().equals("") || row.getCell(6).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            if (!versionList.contains(row.getCell(6).toString())) {
                errorMessage.put(6, "请输入正确的版本");
            }
        }
        return errorMessage;
    }

    private Boolean checkCanceled(Long projectId, Long fileOperationHistoryId, List<Long> importedIssueIds) {
        FileOperationHistoryDO checkCanceledDO = fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryId);
        if ("upload_file".equals(checkCanceledDO.getAction()) && "canceled".equals(checkCanceledDO.getStatus())) {
            if (!importedIssueIds.isEmpty()) {
                LOGGER.info(importedIssueIds.toString());
                issueService.batchDeleteIssuesAgile(projectId, importedIssueIds);
            }
            return true;
        }
        return false;
    }

    @Async
    @Override
    public void batchImport(Long projectId, Long organizationId, Long userId, Workbook workbook) {
//        Long userId = DetailsHelper.getUserDetails().getUserId();
        String status = "doing";
        FileOperationHistoryE fileOperationHistoryE = fileOperationHistoryRepository.create(new FileOperationHistoryE(projectId, userId, "upload_file", 0L, 0L, status));
        sendProcess(fileOperationHistoryE, userId, 0.0);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new CommonException("error.sheet.empty");
        }
        // 获取所有非空行
        Integer allRowCount = sheet.getPhysicalNumberOfRows() - 1;
        // 查询组织下的优先级与问题类型
        Map<String, IssueTypeDTO> issueTypeMap = new HashMap<>();
        Map<String, Long> priorityMap = new HashMap<>();
        List<String> issueTypeList = new ArrayList<>();
        List<String> priorityList = new ArrayList<>();
        setIssueTypeAndPriorityMap(organizationId, issueTypeMap, priorityMap, issueTypeList, priorityList);
        Long failCount = 0L;
        Long successcount = 0L;
        Integer processNum = 0;
        List<Integer> errorRows = new ArrayList<>();
        Map<Integer, List<Integer>> errorMapList = new HashMap<>();
        Map<String, Long> versionMap = new HashMap<>();
        List<ProductVersionCommonDO> productVersionCommonDOList = productVersionMapper.listByProjectId(projectId);
        List<String> versionList = new ArrayList<>();
        for (ProductVersionCommonDO productVersionCommonDO : productVersionCommonDOList) {
            versionMap.put(productVersionCommonDO.getName(), productVersionCommonDO.getVersionId());
            versionList.add(productVersionCommonDO.getName());
        }
        List<Long> importedIssueIds = new ArrayList<>();
        for (int r = 1; r <= allRowCount; r++) {
            if (checkCanceled(projectId, fileOperationHistoryE.getId(), importedIssueIds)) {
                return;
            }
            Row row = sheet.getRow(r);
            if (row == null || (((row.getCell(0)==null || row.getCell(0).equals("") || row.getCell(0).getCellType() ==XSSFCell.CELL_TYPE_BLANK)) &&
                    (row.getCell(1)==null || row.getCell(1).equals("") || row.getCell(1).getCellType() ==XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(2)==null || row.getCell(2).equals("") || row.getCell(2).getCellType() ==XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(3)==null || row.getCell(3).equals("") || row.getCell(3).getCellType() ==XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(4)==null || row.getCell(4).equals("") || row.getCell(4).getCellType() ==XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(5)==null || row.getCell(5).equals("") || row.getCell(5).getCellType() ==XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(6)==null || row.getCell(6).equals("") || row.getCell(6).getCellType() ==XSSFCell.CELL_TYPE_BLANK))) {
                continue;
            }
            for (int w = 0; w < FIELDS.length; w++) {
                if (row.getCell(w) != null) {
                    row.getCell(w).setCellType(XSSFCell.CELL_TYPE_STRING);
                }
            }
            Map errorMap = checkRule(row, issueTypeList, priorityList, versionList);
            if (!errorMap.isEmpty()) {
                failCount ++ ;
                Iterator<Map.Entry<Integer, String>> entries = errorMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<Integer, String> entry = entries.next();
                    Integer key = entry.getKey();
                    String value = entry.getValue();
                    if (row.getCell(key) == null) {
                        row.createCell(key).setCellValue("(" + value + ")");
                    } else {
                        row.getCell(key).setCellValue(row.getCell(key).toString()+ " (" + value + ")");
                    }

                    List<Integer> cList =  errorMapList.get(r);
                    if (cList == null) {
                        cList = new ArrayList<>();
                    }
                    cList.add(key);
                    errorMapList.put(r, cList);
                }
                errorRows.add(row.getRowNum());
                fileOperationHistoryE.setFailCount(++failCount);
                processNum ++;
                sendProcess(fileOperationHistoryE, userId, processNum * 1.0 / allRowCount);
                continue;
            }
            IssueCreateDTO issueCreateDTO = new IssueCreateDTO();

            Boolean ok = setIssueCreateInfo(issueCreateDTO, projectId, row, issueTypeMap, priorityMap, versionMap, userId);
            IssueDTO result = null;
            if (ok) {
                result = stateMachineService.createIssue(issueCreateDTO, "agile");
            }
            if (result == null) {
                failCount ++;
                errorRows.add(row.getRowNum());
            } else {
                importedIssueIds.add(result.getIssueId());
                successcount ++;
            }
            processNum ++ ;
            fileOperationHistoryE.setFailCount(failCount);
            fileOperationHistoryE.setSuccessCount(successcount);
            sendProcess(fileOperationHistoryE, userId, processNum * 1.0 / allRowCount);
        }
        if (!errorRows.isEmpty()) {
            LOGGER.info("导入数据有误");
//            SXSSFWorkbook generateExcel = generateErrorExcel(sheet, errorRows);
            Workbook result = ExcelUtil.generateExcelAwesome(workbook, errorRows, errorMapList, FIELDS_NAME, priorityList, issueTypeList, versionList, ConvertUtil.getName(projectId));
            String errorWorkBookUrl = uploadErrorExcel(result);
            fileOperationHistoryE.setFileUrl(errorWorkBookUrl);
            status = "failed";
        } else {
            status = "success";
        }
        updateFinalRecode(fileOperationHistoryE, successcount, failCount, status);
    }


    @Override
    public void cancelImport(Long projectId, Long id, Long objectVersionNumber) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        fileOperationHistoryE.setId(id);
        fileOperationHistoryE.setStatus("canceled");
        fileOperationHistoryE.setObjectVersionNumber(objectVersionNumber);
        fileOperationHistoryRepository.updateBySeletive(fileOperationHistoryE);
    }

    @Override
    public FileOperationHistoryDTO queryLatestRecode(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        FileOperationHistoryDO result = fileOperationHistoryMapper.queryLatestRecode(projectId, userId);
        return result == null ? new FileOperationHistoryDTO() : ConvertHelper.convert(result, FileOperationHistoryDTO.class);
    }


}
