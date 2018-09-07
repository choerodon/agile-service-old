package io.choerodon.agile.app.service.impl;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.event.IssuePayload;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.domain.agile.rule.ProductVersionRule;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.utils.ExcelUtil;
import io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueServiceImpl implements IssueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueServiceImpl.class);
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ComponentIssueRelRepository componentIssueRelRepository;
    @Autowired
    private IssueLinkRepository issueLinkRepository;
    @Autowired
    private LabelIssueRelRepository labelIssueRelRepository;
    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;
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
    private ReportAssembler reportAssembler;
    @Autowired
    private ProductVersionRule productVersionRule;
    @Autowired
    private IssueComponentRepository issueComponentRepository;
    @Autowired
    private ProductVersionService productVersionService;
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
    private IssueLinkTypeMapper issueLinkTypeMapper;
    @Autowired
    private IssueLinkMapper issueLinkMapper;
    @Autowired
    private IssueSprintRelRepository issueSprintRelRepository;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private StoryMapIssueAssembler storyMapIssueAssembler;
    @Autowired
    private QuickFilterMapper quickFilterMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserSettingRepository userSettingRepository;
    @Autowired
    private UserSettingMapper userSettingMapper;

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
    private static final String STORY_POINTS_FIELD = "storyPoints";
    private static final String REMAIN_TIME_FIELD = "remainingTime";
    private static final String STATUS_ID = "statusId";
    private static final String PARENT_ISSUE_ID = "parentIssueId";
    private static final String EPIC_SEQUENCE = "epicSequence";
    private static final String EPIC_COLOR_TYPE = "epic_color";
    private static final String STORY_TYPE = "story";
    private static final String ASSIGNEE = "assignee";
    private static final String FIELD_RANK = "Rank";
    private static final String RANK_HIGHER = "评级更高";
    private static final String RANK_LOWER = "评级更低";
    private static final String RANK_FIELD = "rank";
    private static final String FIX_RELATION_TYPE = "fix";
    private static final String INFLUENCE_RELATION_TYPE = "influence";
    private static final String[] FIELDS_NAME = {"编码", "概述", "描述", "类型", "所属项目", "经办人", "报告人", "状态", "冲刺", "创建时间", "最后更新时间", "优先级", "是否子任务", "剩余预估", "版本"};
    private static final String[] FIELDS = {"issueNum", "summary", "description", "typeName", "projectName", "assigneeName", "reporterName", "statusName", "sprintName", "creationDate", "lastUpdateDate", "priorityName", "subTask", REMAIN_TIME_FIELD, "versionName"};
    private static final String[] SUB_COLUMN_NAMES = {"关键字", "概述", "类型", "状态", "经办人"};
    private static final String EXPORT_ERROR = "error.issue.export";
    private static final String PROJECT_ERROR = "error.project.notFound";
    private static final String ERROR_ISSUE_NOT_FOUND = "error.Issue.queryIssue";
    private static final String ERROR_PROJECT_INFO_NOT_FOUND = "error.createIssue.projectInfoNotFound";
    private static final String SEARCH = "search";
    private static final String STORYMAP_TYPE_SPRINT = "sprint";
    private static final String STORYMAP_TYPE_VERSION = "version";
    private static final String STORYMAP_TYPE_NONE = "none";
    private static final String USERMAP = "usermap";
    private static final String STORYMAP = "storymap";

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    private final SagaClient sagaClient;

    @Autowired
    public IssueServiceImpl(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    @Override
    public synchronized IssueDTO createIssue(IssueCreateDTO issueCreateDTO) {
        IssueE issueE = issueAssembler.issueCreateDtoToIssueE(issueCreateDTO);
        //设置初始状态,如果有todo，就用todo，否则为doing，最后为done
        List<IssueStatusCreateDO> issueStatusCreateDOList = issueStatusMapper.queryIssueStatus(issueE.getProjectId());
        IssueStatusCreateDO issueStatusDO = issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_TODO)).findFirst().orElse(
                issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DOING)).findFirst().orElse(
                        issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DONE)).findFirst().orElse(null)));
        if (issueStatusDO == null) {
            throw new CommonException("error.createIssue.issueStatusNotFound");
        }
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(issueE.getProjectId());
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        handleInitIssue(issueE, issueStatusDO.getId(), projectInfoE);
        //创建issue
        Long issueId = issueRepository.create(issueE).getIssueId();
        //处理冲刺
        handleCreateSprintRel(issueE.getSprintId(), issueE.getProjectId(), issueId);
        handleCreateLabelIssue(issueCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateComponentIssueRel(issueCreateDTO.getComponentIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId, projectInfoE);
        handleCreateVersionIssueRel(issueCreateDTO.getVersionIssueRelDTOList(), issueCreateDTO.getProjectId(), issueId);
        return queryIssue(issueCreateDTO.getProjectId(), issueId);
    }

    private void handleInitIssue(IssueE issueE, Long statusId, ProjectInfoE projectInfoE) {
        //如果是epic，初始化颜色
        if (ISSUE_EPIC.equals(issueE.getTypeCode())) {
            List<LookupValueDO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueE.initializationColor(colorList);
            //排序编号
            Integer sequence = issueMapper.queryMaxEpicSequenceByProject(issueE.getProjectId());
            issueE.setEpicSequence(sequence == null ? 0 : sequence + 1);
        }
        //初始化创建issue设置issue编号、项目默认设置
        issueE.initializationIssue(statusId, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(issueE.getProjectId());
        //初始化排序
        if (issueE.isIssueRank()) {
            calculationRank(issueE.getProjectId(), issueE);
        }
        if (issueE.isIssueMapRank()) {
            calculationMapRank(issueE);
        }
    }

    private void calculationMapRank(IssueE issueE) {
        String maxRank = issueMapper.selectMaxRankByProjectId(issueE.getProjectId());
        if (maxRank == null) {
            issueE.setMapRank(RankUtil.mid());
        } else {
            issueE.setMapRank(RankUtil.genNext(maxRank));
        }
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
        //处理用户搜索
        if (searchDTO.getSearchArgs() != null && searchDTO.getSearchArgs().get(ASSIGNEE) != null) {
            String userName = (String) searchDTO.getSearchArgs().get(ASSIGNEE);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userRepository.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchDTO.getAdvancedSearchArgs().put("assigneeIds", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                }
            }
        }
        //连表查询需要设置主表别名
        pageRequest.resetOrder(SEARCH, new HashMap<>());
        Page<IssueDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueListWithoutSub(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContent()));
        return handlePageDoToDto(issueDOPage);
    }


    @Override
    public IssueDTO updateIssue(Long projectId, IssueUpdateDTO issueUpdateDTO, List<String> fieldList) {
        if (fieldList != null && !fieldList.isEmpty()) {
            //处理issue自己字段
            handleUpdateIssue(issueUpdateDTO, fieldList, projectId);
        }
        Long issueId = issueUpdateDTO.getIssueId();
        handleUpdateLabelIssue(issueUpdateDTO.getLabelIssueRelDTOList(), issueId);
        handleUpdateComponentIssueRel(issueUpdateDTO.getComponentIssueRelDTOList(), projectId, issueId);
        handleUpdateVersionIssueRel(issueUpdateDTO.getVersionIssueRelDTOList(), projectId, issueId, issueUpdateDTO.getVersionType());
        return queryIssue(projectId, issueId);
    }

    private void handleUpdateIssue(IssueUpdateDTO issueUpdateDTO, List<String> fieldList, Long projectId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        IssueDO originIssue = issueMapper.queryIssueWithNoCloseSprint(issueUpdateDTO.getIssueId());
        IssueE issueE = issueAssembler.issueUpdateDtoToEntity(issueUpdateDTO);
        //处理用户，前端可能会传0，处理为null
        issueE.initializationIssueUser();
        if (fieldList.contains(SPRINT_ID_FIELD)) {
            IssueE oldIssue = ConvertHelper.convert(originIssue, IssueE.class);
            //处理子任务的冲刺
            List<Long> issueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
            Boolean exitSprint = issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L);
            Boolean condition = (!Objects.equals(oldIssue.getSprintId(), issueUpdateDTO.getSprintId()));
            issueIds.add(issueE.getIssueId());
            if (condition) {
                BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, issueE.getSprintId(), issueIds);
                issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
            }
            if (exitSprint) {
                if (oldIssue.getSprintId() == null || oldIssue.getSprintId() == 0) {
                    issueIds.add(issueE.getIssueId());
                }
                issueRepository.issueToDestinationByIds(projectId, issueE.getSprintId(), issueIds, new Date(), customUserDetails.getUserId());
            }
            if (oldIssue.isIssueRank()) {
                calculationRank(projectId, issueE);
                fieldList.add(RANK_FIELD);
                issueE.setOriginSprintId(originIssue.getSprintId());
            }
        }
        issueRepository.update(issueE, fieldList.toArray(new String[fieldList.size()]));
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

    @Override
    public List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds) {
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<StoryMapEpicDTO> storyMapEpicDTOList = ConvertHelper.convertList(issueMapper.queryStoryMapEpicList(projectId, showDoneEpic, assigneeId, onlyStory, filterSql), StoryMapEpicDTO.class);
        if (!storyMapEpicDTOList.isEmpty()) {
            List<Long> epicIds = storyMapEpicDTOList.stream().map(StoryMapEpicDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = issueMapper.queryIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = issueMapper.queryDoneIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryNotEstimateIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> totalEstimateMap = issueMapper.queryTotalEstimateByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            storyMapEpicDTOList.forEach(epicData -> {
                epicData.setDoneIssueCount(doneIssueCountMap.get(epicData.getIssueId()));
                epicData.setIssueCount(issueCountMap.get(epicData.getIssueId()));
                epicData.setNotEstimate(notEstimateIssueCountMap.get(epicData.getIssueId()));
                epicData.setTotalEstimate(totalEstimateMap.get(epicData.getIssueId()));
            });
        }
        return storyMapEpicDTOList;
    }

    private void dataLogDeleteByIssueId(Long projectId, Long issueId) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setIssueId(issueId);
        dataLogRepository.delete(dataLogE);
    }

    @Saga(code = "agile-delete-issue", description = "删除issue", inputSchemaClass = IssuePayload.class)
    @Override
    public void deleteIssue(Long projectId, Long issueId) {
        IssueE issueE = queryIssueByProjectIdAndIssueId(projectId, issueId);
        if (issueE == null) {
            throw new CommonException(ERROR_ISSUE_NOT_FOUND);
        }
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
        //删除冲刺关联
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
            } else {
                redisUtil.deleteRedisCache(new String[]{"Agile:EpicChart" + projectId + ":" + issueE.getEpicId() + ":" + "*"});
            }
            List<IssueDO> issueDOList = issueMapper.queryIssueSubList(projectId, issueE.getIssueId());
            if (issueDOList != null && !issueDOList.isEmpty()) {
                issueDOList.forEach(subIssue -> deleteIssue(subIssue.getProjectId(), subIssue.getIssueId()));
            }
        }
        //删除日志信息
        dataLogDeleteByIssueId(projectId, issueId);
        issueRepository.delete(projectId, issueE.getIssueId());
        //删除issue发送消息
        IssuePayload issuePayload = new IssuePayload();
        issuePayload.setIssueId(issueId);
        issuePayload.setProjectId(projectId);
        sagaClient.startSaga("agile-delete-issue", new StartInstanceDTO(JSON.toJSONString(issuePayload), "", ""));
        //delete cache
        redisUtil.deleteRedisCache(new String[]{"Agile:BurnDownCoordinate" + projectId + ":" + "*",
                "Agile:CumulativeFlowDiagram" + projectId + ":" + "*",
                "Agile:VelocityChart" + projectId + ":" + "*",
                "Agile:PieChart" + projectId + ':' + "*"
        });
    }

    @Override
    public IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO) {
        IssueE subIssueE = issueAssembler.issueSubCreateDtoToEntity(issueSubCreateDTO);
        List<IssueStatusCreateDO> issueStatusCreateDOList = issueStatusMapper.queryIssueStatus(subIssueE.getProjectId());
        IssueStatusCreateDO issueStatusDO = issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_TODO)).findFirst().orElse(
                issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DOING)).findFirst().orElse(
                        issueStatusCreateDOList.stream().filter(issueStatusCreateDO -> issueStatusCreateDO.getCategoryCode().equals(STATUS_CODE_DONE)).findFirst().orElse(null)));
        if (issueStatusDO == null) {
            throw new CommonException("error.createIssue.issueStatusNotFound");
        }
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(subIssueE.getProjectId());
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        //初始化subIssue
        handleInitSubIssue(subIssueE, projectInfoE);
        //创建issue
        Long issueId = issueRepository.create(subIssueE).getIssueId();
        //处理冲刺
        handleCreateSprintRel(subIssueE.getSprintId(), subIssueE.getProjectId(), issueId);
        if (issueSubCreateDTO.getIssueLinkCreateDTOList() != null && !issueSubCreateDTO.getIssueLinkCreateDTOList().isEmpty()) {
            issueLinkService.createIssueLinkList(issueSubCreateDTO.getIssueLinkCreateDTOList(), issueId, issueSubCreateDTO.getProjectId());
        }
        handleCreateLabelIssue(issueSubCreateDTO.getLabelIssueRelDTOList(), issueId);
        handleCreateComponentIssueRel(issueSubCreateDTO.getComponentIssueRelDTOList(), issueSubCreateDTO.getProjectId(), issueId, projectInfoE);
        handleCreateVersionIssueRel(issueSubCreateDTO.getVersionIssueRelDTOList(), issueSubCreateDTO.getProjectId(), issueId);
        return queryIssueSub(subIssueE.getProjectId(), issueId);
    }

    public void handleCreateSprintRel(Long sprintId, Long projectId, Long issueId) {
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            IssueSprintRelE issueSprintRelE = new IssueSprintRelE();
            issueSprintRelE.setIssueId(issueId);
            issueSprintRelE.setSprintId(sprintId);
            issueSprintRelE.setProjectId(projectId);
            issueSprintRelRepository.createIssueSprintRel(issueSprintRelE);
        }
    }

    private void handleInitSubIssue(IssueE subIssueE, ProjectInfoE projectInfoE) {
        IssueE parentIssueE = ConvertHelper.convert(issueMapper.queryIssueSprintNotClosed(subIssueE.getProjectId(), subIssueE.getParentIssueId()), IssueE.class);
        //设置初始状态,跟随父类状态
        subIssueE = parentIssueE.initializationSubIssue(subIssueE, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(subIssueE.getProjectId());
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExist(projectId, versionId);
            List<Long> logAddIssueIds = new ArrayList<>(issueIds);
            logAddIssueIds.removeAll(issueMapper.queryInVersionIssueIds(projectId, versionId, issueIds));
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        } else {
            issueRepository.batchRemoveVersion(projectId, issueIds);
        }
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    @Override
    public void batchToVersionInStoryMap(Long projectId, Long versionId, MoveIssueDTO moveIssueDTO) {
        List<Long> issueIds = moveIssueDTO.getIssueIds();
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExistStoryMap(projectId, versionId);
            issueRepository.batchRemoveVersion(projectId, issueIds);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        } else {
            issueRepository.batchRemoveVersion(projectId, issueIds);
        }
    }

    @Override
    public List<IssueSearchDTO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueRule.judgeExist(projectId, epicId);
        issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    @Override
    public List<IssueSearchDTO> batchIssueToEpicInStoryMap(Long projectId, Long epicId, MoveIssueDTO moveIssueDTO) {
        List<Long> issueIds = moveIssueDTO.getIssueIds();
        issueRule.judgeExist(projectId, epicId);
        issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>());
    }

    private void dataLogRank(Long projectId, MoveIssueDTO moveIssueDTO, String rankStr, Long sprintId) {
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(issueId));
            Boolean condition = (sprintId == 0 && activeSprintName == null) || (activeSprintName != null
                    && sprintId.equals(activeSprintName.getSprintId()));
            if (condition) {
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
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        if (moveIssueDTO.getBefore()) {
            beforeRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
        } else {
            afterRank(projectId, sprintId, moveIssueDTO, moveIssueDOS);
        }
        //处理评级日志
        if (moveIssueDTO.getRankIndex() != null && !moveIssueDTO.getRankIndex()) {
            dataLogRank(projectId, moveIssueDTO, RANK_LOWER, sprintId);
        } else if (moveIssueDTO.getRankIndex() != null && moveIssueDTO.getRankIndex()) {
            dataLogRank(projectId, moveIssueDTO, RANK_HIGHER, sprintId);
        }
        issueRepository.batchUpdateIssueRank(projectId, moveIssueDOS);
        List<Long> moveIssueIds = moveIssueDTO.getIssueIds();
        //处理子任务
        moveIssueIds.addAll(issueMapper.querySubIssueIds(projectId, moveIssueIds));
        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIds);
        issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
        }
        List<IssueSearchDO> issueSearchDOList = issueMapper.queryIssueByIssueIds(projectId, moveIssueDTO.getIssueIds());
        List<Long> assigneeIds = issueSearchDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        return issueSearchAssembler.doListToDTO(issueSearchDOList, usersMap);
    }

    private void dealBoundBeforeRank(Long projectId, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String minRank = issueMapper.selectMinRankByProjectId(projectId);
        if (minRank == null) {
            initMapRank(projectId);
            minRank = issueMapper.selectMinRankByProjectId(projectId);
        }
        Collections.reverse(moveIssueDTO.getIssueIds());
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            minRank = RankUtil.genPre(minRank);
            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, minRank));
        }
    }

    private void dealBoundAfterRank(Long projectId, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String maxRank = issueMapper.selectMaxRankByProjectId(projectId);
        if (maxRank == null) {
            initMapRank(projectId);
            maxRank = issueMapper.selectMaxRankByProjectId(projectId);
        }
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            maxRank = RankUtil.genNext(maxRank);
            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, maxRank));
        }
    }

    private void dealBoundRank(Long projectId, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        if (moveIssueDTO.getBefore()) {
            dealBoundBeforeRank(projectId, moveIssueDTO, storyMapMoveIssueDOS);
        } else {
            dealBoundAfterRank(projectId, moveIssueDTO, storyMapMoveIssueDOS);
        }
    }

    private void dealInnerBeforeRank(Long projectId, String currentMapRank, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String leftMaxRank = issueMapper.selectLeftMaxMapRank(projectId, currentMapRank);
        Collections.reverse(moveIssueDTO.getIssueIds());
        if (leftMaxRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                currentMapRank = RankUtil.genPre(currentMapRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                leftMaxRank = RankUtil.between(leftMaxRank, currentMapRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, leftMaxRank));
            }
        }
    }

    private void dealInnerAfterRank(Long projectId, String currentMapRank, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String rightMinRank = issueMapper.selectRightMinMapRank(projectId, currentMapRank);
        if (rightMinRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                currentMapRank = RankUtil.genNext(currentMapRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                currentMapRank = RankUtil.between(currentMapRank, rightMinRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        }
    }

    private void dealInnerRank(Long projectId, MoveIssueDTO moveIssueDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String currentMapRank = issueMapper.selectMapRankByIssueId(projectId, moveIssueDTO.getOutsetIssueId());
        if (currentMapRank == null) {
            initMapRank(projectId);
            currentMapRank = issueMapper.selectMapRankByIssueId(projectId, moveIssueDTO.getOutsetIssueId());
        }
        if (moveIssueDTO.getBefore()) {
            dealInnerBeforeRank(projectId, currentMapRank, moveIssueDTO, storyMapMoveIssueDOS);
        } else {
            dealInnerAfterRank(projectId, currentMapRank, moveIssueDTO, storyMapMoveIssueDOS);
        }
    }

    private void dealRank(Long projectId, MoveIssueDTO moveIssueDTO) {
        List<StoryMapMoveIssueDO> storyMapMoveIssueDOS = new ArrayList<>();
        if (moveIssueDTO.getOutsetIssueId() == null || Objects.equals(moveIssueDTO.getOutsetIssueId(), 0L)) {
            dealBoundRank(projectId, moveIssueDTO, storyMapMoveIssueDOS);
        } else {
            dealInnerRank(projectId, moveIssueDTO, storyMapMoveIssueDOS);
        }
        issueRepository.batchUpdateMapIssueRank(projectId, storyMapMoveIssueDOS);
    }

    @Override
    public List<IssueSearchDTO> batchIssueToSprintInStoryMap(Long projectId, Long sprintId, MoveIssueDTO moveIssueDTO) {
        sprintRule.judgeExist(projectId, sprintId);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        //处理评级日志
        if (moveIssueDTO.getRankIndex() != null && !moveIssueDTO.getRankIndex()) {
            dataLogRank(projectId, moveIssueDTO, RANK_LOWER, sprintId);
        } else if (moveIssueDTO.getRankIndex() != null && moveIssueDTO.getRankIndex()) {
            dataLogRank(projectId, moveIssueDTO, RANK_HIGHER, sprintId);
        }
        List<Long> moveIssueIds = moveIssueDTO.getIssueIds();
        //处理子任务
        moveIssueIds.addAll(issueMapper.querySubIssueIds(projectId, moveIssueIds));
        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIds);
        issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
        }
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

    @Override
    public synchronized IssueDTO updateIssueTypeCode(IssueE issueE, IssueUpdateTypeDTO issueUpdateTypeDTO) {
        String originType = issueE.getTypeCode();
        if (originType.equals(SUB_TASK)) {
            issueE.setParentIssueId(null);
        }
        if (STORY_TYPE.equals(issueE.getTypeCode()) && issueE.getStoryPoints() != null) {
            issueE.setStoryPoints(0);
        }
        if (issueUpdateTypeDTO.getTypeCode().equals(ISSUE_EPIC)) {
            issueE.setRank(null);
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueE.setEpicName(issueUpdateTypeDTO.getEpicName());
            List<LookupValueDO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueE.initializationColor(colorList);
            issueE.setRemainingTime(null);
            issueE.setEpicId(0L);
            //排序编号
            Integer sequence = issueMapper.queryMaxEpicSequenceByProject(issueE.getProjectId());
            issueE.setEpicSequence(sequence == null ? 0 : sequence + 1);
        } else if (issueE.getTypeCode().equals(ISSUE_EPIC)) {
            // 如果之前类型是epic，会把该epic下的issue的epicId置为0
            issueRepository.batchUpdateIssueEpicId(issueE.getProjectId(), issueE.getIssueId());
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueE.setColorCode(null);
            issueE.setEpicName(null);
            issueE.setEpicSequence(null);
            //rank值重置
            calculationRank(issueE.getProjectId(), issueE);
        } else {
            issueE.setTypeCode(issueUpdateTypeDTO.getTypeCode());
        }
        issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, PARENT_ISSUE_ID, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, STORY_POINTS_FIELD, RANK_FIELD, EPIC_SEQUENCE});
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
            handleVersionIssueRel(ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class), projectId, issueId);
        }
    }

    private void handleVersionIssueRel(List<VersionIssueRelE> versionIssueRelEList, Long projectId, Long issueId) {
        versionIssueRelEList.forEach(versionIssueRelE -> {
            versionIssueRelE.setIssueId(issueId);
            versionIssueRelE.setProjectId(projectId);
            versionIssueRelE.setRelationType(versionIssueRelE.getRelationType() == null ? "fix" : versionIssueRelE.getRelationType());
            issueRule.verifyVersionIssueRelData(versionIssueRelE);
            if (versionIssueRelE.getName() != null && versionIssueRelE.getVersionId() == null) {
                //重名校验
                ProductVersionE productVersionE = versionIssueRelE.createProductVersionE();
                if (productVersionMapper.isRepeatName(productVersionE.getProjectId(), productVersionE.getName())) {
                    //已归档的版本id是null,不进行任何操作
                    Long versionId = productVersionMapper.queryVersionIdByNameAndProjectId(productVersionE.getName(), productVersionE.getProjectId());
                    if (versionId != null) {
                        versionIssueRelE.setVersionId(versionId);
                    } else {
                        return;
                    }
                } else {
                    ProductVersionCreateDTO productVersionCreateDTO = issueAssembler.productVersionEntityToProductVersionCreateDto(productVersionE);
                    ProductVersionDetailDTO productVersionDetailDTO = productVersionService.createVersion(projectId, productVersionCreateDTO);
                    versionIssueRelE.setVersionId(productVersionDetailDTO.getVersionId());
                }
            }
            handleVersionIssueRelCreate(versionIssueRelE);
        });
    }

    private void handleVersionIssueRelCreate(VersionIssueRelE versionIssueRelE) {
        if (issueRule.existVersionIssueRel(versionIssueRelE)) {
            versionIssueRelRepository.create(versionIssueRelE);
        }
    }

    private void handleCreateComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList, Long projectId, Long issueId, ProjectInfoE projectInfoE) {
        if (componentIssueRelDTOList != null && !componentIssueRelDTOList.isEmpty()) {
            handleComponentIssueRelWithHandleAssignee(ConvertHelper.convertList(componentIssueRelDTOList, ComponentIssueRelE.class), projectId, issueId, projectInfoE);
        }
    }

    private void handleComponentIssueRelWithHandleAssignee(List<ComponentIssueRelE> componentIssueRelEList, Long projectId, Long issueId, ProjectInfoE projectInfoE) {
        componentIssueRelEList.forEach(componentIssueRelE -> {
            handleComponentIssueRel(componentIssueRelE, projectId, issueId);
            //issue经办人可以根据模块策略进行区分
            handleComponentIssue(componentIssueRelE, issueId, projectInfoE);
        });
    }

    private void handleComponentIssueRel(ComponentIssueRelE componentIssueRelE, Long projectId, Long issueId) {
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
        if (issueRule.existComponentIssueRel(componentIssueRelE)) {
            componentIssueRelRepository.create(componentIssueRelE);
        }
    }

    private void handleComponentIssue(ComponentIssueRelE componentIssueRelE, Long issueId, ProjectInfoE projectInfoE) {
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentMapper.selectByPrimaryKey(
                componentIssueRelE.getComponentId()), IssueComponentE.class);
        if (ISSUE_MANAGER_TYPE.equals(issueComponentE.getDefaultAssigneeRole()) && issueComponentE.getManagerId() !=
                null && issueComponentE.getManagerId() != 0) {
            //如果模块有选择模块负责人或者经办人的话，对应的issue的负责人要修改
            IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
            Boolean condition = (issueE.getAssigneeId() == null || issueE.getAssigneeId() == 0) ||
                    (projectInfoE.getDefaultAssigneeType() != null);
            if (condition) {
                issueE.setAssigneeId(issueComponentE.getManagerId());
                issueRepository.update(issueE, new String[]{"assigneeId"});
            }
        }
    }

    private void handleUpdateLabelIssue(List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId) {
        if (labelIssueRelDTOList != null) {
            if (!labelIssueRelDTOList.isEmpty()) {
                LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO();
                labelIssueRelDO.setIssueId(issueId);
                List<LabelIssueRelE> originLabels = ConvertHelper.convertList(labelIssueRelMapper.select(labelIssueRelDO), LabelIssueRelE.class);
                List<LabelIssueRelE> labelIssueEList = ConvertHelper.convertList(labelIssueRelDTOList, LabelIssueRelE.class);
                List<LabelIssueRelE> labelIssueCreateList = labelIssueEList.stream().filter(labelIssueRelE ->
                        labelIssueRelE.getLabelId() != null).collect(Collectors.toList());
                List<Long> curLabelIds = originLabels.stream().
                        map(LabelIssueRelE::getLabelId).collect(Collectors.toList());
                List<Long> createLabelIds = labelIssueCreateList.stream().
                        map(LabelIssueRelE::getLabelId).collect(Collectors.toList());
                curLabelIds.forEach(id -> {
                    if (!createLabelIds.contains(id)) {
                        LabelIssueRelDO delete = new LabelIssueRelDO();
                        delete.setIssueId(issueId);
                        delete.setLabelId(id);
                        labelIssueRelRepository.delete(delete);
                    }
                });
                labelIssueEList.forEach(labelIssueRelE -> {
                    labelIssueRelE.setIssueId(issueId);
                    handleLabelIssue(labelIssueRelE);
                });
            } else {
                labelIssueRelRepository.batchDeleteByIssueId(issueId);
            }
            //没有issue使用的标签进行垃圾回收
            issueLabelRepository.labelGarbageCollection();
        }

    }

    private void handleUpdateVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList, Long projectId, Long issueId, String versionType) {
        if (versionIssueRelDTOList != null && versionType != null) {
            if (!versionIssueRelDTOList.isEmpty()) {
                //归档状态的版本之间的关联不删除
                List<VersionIssueRelE> versionIssueRelES = ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class);
                List<VersionIssueRelE> versionIssueRelCreate = versionIssueRelES.stream().filter(versionIssueRelE ->
                        versionIssueRelE.getVersionId() != null).collect(Collectors.toList());
                List<Long> curVersionIds = versionIssueRelMapper.queryByIssueIdAndProjectIdNoArchived(projectId, issueId);
                List<Long> createVersionIds = versionIssueRelCreate.stream().map(VersionIssueRelE::getVersionId).collect(Collectors.toList());
                curVersionIds.forEach(id -> {
                    if (!createVersionIds.contains(id)) {
                        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
                        versionIssueRelDO.setIssueId(issueId);
                        versionIssueRelDO.setVersionId(id);
                        versionIssueRelDO.setProjectId(projectId);
                        versionIssueRelRepository.delete(versionIssueRelDO);
                    }
                });
                handleVersionIssueRel(versionIssueRelES, projectId, issueId);
            } else {
                VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
                versionIssueRelE.createBatchDeleteVersionIssueRel(projectId, issueId, versionType);
                versionIssueRelRepository.batchDeleteByIssueIdAndType(versionIssueRelE);
            }
        }

    }

    private List<ComponentIssueRelDO> getComponentIssueRel(Long projectId, Long issueId) {
        return componentIssueRelMapper.selectByProjectIdAndIssueId(projectId, issueId);
    }

    private void handleUpdateComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList, Long projectId, Long issueId) {
        if (componentIssueRelDTOList != null) {
            if (!componentIssueRelDTOList.isEmpty()) {
                List<ComponentIssueRelE> componentIssueRelEList = ConvertHelper.convertList(componentIssueRelDTOList, ComponentIssueRelE.class);
                List<ComponentIssueRelE> componentIssueRelCreate = componentIssueRelEList.stream().filter(componentIssueRelE ->
                        componentIssueRelE.getComponentId() != null).collect(Collectors.toList());
                List<Long> curComponentIds = getComponentIssueRel(projectId, issueId).stream().
                        map(ComponentIssueRelDO::getComponentId).collect(Collectors.toList());
                List<Long> createComponentIds = componentIssueRelCreate.stream().
                        map(ComponentIssueRelE::getComponentId).collect(Collectors.toList());
                curComponentIds.forEach(id -> {
                    if (!createComponentIds.contains(id)) {
                        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
                        componentIssueRelDO.setIssueId(issueId);
                        componentIssueRelDO.setComponentId(id);
                        componentIssueRelDO.setProjectId(projectId);
                        componentIssueRelRepository.delete(componentIssueRelDO);
                    }
                });
                componentIssueRelEList.forEach(componentIssueRelE -> handleComponentIssueRel(componentIssueRelE, projectId, issueId));
            } else {
                componentIssueRelRepository.batchComponentDelete(issueId);
            }
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

    private Long getActiveSprintId(Long projectId) {
        SprintDO sprintDO = sprintService.getActiveSprint(projectId);
        if (sprintDO != null) {
            return sprintDO.getSprintId();
        }
        return null;
    }

    @Override
    public Page<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.resetOrder("ai", new HashMap<>());
        IssueNumDO issueNumDO = null;
        if (self) {
            issueNumDO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        Long activeSprintId = onlyActiveSprint ? getActiveSprintId(projectId) : null;
        Page<IssueNumDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueByOption(projectId, issueId, issueNum, activeSprintId, self, content));
        if (self && issueNumDO != null) {
            issueDOPage.getContent().add(0, issueNumDO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }
        Page<IssueNumDTO> issueListDTOPage = new Page<>();
        issueListDTOPage.setNumber(issueDOPage.getNumber());
        issueListDTOPage.setNumberOfElements(issueDOPage.getNumberOfElements());
        issueListDTOPage.setSize(issueDOPage.getSize());
        issueListDTOPage.setTotalElements(issueDOPage.getTotalElements());
        issueListDTOPage.setTotalPages(issueDOPage.getTotalPages());
        issueListDTOPage.setContent(issueAssembler.issueNumDOListToIssueNumDTO(issueDOPage.getContent()));
        return issueListDTOPage;
    }

    @Override
    public void exportIssues(Long projectId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        projectInfoDO = projectInfoMapper.selectOne(projectInfoDO);
        String projectCode = projectInfoDO.getProjectCode();
        ProjectDTO project = userRepository.queryProject(projectId);
        if (project == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        project.setCode(projectInfoDO.getProjectCode());
        List<ExportIssuesDTO> exportIssues = issueAssembler.exportIssuesDOListToExportIssuesDTO(issueMapper.queryExportIssues(projectId, searchDTO.getSearchArgs(),
                searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContent(), projectCode));
        List<Long> issueIds = exportIssues.stream().map(ExportIssuesDTO::getIssueId).collect(Collectors.toList());
        if (!issueIds.isEmpty()) {
            Map<Long, List<SprintNameDO>> closeSprintNames = issueMapper.querySprintNameByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(SprintNameDO::getIssueId));
            Map<Long, List<VersionIssueRelDO>> fixVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, FIX_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
            Map<Long, List<VersionIssueRelDO>> influenceVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, INFLUENCE_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
            exportIssues.forEach(exportIssue -> {
                String closeSprintName = closeSprintNames.get(exportIssue.getIssueId()) != null ? closeSprintNames.get(exportIssue.getIssueId()).stream().map(SprintNameDO::getSprintName).collect(Collectors.joining(",")) : "";
                String fixVersionName = fixVersionNames.get(exportIssue.getIssueId()) != null ? fixVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                String influenceVersionName = influenceVersionNames.get(exportIssue.getIssueId()) != null ? influenceVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                exportIssue.setCloseSprintName(closeSprintName);
                exportIssue.setProjectName(project.getName());
                exportIssue.setSprintName(exportIssuesSprintName(exportIssue));
                exportIssue.setVersionName(exportIssuesVersionName(exportIssue));
                exportIssue.setFixVersionName(fixVersionName);
                exportIssue.setInfluenceVersionName(influenceVersionName);
                exportIssue.setDescription(getDes(exportIssue.getDescription()));
            });
        }
        ExcelUtil.export(exportIssues, ExportIssuesDTO.class, FIELDS_NAME, FIELDS, project.getName(), response);
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
        ProjectDTO project = userRepository.queryProject(projectId);
        if (project == null || projectInfoDO == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        project.setCode(projectInfoDO.getProjectCode());
        ExportIssuesDTO exportIssue = issueAssembler.exportIssuesDOToExportIssuesDTO(issueMapper.queryExportIssue(projectId, issueId));
        HSSFWorkbook workbook = new HSSFWorkbook();
        String fileName = project.getCode();
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
            exportIssueXls(workbook, project, exportIssue, subIssues);
            fileName = fileName + "-" + exportIssue.getIssueNum();
        }
        downloadExcel(workbook, fileName, charsetName, response);
    }

    @Override
    public IssueDTO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO) {
        IssueDetailDO issueDetailDO = issueMapper.queryIssueDetail(projectId, issueId);
        if (issueDetailDO != null) {
            issueDetailDO.setSummary(copyConditionDTO.getSummary());
            IssueCreateDTO issueCreateDTO = issueAssembler.issueDtoToIssueCreateDto(issueDetailDO);
            IssueDTO newIssue = createIssue(issueCreateDTO);
            //复制链接
            batchCreateCopyIssueLink(copyConditionDTO.getIssueLink(), issueId, newIssue.getIssueId(), projectId);
            //生成一条复制的关联
            createCopyIssueLink(issueDetailDO.getIssueId(), newIssue.getIssueId(), projectId);
            //复制故事点和剩余工作量并记录日志
            copyStoryPointAndRemainingTimeData(issueDetailDO, projectId, newIssue);
            //复制冲刺
            handleCreateCopyIssueSprintRel(copyConditionDTO.getSprintValues(), issueDetailDO, newIssue.getIssueId());
            if (copyConditionDTO.getSubTask()) {
                List<IssueDO> subIssueDOList = issueDetailDO.getSubIssueDOList();
                if (subIssueDOList != null && !subIssueDOList.isEmpty()) {
                    subIssueDOList.forEach(issueDO -> copySubIssue(issueDO, newIssue.getIssueId(), projectId));
                }
            }
            return queryIssue(projectId, newIssue.getIssueId());
        } else {
            throw new CommonException("error.issue.copyIssueByIssueId");
        }
    }

    private void copyStoryPointAndRemainingTimeData(IssueDetailDO issueDetailDO, Long projectId, IssueDTO newIssue) {
        if (issueDetailDO.getStoryPoints() == null && issueDetailDO.getEstimateTime() == null) {
            return;
        }
        IssueUpdateDTO issueUpdateDTO = new IssueUpdateDTO();
        issueUpdateDTO.setStoryPoints(issueDetailDO.getStoryPoints());
        issueUpdateDTO.setRemainingTime(issueDetailDO.getRemainingTime());
        issueUpdateDTO.setIssueId(newIssue.getIssueId());
        issueUpdateDTO.setObjectVersionNumber(newIssue.getObjectVersionNumber());
        List<String> fieldList = new ArrayList<>();
        if (issueDetailDO.getStoryPoints() != null) {
            fieldList.add(STORY_POINTS_FIELD);
        }
        if (issueDetailDO.getRemainingTime() != null) {
            fieldList.add(REMAIN_TIME_FIELD);
        }
        updateIssue(projectId, issueUpdateDTO, fieldList);
    }

    private void copySubIssue(IssueDO issueDO, Long newIssueId, Long projectId) {
        IssueDetailDO subIssueDetailDO = issueMapper.queryIssueDetail(issueDO.getProjectId(), issueDO.getIssueId());
        IssueSubCreateDTO issueSubCreateDTO = issueAssembler.issueDtoToSubIssueCreateDto(subIssueDetailDO, newIssueId);
        IssueSubDTO newSubIssue = createSubIssue(issueSubCreateDTO);
        //复制剩余工作量并记录日志
        if (issueDO.getRemainingTime() != null) {
            IssueUpdateDTO subIssueUpdateDTO = new IssueUpdateDTO();
            subIssueUpdateDTO.setRemainingTime(issueDO.getRemainingTime());
            subIssueUpdateDTO.setIssueId(newSubIssue.getIssueId());
            subIssueUpdateDTO.setObjectVersionNumber(newSubIssue.getObjectVersionNumber());
            updateIssue(projectId, subIssueUpdateDTO, Lists.newArrayList(REMAIN_TIME_FIELD));
        }
    }

    private void handleCreateCopyIssueSprintRel(Boolean sprintValues, IssueDetailDO issueDetailDO, Long newIssueId) {
        if (sprintValues && issueDetailDO.getActiveSprint() != null) {
            handleCreateSprintRel(issueDetailDO.getActiveSprint().getSprintId(), issueDetailDO.getProjectId(), newIssueId);
        }
    }

    private void batchCreateCopyIssueLink(Boolean condition, Long issueId, Long newIssueId, Long projectId) {
        if (condition) {
            List<IssueLinkE> issueLinkEList = ConvertHelper.convertList(issueLinkMapper.queryIssueLinkByIssueId(issueId, projectId, false), IssueLinkE.class);
            issueLinkEList.forEach(issueLinkE -> {
                IssueLinkE copy = new IssueLinkE();
                if (issueLinkE.getIssueId().equals(issueId)) {
                    copy.setIssueId(newIssueId);
                    copy.setLinkedIssueId(issueLinkE.getLinkedIssueId());
                }
                if (issueLinkE.getLinkedIssueId().equals(issueId)) {
                    copy.setIssueId(issueLinkE.getIssueId());
                    copy.setLinkedIssueId(newIssueId);
                }
                copy.setLinkTypeId(issueLinkE.getLinkTypeId());
                issueLinkRepository.create(copy);
            });
        }
    }

    private void createCopyIssueLink(Long issueId, Long newIssueId, Long projectId) {
        IssueLinkTypeDO query = new IssueLinkTypeDO();
        query.setProjectId(projectId);
        query.setOutWard("复制");
        IssueLinkTypeDO issueLinkTypeDO = issueLinkTypeMapper.selectOne(query);
        if (issueLinkTypeDO != null) {
            IssueLinkE issueLinkE = new IssueLinkE();
            issueLinkE.setLinkedIssueId(issueId);
            issueLinkE.setLinkTypeId(issueLinkTypeDO.getLinkTypeId());
            issueLinkE.setIssueId(newIssueId);
            issueLinkRepository.create(issueLinkE);
        }
    }

    @Override
    public IssueSubDTO transformedSubTask(Long projectId, IssueTransformSubTask issueTransformSubTask) {
        IssueE issueE = ConvertHelper.convert(queryIssueByIssueIdAndProjectId(projectId, issueTransformSubTask.getIssueId()), IssueE.class);
        if (issueE != null) {
            if (!issueE.getTypeCode().equals(SUB_TASK)) {
                issueE.setObjectVersionNumber(issueTransformSubTask.getObjectVersionNumber());
                List<Long> subIssueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
                if (subIssueIds != null && !subIssueIds.isEmpty()) {
                    throw new CommonException("error.transformedSubTask.issueHaveSubIssue");
                }
                issueE.setTypeCode(SUB_TASK);
                issueE.setRank(null);
                issueE.setEpicSequence(null);
                issueE.setParentIssueId(issueTransformSubTask.getParentIssueId());
                issueRule.verifySubTask(issueTransformSubTask.getParentIssueId());
                //删除链接
                issueLinkRepository.deleteByIssueId(issueE.getIssueId());
                issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, RANK_FIELD, STATUS_ID, PARENT_ISSUE_ID, EPIC_SEQUENCE});
                return queryIssueSub(projectId, issueE.getIssueId());
            } else {
                throw new CommonException("error.IssueRule.subTaskError");
            }
        } else {
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }

    private void downloadExcel(HSSFWorkbook workbook, String fileName, String charsetName, HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 设置response参数，可以打开下载页面
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(charsetName)));
        } catch (UnsupportedEncodingException e1) {
            throw new CommonException(EXPORT_ERROR);
        }
        try {
            workbook.write(response.getOutputStream());
        } catch (final IOException e) {
            throw new CommonException(EXPORT_ERROR);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void exportIssueXls(HSSFWorkbook workbook, ProjectDTO project, ExportIssuesDTO exportIssue, List<ExportIssuesDTO> subIssues) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String issueNum = project.getCode() + "-" + exportIssue.getIssueNum();
        HSSFSheet sheet = workbook.createSheet(issueNum);
        int lastRow = 12;
        if (!subIssues.isEmpty()) {
            lastRow = subIssues.size() + 13;
            sheet.addMergedRegion(new CellRangeAddress(13, lastRow, 0, 0));
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
        cell.setCellValue(project.getName());

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

        sheet.createRow(12);

        createSubIssueRow(subIssues, project, sheet);

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

    private void createSubIssueRow(List<ExportIssuesDTO> subIssues, ProjectDTO project, HSSFSheet sheet) {
        if (!subIssues.isEmpty()) {
            HSSFRow row = sheet.createRow(13);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("子任务:");
            for (int i = 0; i < SUB_COLUMN_NAMES.length; i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(SUB_COLUMN_NAMES[i]);
            }
            for (int i = 0; i < subIssues.size(); i++) {
                row = sheet.createRow(i + 14);
                for (int j = 0; j < SUB_COLUMN_NAMES.length; j++) {
                    cell = row.createCell(j + 1);
                    switch (SUB_COLUMN_NAMES[j]) {
                        case "关键字":
                            cell.setCellValue(project.getCode() + "-" + subIssues.get(i).getIssueNum());
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
    }

    private String exportIssuesVersionName(ExportIssuesDTO exportIssuesDTO) {
        StringBuilder versionName = new StringBuilder();
        if (exportIssuesDTO.getFixVersionName() != null && !"".equals(exportIssuesDTO.getFixVersionName())) {
            versionName.append("修复的版本:").append(exportIssuesDTO.getFixVersionName()).append("\r\n");
        } else if (exportIssuesDTO.getInfluenceVersionName() != null && !"".equals(exportIssuesDTO.getInfluenceVersionName())) {
            versionName.append("影响的版本:").append(exportIssuesDTO.getInfluenceVersionName());
        }
        return versionName.toString();
    }

    private String exportIssuesSprintName(ExportIssuesDTO exportIssuesDTO) {
        StringBuilder sprintName = new StringBuilder(exportIssuesDTO.getSprintName() != null ? "正在使用冲刺:" + exportIssuesDTO.getSprintName() + "\r\n" : "");
        sprintName.append(!Objects.equals(exportIssuesDTO.getCloseSprintName(), "") ? "已关闭冲刺:" + exportIssuesDTO.getCloseSprintName() : "");
        return sprintName.toString();
    }

    private IssueDO queryIssueByIssueIdAndProjectId(Long projectId, Long issueId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setIssueId(issueId);
        issueDO.setProjectId(projectId);
        return issueMapper.selectOne(issueDO);
    }

    @Override
    public List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds) {
        return ConvertHelper.convertList(issueMapper.listByIssueIds(projectId, issueIds), IssueInfoDTO.class);
    }

    @Override
    public Page<IssueListDTO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.resetOrder(SEARCH, new HashMap<>());
        Page<IssueDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.listIssueWithoutSubToTestComponent(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContent()));
        return handlePageDoToDto(issueDOPage);
    }

    private Page<IssueListDTO> handlePageDoToDto(Page<IssueDO> issueDOPage) {
        Page<IssueListDTO> issueListDTOPage = new Page<>();
        issueListDTOPage.setNumber(issueDOPage.getNumber());
        issueListDTOPage.setNumberOfElements(issueDOPage.getNumberOfElements());
        issueListDTOPage.setSize(issueDOPage.getSize());
        issueListDTOPage.setTotalElements(issueDOPage.getTotalElements());
        issueListDTOPage.setTotalPages(issueDOPage.getTotalPages());
        issueListDTOPage.setContent(issueAssembler.issueDoToIssueListDto(issueDOPage.getContent()));
        return issueListDTOPage;
    }

    @Override
    public List<IssueCreationNumDTO> queryIssueNumByTimeSlot(Long projectId, String typeCode, Integer timeSlot) {
        //h2 不支持dateSub函数，这个函数不能自定义
        Date date = MybatisFunctionTestUtil.dataSubFunction(new Date(), timeSlot);
        return ConvertHelper.convertList(issueMapper.queryIssueNumByTimeSlot(projectId, typeCode, date), IssueCreationNumDTO.class);
    }

    @Override
    public Page<IssueNumDTO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum, Boolean self, String content, PageRequest pageRequest) {
        pageRequest.resetOrder("ai", new HashMap<>());
        IssueNumDO issueNumDO = null;
        if (self) {
            issueNumDO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        Page<IssueNumDO> issueDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.queryIssueByOptionForAgile(projectId, issueId, issueNum, self, content));
        if (self && issueNumDO != null) {
            issueDOPage.getContent().add(0, issueNumDO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }
        Page<IssueNumDTO> issueListDTOPage = new Page<>();
        issueListDTOPage.setNumber(issueDOPage.getNumber());
        issueListDTOPage.setNumberOfElements(issueDOPage.getNumberOfElements());
        issueListDTOPage.setSize(issueDOPage.getSize());
        issueListDTOPage.setTotalElements(issueDOPage.getTotalElements());
        issueListDTOPage.setTotalPages(issueDOPage.getTotalPages());
        issueListDTOPage.setContent(issueAssembler.issueNumDOListToIssueNumDTO(issueDOPage.getContent()));
        return issueListDTOPage;
    }

    @Override
    public synchronized EpicDataDTO dragEpic(Long projectId, EpicSequenceDTO epicSequenceDTO) {
        if (epicSequenceDTO.getAfterSequence() == null && epicSequenceDTO.getBeforeSequence() == null) {
            throw new CommonException("error.dragEpic.noSequence");
        }
        IssueDO issueDO = new IssueDO();
        issueDO.setIssueId(epicSequenceDTO.getEpicId());
        issueDO.setProjectId(projectId);
        IssueE issueE = ConvertHelper.convert(issueMapper.selectOne(issueDO), IssueE.class);
        if (issueE == null) {
            throw new CommonException("error.issue.notFound");
        } else {
            if (epicSequenceDTO.getAfterSequence() == null) {
                Integer maxSequence = productVersionMapper.queryMaxAfterSequence(epicSequenceDTO.getBeforeSequence(), projectId);
                epicSequenceDTO.setAfterSequence(maxSequence);
            } else if (epicSequenceDTO.getBeforeSequence() == null) {
                Integer minSequence = productVersionMapper.queryMinBeforeSequence(epicSequenceDTO.getAfterSequence(), projectId);
                epicSequenceDTO.setBeforeSequence(minSequence);
            }
            handleSequence(epicSequenceDTO, projectId, issueE);
        }
        return epicDataAssembler.doToEntity(issueMapper.queryEpicListByEpic(epicSequenceDTO.getEpicId(), projectId));
    }

    @Override
    public List<PieChartDTO> issueStatistic(Long projectId, String type, List<String> issueTypes) {
        return reportAssembler.pieChartDoToDto(issueMapper.issueStatistic(projectId, type, issueTypes));
    }

    @Override
    public Page<IssueComponentDetailDTO> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.resetOrder(SEARCH, new HashMap<>());
        Page<IssueComponentDetailDO> issueComponentDetailDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                issueMapper.listIssueWithoutSubDetail(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContent()));
        return handleIssueComponentDetailPageDoToDto(issueComponentDetailDOPage);
    }

    private Page<IssueComponentDetailDTO> handleIssueComponentDetailPageDoToDto(Page<IssueComponentDetailDO> issueComponentDetailDOPage) {
        Page<IssueComponentDetailDTO> issueComponentDetailDTOPage = new Page<>();
        issueComponentDetailDTOPage.setNumber(issueComponentDetailDOPage.getNumber());
        issueComponentDetailDTOPage.setNumberOfElements(issueComponentDetailDOPage.getNumberOfElements());
        issueComponentDetailDTOPage.setSize(issueComponentDetailDOPage.getSize());
        issueComponentDetailDTOPage.setTotalElements(issueComponentDetailDOPage.getTotalElements());
        issueComponentDetailDTOPage.setTotalPages(issueComponentDetailDOPage.getTotalPages());
        issueComponentDetailDTOPage.setContent(issueAssembler.issueComponentDetailDoToDto(issueComponentDetailDOPage.getContent()));
        return issueComponentDetailDTOPage;
    }

    private void handleSequence(EpicSequenceDTO epicSequenceDTO, Long projectId, IssueE issueE) {
        if (epicSequenceDTO.getBeforeSequence() == null) {
            issueE.setEpicSequence(epicSequenceDTO.getAfterSequence() + 1);
            issueRepository.update(issueE, new String[]{EPIC_SEQUENCE});
        } else if (epicSequenceDTO.getAfterSequence() == null) {
            if (issueE.getEpicSequence() > epicSequenceDTO.getBeforeSequence()) {
                Integer add = issueE.getEpicSequence() - epicSequenceDTO.getBeforeSequence();
                if (add > 0) {
                    issueE.setEpicSequence(epicSequenceDTO.getBeforeSequence() - 1);
                    issueRepository.update(issueE, new String[]{EPIC_SEQUENCE});
                } else {
                    issueRepository.batchUpdateSequence(epicSequenceDTO.getBeforeSequence(), projectId,
                            issueE.getEpicSequence() - epicSequenceDTO.getBeforeSequence() + 1, issueE.getIssueId());
                }
            }
        } else {
            Integer sequence = epicSequenceDTO.getAfterSequence() + 1;
            issueE.setEpicSequence(sequence);
            issueRepository.update(issueE, new String[]{EPIC_SEQUENCE});
            Integer update = sequence - epicSequenceDTO.getBeforeSequence();
            if (update >= 0) {
                issueRepository.batchUpdateSequence(epicSequenceDTO.getBeforeSequence(), projectId, update + 1, issueE.getIssueId());
            }
        }
    }

    private String getQuickFilter(List<Long> quickFilterIds) {
        List<String> sqlQuerys = quickFilterMapper.selectSqlQueryByIds(quickFilterIds);
        if (sqlQuerys.isEmpty()) {
            return null;
        }
        int idx = 0;
        StringBuilder sql = new StringBuilder("select issue_id from agile_issue where ");
        for (String filter : sqlQuerys) {
            if (idx == 0) {
                sql.append(" ( " + filter + " ) ");
                idx += 1;
            } else {
                sql.append(" and " + " ( " + filter + " ) ");
            }
        }
        return sql.toString();
    }

    @Override
    public List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds) {
        List<StoryMapIssueDTO> storyMapIssueDTOList = null;
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        //保存用户选择的泳道
        handleSaveUserSetting(projectId, type, pageType);
        switch (type) {
            case STORYMAP_TYPE_SPRINT:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdSprint(projectId, pageType, assigneeId, onlyStory, filterSql));
                break;
            case STORYMAP_TYPE_VERSION:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdVersion(projectId, pageType, assigneeId, onlyStory, filterSql));
                break;
            case STORYMAP_TYPE_NONE:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdNone(projectId, pageType, assigneeId, onlyStory, filterSql));
                break;
            default:
                break;
        }
        return storyMapIssueDTOList == null ? new ArrayList<>() : storyMapIssueDTOList;
    }

    private void handleSaveUserSetting(Long projectId, String type, String pageType) {
        if (USERMAP.equals(pageType)) {
            UserSettingDO userSettingDO = new UserSettingDO();
            userSettingDO.setProjectId(projectId);
            userSettingDO.setUserId(DetailsHelper.getUserDetails().getUserId());
            userSettingDO.setTypeCode(STORYMAP);
            UserSettingDO query = userSettingMapper.selectOne(userSettingDO);
            if (query == null) {
                userSettingDO.setStorymapSwimlaneCode(STORYMAP_TYPE_NONE);
                userSettingRepository.create(ConvertHelper.convert(userSettingDO, UserSettingE.class));
            } else if (!query.getStorymapSwimlaneCode().equals(type)) {
                query.setStorymapSwimlaneCode(type);
                userSettingRepository.update(ConvertHelper.convert(query, UserSettingE.class));
            }
        }
    }


    @Override
    public void storymapMove(Long projectId, StoryMapMoveDTO storyMapMoveDTO) {
        MoveIssueDTO moveIssueDTO = new MoveIssueDTO();
        Long sprintId = storyMapMoveDTO.getSprintId();
        Long versionId = storyMapMoveDTO.getVersionId();
        Long epicId = storyMapMoveDTO.getEpicId();
        BeanUtils.copyProperties(storyMapMoveDTO, moveIssueDTO);
        IssueValidator.checkStoryMapMove(storyMapMoveDTO);
        dealRank(projectId, moveIssueDTO);
        if (epicId != null) {
            batchIssueToEpicInStoryMap(projectId, epicId, moveIssueDTO);
        }
        if (sprintId != null) {
            batchIssueToSprintInStoryMap(projectId, sprintId, moveIssueDTO);
        }
        if (versionId != null) {
            batchToVersionInStoryMap(projectId, versionId, moveIssueDTO);
        }
    }

    @Override
    public IssueDTO issueParentIdUpdate(Long projectId, IssueUpdateParentIdDTO issueUpdateParentIdDTO) {
        Long issueId = issueUpdateParentIdDTO.getIssueId();
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
        Long parentIssueId = issueUpdateParentIdDTO.getParentIssueId();
        IssueDO parentIssueDO = issueMapper.selectByPrimaryKey(parentIssueId);
        IssueValidator.checkParentIdUpdate(issueDO, parentIssueDO);
        IssueE updateIssue = new IssueE();
        updateIssue.setIssueId(issueId);
        updateIssue.setObjectVersionNumber(issueUpdateParentIdDTO.getObjectVersionNumber());
        updateIssue.setParentIssueId(issueUpdateParentIdDTO.getParentIssueId());
        return ConvertHelper.convert(issueRepository.updateSelective(updateIssue), IssueDTO.class);
    }

    @Override
    public JSONObject countUnResolveByProjectId(Long projectId) {
        JSONObject result = new JSONObject();
        result.put("all", issueMapper.countIssueByProjectId(projectId));
        result.put("unresolved", issueMapper.countUnResolveByProjectId(projectId));
        return result;
    }

    @Override
    public List<Long> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO) {
        return issueMapper.queryIssueIdsByOptions(projectId, searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs());
    }

    @Override
    public List<UndistributedIssueDTO> queryUnDistributedIssues(Long projectId) {
        return ConvertHelper.convertList(issueMapper.queryUnDistributedIssues(projectId), UndistributedIssueDTO.class);
    }

    @Override
    public List<UnfinishedIssueDTO> queryUnfinishedIssues(Long projectId, Long assigneeId) {
        return ConvertHelper.convertList(issueMapper.queryUnfinishedIssues(projectId, assigneeId), UnfinishedIssueDTO.class);
    }

    @Override
    public String querySwimLaneCode(Long projectId) {
        UserSettingE userSettingE = new UserSettingE();
        userSettingE.initUserSetting(projectId);
        userSettingE.setTypeCode("storymap");
        UserSettingDO query = userSettingMapper.selectOne(ConvertHelper.convert(userSettingE, UserSettingDO.class));
        String result;
        if (query == null) {
            userSettingE.setStorymapSwimlaneCode("none");
            result = userSettingRepository.create(userSettingE).getStorymapSwimlaneCode();
        } else {
            result = query.getStorymapSwimlaneCode();
        }
        return result;
    }

    @Override
    public List<Long> cloneIssuesByVersionId(Long projectId, Long versionId, List<Long> issueIds) {
        List<IssueDetailDO> issueDOList = issueMapper.queryByIssueIds(projectId, issueIds);
        List<Long> newIssueIds = new ArrayList<>();
        if (issueDOList.size() == issueIds.size()) {
            //保证有序性，虽然数据库可以通过函数保证in的有序性，但是测试h2函数不支持，所以代码保证
            Map<Long, IssueDetailDO> issueDetailDOMap = issueDOList.stream().collect(Collectors.toMap(IssueDetailDO::getIssueId, Function.identity()));
            List<IssueCreateDTO> issueCreateDTOS = issueAssembler.issueDetailListDoToDto(issueIds, issueDetailDOMap, versionId);
            issueCreateDTOS.forEach(issueCreateDTO -> {
                IssueDTO newIssue = createIssue(issueCreateDTO);
                newIssueIds.add(newIssue.getIssueId());
            });
        } else {
            throw new CommonException("error.issueServiceImpl.issueTypeError");
        }
        return newIssueIds;
    }

    public String getDes(String str) {
        String result = "";
        if (!"".equals(str) && str != null) {
            String[] arrayLine = str.split(("\\},\\{"));
            String regEx = "\"insert\":\"(.*)\"";
            Pattern pattern = Pattern.compile(regEx);
            for (String s : arrayLine) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    result = result + StringEscapeUtils.unescapeJava(matcher.group(1));
                }
            }
        }
        return result;
    }

    @Override
    public void initMapRank(Long projectId) {
        List<Long> issueIds = issueMapper.selectIssueIdsByProjectId(projectId);
        List<StoryMapMoveIssueDO> mapMoveIssueDOS = new ArrayList<>();
        String mapRank = RankUtil.mid();
        for (Long issueId : issueIds) {
            mapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, mapRank));
            mapRank = RankUtil.genNext(mapRank);
        }
        if (!mapMoveIssueDOS.isEmpty()) {
            issueMapper.updateMapRank(projectId, mapMoveIssueDOS);
        }
    }
}