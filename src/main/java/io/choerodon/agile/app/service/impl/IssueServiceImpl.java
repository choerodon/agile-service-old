package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.event.IssuePayload;
import io.choerodon.agile.domain.agile.rule.IssueLinkRule;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.domain.agile.rule.ProductVersionRule;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.aspect.DataLogRedisUtil;
import io.choerodon.agile.infra.common.enums.ObjectSchemeCode;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.*;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.*;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.base.domain.Sort;
import io.choerodon.core.convertor.ConvertHelper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;

import org.apache.commons.lang.StringEscapeUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private DataLogRepository dataLogRepository;
    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;
    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;
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
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private InstanceFeignClient instanceFeignClient;
    @Autowired
    private FoundationFeignClient foundationFeignClient;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;
    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper;
    @Autowired
    private FeatureMapper featureMapper;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private FeatureCommonAssembler featureCommonAssembler;
    @Autowired
    private SendMsgUtil sendMsgUtil;
    @Autowired
    private BoardFeatureService boardFeatureService;
    @Autowired
    private StoryMapWidthMapper storyMapWidthMapper;
    @Autowired
    private StoryMapMapper storyMapMapper;

    private static final String SUB_TASK = "sub_task";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String COPY = "Copy";
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
    private static final String ISSUE_TYPE_ID = "issueTypeId";
    private static final String EPIC_COLOR_TYPE = "epic_color";
    private static final String STORY_TYPE = "story";
    private static final String ASSIGNEE = "assignee";
    private static final String REPORTER = "reporter";
    private static final String FIELD_RANK = "Rank";
    private static final String RANK_HIGHER = "评级更高";
    private static final String RANK_LOWER = "评级更低";
    private static final String RANK_FIELD = "rank";
    private static final String FIX_RELATION_TYPE = "fix";
    private static final String INFLUENCE_RELATION_TYPE = "influence";
    private static final String[] FIELDS_NAME = {"任务编号", "概要", "描述", "类型", "所属项目", "经办人", "经办人名称", "报告人", "报告人名称", "解决状态", "状态", "冲刺", "创建时间", "最后更新时间", "优先级", "是否子任务", "剩余预估", "版本", "史诗", "标签", "故事点", "模块"};
    private static final String[] FIELDS = {"issueNum", "summary", "description", "typeName", "projectName", "assigneeName", "assigneeRealName", "reporterName", "reporterRealName", "resolution", "statusName", "sprintName", "creationDate", "lastUpdateDate", "priorityName", "subTask", REMAIN_TIME_FIELD, "versionName", "epicName", "labelName", "storyPoints", "componentName"};
    private static final String[] FIELDS_IN_PROGRAM = {"issueNum", "summary", "typeName", "statusName", "piName", "creationDate", "lastUpdateDate", "epicName", "storyPoints", "benfitHypothesis", "acceptanceCritera"};
    private static final String[] FIELDS_NAME_IN_PROGRAM = {"任务编号", "概要", "类型", "状态", "PI", "创建时间", "最后更新时间", "史诗", "故事点", "特性价值", "验收标准"};
    private static final String PROJECT_ERROR = "error.project.notFound";
    private static final String ERROR_ISSUE_NOT_FOUND = "error.Issue.queryIssue";
    private static final String ERROR_PROJECT_INFO_NOT_FOUND = "error.createIssue.projectInfoNotFound";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.createIssue.stateMachineNotFound";
    private static final String SEARCH = "search";
    private static final String STORYMAP_TYPE_SPRINT = "sprint";
    private static final String STORYMAP_TYPE_VERSION = "version";
    private static final String STORYMAP_TYPE_NONE = "none";
    private static final String USERMAP = "usermap";
    private static final String STORYMAP = "storymap";
    private static final String URL_TEMPLATE1 = "#/agile/issue?type=project&id=";
    private static final String URL_TEMPLATE2 = "&name=";
    private static final String URL_TEMPLATE3 = "&paramName=";
    private static final String URL_TEMPLATE4 = "&paramIssueId=";
    private static final String URL_TEMPLATE5 = "&paramOpenIssueId=";
    private static final String URL_TEMPLATE6 = "&organizationId=";
    private static final String AGILE = "agile";
    private static final String FIELD_CODES = "fieldCodes";
    private static final String FIELD_NAMES = "fieldNames";
    private static final String ISSUE_TYPE_FEATURE = "feature";
    private static final String ERROR_PROJECT_NOTEXIST = "error.project.notExist";
    private static final String FEATURE_TYPE_BUSINESS = "business";
    private static final String FEATURE_TYPE_ENABLER = "enabler";

    private ModelMapper modelMapper = new ModelMapper();

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    private SagaClient sagaClient;

    @Autowired
    public IssueServiceImpl(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    public void setSagaClient(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    @Override
    public void setIssueMapper(IssueMapper issueMapper) {
        this.issueMapper = issueMapper;
    }

    @Autowired
    private IssueLinkAssembler issueLinkAssembler;
    @Autowired
    private IssueLinkRule issueLinkRule;
    @Autowired
    private PiMapper piMapper;


    @Override
    public void afterCreateIssue(Long issueId, IssueE issueE, IssueCreateDTO issueCreateDTO, ProjectInfoE projectInfoE) {
        handleCreateIssueRearAction(issueE, issueId, projectInfoE, issueCreateDTO.getLabelIssueRelDTOList(), issueCreateDTO.getComponentIssueRelDTOList(), issueCreateDTO.getVersionIssueRelDTOList(), issueCreateDTO.getIssueLinkCreateDTOList());
    }

    private void handleCreateIssueRearAction(IssueE issueE, Long issueId, ProjectInfoE projectInfoE, List<LabelIssueRelDTO> labelIssueRelDTOList, List<ComponentIssueRelDTO> componentIssueRelDTOList, List<VersionIssueRelDTO> versionIssueRelDTOList, List<IssueLinkCreateDTO> issueLinkCreateDTOList) {
        //处理冲刺
        handleCreateSprintRel(issueE.getSprintId(), issueE.getProjectId(), issueId);
        handleCreateLabelIssue(labelIssueRelDTOList, issueId);
        handleCreateComponentIssueRel(componentIssueRelDTOList, projectInfoE.getProjectId(), issueId, projectInfoE, issueE.getAssigneerCondtiion());
        handleCreateVersionIssueRel(versionIssueRelDTOList, projectInfoE.getProjectId(), issueId);
        handleCreateIssueLink(issueLinkCreateDTOList, projectInfoE.getProjectId(), issueId);
    }

    @Override
    public void afterCreateSubIssue(Long issueId, IssueE subIssueE, IssueSubCreateDTO issueSubCreateDTO, ProjectInfoE projectInfoE) {
        handleCreateIssueRearAction(subIssueE, issueId, projectInfoE, issueSubCreateDTO.getLabelIssueRelDTOList(), issueSubCreateDTO.getComponentIssueRelDTOList(), issueSubCreateDTO.getVersionIssueRelDTOList(), issueSubCreateDTO.getIssueLinkCreateDTOList());
    }

    @Override
    public void handleInitIssue(IssueE issueE, Long statusId, ProjectInfoE projectInfoE) {
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
        projectInfoRepository.updateIssueMaxNum(issueE.getProjectId(), issueE.getIssueNum());
        //初始化排序
        if (issueE.isIssueRank()) {
            calculationRank(issueE.getProjectId(), issueE);
        }
        // 初始化feature排序
        if (issueE.isProgramRank()) {
            calculationProgramRank(issueE);
        }
        if (issueE.isIssueMapRank()) {
            calculationMapRank(issueE);
        }
        issueRule.verifyStoryPoints(issueE);
    }

    private void calculationProgramRank(IssueE issueE) {
        if (piMapper.hasPiIssue(issueE.getProgramId(), issueE.getPiId())) {
            String rank = piMapper.queryPiMaxRank(issueE.getProgramId(), issueE.getPiId());
            issueE.setRank(RankUtil.genNext(rank));
        } else {
            issueE.setRank(RankUtil.mid());
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
    public IssueDTO queryIssueCreate(Long projectId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        IssueDTO result = issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
        sendMsgUtil.sendMsgByIssueCreate(projectId, result);
        return result;
    }

    private PriorityDTO getPriorityById(Long organizationId, Long priorityId) {
        ResponseEntity<PriorityDTO> priorityDTOResponseEntity = issueFeignClient.queryById(organizationId, priorityId);
        if (priorityDTOResponseEntity == null) {
            throw new CommonException("error.priority.get");
        }
        return priorityDTOResponseEntity.getBody();
    }

    private IssueTypeDTO getIssueTypeById(Long organizationId, Long issueTypeId) {
        ResponseEntity<IssueTypeDTO> issueTypeDTOResponseEntity = issueFeignClient.queryIssueTypeById(organizationId, issueTypeId);
        if (issueTypeDTOResponseEntity == null) {
            throw new CommonException("error.issueType.get");
        }
        return issueTypeDTOResponseEntity.getBody();
    }

    private StatusMapDTO getStatusById(Long organizationId, Long statusId) {
        ResponseEntity<StatusMapDTO> statusInfoDTOResponseEntity = stateMachineFeignClient.queryStatusById(organizationId, statusId);
        if (statusInfoDTOResponseEntity == null) {
            throw new CommonException("error.status.get");
        }
        return statusInfoDTOResponseEntity.getBody();
    }

    @Override
    public IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        if (ISSUE_TYPE_FEATURE.equals(issue.getTypeCode())) {
            FeatureDO featureDO = new FeatureDO();
            featureDO.setIssueId(issue.getIssueId());
            FeatureDO res = featureMapper.selectOne(featureDO);
            if (res != null) {
                issue.setFeatureDO(res);
            }
        }
        if (STORY_TYPE.equals(issue.getTypeCode()) && issue.getFeatureId() != null) {
            IssueDO issueInfo = issueMapper.selectByPrimaryKey(issue.getFeatureId());
            if (issueInfo != null) {
                issue.setFeatureName(issueInfo.getSummary());
            }
        }
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        return issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
    }

    private IssueDTO queryIssueByUpdate(Long projectId, Long issueId, List<String> fieldList) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        if (STORY_TYPE.equals(issue.getTypeCode()) && issue.getFeatureId() != null) {
            IssueDO issueInfo = issueMapper.selectByPrimaryKey(issue.getFeatureId());
            if (issueInfo != null) {
                issue.setFeatureName(issueInfo.getSummary());
            }
        }
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        IssueDTO result = issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
        sendMsgUtil.sendMsgByIssueAssignee(projectId, fieldList, result);
        // return feature extends table info
        if (ISSUE_TYPE_FEATURE.equals(result.getTypeCode())) {
            FeatureDO featureDO = new FeatureDO();
            featureDO.setIssueId(result.getIssueId());
            FeatureDO res = featureMapper.selectOne(featureDO);
            if (res != null) {
                result.setFeatureDTO(ConvertHelper.convert(res, FeatureDTO.class));
            }
        }
        sendMsgUtil.sendMsgByIssueComplete(projectId, fieldList, result);
        return result;
    }

    @Override
    public PageInfo<IssueListDTO> listIssueWithSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        if (organizationId == null) {
            organizationId = ConvertUtil.getOrganizationId(projectId);
        }
        //处理用户搜索
        Boolean condition = handleSearchUser(searchDTO, projectId);
        if (condition) {
            //处理表映射
            String filterSql = null;
            //处理自定义搜索
            if (searchDTO.getQuickFilterIds() != null && !searchDTO.getQuickFilterIds().isEmpty()) {
                filterSql = getQuickFilter(searchDTO.getQuickFilterIds());
            }
            //处理未匹配的筛选
            handleOtherArgs(searchDTO);
            final String searchSql = filterSql;
            for (Sort.Order order : pageRequest.getSort()) {
                if (order.getProperty().equals("issueId")) {
                    (order).setProperty("search.issue_issue_id");
                }
            }
            PageInfo<Long> issueIdPage = PageHelper.startPage(pageRequest.getPage(),
                    pageRequest.getSize(), pageRequest.getSort().toSql()).doSelectPageInfo(() -> issueMapper.queryIssueIdsListWithSub
                    (projectId, searchDTO, searchSql, searchDTO.getAssigneeFilterIds()));
            PageInfo<IssueListDTO> issueListDTOPage;
            if (issueIdPage.getList() != null && !issueIdPage.getList().isEmpty()) {
                List<IssueDO> issueDOList = issueMapper.queryIssueListWithSubByIssueIds(issueIdPage.getList());
                Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
                Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
                Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
                issueListDTOPage = PageUtil.buildPageInfoWithPageInfoList(issueIdPage,
                        issueAssembler.issueDoToIssueListDto(issueDOList, priorityMap, statusMapDTOMap, issueTypeDTOMap));
            } else {
                issueListDTOPage = new PageInfo<>();
            }
            return issueListDTOPage;
        } else {
            return new PageInfo<>();
        }
    }

    private void handleOtherArgs(SearchDTO searchDTO) {
        Map<String, Object> otherArgs = searchDTO.getOtherArgs();
        if (otherArgs != null) {
            List<String> list = (List<String>) otherArgs.get("sprint");
            if (list != null && list.contains("0")) {
                otherArgs.put("sprintNull", true);
            }
            list = (List<String>) otherArgs.get("version");
            if (list != null && list.contains("0")) {
                otherArgs.put("versionNull", true);
            }
            list = (List<String>) otherArgs.get("component");
            if (list != null && list.contains("0")) {
                otherArgs.put("componentNull", true);
            }
            list = (List<String>) otherArgs.get("epic");
            if (list != null && list.contains("0")) {
                otherArgs.put("epicNull", true);
            }
            list = (List<String>) otherArgs.get("label");
            if (list != null && list.contains("0")) {
                otherArgs.put("labelNull", true);
            }
            list = (List<String>) otherArgs.get("assigneeId");
            if (list != null && list.contains("0")) {
                otherArgs.put("assigneeIdNull", true);
            }
        }
    }

    @Override
    public Boolean handleSearchUser(SearchDTO searchDTO, Long projectId) {
        if (searchDTO.getSearchArgs() != null && searchDTO.getSearchArgs().get(ASSIGNEE) != null) {
            String userName = (String) searchDTO.getSearchArgs().get(ASSIGNEE);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userRepository.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchDTO.getAdvancedSearchArgs().put("assigneeIds", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        if (searchDTO.getSearchArgs() != null && searchDTO.getSearchArgs().get(REPORTER) != null) {
            String userName = (String) searchDTO.getSearchArgs().get(REPORTER);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userRepository.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchDTO.getAdvancedSearchArgs().put("reporterIds", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean checkEpicNameUpdate(Long projectId, Long issueId, String epicName) {
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
        if (epicName.equals(issueDO.getEpicName())) {
            return false;
        }
        IssueDO check = new IssueDO();
        check.setProjectId(projectId);
        check.setEpicName(epicName);
        List<IssueDO> issueDOList = issueMapper.select(check);
        return issueDOList != null && !issueDOList.isEmpty();
    }

    @Override
    public IssueDTO updateIssue(Long projectId, IssueUpdateDTO issueUpdateDTO, List<String> fieldList) {
        if (fieldList.contains("epicName") && issueUpdateDTO.getEpicName() != null && checkEpicNameUpdate(projectId, issueUpdateDTO.getIssueId(), issueUpdateDTO.getEpicName())) {
            throw new CommonException("error.epicName.exist");
        }
        if (!fieldList.isEmpty()) {
            //处理issue自己字段
            handleUpdateIssue(issueUpdateDTO, fieldList, projectId);
        }
        Long issueId = issueUpdateDTO.getIssueId();
        handleUpdateLabelIssue(issueUpdateDTO.getLabelIssueRelDTOList(), issueId, projectId);
        handleUpdateComponentIssueRel(issueUpdateDTO.getComponentIssueRelDTOList(), projectId, issueId);
        handleUpdateVersionIssueRel(issueUpdateDTO.getVersionIssueRelDTOList(), projectId, issueId, issueUpdateDTO.getVersionType());
        return queryIssueByUpdate(projectId, issueId, fieldList);
    }

    @Override
    public IssueDTO updateIssueStatus(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType) {
        stateMachineService.executeTransform(projectId, issueId, transformId, objectVersionNumber, applyType, new InputDTO(issueId, "updateStatus", null));
        if ("agile".equals(applyType)) {
            IssueE issueE = new IssueE();
            issueE.setIssueId(issueId);
            issueE.setStayDate(new Date());
            issueE.setObjectVersionNumber(issueMapper.selectByPrimaryKey(issueId).getObjectVersionNumber());
            issueRepository.updateSelective(issueE);
        }
        return queryIssueByUpdate(projectId, issueId, Collections.singletonList("statusId"));
    }

    @Override
    public void handleUpdateIssue(IssueUpdateDTO issueUpdateDTO, List<String> fieldList, Long projectId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        IssueDO originIssue = issueMapper.queryIssueWithNoCloseSprint(issueUpdateDTO.getIssueId());
        IssueE issueE = issueAssembler.toTarget(issueUpdateDTO, IssueE.class);
        //处理用户，前端可能会传0，处理为null
        issueE.initializationIssueUser();
        if (fieldList.contains(SPRINT_ID_FIELD)) {
            IssueE oldIssue = ConvertHelper.convert(originIssue, IssueE.class);
            //处理子任务的冲刺
            List<Long> issueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
            List<Long> subBugIds = issueMapper.querySubBugIdsByIssueId(projectId, issueE.getIssueId());
            if (subBugIds != null && !subBugIds.isEmpty()) {
                issueIds.addAll(subBugIds);
            }
            Boolean exitSprint = issueE.getSprintId() != null && !Objects.equals(issueE.getSprintId(), 0L);
            Boolean condition = (!Objects.equals(oldIssue.getSprintId(), issueUpdateDTO.getSprintId()));
            issueIds.add(issueE.getIssueId());
            if (condition) {
                BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, issueE.getSprintId(), issueIds);
                issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
//                //不是活跃冲刺，修改冲刺状态回到第一个状态
//                handleIssueStatus(projectId, oldIssue, issueE, fieldList, issueIds);
            }
            if (exitSprint) {
//                if (oldIssue.getSprintId() == null || oldIssue.getSprintId() == 0) {
//                    issueIds.add(issueE.getIssueId());
//                }
                issueRepository.issueToDestinationByIds(projectId, issueE.getSprintId(), issueIds, new Date(), customUserDetails.getUserId());
            }
            if (oldIssue.isIssueRank()) {
                calculationRank(projectId, issueE);
                fieldList.add(RANK_FIELD);
                issueE.setOriginSprintId(originIssue.getSprintId());
            }
        }
        if (STORY_TYPE.equals(originIssue.getTypeCode()) && fieldList.contains("featureId")) {
            if (Objects.equals(issueUpdateDTO.getFeatureId(), 0L) && !Objects.equals(originIssue.getEpicId(), 0L)) {
                issueE.setEpicId(0L);
                fieldList.add("epicId");
            } else if (!Objects.equals(issueUpdateDTO.getFeatureId(), 0L)) {
                IssueDO featureUpdate = issueMapper.selectByPrimaryKey(issueUpdateDTO.getFeatureId());
                issueE.setEpicId(featureUpdate.getEpicId() == null ? 0L : featureUpdate.getEpicId());
                fieldList.add("epicId");
            }
        }
        if ("feature".equals(originIssue.getTypeCode()) && fieldList.contains("epicId")) {
            if (Objects.equals(issueUpdateDTO.getEpicId(), 0L) && !Objects.equals(originIssue.getEpicId(), 0L)) {
                issueRepository.updateEpicIdOfStoryByFeature(issueUpdateDTO.getIssueId(), issueUpdateDTO.getEpicId());
            } else if (!Objects.equals(issueUpdateDTO.getEpicId(), 0L)) {
                issueRepository.updateEpicIdOfStoryByFeature(issueUpdateDTO.getIssueId(), issueUpdateDTO.getEpicId());
            }
        }
        issueRepository.update(issueE, fieldList.toArray(new String[fieldList.size()]));
        if (issueUpdateDTO.getFeatureDTO() != null && issueUpdateDTO.getFeatureDTO().getIssueId() != null) {
            FeatureDTO featureDTO = issueUpdateDTO.getFeatureDTO();
            if (featureDTO != null) {
                featureRepository.updateSelective(ConvertHelper.convert(featureDTO, FeatureE.class));
            }
        }
    }

    private void handleIssueStatus(Long projectId, IssueE oldIssue, IssueE issueE, List<String> fieldList, List<Long> issueIds) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (oldIssue.getApplyType().equals(SchemeApplyType.AGILE)) {
            if (sprintSearchDO == null || !Objects.equals(issueE.getSprintId(), sprintSearchDO.getSprintId())) {
                Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, oldIssue.getIssueTypeId()).getBody();
                if (stateMachineId == null) {
                    throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
                }
                Long initStatusId = instanceFeignClient.queryInitStatusId(ConvertUtil.getOrganizationId(projectId), stateMachineId).getBody();
                if (issueE.getStatusId() == null && !oldIssue.getStatusId().equals(initStatusId)) {
                    issueE.setStatusId(initStatusId);
                    fieldList.add(STATUS_ID);
                }
                //子任务的处理
                if (issueIds != null && !issueIds.isEmpty()) {
                    List<IssueE> issueDOList = issueAssembler.toTargetList(issueMapper.queryIssueSubList(projectId, oldIssue.getIssueId()), IssueE.class);
                    String[] field = {STATUS_ID};
                    issueDOList.forEach(issue -> {
                        if (!issue.getStatusId().equals(initStatusId)) {
                            issue.setStatusId(initStatusId);
                            issueRepository.update(issue, field);
                        }
                    });
                }
            }
        }
    }


    @Override
    public List<EpicDataDTO> listEpic(Long projectId) {
        List<EpicDataDTO> epicDataList = epicDataAssembler.toTargetList(issueMapper.queryEpicList(projectId), EpicDataDTO.class);
        ProjectDTO program = userRepository.getGroupInfoByEnableProject(ConvertUtil.getOrganizationId(projectId), projectId);
        List<EpicDataDTO> programEpics = null;
        if (program != null) {
            programEpics = epicDataAssembler.toTargetList(issueMapper.selectEpicByProgram(program.getId()), EpicDataDTO.class);
            if (programEpics != null && !programEpics.isEmpty()) {
                epicDataList.addAll(programEpics);
            }
        }
        if (!epicDataList.isEmpty()) {
            List<Long> epicIds = epicDataList.stream().map(EpicDataDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = issueMapper.queryIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = issueMapper.queryDoneIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryNotEstimateIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, BigDecimal> totalEstimateMap = issueMapper.queryTotalEstimateByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
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
    public List<EpicDataDTO> listProgramEpic(Long programId) {
        List<EpicDataDTO> epicDataList = epicDataAssembler.toTargetList(issueMapper.queryProgramEpicList(programId), EpicDataDTO.class);
        if (!epicDataList.isEmpty()) {
            List<Long> epicIds = epicDataList.stream().map(EpicDataDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = issueMapper.queryProgramIssueCountByEpicIds(programId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = issueMapper.queryProgramDoneIssueCountByEpicIds(programId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryProgramNotEstimateIssueCountByEpicIds(programId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, BigDecimal> totalEstimateMap = issueMapper.queryProgramTotalEstimateByEpicIds(programId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
            epicDataList.forEach(epicData -> {
                epicData.setIssueCount(issueCountMap.get(epicData.getIssueId()));
                epicData.setDoneIssueCount(doneIssueCountMap.get(epicData.getIssueId()));
                epicData.setTotalEstimate(totalEstimateMap.get(epicData.getIssueId()));
                epicData.setNotEstimate(notEstimateIssueCountMap.get(epicData.getIssueId()));
            });
        }
        return epicDataList;
    }

    @Override
    public List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Long organizationId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds) {
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<StoryMapEpicDTO> storyMapEpicDTOList = ConvertHelper.convertList(issueMapper.queryStoryMapEpicList(projectId, showDoneEpic, assigneeId, onlyStory, filterSql), StoryMapEpicDTO.class);
        if (!storyMapEpicDTOList.isEmpty()) {
            Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            List<Long> epicIds = storyMapEpicDTOList.stream().map(StoryMapEpicDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = issueMapper.queryIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = issueMapper.queryDoneIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryNotEstimateIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, BigDecimal> totalEstimateMap = issueMapper.queryTotalEstimateByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
            storyMapEpicDTOList.forEach(epicData -> {
                epicData.setStatusMapDTO(statusMapDTOMap.get(epicData.getStatusId()));
                epicData.setIssueTypeDTO(issueTypeDTOMap.get(epicData.getIssueTypeId()));
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

    private void deleteStoryMapWidth(Long issueId) {
        StoryMapWidthDO selectMapWidthDO = new StoryMapWidthDO();
        selectMapWidthDO.setIssueId(issueId);
        selectMapWidthDO.setType("feature");
        List<StoryMapWidthDO> storyMapWidthDOList = storyMapWidthMapper.select(selectMapWidthDO);
        if (storyMapWidthDOList != null && !storyMapWidthDOList.isEmpty()) {
            storyMapWidthMapper.delete(selectMapWidthDO);
        }
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
        issueLabelRepository.labelGarbageCollection(projectId);
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
        //删除公告板特性及依赖
        boardFeatureService.deleteByFeatureId(projectId, issueId);

        if (ISSUE_TYPE_FEATURE.equals(issueE.getTypeCode())) {
            featureRepository.delete(issueId);
        }
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
        // 如果是删除feature，将其下的issue的featureId置为0
        if ("feature".equals(issueE.getTypeCode())) {
            issueRepository.updateEpicIdOfStoryByFeature(issueE.getIssueId(), 0L);
            issueMapper.updateFeatureIdOfStoryByFeature(issueE.getIssueId(), 0L);
            // 删除故事地图扩列
            deleteStoryMapWidth(issueE.getIssueId());
        }
        //删除日志信息
        dataLogDeleteByIssueId(projectId, issueId);
        issueRepository.delete(projectId, issueE.getIssueId());
        //删除issue发送消息
        IssuePayload issuePayload = new IssuePayload();
        issuePayload.setIssueId(issueId);
        issuePayload.setProjectId(projectId);
        sagaClient.startSaga("agile-delete-issue", new StartInstanceDTO(JSON.toJSONString(issuePayload), "", "", ResourceLevel.PROJECT.value(), projectId));
        //delete cache
        dataLogRedisUtil.handleDeleteRedisByDeleteIssue(projectId);
    }

    @Override
    public void batchDeleteIssuesAgile(Long projectId, List<Long> issueIds) {
        if (issueMapper.queryIssueIdsIsNotTest(projectId, issueIds) != issueIds.size()) {
            throw new CommonException("error.Issue.type.isNotIssueTest");
        }
//        List<Long> issueIdList = issueMapper.queryIssueSubListByIssueIds(projectId, issueIds);
//        issueIds.addAll(issueIdList);
        issueMapper.batchDeleteIssues(projectId, issueIds);
//        issueIds.forEach(issueId -> deleteIssueInfo(issueId, projectId));
        dataLogRedisUtil.deleteByDeleteIssueInfo(projectId);
    }

    @Override
    public void batchDeleteIssues(Long projectId, List<Long> issueIds) {
        if (issueMapper.queryIssueIdsIsTest(projectId, issueIds) != issueIds.size()) {
            throw new CommonException("error.Issue.type.isNotIssueTest");
        }
        List<Long> issueIdList = issueMapper.queryIssueSubListByIssueIds(projectId, issueIds);
        issueIds.addAll(issueIdList);
        issueMapper.batchDeleteIssues(projectId, issueIds);
        issueIds.forEach(issueId -> deleteIssueInfo(issueId, projectId));
        //delete cache
        dataLogRedisUtil.deleteByDeleteIssueInfo(projectId);
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

    @Override
    public void handleInitSubIssue(IssueE subIssueE, Long statusId, ProjectInfoE projectInfoE) {
        IssueE parentIssueE = ConvertHelper.convert(issueMapper.queryIssueSprintNotClosed(subIssueE.getProjectId(), subIssueE.getParentIssueId()), IssueE.class);
        //设置初始状态,跟随父类状态
        subIssueE = parentIssueE.initializationSubIssue(subIssueE, statusId, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(subIssueE.getProjectId(), subIssueE.getIssueNum());
        //初始化排序
        if (subIssueE.isIssueRank()) {
            calculationRank(subIssueE.getProjectId(), subIssueE);
        }
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExist(projectId, versionId);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        } else {
            issueRepository.batchRemoveVersion(projectId, issueIds);
        }
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public void batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionRule.judgeExist(projectId, versionId);
            if (issueMapper.queryIssueIdsIsTest(projectId, issueIds) != issueIds.size()) {
                throw new CommonException("error.Issue.type.isNotIssueTest");
            }
            issueRepository.batchRemoveVersionTest(projectId, issueIds);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueRepository.batchIssueToVersion(versionIssueRelE);
        }
    }

    @Override
    public void batchToVersionInStoryMap(Long projectId, Long versionId, StoryMapMoveDTO storyMapMoveDTO) {
        List<Long> issueIds = storyMapMoveDTO.getVersionIssueIds();
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
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public void batchStoryToFeature(Long projectId, Long featureId, List<Long> issueIds) {
        issueRule.checkBatchStoryToFeature(featureId);
        List<Long> filterIds = issueMapper.filterStoryIds(projectId, issueIds);
        if (filterIds != null && !filterIds.isEmpty()) {
            IssueDO feature = issueMapper.selectByPrimaryKey(featureId);
            Long updateEpicId = (feature.getEpicId() == null ? 0L : feature.getEpicId());
            issueRepository.batchStoryToFeature(projectId, featureId, filterIds, updateEpicId);
        }
    }

    @Override
    public List<IssueSearchDTO> batchIssueToEpicInStoryMap(Long projectId, Long epicId, StoryMapMoveDTO storyMapMoveDTO) {
        List<Long> issueIds = storyMapMoveDTO.getEpicIssueIds();
        issueRule.judgeExist(projectId, epicId);
        issueRepository.batchIssueToEpic(projectId, epicId, issueIds);
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private void dataLogRank(Long projectId, MoveIssueDTO moveIssueDTO, String rankStr, Long sprintId) {
        for (Long issueId : moveIssueDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.toTarget(issueMapper.queryActiveSprintNameByIssueId(issueId), SprintNameDTO.class);
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
        //处理子任务与子缺陷
        List<Long> subTaskIds = issueMapper.querySubIssueIds(projectId, moveIssueIds);
        List<Long> subBugIds = issueMapper.querySubBugIds(projectId, moveIssueIds);
        if (subTaskIds != null && !subTaskIds.isEmpty()) {
            moveIssueIds.addAll(subTaskIds);
        }
        if (subBugIds != null && !subBugIds.isEmpty()) {
            moveIssueIds.addAll(subBugIds);
        }
        //把与现在冲刺与要移动的冲刺相同的issue排除掉
        List<IssueSearchDO> issueSearchDOList = issueMapper.queryIssueByIssueIds(projectId, moveIssueDTO.getIssueIds()).stream()
                .filter(issueDO -> issueDO.getSprintId() == null ? sprintId != 0 : !issueDO.getSprintId().equals(sprintId)).collect(Collectors.toList());
        if (issueSearchDOList != null && !issueSearchDOList.isEmpty()) {
            List<Long> moveIssueIdsFilter = issueSearchDOList.stream().map(IssueSearchDO::getIssueId).collect(Collectors.toList());
            BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIdsFilter);
            issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
            if (sprintId != null && !Objects.equals(sprintId, 0L)) {
                issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIdsFilter, new Date(), customUserDetails.getUserId());
            }
//            //如果移动冲刺不是活跃冲刺，则状态回到默认状态
//            batchHandleIssueStatus(projectId, moveIssueIdsFilter, sprintId);
            List<Long> assigneeIds = issueSearchDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDO::getAssigneeId).distinct().collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
            return issueSearchAssembler.doListToDTO(issueSearchDOList, usersMap, new HashMap<>(), new HashMap<>(), new HashMap<>());
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public void batchHandleIssueStatus(Long projectId, List<Long> moveIssueIds, Long sprintId) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDO == null || !Objects.equals(sprintId, sprintSearchDO.getSprintId())) {
            List<IssueE> issueEList = issueAssembler.toTargetList(issueMapper.queryIssueByIssueIdsAndSubIssueIds(moveIssueIds), IssueE.class);
            Map<Long, IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIdDTOMap = ConvertUtil.queryIssueTypesWithStateMachineIdByProjectId(projectId, AGILE);
            issueEList.forEach(issueE -> {
                Long initStatusId = issueTypeWithStateMachineIdDTOMap.get(issueE.getIssueTypeId()).getInitStatusId();
                if (!issueE.getStatusId().equals(initStatusId)) {
                    issueE.setStatusId(initStatusId);
                    issueRepository.update(issueE, new String[]{STATUS_ID});
                }
            });
        }
    }

    private void dealBoundBeforeRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String minRank = issueMapper.selectMinRankByProjectId(projectId);
        if (minRank == null) {
            initMapRank(projectId);
            minRank = issueMapper.selectMinRankByProjectId(projectId);
        }
        Collections.reverse(storyMapMoveDTO.getIssueIds());
        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
            minRank = RankUtil.genPre(minRank);
            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, minRank));
        }
    }

    private void dealBoundAfterRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String maxRank = issueMapper.selectMaxRankByProjectId(projectId);
        if (maxRank == null) {
            initMapRank(projectId);
            maxRank = issueMapper.selectMaxRankByProjectId(projectId);
        }
        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
            maxRank = RankUtil.genNext(maxRank);
            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, maxRank));
        }
    }

    private void dealBoundRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        if (storyMapMoveDTO.getBefore()) {
            dealBoundBeforeRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
        } else {
            dealBoundAfterRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
        }
    }

    private void dealMapRankLengthTooLarge(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String maxRank = issueMapper.selectMaxRankByProjectId(projectId);
        storyMapMoveIssueDOS.clear();
        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
            maxRank = RankUtil.genNext(maxRank);
            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, maxRank));
        }
    }

    private void dealInnerBeforeRank(Long projectId, String currentMapRank, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String leftMaxRank = issueMapper.selectLeftMaxMapRank(projectId, currentMapRank);
        List<Long> issueIds = storyMapMoveDTO.getIssueIds();
        Collections.reverse(issueIds);
        if (leftMaxRank == null) {
            for (Long issueId : issueIds) {
                currentMapRank = RankUtil.genPre(currentMapRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        } else {
            for (Long issueId : issueIds) {
                leftMaxRank = RankUtil.between(leftMaxRank, currentMapRank);
                if (leftMaxRank.length() >= 700) {
                    dealMapRankLengthTooLarge(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
                    return;
                }
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, leftMaxRank));
            }
        }
    }

    private void dealInnerAfterRank(Long projectId, String currentMapRank, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String rightMinRank = issueMapper.selectRightMinMapRank(projectId, currentMapRank);
        if (rightMinRank == null) {
            for (Long issueId : storyMapMoveDTO.getIssueIds()) {
                currentMapRank = RankUtil.genNext(currentMapRank);
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        } else {
            for (Long issueId : storyMapMoveDTO.getIssueIds()) {
                currentMapRank = RankUtil.between(currentMapRank, rightMinRank);
                if (currentMapRank.length() >= 700) {
                    dealMapRankLengthTooLarge(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
                    return;
                }
                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
            }
        }
    }

    private void dealInnerRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
        String currentMapRank = issueMapper.selectMapRankByIssueId(projectId, storyMapMoveDTO.getOutsetIssueId());
        if (currentMapRank == null) {
            initMapRank(projectId);
            currentMapRank = issueMapper.selectMapRankByIssueId(projectId, storyMapMoveDTO.getOutsetIssueId());
        }
        if (storyMapMoveDTO.getBefore()) {
            dealInnerBeforeRank(projectId, currentMapRank, storyMapMoveDTO, storyMapMoveIssueDOS);
        } else {
            dealInnerAfterRank(projectId, currentMapRank, storyMapMoveDTO, storyMapMoveIssueDOS);
        }
    }

    private void dealRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO) {
        List<StoryMapMoveIssueDO> storyMapMoveIssueDOS = new ArrayList<>();
        if (storyMapMoveDTO.getOutsetIssueId() == null || Objects.equals(storyMapMoveDTO.getOutsetIssueId(), 0L)) {
            dealBoundRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
        } else {
            dealInnerRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
        }
        issueRepository.batchUpdateMapIssueRank(projectId, storyMapMoveIssueDOS);
    }

    @Override
    public List<IssueSearchDTO> batchIssueToSprintInStoryMap(Long projectId, Long sprintId, StoryMapMoveDTO storyMapMoveDTO) {
        sprintRule.judgeExist(projectId, sprintId);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        //处理评级日志
        if (storyMapMoveDTO.getRankIndex() != null && !storyMapMoveDTO.getRankIndex()) {
            dataLogRankInStoryMap(projectId, storyMapMoveDTO, RANK_LOWER, sprintId);
        } else if (storyMapMoveDTO.getRankIndex() != null && storyMapMoveDTO.getRankIndex()) {
            dataLogRankInStoryMap(projectId, storyMapMoveDTO, RANK_HIGHER, sprintId);
        }
        List<Long> moveIssueIds = storyMapMoveDTO.getSprintIssueIds();
        //处理子任务和子缺陷
        List<Long> subTaskIds = issueMapper.querySubIssueIds(projectId, moveIssueIds);
        List<Long> subBugIds = issueMapper.querySubBugIds(projectId, moveIssueIds);
        if (subTaskIds != null && !subTaskIds.isEmpty()) {
            moveIssueIds.addAll(subTaskIds);
        }
        if (subBugIds != null && !subBugIds.isEmpty()) {
            moveIssueIds.addAll(subBugIds);
        }
        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIds);
        issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
            issueRepository.issueToDestinationByIds(projectId, sprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
        }
        List<IssueSearchDO> issueSearchDOList = issueMapper.queryIssueByIssueIds(projectId, storyMapMoveDTO.getSprintIssueIds());
        List<Long> assigneeIds = issueSearchDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        return issueSearchAssembler.doListToDTO(issueSearchDOList, usersMap, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private void dataLogRankInStoryMap(Long projectId, StoryMapMoveDTO storyMapMoveDTO, String rankStr, Long sprintId) {
        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
            SprintNameDTO activeSprintName = sprintNameAssembler.toTarget(issueMapper.queryActiveSprintNameByIssueId(issueId), SprintNameDTO.class);
            Boolean condition = (sprintId == 0 && activeSprintName == null) || (activeSprintName != null
                    && sprintId.equals(activeSprintName.getSprintId()));
            if (condition) {
                DataLogE dataLogE = new DataLogE();
                dataLogE.setField(FIELD_RANK);
                dataLogE.setProjectId(projectId);
                dataLogE.setIssueId(issueId);
                dataLogE.setNewString(rankStr);
                dataLogRepository.create(dataLogE);
            }
        }
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
        String rightRank = issueMapper.queryRank(projectId, moveIssueDTO.getOutsetIssueId());
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
        String leftRank = issueMapper.queryRank(projectId, moveIssueDTO.getOutsetIssueId());
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
        return issueAssembler.toTargetList(issueMapper.queryIssueEpicSelectList(projectId), IssueEpicDTO.class);
    }

    @Override
    public List<IssueFeatureDTO> listFeatureSelectData(Long projectId, Long organizationId, Long epicId) {
        ProjectDTO program = userRepository.getGroupInfoByEnableProject(organizationId, projectId);
        if (program != null) {
            return issueAssembler.toTargetList(issueMapper.queryIssueFeatureSelectList(program.getId(), epicId), IssueFeatureDTO.class);
        } else {
            return issueAssembler.toTargetList(issueMapper.selectFeatureListByAgileProject(projectId), IssueFeatureDTO.class);
        }
    }

    private void setFeatureStatisticDetail(Long projectId, List<IssueFeatureDTO> featureList) {
        if (featureList != null && !featureList.isEmpty()) {
            List<Long> ids = featureList.stream().map(IssueFeatureDTO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> storyCountMap = issueMapper.selectStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> completedStoryCountMap = issueMapper.selectCompletedStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> unestimateStoryCountMap = issueMapper.selectUnEstimateStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, BigDecimal> totalStoryPointsMap = issueMapper.selectTotalStoryPointsByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
            featureList.forEach(issueFeatureDTO -> {
                issueFeatureDTO.setStoryCount(storyCountMap.get(issueFeatureDTO.getIssueId()) == null ? 0 : storyCountMap.get(issueFeatureDTO.getIssueId()));
                issueFeatureDTO.setStoryCompletedCount(completedStoryCountMap.get(issueFeatureDTO.getIssueId()) == null ? 0 : completedStoryCountMap.get(issueFeatureDTO.getIssueId()));
                issueFeatureDTO.setUnEstimateStoryCount(unestimateStoryCountMap.get(issueFeatureDTO.getIssueId()) == null ? 0 : unestimateStoryCountMap.get(issueFeatureDTO.getIssueId()));
                issueFeatureDTO.setTotalStoryPoints(totalStoryPointsMap.get(issueFeatureDTO.getIssueId()) == null ? new BigDecimal(0) : totalStoryPointsMap.get(issueFeatureDTO.getIssueId()));
            });
        }
    }

    @Override
    public List<IssueFeatureDTO> listFeature(Long projectId, Long organizationId) {
        ProjectDTO program = userRepository.getGroupInfoByEnableProject(organizationId, projectId);
        if (program != null) {
            List<IssueDO> programFeatureList = issueMapper.queryIssueFeatureSelectList(program.getId(), null);
            List<IssueFeatureDTO> issueFeatureDTOList = issueAssembler.toTargetList(programFeatureList, IssueFeatureDTO.class);
            setFeatureStatisticDetail(projectId, issueFeatureDTOList);
            return issueFeatureDTOList;
        } else {
            List<IssueDO> projectFeatureList = issueMapper.selectFeatureListByAgileProject(projectId);
            List<IssueFeatureDTO> featureDTOList = issueAssembler.toTargetList(projectFeatureList, IssueFeatureDTO.class);
            setFeatureStatisticDetail(projectId, featureDTOList);
            return featureDTOList;
        }
    }

    @Override
    public List<IssueEpicDTO> listEpicSelectProgramData(Long programId) {
        return issueAssembler.toTargetList(issueMapper.listEpicSelectProgramData(programId), IssueEpicDTO.class);
    }

    @Override
    public IssueSubDTO queryIssueSub(Long projectId, Long organizationId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        issue.setPriorityDTO(getPriorityById(organizationId, issue.getPriorityId()));
        issue.setIssueTypeDTO(getIssueTypeById(organizationId, issue.getIssueTypeId()));
        issue.setStatusMapDTO(getStatusById(organizationId, issue.getStatusId()));
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        return issueAssembler.issueDetailDoToIssueSubDto(issue);
    }

    @Override
    public IssueSubDTO queryIssueSubByCreate(Long projectId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDOList() != null && !issue.getIssueAttachmentDOList().isEmpty()) {
            issue.getIssueAttachmentDOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        IssueSubDTO result = issueAssembler.issueDetailDoToIssueSubDto(issue);
        sendMsgUtil.sendMsgBySubIssueCreate(projectId, result);
        return result;
    }

    @Override
    public synchronized IssueDTO updateIssueTypeCode(IssueE issueE, IssueUpdateTypeDTO issueUpdateTypeDTO, Long organizationId) {
        String originType = issueE.getTypeCode();
        if (originType.equals(SUB_TASK)) {
            issueE.setParentIssueId(null);
        }
        if (STORY_TYPE.equals(issueE.getTypeCode()) && issueE.getStoryPoints() != null) {
            issueE.setStoryPoints(null);
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
        issueE.setIssueTypeId(issueUpdateTypeDTO.getIssueTypeId());
        issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, REMAIN_TIME_FIELD, PARENT_ISSUE_ID, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, STORY_POINTS_FIELD, RANK_FIELD, EPIC_SEQUENCE, ISSUE_TYPE_ID});
        return queryIssue(issueE.getProjectId(), issueE.getIssueId(), organizationId);
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

    private void handleCreateIssueLink(List<IssueLinkCreateDTO> issueLinkCreateDTOList, Long projectId, Long issueId) {
        if (issueLinkCreateDTOList != null && !issueLinkCreateDTOList.isEmpty()) {
            List<IssueLinkE> issueLinkEList = issueLinkAssembler.toTargetList(issueLinkCreateDTOList, IssueLinkE.class);
            issueLinkEList.forEach(issueLinkE -> {
                Long linkIssueId = issueLinkE.getLinkedIssueId();
                issueLinkE.setIssueId(issueLinkE.getIn() ? issueId : linkIssueId);
                issueLinkE.setLinkedIssueId(issueLinkE.getIn() ? linkIssueId : issueId);
                issueLinkE.setProjectId(projectId);
                issueLinkRule.verifyCreateData(issueLinkE);
                if (issueLinkRule.checkUniqueLink(issueLinkE)) {
                    issueLinkRepository.create(issueLinkE);
                }
            });
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
                    ProductVersionCreateDTO productVersionCreateDTO = issueAssembler.toTarget(productVersionE, ProductVersionCreateDTO.class);
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

    private void handleCreateComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList, Long projectId, Long issueId, ProjectInfoE projectInfoE, Boolean assigneeCondition) {
        if (componentIssueRelDTOList != null && !componentIssueRelDTOList.isEmpty()) {
            handleComponentIssueRelWithHandleAssignee(ConvertHelper.convertList(componentIssueRelDTOList, ComponentIssueRelE.class), projectId, issueId, projectInfoE, assigneeCondition);
        }
    }

    private void handleComponentIssueRelWithHandleAssignee(List<ComponentIssueRelE> componentIssueRelEList, Long projectId, Long issueId, ProjectInfoE projectInfoE, Boolean assigneeCondition) {
        componentIssueRelEList.forEach(componentIssueRelE -> {
            handleComponentIssueRel(componentIssueRelE, projectId, issueId);
            //issue经办人可以根据模块策略进行区分
            if (assigneeCondition) {
                handleComponentIssue(componentIssueRelE, issueId, projectInfoE);
            }
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

    private void handleUpdateLabelIssue(List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId, Long projectId) {
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
                        delete.setProjectId(projectId);
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
            issueLabelRepository.labelGarbageCollection(projectId);
        }

    }

    private void handleUpdateVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList, Long projectId, Long issueId, String versionType) {
        if (versionIssueRelDTOList != null && versionType != null) {
            if (!versionIssueRelDTOList.isEmpty()) {
                //归档状态的版本之间的关联不删除
                List<VersionIssueRelE> versionIssueRelES = ConvertHelper.convertList(versionIssueRelDTOList, VersionIssueRelE.class);
                List<VersionIssueRelE> versionIssueRelCreate = versionIssueRelES.stream().filter(versionIssueRelE ->
                        versionIssueRelE.getVersionId() != null).collect(Collectors.toList());
                List<Long> curVersionIds = versionIssueRelMapper.queryByIssueIdAndProjectIdNoArchivedExceptInfluence(projectId, issueId, versionType);
                List<Long> createVersionIds = versionIssueRelCreate.stream().map(VersionIssueRelE::getVersionId).collect(Collectors.toList());
                curVersionIds.forEach(id -> {
                    if (!createVersionIds.contains(id)) {
                        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
                        versionIssueRelDO.setIssueId(issueId);
                        versionIssueRelDO.setVersionId(id);
                        versionIssueRelDO.setRelationType(versionType);
                        versionIssueRelDO.setProjectId(projectId);
                        versionIssueRelRepository.delete(versionIssueRelDO);
                    }
                });
                handleVersionIssueRel(versionIssueRelES, projectId, issueId);
            } else {
                VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
                versionIssueRelE.createBatchDeleteVersionIssueRel(projectId, issueId, versionType);
                versionIssueRelRepository.batchDeleteByIssueIdAndTypeArchivedExceptInfluence(versionIssueRelE);
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

    private Long getActiveSprintId(Long projectId) {
        SprintDO sprintDO = sprintService.getActiveSprint(projectId);
        if (sprintDO != null) {
            return sprintDO.getSprintId();
        }
        return null;
    }

    @Override
    public PageInfo<IssueNumDTO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "ai", new HashMap<>()));
        //pageRequest.resetOrder("ai", new HashMap<>());
        IssueNumDO issueNumDO = null;
        if (self) {
            issueNumDO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        Long activeSprintId = onlyActiveSprint ? getActiveSprintId(projectId) : null;
        PageInfo<IssueNumDO> issueDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), pageRequest.getSort().toSql()).doSelectPageInfo(() -> issueMapper.
                queryIssueByOption(projectId, issueId, issueNum, activeSprintId, self, content));
        if (self && issueNumDO != null) {
            issueDOPage.getList().add(0, issueNumDO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }

        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueNumDoToDto(issueDOPage.getList(), projectId));
    }

    @Override
    public void exportIssues(Long projectId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        //处理根据界面筛选结果导出的字段
        Map<String, String[]> fieldMap = handleExportFields(searchDTO.getExportFieldCodes(), projectId, organizationId);
        String[] fieldCodes = fieldMap.get(FIELD_CODES);
        String[] fieldNames = fieldMap.get(FIELD_NAMES);

        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        projectInfoDO = projectInfoMapper.selectOne(projectInfoDO);
        String projectCode = projectInfoDO.getProjectCode();
        ProjectDTO project = userRepository.queryProject(projectId);
        if (project == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        project.setCode(projectInfoDO.getProjectCode());
        Boolean condition = handleSearchUser(searchDTO, projectId);
        if (condition) {
            String filterSql = null;
            if (searchDTO.getQuickFilterIds() != null && !searchDTO.getQuickFilterIds().isEmpty()) {
                filterSql = getQuickFilter(searchDTO.getQuickFilterIds());
            }
            final String searchSql = filterSql;
            //连表查询需要设置主表别名
            List<Long> issueIds = issueMapper.queryIssueIdsListWithSub(projectId, searchDTO, searchSql, searchDTO.getAssigneeFilterIds());
            List<ExportIssuesDTO> exportIssues = issueAssembler.exportIssuesDOListToExportIssuesDTO(issueMapper.queryExportIssues(projectId, issueIds, projectCode), projectId);
            if (!issueIds.isEmpty()) {
                Map<Long, List<SprintNameDO>> closeSprintNames = issueMapper.querySprintNameByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(SprintNameDO::getIssueId));
                Map<Long, List<VersionIssueRelDO>> fixVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, FIX_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
                Map<Long, List<VersionIssueRelDO>> influenceVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, INFLUENCE_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
                Map<Long, List<LabelIssueRelDO>> labelNames = issueMapper.queryLabelIssueByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(LabelIssueRelDO::getIssueId));
                Map<Long, List<ComponentIssueRelDO>> componentMap = issueMapper.queryComponentIssueByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(ComponentIssueRelDO::getIssueId));
                Map<Long, Map<String, String>> foundationCodeValue = foundationFeignClient.queryFieldValueWithIssueIds(organizationId, projectId, issueIds).getBody();
                exportIssues.forEach(exportIssue -> {
                    String closeSprintName = closeSprintNames.get(exportIssue.getIssueId()) != null ? closeSprintNames.get(exportIssue.getIssueId()).stream().map(SprintNameDO::getSprintName).collect(Collectors.joining(",")) : "";
                    String fixVersionName = fixVersionNames.get(exportIssue.getIssueId()) != null ? fixVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                    String influenceVersionName = influenceVersionNames.get(exportIssue.getIssueId()) != null ? influenceVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                    String labelName = labelNames.get(exportIssue.getIssueId()) != null ? labelNames.get(exportIssue.getIssueId()).stream().map(LabelIssueRelDO::getLabelName).collect(Collectors.joining(",")) : "";
                    String componentName = componentMap.get(exportIssue.getIssueId()) != null ? componentMap.get(exportIssue.getIssueId()).stream().map(ComponentIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                    Map<String, String> fieldValue = foundationCodeValue.get(exportIssue.getIssueId()) != null ? foundationCodeValue.get(exportIssue.getIssueId()) : new HashMap<>();
                    exportIssue.setCloseSprintName(closeSprintName);
                    exportIssue.setProjectName(project.getName());
                    exportIssue.setSprintName(exportIssuesSprintName(exportIssue));
                    exportIssue.setFixVersionName(fixVersionName);
                    exportIssue.setInfluenceVersionName(influenceVersionName);
                    exportIssue.setVersionName(exportIssuesVersionName(exportIssue));
                    exportIssue.setDescription(getDes(exportIssue.getDescription()));
                    exportIssue.setLabelName(labelName);
                    exportIssue.setComponentName(componentName);
                    exportIssue.setFoundationFieldValue(fieldValue);
                });
            }
            ExcelUtil.export(exportIssues, ExportIssuesDTO.class, fieldNames, fieldCodes, project.getName(), Arrays.asList("sprintName"), response);
        } else {
            ExcelUtil.export(new ArrayList<>(), ExportIssuesDTO.class, fieldNames, fieldCodes, project.getName(), Arrays.asList("sprintName"), response);
        }
    }

    @Override
    public void exportProgramIssues(Long programId, SearchDTO searchDTO, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        ProjectDTO project = userRepository.queryProject(programId);
        if (project == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        Map<String, String[]> fieldMap = handleExportFieldsInProgram(searchDTO.getExportFieldCodes());
        String[] fieldCodes = fieldMap.get(FIELD_CODES);
        String[] fieldNames = fieldMap.get(FIELD_NAMES);
        List<Long> exportIssueIds = issueMapper.selectExportIssueIdsInProgram(programId, searchDTO);
        List<FeatureExportDO> featureExportDOList = issueMapper.selectExportIssuesInProgram(programId, exportIssueIds);
        List<FeatureExportDTO> featureExportDTOList = (featureExportDOList != null && !featureExportDOList.isEmpty() ? ConvertHelper.convertList(featureExportDOList, FeatureExportDTO.class) : null);
        if (featureExportDTOList != null && !featureExportDTOList.isEmpty()) {
            Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            Map<Long, List<PiExportNameDO>> closePiIssueMap = issueMapper.queryPiNameByIssueIds(programId, exportIssueIds).stream().collect(Collectors.groupingBy(PiExportNameDO::getIssueId));
            Map<Long, PiExportNameDO> activePiIssueMap = issueMapper.queryActivePiNameByIssueIds(programId, exportIssueIds).stream().collect(Collectors.toMap(PiExportNameDO::getIssueId, Function.identity()));
            featureExportDTOList.forEach(featureExportDTO -> {
                String closePiName = closePiIssueMap.get(featureExportDTO.getIssueId()) != null ? closePiIssueMap.get(featureExportDTO.getIssueId()).stream().map(PiExportNameDO::getPiCodeName).collect(Collectors.joining(",")) : "";
                String activePiName = activePiIssueMap.get(featureExportDTO.getIssueId()) != null ? activePiIssueMap.get(featureExportDTO.getIssueId()).getPiCodeName() : "";
                featureExportDTO.setPiName(exportIssuesPiName(closePiName, activePiName));
                featureExportDTO.setStatusName(statusMapDTOMap.get(featureExportDTO.getStatusId()).getName());
                if (ISSUE_TYPE_FEATURE.equals(featureExportDTO.getTypeCode())) {
                    featureExportDTO.setTypeName(FEATURE_TYPE_BUSINESS.equals(featureExportDTO.getFeatureType()) ? "特性" : "使能");
                } else {
                    featureExportDTO.setTypeName(issueTypeDTOMap.get(featureExportDTO.getIssuetypeId()).getName());
                }
            });
            ExcelUtil.export(featureExportDTOList, FeatureExportDTO.class, fieldNames, fieldCodes, project.getName(), Arrays.asList("piName"), response);
        } else {
            ExcelUtil.export(new ArrayList<>(), FeatureExportDTO.class, fieldNames, fieldCodes, project.getName(), Arrays.asList("piName"), response);
        }
    }

    /**
     * 处理根据界面筛选结果导出的字段
     *
     * @param exportFieldCodes
     * @return
     */
    private Map<String, String[]> handleExportFields(List<String> exportFieldCodes, Long projectId, Long organizationId) {
        Map<String, String[]> fieldMap = new HashMap<>(2);
        if (exportFieldCodes != null && exportFieldCodes.size() != 0) {
            Map<String, String> data = new HashMap<>(FIELDS.length);
            for (int i = 0; i < FIELDS.length; i++) {
                data.put(FIELDS[i], FIELDS_NAME[i]);
            }
            List<String> fieldCodes = new ArrayList<>(exportFieldCodes.size());
            List<String> fieldNames = new ArrayList<>(exportFieldCodes.size());
            exportFieldCodes.stream().forEach(code -> {
                String name = data.get(code);
                if (name != null) {
                    fieldCodes.add(code);
                    fieldNames.add(name);
                } else {
                    throw new CommonException("error.issue.exportFieldIllegal");
                }
            });
            fieldMap.put(FIELD_CODES, fieldCodes.stream().toArray(String[]::new));
            fieldMap.put(FIELD_NAMES, fieldNames.stream().toArray(String[]::new));
        } else {
            ObjectMapper m = new ObjectMapper();

            Object content = Optional.ofNullable(foundationFeignClient
                    .listQuery(projectId, organizationId, ObjectSchemeCode.AGILE_ISSUE)
                    .getBody()).orElseThrow(() -> new CommonException("error.foundation.listQuery"))
                    .get("content");

            List<Object> contentList = m.convertValue(content, List.class);
            List<ObjectSchemeFieldDTO> fieldDTOS = new ArrayList<>();

            if (content != null) {
                contentList.forEach(k ->
                        fieldDTOS.add(m.convertValue(k, ObjectSchemeFieldDTO.class)));
            }

            List<ObjectSchemeFieldDTO> userDefinedFieldDTOS = fieldDTOS.stream().
                    filter(v -> !v.getSystem()).collect(Collectors.toList());

            if (!userDefinedFieldDTOS.isEmpty()) {
                List<String> fieldCodes = new ArrayList(Arrays.asList(FIELDS));
                List<String> fieldNames = new ArrayList(Arrays.asList(FIELDS_NAME));
                userDefinedFieldDTOS.forEach(fieldDTO -> {
                    fieldCodes.add(fieldDTO.getCode());
                    fieldNames.add(fieldDTO.getName());
                });

                fieldMap.put(FIELD_CODES, fieldCodes.stream().toArray(String[]::new));
                fieldMap.put(FIELD_NAMES, fieldNames.stream().toArray(String[]::new));
            } else {
                fieldMap.put(FIELD_CODES, FIELDS);
                fieldMap.put(FIELD_NAMES, FIELDS_NAME);
            }
        }
        return fieldMap;
    }

    private Map<String, String[]> handleExportFieldsInProgram(List<String> exportFieldCodes) {
        Map<String, String[]> fieldMap = new HashMap<>(2);
        if (exportFieldCodes != null && exportFieldCodes.size() != 0) {
            Map<String, String> data = new HashMap<>(FIELDS_IN_PROGRAM.length);
            for (int i = 0; i < FIELDS_IN_PROGRAM.length; i++) {
                data.put(FIELDS_IN_PROGRAM[i], FIELDS_NAME_IN_PROGRAM[i]);
            }
            List<String> fieldCodes = new ArrayList<>(exportFieldCodes.size());
            List<String> fieldNames = new ArrayList<>(exportFieldCodes.size());
            exportFieldCodes.stream().forEach(code -> {
                String name = data.get(code);
                if (name != null) {
                    fieldCodes.add(code);
                    fieldNames.add(name);
                } else {
                    throw new CommonException("error.issue.exportFieldIllegal");
                }
            });
            fieldMap.put(FIELD_CODES, fieldCodes.stream().toArray(String[]::new));
            fieldMap.put(FIELD_NAMES, fieldNames.stream().toArray(String[]::new));
        } else {
            fieldMap.put(FIELD_CODES, FIELDS_IN_PROGRAM);
            fieldMap.put(FIELD_NAMES, FIELDS_NAME_IN_PROGRAM);
        }
        return fieldMap;
    }

    @Override
    public IssueDTO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO, Long organizationId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        IssueDetailDO issueDetailDO = issueMapper.queryIssueDetail(projectId, issueId);
        if (issueDetailDO != null) {
            Long newIssueId;
            Long objectVersionNumber;
            issueDetailDO.setSummary(copyConditionDTO.getSummary());
            IssueTypeDTO issueTypeDTO = issueFeignClient.queryIssueTypeById(ConvertUtil.getOrganizationId(projectId), issueDetailDO.getIssueTypeId()).getBody();
            if (issueTypeDTO.getTypeCode().equals(SUB_TASK)) {
                IssueSubCreateDTO issueSubCreateDTO = issueAssembler.issueDtoToIssueSubCreateDto(issueDetailDO);
                IssueSubDTO newIssue = stateMachineService.createSubIssue(issueSubCreateDTO);
                newIssueId = newIssue.getIssueId();
                objectVersionNumber = newIssue.getObjectVersionNumber();
            } else {
                IssueCreateDTO issueCreateDTO = issueAssembler.issueDtoToIssueCreateDto(issueDetailDO);
                issueCreateDTO.setEpicName(issueCreateDTO.getTypeCode().equals(ISSUE_EPIC) ? issueCreateDTO.getEpicName() + COPY : null);
                // deal feature extends table
                if (ISSUE_TYPE_FEATURE.equals(issueDetailDO.getTypeCode())) {
                    FeatureDO featureDO = new FeatureDO();
                    featureDO.setIssueId(issueId);
                    FeatureDO res = featureMapper.selectOne(featureDO);
                    if (res != null) {
                        FeatureDTO featureDTO = new FeatureDTO();
                        featureDTO.setAcceptanceCritera(res.getAcceptanceCritera());
                        featureDTO.setBenfitHypothesis(res.getBenfitHypothesis());
                        featureDTO.setFeatureType(res.getFeatureType());
                        featureDTO.setProjectId(projectId);
                        issueCreateDTO.setFeatureDTO(featureDTO);
                    }
                }
                if ("program".equals(applyType)) {
                    issueCreateDTO.setProgramId(projectId);
                }
                IssueDTO newIssue = stateMachineService.createIssue(issueCreateDTO, applyType);
                newIssueId = newIssue.getIssueId();
                objectVersionNumber = newIssue.getObjectVersionNumber();
            }
            //复制链接
            batchCreateCopyIssueLink(copyConditionDTO.getIssueLink(), issueId, newIssueId, projectId);
            //生成一条复制的关联
            createCopyIssueLink(issueDetailDO.getIssueId(), newIssueId, projectId);
            //复制故事点和剩余工作量并记录日志
            copyStoryPointAndRemainingTimeData(issueDetailDO, projectId, newIssueId, objectVersionNumber);
            //复制冲刺
            handleCreateCopyIssueSprintRel(copyConditionDTO.getSprintValues(), issueDetailDO, newIssueId);
            if (copyConditionDTO.getSubTask()) {
                List<IssueDO> subIssueDOList = issueDetailDO.getSubIssueDOList();
                if (subIssueDOList != null && !subIssueDOList.isEmpty()) {
                    subIssueDOList.forEach(issueDO -> copySubIssue(issueDO, newIssueId, projectId));
                }
            }
            return queryIssue(projectId, newIssueId, organizationId);
        } else {
            throw new CommonException("error.issue.copyIssueByIssueId");
        }
    }

    private void copyStoryPointAndRemainingTimeData(IssueDetailDO issueDetailDO, Long projectId, Long issueId, Long objectVersionNumber) {
        if (issueDetailDO.getStoryPoints() == null && issueDetailDO.getEstimateTime() == null) {
            return;
        }
        IssueUpdateDTO issueUpdateDTO = new IssueUpdateDTO();
        issueUpdateDTO.setStoryPoints(issueDetailDO.getStoryPoints());
        issueUpdateDTO.setRemainingTime(issueDetailDO.getRemainingTime());
        issueUpdateDTO.setIssueId(issueId);
        issueUpdateDTO.setObjectVersionNumber(objectVersionNumber);
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
        IssueSubDTO newSubIssue = stateMachineService.createSubIssue(issueSubCreateDTO);
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
                copy.setProjectId(projectId);
                if (issueLinkRule.checkUniqueLink(copy)) {
                    issueLinkRepository.create(copy);
                }
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
            issueLinkE.setProjectId(projectId);
            if (issueLinkRule.checkUniqueLink(issueLinkE)) {
                issueLinkRepository.create(issueLinkE);
            }
        }
    }

    private void insertSprintWhenTransform(Long issueId, Long sprintId, Long projectId, List<Long> issueIds) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO();
        issueSprintRelDO.setIssueId(issueId);
        issueSprintRelDO.setSprintId(sprintId);
        issueSprintRelDO.setProjectId(projectId);
        if (issueSprintRelMapper.selectOne(issueSprintRelDO) == null) {
            if (issueMapper.selectUnCloseSprintId(projectId, issueId) != null) {
                BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, issueIds);
                issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
                issueRepository.issueToDestinationByIds(projectId, sprintId, issueIds, new Date(), customUserDetails.getUserId());
            } else {
                issueSprintRelRepository.createIssueSprintRel(ConvertHelper.convert(issueSprintRelDO, IssueSprintRelE.class));
            }
        }
    }

    @Override
    public IssueSubDTO transformedSubTask(Long projectId, Long organizationId, IssueTransformSubTask issueTransformSubTask) {
        IssueE issueE = ConvertHelper.convert(queryIssueByIssueIdAndProjectId(projectId, issueTransformSubTask.getIssueId()), IssueE.class);
        if (issueE != null) {
            if (!issueE.getTypeCode().equals(SUB_TASK)) {
                issueE.setObjectVersionNumber(issueTransformSubTask.getObjectVersionNumber());
                List<Long> subIssueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueE.getIssueId());
                if (subIssueIds != null && !subIssueIds.isEmpty()) {
                    throw new CommonException("error.transformedSubTask.issueHaveSubIssue");
                }
                issueE.setEpicSequence(null);
                issueE.setStoryPoints(null);
                issueE.setStatusId(issueTransformSubTask.getStatusId());
                issueE.setTypeCode(SUB_TASK);
                issueE.setIssueTypeId(issueTransformSubTask.getIssueTypeId());
                issueE.setParentIssueId(issueTransformSubTask.getParentIssueId());
                issueRule.verifySubTask(issueTransformSubTask.getParentIssueId());
                //删除链接
                issueLinkRepository.deleteByIssueId(issueE.getIssueId());
                issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, ISSUE_TYPE_ID, RANK_FIELD, STATUS_ID, PARENT_ISSUE_ID, EPIC_SEQUENCE, STORY_POINTS_FIELD});
                Long sprintId = issueMapper.selectUnCloseSprintId(projectId, issueTransformSubTask.getParentIssueId());
                List<Long> issueIds = new ArrayList<>();
                issueIds.add(issueE.getIssueId());
                if (sprintId != null) {
                    insertSprintWhenTransform(issueE.getIssueId(), sprintId, projectId, issueIds);
                } else {
                    if (issueMapper.selectUnCloseSprintId(projectId, issueE.getIssueId()) != null) {
                        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, issueIds);
                        issueRepository.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
                    }
                }
                return queryIssueSub(projectId, organizationId, issueE.getIssueId());
            } else {
                throw new CommonException("error.IssueRule.subTaskError");
            }
        } else {
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }

    @Override
    public synchronized IssueDTO transformedTask(IssueE issueE, IssueTransformTask issueTransformTask, Long organizationId) {
        String originType = issueE.getTypeCode();
        if (originType.equals(SUB_TASK)) {
            issueE.setParentIssueId(null);
        }
        if (STORY_TYPE.equals(issueE.getTypeCode()) && issueE.getStoryPoints() != null) {
            issueE.setStoryPoints(null);
        }
        if (issueTransformTask.getTypeCode().equals(ISSUE_EPIC)) {
            issueE.setRank(null);
            issueE.setTypeCode(issueTransformTask.getTypeCode());
            issueE.setEpicName(issueTransformTask.getEpicName());
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
            issueE.setTypeCode(issueTransformTask.getTypeCode());
            issueE.setColorCode(null);
            issueE.setEpicName(null);
            issueE.setEpicSequence(null);
            //rank值重置
            calculationRank(issueE.getProjectId(), issueE);
        } else {
            issueE.setTypeCode(issueTransformTask.getTypeCode());
        }
        if (issueTransformTask.getStatusId() != null) {
            issueE.setStatusId(issueTransformTask.getStatusId());
        }
        issueE.setIssueTypeId(issueTransformTask.getIssueTypeId());
        issueRepository.update(issueE, new String[]{TYPE_CODE_FIELD, REMAIN_TIME_FIELD, PARENT_ISSUE_ID, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, STORY_POINTS_FIELD, RANK_FIELD, EPIC_SEQUENCE, ISSUE_TYPE_ID, STATUS_ID});
        return queryIssue(issueE.getProjectId(), issueE.getIssueId(), organizationId);
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

    private String exportIssuesPiName(String closePiName, String activePiName) {
        StringBuilder piName = new StringBuilder(activePiName != null && !Objects.equals(activePiName, "") ? "正在使用PI:" + activePiName + "\r\n" : "");
        piName.append(closePiName != null && !Objects.equals(closePiName, "") ? "已关闭PI:" + closePiName : "");
        return piName.toString();
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
    public PageInfo<IssueListTestDTO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<IssueDO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSort().toSql()).doSelectPageInfo(() -> issueMapper.listIssueWithoutSubToTestComponent(projectId, searchDTO.getSearchArgs(),
                searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContents()));
        return handleIssueListTestDoToDto(issueDOPage, organizationId, projectId);
    }

    private PageInfo<IssueListTestDTO> handleIssueListTestDoToDto(PageInfo<IssueDO> issueDOPage, Long organizationId, Long projectId) {
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueDoToIssueTestListDto(issueDOPage.getList(), priorityMap, statusMapDTOMap, issueTypeDTOMap));
    }

    @Override
    public PageInfo<IssueListTestWithSprintVersionDTO> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<IssueDO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toSql()).doSelectPageInfo(() ->
                issueMapper.listIssueWithLinkedIssues(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContents()));
        return handleILTDTOToILTWSVDTO(projectId, handleIssueListTestDoToDto(issueDOPage, organizationId, projectId));
    }

    private PageInfo<IssueListTestWithSprintVersionDTO> handleILTDTOToILTWSVDTO(Long projectId, PageInfo<IssueListTestDTO> issueListTestDTOSPage) {

//        Map<Long, ProductVersionDataDTO> versionIssueRelDTOMap = productVersionService
//                .queryVersionByProjectId(projectId).stream().collect(
//                        Collectors.toMap(ProductVersionDataDTO::getVersionId, x-> x));

        Map<Long, SprintDO> sprintDoMap = sprintMapper.getSprintByProjectId(projectId).stream().collect(
                Collectors.toMap(SprintDO::getSprintId, x -> x));

        List<IssueListTestWithSprintVersionDTO> issueListTestWithSprintVersionDTOS = new ArrayList<>();

        for (int a = 0; a < issueListTestDTOSPage.getSize(); a++) {
            IssueListTestWithSprintVersionDTO issueListTestWithSprintVersionDTO = new IssueListTestWithSprintVersionDTO(issueListTestDTOSPage.getList().get(a));

            List<VersionIssueRelDTO> versionList = new ArrayList<>();
            List<IssueSprintDTO> sprintList = new ArrayList<>();

            issueMapper.queryVersionIssueRelByIssueId(issueListTestWithSprintVersionDTO.getIssueId()).forEach(v -> {
                VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
                versionIssueRelDTO.setVersionId(v.getVersionId());
                versionIssueRelDTO.setName(v.getName());

                versionList.add(versionIssueRelDTO);
            });

            issueMapper.querySprintNameByIssueId(issueListTestWithSprintVersionDTO.getIssueId()).forEach(v -> {
                SprintDO sprintDO = sprintDoMap.get(v.getSprintId());

                IssueSprintDTO issueSprintDTO = new IssueSprintDTO();
                issueSprintDTO.setSprintId(sprintDO.getSprintId());
                issueSprintDTO.setSprintName(sprintDO.getSprintName());
                issueSprintDTO.setStatusCode(sprintDO.getStatusCode());

                sprintList.add(issueSprintDTO);
            });

            issueListTestWithSprintVersionDTO.setVersionDTOList(versionList);
            issueListTestWithSprintVersionDTO.setSprintDTOList(sprintList);

            issueListTestWithSprintVersionDTOS.add(issueListTestWithSprintVersionDTO);
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueListTestDTOSPage, issueListTestWithSprintVersionDTOS);
    }

    @Override
    public List<IssueCreationNumDTO> queryIssueNumByTimeSlot(Long projectId, String typeCode, Integer timeSlot) {
        //h2 不支持dateSub函数，这个函数不能自定义
        Date date = MybatisFunctionTestUtil.dataSubFunction(new Date(), timeSlot);
        return ConvertHelper.convertList(issueMapper.queryIssueNumByTimeSlot(projectId, typeCode, date), IssueCreationNumDTO.class);
    }

    @Override
    public PageInfo<IssueNumDTO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum, Boolean self, String content, PageRequest pageRequest) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder("search", new HashMap<>());
        IssueNumDO issueNumDO = null;
        if (self) {
            issueNumDO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        PageInfo<IssueNumDO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSort().toSql()).doSelectPageInfo(() ->
                issueMapper.queryIssueByOptionForAgile(projectId, issueId, issueNum, self, content));
        if (self && issueNumDO != null) {
            issueDOPage.getList().add(0, issueNumDO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueNumDoToDto(issueDOPage.getList(), projectId));
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
        return epicDataAssembler.toTarget(issueMapper.queryEpicListByEpic(epicSequenceDTO.getEpicId(), projectId), EpicDataDTO.class);
    }

    @Override
    public List<PieChartDTO> issueStatistic(Long projectId, String type, List<String> issueTypes) {
        return reportAssembler.toTargetList(issueMapper.issueStatistic(projectId, type, issueTypes), PieChartDTO.class);
    }

    @Override
    public PageInfo<IssueComponentDetailDTO> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<Long> issueIds = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSort().toSql()).doSelectPageInfo(() -> issueMapper.listIssueIdsWithoutSubDetail(projectId, searchDTO.getSearchArgs(),
                searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContents()));
        List<IssueComponentDetailDO> issueComponentDetailDOS = new ArrayList<>(issueIds.getList().size());
        if (issueIds.getList() != null && !issueIds.getList().isEmpty()) {
            issueComponentDetailDOS.addAll(issueMapper.listIssueWithoutSubDetailByIssueIds(issueIds.getList()));
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueIds, issueAssembler.issueComponentDetailDoToDto(projectId, issueComponentDetailDOS));
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

    private void getDoneIds(Map<Long, StatusMapDTO> statusMapDTOMap, List<Long> doneIds) {
        for (Long key : statusMapDTOMap.keySet()) {
            if ("done".equals(statusMapDTOMap.get(key).getType())) {
                doneIds.add(key);
            }
        }
    }

    @Override
    public List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds) {
        List<StoryMapIssueDTO> storyMapIssueDTOList = null;
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        //保存用户选择的泳道
        handleSaveUserSetting(projectId, type, pageType);
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        List<Long> doneIds = new ArrayList<>();
        getDoneIds(statusMapDTOMap, doneIds);
        switch (type) {
            case STORYMAP_TYPE_SPRINT:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdSprint(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
                break;
            case STORYMAP_TYPE_VERSION:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdVersion(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
                break;
            case STORYMAP_TYPE_NONE:
                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdNone(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
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
        Long sprintId = storyMapMoveDTO.getSprintId();
        Long versionId = storyMapMoveDTO.getVersionId();
        Long epicId = storyMapMoveDTO.getEpicId();
        IssueValidator.checkStoryMapMove(storyMapMoveDTO);
        dealRank(projectId, storyMapMoveDTO);
        if (epicId != null) {
            batchIssueToEpicInStoryMap(projectId, epicId, storyMapMoveDTO);
        }
        if (sprintId != null) {
            batchIssueToSprintInStoryMap(projectId, sprintId, storyMapMoveDTO);
        }
        if (versionId != null) {
            batchToVersionInStoryMap(projectId, versionId, storyMapMoveDTO);
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
        return issueMapper.queryIssueIdsByOptions(projectId, searchDTO.getAdvancedSearchArgs(), searchDTO.getOtherArgs(), searchDTO.getContents());
    }

    @Override
    public PageInfo<UndistributedIssueDTO> queryUnDistributedIssues(Long projectId, PageRequest pageRequest) {
        PageInfo<UndistributedIssueDO> undistributedIssueDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), pageRequest.getSort().toSql()).doSelectPageInfo(() ->
                issueMapper.queryUnDistributedIssues(projectId)
        );
        return PageUtil.buildPageInfoWithPageInfoList(undistributedIssueDOPage, issueAssembler.undistributedIssueDOToDto(undistributedIssueDOPage.getList(), projectId));
    }

    @Override
    public List<UnfinishedIssueDTO> queryUnfinishedIssues(Long projectId, Long assigneeId) {
        return issueAssembler.unfinishedIssueDoToDto(issueMapper.queryUnfinishedIssues(projectId, assigneeId), projectId);
    }

    @Override
    public String querySwimLaneCode(Long projectId) {
        UserSettingE userSettingE = new UserSettingE();
        userSettingE.initUserSetting(projectId);
        userSettingE.setTypeCode(STORYMAP);
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
    public synchronized List<Long> cloneIssuesByVersionId(Long projectId, Long versionId, List<Long> issueIds) {
        List<IssueDetailDO> issueDOList = issueMapper.queryByIssueIds(projectId, issueIds);
        if (issueDOList.size() == issueIds.size()) {
            return batchCreateIssue(issueDOList, projectId, versionId);
        } else {
            throw new CommonException("error.issueServiceImpl.issueTypeError");
        }
    }

    private List<Long> batchCreateIssue(List<IssueDetailDO> issueDOList, Long projectId, Long versionId) {
        List<Long> issueIds = new ArrayList<>(issueDOList.size());
        //获取issueTypeId
        Long issueTypeId = issueDOList.get(0).getIssueTypeId();
        //获取状态机id
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, SchemeApplyType.TEST, issueTypeId).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();

        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        issueDOList.forEach(issueDetailDO -> {
            IssueE issueE = issueAssembler.toTarget(issueDetailDO, IssueE.class);
            //初始化创建issue设置issue编号、项目默认设置
            issueE.initializationIssueByCopy(initStatusId);
            projectInfoRepository.updateIssueMaxNum(projectId, issueE.getIssueNum());
            issueE.setApplyType(SchemeApplyType.TEST);
            Long issueId = issueRepository.create(issueE).getIssueId();
            handleCreateCopyLabelIssueRel(issueDetailDO.getLabelIssueRelDOList(), issueId);
            handleCreateCopyComponentIssueRel(issueDetailDO.getComponentIssueRelDOList(), issueId);
            issueIds.add(issueId);
        });
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
        issueRepository.batchIssueToVersion(versionIssueRelE);
        return issueIds;
    }

    private void handleCreateCopyComponentIssueRel(List<ComponentIssueRelDO> componentIssueRelDOList, Long issueId) {
        componentIssueRelDOList.forEach(componentIssueRelDO -> {
            ComponentIssueRelE componentIssueRelE = new ComponentIssueRelE();
            BeanUtils.copyProperties(componentIssueRelDO, componentIssueRelE);
            componentIssueRelE.setIssueId(issueId);
            componentIssueRelE.setObjectVersionNumber(null);
            componentIssueRelRepository.create(componentIssueRelE);
        });
    }

    private void handleCreateCopyLabelIssueRel(List<LabelIssueRelDO> labelIssueRelDOList, Long issueId) {
        labelIssueRelDOList.forEach(labelIssueRelDO -> {
            LabelIssueRelE labelIssueRelE = new LabelIssueRelE();
            BeanUtils.copyProperties(labelIssueRelDO, labelIssueRelE);
            labelIssueRelE.setIssueId(issueId);
            labelIssueRelE.setObjectVersionNumber(null);
            labelIssueRelRepository.create(labelIssueRelE);
        });
    }

    public String getDes(String str) {
        StringBuilder result = new StringBuilder();
        if (!"".equals(str) && str != null) {
            String[] arrayLine = str.split(("\\},\\{"));
            String regEx = "\"insert\":\"(.*)\"";
            Pattern pattern = Pattern.compile(regEx);
            for (String s : arrayLine) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    result.append(StringEscapeUtils.unescapeJava(matcher.group(1)));
                }
            }
        }
        return result.toString();
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

    @Override
    public List<IssueProjectDTO> queryIssueTestGroupByProject() {
        return issueAssembler.toTargetList(issueMapper.queryIssueTestGroupByProject(), IssueProjectDTO.class);
    }

    public void deleteIssueInfo(Long issueId, Long projectId) {
        //删除issue发送消息
        IssuePayload issuePayload = new IssuePayload();
        issuePayload.setIssueId(issueId);
        issuePayload.setProjectId(projectId);
        sagaClient.startSaga("agile-delete-issue", new StartInstanceDTO(JSON.toJSONString(issuePayload), "", "", ResourceLevel.PROJECT.value(), projectId));
    }

    @Override
    public Boolean checkEpicName(Long projectId, String epicName) {
        IssueDO issueDO = new IssueDO();
        issueDO.setProjectId(projectId);
        issueDO.setEpicName(epicName);
        List<IssueDO> issueDOList = issueMapper.select(issueDO);
        return issueDOList != null && !issueDOList.isEmpty();
    }

    @Override
    public PageInfo<FeatureCommonDTO> queryFeatureList(Long programId, Long organizationId, PageRequest pageRequest, SearchDTO searchDTO) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "issue_page", new HashMap<>()));
        //pageRequest.resetOrder("issue_page", new HashMap<>());
        PageInfo<Long> featureCommonDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSort().toSql()).doSelectPageInfo(() ->
                issueMapper.selectFeatureIdsByPage(programId, searchDTO)
        );
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        return PageUtil.buildPageInfoWithPageInfoList(featureCommonDOPage, featureCommonDOPage.getList() != null && !featureCommonDOPage.getList().isEmpty() ? featureCommonAssembler.featureCommonDOToDTO(issueMapper.selectFeatureList(programId, featureCommonDOPage.getList()), statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
    }

    @Override
    public List<FeatureCommonDTO> queryFeatureListByPiId(Long programId, Long organizationId, Long piId) {
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        List<FeatureCommonDTO> featureCommonDTOS = modelMapper.map(issueMapper.selectFeatureByPiId(programId, piId), new TypeToken<List<FeatureCommonDTO>>() {
        }.getType());
        for (FeatureCommonDTO dto : featureCommonDTOS) {
            dto.setIssueTypeDTO(issueTypeDTOMap.get(dto.getIssuetypeId()));
        }
        return featureCommonDTOS;
    }
}