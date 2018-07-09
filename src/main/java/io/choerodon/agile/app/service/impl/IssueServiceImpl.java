package io.choerodon.agile.app.service.impl;


import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.domain.agile.rule.ProductVersionRule;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ComponentIssueRelRepository componentIssueRelRepository;
    @Autowired
    private IssueLinkRepository issueLinkRepository;
    @Autowired
    private LabelIssueRelRepository labelIssueRelRepository;
    @Autowired
    private VersionIssueRelRepository versionIssueRelRepository;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private EpicDataAssembler epicDataAssembler;
    @Autowired
    private IssueSearchAssembler issueSearchAssembler;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private ProductVersionRule productVersionRule;
    @Autowired
    private IssueComponentRepository issueComponentRepository;
    @Autowired
    private ProductVersionRepository productVersionRepository;
    @Autowired
    private IssueLabelRepository issueLabelRepository;
    @Autowired
    private IssueRule issueRule;
    @Autowired
    private SprintRule sprintRule;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private IssueStatusMapper issueStatusMapper;
    @Autowired
    private IssueAttachmentService issueAttachmentService;
    @Autowired
    private IssueLabelMapper issueLabelMapper;
    @Autowired
    private ProductVersionMapper productVersionMapper;
    @Autowired
    private IssueComponentMapper issueComponentMapper;
    @Autowired
    private IssueCommentService issueCommentService;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private ProjectInfoRepository projectInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private IssueLinkService issueLinkService;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;
    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;
    @Autowired
    private IssueCommonAssembler issueCommonAssembler;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;
    @Autowired
    private BoardService boardService;
    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;
    @Autowired
    private IssueLinkMapper issueLinkMapper;
    @Autowired
    private IssueSprintRelRepository issueSprintRelRepository;
    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper;

    private static final String STATUS_CODE_TODO = "todo";
    private static final String STATUS_CODE_DOING = "doing";
    private static final String STATUS_CODE_DONE = "done";
    private static final String SUB_TASK = "sub_task";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String ISSUE_MANAGER_TYPE = "模块负责人";
    private static final String TYPE_CODE_FIELD = "typeCode";
    private static final String EPIC_NAME_FIELD = "epicName";
    private static final String COLOR_CODE_FIELD = "colorCode";
    private static final String EPIC_ID_FIELD = "epicId";
    private static final String SPRINT_ID_FIELD = "sprintId";
    private static final String STATUS_ID = "statusId";
    private static final String EPIC_COLOR_TYPE = "epic_color";
    private static final String FIELD_SUMMARY = "summary";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_PRIORITY = "priority";
    private static final String FIELD_ASSIGNEE = "assignee";
    private static final String FIELD_REPORTER = "reporter";
    private static final String FIELD_SPRINT = "Sprint";
    private static final String STORY_TYPE = "story";
    private static final String FIELD_STORY_POINTS = "Story Points";
    private static final String FIELD_EPIC_LINK = "Epic Link";
    private static final String FIELD_TIMEESTIMATE = "timeestimate";
    private static final String FIELD_ISSUETYPE = "issuetype";
    private static final String FIELD_EPIC_CHILD = "Epic Child";
    private static final String FIELD_DESCRIPTION_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_FIX_VERSION = "Fix Version";
    private static final String FIELD_RANK = "Rank";
    private static final String RANK_HIGHER = "评级更高";
    private static final String RANK_LOWER = "评级更低";
    private static final String FIELD_LABELS = "labels";
    private static final String FIELD_COMPONENT = "Component";
    private static final String FIELD_EPIC_NAME = "Epic Name";
    private static final String NEW_STRING = "newString";
    private static final String NEW_VALUE = "newValue";
    private static final String OLD_STRING = "oldString";
    private static final String OLD_VALUE = "oldValue";
    private static final String RANK_FIELD = "rank";
    private static final String FIX_RELATION_TYPE = "fix";
    private static final String INFLUENCE_RELATION_TYPE = "influence";
    private static final String[] COLUMN_NAMES = {"编码", "概述", "类型", "所属项目", "经办人", "报告人", "状态", "描述", "关注", "冲刺", "创建时间", "最后更新时间", "优先级", "是否子任务", "初始预估", "剩余预估", "版本"};
    private static final String[] SUB_COLUMN_NAMES = {"关键字", "概述", "类型", "状态", "经办人"};
    private static final String EXPORT_ERROR = "error.issue.export";
    private static final String PROJECT_ERROR = "error.project.notFound";

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public IssueDTO createIssue(IssueCreateDTO issueCreateDTO) {
        IssueE issueE = issueAssembler.issueCreateDtoToIssueE(issueCreateDTO);
        //设置初始状态,如果有todo，就用todo，否则为doing，最后为done
        List<IssueStatusCreateDO> issueStatusCreateDOList = issueStatusMapper.queryIssueStatus(issueE.getProjectId());
        Long statusId = issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_TODO)).findFirst().orElse(
                issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DOING)).findFirst().orElse(
                        issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DONE)).findFirst().orElse(new IssueStatusCreateDO()))).getId();
        //如果是epic，初始化颜色
        if (ISSUE_EPIC.equals(issueE.getTypeCode())) {
            List<LookupValueDO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueE.initializationColor(colorList);
        }
        //初始化创建issue设置issue编号、项目默认设置
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(issueCreateDTO.getProjectId());
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        issueE.initializationIssue(statusId, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(issueE.getProjectId());
        //初始化排序
        if (issueE.isIssueRank()) {
            calculationRank(issueE.getProjectId(), issueE);
        }
        //创建issue
        Long issueId = issueRepository.create(issueE).getIssueId();
        if (issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L)) {
            issueRepository.issueToSprint(issueE.getProjectId(), issueE.getSprintId(), issueId, new Date());
        }
        handleCreateLabelIssue(issueCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateComponentIssueRel(issueCreateDTO.getComponentIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId);
        handleCreateVersionIssueRel(issueCreateDTO.getVersionIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId);
        if (issueE.getSprintId() != null && issueE.getSprintId() != 0) {
            handleCreateSprintRel(issueId, issueE.getSprintId(), issueE.getProjectId());
        }
        return queryIssue(issueCreateDTO.getProjectId(), issueId);
    }

    private void handleCreateSprintRel(Long issueId, Long sprintId, Long projectId) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setIssueId(issueId);
        dataLogE.setField(FIELD_SPRINT);
        dataLogE.setOldValue(null);
        dataLogE.setOldString(null);
        dataLogE.setNewValue(sprintId.toString());
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        dataLogE.setNewString(sprintDO.getSprintName());
        dataLogRepository.create(dataLogE);
    }

    private void calculationRank(Long projectId, IssueE issueE) {
        if (sprintRule.hasIssue(projectId, issueE.getSprintId())) {
            String rank = sprintMapper.queryMaxRank(projectId, issueE.getSprintId());
            issueE.setRank(RankUtil.genNext(rank));
        } else {
            issueE.setRank(RankUtil.mid());
        }
    }

    @Override
    public IssueDTO queryIssue(Long projectId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        return issueAssembler.issueDetailDoToDto(issue);
    }

    @Override
    public Page<IssueListDTO> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.resetOrder("search", new HashMap<>());
        Page<IssueDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueListWithoutSub(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContent()));
        Page<IssueListDTO> issueListDTOPage = new Page<>();
        issueListDTOPage.setNumber(issueDOPage.getNumber());
        issueListDTOPage.setNumberOfElements(issueDOPage.getNumberOfElements());
        issueListDTOPage.setSize(issueDOPage.getSize());
        issueListDTOPage.setTotalElements(issueDOPage.getTotalElements());
        issueListDTOPage.setTotalPages(issueDOPage.getTotalPages());
        issueListDTOPage.setContent(issueAssembler.issueDoToIssueListDto(issueDOPage.getContent()));
        return issueListDTOPage;
    }

    private void dataLogSummary(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getSummary() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_SUMMARY);
            dataLogE.setOldString(originIssue.getSummary());
            dataLogE.setNewString(issueUpdateDTO.getSummary());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogDescription(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getDescription() != null) {
            if (!FIELD_DESCRIPTION_NULL.equals(issueUpdateDTO.getDescription())) {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setProjectId(originIssue.getProjectId());
                dataLogE.setIssueId(issueUpdateDTO.getIssueId());
                dataLogE.setField(FIELD_DESCRIPTION);
                dataLogE.setOldString(originIssue.getDescription());
                dataLogE.setNewString(issueUpdateDTO.getDescription());
                dataLogRepository.create(dataLogE);
            } else {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setProjectId(originIssue.getProjectId());
                dataLogE.setIssueId(issueUpdateDTO.getIssueId());
                dataLogE.setField(FIELD_DESCRIPTION);
                dataLogE.setOldString(originIssue.getDescription());
                dataLogRepository.create(dataLogE);
            }
        }
    }

    private void dataLogPriority(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getPriorityCode() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_PRIORITY);
            dataLogE.setOldString(lookupValueMapper.selectNameByValueCode(originIssue.getPriorityCode()));
            dataLogE.setNewString(lookupValueMapper.selectNameByValueCode(issueUpdateDTO.getPriorityCode()));
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogAssignee(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getAssigneeId() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_ASSIGNEE);
            if (originIssue.getAssigneeId() != null && originIssue.getAssigneeId() != 0) {
                dataLogE.setOldValue(originIssue.getAssigneeId().toString());
                dataLogE.setOldString(userRepository.queryUserNameByOption(originIssue.getAssigneeId(), false));
            }
            if (issueUpdateDTO.getAssigneeId() != 0) {
                dataLogE.setNewValue(issueUpdateDTO.getAssigneeId().toString());
                dataLogE.setNewString(userRepository.queryUserNameByOption(issueUpdateDTO.getAssigneeId(), false));
            } else {
                dataLogE.setNewValue(null);
                dataLogE.setNewString(null);
            }
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogReporter(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getReporterId() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_REPORTER);
            if (originIssue.getReporterId() != null && originIssue.getReporterId() != 0) {
                dataLogE.setOldValue(originIssue.getReporterId().toString());
                dataLogE.setOldString(userRepository.queryUserNameByOption(originIssue.getReporterId(), false));
            }
            if (issueUpdateDTO.getReporterId() != 0) {
                dataLogE.setNewValue(issueUpdateDTO.getReporterId().toString());
                dataLogE.setNewString(userRepository.queryUserNameByOption(issueUpdateDTO.getReporterId(), false));
            }
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogSprint(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO, boolean isRecordSprintLog) {
        if (!isRecordSprintLog) {
            return;
        }
        SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(originIssue.getIssueId()));
        List<SprintNameDTO> closeSprintNames = sprintNameAssembler.doListToDTO(issueMapper.queryCloseSprintNameByIssueId(originIssue.getIssueId()));
        SprintNameDTO sprintName = sprintNameAssembler.doToDTO(sprintMapper.querySprintNameBySprintId(originIssue.getProjectId(), issueUpdateDTO.getSprintId()));
        if ((activeSprintName == null && sprintName == null) || (sprintName != null && activeSprintName != null && Objects.equals(sprintName.getSprintId(), activeSprintName.getSprintId()))) {
            return;
        }
        Map<String, String> valuesMap = dealSprint(closeSprintNames, activeSprintName, sprintName);
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(originIssue.getProjectId());
        dataLogE.setIssueId(issueUpdateDTO.getIssueId());
        dataLogE.setField(FIELD_SPRINT);
        dataLogE.setOldValue("".equals(valuesMap.get(OLD_VALUE)) ? null : valuesMap.get(OLD_VALUE));
        dataLogE.setOldString("".equals(valuesMap.get(OLD_STRING)) ? null : valuesMap.get(OLD_STRING));
        dataLogE.setNewValue("".equals(valuesMap.get(NEW_VALUE)) ? null : valuesMap.get(NEW_VALUE));
        dataLogE.setNewString("".equals(valuesMap.get(NEW_STRING)) ? null : valuesMap.get(NEW_STRING));
        dataLogRepository.create(dataLogE);
        dataLogSubIssueSprint(issueUpdateDTO.getIssueId(), originIssue.getProjectId(), valuesMap);
    }

    private void dataLogSubIssueSprint(Long issueId, Long projectId, Map<String, String> valuesMap) {
        List<IssueE> issueEList = ConvertHelper.convertList(issueMapper.queryIssueSubList(projectId, issueId), IssueE.class);
        issueEList.forEach(issueE -> {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(projectId);
            dataLogE.setIssueId(issueE.getIssueId());
            dataLogE.setField(FIELD_SPRINT);
            dataLogE.setOldValue("".equals(valuesMap.get(OLD_VALUE)) ? null : valuesMap.get(OLD_VALUE));
            dataLogE.setOldString("".equals(valuesMap.get(OLD_STRING)) ? null : valuesMap.get(OLD_STRING));
            dataLogE.setNewValue("".equals(valuesMap.get(NEW_VALUE)) ? null : valuesMap.get(NEW_VALUE));
            dataLogE.setNewString("".equals(valuesMap.get(NEW_STRING)) ? null : valuesMap.get(NEW_STRING));
            dataLogRepository.create(dataLogE);
        });
    }

    private Map<String, String> dealSprint(List<SprintNameDTO> closeSprintNames, SprintNameDTO activeSprintName, SprintNameDTO sprintName) {
        Map<String, String> valuesMap = new HashMap<>();
        String oldValue;
        String oldString;
        String newValue;
        String newString;
        String closeSprintIdStr = closeSprintNames.stream().map(closeSprintName -> closeSprintName.getSprintId().toString()).collect(Collectors.joining(","));
        String closeSprintNameStr = closeSprintNames.stream().map(SprintNameDTO::getSprintName).collect(Collectors.joining(","));
        oldValue = newValue = closeSprintIdStr;
        oldString = newString = closeSprintNameStr;
        if (activeSprintName != null) {
            oldValue = ("".equals(oldValue) ? activeSprintName.getSprintId().toString() : oldValue + "," + activeSprintName.getSprintId().toString());
            oldString = ("".equals(oldString) ? activeSprintName.getSprintName() : oldString + "," + activeSprintName.getSprintName());
        }
        if (sprintName != null) {
            newValue = ("".equals(newValue) ? sprintName.getSprintId().toString() : newValue + "," + sprintName.getSprintId().toString());
            newString = ("".equals(newString) ? sprintName.getSprintName() : newString + "," + sprintName.getSprintName());
        }
        valuesMap.put(OLD_VALUE, oldValue);
        valuesMap.put(OLD_STRING, oldString);
        valuesMap.put(NEW_VALUE, newValue);
        valuesMap.put(NEW_STRING, newString);
        return valuesMap;
    }

    private void dataLogStoryPoint(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getStoryPoints() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_STORY_POINTS);
            if (originIssue.getStoryPoints() != null) {
                dataLogE.setOldString(originIssue.getStoryPoints().toString());
            }
            dataLogE.setNewString(issueUpdateDTO.getStoryPoints().toString());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogEpicChild(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (originIssue.getEpicId() != null && originIssue.getEpicId() != 0) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setField(FIELD_EPIC_CHILD);
            dataLogE.setIssueId(issueUpdateDTO.getEpicId());
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setNewValue(originIssue.getIssueId().toString());
            dataLogE.setNewString(originIssue.getSummary());
            dataLogRepository.create(dataLogE);
            DataLogE removeEpicChild = new DataLogE();
            removeEpicChild.setProjectId(originIssue.getProjectId());
            removeEpicChild.setIssueId(originIssue.getEpicId());
            removeEpicChild.setField(FIELD_EPIC_CHILD);
            removeEpicChild.setOldValue(originIssue.getIssueId().toString());
            removeEpicChild.setOldString(originIssue.getSummary());
            removeEpicChild.setNewValue(null);
            removeEpicChild.setNewString(null);
            dataLogRepository.create(removeEpicChild);
        } else {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setField(FIELD_EPIC_CHILD);
            dataLogE.setIssueId(issueUpdateDTO.getEpicId());
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setNewValue(originIssue.getIssueId().toString());
            dataLogE.setNewString(originIssue.getSummary());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogEpic(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getEpicId() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_EPIC_LINK);
            if (originIssue.getEpicId() != null && originIssue.getEpicId() != 0) {
                dataLogE.setOldValue(originIssue.getEpicId().toString());
                dataLogE.setOldString(issueMapper.selectByPrimaryKey(originIssue.getEpicId()).getSummary());
            }
            if (issueUpdateDTO.getEpicId() != 0) {
                dataLogE.setNewValue(issueUpdateDTO.getEpicId().toString());
                dataLogE.setNewString(issueMapper.selectByPrimaryKey(issueUpdateDTO.getEpicId()).getSummary());
            } else {
                dataLogE.setNewValue(null);
                dataLogE.setNewString(null);
            }
            dataLogRepository.create(dataLogE);
            dataLogEpicChild(originIssue, issueUpdateDTO);
        }
    }

    private void dataLogRemainTime(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getRemainingTime() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_TIMEESTIMATE);
            if (originIssue.getRemainingTime() != null) {
                dataLogE.setOldValue(originIssue.getRemainingTime().toString());
                dataLogE.setOldString(originIssue.getRemainingTime().toString());
            }
            dataLogE.setNewValue(issueUpdateDTO.getRemainingTime().toString());
            dataLogE.setNewString(issueUpdateDTO.getRemainingTime().toString());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogEpicName(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO) {
        if (issueUpdateDTO.getEpicName() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_EPIC_NAME);
            dataLogE.setOldString(originIssue.getEpicName());
            dataLogE.setNewString(issueUpdateDTO.getEpicName());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLog(List<String> fieldList, IssueDO originIssue, IssueUpdateDTO issueUpdateDTO, boolean isRecordSprintLog) {
        dataLogEpicName(originIssue, issueUpdateDTO);
        dataLogSummary(originIssue, issueUpdateDTO);
        dataLogDescription(originIssue, issueUpdateDTO);
        dataLogPriority(originIssue, issueUpdateDTO);
        dataLogAssignee(originIssue, issueUpdateDTO);
        dataLogReporter(originIssue, issueUpdateDTO);
        dataLogSprint(originIssue, issueUpdateDTO, isRecordSprintLog);
        dataLogStoryPoint(originIssue, issueUpdateDTO);
        dataLogEpic(originIssue, issueUpdateDTO);
        dataLogRemainTime(originIssue, issueUpdateDTO);
        dataLogStatus(fieldList, issueUpdateDTO, originIssue);
    }

    @Override
    public IssueDTO updateIssue(Long projectId, IssueUpdateDTO issueUpdateDTO, List<String> fieldList) {
        IssueDO originIssue = issueMapper.selectByPrimaryKey(issueUpdateDTO.getIssueId());
        if (fieldList != null && !fieldList.isEmpty()) {
            //日志记录
            dataLog(fieldList, originIssue, issueUpdateDTO, fieldList.contains(SPRINT_ID_FIELD));
            IssueE issueE = issueAssembler.issueUpdateDtoToEntity(issueUpdateDTO);
            //处理用户，前端可能会传0，处理为null
            issueE.initializationIssueUser();
            if (fieldList.contains(SPRINT_ID_FIELD)) {
                IssueE oldIssue = ConvertHelper.convert(originIssue, IssueE.class);
                List<Long> issueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
                issueIds.add(issueE.getIssueId());
                issueRepository.removeIssueFromSprintByIssueIds(projectId, issueIds);
                if (issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L)) {
                    issueRepository.issueToDestinationByIds(projectId, issueE.getSprintId(), issueIds, new Date());
                }
                if (issueE.isIssueRank() || (issueE.getTypeCode() == null && oldIssue.isIssueRank())) {
                    calculationRank(projectId, issueE);
                    fieldList.add(RANK_FIELD);
                }
            }
            //todo epicId改变记录日志
            issueRepository.update(issueE, fieldList.toArray(new String[fieldList.size()]));
        }
        Long issueId = issueUpdateDTO.getIssueId();
        handleUpdateLabelIssue(issueUpdateDTO.getLabelIssueRelDTOList(), issueId);
        handleUpdateComponentIssueRel(issueUpdateDTO.getComponentIssueRelDTOList(), projectId, issueId);
        if (issueUpdateDTO.getVersionType() != null) {
            handleUpdateVersionIssueRel(issueUpdateDTO.getVersionIssueRelDTOList(), projectId, issueId, issueUpdateDTO.getVersionType());
        }
        return queryIssue(projectId, issueId);
    }

    private void dataLogStatus(List<String> fieldList, IssueUpdateDTO issueUpdateDTO, IssueDO originIssue) {
        if (fieldList.contains(STATUS_ID)) {
            IssueE dataLogIssue = new IssueE();
            dataLogIssue.setIssueId(issueUpdateDTO.getIssueId());
            dataLogIssue.setStatusId(issueUpdateDTO.getStatusId());
            dataLogIssue.setProjectId(originIssue.getProjectId());
            boardService.dataLogStatus(originIssue, dataLogIssue);
        }
    }


    @Override
    public List<EpicDataDTO> listEpic(Long projectId) {
        List<EpicDataDTO> epicDataList = epicDataAssembler.doListToDTO(issueMapper.queryEpicList(projectId));
        if (!epicDataList.isEmpty()) {
            List<Long> epicIds = epicDataList.stream().map(EpicDataDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = issueMapper.queryIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = issueMapper.queryDoneIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryNotEstimateIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> totalEstimateMap = issueMapper.queryTotalEstimateByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            epicDataList.forEach(epicData -> {
                epicData.setIssueCount(issueCountMap.get(epicData.getIssueId()));
                epicData.setDoneIssueCount(doneIssueCountMap.get(epicData.getIssueId()));
                epicData.setNotEstimate(notEstimateIssueCountMap.get(epicData.getIssueId()));
                epicData.setTotalEstimate(totalEstimateMap.get(epicData.getIssueId()));
            });
        }
        return epicDataList;
    }

    private void dataLogDeleteByIssueId(Long projectId, Long issueId) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setIssueId(issueId);
        dataLogRepository.delete(dataLogE);
    }

    @Override
    public int deleteIssue(Long projectId, Long issueId) {
        IssueE issueE = queryIssueByProjectIdAndIssueId(projectId, issueId);
        //删除issueLink
        issueLinkRepository.deleteByIssueId(issueE.getIssueId());
        //删除标签关联
        labelIssueRelRepository.deleteByIssueId(issueE.getIssueId());
        //没有issue使用的标签进行垃圾回收
        issueLabelRepository.labelGarbageCollection();
        //删除模块关联
        componentIssueRelRepository.deleteByIssueId(issueE.getIssueId());
        //删除版本关联
        versionIssueRelRepository.deleteByIssueId(issueE.getIssueId());
        issueRepository.deleteIssueFromSprintByIssueId(projectId, issueId);
        //删除评论信息
        issueCommentService.deleteByIssueId(issueE.getIssueId());
        //删除附件
        issueAttachmentService.deleteByIssueId(issueE.getIssueId());
        //不是子任务的issue删除子任务
        if (!(SUB_TASK).equals(issueE.getTypeCode())) {
            if ((ISSUE_EPIC).equals(issueE.getTypeCode())) {
                //如果是epic，会把该epic下的issue的epicId置为0
                issueRepository.batchUpdateIssueEpicId(projectId, issueE.getIssueId());
            }
            List<IssueDO> issueDOList = issueMapper.queryIssueSubList(projectId, issueE.getIssueId());
            if (issueDOList != null && !issueDOList.isEmpty()) {
                issueDOList.forEach(subIssue -> deleteIssue(subIssue.getProjectId(), subIssue.getIssueId()));
            }
        }
        dataLogDeleteByIssueId(projectId, issueId);
        return issueRepository.delete(projectId, issueE.getIssueId());
    }

    @Override
    public IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO) {
        IssueE subIssueE = issueAssembler.issueSubCreateDtoToEntity(issueSubCreateDTO);
        IssueE parentIssueE = ConvertHelper.convert(issueMapper.queryIssueByIssueId(subIssueE.getProjectId(), subIssueE.getParentIssueId()), IssueE.class);
        //日志记录
        IssueDO issueDO = new IssueDO();
        List<IssueStatusCreateDO> issueStatusCreateDOList = issueStatusMapper.queryIssueStatus(subIssueE.getProjectId());
        Long statusId = issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_TODO)).findFirst().orElse(
                issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DOING)).findFirst().orElse(
                        issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DONE)).findFirst().orElse(new IssueStatusCreateDO()))).getId();
        issueDO.setProjectId(subIssueE.getProjectId());
        issueDO.setStatusId(statusId);
        //设置初始状态,跟随父类状态
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(subIssueE.getProjectId());
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        subIssueE = parentIssueE.initializationSubIssue(subIssueE, projectInfoE);
        boardService.dataLogStatus(issueDO, subIssueE);
        projectInfoRepository.updateIssueMaxNum(subIssueE.getProjectId());
        //创建issue
        Long issueId = issueRepository.create(subIssueE).getIssueId();
        if (subIssueE.getSprintId() != null && !Objects.equals(subIssueE.getSprintId(), 0L)) {
            issueRepository.issueToSprint(subIssueE.getProjectId(), subIssueE.getSprintId(), issueId, new Date());
        }
        if (issueSubCreateDTO.getIssueLinkCreateDTOList() != null && !issueSubCreateDTO.getIssueLinkCreateDTOList().isEmpty()) {
            issueLinkService.createIssueLinkList(issueSubCreateDTO.getIssueLinkCreateDTOList(), issueId, issueSubCreateDTO.getProjectId());
        }
        handleCreateLabelIssue(issueSubCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateComponentIssueRel(issueSubCreateDTO.getComponentIssueRelDTOList(), issueSubCreateDTO.getProjectId(), issueId);
        handleCreateVersionIssueRel(issueSubCreateDTO.getVersionIssueRelDTOList(), issueSubCreateDTO.getProjectId(), issueId);
        if (subIssueE.getSprintId() != null) {
            handleCreateSprintRel(issueId, subIssueE.getSprintId(), subIssueE.getProjectId());
        }
        return queryIssueSub(subIssueE.getProjectId(), issueId);
    }

    private List<ProductVersionDO> getVersionRelsByIssueId(Long projectId, Long issueId) {
        return productVersionMapper.selectVersionRelsByIssueId(projectId, issueId);
    }

    private Map getVersionIssueRelsByBatch(Long projectId, List<Long> issueIds) {
        Map map = new HashMap();
        for (Long issueId : issueIds) {
            map.put(issueId, getVersionRelsByIssueId(projectId, issueId));
        }
        return map;
    }

    private void dataLogVersionByAdd(Long projectId, Long versionId, List<Long> issueIds) {
        ProductVersionDO productVersionDO = productVersionMapper.selectByPrimaryKey(versionId);
        if (productVersionDO == null) {
            throw new CommonException("error.productVersion.get");
        }
        for (Long issueId : issueIds) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(projectId);
            dataLogE.setIssueId(issueId);
            dataLogE.setField(FIELD_FIX_VERSION);
            dataLogE.setNewValue(productVersionDO.getVersionId().toString());
            dataLogE.setNewString(productVersionDO.getName());
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogVersionByRemove(Long projectId, Map map) {
        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry<Long, List<ProductVersionDO>>)object;
            Long issueId = Long.parseLong(entry.getKey().toString());
            List<ProductVersionDO> versionIssueRelDOList = (List<ProductVersionDO>) entry.getValue();
            for (ProductVersionDO productVersionDO : versionIssueRelDOList) {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setProjectId(projectId);
                dataLogE.setField(FIELD_FIX_VERSION);
                dataLogE.setIssueId(issueId);
                dataLogE.setOldValue(productVersionDO.getVersionId().toString());
                dataLogE.setOldString(productVersionDO.getName());
                dataLogRepository.create(dataLogE);
            }
        }
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExist(projectId, versionId);
            issueRepository.batchIssueToVersion(projectId, versionId, issueIds);
            dataLogVersionByAdd(projectId, versionId, issueIds);
        } else {
            Map map = getVersionIssueRelsByBatch(projectId, issueIds);
            issueRepository.batchRemoveVersion(projectId, issueIds);
            dataLogVersionByRemove(projectId, map);
        }
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    @Override
    public List<IssueSearchDTO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueRule.judgeExist(projectId, epicId);
        issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        //todo 修改epic记录日志
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    private void dataLogRank(Long projectId, MoveIssueDTO moveIssueDTO, String rankStr, Long sprintId) {
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(issueId));
            if ((sprintId == 0 && activeSprintName == null) || (activeSprintName != null && sprintId.equals(activeSprintName.getSprintId()))) {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setProjectId(projectId);
                dataLogE.setField(FIELD_RANK);
                dataLogE.setIssueId(issueId);
                dataLogE.setNewString(rankStr);
                dataLogRepository.create(dataLogE);
            }
        }
    }

    private SprintDO getSprintById(Long sprintId) {
        return sprintMapper.selectByPrimaryKey(sprintId);
    }

    private List<DataLogE> getSprintDataLogByMove(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO) {
        SprintDO sprintDO = getSprintById(sprintId);
        List<DataLogE> dataLogEList = new ArrayList<>();
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(issueId));
            if (activeSprintName != null && sprintId.equals(activeSprintName.getSprintId())) {
                continue;
            }
            String newSprintIdStr = "";
            String newSprintNameStr = "";
            List<SprintNameDTO> sprintNames = sprintNameAssembler.doListToDTO(issueMapper.querySprintNameByIssueId(issueId));
            String oldSprintIdStr = sprintNames.stream().map(sprintName -> sprintName.getSprintId().toString()).collect(Collectors.joining(","));
            String oldSprintNameStr = sprintNames.stream().map(SprintNameDTO::getSprintName).collect(Collectors.joining(","));
            int idx = 0;
            for (SprintNameDTO sprintName : sprintNames) {
                if (activeSprintName != null && activeSprintName.getSprintId().equals(sprintName.getSprintId())) {
                    continue;
                }
                if (idx == 0) {
                    newSprintNameStr = sprintName.getSprintName();
                    newSprintIdStr = sprintName.getSprintId().toString();
                    idx++;
                } else {
                    newSprintNameStr = newSprintNameStr + "," + sprintName.getSprintName();
                    newSprintIdStr = newSprintIdStr + "," + sprintName.getSprintId().toString();
                }
            }
            if (sprintDO != null) {
                newSprintIdStr = "".equals(newSprintIdStr) ? sprintDO.getSprintId().toString() : newSprintIdStr + "," + sprintDO.getSprintId().toString();
                newSprintNameStr = "".equals(newSprintNameStr) ? sprintDO.getSprintName() : newSprintNameStr + "," + sprintDO.getSprintName();
            }
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(projectId);
            dataLogE.setIssueId(issueId);
            dataLogE.setField(FIELD_SPRINT);
            dataLogE.setOldValue("".equals(oldSprintIdStr) ? null : oldSprintIdStr);
            dataLogE.setOldString("".equals(oldSprintNameStr) ? null : oldSprintNameStr);
            dataLogE.setNewValue("".equals(newSprintIdStr) ? null : newSprintIdStr);
            dataLogE.setNewString("".equals(newSprintNameStr) ? null : newSprintNameStr);
            dataLogEList.add(dataLogE);
        }
        return dataLogEList;
    }

    private void dataLogSprintByMove(List<DataLogE> dataLogEList) {
        for (DataLogE dataLogE : dataLogEList) {
            dataLogRepository.create(dataLogE);
        }
    }

    @Override
    public List<IssueSearchDTO> batchIssueToSprint(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO) {
        sprintRule.judgeExist(projectId, sprintId);
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        if (moveIssueDTO.getBefore()) {
            beforeRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
            dataLogRank(projectId, moveIssueDTO, RANK_HIGHER, sprintId);
        } else {
            afterRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
            dataLogRank(projectId, moveIssueDTO, RANK_LOWER, sprintId);
        }
        List<Long> moveIssueIds = moveIssueDTO.getIssueIds();
        moveIssueIds.addAll(issueMapper.querySubIssueIds(projectId, moveIssueIds));
        List<DataLogE> dataLogEList = getSprintDataLogByMove(projectId, sprintId, moveIssueDTO);
        issueRepository.removeIssueFromSprintByIssueIds(projectId, moveIssueIds);
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIds, new Date());
        }
        dataLogSprintByMove(dataLogEList);
        issueRepository.batchUpdateIssueRank(projectId, moveIssueDOS);
        List<IssueSearchDO> issueSearchDOList = issueMapper.queryIssueByIssueIds(projectId, moveIssueDTO.getIssueIds());
        List<Long> assigneeIds = issueSearchDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        return issueSearchAssembler.doListToDTO(issueSearchDOList, usersMap);
    }

    private void beforeRank(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueDTO.setIssueIds(issueMapper.queryIssueIdOrderByRankDesc(projectId, moveIssueDTO.getIssueIds()));
        if (moveIssueDTO.getOutsetIssueId() == null || Objects.equals(moveIssueDTO.getOutsetIssueId(), 0L)) {
            noOutsetBeforeRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
        } else {
            outsetBeforeRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
        }
    }

    private void outsetBeforeRank(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        String rightRank = issueMapper.queryRank(projectId, sprintId, moveIssueDTO.getOutsetIssueId());
        String leftRank = issueMapper.queryLeftRank(projectId, sprintId, rightRank);
        if (leftRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                rightRank = RankUtil.genPre(rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                rightRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        }
    }

    private void noOutsetBeforeRank(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        String minRank = sprintMapper.queryMinRank(projectId, sprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
            }
        }
    }

    private void afterRank(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueDTO.setIssueIds(issueMapper.queryIssueIdOrderByRankAsc(projectId, moveIssueDTO.getIssueIds()));
        String leftRank = issueMapper.queryRank(projectId, sprintId, moveIssueDTO.getOutsetIssueId());
        String rightRank = issueMapper.queryRightRank(projectId, sprintId, leftRank);
        if (rightRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                leftRank = RankUtil.genNext(leftRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                leftRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        }
    }

    @Override
    public List<IssueEpicDTO> listEpicSelectData(Long projectId) {
        return issueAssembler.doListToEpicDto(issueMapper.queryIssueEpicSelectList(projectId));
    }

    @Override
    public IssueSubDTO queryIssueSub(Long projectId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        return issueAssembler.issueDetailDoToIssueSubDto(issue);
    }

    private void dataLogIssueType(String originType, IssueUpdateTypeDTO issueUpdateTypeDTO) {
        String originTypeName = lookupValueMapper.selectNameByValueCode(originType);
        String currentTypeName = lookupValueMapper.selectNameByValueCode(issueUpdateTypeDTO.getTypeCode());
        DataLogE dataLogE = new DataLogE();
        dataLogE.setField(FIELD_ISSUETYPE);
        dataLogE.setIssueId(issueUpdateTypeDTO.getIssueId());
        dataLogE.setProjectId(issueUpdateTypeDTO.getProjectId());
        dataLogE.setOldString(originTypeName);
        dataLogE.setNewString(currentTypeName);
        dataLogRepository.create(dataLogE);
    }

    @Override
    public IssueDTO updateIssueTypeCode(IssueE issueE, IssueUpdateTypeDTO issueUpdateTypeDTO) {
        String originType = issueE.getTypeCode();
        if (issueUpdateTypeDTO.getTypeCode().equals(ISSUE_EPIC)) {
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueE.setEpicName(issueUpdateTypeDTO.getEpicName());
            List<LookupValueDO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueE.initializationColor(colorList);
            //保存日志
            handleChangeStoryTypeIssue(issueE);
            handleChangeRemainTimeIssue(issueE);
            issueE.setEpicId(0L);
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, FIELD_STORY_POINTS});
        } else if (issueE.getTypeCode().equals(ISSUE_EPIC)) {
            //如果之前类型是epic，会把该epic下的issue的epicId置为0
            //todo epicId改变是否记录日志
            issueRepository.batchUpdateIssueEpicId(issueE.getProjectId(), issueE.getIssueId());
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueE.setColorCode(null);
            issueE.setEpicName(null);
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, EPIC_NAME_FIELD, COLOR_CODE_FIELD});
        } else {
            handleChangeStoryTypeIssue(issueE);
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD});
        }
        dataLogIssueType(originType, issueUpdateTypeDTO);
        return queryIssue(issueE.getProjectId(), issueE.getIssueId());
    }

    private void handleChangeRemainTimeIssue(IssueE issueE) {
        if (issueE.getRemainingTime() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(issueE.getProjectId());
            dataLogE.setIssueId(issueE.getIssueId());
            dataLogE.setField(FIELD_TIMEESTIMATE);
            dataLogE.setOldValue(issueE.getRemainingTime().toString());
            dataLogE.setOldString(issueE.getRemainingTime().toString());
            dataLogE.setNewValue(null);
            dataLogE.setNewString(null);
            dataLogRepository.create(dataLogE);
        }
    }

    @Override
    public IssueE queryIssueByProjectIdAndIssueId(Long projectId, Long issueId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setProjectId(projectId);
        issueDO.setIssueId(issueId);
        return ConvertHelper.convert(issueMapper.selectOne(issueDO), IssueE.class);
    }

    private void handleCreateLabelIssue(List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId) {
        if (labelIssueRelDTOList != null && !labelIssueRelDTOList.isEmpty()) {
            List<LabelIssueRelE> labelIssueEList = ConvertHelper.convertList(labelIssueRelDTOList, LabelIssueRelE.class);
            labelIssueEList.forEach(labelIssueRelE -> {
                labelIssueRelE.setIssueId(issueId);
                handleLabelIssue(labelIssueRelE);
            });
        }
    }

    private void handleCreateVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList, Long projectId, Long issueId) {
        if (versionIssueRelDTOList != null && !versionIssueRelDTOList.isEmpty()) {
            //创建版本默认为fix
//            versionIssueRelDTOList.forEach(versionIssueRelDTO -> versionIssueRelDTO.setRelationType(RELATION_TYPE_FIX));
            handleVersionIssueRel(ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class), projectId, issueId, "fix");
        }
    }

    private void handleVersionIssueRel(List<VersionIssueRelE> versionIssueRelEList, Long projectId, Long issueId, String versionType) {
        versionIssueRelEList.forEach(versionIssueRelE -> {
            versionIssueRelE.setIssueId(issueId);
            versionIssueRelE.setProjectId(projectId);
            versionIssueRelE.setRelationType(versionType);
            issueRule.verifyVersionIssueRelData(versionIssueRelE);
            if (versionIssueRelE.getName() != null && versionIssueRelE.getVersionId() == null) {
                //重名校验
                ProductVersionE productVersionE = versionIssueRelE.createProductVersionE();
                if (productVersionMapper.isRepeatName(productVersionE.getProjectId(), productVersionE.getName())) {
                    versionIssueRelE.setVersionId(productVersionMapper.queryVersionIdByNameAndProjectId(productVersionE.getName(), productVersionE.getProjectId()));
                } else {
                    productVersionE = productVersionRepository.createVersion(productVersionE);
                    versionIssueRelE.setVersionId(productVersionE.getVersionId());
                }
            }
            if (issueRule.existVersionIssueRel(versionIssueRelE)) {
                versionIssueRelRepository.create(versionIssueRelE);
            }
        });
    }

    private void handleCreateComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList, Long projectId, Long issueId) {
        if (componentIssueRelDTOList != null && !componentIssueRelDTOList.isEmpty()) {
            handleComponentIssueRel(ConvertHelper.convertList(componentIssueRelDTOList, ComponentIssueRelE.class), projectId, issueId);
        }
    }

    private void handleComponentIssueRel(List<ComponentIssueRelE> componentIssueRelEList, Long projectId, Long issueId) {
        componentIssueRelEList.forEach(componentIssueRelE -> {
            componentIssueRelE.setIssueId(issueId);
            componentIssueRelE.setProjectId(projectId);
            issueRule.verifyComponentIssueRelData(componentIssueRelE);
            //重名校验
            if (componentIssueRelE.getName() != null && componentIssueRelE.getComponentId() == null) {
                if (issueComponentMapper.checkNameExist(componentIssueRelE.getName(), componentIssueRelE.getProjectId())) {
                    componentIssueRelE.setComponentId(issueComponentMapper.queryComponentIdByNameAndProjectId(
                            componentIssueRelE.getName(), componentIssueRelE.getProjectId()));
                } else {
                    IssueComponentE issueComponentE = componentIssueRelE.createIssueComponent();
                    issueComponentE = issueComponentRepository.create(issueComponentE);
                    componentIssueRelE.setComponentId(issueComponentE.getComponentId());
                }
            }
            //issue经办人可以根据模块策略进行区分
            handleComponentIssue(componentIssueRelE, issueId);
            if (issueRule.existComponentIssueRel(componentIssueRelE)) {
                componentIssueRelRepository.create(componentIssueRelE);
            }
        });
    }

    private void handleComponentIssue(ComponentIssueRelE componentIssueRelE, Long issueId) {
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentMapper.selectByPrimaryKey(
                componentIssueRelE.getComponentId()), IssueComponentE.class);
        if (ISSUE_MANAGER_TYPE.equals(issueComponentE.getDefaultAssigneeRole()) && issueComponentE.getManagerId() !=
                null && issueComponentE.getManagerId() != 0) {
            //如果模块有选择模块负责人或者经办人的话，对应的issue的负责人要修改
            IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
            if (issueE.getAssigneeId() == null || issueE.getAssigneeId() == 0) {
                issueE.setAssigneeId(issueComponentE.getManagerId());
                issueRepository.update(issueE, new String[]{"assigneeId"});
            }
        }
    }

    private String getOriginLabelNames(List<IssueLabelDO> originLabels) {
        StringBuilder originLabelNames = new StringBuilder();
        int originIdx = 0;
        for (IssueLabelDO label : originLabels) {
            if (originIdx == originLabels.size() - 1) {
                originLabelNames.append(label.getLabelName());
            } else {
                originLabelNames.append(label.getLabelName() + " ");
            }
        }
        return originLabelNames.toString();
    }

    private String getCurrentLabelNames(List<LabelIssueRelDTO> labelIssueRelDTOList) {
        StringBuilder stringBuilder = new StringBuilder();
        int idx = 0;
        for (LabelIssueRelDTO label : labelIssueRelDTOList) {
            if (idx == labelIssueRelDTOList.size() - 1) {
                stringBuilder.append(label.getLabelName());
            } else {
                stringBuilder.append(label.getLabelName() + " ");
            }
        }
        return stringBuilder.toString();
    }

    private void dataLogLabel(List<IssueLabelDO> originLabels, List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId) {
        if (labelIssueRelDTOList != null) {
            IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(issueDO.getProjectId());
            dataLogE.setField(FIELD_LABELS);
            dataLogE.setIssueId(issueId);
            if (!labelIssueRelDTOList.isEmpty()) {
                dataLogE.setOldString(getOriginLabelNames(originLabels));
                dataLogE.setNewString(getCurrentLabelNames(labelIssueRelDTOList));
            } else {
                dataLogE.setOldString(getOriginLabelNames(originLabels));
            }
            dataLogRepository.create(dataLogE);
        }
    }

    private void handleUpdateLabelIssue(List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId) {
        if (labelIssueRelDTOList != null) {
            List<IssueLabelDO> originLabels = issueMapper.selectLabelNameByIssueId(issueId);
            if (!labelIssueRelDTOList.isEmpty()) {
                labelIssueRelRepository.deleteByIssueId(issueId);
                List<LabelIssueRelE> labelIssueEList = ConvertHelper.convertList(labelIssueRelDTOList, LabelIssueRelE.class);
                labelIssueEList.forEach(labelIssueRelE -> {
                    labelIssueRelE.setIssueId(issueId);
                    handleLabelIssue(labelIssueRelE);
                });
            } else {
                labelIssueRelRepository.deleteByIssueId(issueId);
            }
            dataLogLabel(originLabels, labelIssueRelDTOList, issueId);
            //没有issue使用的标签进行垃圾回收
            issueLabelRepository.labelGarbageCollection();
        }

    }

    private void dataLogVersion(Long projectId, Long issueId, List<VersionIssueRelDO> versionIssueRelDOList, List<VersionIssueRelDTO> versionIssueRelDTOList, String versionType) {
        if (!"fix".equals(versionType)) {
            return;
        }
        if (versionIssueRelDOList != null && !versionIssueRelDOList.isEmpty()) {
            for (VersionIssueRelDO versionIssueRel : versionIssueRelDOList) {
                int flag = 0;
                for (VersionIssueRelDTO versionRel : versionIssueRelDTOList) {
                    if (versionRel.getVersionId().equals(versionIssueRel.getVersionId())) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    DataLogE dataLogE = new DataLogE();
                    dataLogE.setProjectId(projectId);
                    dataLogE.setIssueId(issueId);
                    dataLogE.setField(FIELD_FIX_VERSION);
                    dataLogE.setOldValue(versionIssueRel.getVersionId().toString());
                    dataLogE.setOldString(productVersionMapper.selectByPrimaryKey(versionIssueRel.getVersionId()).getName());
                    dataLogRepository.create(dataLogE);
                }
            }
        }
        if (versionIssueRelDTOList != null && !versionIssueRelDTOList.isEmpty()) {
            for (VersionIssueRelDTO versionRel : versionIssueRelDTOList) {
                int flag = 0;
                for (VersionIssueRelDO versionIssueRel : versionIssueRelDOList) {
                    if (versionRel.getVersionId().equals(versionIssueRel.getVersionId())) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    DataLogE dataLogE = new DataLogE();
                    dataLogE.setProjectId(projectId);
                    dataLogE.setIssueId(issueId);
                    dataLogE.setField(FIELD_FIX_VERSION);
                    dataLogE.setNewValue(versionRel.getVersionId().toString());
                    dataLogE.setNewString(productVersionMapper.selectByPrimaryKey(versionRel.getVersionId()).getName());
                    dataLogRepository.create(dataLogE);
                }
            }
        }

    }

    public List<VersionIssueRelDO> getVersionIssueRels(Long projectId, Long issueId) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        versionIssueRelDO.setProjectId(projectId);
        versionIssueRelDO.setIssueId(issueId);
        versionIssueRelDO.setRelationType("fix");
        return versionIssueRelMapper.select(versionIssueRelDO);
    }

    private void handleUpdateVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList, Long projectId, Long issueId, String versionType) {
        if (versionIssueRelDTOList != null) {
            List<VersionIssueRelDO> originVersionIssueRels = getVersionIssueRels(projectId, issueId);
            List<VersionIssueRelDTO> versionIssueRelDTOS = null;
            if (!versionIssueRelDTOList.isEmpty()) {
                versionIssueRelRepository.deleteByIssueIdAndType(issueId, versionType);
                handleVersionIssueRel(ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class), projectId, issueId, versionType);
                versionIssueRelDTOS = ConvertHelper.convertList(getVersionIssueRels(projectId, issueId), VersionIssueRelDTO.class);
            } else {
                versionIssueRelRepository.deleteByIssueIdAndType(issueId, versionType);
                versionIssueRelDTOS = new ArrayList<>();
            }
            dataLogVersion(projectId, issueId, originVersionIssueRels, versionIssueRelDTOS, versionType);
        }

    }

    private List<ComponentIssueRelDO> getComponentIssueRel(Long projectId, Long issueId) {
        return componentIssueRelMapper.selectByProjectIdAndIssueId(projectId, issueId);
    }

    private void dataLogComponent(Long projectId, Long issueId, List<ComponentIssueRelDO> originComponentIssueRels, List<ComponentIssueRelDO> curComponentIssueRels) {
        if (originComponentIssueRels != null && !originComponentIssueRels.isEmpty()) {
            for (ComponentIssueRelDO originRel : originComponentIssueRels) {
                int flag = 0;
                for (ComponentIssueRelDO curRel : curComponentIssueRels) {
                    if (originRel.getComponentId().equals(curRel.getComponentId())) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    DataLogE dataLogE = new DataLogE();
                    dataLogE.setProjectId(projectId);
                    dataLogE.setIssueId(issueId);
                    dataLogE.setField(FIELD_COMPONENT);
                    dataLogE.setOldValue(originRel.getComponentId().toString());
                    dataLogE.setOldString(issueComponentMapper.selectByPrimaryKey(originRel.getComponentId()).getName());
                    dataLogRepository.create(dataLogE);
                }
            }
        }
        if (curComponentIssueRels != null && !curComponentIssueRels.isEmpty()) {
            for (ComponentIssueRelDO curRel : curComponentIssueRels) {
                int flag = 0;
                for (ComponentIssueRelDO originRel : originComponentIssueRels) {
                    if (originRel.getComponentId().equals(curRel.getComponentId())) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    DataLogE dataLogE = new DataLogE();
                    dataLogE.setProjectId(projectId);
                    dataLogE.setIssueId(issueId);
                    dataLogE.setField(FIELD_COMPONENT);
                    dataLogE.setNewValue(curRel.getComponentId().toString());
                    dataLogE.setNewString(curRel.getName());
                    dataLogRepository.create(dataLogE);
                }
            }
        }
    }

    private void handleUpdateComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList, Long projectId, Long issueId) {
        if (componentIssueRelDTOList != null) {
            List<ComponentIssueRelDO> originComponentIssueRels = getComponentIssueRel(projectId, issueId);
            if (!componentIssueRelDTOList.isEmpty()) {
                componentIssueRelRepository.deleteByIssueId(issueId);
                handleComponentIssueRel(ConvertHelper.convertList(componentIssueRelDTOList, ComponentIssueRelE.class), projectId, issueId);
            } else {
                componentIssueRelRepository.deleteByIssueId(issueId);
            }
            List<ComponentIssueRelDO> curComponentIssueRels = getComponentIssueRel(projectId, issueId);
            dataLogComponent(projectId, issueId, originComponentIssueRels, curComponentIssueRels);
        }
    }

    private void handleLabelIssue(LabelIssueRelE labelIssueRelE) {
        issueRule.verifyLabelIssueData(labelIssueRelE);
        if (labelIssueRelE.getLabelName() != null && labelIssueRelE.getLabelId() == null) {
            //重名校验
            if (issueLabelMapper.checkNameExist(labelIssueRelE.getLabelName(), labelIssueRelE.getProjectId())) {
                labelIssueRelE.setLabelId(issueLabelMapper.queryLabelIdByLabelNameAndProjectId(labelIssueRelE.getLabelName(), labelIssueRelE.getProjectId()));
            } else {
                IssueLabelE issueLabelE = labelIssueRelE.createIssueLabelE();
                issueLabelE = issueLabelRepository.create(issueLabelE);
                labelIssueRelE.setLabelId(issueLabelE.getLabelId());
            }
        }
        if (issueRule.existLabelIssue(labelIssueRelE)) {
            labelIssueRelRepository.create(labelIssueRelE);
        }
    }

    @Override
    public Page<IssueCommonDTO> listByOptions(Long projectId, String typeCode, PageRequest pageRequest) {
        Page<IssueCommonDO> issueCommonDOPage = PageHelper.doPageAndSort(pageRequest, () -> issueMapper.listByOptions(projectId, typeCode));
        Page<IssueCommonDTO> issueCommonDTOPage = new Page<>();
        issueCommonDTOPage.setTotalPages(issueCommonDOPage.getTotalPages());
        issueCommonDTOPage.setSize(issueCommonDOPage.getSize());
        issueCommonDTOPage.setTotalElements(issueCommonDOPage.getTotalElements());
        issueCommonDTOPage.setNumberOfElements(issueCommonDOPage.getNumberOfElements());
        issueCommonDTOPage.setNumber(issueCommonDOPage.getNumber());
        issueCommonDTOPage.setContent(issueCommonAssembler.issueCommonToIssueCommonDto(issueCommonDOPage.getContent()));
        return issueCommonDTOPage;
    }

    @Override
    public Page<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String content, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.resetOrder("ai", new HashMap<>());
        Page<IssueNumDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueByOption(projectId, issueId, content));
        Page<IssueNumDTO> issueListDTOPage = new Page<>();
        issueListDTOPage.setNumber(issueDOPage.getNumber());
        issueListDTOPage.setNumberOfElements(issueDOPage.getNumberOfElements());
        issueListDTOPage.setSize(issueDOPage.getSize());
        issueListDTOPage.setTotalElements(issueDOPage.getTotalElements());
        issueListDTOPage.setTotalPages(issueDOPage.getTotalPages());
        issueListDTOPage.setContent(issueAssembler.issueNumDOToIssueNumDTO(issueDOPage.getContent()));
        return issueListDTOPage;
    }

    @Override
    public void exportIssues(Long projectId, HttpServletRequest request, HttpServletResponse response) {
        String charsetName = "UTF-8";
        if (request.getHeader("User-Agent").contains("Firefox")) {
            charsetName = "GB2312";
        }
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        projectInfoDO = projectInfoMapper.selectOne(projectInfoDO);
        if (projectInfoDO == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        List<ExportIssuesDTO> exportIssues = issueAssembler.exportIssuesDOListToExportIssuesDTO(issueMapper.queryExportIssues(projectId));
        List<Long> issueIds = exportIssues.stream().map(ExportIssuesDTO::getIssueId).collect(Collectors.toList());
        if (!issueIds.isEmpty()) {
            Map<Long, List<SprintNameDO>> closeSprintNames = issueMapper.querySprintNameByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(SprintNameDO::getIssueId));
            Map<Long, List<VersionIssueRelDO>> fixVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, FIX_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
            Map<Long, List<VersionIssueRelDO>> influenceVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, INFLUENCE_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
            exportIssues = exportIssues.stream().map(exportIssue -> {
                String closeSprintName = closeSprintNames.get(exportIssue.getIssueId()) != null ? closeSprintNames.get(exportIssue.getIssueId()).stream().map(SprintNameDO::getSprintName).collect(Collectors.joining(",")) : "";
                String fixVersionName = fixVersionNames.get(exportIssue.getIssueId()) != null ? fixVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                String influenceVersionName = influenceVersionNames.get(exportIssue.getIssueId()) != null ? influenceVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                exportIssue.setCloseSprintName(closeSprintName);
                exportIssue.setFixVersionName(fixVersionName);
                exportIssue.setInfluenceVersionName(influenceVersionName);
                return exportIssue;
            }).collect(Collectors.toList());
        }
        HSSFWorkbook workbook = exportIssuesXls(projectInfoDO, exportIssues);
        String fileName = projectInfoDO.getProjectCode();
        dowloadExcel(workbook, fileName, charsetName, response);
    }

    @Override
    public void exportIssue(Long projectId, Long issueId, HttpServletRequest request, HttpServletResponse response) {
        String charsetName = "UTF-8";
        if (request.getHeader("User-Agent").contains("Firefox")) {
            charsetName = "GB2312";
        }
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        projectInfoDO = projectInfoMapper.selectOne(projectInfoDO);
        if (projectInfoDO == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        ExportIssuesDTO exportIssue = issueAssembler.exportIssuesDOToExportIssuesDTO(issueMapper.queryExportIssue(projectId, issueId));
        HSSFWorkbook workbook = new HSSFWorkbook();
        String fileName = projectInfoDO.getProjectCode();
        if (exportIssue != null) {
            String componentName = issueMapper.queryComponentNameByIssueId(projectId, issueId).stream().collect(Collectors.joining(","));
            String labelName = issueMapper.queryLabelNameByIssueId(projectId, issueId).stream().collect(Collectors.joining(","));
            List<ExportIssuesDTO> subIssues = issueAssembler.exportIssuesDOListToExportIssuesDTO(issueMapper.querySubIssuesByIssueId(projectId, issueId));
            String closeSprintName = issueMapper.querySprintNameByIssueId(issueId).stream().map(SprintNameDO::getSprintName).collect(Collectors.joining(","));
            String fixVersionName = issueMapper.queryVersionNameByIssueId(projectId, issueId, FIX_RELATION_TYPE).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(","));
            String influenceVersionName = issueMapper.queryVersionNameByIssueId(projectId, issueId, INFLUENCE_RELATION_TYPE).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(","));
            exportIssue.setComponentName(componentName);
            exportIssue.setComponentName(labelName);
            exportIssue.setCloseSprintName(closeSprintName);
            exportIssue.setFixVersionName(fixVersionName);
            exportIssue.setInfluenceVersionName(influenceVersionName);
            exportIssueXls(workbook, projectInfoDO, exportIssue, subIssues);
            fileName = fileName + "-" + exportIssue.getIssueNum();
        }
        dowloadExcel(workbook, fileName, charsetName, response);
    }

    @Override
    public IssueDTO copyIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO) {
        IssueDetailDO issueDetailDO = issueMapper.queryIssueDetail(projectId, issueId);
        if (issueDetailDO != null) {
            issueDetailDO.setSummary(copyConditionDTO.getSummary());
            IssueCreateDTO issueCreateDTO = issueAssembler.issueDtoToIssueCreateDto(issueDetailDO);
            IssueDTO newIssue = createIssue(issueCreateDTO);
            //生成一条复制的关联
            IssueLinkTypeDO query = new IssueLinkTypeDO();
            query.setProjectId(issueDetailDO.getProjectId());
            query.setOutWard("复制");
            IssueLinkTypeDO issueLinkTypeDO = issueLinkTypeMapper.selectOne(query);
            if (issueLinkTypeDO != null) {
                createCopyIssueLink(issueDetailDO.getIssueId(), newIssue.getIssueId(), issueLinkTypeDO.getLinkTypeId());
            }
            //复制故事点和剩余工作量并记录日志
            IssueUpdateDTO issueUpdateDTO = new IssueUpdateDTO();
            issueUpdateDTO.setStoryPoints(issueDetailDO.getStoryPoints());
            issueUpdateDTO.setRemainingTime(issueDetailDO.getRemainingTime());
            issueUpdateDTO.setIssueId(newIssue.getIssueId());
            issueUpdateDTO.setObjectVersionNumber(newIssue.getObjectVersionNumber());
            updateIssue(projectId, issueUpdateDTO, Lists.newArrayList("storyPoints", "remainingTime"));
            //复制链接
            batchCreateCopyIssueLink(copyConditionDTO.getIssueLink(), issueId, newIssue.getIssueId(), projectId);
            //复制冲刺
            handleCreateCopyIssueSprintRel(copyConditionDTO.getSprintValues(), issueId, newIssue.getIssueId(), projectId);
            if (copyConditionDTO.getSubTask()) {
                List<IssueDO> subIssueDOList = issueDetailDO.getSubIssueDOList();
                if (subIssueDOList != null && !subIssueDOList.isEmpty()) {
                    subIssueDOList.forEach(issueDO -> {
                        IssueDetailDO subIssueDetailDO = issueMapper.queryIssueDetail(issueDO.getProjectId(), issueDO.getIssueId());
                        IssueSubCreateDTO issueSubCreateDTO = issueAssembler.issueDtoToSubIssueCreateDto(subIssueDetailDO, newIssue.getIssueId());
                        IssueSubDTO newSubIssue = createSubIssue(issueSubCreateDTO);
                        //生成一条复制的关联
                        if (issueLinkTypeDO != null) {
                            createCopyIssueLink(subIssueDetailDO.getIssueId(), newSubIssue.getIssueId(), issueLinkTypeDO.getLinkTypeId());
                        }
                        //复制链接
                        batchCreateCopyIssueLink(copyConditionDTO.getIssueLink(), issueDO.getIssueId(), newSubIssue.getIssueId(), projectId);
                        //复制冲刺
                        handleCreateCopyIssueSprintRel(copyConditionDTO.getSprintValues(), issueDO.getIssueId(), newSubIssue.getIssueId(), projectId);
                        //复制剩余工作量并记录日志
                        IssueUpdateDTO subIssueUpdateDTO = new IssueUpdateDTO();
                        subIssueUpdateDTO.setRemainingTime(issueDO.getRemainingTime());
                        subIssueUpdateDTO.setIssueId(newSubIssue.getIssueId());
                        subIssueUpdateDTO.setObjectVersionNumber(newSubIssue.getObjectVersionNumber());
                        updateIssue(projectId, subIssueUpdateDTO, Lists.newArrayList("remainingTime"));
                    });
                }
            }
            return queryIssue(projectId, newIssue.getIssueId());
        } else {
            throw new CommonException("error.issue.copyIssueByIssueId");
        }
    }

    private void handleCreateCopyIssueSprintRel(Boolean sprintValues, Long issueId, Long newIssueId, Long projectId) {
        if (sprintValues) {
            IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO();
            issueSprintRelDO.setIssueId(issueId);
            issueSprintRelDO.setProjectId(projectId);
            List<IssueSprintRelDO> issueSprintRelDOList = issueSprintRelMapper.select(issueSprintRelDO);
            issueSprintRelDOList.parallelStream().forEach(createIssueSprintRel -> {
                createIssueSprintRel.setIssueId(newIssueId);
                createIssueSprintRel.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
                createIssueSprintRel.setObjectVersionNumber(null);
                createIssueSprintRel.setCreationDate(new Date());
                issueSprintRelRepository.createIssueSprintRel(createIssueSprintRel);
            });
        }
    }

    private void batchCreateCopyIssueLink(Boolean condition, Long issueId, Long newIssueId, Long projectId) {
        if (condition) {
            List<IssueLinkE> issueLinkEList = ConvertHelper.convertList(issueLinkMapper.queryIssueLinkByIssueId(issueId, projectId), IssueLinkE.class);
            issueLinkEList.parallelStream().forEach(issueLinkE -> {
                if (issueLinkE.getIssueId().equals(issueId)) {
                    issueLinkE.setIssueId(newIssueId);
                }
                if (issueLinkE.getLinkedIssueId().equals(issueId)) {
                    issueLinkE.setLinkedIssueId(newIssueId);
                }
                issueLinkE.setObjectVersionNumber(null);
                issueLinkE.setLinkId(null);
                issueLinkRepository.create(issueLinkE);
            });
        }
    }

    private void createCopyIssueLink(Long issueId, Long newIssueId, Long linkTypeId) {
        IssueLinkE issueLinkE = new IssueLinkE();
        issueLinkE.setLinkedIssueId(issueId);
        issueLinkE.setLinkTypeId(linkTypeId);
        issueLinkE.setIssueId(newIssueId);
        issueLinkRepository.create(issueLinkE);
    }

    @Override
    public IssueSubDTO transformedSubTask(Long projectId, IssueTransformSubTask issueTransformSubTask) {
        IssueE issueE = ConvertHelper.convert(issueMapper.queryIssueByIssueId(projectId, issueTransformSubTask.getIssueId()), IssueE.class);
        if (issueE != null) {
            if (!issueE.getTypeCode().equals(SUB_TASK)) {
                String originTypeCode = issueE.getTypeCode();
                issueE.setTypeCode(SUB_TASK);
                issueE.setRank(null);
                issueE.setParentIssueId(issueTransformSubTask.getParentIssueId());
                //日志记录故事点
                handleChangeStoryTypeIssue(issueE);
                //日志记录状态
                IssueDO issueDO = new IssueDO();
                issueDO.setStatusId(issueE.getStatusId());
                issueDO.setProjectId(issueE.getProjectId());
                issueE.setStatusId(issueTransformSubTask.getStatusId());
                boardService.dataLogStatus(issueDO, issueE);
                //日志记录issue类型变化
                String originTypeName = lookupValueMapper.selectNameByValueCode(originTypeCode);
                String currentTypeName = lookupValueMapper.selectNameByValueCode(issueE.getTypeCode());
                DataLogE dataLogE = new DataLogE();
                dataLogE.setField(FIELD_ISSUETYPE);
                dataLogE.setIssueId(issueE.getIssueId());
                dataLogE.setProjectId(issueE.getProjectId());
                dataLogE.setOldString(originTypeName);
                dataLogE.setNewString(currentTypeName);
                dataLogRepository.create(dataLogE);
                return queryIssueSub(projectId, issueE.getIssueId());
            } else {
                throw new CommonException("error.IssueRule.subTaskError");
            }
        } else {
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }

    private void handleChangeStoryTypeIssue(IssueE issueE) {
        if (STORY_TYPE.equals(issueE.getTypeCode()) && issueE.getStoryPoints() != null) {
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(issueE.getProjectId());
            dataLogE.setIssueId(issueE.getIssueId());
            dataLogE.setField(FIELD_STORY_POINTS);
            if (issueE.getStoryPoints() != null) {
                dataLogE.setOldString(issueE.getStoryPoints().toString());
            }
            dataLogE.setNewString(null);
            dataLogRepository.create(dataLogE);
            issueE.setStoryPoints(null);
        }
    }

    private void dowloadExcel(HSSFWorkbook workbook, String fileName, String charsetName, HttpServletResponse response) {
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/ms-excel;charset=utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((fileName + ".xls").getBytes(charsetName),
                    "ISO-8859-1"));
        } catch (UnsupportedEncodingException e1) {
            throw new CommonException(EXPORT_ERROR);
        }
        response.setCharacterEncoding("utf-8");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            workbook.write(out);
        } catch (final IOException e) {
            throw new CommonException(EXPORT_ERROR);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new CommonException(EXPORT_ERROR);
            }
        }
    }

    private void exportIssueXls(HSSFWorkbook workbook, ProjectInfoDO projectInfoDO, ExportIssuesDTO exportIssue, List<ExportIssuesDTO> subIssues) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String issueNum = projectInfoDO.getProjectCode() + "-" + exportIssue.getIssueNum();
        HSSFSheet sheet = workbook.createSheet(issueNum);
        int lastRow = 13;
        if (!subIssues.isEmpty()) {
            lastRow = subIssues.size() + 14;
            sheet.addMergedRegion(new CellRangeAddress(14, lastRow, 0, 0));
        }
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(issueNum);
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getSummary());
        cell = row.createCell(2);
        cell.setCellValue("创建日期:" + dateFormat.format(exportIssue.getCreationDate()));
        cell = row.createCell(3);
        cell.setCellValue("更新日期:" + dateFormat.format(exportIssue.getLastUpdateDate()));

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("状态:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getStatusName());

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("项目:");
        cell = row.createCell(1);
        cell.setCellValue(projectInfoDO.getProjectCode());

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("模块:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getComponentName());

        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("影响版本:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getInfluenceVersionName());

        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("修复的版本:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getFixVersionName());

        sheet.createRow(6);

        row = sheet.createRow(7);
        cell = row.createCell(0);
        cell.setCellValue("类型:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getTypeName());
        cell = row.createCell(2);
        cell.setCellValue("优先级:");
        cell = row.createCell(3);
        cell.setCellValue(exportIssue.getPriorityName());

        row = sheet.createRow(8);
        cell = row.createCell(0);
        cell.setCellValue("报告人:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getReporterName());
        cell = row.createCell(2);
        cell.setCellValue("经办人:");
        cell = row.createCell(3);
        cell.setCellValue(exportIssue.getAssigneeName());

        row = sheet.createRow(9);
        cell = row.createCell(0);
        cell.setCellValue("解决结果:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getSolution());

        row = sheet.createRow(10);
        cell = row.createCell(0);
        cell.setCellValue("标签:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getLabelName());

        row = sheet.createRow(11);
        cell = row.createCell(0);
        cell.setCellValue("Σ预估剩余时间:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getSumRemainingTime() != null ? exportIssue.getSumRemainingTime().toString() : "");
        cell = row.createCell(2);
        cell.setCellValue("剩余的估算:");
        cell = row.createCell(3);
        cell.setCellValue(exportIssue.getRemainingTime() != null ? exportIssue.getRemainingTime().toString() : "");

        row = sheet.createRow(12);
        cell = row.createCell(0);
        cell.setCellValue("Σ原预估时间:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getSumEstimateTime() != null ? exportIssue.getSumEstimateTime().toString() : "");
        cell = row.createCell(2);
        cell.setCellValue("初始预估:");
        cell = row.createCell(3);
        cell.setCellValue(exportIssue.getEstimateTime() != null ? exportIssue.getEstimateTime().toString() : "");

        sheet.createRow(13);

        if (!subIssues.isEmpty()) {
            row = sheet.createRow(14);
            cell = row.createCell(0);
            cell.setCellValue("子任务:");
            for (int i = 0; i < SUB_COLUMN_NAMES.length; i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(SUB_COLUMN_NAMES[i]);
            }
            for (int i = 0; i < subIssues.size(); i++) {
                row = sheet.createRow(i + 15);
                for (int j = 0; j < SUB_COLUMN_NAMES.length; j++) {
                    cell = row.createCell(j + 1);
                    switch (SUB_COLUMN_NAMES[j]) {
                        case "关键字":
                            cell.setCellValue(projectInfoDO.getProjectCode() + "-" + subIssues.get(i).getIssueNum());
                            break;
                        case "概述":
                            cell.setCellValue(subIssues.get(i).getSummary());
                            break;
                        case "类型":
                            cell.setCellValue(subIssues.get(i).getTypeName());
                            break;
                        case "状态":
                            cell.setCellValue(subIssues.get(i).getStatusName());
                            break;
                        case "经办人":
                            cell.setCellValue(subIssues.get(i).getAssigneeName());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        row = sheet.createRow(lastRow + 1);
        cell = row.createCell(0);
        cell.setCellValue("Epic Link:");
        cell = row.createCell(1);
        cell.setCellValue(exportIssue.getEpicName());

        row = sheet.createRow(lastRow + 2);
        cell = row.createCell(0);
        cell.setCellValue("Sprint:");
        cell = row.createCell(1);
        String sprintName = exportIssue.getSprintName() != null ? "正在使用冲刺:" + exportIssue.getSprintName() + " " : "";
        sprintName = sprintName + (!Objects.equals(exportIssue.getCloseSprintName(), "") ? "已关闭冲刺:" + exportIssue.getCloseSprintName() : "");
        cell.setCellValue(sprintName);

        row = sheet.createRow(lastRow + 3);
        cell = row.createCell(0);
        cell.setCellValue(customUserDetails.getUsername() + "于" + dateFormat.format(new Date()) + "导出");
    }

    private HSSFWorkbook exportIssuesXls(ProjectInfoDO projectInfoDO, List<ExportIssuesDTO> exportIssues) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(projectInfoDO.getProjectCode());
        HSSFRow row = sheet.createRow(0);

        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(COLUMN_NAMES[i]);
        }

        for (int i = 0; i < exportIssues.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < COLUMN_NAMES.length; j++) {
                HSSFCell cell = row.createCell(j);
                switch (COLUMN_NAMES[j]) {
                    case "编码":
                        cell.setCellValue(exportIssues.get(i).getIssueNum());
                        break;
                    case "概述":
                        cell.setCellValue(exportIssues.get(i).getSummary());
                        break;
                    case "类型":
                        cell.setCellValue(exportIssues.get(i).getTypeName());
                        break;
                    case "所属项目":
                        cell.setCellValue(projectInfoDO.getProjectCode());
                        break;
                    case "经办人":
                        cell.setCellValue(exportIssues.get(i).getAssigneeName());
                        break;
                    case "报告人":
                        cell.setCellValue(exportIssues.get(i).getReporterName());
                        break;
                    case "状态":
                        cell.setCellValue(exportIssues.get(i).getStatusName());
                        break;
                    case "描述":
                        cell.setCellValue(exportIssues.get(i).getDescription());
                        break;
                    case "冲刺":
                        StringBuilder sprintName = new StringBuilder(exportIssues.get(i).getSprintName() != null ? "正在使用冲刺:" + exportIssues.get(i).getSprintName() + " " : "");
                        sprintName.append(!Objects.equals(exportIssues.get(i).getCloseSprintName(), "") ? "已关闭冲刺:" + exportIssues.get(i).getCloseSprintName() : "");
                        cell.setCellValue(sprintName.toString());
                        break;
                    case "创建时间":
                        cell.setCellValue(dateFormat.format(exportIssues.get(i).getCreationDate()));
                        break;
                    case "最后更新时间":
                        cell.setCellValue(dateFormat.format(exportIssues.get(i).getLastUpdateDate()));
                        break;
                    case "优先级":
                        cell.setCellValue(exportIssues.get(i).getPriorityName());
                        break;
                    case "是否子任务":
                        cell.setCellValue(exportIssues.get(i).getSubTask());
                        break;
                    case "初始预估":
                        cell.setCellValue(exportIssues.get(i).getEstimateTime() != null ? exportIssues.get(i).getEstimateTime().toString() : "");
                        break;
                    case "剩余预估":
                        cell.setCellValue(exportIssues.get(i).getRemainingTime() != null ? exportIssues.get(i).getRemainingTime().toString() : "");
                        break;
                    case "版本":
                        StringBuilder versionName = new StringBuilder(!Objects.equals(exportIssues.get(i).getFixVersionName(), "") ? "修复的版本:" + exportIssues.get(i).getFixVersionName() + "" : "");
                        versionName.append(!Objects.equals(exportIssues.get(i).getInfluenceVersionName(), "") ? "影响的版本:" + exportIssues.get(i).getInfluenceVersionName() : "");
                        cell.setCellValue(versionName.toString());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

}