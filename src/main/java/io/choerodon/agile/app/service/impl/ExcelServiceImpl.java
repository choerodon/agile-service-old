package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.FileFeignClient;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.utils.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private static final String[] FIELDS_NAME = {"概要", "描述", "优先级", "问题类型", "故事点", "剩余时间", "修复版本", "史诗名称", "模块", "冲刺"};
    private static final String[] FIELDS = {"summary", "description", "priorityName", "typeName", "storyPoints", "remainTime", "version", "epicName", "component", "sprint"};
    private static final String BACKETNAME = "agile-service";
    private static final String SUB_TASK = "sub_task";
    private static final String UPLOAD_FILE = "upload_file";
    private static final String APPLY_TYPE_AGILE = "agile";
    private static final String CANCELED = "canceled";
    private static final String DOING = "doing";
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";
    private static final String WEBSOCKET_IMPORT_CODE = "agile-import-issues";
    private static final String STORY = "story";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String FEATURE = "feature";
    private static final String FILE_NAME = "error.xlsx";
    private static final String MULTIPART_NAME = "file";
    private static final String ORIGINAL_FILE_NAME = ".xlsx";
    private static final String VERSION_PLANNING = "version_planning";
    private static final String HIDDEN_PRIORITY = "hidden_priority";
    private static final String HIDDEN_ISSUE_TYPE = "hidden_issue_type";
    private static final String HIDDEN_FIX_VERSION = "hidden_fix_version";
    private static final String HIDDEN_COMPONENT = "hidden_component";
    private static final String HIDDEN_SPRINT = "hidden_sprint";
    private static final String RELATION_TYPE_FIX = "fix";
    private static final String IMPORT_TEMPLATE_NAME = "导入模板";

    @Autowired
    private StateMachineClientService stateMachineClientService;

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

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private IssueComponentMapper issueComponentMapper;

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private PriorityService priorityService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void download(Long projectId, Long organizationId, HttpServletRequest request, HttpServletResponse response) {
        List<PriorityVO> priorityVOList = priorityService.queryByOrganizationIdList(organizationId);
        List<IssueTypeVO> issueTypeVOList = projectConfigService.queryIssueTypesByProjectId(projectId, APPLY_TYPE_AGILE);
        List<ProductVersionCommonDTO> productVersionCommonDTOList = productVersionMapper.listByProjectId(projectId);
        List<IssueComponentDTO> issueComponentDTOList = issueComponentMapper.selectByProjectId(projectId);
        List<SprintDTO> sprintDTOList = sprintMapper.selectNotDoneByProjectId(projectId);
        List<String> priorityList = new ArrayList<>();
        for (PriorityVO priorityVO : priorityVOList) {
            if (priorityVO.getEnable()) {
                priorityList.add(priorityVO.getName());
            }
        }
        List<String> issueTypeList = new ArrayList<>();
        for (IssueTypeVO issueTypeVO : issueTypeVOList) {
            if (!SUB_TASK.equals(issueTypeVO.getTypeCode()) && !FEATURE.equals(issueTypeVO.getTypeCode())) {
                issueTypeList.add(issueTypeVO.getName());
            }
        }
        List<String> versionList = new ArrayList<>();
        for (ProductVersionCommonDTO productVersionCommonDTO : productVersionCommonDTOList) {
            if (VERSION_PLANNING.equals(productVersionCommonDTO.getStatusCode())) {
                versionList.add(productVersionCommonDTO.getName());
            }
        }
        List<String> componentList = new ArrayList<>();
        for (IssueComponentDTO issueComponentDTO : issueComponentDTOList) {
            componentList.add(issueComponentDTO.getName());
        }
        List<String> sprintList = new ArrayList<>();
        for (SprintDTO sprintDTO : sprintDTOList) {
            sprintList.add(sprintDTO.getSprintName());
        }
        Workbook wb = new XSSFWorkbook();
        // create guide sheet
        ExcelUtil.createGuideSheet(wb);
        Sheet sheet = wb.createSheet(IMPORT_TEMPLATE_NAME);
        Row row = sheet.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(wb);
        for (int i = 0; i < 10; i++) {
            sheet.setColumnWidth(i, 3500);
        }

        CatalogExcelUtil.initCell(row.createCell(0), style, FIELDS_NAME[0]);
        CatalogExcelUtil.initCell(row.createCell(1), style, FIELDS_NAME[1]);
        CatalogExcelUtil.initCell(row.createCell(2), style, FIELDS_NAME[2]);
        CatalogExcelUtil.initCell(row.createCell(3), style, FIELDS_NAME[3]);
        CatalogExcelUtil.initCell(row.createCell(4), style, FIELDS_NAME[4]);
        CatalogExcelUtil.initCell(row.createCell(5), style, FIELDS_NAME[5]);
        CatalogExcelUtil.initCell(row.createCell(6), style, FIELDS_NAME[6]);
        CatalogExcelUtil.initCell(row.createCell(7), style, FIELDS_NAME[7]);
        CatalogExcelUtil.initCell(row.createCell(8), style, FIELDS_NAME[8]);
        CatalogExcelUtil.initCell(row.createCell(9), style, FIELDS_NAME[9]);

        try {
            wb = ExcelUtil.dropDownList2007(wb, sheet, priorityList, 1, 500, 2, 2, HIDDEN_PRIORITY, 2);
            wb = ExcelUtil.dropDownList2007(wb, sheet, issueTypeList, 1, 500, 3, 3, HIDDEN_ISSUE_TYPE, 3);
            wb = ExcelUtil.dropDownList2007(wb, sheet, versionList, 1, 500, 6, 6, HIDDEN_FIX_VERSION, 4);
            wb = ExcelUtil.dropDownList2007(wb, sheet, componentList, 1, 500, 8, 8, HIDDEN_COMPONENT, 5);
            wb = ExcelUtil.dropDownList2007(wb, sheet, sprintList, 1, 500, 9, 9, HIDDEN_SPRINT, 6);
            wb.write(response.getOutputStream());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    private Boolean setIssueCreateInfo(IssueCreateVO issueCreateVO, Long projectId, Row row, Map<String, IssueTypeVO> issueTypeMap, Map<String, Long> priorityMap, Map<String, Long> versionMap, Long userId, Map<String, Long> componentMap, Map<String, Long> sprintMap) {
        String summary = row.getCell(0).toString();
        if (summary == null) {
            throw new CommonException("error.summary.null");
        }
        String description = null;
        if (!(row.getCell(1) == null || row.getCell(1).toString().equals("") || row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
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
        if (!(row.getCell(4) == null || row.getCell(4).toString().equals("") || row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK) && "故事".equals(typeName)) {
            storyPoint = new BigDecimal(row.getCell(4).toString());
        }
        BigDecimal remainTime = null;
        if (!(row.getCell(5) == null || row.getCell(5).toString().equals("") || row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            remainTime = new BigDecimal(row.getCell(5).toString());
        }
        String versionName = null;
        if (!(row.getCell(6) == null || row.getCell(6).toString().equals("") || row.getCell(6).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            versionName = row.getCell(6).toString();
        }
        String epicName = null;
        if (!(row.getCell(7) == null || row.getCell(7).toString().equals("") || row.getCell(7).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            epicName = row.getCell(7).toString();
        }
        String componentName = null;
        if (!(row.getCell(8) == null || row.getCell(8).toString().equals("") || row.getCell(8).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            componentName = row.getCell(8).toString();
        }
        String sprintName = null;
        if (!(row.getCell(9) == null || row.getCell(9).toString().equals("") || row.getCell(9).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            sprintName = row.getCell(9).toString();
        }
        List<VersionIssueRelVO> versionIssueRelVOList = null;
        if (!(versionName == null || "".equals(versionName))) {
            versionIssueRelVOList = new ArrayList<>();
            VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
            versionIssueRelVO.setVersionId(versionMap.get(versionName));
            versionIssueRelVO.setRelationType(RELATION_TYPE_FIX);
            versionIssueRelVOList.add(versionIssueRelVO);
        }
        String typeCode = issueTypeMap.get(typeName).getTypeCode();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setSummary(summary);
        if (description != null) {
            issueCreateVO.setDescription("[{\"insert\":\"" + StringUtil.replaceChar(description) + "\\n\"}]");
        }
        issueCreateVO.setPriorityCode("priority" + priorityMap.get(priorityName));
        issueCreateVO.setPriorityId(priorityMap.get(priorityName));
        issueCreateVO.setIssueTypeId(issueTypeMap.get(typeName).getId());
        issueCreateVO.setTypeCode(typeCode);
        // 当问题类型为故事，设置故事点
        if (STORY.equals(typeCode)) {
            issueCreateVO.setStoryPoints(storyPoint);
        }
        // 当问题类型为史诗，默认史诗名称与概要相同
        if (ISSUE_EPIC.equals(typeCode)) {
            issueCreateVO.setEpicName(summary);
            issueCreateVO.setEpicName(epicName);
        }
        List<ComponentIssueRelVO> componentIssueRelVOList = null;
        if (!(componentName == null || "".equals(componentName))) {
            componentIssueRelVOList = new ArrayList<>();
            ComponentIssueRelVO componentIssueRelVO = new ComponentIssueRelVO();
            componentIssueRelVO.setComponentId(componentMap.get(componentName));
            componentIssueRelVOList.add(componentIssueRelVO);
        }
        if (sprintName != null) {
            issueCreateVO.setSprintId(sprintMap.get(sprintName));
        }
        issueCreateVO.setComponentIssueRelVOList(componentIssueRelVOList);
        issueCreateVO.setRemainingTime(remainTime);
        issueCreateVO.setVersionIssueRelVOList(versionIssueRelVOList);
        issueCreateVO.setReporterId(userId);
        return true;
    }

    private void updateFinalRecode(FileOperationHistoryDTO fileOperationHistoryDTO, Long successcount, Long failCount, String status) {
        FileOperationHistoryDTO update = new FileOperationHistoryDTO();
        update.setId(fileOperationHistoryDTO.getId());
        update.setSuccessCount(successcount);
        update.setFailCount(failCount);
        update.setStatus(status);
        update.setFileUrl(fileOperationHistoryDTO.getFileUrl());
        update.setObjectVersionNumber(fileOperationHistoryDTO.getObjectVersionNumber());
        if (fileOperationHistoryMapper.updateByPrimaryKeySelective(update) != 1) {
            throw new CommonException("error.FileOperationHistoryDTO.update");
        }
        FileOperationHistoryDTO result = fileOperationHistoryMapper.selectByPrimaryKey(update.getId());
        sendProcess(result, result.getUserId(), 1.0);
    }

    private void setIssueTypeAndPriorityMap(Long organizationId, Long projectId, Map<String, IssueTypeVO> issueTypeMap, Map<String, Long> priorityMap, List<String> issueTypeList, List<String> priorityList) {
        List<PriorityVO> priorityVOList = priorityService.queryByOrganizationIdList(organizationId);
        List<IssueTypeVO> issueTypeVOList = projectConfigService.queryIssueTypesByProjectId(projectId, APPLY_TYPE_AGILE);
        for (PriorityVO priorityVO : priorityVOList) {
            if (priorityVO.getEnable()) {
                priorityMap.put(priorityVO.getName(), priorityVO.getId());
                priorityList.add(priorityVO.getName());
            }
        }
        for (IssueTypeVO issueTypeVO : issueTypeVOList) {
            if (!SUB_TASK.equals(issueTypeVO.getTypeCode()) && !FEATURE.equals(issueTypeVO.getTypeCode())) {
                issueTypeMap.put(issueTypeVO.getName(), issueTypeVO);
                issueTypeList.add(issueTypeVO.getName());
            }
        }
    }

    private void sendProcess(FileOperationHistoryDTO fileOperationHistoryDTO, Long userId, Double process) {
        fileOperationHistoryDTO.setProcess(process);
        notifyFeignClient.postWebSocket(WEBSOCKET_IMPORT_CODE, userId.toString(), JSON.toJSONString(fileOperationHistoryDTO));
    }

    private String uploadErrorExcel(Workbook errorWorkbook) {
        // 上传错误的excel
        ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, FILE_NAME, new MultipartExcelUtil(MULTIPART_NAME, ORIGINAL_FILE_NAME, errorWorkbook));
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.errorWorkbook.upload");
        }
        return response.getBody();
    }

    private Boolean checkEpicNameExist(Long projectId, String epicName) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setProjectId(projectId);
        issueDTO.setEpicName(epicName);
        List<IssueDTO> issueDTOList = issueMapper.select(issueDTO);
        return issueDTOList == null || issueDTOList.isEmpty();
    }

    private Map<Integer, String> checkRule(Long projectId, Row row, List<String> issueTypeList, List<String> priorityList, List<String> versionList, Map<String, IssueTypeVO> issueTypeMap, List<String> componentList, List<String> sprintList) {
        Map<Integer, String> errorMessage = new HashMap<>();
        // check summary
        if (row.getCell(0) == null || row.getCell(0).toString().equals("") || row.getCell(0).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(0, "概要不能为空");
        } else if (row.getCell(0).toString().length() > 44) {
            errorMessage.put(0, "概要过长");
        }
        // check priority
        if (row.getCell(2) == null || row.getCell(2).toString().equals("") || row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(2, "优先级不能为空");
        } else if (!priorityList.contains(row.getCell(2).toString())) {
            errorMessage.put(2, "优先级输入错误");
        }
        // check issue type
        if (row.getCell(3) == null || row.getCell(3).toString().equals("") || row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
            errorMessage.put(3, "问题类型不能为空");
        } else if (!issueTypeList.contains(row.getCell(3).toString())) {
            errorMessage.put(3, "问题类型输入错误");
        }
        // check story point
        if (!(row.getCell(4) == null || row.getCell(4).toString().equals("") || row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            String storyPointStr = row.getCell(4).toString().trim();
            if (storyPointStr.length() > 3) {
                errorMessage.put(4, "请输入正确的位数");
            } else if (!NumberUtil.isNumeric(storyPointStr)) {
                errorMessage.put(4, "请输入数字");
            } else {
                if (NumberUtil.isInteger(storyPointStr) || NumberUtil.canParseInteger(storyPointStr)) {
                    if (storyPointStr.trim().length() > 3) {
                        errorMessage.put(4, "最大支持3位整数");
                    } else if (storyPointStr.trim().length() > 1 && "0".equals(storyPointStr.trim().substring(0, 0))) {
                        errorMessage.put(4, "请输入正确的整数");
                    }
                } else if (!"0.5".equals(storyPointStr)) {
                    errorMessage.put(4, "小数只支持0.5");
                }
            }
        }
        // check remain time
        if (!(row.getCell(5) == null || row.getCell(5).toString().equals("") || row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            String remainTime = row.getCell(5).toString().trim();
            if (remainTime.length() > 3) {
                errorMessage.put(5, "请输入正确的位数");
            } else if (!NumberUtil.isNumeric(remainTime)) {
                errorMessage.put(5, "请输入数字");
            } else {
                if (NumberUtil.isInteger(remainTime) || NumberUtil.canParseInteger(remainTime)) {
                    if (remainTime.trim().length() > 3) {
                        errorMessage.put(5, "最大支持3位整数");
                    } else if (remainTime.trim().length() > 1 && "0".equals(remainTime.trim().substring(0, 0))) {
                        errorMessage.put(5, "请输入正确的整数");
                    }
                } else if (!"0.5".equals(remainTime)) {
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
        // check epic name
        if (!(row.getCell(3) == null || row.getCell(3).toString().equals("") || row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_BLANK)
                && issueTypeList.contains(row.getCell(3).toString())
                && ISSUE_EPIC.equals(issueTypeMap.get(row.getCell(3).toString()).getTypeCode())) {
            if (row.getCell(7) == null || row.getCell(7).toString().equals("") || row.getCell(7).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                errorMessage.put(7, "史诗名称不能为空");
            } else {
                String epicName = row.getCell(7).toString().trim();
                if (epicName.length() > 10) {
                    errorMessage.put(7, "史诗名称过长");
                } else if (!checkEpicNameExist(projectId, epicName)) {
                    errorMessage.put(7, "史诗名称重复");
                }
            }
        }
        // check component
        if (!(row.getCell(8) == null || row.getCell(8).toString().equals("") || row.getCell(8).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            if (!componentList.contains(row.getCell(8).toString())) {
                errorMessage.put(8, "请输入正确的模块");
            }
        }
        // check sprint
        if (!(row.getCell(9) == null || row.getCell(9).toString().equals("") || row.getCell(9).getCellType() == XSSFCell.CELL_TYPE_BLANK)) {
            if (!sprintList.contains(row.getCell(9).toString())) {
                errorMessage.put(9, "请输入正确的冲刺");
            }
        }
        return errorMessage;
    }

    private Boolean checkCanceled(Long projectId, Long fileOperationHistoryId, List<Long> importedIssueIds) {
        FileOperationHistoryDTO checkCanceledDO = fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryId);
        if (UPLOAD_FILE.equals(checkCanceledDO.getAction()) && CANCELED.equals(checkCanceledDO.getStatus())) {
            if (!importedIssueIds.isEmpty()) {
                LOGGER.info(importedIssueIds.toString());
                issueService.batchDeleteIssuesAgile(projectId, importedIssueIds);
            }
            return true;
        }
        return false;
    }

    private Integer getRealRowCount(Sheet sheet) {
        Integer count = 0;
        for (int r = 1; r <= sheet.getPhysicalNumberOfRows(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || (((row.getCell(0) == null || row.getCell(0).toString().equals("") || row.getCell(0).getCellType() == XSSFCell.CELL_TYPE_BLANK)) &&
                    (row.getCell(1) == null || row.getCell(1).toString().equals("") || row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(2) == null || row.getCell(2).toString().equals("") || row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(3) == null || row.getCell(3).toString().equals("") || row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(4) == null || row.getCell(4).toString().equals("") || row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(5) == null || row.getCell(5).toString().equals("") || row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(6) == null || row.getCell(6).toString().equals("") || row.getCell(6).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(7) == null || row.getCell(7).toString().equals("") || row.getCell(7).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(8) == null || row.getCell(8).toString().equals("") || row.getCell(8).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(9) == null || row.getCell(9).toString().equals("") || row.getCell(9).getCellType() == XSSFCell.CELL_TYPE_BLANK))) {
                continue;
            }
            count++;
        }
        return count;
    }

    @Async
    @Override
    public void batchImport(Long projectId, Long organizationId, Long userId, Workbook workbook) {
        String status = DOING;
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO(projectId, userId, UPLOAD_FILE, 0L, 0L, status);
        if (fileOperationHistoryMapper.insert(fileOperationHistoryDTO) != 1) {
            throw new CommonException("error.FileOperationHistoryDTO.insert");
        }
        FileOperationHistoryDTO res = fileOperationHistoryMapper.selectByPrimaryKey(fileOperationHistoryDTO.getId());
        sendProcess(res, userId, 0.0);
        if (workbook.getActiveSheetIndex() < 1
                || workbook.getSheetAt(1) == null
                || workbook.getSheetAt(1).getSheetName() == null
                || !IMPORT_TEMPLATE_NAME.equals(workbook.getSheetAt(1).getSheetName())) {
            if (fileOperationHistoryMapper.updateByPrimaryKeySelective(new FileOperationHistoryDTO(projectId, res.getId(), UPLOAD_FILE, "template_error", res.getObjectVersionNumber())) != 1) {
                throw new CommonException("error.FileOperationHistoryDTO.update");
            }
            FileOperationHistoryDTO errorImport = fileOperationHistoryMapper.selectByPrimaryKey(res.getId());
            sendProcess(errorImport, userId, 0.0);
            throw new CommonException("error.sheet.import");
        }
        Sheet sheet = workbook.getSheetAt(1);
        // 获取所有非空行
        Integer allRowCount = getRealRowCount(sheet);
        // 查询组织下的优先级与问题类型
        Map<String, IssueTypeVO> issueTypeMap = new HashMap<>();
        Map<String, Long> priorityMap = new HashMap<>();
        List<String> issueTypeList = new ArrayList<>();
        List<String> priorityList = new ArrayList<>();
        setIssueTypeAndPriorityMap(organizationId, projectId, issueTypeMap, priorityMap, issueTypeList, priorityList);
        Long failCount = 0L;
        Long successcount = 0L;
        Integer processNum = 0;
        List<Integer> errorRows = new ArrayList<>();
        Map<Integer, List<Integer>> errorMapList = new HashMap<>();
        Map<String, Long> versionMap = new HashMap<>();
        Map<String, Long> componentMap = new HashMap<>();
        Map<String, Long> sprintMap = new HashMap<>();
        List<ProductVersionCommonDTO> productVersionCommonDTOList = productVersionMapper.listByProjectId(projectId);
        List<IssueComponentDTO> issueComponentDTOList = issueComponentMapper.selectByProjectId(projectId);
        List<SprintDTO> sprintDTOList = sprintMapper.selectNotDoneByProjectId(projectId);
        List<String> versionList = new ArrayList<>();
        for (ProductVersionCommonDTO productVersionCommonDTO : productVersionCommonDTOList) {
            if (VERSION_PLANNING.equals(productVersionCommonDTO.getStatusCode())) {
                versionMap.put(productVersionCommonDTO.getName(), productVersionCommonDTO.getVersionId());
                versionList.add(productVersionCommonDTO.getName());
            }
        }
        List<String> componentList = new ArrayList<>();
        for (IssueComponentDTO issueComponentDTO : issueComponentDTOList) {
            componentList.add(issueComponentDTO.getName());
            componentMap.put(issueComponentDTO.getName(), issueComponentDTO.getComponentId());
        }
        List<String> sprintList = new ArrayList<>();
        for (SprintDTO sprintDTO : sprintDTOList) {
            sprintList.add(sprintDTO.getSprintName());
            sprintMap.put(sprintDTO.getSprintName(), sprintDTO.getSprintId());
        }
        List<Long> importedIssueIds = new ArrayList<>();
        for (int r = 1; r <= allRowCount; r++) {
            if (checkCanceled(projectId, res.getId(), importedIssueIds)) {
                return;
            }
            Row row = sheet.getRow(r);
            if (row == null || (((row.getCell(0) == null || row.getCell(0).toString().equals("") || row.getCell(0).getCellType() == XSSFCell.CELL_TYPE_BLANK)) &&
                    (row.getCell(1) == null || row.getCell(1).toString().equals("") || row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(2) == null || row.getCell(2).toString().equals("") || row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(3) == null || row.getCell(3).toString().equals("") || row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(4) == null || row.getCell(4).toString().equals("") || row.getCell(4).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(5) == null || row.getCell(5).toString().equals("") || row.getCell(5).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(6) == null || row.getCell(6).toString().equals("") || row.getCell(6).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(7) == null || row.getCell(7).toString().equals("") || row.getCell(7).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(8) == null || row.getCell(8).toString().equals("") || row.getCell(8).getCellType() == XSSFCell.CELL_TYPE_BLANK) &&
                    (row.getCell(9) == null || row.getCell(9).toString().equals("") || row.getCell(9).getCellType() == XSSFCell.CELL_TYPE_BLANK))) {
                continue;
            }
            for (int w = 0; w < FIELDS.length; w++) {
                if (row.getCell(w) != null) {
                    row.getCell(w).setCellType(XSSFCell.CELL_TYPE_STRING);
                }
            }
            Map errorMap = checkRule(projectId, row, issueTypeList, priorityList, versionList, issueTypeMap, componentList, sprintList);
            if (!errorMap.isEmpty()) {
                failCount++;
                Iterator<Map.Entry<Integer, String>> entries = errorMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<Integer, String> entry = entries.next();
                    Integer key = entry.getKey();
                    String value = entry.getValue();
                    if (row.getCell(key) == null) {
                        row.createCell(key).setCellValue("(" + value + ")");
                    } else {
                        row.getCell(key).setCellValue(row.getCell(key).toString() + " (" + value + ")");
                    }

                    List<Integer> cList = errorMapList.get(r);
                    if (cList == null) {
                        cList = new ArrayList<>();
                    }
                    cList.add(key);
                    errorMapList.put(r, cList);
                }
                errorRows.add(row.getRowNum());
                res.setFailCount(failCount);
                processNum++;
                sendProcess(res, userId, processNum * 1.0 / allRowCount);
                continue;
            }
            IssueCreateVO issueCreateVO = new IssueCreateVO();

            Boolean ok = setIssueCreateInfo(issueCreateVO, projectId, row, issueTypeMap, priorityMap, versionMap, userId, componentMap, sprintMap);
            IssueVO result = null;
            if (ok) {
                result = stateMachineClientService.createIssue(issueCreateVO, APPLY_TYPE_AGILE);
            }
            if (result == null) {
                failCount++;
                errorRows.add(row.getRowNum());
            } else {
                importedIssueIds.add(result.getIssueId());
                successcount++;
            }
            processNum++;
            res.setFailCount(failCount);
            res.setSuccessCount(successcount);
            sendProcess(res, userId, processNum * 1.0 / allRowCount);
        }
        if (!errorRows.isEmpty()) {
            LOGGER.info("导入数据有误");
            Workbook result = ExcelUtil.generateExcelAwesome(workbook, errorRows, errorMapList, FIELDS_NAME, priorityList, issueTypeList, versionList, IMPORT_TEMPLATE_NAME, componentList, sprintList);
            String errorWorkBookUrl = uploadErrorExcel(result);
            res.setFileUrl(errorWorkBookUrl);
            status = FAILED;
        } else {
            status = SUCCESS;
        }
        updateFinalRecode(res, successcount, failCount, status);
    }


    @Override
    public void cancelImport(Long projectId, Long id, Long objectVersionNumber) {
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO();
        fileOperationHistoryDTO.setId(id);
        fileOperationHistoryDTO.setStatus(CANCELED);
        fileOperationHistoryDTO.setObjectVersionNumber(objectVersionNumber);
        if (fileOperationHistoryMapper.updateByPrimaryKeySelective(fileOperationHistoryDTO) != 1) {
            throw new CommonException("error.FileOperationHistoryDTO.update");
        }
    }


    @Override
    public FileOperationHistoryVO queryLatestRecode(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        FileOperationHistoryDTO result = fileOperationHistoryMapper.queryLatestRecode(projectId, userId);
        return result == null ? new FileOperationHistoryVO() : modelMapper.map(result, FileOperationHistoryVO.class);
    }


}
