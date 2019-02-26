package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.ExcelService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
import io.choerodon.agile.domain.agile.repository.FileOperationHistoryRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.ExcelUtil;
import io.choerodon.agile.infra.common.utils.MultipartExcelUtil;
import io.choerodon.agile.infra.dataobject.FileOperationHistoryDO;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.agile.infra.mapper.FileOperationHistoryMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private static final String[] FIELDS_NAME = {"概要", "描述", "优先级", "问题类型"};
    private static final String[] FIELDS = {"summary", "description", "priorityName", "typeName"};
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

    @Override
    public void download(Long projectId, HttpServletRequest request, HttpServletResponse response) {
        String projectName = ConvertUtil.getName(projectId);
        List<ExportIssuesDTO> res = new ArrayList<>();
        ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
        exportIssuesDTO.setSummary("输入概要");
        exportIssuesDTO.setDescription("输入描述");
        exportIssuesDTO.setPriorityName("高");
        exportIssuesDTO.setTypeName("故事");
        ExportIssuesDTO exportIssuesDTO1 = new ExportIssuesDTO();
        exportIssuesDTO.setSummary("输入概要");
        exportIssuesDTO.setDescription("输入描述");
        exportIssuesDTO.setPriorityName("1");
        exportIssuesDTO.setTypeName("故事");
        res.add(exportIssuesDTO);
        res.add(exportIssuesDTO1);
        ExcelUtil.export(res, ExportIssuesDTO.class, FIELDS_NAME, FIELDS, projectName, response);
    }

    private Boolean setIssueCreateInfo(IssueCreateDTO issueCreateDTO, Long projectId, Row row, Map<String, IssueTypeDTO> issueTypeMap, Map<String, Long> priorityMap) {
        String summary = row.getCell(0).getStringCellValue();
        if (summary == null) {
            throw new CommonException("error.summary.null");
        }
        String description = row.getCell(1).getStringCellValue();
        String priorityName = row.getCell(2).getStringCellValue();
        String typeName = row.getCell(3).getStringCellValue();
        if (priorityMap.get(priorityName) == null) {
            return false;
        }
        if (issueTypeMap.get(typeName) == null) {
            return false;
        }
        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setDescription(description);
        issueCreateDTO.setPriorityCode("priority" + priorityMap.get(priorityName));
        issueCreateDTO.setPriorityId(priorityMap.get(priorityName));
        issueCreateDTO.setIssueTypeId(issueTypeMap.get(typeName).getId());
        issueCreateDTO.setSprintId(null);
        issueCreateDTO.setTypeCode(issueTypeMap.get(typeName).getTypeCode());
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

    private void setIssueTypeAndPriorityMap(Long organizationId, Map<String, IssueTypeDTO> issueTypeMap, Map<String, Long> priorityMap) {
        List<PriorityDTO> priorityDTOList = issueFeignClient.queryByOrganizationIdList(organizationId).getBody();
        List<IssueTypeDTO> issueTypeDTOList = issueFeignClient.queryByOrgId(organizationId).getBody();
        for (PriorityDTO priorityDTO : priorityDTOList) {
            priorityMap.put(priorityDTO.getName(), priorityDTO.getId());
        }
        for (IssueTypeDTO issueTypeDTO : issueTypeDTOList) {
            issueTypeMap.put(issueTypeDTO.getName(), issueTypeDTO);
        }
    }

    private void sendProcess(FileOperationHistoryE fileOperationHistoryE, Long userId, Double process) {
        fileOperationHistoryE.setProcess(process);
        notifyFeignClient.postWebSocket("agile-import-issues", userId.toString(), JSON.toJSONString(fileOperationHistoryE));
    }


    private SXSSFWorkbook generateErrorExcel(Sheet sheet, List<Integer> errorRows) {
        List<ExportIssuesDTO> res = new ArrayList<>();
        for (int i = 0; i < errorRows.size(); i++) {
            Row row = sheet.getRow(errorRows.get(i));
            ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
            exportIssuesDTO.setSummary(row.getCell(0).getStringCellValue());
            exportIssuesDTO.setDescription(row.getCell(1).getStringCellValue());
            exportIssuesDTO.setPriorityName(row.getCell(2).getStringCellValue());
            exportIssuesDTO.setTypeName(row.getCell(3).getStringCellValue());
            res.add(exportIssuesDTO);
        }
        return ExcelUtil.generateExcel(res, ExportIssuesDTO.class, FIELDS_NAME, FIELDS, "123");
    }

    private String uploadErrorExcel(Workbook errorWorkbook) {
        // 上传错误的excel
        ResponseEntity<String> response =  fileFeignClient.uploadFile(BACKETNAME, "error.xlsx", new MultipartExcelUtil("file", ".xlsx", errorWorkbook));
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.errorWorkbook.upload");
        }
        return response.getBody();
    }

    @Override
    public void batchImport(Long projectId, Long organizationId, Workbook workbook) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        String status = "doing";
        FileOperationHistoryE fileOperationHistoryE = fileOperationHistoryRepository.create(new FileOperationHistoryE(projectId, userId, "upload_file", 0L, 0L, status));
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new CommonException("error.sheet.empty");
        }
        // 获取所有非空行
        Integer allRowCount = sheet.getPhysicalNumberOfRows() - 1;
        // 查询组织下的优先级与问题类型
        Map<String, IssueTypeDTO> issueTypeMap = new HashMap<>();
        Map<String, Long> priorityMap = new HashMap<>();
        setIssueTypeAndPriorityMap(organizationId, issueTypeMap, priorityMap);
        Long failCount = 0L;
        Long successcount = 0L;
        Integer processNum = 0;
        List<Integer> errorRows = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
            Boolean ok = setIssueCreateInfo(issueCreateDTO, projectId, row, issueTypeMap, priorityMap);
            IssueDTO result = null;
            if (ok) {
                result = stateMachineService.createIssue(issueCreateDTO, "agile");
            }
            if (result == null) {
                failCount ++;
                errorRows.add(row.getRowNum());
            } else {
                successcount ++;
            }
            processNum ++ ;
            fileOperationHistoryE.setFailCount(failCount);
            fileOperationHistoryE.setSuccessCount(successcount);
            sendProcess(fileOperationHistoryE, userId, processNum * 1.0 / allRowCount);
        }
        if (!errorRows.isEmpty()) {
            LOGGER.info("导入数据有误");
            SXSSFWorkbook generateExcel = generateErrorExcel(sheet, errorRows);
            String errorWorkBookUrl = uploadErrorExcel(generateExcel);
            fileOperationHistoryE.setFileUrl(errorWorkBookUrl);
            status = "failed";
        } else {
            status = "success";
        }
        updateFinalRecode(fileOperationHistoryE, successcount, failCount, status);
    }


    @Override
    public void cancelImport(Long projectId, Long id) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        fileOperationHistoryE.setId(id);
        fileOperationHistoryE.setStatus("canceled");
        fileOperationHistoryRepository.updateBySeletive(fileOperationHistoryE);
    }

    @Override
    public FileOperationHistoryDTO queryLatestRecode(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        FileOperationHistoryDO result = fileOperationHistoryMapper.queryLatestRecode(projectId, userId);
        return result == null ? new FileOperationHistoryDTO() : ConvertHelper.convert(result, FileOperationHistoryDTO.class);
    }


}
