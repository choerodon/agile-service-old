package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.IssueAttachmentService;
import io.choerodon.agile.app.service.IssueCommentService;
import io.choerodon.agile.app.service.IssueLinkService;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.domain.agile.rule.ProductVersionRule;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Service
@Transactional(rollbackFor = CommonException.class)
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
    private static final String EPIC_COLOR_TYPE = "epic_color";
    private static final String RELATION_TYPE_FIX = "fix";
    private static final String FIELD_SUMMARY = "summary";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_PRIORITY = "priority";
    private static final String FIELD_ASSIGNEE = "assignee";
    private static final String FIELD_REPORTER = "reporter";
    private static final String FIELD_SPRINT = "Sprint";
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
        //设置issue编号
        initializationIssueNum(issueE);
        issueE.initializationIssue(statusId);
        //如果是epic，初始化颜色
        List<LookupValueDO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
        issueE.initializationColor(colorList);
        if (issueE.isIssueRank()) {
            calculationRank(issueE);
        }
        Long issueId = issueRepository.create(issueE).getIssueId();
        if (issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L)) {
            issueRepository.issueToSprint(issueE.getProjectId(), issueE.getSprintId(), issueId);
        }
        if (issueCreateDTO.getIssueLinkCreateDTOList() != null && !issueCreateDTO.getIssueLinkCreateDTOList().isEmpty()) {
            issueLinkService.createIssueLinkList(issueCreateDTO.getIssueLinkCreateDTOList(), issueId, issueCreateDTO.getProjectId());
        }
        handleCreateLabelIssue(issueCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateComponentIssueRel(issueCreateDTO.getComponentIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId);
        handleCreateVersionIssueRel(issueCreateDTO.getVersionIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId);
        return queryIssue(issueCreateDTO.getProjectId(), issueId);
    }

    private void calculationRank(IssueE issueE) {
        if (sprintRule.hasIssue(issueE.getProjectId(), issueE.getSprintId())) {
            String rank = sprintMapper.queryMaxRank(issueE.getProjectId(), issueE.getSprintId());
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
        pageRequest.resetOrder("ai", new HashMap<>());
        Page<IssueDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueListWithoutSub(projectId, searchDTO.getSearchArgs(), searchDTO.getAdvancedSearchArgs()));
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
                dataLogE.setOldString(userRepository.queryUserNameByOption(originIssue.getAssigneeId(),false));
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
                dataLogE.setOldString(userRepository.queryUserNameByOption(originIssue.getReporterId(),false));
            }
            if (issueUpdateDTO.getReporterId() != 0) {
                dataLogE.setNewValue(issueUpdateDTO.getReporterId().toString());
                dataLogE.setNewString(userRepository.queryUserNameByOption(issueUpdateDTO.getReporterId(), false));
            }
            dataLogRepository.create(dataLogE);
        }
    }

    private void dataLogSprint(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO, boolean isRecordSprintLog) {
        if (isRecordSprintLog) {
            String oldValue, oldString, newValue, newString;
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(originIssue.getIssueId()));
            List<SprintNameDTO> closeSprintNames = sprintNameAssembler.doListToDTO(issueMapper.queryCloseSprintNameByIssueId(originIssue.getIssueId()));
            SprintNameDTO sprintName = sprintNameAssembler.doToDTO(sprintMapper.querySprintNameBySprintId(originIssue.getProjectId(), issueUpdateDTO.getSprintId()));
            if ((activeSprintName == null && sprintName == null) || (sprintName != null && activeSprintName != null && Objects.equals(sprintName.getSprintId(), activeSprintName.getSprintId()))) {
                return;
            }
            String closeSprintIdStr = closeSprintNames.stream().map(closeSprintName -> closeSprintName.getSprintId().toString()).collect(Collectors.joining(","));
            String closeSprintNameStr = closeSprintNames.stream().map(closeSprintName -> closeSprintName.getSprintName()).collect(Collectors.joining(","));
            oldValue = newValue = closeSprintIdStr;
            oldString = newString = closeSprintNameStr;
            if (activeSprintName != null) {
                oldValue = ("".equals(oldValue) ? activeSprintName.getSprintId().toString() : oldValue + "," + activeSprintName.getSprintId().toString());
                oldString = ("".equals(oldString) ? activeSprintName.getSprintName() : oldString + "," + activeSprintName.getSprintName());
            }
            if (sprintName != null) {
                newValue = ("".equals(newValue) ? sprintName.getSprintId().toString() :  newValue + "," + sprintName.getSprintId().toString());
                newString = ("".equals(newString) ? sprintName.getSprintName() : newString + "," + sprintName.getSprintName());
            }
            DataLogE dataLogE = new DataLogE();
            dataLogE.setProjectId(originIssue.getProjectId());
            dataLogE.setIssueId(issueUpdateDTO.getIssueId());
            dataLogE.setField(FIELD_SPRINT);
            dataLogE.setOldValue(oldValue);
            dataLogE.setOldString(oldString);
            dataLogE.setNewValue(newValue);
            dataLogE.setNewString(newString);
            dataLogRepository.create(dataLogE);
        }
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

    private void dataLog(IssueDO originIssue, IssueUpdateDTO issueUpdateDTO, boolean isRecordSprintLog) {
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
    }

    @Override
    public IssueDTO updateIssue(Long projectId, IssueUpdateDTO issueUpdateDTO, List<String> fieldList) {
        IssueDO originIssue = issueMapper.selectByPrimaryKey(issueUpdateDTO.getIssueId());
        dataLog(originIssue, issueUpdateDTO, fieldList.contains(SPRINT_ID_FIELD));
        if (fieldList != null && !fieldList.isEmpty()) {
            IssueE issueE = issueAssembler.issueUpdateDtoToEntity(issueUpdateDTO);
            if (fieldList.contains(SPRINT_ID_FIELD)) {
                List<Long> issueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
                issueIds.add(issueE.getIssueId());
                issueRepository.removeIssueFromSprintByIssueIds(projectId, issueIds);
                if (issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L)) {
                    issueRepository.issueToDestinationByIds(projectId, issueE.getSprintId(), issueIds);
                }
                if (issueE.isIssueRank()) {
                    calculationRank(issueE);
                }
            }
            issueRepository.update(issueE, fieldList.toArray(new String[fieldList.size()]));
        }
        Long issueId = issueUpdateDTO.getIssueId();
        handleUpdateLabelIssue(issueUpdateDTO.getLabelIssueRelDTOList(), issueId);
        handleUpdateComponentIssueRel(issueUpdateDTO.getComponentIssueRelDTOList(), projectId, issueId);
        handleUpdateVersionIssueRel(issueUpdateDTO.getVersionIssueRelDTOList(), projectId, issueId);
        return queryIssue(projectId, issueId);
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
        IssueE issueE = queryIssueByProjectIdAndIssueId(subIssueE.getProjectId(), subIssueE.getParentIssueId());
        //设置初始状态,如果有todo，就用todo，否则为doing，最后为done
        List<IssueStatusCreateDO> issueStatusCreateDOList = issueStatusMapper.queryIssueStatus(issueE.getProjectId());
        Long statusId = issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_TODO)).findFirst().orElse(
                issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DOING)).findFirst().orElse(
                        issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DONE)).findFirst().orElse(new IssueStatusCreateDO()))).getId();
        subIssueE = issueE.initializationSubIssue(subIssueE, statusId);
        //设置issue编号
        initializationIssueNum(subIssueE);
        Long issueId = issueRepository.create(subIssueE).getIssueId();
        if (issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L)) {
            issueRepository.issueToSprint(subIssueE.getProjectId(), subIssueE.getSprintId(), issueId);
        }
        if (issueSubCreateDTO.getIssueLinkCreateDTOList() != null && !issueSubCreateDTO.getIssueLinkCreateDTOList().isEmpty()) {
            issueLinkService.createIssueLinkList(issueSubCreateDTO.getIssueLinkCreateDTOList(), issueId, issueSubCreateDTO.getProjectId());
        }
        handleCreateLabelIssue(issueSubCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateVersionIssueRel(issueSubCreateDTO.getVersionIssueRelDTOList(), issueSubCreateDTO.getProjectId(), issueId);
        return queryIssueSub(subIssueE.getProjectId(), issueId);
    }

    @Override
    public IssueSubDTO updateSubIssue(Long projectId, IssueSubUpdateDTO issueSubUpdateDTO, List<String> fieldList) {
        if (fieldList != null && !fieldList.isEmpty()) {
            issueRepository.update(issueAssembler.issueSubUpdateDtoToEntity(issueSubUpdateDTO), fieldList.toArray(new String[fieldList.size()]));
        }
        Long issueId = issueSubUpdateDTO.getIssueId();
        handleUpdateLabelIssue(issueSubUpdateDTO.getLabelIssueRelDTOList(), issueId);
        handleUpdateVersionIssueRel(issueSubUpdateDTO.getVersionIssueRelDTOList(), projectId, issueId);
        return queryIssueSub(projectId, issueId);
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExist(projectId, versionId);
            issueRepository.batchIssueToVersion(projectId, versionId, issueIds);
        } else {
            issueRepository.batchRemoveVersion(projectId, issueIds);
        }
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    @Override
    public List<IssueSearchDTO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueRule.judgeExist(projectId, epicId);
        issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    private void dataLogRank(Long projectId, MoveIssueDTO moveIssueDTO, String rankStr, Long sprintId) {
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(issueId));
            if (sprintId.equals(activeSprintName.getSprintId())) {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setProjectId(projectId);
                dataLogE.setField(FIELD_RANK);
                dataLogE.setIssueId(issueId);
                dataLogE.setNewString(rankStr);
                dataLogRepository.create(dataLogE);
            }
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
        issueRepository.removeIssueFromSprintByIssueIds(projectId, moveIssueIds);
        issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIds);
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
            issueE.setEpicId(0L);
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD});
        } else if (issueE.getTypeCode().equals(ISSUE_EPIC)) {
            //如果之前类型是epic，会把该epic下的issue的epicId置为0
            issueRepository.batchUpdateIssueEpicId(issueE.getProjectId(), issueE.getIssueId());
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueE.setColorCode(null);
            issueE.setEpicName(null);
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, EPIC_NAME_FIELD, COLOR_CODE_FIELD});
        } else {
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD});
        }
        dataLogIssueType(originType, issueUpdateTypeDTO);
        return queryIssue(issueE.getProjectId(), issueE.getIssueId());
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
            versionIssueRelDTOList.forEach(versionIssueRelDTO -> versionIssueRelDTO.setRelationType(RELATION_TYPE_FIX));
            handleVersionIssueRel(ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class), projectId, issueId);
        }
    }

    private void handleVersionIssueRel(List<VersionIssueRelE> versionIssueRelEList, Long projectId, Long issueId) {
        versionIssueRelEList.forEach(versionIssueRelE -> {
            versionIssueRelE.setIssueId(issueId);
            versionIssueRelE.setProjectId(projectId);
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

    private void dataLogVersion(Long projectId, Long issueId, List<VersionIssueRelDTO> versionIssueRelDTOList) {
        if (versionIssueRelDTOList != null && !versionIssueRelDTOList.isEmpty() && !"fix".equals(versionIssueRelDTOList.get(0).getRelationType())) {
            return;
        }
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        versionIssueRelDO.setProjectId(projectId);
        versionIssueRelDO.setIssueId(issueId);
        List<VersionIssueRelDO> versionIssueRelDOList = versionIssueRelMapper.select(versionIssueRelDO);
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

    private void handleUpdateVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList, Long projectId, Long issueId) {
        if (versionIssueRelDTOList != null) {
            dataLogVersion(projectId, issueId, versionIssueRelDTOList);
            if (!versionIssueRelDTOList.isEmpty()) {
                versionIssueRelRepository.deleteByIssueId(issueId);
                handleVersionIssueRel(ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class), projectId, issueId);
            } else {
                versionIssueRelRepository.deleteByIssueId(issueId);
            }
        }

    }

    private List<ComponentIssueRelDO> getComponentIssueRel(Long projectId, Long issueId) {
        List<ComponentIssueRelDO> componentIssueRelDOList = componentIssueRelMapper.selectByProjectIdAndIssueId(projectId, issueId);
        return componentIssueRelDOList;
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

    private void initializationIssueNum(IssueE issueE) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(issueE.getProjectId());
        ProjectInfoDO query = projectInfoMapper.selectOne(projectInfoDO);
        Integer max = query.getIssueMaxNum().intValue() + 1;
        issueE.setIssueNum(max.toString());
        projectInfoRepository.updateIssueMaxNum(issueE.getProjectId());
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

}