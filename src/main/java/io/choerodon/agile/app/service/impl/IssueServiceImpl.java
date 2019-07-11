package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueLinkValidator;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.api.validator.ProductVersionValidator;
import io.choerodon.agile.api.validator.SprintValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.api.vo.event.IssuePayload;
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
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;

import org.apache.commons.lang.StringEscapeUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private IssueAccessDataService issueAccessDataService;
    @Autowired
    private ComponentIssueRelRepository componentIssueRelRepository;
//    @Autowired
//    private IssueLinkRepository issueLinkRepository;
    @Autowired
    private IssueLinkService issueLinkService;
    @Autowired
    private LabelIssueRelRepository labelIssueRelRepository;
    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;
    @Autowired
    private VersionIssueRelService versionIssueRelService;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private EpicDataAssembler epicDataAssembler;
    @Autowired
    private IssueSearchAssembler issueSearchAssembler;
    @Autowired
    private ReportAssembler reportAssembler;
    @Autowired
    private ProductVersionValidator productVersionValidator;
    @Autowired
    private IssueComponentRepository issueComponentRepository;
    @Autowired
    private ProductVersionService productVersionService;
    @Autowired
    private IssueLabelRepository issueLabelRepository;
    @Autowired
    private SprintValidator sprintValidator;
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
    private UserService userService;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private DataLogService dataLogService;
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
//    @Autowired
//    private StoryMapIssueAssembler storyMapIssueAssembler;
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
    @Autowired
    private RankMapper rankMapper;
    @Autowired
    private IssueValidator issueValidator;

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

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

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
    private IssueLinkValidator issueLinkValidator;
    @Autowired
    private PiMapper piMapper;


    @Override
    public void afterCreateIssue(Long issueId, IssueConvertDTO issueConvertDTO, IssueCreateVO issueCreateVO, ProjectInfoE projectInfoE) {
        handleCreateIssueRearAction(issueConvertDTO, issueId, projectInfoE, issueCreateVO.getLabelIssueRelVOList(), issueCreateVO.getComponentIssueRelDTOList(), issueCreateVO.getVersionIssueRelDTOList(), issueCreateVO.getIssueLinkCreateVOList());
    }

    private void handleCreateIssueRearAction(IssueConvertDTO issueConvertDTO, Long issueId, ProjectInfoE projectInfoE, List<LabelIssueRelVO> labelIssueRelVOList, List<ComponentIssueRelDTO> componentIssueRelDTOList, List<VersionIssueRelDTO> versionIssueRelDTOList, List<IssueLinkCreateVO> issueLinkCreateVOList) {
        //处理冲刺
        handleCreateSprintRel(issueConvertDTO.getSprintId(), issueConvertDTO.getProjectId(), issueId);
        handleCreateLabelIssue(labelIssueRelVOList, issueId);
        handleCreateComponentIssueRel(componentIssueRelDTOList, projectInfoE.getProjectId(), issueId, projectInfoE, issueConvertDTO.getAssigneerCondtiion());
        handleCreateVersionIssueRel(versionIssueRelDTOList, projectInfoE.getProjectId(), issueId);
        handleCreateIssueLink(issueLinkCreateVOList, projectInfoE.getProjectId(), issueId);
    }

    @Override
    public void afterCreateSubIssue(Long issueId, IssueConvertDTO subIssueConvertDTO, IssueSubCreateVO issueSubCreateVO, ProjectInfoE projectInfoE) {
        handleCreateIssueRearAction(subIssueConvertDTO, issueId, projectInfoE, issueSubCreateVO.getLabelIssueRelVOList(), issueSubCreateVO.getComponentIssueRelDTOList(), issueSubCreateVO.getVersionIssueRelDTOList(), issueSubCreateVO.getIssueLinkCreateVOList());
    }

    @Override
    public void handleInitIssue(IssueConvertDTO issueConvertDTO, Long statusId, ProjectInfoE projectInfoE) {
        //如果是epic，初始化颜色
        if (ISSUE_EPIC.equals(issueConvertDTO.getTypeCode())) {
            List<LookupValueDTO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueConvertDTO.initializationColor(colorList);
            //排序编号
            Integer sequence = issueMapper.queryMaxEpicSequenceByProject(issueConvertDTO.getProjectId());
            issueConvertDTO.setEpicSequence(sequence == null ? 0 : sequence + 1);
        }
        //初始化创建issue设置issue编号、项目默认设置
        issueConvertDTO.initializationIssue(statusId, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueNum());
        //初始化排序
        if (issueConvertDTO.isIssueRank()) {
            calculationRank(issueConvertDTO.getProjectId(), issueConvertDTO);
        }
        // 初始化feature排序
        if (issueConvertDTO.isProgramRank()) {
            calculationProgramRank(issueConvertDTO);
        }
        if (issueConvertDTO.isIssueMapRank()) {
            calculationMapRank(issueConvertDTO);
        }
        issueValidator.verifyStoryPoints(issueConvertDTO);
    }

    private void calculationProgramRank(IssueConvertDTO issueConvertDTO) {
        if (piMapper.hasPiIssue(issueConvertDTO.getProgramId(), issueConvertDTO.getPiId())) {
            String rank = piMapper.queryPiMaxRank(issueConvertDTO.getProgramId(), issueConvertDTO.getPiId());
            issueConvertDTO.setRank(RankUtil.genNext(rank));
        } else {
            issueConvertDTO.setRank(RankUtil.mid());
        }
    }

    private void calculationMapRank(IssueConvertDTO issueConvertDTO) {
        String maxRank = issueMapper.selectMaxRankByProjectId(issueConvertDTO.getProjectId());
        if (maxRank == null) {
            issueConvertDTO.setMapRank(RankUtil.mid());
        } else {
            issueConvertDTO.setMapRank(RankUtil.genNext(maxRank));
        }
    }

    private void calculationRank(Long projectId, IssueConvertDTO issueConvertDTO) {
        if (sprintValidator.hasIssue(projectId, issueConvertDTO.getSprintId())) {
            String rank = sprintMapper.queryMaxRank(projectId, issueConvertDTO.getSprintId());
            issueConvertDTO.setRank(RankUtil.genNext(rank));
        } else {
            issueConvertDTO.setRank(RankUtil.mid());
        }
    }

    @Override
    public IssueVO queryIssueCreate(Long projectId, Long issueId) {
        IssueDetailDTO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDTOList() != null && !issue.getIssueAttachmentDTOList().isEmpty()) {
            issue.getIssueAttachmentDTOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        IssueVO result = issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
        sendMsgUtil.sendMsgByIssueCreate(projectId, result);
        return result;
    }

    private PriorityVO getPriorityById(Long organizationId, Long priorityId) {
        ResponseEntity<PriorityVO> priorityDTOResponseEntity = issueFeignClient.queryById(organizationId, priorityId);
        if (priorityDTOResponseEntity == null) {
            throw new CommonException("error.priority.get");
        }
        return priorityDTOResponseEntity.getBody();
    }

    private IssueTypeVO getIssueTypeById(Long organizationId, Long issueTypeId) {
        ResponseEntity<IssueTypeVO> issueTypeDTOResponseEntity = issueFeignClient.queryIssueTypeById(organizationId, issueTypeId);
        if (issueTypeDTOResponseEntity == null) {
            throw new CommonException("error.issueType.get");
        }
        return issueTypeDTOResponseEntity.getBody();
    }

    private StatusMapVO getStatusById(Long organizationId, Long statusId) {
        ResponseEntity<StatusMapVO> statusInfoDTOResponseEntity = stateMachineFeignClient.queryStatusById(organizationId, statusId);
        if (statusInfoDTOResponseEntity == null) {
            throw new CommonException("error.status.get");
        }
        return statusInfoDTOResponseEntity.getBody();
    }

    @Override
    public IssueVO queryIssue(Long projectId, Long issueId, Long organizationId) {
        IssueDetailDTO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDTOList() != null && !issue.getIssueAttachmentDTOList().isEmpty()) {
            issue.getIssueAttachmentDTOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
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
            IssueDTO issueInfo = issueMapper.selectByPrimaryKey(issue.getFeatureId());
            if (issueInfo != null) {
                issue.setFeatureName(issueInfo.getSummary());
            }
        }
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        return issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
    }

    private IssueVO queryIssueByUpdate(Long projectId, Long issueId, List<String> fieldList) {
        IssueDetailDTO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDTOList() != null && !issue.getIssueAttachmentDTOList().isEmpty()) {
            issue.getIssueAttachmentDTOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        if (STORY_TYPE.equals(issue.getTypeCode()) && issue.getFeatureId() != null) {
            IssueDTO issueInfo = issueMapper.selectByPrimaryKey(issue.getFeatureId());
            if (issueInfo != null) {
                issue.setFeatureName(issueInfo.getSummary());
            }
        }
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, issue.getApplyType());
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        IssueVO result = issueAssembler.issueDetailDoToDto(issue, issueTypeDTOMap, statusMapDTOMap, priorityDTOMap);
        sendMsgUtil.sendMsgByIssueAssignee(projectId, fieldList, result);
        // return feature extends table info
        if (ISSUE_TYPE_FEATURE.equals(result.getTypeCode())) {
            FeatureDO featureDO = new FeatureDO();
            featureDO.setIssueId(result.getIssueId());
            FeatureDO res = featureMapper.selectOne(featureDO);
            if (res != null) {
                result.setFeatureVO(ConvertHelper.convert(res, FeatureVO.class));
            }
        }
        sendMsgUtil.sendMsgByIssueComplete(projectId, fieldList, result);
        return result;
    }

    @Override
    public PageInfo<IssueListFieldKVVO> listIssueWithSub(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId) {
        if (organizationId == null) {
            organizationId = ConvertUtil.getOrganizationId(projectId);
        }
        //处理用户搜索
        Boolean condition = handleSearchUser(searchVO, projectId);
        if (condition) {
            PageInfo<Long> issueIdPage;
            String filterSql = null;
            //处理自定义搜索
            if (searchVO.getQuickFilterIds() != null && !searchVO.getQuickFilterIds().isEmpty()) {
                filterSql = getQuickFilter(searchVO.getQuickFilterIds());
            }
            //处理未匹配的筛选
            handleOtherArgs(searchVO);
            final String searchSql = filterSql;

            if (!handleSortField(pageRequest).equals("")) {
                String fieldCode = handleSortField(pageRequest);
                Map<String, String> order = new HashMap<>(1);
                String sortCode = fieldCode.split("\\.")[1];
                order.put(fieldCode, sortCode);
                PageUtil.sortResetOrder(pageRequest.getSort(), null, order);
                List<Long> issueIdsWithSub = issueMapper.queryIssueIdsListWithSub(projectId, searchVO, searchSql, searchVO.getAssigneeFilterIds());
                List<Long> foundationIssueIds = foundationFeignClient.sortIssueIdsByFieldValue(organizationId, projectId, pageRequest.getSort().toString()).getBody();

                List<Long> foundationIssueIdsWithSub = foundationIssueIds.stream().filter(issueIdsWithSub::contains).collect(Collectors.toList());
                List<Long> issueIdsWithSubWithoutFoundation = issueIdsWithSub.stream().filter(t -> !foundationIssueIdsWithSub.contains(t)).collect(Collectors.toList());

                Page page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
                page.setTotal(issueIdsWithSub.size());
                page.addAll(handleIssueLists(foundationIssueIdsWithSub, issueIdsWithSubWithoutFoundation, pageRequest)
                        .subList((pageRequest.getPage() - 1) * pageRequest.getSize(), pageRequest.getPage() * pageRequest.getSize()));

                issueIdPage = page.toPageInfo();
            } else {
                Map<String, String> order = new HashMap<>(1);
                //处理表映射
                order.put("issueId", "search.issue_issue_id");
                pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, order));
                issueIdPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                        PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueMapper.queryIssueIdsListWithSub
                        (projectId, searchVO, searchSql, searchVO.getAssigneeFilterIds()));
            }

            PageInfo<IssueListFieldKVVO> issueListDTOPage;
            if (issueIdPage.getList() != null && !issueIdPage.getList().isEmpty()) {
                List<IssueDTO> issueDTOList = issueMapper.queryIssueListWithSubByIssueIds(issueIdPage.getList());
                Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
                Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
                Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
                Map<Long, Map<String, String>> foundationCodeValue = foundationFeignClient.queryFieldValueWithIssueIds(organizationId, projectId, issueIdPage.getList()).getBody();
                issueListDTOPage = PageUtil.buildPageInfoWithPageInfoList(issueIdPage,
                        issueAssembler.issueDoToIssueListFieldKVDTO(issueDTOList, priorityMap, statusMapDTOMap, issueTypeDTOMap, foundationCodeValue));
            } else {
                issueListDTOPage = new PageInfo<>(new ArrayList<>());
            }
            return issueListDTOPage;
        } else {
            return new PageInfo<>(new ArrayList<>());
        }
    }

    private List<Long> handleIssueLists(List<Long> foundationList, List<Long> agileList, PageRequest pageRequest) {
        if (pageRequest.getSort() != null) {
            Iterator<Sort.Order> iterator = pageRequest.getSort().iterator();
            Sort.Direction direction = Sort.Direction.ASC;
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                direction = order.getDirection();
            }
            if (direction.isAscending()) {
                agileList.addAll(foundationList);
                return agileList;
            } else {
                foundationList.addAll(agileList);
                return foundationList;
            }
        } else return new ArrayList<>();
    }

    private String handleSortField(PageRequest pageRequest) {
        if (pageRequest.getSort() != null) {
            Iterator<Sort.Order> iterator = pageRequest.getSort().iterator();
            String fieldCode = "";
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                fieldCode = order.getProperty();
            }
            if (fieldCode.contains("foundation.")) {
                return fieldCode;
            } else return "";
        } else return "";
    }

    private void handleOtherArgs(SearchVO searchVO) {
        Map<String, Object> otherArgs = searchVO.getOtherArgs();
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
    public Boolean handleSearchUser(SearchVO searchVO, Long projectId) {
        if (searchVO.getSearchArgs() != null && searchVO.getSearchArgs().get(ASSIGNEE) != null) {
            String userName = (String) searchVO.getSearchArgs().get(ASSIGNEE);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userService.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchVO.getAdvancedSearchArgs().put("assigneeIds", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        if (searchVO.getSearchArgs() != null && searchVO.getSearchArgs().get(REPORTER) != null) {
            String userName = (String) searchVO.getSearchArgs().get(REPORTER);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userService.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchVO.getAdvancedSearchArgs().put("reporterIds", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean checkEpicNameUpdate(Long projectId, Long issueId, String epicName) {
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(issueId);
        if (epicName.equals(issueDTO.getEpicName())) {
            return false;
        }
        IssueDTO check = new IssueDTO();
        check.setProjectId(projectId);
        check.setEpicName(epicName);
        List<IssueDTO> issueDTOList = issueMapper.select(check);
        return issueDTOList != null && !issueDTOList.isEmpty();
    }

    @Override
    public IssueVO updateIssue(Long projectId, IssueUpdateVO issueUpdateVO, List<String> fieldList) {
        if (fieldList.contains("epicName") && issueUpdateVO.getEpicName() != null && checkEpicNameUpdate(projectId, issueUpdateVO.getIssueId(), issueUpdateVO.getEpicName())) {
            throw new CommonException("error.epicName.exist");
        }
        if (!fieldList.isEmpty()) {
            //处理issue自己字段
            handleUpdateIssue(issueUpdateVO, fieldList, projectId);
        }
        Long issueId = issueUpdateVO.getIssueId();
        handleUpdateLabelIssue(issueUpdateVO.getLabelIssueRelVOList(), issueId, projectId);
        handleUpdateComponentIssueRel(issueUpdateVO.getComponentIssueRelDTOList(), projectId, issueId);
        handleUpdateVersionIssueRel(issueUpdateVO.getVersionIssueRelDTOList(), projectId, issueId, issueUpdateVO.getVersionType());
        return queryIssueByUpdate(projectId, issueId, fieldList);
    }

    @Override
    public IssueVO updateIssueStatus(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType) {
        stateMachineService.executeTransform(projectId, issueId, transformId, objectVersionNumber, applyType, new InputDTO(issueId, "updateStatus", null));
        if ("agile".equals(applyType)) {
            IssueConvertDTO issueConvertDTO = new IssueConvertDTO();
            issueConvertDTO.setIssueId(issueId);
            issueConvertDTO.setStayDate(new Date());
            issueConvertDTO.setObjectVersionNumber(issueMapper.selectByPrimaryKey(issueId).getObjectVersionNumber());
            issueAccessDataService.updateSelective(issueConvertDTO);
        }
        return queryIssueByUpdate(projectId, issueId, Collections.singletonList("statusId"));
    }

    @Override
    public void handleUpdateIssue(IssueUpdateVO issueUpdateVO, List<String> fieldList, Long projectId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        IssueDTO originIssue = issueMapper.queryIssueWithNoCloseSprint(issueUpdateVO.getIssueId());
        IssueConvertDTO issueConvertDTO = issueAssembler.toTarget(issueUpdateVO, IssueConvertDTO.class);
        //处理用户，前端可能会传0，处理为null
        issueConvertDTO.initializationIssueUser();
        if (fieldList.contains(SPRINT_ID_FIELD)) {
            IssueConvertDTO oldIssue = modelMapper.map(originIssue, IssueConvertDTO.class);
            //处理子任务的冲刺
            List<Long> issueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueConvertDTO.getIssueId());
            List<Long> subBugIds = issueMapper.querySubBugIdsByIssueId(projectId, issueConvertDTO.getIssueId());
            if (subBugIds != null && !subBugIds.isEmpty()) {
                issueIds.addAll(subBugIds);
            }
            Boolean exitSprint = issueConvertDTO.getSprintId() != null && !Objects.equals(issueConvertDTO.getSprintId(), 0L);
            Boolean condition = (!Objects.equals(oldIssue.getSprintId(), issueUpdateVO.getSprintId()));
            issueIds.add(issueConvertDTO.getIssueId());
            if (condition) {
                BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, issueConvertDTO.getSprintId(), issueIds);
                issueAccessDataService.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
//                //不是活跃冲刺，修改冲刺状态回到第一个状态
//                handleIssueStatus(projectId, oldIssue, issueConvertDTO, fieldList, issueIds);
            }
            if (exitSprint) {
                issueAccessDataService.issueToDestinationByIds(projectId, issueConvertDTO.getSprintId(), issueIds, new Date(), customUserDetails.getUserId());
            }
            if (oldIssue.isIssueRank()) {
                calculationRank(projectId, issueConvertDTO);
                fieldList.add(RANK_FIELD);
                issueConvertDTO.setOriginSprintId(originIssue.getSprintId());
            }
        }
        if (STORY_TYPE.equals(originIssue.getTypeCode()) && fieldList.contains("featureId")) {
            if (Objects.equals(issueUpdateVO.getFeatureId(), 0L) && !Objects.equals(originIssue.getEpicId(), 0L)) {
                issueConvertDTO.setEpicId(0L);
                fieldList.add("epicId");
            } else if (!Objects.equals(issueUpdateVO.getFeatureId(), 0L)) {
                IssueDTO featureUpdate = issueMapper.selectByPrimaryKey(issueUpdateVO.getFeatureId());
                issueConvertDTO.setEpicId(featureUpdate.getEpicId() == null ? 0L : featureUpdate.getEpicId());
                fieldList.add("epicId");
            }
        }
        if ("feature".equals(originIssue.getTypeCode()) && fieldList.contains("epicId")) {
            if (Objects.equals(issueUpdateVO.getEpicId(), 0L) && !Objects.equals(originIssue.getEpicId(), 0L)) {
                issueAccessDataService.updateEpicIdOfStoryByFeature(issueUpdateVO.getIssueId(), issueUpdateVO.getEpicId());
            } else if (!Objects.equals(issueUpdateVO.getEpicId(), 0L)) {
                issueAccessDataService.updateEpicIdOfStoryByFeature(issueUpdateVO.getIssueId(), issueUpdateVO.getEpicId());
            }
        }
        issueAccessDataService.update(issueConvertDTO, fieldList.toArray(new String[fieldList.size()]));
        if (issueUpdateVO.getFeatureVO() != null && issueUpdateVO.getFeatureVO().getIssueId() != null) {
            FeatureVO featureVO = issueUpdateVO.getFeatureVO();
            if (featureVO != null) {
                featureRepository.updateSelective(ConvertHelper.convert(featureVO, FeatureE.class));
            }
        }
    }

    private void handleIssueStatus(Long projectId, IssueConvertDTO oldIssue, IssueConvertDTO issueConvertDTO, List<String> fieldList, List<Long> issueIds) {
        SprintSearchDTO sprintSearchDTO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (oldIssue.getApplyType().equals(SchemeApplyType.AGILE)) {
            if (sprintSearchDTO == null || !Objects.equals(issueConvertDTO.getSprintId(), sprintSearchDTO.getSprintId())) {
                Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, oldIssue.getIssueTypeId()).getBody();
                if (stateMachineId == null) {
                    throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
                }
                Long initStatusId = instanceFeignClient.queryInitStatusId(ConvertUtil.getOrganizationId(projectId), stateMachineId).getBody();
                if (issueConvertDTO.getStatusId() == null && !oldIssue.getStatusId().equals(initStatusId)) {
                    issueConvertDTO.setStatusId(initStatusId);
                    fieldList.add(STATUS_ID);
                }
                //子任务的处理
                if (issueIds != null && !issueIds.isEmpty()) {
                    List<IssueConvertDTO> issueDOList = issueAssembler.toTargetList(issueMapper.queryIssueSubList(projectId, oldIssue.getIssueId()), IssueConvertDTO.class);
                    String[] field = {STATUS_ID};
                    issueDOList.forEach(issue -> {
                        if (!issue.getStatusId().equals(initStatusId)) {
                            issue.setStatusId(initStatusId);
                            issueAccessDataService.update(issue, field);
                        }
                    });
                }
            }
        }
    }


    @Override
    public List<EpicDataVO> listEpic(Long projectId) {
        List<EpicDataVO> epicDataList = epicDataAssembler.toTargetList(issueMapper.queryEpicList(projectId), EpicDataVO.class);
        ProjectVO program = userService.getGroupInfoByEnableProject(ConvertUtil.getOrganizationId(projectId), projectId);
        List<EpicDataVO> programEpics = null;
        if (program != null) {
            programEpics = epicDataAssembler.toTargetList(issueMapper.selectEpicByProgram(program.getId(), projectId), EpicDataVO.class);
            if (programEpics != null && !programEpics.isEmpty()) {
                epicDataList.addAll(programEpics);
            }
        }
        if (!epicDataList.isEmpty()) {
            List<Long> epicIds = epicDataList.stream().map(EpicDataVO::getIssueId).collect(Collectors.toList());
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
    public List<EpicDataVO> listProgramEpic(Long programId) {
        List<EpicDataVO> epicDataList = epicDataAssembler.toTargetList(issueMapper.queryProgramEpicList(programId), EpicDataVO.class);
        if (!epicDataList.isEmpty()) {
            List<Long> epicIds = epicDataList.stream().map(EpicDataVO::getIssueId).collect(Collectors.toList());
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

//    @Override
//    public List<StoryMapEpicDTO> listStoryMapEpic(Long projectId, Long organizationId, Boolean showDoneEpic, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds) {
//        String filterSql = null;
//        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
//            filterSql = getQuickFilter(quickFilterIds);
//        }
//        List<StoryMapEpicDTO> storyMapEpicDTOList = ConvertHelper.convertList(issueMapper.queryStoryMapEpicList(projectId, showDoneEpic, assigneeId, onlyStory, filterSql), StoryMapEpicDTO.class);
//        if (!storyMapEpicDTOList.isEmpty()) {
//            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
//            Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
//            List<Long> epicIds = storyMapEpicDTOList.stream().map(StoryMapEpicDTO::getIssueId).collect(Collectors.toList());
//            Map<Long, Integer> issueCountMap = issueMapper.queryIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
//            Map<Long, Integer> doneIssueCountMap = issueMapper.queryDoneIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
//            Map<Long, Integer> notEstimateIssueCountMap = issueMapper.queryNotEstimateIssueCountByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
//            Map<Long, BigDecimal> totalEstimateMap = issueMapper.queryTotalEstimateByEpicIds(projectId, epicIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
//            storyMapEpicDTOList.forEach(epicData -> {
//                epicData.setStatusMapVO(statusMapDTOMap.get(epicData.getStatusId()));
//                epicData.setIssueTypeVO(issueTypeDTOMap.get(epicData.getIssueTypeId()));
//                epicData.setDoneIssueCount(doneIssueCountMap.get(epicData.getIssueId()));
//                epicData.setIssueCount(issueCountMap.get(epicData.getIssueId()));
//                epicData.setNotEstimate(notEstimateIssueCountMap.get(epicData.getIssueId()));
//                epicData.setTotalEstimate(totalEstimateMap.get(epicData.getIssueId()));
//            });
//        }
//        return storyMapEpicDTOList;
//    }

    private void dataLogDeleteByIssueId(Long projectId, Long issueId) {
        DataLogDTO dataLogDTO = new DataLogDTO();
        dataLogDTO.setProjectId(projectId);
        dataLogDTO.setIssueId(issueId);
        dataLogService.delete(dataLogDTO);
    }

    private void deleteStoryMapWidth(Long issueId) {
        StoryMapWidthDTO selectMapWidthDO = new StoryMapWidthDTO();
        selectMapWidthDO.setIssueId(issueId);
        selectMapWidthDO.setType("feature");
        List<StoryMapWidthDTO> storyMapWidthDTOList = storyMapWidthMapper.select(selectMapWidthDO);
        if (storyMapWidthDTOList != null && !storyMapWidthDTOList.isEmpty()) {
            storyMapWidthMapper.delete(selectMapWidthDO);
        }
    }


    @Saga(code = "agile-delete-issue", description = "删除issue", inputSchemaClass = IssuePayload.class)
    @Override
    public void deleteIssue(Long projectId, Long issueId) {
        IssueConvertDTO issueConvertDTO = queryIssueByProjectIdAndIssueId(projectId, issueId);
        if (issueConvertDTO == null) {
            throw new CommonException(ERROR_ISSUE_NOT_FOUND);
        }
        //删除issueLink
        issueLinkService.deleteByIssueId(issueConvertDTO.getIssueId());
        //删除标签关联
        labelIssueRelRepository.deleteByIssueId(issueConvertDTO.getIssueId());
        //没有issue使用的标签进行垃圾回收
        issueLabelRepository.labelGarbageCollection(projectId);
        //删除模块关联
        componentIssueRelRepository.deleteByIssueId(issueConvertDTO.getIssueId());
        //删除版本关联
        versionIssueRelService.deleteByIssueId(issueConvertDTO.getIssueId());
        //删除冲刺关联
        issueAccessDataService.deleteIssueFromSprintByIssueId(projectId, issueId);
        //删除评论信息
        issueCommentService.deleteByIssueId(issueConvertDTO.getIssueId());
        //删除附件
        issueAttachmentService.deleteByIssueId(issueConvertDTO.getIssueId());
        //删除公告板特性及依赖
        boardFeatureService.deleteByFeatureId(projectId, issueId);

        if (ISSUE_TYPE_FEATURE.equals(issueConvertDTO.getTypeCode())) {
            featureRepository.delete(issueId);
        }
        //不是子任务的issue删除子任务
        if (!(SUB_TASK).equals(issueConvertDTO.getTypeCode())) {
            if ((ISSUE_EPIC).equals(issueConvertDTO.getTypeCode())) {
                //如果是epic，会把该epic下的issue的epicId置为0
                issueAccessDataService.batchUpdateIssueEpicId(projectId, issueConvertDTO.getIssueId());
            } else {
                redisUtil.deleteRedisCache(new String[]{"Agile:EpicChart" + projectId + ":" + issueConvertDTO.getEpicId() + ":" + "*"});
            }
            List<IssueDTO> issueDTOList = issueMapper.queryIssueSubList(projectId, issueConvertDTO.getIssueId());
            if (issueDTOList != null && !issueDTOList.isEmpty()) {
                issueDTOList.forEach(subIssue -> deleteIssue(subIssue.getProjectId(), subIssue.getIssueId()));
            }
        }
        // 如果是删除feature，将其下的issue的featureId置为0
        if ("feature".equals(issueConvertDTO.getTypeCode())) {
            issueAccessDataService.updateEpicIdOfStoryByFeature(issueConvertDTO.getIssueId(), 0L);
            issueMapper.updateFeatureIdOfStoryByFeature(issueConvertDTO.getIssueId(), 0L);
            // 删除故事地图扩列
            deleteStoryMapWidth(issueConvertDTO.getIssueId());
        }
        //删除日志信息
        dataLogDeleteByIssueId(projectId, issueId);
        issueAccessDataService.delete(projectId, issueConvertDTO.getIssueId());
        //删除rank数据
        rankMapper.deleteRankByIssueId(issueId);
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
    public void handleInitSubIssue(IssueConvertDTO subIssueConvertDTO, Long statusId, ProjectInfoE projectInfoE) {
        IssueConvertDTO parentIssueConvertDTO = ConvertHelper.convert(issueMapper.queryIssueSprintNotClosed(subIssueConvertDTO.getProjectId(), subIssueConvertDTO.getParentIssueId()), IssueConvertDTO.class);
        //设置初始状态,跟随父类状态
        subIssueConvertDTO = parentIssueConvertDTO.initializationSubIssue(subIssueConvertDTO, statusId, projectInfoE);
        projectInfoRepository.updateIssueMaxNum(subIssueConvertDTO.getProjectId(), subIssueConvertDTO.getIssueNum());
        //初始化排序
        if (subIssueConvertDTO.isIssueRank()) {
            calculationRank(subIssueConvertDTO.getProjectId(), subIssueConvertDTO);
        }
    }

    @Override
    public List<IssueSearchVO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionValidator.judgeExist(projectId, versionId);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueAccessDataService.batchIssueToVersion(versionIssueRelE);
        } else {
            issueAccessDataService.batchRemoveVersion(projectId, issueIds);
        }
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public void batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            productVersionValidator.judgeExist(projectId, versionId);
            if (issueMapper.queryIssueIdsIsTest(projectId, issueIds) != issueIds.size()) {
                throw new CommonException("error.Issue.type.isNotIssueTest");
            }
            issueAccessDataService.batchRemoveVersionTest(projectId, issueIds);
            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
            issueAccessDataService.batchIssueToVersion(versionIssueRelE);
        }
    }

//    @Override
//    public void batchToVersionInStoryMap(Long projectId, Long versionId, StoryMapMoveDTO storyMapMoveDTO) {
//        List<Long> issueIds = storyMapMoveDTO.getVersionIssueIds();
//        if (versionId != null && !Objects.equals(versionId, 0L)) {
//            productVersionValidator.judgeExistStoryMap(projectId, versionId);
//            issueAccessDataService.batchRemoveVersion(projectId, issueIds);
//            VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
//            versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
//            issueAccessDataService.batchIssueToVersion(versionIssueRelE);
//        } else {
//            issueAccessDataService.batchRemoveVersion(projectId, issueIds);
//        }
//    }

    @Override
    public List<IssueSearchVO> batchIssueToEpic(Long projectId, Long epicId, List<Long> issueIds) {
        issueValidator.judgeExist(projectId, epicId);
        issueAccessDataService.batchIssueToEpic(projectId, epicId, issueIds);
        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public void batchStoryToFeature(Long projectId, Long featureId, List<Long> issueIds) {
        issueValidator.checkBatchStoryToFeature(featureId);
        List<Long> filterIds = issueMapper.filterStoryIds(projectId, issueIds);
        if (filterIds != null && !filterIds.isEmpty()) {
            IssueDTO feature = issueMapper.selectByPrimaryKey(featureId);
            Long updateEpicId = (feature.getEpicId() == null ? 0L : feature.getEpicId());
            issueAccessDataService.batchStoryToFeature(projectId, featureId, filterIds, updateEpicId);
        }
    }

//    @Override
//    public List<IssueSearchVO> batchIssueToEpicInStoryMap(Long projectId, Long epicId, StoryMapMoveDTO storyMapMoveDTO) {
//        List<Long> issueIds = storyMapMoveDTO.getEpicIssueIds();
//        issueValidator.judgeExist(projectId, epicId);
//        issueAccessDataService.batchIssueToEpic(projectId, epicId, issueIds);
//        return issueSearchAssembler.doListToDTO(issueMapper.queryIssueByIssueIds(projectId, issueIds), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
//    }

    private void dataLogRank(Long projectId, MoveIssueVO moveIssueVO, String rankStr, Long sprintId) {
        for (Long issueId : moveIssueVO.getIssueIds()) {
            SprintNameVO activeSprintName = sprintNameAssembler.toTarget(issueMapper.queryActiveSprintNameByIssueId(issueId), SprintNameVO.class);
            Boolean condition = (sprintId == 0 && activeSprintName == null) || (activeSprintName != null
                    && sprintId.equals(activeSprintName.getSprintId()));
            if (condition) {
                DataLogDTO dataLogDTO = new DataLogDTO();
                dataLogDTO.setProjectId(projectId);
                dataLogDTO.setField(FIELD_RANK);
                dataLogDTO.setIssueId(issueId);
                dataLogDTO.setNewString(rankStr);
                dataLogService.create(dataLogDTO);
            }
        }
    }

    @Override
    public List<IssueSearchVO> batchIssueToSprint(Long projectId, Long sprintId, MoveIssueVO moveIssueVO) {
        sprintValidator.judgeExist(projectId, sprintId);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        if (moveIssueVO.getBefore()) {
            beforeRank(projectId, sprintId, moveIssueVO, moveIssueDOS);
        } else {
            afterRank(projectId, sprintId, moveIssueVO, moveIssueDOS);
        }
        //处理评级日志
        if (moveIssueVO.getRankIndex() != null && !moveIssueVO.getRankIndex()) {
            dataLogRank(projectId, moveIssueVO, RANK_LOWER, sprintId);
        } else if (moveIssueVO.getRankIndex() != null && moveIssueVO.getRankIndex()) {
            dataLogRank(projectId, moveIssueVO, RANK_HIGHER, sprintId);
        }
        issueAccessDataService.batchUpdateIssueRank(projectId, moveIssueDOS);
        List<Long> moveIssueIds = moveIssueVO.getIssueIds();
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
        List<IssueSearchDTO> issueSearchDTOList = issueMapper.queryIssueByIssueIds(projectId, moveIssueVO.getIssueIds()).stream()
                .filter(issueDO -> issueDO.getSprintId() == null ? sprintId != 0 : !issueDO.getSprintId().equals(sprintId)).collect(Collectors.toList());
        if (issueSearchDTOList != null && !issueSearchDTOList.isEmpty()) {
            List<Long> moveIssueIdsFilter = issueSearchDTOList.stream().map(IssueSearchDTO::getIssueId).collect(Collectors.toList());
            BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIdsFilter);
            issueAccessDataService.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
            if (sprintId != null && !Objects.equals(sprintId, 0L)) {
                issueAccessDataService.issueToDestinationByIds(projectId, sprintId, moveIssueIdsFilter, new Date(), customUserDetails.getUserId());
            }
//            //如果移动冲刺不是活跃冲刺，则状态回到默认状态
//            batchHandleIssueStatus(projectId, moveIssueIdsFilter, sprintId);
            List<Long> assigneeIds = issueSearchDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDTO::getAssigneeId).distinct().collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(assigneeIds, true);
            return issueSearchAssembler.doListToDTO(issueSearchDTOList, usersMap, new HashMap<>(), new HashMap<>(), new HashMap<>());
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public void batchHandleIssueStatus(Long projectId, List<Long> moveIssueIds, Long sprintId) {
        SprintSearchDTO sprintSearchDTO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDTO == null || !Objects.equals(sprintId, sprintSearchDTO.getSprintId())) {
            List<IssueConvertDTO> issueConvertDTOList = issueAssembler.toTargetList(issueMapper.queryIssueByIssueIdsAndSubIssueIds(moveIssueIds), IssueConvertDTO.class);
            Map<Long, IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIdDTOMap = ConvertUtil.queryIssueTypesWithStateMachineIdByProjectId(projectId, AGILE);
            issueConvertDTOList.forEach(issueE -> {
                Long initStatusId = issueTypeWithStateMachineIdDTOMap.get(issueE.getIssueTypeId()).getInitStatusId();
                if (!issueE.getStatusId().equals(initStatusId)) {
                    issueE.setStatusId(initStatusId);
                    issueAccessDataService.update(issueE, new String[]{STATUS_ID});
                }
            });
        }
    }

//    private void dealBoundBeforeRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String minRank = issueMapper.selectMinRankByProjectId(projectId);
//        if (minRank == null) {
//            initMapRank(projectId);
//            minRank = issueMapper.selectMinRankByProjectId(projectId);
//        }
//        Collections.reverse(storyMapMoveDTO.getIssueIds());
//        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//            minRank = RankUtil.genPre(minRank);
//            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, minRank));
//        }
//    }
//
//    private void dealBoundAfterRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String maxRank = issueMapper.selectMaxRankByProjectId(projectId);
//        if (maxRank == null) {
//            initMapRank(projectId);
//            maxRank = issueMapper.selectMaxRankByProjectId(projectId);
//        }
//        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//            maxRank = RankUtil.genNext(maxRank);
//            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, maxRank));
//        }
//    }

//    private void dealBoundRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        if (storyMapMoveDTO.getBefore()) {
//            dealBoundBeforeRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//        } else {
//            dealBoundAfterRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//        }
//    }

//    private void dealMapRankLengthTooLarge(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String maxRank = issueMapper.selectMaxRankByProjectId(projectId);
//        storyMapMoveIssueDOS.clear();
//        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//            maxRank = RankUtil.genNext(maxRank);
//            storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, maxRank));
//        }
//    }

//    private void dealInnerBeforeRank(Long projectId, String currentMapRank, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String leftMaxRank = issueMapper.selectLeftMaxMapRank(projectId, currentMapRank);
//        List<Long> issueIds = storyMapMoveDTO.getIssueIds();
//        Collections.reverse(issueIds);
//        if (leftMaxRank == null) {
//            for (Long issueId : issueIds) {
//                currentMapRank = RankUtil.genPre(currentMapRank);
//                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
//            }
//        } else {
//            for (Long issueId : issueIds) {
//                leftMaxRank = RankUtil.between(leftMaxRank, currentMapRank);
//                if (leftMaxRank.length() >= 700) {
//                    dealMapRankLengthTooLarge(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//                    return;
//                }
//                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, leftMaxRank));
//            }
//        }
//    }

//    private void dealInnerAfterRank(Long projectId, String currentMapRank, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String rightMinRank = issueMapper.selectRightMinMapRank(projectId, currentMapRank);
//        if (rightMinRank == null) {
//            for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//                currentMapRank = RankUtil.genNext(currentMapRank);
//                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
//            }
//        } else {
//            for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//                currentMapRank = RankUtil.between(currentMapRank, rightMinRank);
//                if (currentMapRank.length() >= 700) {
//                    dealMapRankLengthTooLarge(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//                    return;
//                }
//                storyMapMoveIssueDOS.add(new StoryMapMoveIssueDO(issueId, currentMapRank));
//            }
//        }
//    }

//    private void dealInnerRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO, List<StoryMapMoveIssueDO> storyMapMoveIssueDOS) {
//        String currentMapRank = issueMapper.selectMapRankByIssueId(projectId, storyMapMoveDTO.getOutsetIssueId());
//        if (currentMapRank == null) {
//            initMapRank(projectId);
//            currentMapRank = issueMapper.selectMapRankByIssueId(projectId, storyMapMoveDTO.getOutsetIssueId());
//        }
//        if (storyMapMoveDTO.getBefore()) {
//            dealInnerBeforeRank(projectId, currentMapRank, storyMapMoveDTO, storyMapMoveIssueDOS);
//        } else {
//            dealInnerAfterRank(projectId, currentMapRank, storyMapMoveDTO, storyMapMoveIssueDOS);
//        }
//    }

//    private void dealRank(Long projectId, StoryMapMoveDTO storyMapMoveDTO) {
//        List<StoryMapMoveIssueDO> storyMapMoveIssueDOS = new ArrayList<>();
//        if (storyMapMoveDTO.getOutsetIssueId() == null || Objects.equals(storyMapMoveDTO.getOutsetIssueId(), 0L)) {
//            dealBoundRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//        } else {
//            dealInnerRank(projectId, storyMapMoveDTO, storyMapMoveIssueDOS);
//        }
//        issueAccessDataService.batchUpdateMapIssueRank(projectId, storyMapMoveIssueDOS);
//    }

//    @Override
//    public List<IssueSearchVO> batchIssueToSprintInStoryMap(Long projectId, Long sprintId, StoryMapMoveDTO storyMapMoveDTO) {
//        sprintValidator.judgeExist(projectId, sprintId);
//        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
//        //处理评级日志
//        if (storyMapMoveDTO.getRankIndex() != null && !storyMapMoveDTO.getRankIndex()) {
//            dataLogRankInStoryMap(projectId, storyMapMoveDTO, RANK_LOWER, sprintId);
//        } else if (storyMapMoveDTO.getRankIndex() != null && storyMapMoveDTO.getRankIndex()) {
//            dataLogRankInStoryMap(projectId, storyMapMoveDTO, RANK_HIGHER, sprintId);
//        }
//        List<Long> moveIssueIds = storyMapMoveDTO.getSprintIssueIds();
//        //处理子任务和子缺陷
//        List<Long> subTaskIds = issueMapper.querySubIssueIds(projectId, moveIssueIds);
//        List<Long> subBugIds = issueMapper.querySubBugIds(projectId, moveIssueIds);
//        if (subTaskIds != null && !subTaskIds.isEmpty()) {
//            moveIssueIds.addAll(subTaskIds);
//        }
//        if (subBugIds != null && !subBugIds.isEmpty()) {
//            moveIssueIds.addAll(subBugIds);
//        }
//        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, moveIssueIds);
//        issueAccessDataService.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
//        if (sprintId != null && !Objects.equals(sprintId, 0L)) {
//            issueAccessDataService.issueToDestinationByIds(projectId, sprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
//        }
//        List<IssueSearchDTO> issueSearchDTOList = issueMapper.queryIssueByIssueIds(projectId, storyMapMoveDTO.getSprintIssueIds());
//        List<Long> assigneeIds = issueSearchDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDTO::getAssigneeId).distinct().collect(Collectors.toList());
//        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(assigneeIds, true);
//        return issueSearchAssembler.doListToDTO(issueSearchDTOList, usersMap, new HashMap<>(), new HashMap<>(), new HashMap<>());
//    }

//    private void dataLogRankInStoryMap(Long projectId, StoryMapMoveDTO storyMapMoveDTO, String rankStr, Long sprintId) {
//        for (Long issueId : storyMapMoveDTO.getIssueIds()) {
//            SprintNameVO activeSprintName = sprintNameAssembler.toTarget(issueMapper.queryActiveSprintNameByIssueId(issueId), SprintNameVO.class);
//            Boolean condition = (sprintId == 0 && activeSprintName == null) || (activeSprintName != null
//                    && sprintId.equals(activeSprintName.getSprintId()));
//            if (condition) {
//                DataLogE dataLogE = new DataLogE();
//                dataLogE.setField(FIELD_RANK);
//                dataLogE.setProjectId(projectId);
//                dataLogE.setIssueId(issueId);
//                dataLogE.setNewString(rankStr);
//                dataLogRepository.create(dataLogE);
//            }
//        }
//    }

    private void beforeRank(Long projectId, Long sprintId, MoveIssueVO moveIssueVO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueVO.setIssueIds(issueMapper.queryIssueIdOrderByRankDesc(projectId, moveIssueVO.getIssueIds()));
        if (moveIssueVO.getOutsetIssueId() == null || Objects.equals(moveIssueVO.getOutsetIssueId(), 0L)) {
            noOutsetBeforeRank(projectId, sprintId, moveIssueVO, moveIssueDOS);
        } else {
            outsetBeforeRank(projectId, sprintId, moveIssueVO, moveIssueDOS);
        }
    }

    private void outsetBeforeRank(Long projectId, Long sprintId, MoveIssueVO moveIssueVO, List<MoveIssueDO> moveIssueDOS) {
        String rightRank = issueMapper.queryRank(projectId, moveIssueVO.getOutsetIssueId());
        String leftRank = issueMapper.queryLeftRank(projectId, sprintId, rightRank);
        if (leftRank == null) {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                rightRank = RankUtil.genPre(rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                rightRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        }
    }

    private void noOutsetBeforeRank(Long projectId, Long sprintId, MoveIssueVO moveIssueVO, List<MoveIssueDO> moveIssueDOS) {
        String minRank = sprintMapper.queryMinRank(projectId, sprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueVO.getIssueIds()) {
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
            }
        }
    }

    private void afterRank(Long projectId, Long sprintId, MoveIssueVO moveIssueVO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueVO.setIssueIds(issueMapper.queryIssueIdOrderByRankAsc(projectId, moveIssueVO.getIssueIds()));
        String leftRank = issueMapper.queryRank(projectId, moveIssueVO.getOutsetIssueId());
        String rightRank = issueMapper.queryRightRank(projectId, sprintId, leftRank);
        if (rightRank == null) {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                leftRank = RankUtil.genNext(leftRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                leftRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        }
    }

    @Override
    public List<IssueEpicVO> listEpicSelectData(Long projectId) {
        return issueAssembler.toTargetList(issueMapper.queryIssueEpicSelectList(projectId), IssueEpicVO.class);
    }

    @Override
    public List<IssueFeatureVO> listFeatureSelectData(Long projectId, Long organizationId, Long epicId) {
        ProjectVO program = userService.getGroupInfoByEnableProject(organizationId, projectId);
        if (program != null) {
            return issueAssembler.toTargetList(issueMapper.queryIssueFeatureSelectList(program.getId(), projectId, epicId), IssueFeatureVO.class);
        } else {
            return issueAssembler.toTargetList(issueMapper.selectFeatureListByAgileProject(projectId), IssueFeatureVO.class);
        }
    }

    private void setFeatureStatisticDetail(Long projectId, List<IssueFeatureVO> featureList) {
        if (featureList != null && !featureList.isEmpty()) {
            List<Long> ids = featureList.stream().map(IssueFeatureVO::getIssueId).collect(Collectors.toList());
            Map<Long, Integer> storyCountMap = issueMapper.selectStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> completedStoryCountMap = issueMapper.selectCompletedStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> unestimateStoryCountMap = issueMapper.selectUnEstimateStoryCountByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, BigDecimal> totalStoryPointsMap = issueMapper.selectTotalStoryPointsByIds(projectId, ids).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getStoryPointCount));
            featureList.forEach(issueFeatureVO -> {
                issueFeatureVO.setStoryCount(storyCountMap.get(issueFeatureVO.getIssueId()) == null ? 0 : storyCountMap.get(issueFeatureVO.getIssueId()));
                issueFeatureVO.setStoryCompletedCount(completedStoryCountMap.get(issueFeatureVO.getIssueId()) == null ? 0 : completedStoryCountMap.get(issueFeatureVO.getIssueId()));
                issueFeatureVO.setUnEstimateStoryCount(unestimateStoryCountMap.get(issueFeatureVO.getIssueId()) == null ? 0 : unestimateStoryCountMap.get(issueFeatureVO.getIssueId()));
                issueFeatureVO.setTotalStoryPoints(totalStoryPointsMap.get(issueFeatureVO.getIssueId()) == null ? new BigDecimal(0) : totalStoryPointsMap.get(issueFeatureVO.getIssueId()));
            });
        }
    }

    @Override
    public List<IssueFeatureVO> listFeature(Long projectId, Long organizationId) {
        ProjectVO program = userService.getGroupInfoByEnableProject(organizationId, projectId);
        if (program != null) {
            List<IssueDTO> programFeatureList = issueMapper.queryIssueFeatureSelectList(program.getId(), projectId, null);
            List<IssueFeatureVO> issueFeatureVOList = issueAssembler.toTargetList(programFeatureList, IssueFeatureVO.class);
            setFeatureStatisticDetail(projectId, issueFeatureVOList);
            return issueFeatureVOList;
        } else {
            List<IssueDTO> projectFeatureList = issueMapper.selectFeatureListByAgileProject(projectId);
            List<IssueFeatureVO> featureDTOList = issueAssembler.toTargetList(projectFeatureList, IssueFeatureVO.class);
            setFeatureStatisticDetail(projectId, featureDTOList);
            return featureDTOList;
        }
    }

    @Override
    public List<IssueEpicVO> listEpicSelectProgramData(Long programId) {
        return issueAssembler.toTargetList(issueMapper.listEpicSelectProgramData(programId), IssueEpicVO.class);
    }

    @Override
    public IssueSubVO queryIssueSub(Long projectId, Long organizationId, Long issueId) {
        IssueDetailDTO issue = issueMapper.queryIssueDetail(projectId, issueId);
        issue.setPriorityVO(getPriorityById(organizationId, issue.getPriorityId()));
        issue.setIssueTypeVO(getIssueTypeById(organizationId, issue.getIssueTypeId()));
        issue.setStatusMapVO(getStatusById(organizationId, issue.getStatusId()));
        if (issue.getIssueAttachmentDTOList() != null && !issue.getIssueAttachmentDTOList().isEmpty()) {
            issue.getIssueAttachmentDTOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        return issueAssembler.issueDetailDoToIssueSubDto(issue);
    }

    @Override
    public IssueSubVO queryIssueSubByCreate(Long projectId, Long issueId) {
        IssueDetailDTO issue = issueMapper.queryIssueDetail(projectId, issueId);
        if (issue.getIssueAttachmentDTOList() != null && !issue.getIssueAttachmentDTOList().isEmpty()) {
            issue.getIssueAttachmentDTOList().forEach(issueAttachmentDO -> issueAttachmentDO.setUrl(attachmentUrl + issueAttachmentDO.getUrl()));
        }
        IssueSubVO result = issueAssembler.issueDetailDoToIssueSubDto(issue);
        sendMsgUtil.sendMsgBySubIssueCreate(projectId, result);
        return result;
    }

    @Override
    public synchronized IssueVO updateIssueTypeCode(IssueConvertDTO issueConvertDTO, IssueUpdateTypeDTO issueUpdateTypeDTO, Long organizationId) {
        String originType = issueConvertDTO.getTypeCode();
        if (originType.equals(SUB_TASK)) {
            issueConvertDTO.setParentIssueId(null);
        }
        if (STORY_TYPE.equals(issueConvertDTO.getTypeCode()) && issueConvertDTO.getStoryPoints() != null) {
            issueConvertDTO.setStoryPoints(null);
        }
        if (issueUpdateTypeDTO.getTypeCode().equals(ISSUE_EPIC)) {
            issueConvertDTO.setRank(null);
            issueConvertDTO.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueConvertDTO.setEpicName(issueUpdateTypeDTO.getEpicName());
            List<LookupValueDTO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueConvertDTO.initializationColor(colorList);
            issueConvertDTO.setRemainingTime(null);
            issueConvertDTO.setEpicId(0L);
            //排序编号
            Integer sequence = issueMapper.queryMaxEpicSequenceByProject(issueConvertDTO.getProjectId());
            issueConvertDTO.setEpicSequence(sequence == null ? 0 : sequence + 1);
        } else if (issueConvertDTO.getTypeCode().equals(ISSUE_EPIC)) {
            // 如果之前类型是epic，会把该epic下的issue的epicId置为0
            issueAccessDataService.batchUpdateIssueEpicId(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueId());
            issueConvertDTO.setTypeCode(issueUpdateTypeDTO.getTypeCode());
            issueConvertDTO.setColorCode(null);
            issueConvertDTO.setEpicName(null);
            issueConvertDTO.setEpicSequence(null);
            //rank值重置
            calculationRank(issueConvertDTO.getProjectId(), issueConvertDTO);
        } else {
            issueConvertDTO.setTypeCode(issueUpdateTypeDTO.getTypeCode());
        }
        issueConvertDTO.setIssueTypeId(issueUpdateTypeDTO.getIssueTypeId());
        issueAccessDataService.update(issueConvertDTO, new String[]{TYPE_CODE_FIELD, REMAIN_TIME_FIELD, PARENT_ISSUE_ID, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, STORY_POINTS_FIELD, RANK_FIELD, EPIC_SEQUENCE, ISSUE_TYPE_ID});
        return queryIssue(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueId(), organizationId);
    }

    @Override
    public IssueConvertDTO queryIssueByProjectIdAndIssueId(Long projectId, Long issueId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setProjectId(projectId);
        issueDTO.setIssueId(issueId);
        return ConvertHelper.convert(issueMapper.selectOne(issueDTO), IssueConvertDTO.class);
    }

    private void handleCreateLabelIssue(List<LabelIssueRelVO> labelIssueRelVOList, Long issueId) {
        if (labelIssueRelVOList != null && !labelIssueRelVOList.isEmpty()) {
            List<LabelIssueRelE> labelIssueEList = ConvertHelper.convertList(labelIssueRelVOList, LabelIssueRelE.class);
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

    private void handleCreateIssueLink(List<IssueLinkCreateVO> issueLinkCreateVOList, Long projectId, Long issueId) {
        if (issueLinkCreateVOList != null && !issueLinkCreateVOList.isEmpty()) {
            List<IssueLinkDTO> issueLinkDTOList = issueLinkAssembler.toTargetList(issueLinkCreateVOList, IssueLinkDTO.class);
            issueLinkDTOList.forEach(issueLinkDTO -> {
                Long linkIssueId = issueLinkDTO.getLinkedIssueId();
                issueLinkDTO.setIssueId(issueLinkDTO.getIn() ? issueId : linkIssueId);
                issueLinkDTO.setLinkedIssueId(issueLinkDTO.getIn() ? linkIssueId : issueId);
                issueLinkDTO.setProjectId(projectId);
                issueLinkValidator.verifyCreateData(issueLinkDTO);
                if (issueLinkValidator.checkUniqueLink(issueLinkDTO)) {
                    issueLinkService.create(issueLinkDTO);
                }
            });
        }
    }

    private void handleVersionIssueRel(List<VersionIssueRelE> versionIssueRelEList, Long projectId, Long issueId) {
        versionIssueRelEList.forEach(versionIssueRelE -> {
            versionIssueRelE.setIssueId(issueId);
            versionIssueRelE.setProjectId(projectId);
            versionIssueRelE.setRelationType(versionIssueRelE.getRelationType() == null ? "fix" : versionIssueRelE.getRelationType());
            issueValidator.verifyVersionIssueRelData(versionIssueRelE);
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
                    ProductVersionCreateVO productVersionCreateVO = issueAssembler.toTarget(productVersionE, ProductVersionCreateVO.class);
                    ProductVersionDetailVO productVersionDetailVO = productVersionService.createVersion(projectId, productVersionCreateVO);
                    versionIssueRelE.setVersionId(productVersionDetailVO.getVersionId());
                }
            }
            handleVersionIssueRelCreate(versionIssueRelE);
        });
    }

    private void handleVersionIssueRelCreate(VersionIssueRelE versionIssueRelE) {
        if (issueValidator.existVersionIssueRel(versionIssueRelE)) {
            versionIssueRelService.create(versionIssueRelE);
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
        issueValidator.verifyComponentIssueRelData(componentIssueRelE);
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
        if (issueValidator.existComponentIssueRel(componentIssueRelE)) {
            componentIssueRelRepository.create(componentIssueRelE);
        }
    }

    private void handleComponentIssue(ComponentIssueRelE componentIssueRelE, Long issueId, ProjectInfoE projectInfoE) {
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentMapper.selectByPrimaryKey(
                componentIssueRelE.getComponentId()), IssueComponentE.class);
        if (ISSUE_MANAGER_TYPE.equals(issueComponentE.getDefaultAssigneeRole()) && issueComponentE.getManagerId() !=
                null && issueComponentE.getManagerId() != 0) {
            //如果模块有选择模块负责人或者经办人的话，对应的issue的负责人要修改
            IssueConvertDTO issueConvertDTO = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueConvertDTO.class);
            Boolean condition = (issueConvertDTO.getAssigneeId() == null || issueConvertDTO.getAssigneeId() == 0) ||
                    (projectInfoE.getDefaultAssigneeType() != null);
            if (condition) {
                issueConvertDTO.setAssigneeId(issueComponentE.getManagerId());
                issueAccessDataService.update(issueConvertDTO, new String[]{"assigneeId"});
            }
        }
    }

    private void handleUpdateLabelIssue(List<LabelIssueRelVO> labelIssueRelVOList, Long issueId, Long projectId) {
        if (labelIssueRelVOList != null) {
            if (!labelIssueRelVOList.isEmpty()) {
                LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
                labelIssueRelDTO.setIssueId(issueId);
                List<LabelIssueRelE> originLabels = ConvertHelper.convertList(labelIssueRelMapper.select(labelIssueRelDTO), LabelIssueRelE.class);
                List<LabelIssueRelE> labelIssueEList = ConvertHelper.convertList(labelIssueRelVOList, LabelIssueRelE.class);
                List<LabelIssueRelE> labelIssueCreateList = labelIssueEList.stream().filter(labelIssueRelE ->
                        labelIssueRelE.getLabelId() != null).collect(Collectors.toList());
                List<Long> curLabelIds = originLabels.stream().
                        map(LabelIssueRelE::getLabelId).collect(Collectors.toList());
                List<Long> createLabelIds = labelIssueCreateList.stream().
                        map(LabelIssueRelE::getLabelId).collect(Collectors.toList());
                curLabelIds.forEach(id -> {
                    if (!createLabelIds.contains(id)) {
                        LabelIssueRelDTO delete = new LabelIssueRelDTO();
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
                        versionIssueRelService.delete(versionIssueRelDO);
                    }
                });
                versionIssueRelES.forEach(rel -> rel.setRelationType(versionType));
                handleVersionIssueRel(versionIssueRelES, projectId, issueId);
            } else {
                VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
                versionIssueRelE.createBatchDeleteVersionIssueRel(projectId, issueId, versionType);
                versionIssueRelService.batchDeleteByIssueIdAndTypeArchivedExceptInfluence(versionIssueRelE);
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
        issueValidator.verifyLabelIssueData(labelIssueRelE);
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
        if (issueValidator.existLabelIssue(labelIssueRelE)) {
            labelIssueRelRepository.create(labelIssueRelE);
        }
    }

    private Long getActiveSprintId(Long projectId) {
        SprintDTO sprintDTO = sprintService.getActiveSprint(projectId);
        if (sprintDTO != null) {
            return sprintDTO.getSprintId();
        }
        return null;
    }

    @Override
    public PageInfo<IssueNumVO> queryIssueByOption(Long projectId, Long issueId, String issueNum, Boolean onlyActiveSprint, Boolean self, String content, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "ai", new HashMap<>()));
        //pageRequest.resetOrder("ai", new HashMap<>());
        IssueNumDTO issueNumDTO = null;
        if (self) {
            issueNumDTO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDTO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        Long activeSprintId = onlyActiveSprint ? getActiveSprintId(projectId) : null;
        PageInfo<IssueNumDTO> issueDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueMapper.
                queryIssueByOption(projectId, issueId, issueNum, activeSprintId, self, content));
        if (self && issueNumDTO != null) {
            issueDOPage.getList().add(0, issueNumDTO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }

        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueNumDoToDto(issueDOPage.getList(), projectId));
    }

    @Override
    public void exportIssues(Long projectId, SearchVO searchVO, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        //处理根据界面筛选结果导出的字段
        Map<String, String[]> fieldMap = handleExportFields(searchVO.getExportFieldCodes(), projectId, organizationId);
        String[] fieldCodes = fieldMap.get(FIELD_CODES);
        String[] fieldNames = fieldMap.get(FIELD_NAMES);

        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(projectId);
        projectInfoDTO = projectInfoMapper.selectOne(projectInfoDTO);
        String projectCode = projectInfoDTO.getProjectCode();
        ProjectVO project = userService.queryProject(projectId);
        if (project == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        project.setCode(projectInfoDTO.getProjectCode());
        Boolean condition = handleSearchUser(searchVO, projectId);
        if (condition) {
            String filterSql = null;
            if (searchVO.getQuickFilterIds() != null && !searchVO.getQuickFilterIds().isEmpty()) {
                filterSql = getQuickFilter(searchVO.getQuickFilterIds());
            }
            final String searchSql = filterSql;
            //连表查询需要设置主表别名
            List<Long> issueIds = issueMapper.queryIssueIdsListWithSub(projectId, searchVO, searchSql, searchVO.getAssigneeFilterIds());
            List<ExportIssuesDTO> exportIssues = issueAssembler.exportIssuesDOListToExportIssuesDTO(issueMapper.queryExportIssues(projectId, issueIds, projectCode), projectId);
            if (!issueIds.isEmpty()) {
                Map<Long, List<SprintNameDTO>> closeSprintNames = issueMapper.querySprintNameByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(SprintNameDTO::getIssueId));
                Map<Long, List<VersionIssueRelDO>> fixVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, FIX_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
                Map<Long, List<VersionIssueRelDO>> influenceVersionNames = issueMapper.queryVersionNameByIssueIds(projectId, issueIds, INFLUENCE_RELATION_TYPE).stream().collect(Collectors.groupingBy(VersionIssueRelDO::getIssueId));
                Map<Long, List<LabelIssueRelDTO>> labelNames = issueMapper.queryLabelIssueByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(LabelIssueRelDTO::getIssueId));
                Map<Long, List<ComponentIssueRelDO>> componentMap = issueMapper.queryComponentIssueByIssueIds(projectId, issueIds).stream().collect(Collectors.groupingBy(ComponentIssueRelDO::getIssueId));
                Map<Long, Map<String, String>> foundationCodeValue = foundationFeignClient.queryFieldValueWithIssueIds(organizationId, projectId, issueIds).getBody();
                exportIssues.forEach(exportIssue -> {
                    String closeSprintName = closeSprintNames.get(exportIssue.getIssueId()) != null ? closeSprintNames.get(exportIssue.getIssueId()).stream().map(SprintNameDTO::getSprintName).collect(Collectors.joining(",")) : "";
                    String fixVersionName = fixVersionNames.get(exportIssue.getIssueId()) != null ? fixVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                    String influenceVersionName = influenceVersionNames.get(exportIssue.getIssueId()) != null ? influenceVersionNames.get(exportIssue.getIssueId()).stream().map(VersionIssueRelDO::getName).collect(Collectors.joining(",")) : "";
                    String labelName = labelNames.get(exportIssue.getIssueId()) != null ? labelNames.get(exportIssue.getIssueId()).stream().map(LabelIssueRelDTO::getLabelName).collect(Collectors.joining(",")) : "";
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
    public void exportProgramIssues(Long programId, SearchVO searchVO, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        ProjectVO project = userService.queryProject(programId);
        if (project == null) {
            throw new CommonException(PROJECT_ERROR);
        }
        Map<String, String[]> fieldMap = handleExportFieldsInProgram(searchVO.getExportFieldCodes());
        String[] fieldCodes = fieldMap.get(FIELD_CODES);
        String[] fieldNames = fieldMap.get(FIELD_NAMES);
        List<Long> exportIssueIds = issueMapper.selectExportIssueIdsInProgram(programId, searchVO);
        List<FeatureExportDO> featureExportDOList = issueMapper.selectExportIssuesInProgram(programId, exportIssueIds);
        List<FeatureExportDTO> featureExportDTOList = (featureExportDOList != null && !featureExportDOList.isEmpty() ? ConvertHelper.convertList(featureExportDOList, FeatureExportDTO.class) : null);
        if (featureExportDTOList != null && !featureExportDTOList.isEmpty()) {
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
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

        if (exportFieldCodes != null && exportFieldCodes.size() != 0) {
            Map<String, String> data = new HashMap<>(FIELDS.length + userDefinedFieldDTOS.size());
            for (int i = 0; i < FIELDS.length; i++) {
                data.put(FIELDS[i], FIELDS_NAME[i]);
            }
            for (ObjectSchemeFieldDTO userDefinedFieldDTO : userDefinedFieldDTOS) {
                data.put(userDefinedFieldDTO.getCode(), userDefinedFieldDTO.getName());
            }

            List<String> fieldCodes = new ArrayList<>(exportFieldCodes.size());
            List<String> fieldNames = new ArrayList<>(exportFieldCodes.size());
            exportFieldCodes.forEach(code -> {
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
    public IssueVO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionVO copyConditionVO, Long organizationId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        IssueDetailDTO issueDetailDTO = issueMapper.queryIssueDetail(projectId, issueId);
        if (issueDetailDTO != null) {
            Long newIssueId;
            Long objectVersionNumber;
            issueDetailDTO.setSummary(copyConditionVO.getSummary());
            IssueTypeVO issueTypeVO = issueFeignClient.queryIssueTypeById(ConvertUtil.getOrganizationId(projectId), issueDetailDTO.getIssueTypeId()).getBody();
            if (issueTypeVO.getTypeCode().equals(SUB_TASK)) {
                IssueSubCreateVO issueSubCreateVO = issueAssembler.issueDtoToIssueSubCreateDto(issueDetailDTO);
                IssueSubVO newIssue = stateMachineService.createSubIssue(issueSubCreateVO);
                newIssueId = newIssue.getIssueId();
                objectVersionNumber = newIssue.getObjectVersionNumber();
            } else {
                IssueCreateVO issueCreateVO = issueAssembler.issueDtoToIssueCreateDto(issueDetailDTO);
                issueCreateVO.setEpicName(issueCreateVO.getTypeCode().equals(ISSUE_EPIC) ? issueCreateVO.getEpicName() + COPY : null);
                // deal feature extends table
                if (ISSUE_TYPE_FEATURE.equals(issueDetailDTO.getTypeCode())) {
                    FeatureDO featureDO = new FeatureDO();
                    featureDO.setIssueId(issueId);
                    FeatureDO res = featureMapper.selectOne(featureDO);
                    if (res != null) {
                        FeatureVO featureVO = new FeatureVO();
                        featureVO.setAcceptanceCritera(res.getAcceptanceCritera());
                        featureVO.setBenfitHypothesis(res.getBenfitHypothesis());
                        featureVO.setFeatureType(res.getFeatureType());
                        featureVO.setProjectId(projectId);
                        issueCreateVO.setFeatureVO(featureVO);
                    }
                }
                if ("program".equals(applyType)) {
                    issueCreateVO.setProgramId(projectId);
                }
                IssueVO newIssue = stateMachineService.createIssue(issueCreateVO, applyType);
                newIssueId = newIssue.getIssueId();
                objectVersionNumber = newIssue.getObjectVersionNumber();
            }
            //复制链接
            batchCreateCopyIssueLink(copyConditionVO.getIssueLink(), issueId, newIssueId, projectId);
            //生成一条复制的关联
            createCopyIssueLink(issueDetailDTO.getIssueId(), newIssueId, projectId);
            //复制故事点和剩余工作量并记录日志
            copyStoryPointAndRemainingTimeData(issueDetailDTO, projectId, newIssueId, objectVersionNumber);
            //复制冲刺
            handleCreateCopyIssueSprintRel(copyConditionVO.getSprintValues(), issueDetailDTO, newIssueId);
            if (copyConditionVO.getSubTask()) {
                List<IssueDTO> subIssueDTOList = issueDetailDTO.getSubIssueDTOList();
                if (subIssueDTOList != null && !subIssueDTOList.isEmpty()) {
                    subIssueDTOList.forEach(issueDO -> copySubIssue(issueDO, newIssueId, projectId));
                }
            }
            return queryIssue(projectId, newIssueId, organizationId);
        } else {
            throw new CommonException("error.issue.copyIssueByIssueId");
        }
    }

    private void copyStoryPointAndRemainingTimeData(IssueDetailDTO issueDetailDTO, Long projectId, Long issueId, Long objectVersionNumber) {
        if (issueDetailDTO.getStoryPoints() == null && issueDetailDTO.getEstimateTime() == null) {
            return;
        }
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setStoryPoints(issueDetailDTO.getStoryPoints());
        issueUpdateVO.setRemainingTime(issueDetailDTO.getRemainingTime());
        issueUpdateVO.setIssueId(issueId);
        issueUpdateVO.setObjectVersionNumber(objectVersionNumber);
        List<String> fieldList = new ArrayList<>();
        if (issueDetailDTO.getStoryPoints() != null) {
            fieldList.add(STORY_POINTS_FIELD);
        }
        if (issueDetailDTO.getRemainingTime() != null) {
            fieldList.add(REMAIN_TIME_FIELD);
        }
        updateIssue(projectId, issueUpdateVO, fieldList);
    }

    private void copySubIssue(IssueDTO issueDTO, Long newIssueId, Long projectId) {
        IssueDetailDTO subIssueDetailDTO = issueMapper.queryIssueDetail(issueDTO.getProjectId(), issueDTO.getIssueId());
        IssueSubCreateVO issueSubCreateVO = issueAssembler.issueDtoToSubIssueCreateDto(subIssueDetailDTO, newIssueId);
        IssueSubVO newSubIssue = stateMachineService.createSubIssue(issueSubCreateVO);
        //复制剩余工作量并记录日志
        if (issueDTO.getRemainingTime() != null) {
            IssueUpdateVO subIssueUpdateVO = new IssueUpdateVO();
            subIssueUpdateVO.setRemainingTime(issueDTO.getRemainingTime());
            subIssueUpdateVO.setIssueId(newSubIssue.getIssueId());
            subIssueUpdateVO.setObjectVersionNumber(newSubIssue.getObjectVersionNumber());
            updateIssue(projectId, subIssueUpdateVO, Lists.newArrayList(REMAIN_TIME_FIELD));
        }
    }

    private void handleCreateCopyIssueSprintRel(Boolean sprintValues, IssueDetailDTO issueDetailDTO, Long newIssueId) {
        if (sprintValues && issueDetailDTO.getActiveSprint() != null) {
            handleCreateSprintRel(issueDetailDTO.getActiveSprint().getSprintId(), issueDetailDTO.getProjectId(), newIssueId);
        }
    }

    private void batchCreateCopyIssueLink(Boolean condition, Long issueId, Long newIssueId, Long projectId) {
        if (condition) {
            List<IssueLinkDTO> issueLinkDTOList = modelMapper.map(issueLinkMapper.queryIssueLinkByIssueId(issueId, projectId, false), new TypeToken<List<IssueLinkDTO>>(){}.getType());
            issueLinkDTOList.forEach(issueLinkDTO -> {
                IssueLinkDTO copy = new IssueLinkDTO();
                if (issueLinkDTO.getIssueId().equals(issueId)) {
                    copy.setIssueId(newIssueId);
                    copy.setLinkedIssueId(issueLinkDTO.getLinkedIssueId());
                }
                if (issueLinkDTO.getLinkedIssueId().equals(issueId)) {
                    copy.setIssueId(issueLinkDTO.getIssueId());
                    copy.setLinkedIssueId(newIssueId);
                }
                copy.setLinkTypeId(issueLinkDTO.getLinkTypeId());
                copy.setProjectId(projectId);
                if (issueLinkValidator.checkUniqueLink(copy)) {
                    issueLinkService.create(copy);
                }
            });
        }
    }

    private void createCopyIssueLink(Long issueId, Long newIssueId, Long projectId) {
        IssueLinkTypeDTO query = new IssueLinkTypeDTO();
        query.setProjectId(projectId);
        query.setOutWard("复制");
        IssueLinkTypeDTO issueLinkTypeDTO = issueLinkTypeMapper.selectOne(query);
        if (issueLinkTypeDTO != null) {
            IssueLinkDTO issueLink = new IssueLinkDTO();
            issueLink.setLinkedIssueId(issueId);
            issueLink.setLinkTypeId(issueLinkTypeDTO.getLinkTypeId());
            issueLink.setIssueId(newIssueId);
            issueLink.setProjectId(projectId);
            if (issueLinkValidator.checkUniqueLink(issueLink)) {
                issueLinkService.create(issueLink);
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
                issueAccessDataService.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
                issueAccessDataService.issueToDestinationByIds(projectId, sprintId, issueIds, new Date(), customUserDetails.getUserId());
            } else {
                issueSprintRelRepository.createIssueSprintRel(ConvertHelper.convert(issueSprintRelDO, IssueSprintRelE.class));
            }
        }
    }

    @Override
    public IssueSubVO transformedSubTask(Long projectId, Long organizationId, IssueTransformSubTask issueTransformSubTask) {
        IssueConvertDTO issueConvertDTO = ConvertHelper.convert(queryIssueByIssueIdAndProjectId(projectId, issueTransformSubTask.getIssueId()), IssueConvertDTO.class);
        if (issueConvertDTO != null) {
            if (!issueConvertDTO.getTypeCode().equals(SUB_TASK)) {
                issueConvertDTO.setObjectVersionNumber(issueTransformSubTask.getObjectVersionNumber());
                List<Long> subIssueIds = issueMapper.querySubIssueIdsByIssueId(projectId, issueConvertDTO.getIssueId());
                if (subIssueIds != null && !subIssueIds.isEmpty()) {
                    throw new CommonException("error.transformedSubTask.issueHaveSubIssue");
                }
                issueConvertDTO.setEpicSequence(null);
                issueConvertDTO.setStoryPoints(null);
                issueConvertDTO.setStatusId(issueTransformSubTask.getStatusId());
                issueConvertDTO.setTypeCode(SUB_TASK);
                issueConvertDTO.setIssueTypeId(issueTransformSubTask.getIssueTypeId());
                issueConvertDTO.setParentIssueId(issueTransformSubTask.getParentIssueId());
                issueValidator.verifySubTask(issueTransformSubTask.getParentIssueId());
                //删除链接
                issueLinkService.deleteByIssueId(issueConvertDTO.getIssueId());
                issueAccessDataService.update(issueConvertDTO, new String[]{TYPE_CODE_FIELD, ISSUE_TYPE_ID, RANK_FIELD, STATUS_ID, PARENT_ISSUE_ID, EPIC_SEQUENCE, STORY_POINTS_FIELD});
                Long sprintId = issueMapper.selectUnCloseSprintId(projectId, issueTransformSubTask.getParentIssueId());
                List<Long> issueIds = new ArrayList<>();
                issueIds.add(issueConvertDTO.getIssueId());
                if (sprintId != null) {
                    insertSprintWhenTransform(issueConvertDTO.getIssueId(), sprintId, projectId, issueIds);
                } else {
                    if (issueMapper.selectUnCloseSprintId(projectId, issueConvertDTO.getIssueId()) != null) {
                        BatchRemoveSprintE batchRemoveSprintE = new BatchRemoveSprintE(projectId, sprintId, issueIds);
                        issueAccessDataService.removeIssueFromSprintByIssueIds(batchRemoveSprintE);
                    }
                }
                return queryIssueSub(projectId, organizationId, issueConvertDTO.getIssueId());
            } else {
                throw new CommonException("error.issueValidator.subTaskError");
            }
        } else {
            throw new CommonException("error.issueValidator.issueNoFound");
        }
    }

    @Override
    public synchronized IssueVO transformedTask(IssueConvertDTO issueConvertDTO, IssueTransformTask issueTransformTask, Long organizationId) {
        String originType = issueConvertDTO.getTypeCode();
        if (originType.equals(SUB_TASK)) {
            issueConvertDTO.setParentIssueId(null);
        }
        if (STORY_TYPE.equals(issueConvertDTO.getTypeCode()) && issueConvertDTO.getStoryPoints() != null) {
            issueConvertDTO.setStoryPoints(null);
        }
        if (issueTransformTask.getTypeCode().equals(ISSUE_EPIC)) {
            issueConvertDTO.setRank(null);
            issueConvertDTO.setTypeCode(issueTransformTask.getTypeCode());
            issueConvertDTO.setEpicName(issueTransformTask.getEpicName());
            List<LookupValueDTO> colorList = lookupValueMapper.queryLookupValueByCode(EPIC_COLOR_TYPE).getLookupValues();
            issueConvertDTO.initializationColor(colorList);
            issueConvertDTO.setRemainingTime(null);
            issueConvertDTO.setEpicId(0L);
            //排序编号
            Integer sequence = issueMapper.queryMaxEpicSequenceByProject(issueConvertDTO.getProjectId());
            issueConvertDTO.setEpicSequence(sequence == null ? 0 : sequence + 1);
        } else if (issueConvertDTO.getTypeCode().equals(ISSUE_EPIC)) {
            // 如果之前类型是epic，会把该epic下的issue的epicId置为0
            issueAccessDataService.batchUpdateIssueEpicId(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueId());
            issueConvertDTO.setTypeCode(issueTransformTask.getTypeCode());
            issueConvertDTO.setColorCode(null);
            issueConvertDTO.setEpicName(null);
            issueConvertDTO.setEpicSequence(null);
            //rank值重置
            calculationRank(issueConvertDTO.getProjectId(), issueConvertDTO);
        } else {
            issueConvertDTO.setTypeCode(issueTransformTask.getTypeCode());
        }
        if (issueTransformTask.getStatusId() != null) {
            issueConvertDTO.setStatusId(issueTransformTask.getStatusId());
        }
        issueConvertDTO.setIssueTypeId(issueTransformTask.getIssueTypeId());
        issueAccessDataService.update(issueConvertDTO, new String[]{TYPE_CODE_FIELD, REMAIN_TIME_FIELD, PARENT_ISSUE_ID, EPIC_NAME_FIELD, COLOR_CODE_FIELD, EPIC_ID_FIELD, STORY_POINTS_FIELD, RANK_FIELD, EPIC_SEQUENCE, ISSUE_TYPE_ID, STATUS_ID});
        return queryIssue(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueId(), organizationId);
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

    private IssueDTO queryIssueByIssueIdAndProjectId(Long projectId, Long issueId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setIssueId(issueId);
        issueDTO.setProjectId(projectId);
        return issueMapper.selectOne(issueDTO);
    }

    @Override
    public List<IssueInfoVO> listByIssueIds(Long projectId, List<Long> issueIds) {
        return ConvertHelper.convertList(issueMapper.listByIssueIds(projectId, issueIds), IssueInfoVO.class);
    }

    @Override
    public PageInfo<IssueListTestVO> listIssueWithoutSubToTestComponent(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<IssueDTO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueMapper.listIssueWithoutSubToTestComponent(projectId, searchVO.getSearchArgs(),
                searchVO.getAdvancedSearchArgs(), searchVO.getOtherArgs(), searchVO.getContents()));
        return handleIssueListTestDoToDto(issueDOPage, organizationId, projectId);
    }

    private PageInfo<IssueListTestVO> handleIssueListTestDoToDto(PageInfo<IssueDTO> issueDOPage, Long organizationId, Long projectId) {
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueDoToIssueTestListDto(issueDOPage.getList(), priorityMap, statusMapDTOMap, issueTypeDTOMap));
    }

    @Override
    public PageInfo<IssueListTestWithSprintVersionVO> listIssueWithLinkedIssues(Long projectId, SearchVO searchVO, PageRequest pageRequest, Long organizationId) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<IssueDTO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() ->
                issueMapper.listIssueWithLinkedIssues(projectId, searchVO.getSearchArgs(),
                        searchVO.getAdvancedSearchArgs(), searchVO.getOtherArgs(), searchVO.getContents()));
        return handleILTDTOToILTWSVDTO(projectId, handleIssueListTestDoToDto(issueDOPage, organizationId, projectId));
    }

    private PageInfo<IssueListTestWithSprintVersionVO> handleILTDTOToILTWSVDTO(Long projectId, PageInfo<IssueListTestVO> issueListTestDTOSPage) {

//        Map<Long, ProductVersionDataVO> versionIssueRelDTOMap = productVersionService
//                .queryVersionByProjectId(projectId).stream().collect(
//                        Collectors.toMap(ProductVersionDataVO::getVersionId, x-> x));

        Map<Long, SprintDTO> sprintDoMap = sprintMapper.getSprintByProjectId(projectId).stream().collect(
                Collectors.toMap(SprintDTO::getSprintId, x -> x));

        List<IssueListTestWithSprintVersionVO> issueListTestWithSprintVersionVOS = new ArrayList<>();

        for (int a = 0; a < issueListTestDTOSPage.getSize(); a++) {
            IssueListTestWithSprintVersionVO issueListTestWithSprintVersionVO = new IssueListTestWithSprintVersionVO(issueListTestDTOSPage.getList().get(a));

            List<VersionIssueRelDTO> versionList = new ArrayList<>();
            List<IssueSprintDTO> sprintList = new ArrayList<>();

            issueMapper.queryVersionIssueRelByIssueId(issueListTestWithSprintVersionVO.getIssueId()).forEach(v -> {
                VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
                versionIssueRelDTO.setVersionId(v.getVersionId());
                versionIssueRelDTO.setName(v.getName());

                versionList.add(versionIssueRelDTO);
            });

            issueMapper.querySprintNameByIssueId(issueListTestWithSprintVersionVO.getIssueId()).forEach(v -> {
                SprintDTO sprintDTO = sprintDoMap.get(v.getSprintId());

                IssueSprintDTO issueSprintDTO = new IssueSprintDTO();
                issueSprintDTO.setSprintId(sprintDTO.getSprintId());
                issueSprintDTO.setSprintName(sprintDTO.getSprintName());
                issueSprintDTO.setStatusCode(sprintDTO.getStatusCode());

                sprintList.add(issueSprintDTO);
            });

            issueListTestWithSprintVersionVO.setVersionDTOList(versionList);
            issueListTestWithSprintVersionVO.setSprintDTOList(sprintList);

            issueListTestWithSprintVersionVOS.add(issueListTestWithSprintVersionVO);
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueListTestDTOSPage, issueListTestWithSprintVersionVOS);
    }

    @Override
    public List<IssueCreationNumVO> queryIssueNumByTimeSlot(Long projectId, String typeCode, Integer timeSlot) {
        //h2 不支持dateSub函数，这个函数不能自定义
        Date date = MybatisFunctionTestUtil.dataSubFunction(new Date(), timeSlot);
        return modelMapper.map(issueMapper.queryIssueNumByTimeSlot(projectId, typeCode, date), new TypeToken<List<IssueCreationNumVO>>(){}.getType());
    }

    @Override
    public PageInfo<IssueNumVO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum, Boolean self, String content, PageRequest pageRequest) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder("search", new HashMap<>());
        IssueNumDTO issueNumDTO = null;
        if (self) {
            issueNumDTO = issueMapper.queryIssueByIssueNumOrIssueId(projectId, issueId, issueNum);
            if (issueNumDTO != null) {
                pageRequest.setSize(pageRequest.getSize() - 1);
            }
        }
        PageInfo<IssueNumDTO> issueDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() ->
                issueMapper.queryIssueByOptionForAgile(projectId, issueId, issueNum, self, content));
        if (self && issueNumDTO != null) {
            issueDOPage.getList().add(0, issueNumDTO);
            issueDOPage.setSize(issueDOPage.getSize() + 1);
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueDOPage, issueAssembler.issueNumDoToDto(issueDOPage.getList(), projectId));
    }

    @Override
    public synchronized EpicDataVO dragEpic(Long projectId, EpicSequenceDTO epicSequenceDTO) {
        if (epicSequenceDTO.getAfterSequence() == null && epicSequenceDTO.getBeforeSequence() == null) {
            throw new CommonException("error.dragEpic.noSequence");
        }
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setIssueId(epicSequenceDTO.getEpicId());
        issueDTO.setProjectId(projectId);
        IssueConvertDTO issueConvertDTO = ConvertHelper.convert(issueMapper.selectOne(issueDTO), IssueConvertDTO.class);
        if (issueConvertDTO == null) {
            throw new CommonException("error.issue.notFound");
        } else {
            if (epicSequenceDTO.getAfterSequence() == null) {
                Integer maxSequence = productVersionMapper.queryMaxAfterSequence(epicSequenceDTO.getBeforeSequence(), projectId);
                epicSequenceDTO.setAfterSequence(maxSequence);
            } else if (epicSequenceDTO.getBeforeSequence() == null) {
                Integer minSequence = productVersionMapper.queryMinBeforeSequence(epicSequenceDTO.getAfterSequence(), projectId);
                epicSequenceDTO.setBeforeSequence(minSequence);
            }
            handleSequence(epicSequenceDTO, projectId, issueConvertDTO);
        }
        return epicDataAssembler.toTarget(issueMapper.queryEpicListByEpic(epicSequenceDTO.getEpicId(), projectId), EpicDataVO.class);
    }

    @Override
    public List<PieChartVO> issueStatistic(Long projectId, String type, List<String> issueTypes) {
        return reportAssembler.toTargetList(issueMapper.issueStatistic(projectId, type, issueTypes), PieChartVO.class);
    }

    @Override
    public PageInfo<IssueComponentDetailDTO> listIssueWithoutSubDetail(Long projectId, SearchVO searchVO, PageRequest pageRequest) {
        //连表查询需要设置主表别名
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), SEARCH, new HashMap<>()));
        //pageRequest.resetOrder(SEARCH, new HashMap<>());
        PageInfo<Long> issueIds = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueMapper.listIssueIdsWithoutSubDetail(projectId, searchVO.getSearchArgs(),
                searchVO.getAdvancedSearchArgs(), searchVO.getOtherArgs(), searchVO.getContents()));
        List<IssueComponentDetailDO> issueComponentDetailDOS = new ArrayList<>(issueIds.getList().size());
        if (issueIds.getList() != null && !issueIds.getList().isEmpty()) {
            issueComponentDetailDOS.addAll(issueMapper.listIssueWithoutSubDetailByIssueIds(issueIds.getList()));
        }
        return PageUtil.buildPageInfoWithPageInfoList(issueIds, issueAssembler.issueComponentDetailDoToDto(projectId, issueComponentDetailDOS));
    }


    private void handleSequence(EpicSequenceDTO epicSequenceDTO, Long projectId, IssueConvertDTO issueConvertDTO) {
        if (epicSequenceDTO.getBeforeSequence() == null) {
            issueConvertDTO.setEpicSequence(epicSequenceDTO.getAfterSequence() + 1);
            issueAccessDataService.update(issueConvertDTO, new String[]{EPIC_SEQUENCE});
        } else if (epicSequenceDTO.getAfterSequence() == null) {
            if (issueConvertDTO.getEpicSequence() > epicSequenceDTO.getBeforeSequence()) {
                Integer add = issueConvertDTO.getEpicSequence() - epicSequenceDTO.getBeforeSequence();
                if (add > 0) {
                    issueConvertDTO.setEpicSequence(epicSequenceDTO.getBeforeSequence() - 1);
                    issueAccessDataService.update(issueConvertDTO, new String[]{EPIC_SEQUENCE});
                } else {
                    issueAccessDataService.batchUpdateSequence(epicSequenceDTO.getBeforeSequence(), projectId,
                            issueConvertDTO.getEpicSequence() - epicSequenceDTO.getBeforeSequence() + 1, issueConvertDTO.getIssueId());
                }
            }
        } else {
            Integer sequence = epicSequenceDTO.getAfterSequence() + 1;
            issueConvertDTO.setEpicSequence(sequence);
            issueAccessDataService.update(issueConvertDTO, new String[]{EPIC_SEQUENCE});
            Integer update = sequence - epicSequenceDTO.getBeforeSequence();
            if (update >= 0) {
                issueAccessDataService.batchUpdateSequence(epicSequenceDTO.getBeforeSequence(), projectId, update + 1, issueConvertDTO.getIssueId());
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

    private void getDoneIds(Map<Long, StatusMapVO> statusMapDTOMap, List<Long> doneIds) {
        for (Long key : statusMapDTOMap.keySet()) {
            if ("done".equals(statusMapDTOMap.get(key).getType())) {
                doneIds.add(key);
            }
        }
    }

//    @Override
//    public List<StoryMapIssueDTO> listIssuesByProjectId(Long projectId, String type, String pageType, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds) {
//        List<StoryMapIssueDTO> storyMapIssueDTOList = null;
//        String filterSql = null;
//        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
//            filterSql = getQuickFilter(quickFilterIds);
//        }
//        //保存用户选择的泳道
//        handleSaveUserSetting(projectId, type, pageType);
//        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
//        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
//        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
//        List<Long> doneIds = new ArrayList<>();
//        getDoneIds(statusMapDTOMap, doneIds);
//        switch (type) {
//            case STORYMAP_TYPE_SPRINT:
//                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdSprint(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
//                break;
//            case STORYMAP_TYPE_VERSION:
//                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdVersion(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
//                break;
//            case STORYMAP_TYPE_NONE:
//                storyMapIssueDTOList = storyMapIssueAssembler.storyMapIssueDOToDTO(issueMapper.listIssuesByProjectIdNone(projectId, pageType, assigneeId, onlyStory, filterSql, doneIds, assigneeFilterIds), priorityMap, statusMapDTOMap, issueTypeDTOMap);
//                break;
//            default:
//                break;
//        }
//        return storyMapIssueDTOList == null ? new ArrayList<>() : storyMapIssueDTOList;
//    }

    private void handleSaveUserSetting(Long projectId, String type, String pageType) {
        if (USERMAP.equals(pageType)) {
            UserSettingDTO userSettingDTO = new UserSettingDTO();
            userSettingDTO.setProjectId(projectId);
            userSettingDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
            userSettingDTO.setTypeCode(STORYMAP);
            UserSettingDTO query = userSettingMapper.selectOne(userSettingDTO);
            if (query == null) {
                userSettingDTO.setStorymapSwimlaneCode(STORYMAP_TYPE_NONE);
                userSettingRepository.create(ConvertHelper.convert(userSettingDTO, UserSettingE.class));
            } else if (!query.getStorymapSwimlaneCode().equals(type)) {
                query.setStorymapSwimlaneCode(type);
                userSettingRepository.update(ConvertHelper.convert(query, UserSettingE.class));
            }
        }
    }


//    @Override
//    public void storymapMove(Long projectId, StoryMapMoveDTO storyMapMoveDTO) {
//        Long sprintId = storyMapMoveDTO.getSprintId();
//        Long versionId = storyMapMoveDTO.getVersionId();
//        Long epicId = storyMapMoveDTO.getEpicId();
//        IssueValidator.checkStoryMapMove(storyMapMoveDTO);
//        dealRank(projectId, storyMapMoveDTO);
//        if (epicId != null) {
//            batchIssueToEpicInStoryMap(projectId, epicId, storyMapMoveDTO);
//        }
//        if (sprintId != null) {
//            batchIssueToSprintInStoryMap(projectId, sprintId, storyMapMoveDTO);
//        }
//        if (versionId != null) {
//            batchToVersionInStoryMap(projectId, versionId, storyMapMoveDTO);
//        }
//    }

    @Override
    public IssueVO issueParentIdUpdate(Long projectId, IssueUpdateParentIdVO issueUpdateParentIdVO) {
        Long issueId = issueUpdateParentIdVO.getIssueId();
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(issueId);
        Long parentIssueId = issueUpdateParentIdVO.getParentIssueId();
        IssueDTO parentIssueDTO = issueMapper.selectByPrimaryKey(parentIssueId);
        IssueValidator.checkParentIdUpdate(issueDTO, parentIssueDTO);
        IssueConvertDTO updateIssue = new IssueConvertDTO();
        updateIssue.setIssueId(issueId);
        updateIssue.setObjectVersionNumber(issueUpdateParentIdVO.getObjectVersionNumber());
        updateIssue.setParentIssueId(issueUpdateParentIdVO.getParentIssueId());
        return modelMapper.map(issueAccessDataService.updateSelective(updateIssue), IssueVO.class);
    }

    @Override
    public JSONObject countUnResolveByProjectId(Long projectId) {
        JSONObject result = new JSONObject();
        result.put("all", issueMapper.countIssueByProjectId(projectId));
        result.put("unresolved", issueMapper.countUnResolveByProjectId(projectId));
        return result;
    }

    @Override
    public List<Long> queryIssueIdsByOptions(Long projectId, SearchVO searchVO) {
        return issueMapper.queryIssueIdsByOptions(projectId, searchVO.getAdvancedSearchArgs(), searchVO.getOtherArgs(), searchVO.getContents());
    }

    @Override
    public PageInfo<UndistributedIssueVO> queryUnDistributedIssues(Long projectId, PageRequest pageRequest) {
        PageInfo<UndistributedIssueDTO> undistributedIssueDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize()).doSelectPageInfo(() ->
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
        UserSettingDTO query = userSettingMapper.selectOne(ConvertHelper.convert(userSettingE, UserSettingDTO.class));
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
        List<IssueDetailDTO> issueDOList = issueMapper.queryByIssueIds(projectId, issueIds);
        if (issueDOList.size() == issueIds.size()) {
            return batchCreateIssue(issueDOList, projectId, versionId);
        } else {
            throw new CommonException("error.issueServiceImpl.issueTypeError");
        }
    }

    private List<Long> batchCreateIssue(List<IssueDetailDTO> issueDOList, Long projectId, Long versionId) {
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

        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(projectId);
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDTO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        issueDOList.forEach(issueDetailDTO -> {
            IssueConvertDTO issueConvertDTO = issueAssembler.toTarget(issueDetailDTO, IssueConvertDTO.class);
            //初始化创建issue设置issue编号、项目默认设置
            issueConvertDTO.initializationIssueByCopy(initStatusId);
            projectInfoRepository.updateIssueMaxNum(projectId, issueConvertDTO.getIssueNum());
            issueConvertDTO.setApplyType(SchemeApplyType.TEST);
            Long issueId = issueAccessDataService.create(issueConvertDTO).getIssueId();
            handleCreateCopyLabelIssueRel(issueDetailDTO.getLabelIssueRelDTOList(), issueId);
            handleCreateCopyComponentIssueRel(issueDetailDTO.getComponentIssueRelDOList(), issueId);
            issueIds.add(issueId);
        });
        VersionIssueRelE versionIssueRelE = new VersionIssueRelE();
        versionIssueRelE.createBatchIssueToVersionE(projectId, versionId, issueIds);
        issueAccessDataService.batchIssueToVersion(versionIssueRelE);
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

    private void handleCreateCopyLabelIssueRel(List<LabelIssueRelDTO> labelIssueRelDTOList, Long issueId) {
        labelIssueRelDTOList.forEach(labelIssueRelDO -> {
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
    public List<IssueProjectVO> queryIssueTestGroupByProject() {
        return issueAssembler.toTargetList(issueMapper.queryIssueTestGroupByProject(), IssueProjectVO.class);
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
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setProjectId(projectId);
        issueDTO.setEpicName(epicName);
        List<IssueDTO> issueDTOList = issueMapper.select(issueDTO);
        return issueDTOList != null && !issueDTOList.isEmpty();
    }

    @Override
    public PageInfo<FeatureCommonVO> queryFeatureList(Long programId, Long organizationId, PageRequest pageRequest, SearchVO searchVO) {
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "issue_page", new HashMap<>()));
        //pageRequest.resetOrder("issue_page", new HashMap<>());
        PageInfo<Long> featureCommonDOPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() ->
                issueMapper.selectFeatureIdsByPage(programId, searchVO)
        );
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        return PageUtil.buildPageInfoWithPageInfoList(featureCommonDOPage, featureCommonDOPage.getList() != null && !featureCommonDOPage.getList().isEmpty() ? featureCommonAssembler.featureCommonDOToDTO(issueMapper.selectFeatureList(programId, featureCommonDOPage.getList()), statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
    }

    @Override
    public List<FeatureCommonVO> queryFeatureListByPiId(Long programId, Long organizationId, Long piId) {
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        List<FeatureCommonVO> featureCommonVOS = modelMapper.map(issueMapper.selectFeatureByPiId(programId, piId), new TypeToken<List<FeatureCommonVO>>() {
        }.getType());
        for (FeatureCommonVO dto : featureCommonVOS) {
            dto.setIssueTypeVO(issueTypeDTOMap.get(dto.getIssuetypeId()));
        }
        return featureCommonVOS;
    }
}