package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 日志切面
 *
 * @author dinghuang123@gmail.com
 * @since 2018/7/23
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class DataLogAspect {

    private static final String ISSUE = "issue";
    private static final String ISSUE_CREATE = "issueCreate";
    private static final String SPRINT = "sprint";
    private static final String PI = "pi";
    private static final String VERSION_CREATE = "versionCreate";
    private static final String COMPONENT_CREATE = "componentCreate";
    private static final String COMPONENT_DELETE = "componentDelete";
    private static final String LABEL_DELETE = "labelDelete";
    private static final String LABEL_CREATE = "labelCreate";
    private static final String VERSION_DELETE = "versionDelete";
    private static final String BATCH_DELETE_VERSION = "batchDeleteVersion";
    private static final String BATCH_DELETE_BY_VERSIONID = "batchDeleteByVersionId";
    private static final String BATCH_UPDATE_ISSUE_EPIC_ID = "batchUpdateIssueEpicId";
    private static final String BATCH_VERSION_DELETE_BY_IN_COMPLETE_ISSUE = "batchVersionDeleteByIncompleteIssue";
    private static final String BATCH_DELETE_VERSION_BY_VERSION = "batchDeleteVersionByVersion";
    private static final String BATCH_VERSION_DELETE_BY_VERSION_IDS = "batchVersionDeleteByVersionIds";
    private static final String BATCH_COMPONENT_DELETE = "batchComponentDelete";
    private static final String BATCH_TO_VERSION = "batchToVersion";
    private static final String BATCH_REMOVE_VERSION = "batchRemoveVersion";
    private static final String BATCH_REMOVE_SPRINT_TO_TARGET = "batchRemoveSprintToTarget";
    private static final String BATCH_REMOVE_PI_TO_TARGET = "batchRemovePiToTarget";
    private static final String BATCH_TO_EPIC = "batchToEpic";
    private static final String BATCH_STORY_TO_FEATURE = "batchStoryToFeature";
    private static final String UPDATE_EPIC_OF_STORY_BY_FEATURE = "updateEpicIdOfStoryByFeature";
    private static final String UPDATE_EPIC_OF_STORY_BY_FEATURE_LIST = "updateEpicIdOfStoryByFeatureList";
    private static final String BATCH_REMOVE_SPRINT = "batchRemoveSprint";
    private static final String BATCH_REMOVE_PI = "batchRemovePi";
    private static final String BATCH_REMOVE_SPRINT_BY_SPRINT_ID = "batchRemoveSprintBySprintId";
    private static final String BATCH_DELETE_LABEL = "batchDeleteLabel";
    private static final String BATCH_UPDATE_ISSUE_STATUS = "batchUpdateIssueStatus";
    private static final String BATCH_UPDATE_ISSUE_STATUS_TO_OTHER = "batchUpdateIssueStatusToOther";
    private static final String BATCH_UPDATE_ISSUE_PRIORITY = "batchUpdateIssuePriority";
    private static final String CREATE_ATTACHMENT = "createAttachment";
    private static final String DELETE_ATTACHMENT = "deleteAttachment";
    private static final String CREATE_COMMENT = "createComment";
    private static final String UPDATE_COMMENT = "updateComment";
    private static final String DELETE_COMMENT = "deleteComment";
    private static final String CREATE_WORKLOG = "createWorkLog";
    private static final String DELETE_WORKLOG = "deleteWorkLog";
    private static final String EPIC_NAME_FIELD = "epicName";
    private static final String FIELD_EPIC_NAME = "Epic Name";
    private static final String SUMMARY_FIELD = "summary";
    private static final String DESCRIPTION = "description";
    private static final String FIELD_DESCRIPTION_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_PRIORITY = "priority";
    private static final String PRIORITY_CODE_FIELD = "priorityId";
    private static final String FIELD_ASSIGNEE = "assignee";
    private static final String ASSIGNEE_ID_FIELD = "assigneeId";
    private static final String REPORTER_ID_FIELD = "reporterId";
    private static final String FIELD_REPORTER = "reporter";
    private static final String FIELD_SPRINT = "Sprint";
    private static final String FIELD_PI = "Pi";
    private static final String STORY_POINTS_FIELD = "storyPoints";
    private static final String EPIC_ID_FIELD = "epicId";
    private static final String FIELD_STORY_POINTS = "Story Points";
    private static final String ERROR_PROJECT_INFO_NOT_FOUND = "error.createIssue.projectInfoNotFound";
    private static final String ERROR_EPIC_NOT_FOUND = "error.dataLogEpic.epicNotFound";
    private static final String ERROR_METHOD_EXECUTE = "error.dataLogEpic.methodExecute";
    private static final String FIELD_EPIC_LINK = "Epic Link";
    private static final String FIELD_EPIC_CHILD = "Epic Child";
    private static final String REMAIN_TIME_FIELD = "remainingTime";
    private static final String FIELD_TIMEESTIMATE = "timeestimate";
    private static final String STATUS_ID = "statusId";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_RESOLUTION = "resolution";
    private static final String RANK_FIELD = "rank";
    private static final String FIELD_RANK = "Rank";
    private static final String RANK_HIGHER = "评级更高";
    private static final String RANK_LOWER = "评级更低";
    private static final String TYPE_CODE = "typeCode";
    private static final String FIELD_ISSUETYPE = "issuetype";
    private static final String FIELD_FIX_VERSION = "Fix Version";
    private static final String BATCH_MOVE_TO_VERSION = "batchMoveVersion";
    private static final String FIX_VERSION = "fix";
    private static final String FIELD_VERSION = "Version";
    private static final String FIELD_COMPONENT = "Component";
    private static final String FIELD_LABELS = "labels";
    private static final String FIELD_ATTACHMENT = "Attachment";
    private static final String FIELD_COMMENT = "Comment";
    private static final String FIELD_TIMESPENT = "timespent";
    private static final String FIELD_WORKLOGID = "WorklogId";
    private static final String ERROR_UPDATE = "error.LogDataAspect.update";
    private static final String AGILE = "Agile:";
    private static final String VERSION_CHART = AGILE + "VersionChart";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String COMPONENT = "component";
    private static final String BURN_DOWN_COORDINATE_BY_TYPE = AGILE + "BurnDownCoordinateByType";
    private static final String VERSION = "Version";
    private static final String EPIC = "Epic";
    private static final String BATCH_UPDATE_STATUS_ID = "batchUpdateStatusId";

    @Autowired
    private IssueStatusMapper issueStatusMapper;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private ProductVersionMapper productVersionMapper;
    @Autowired
    private IssueComponentMapper issueComponentMapper;
    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;
    @Autowired
    private IssueAttachmentMapper issueAttachmentMapper;
    @Autowired
    private DataLogMapper dataLogMapper;
    @Autowired
    private IssueCommentMapper issueCommentMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;
    @Autowired
    private WorkLogMapper workLogMapper;
    @Autowired
    private PiMapper piMapper;

    /**
     * 定义拦截规则：拦截Spring管理的后缀为RepositoryImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut("bean(*RepositoryImpl) && @annotation(io.choerodon.agile.infra.common.annotation.DataLog)")
    public void updateMethodPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("updateMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Object result = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();
        DataLog dataLog = method.getAnnotation(DataLog.class);
        //获取被拦截的方法名
        Object[] args = pjp.getArgs();
        if (dataLog != null && args != null) {
            if (dataLog.single()) {
                switch (dataLog.type()) {
                    case ISSUE:
                        handleIssueDataLog(args);
                        break;
                    case ISSUE_CREATE:
                        result = handleIssueCreateDataLog(pjp);
                        break;
                    case SPRINT:
                        handleSprintDataLog(args);
                        break;
                    case PI:
                        handlePiDataLog(args);
                        break;
                    case VERSION_CREATE:
                        handleVersionCreateDataLog(args);
                        break;
                    case VERSION_DELETE:
                        handleVersionDeleteDataLog(args);
                        break;
                    case COMPONENT_CREATE:
                        handleComponentCreateDataLog(args);
                        break;
                    case COMPONENT_DELETE:
                        handleComponentDeleteDataLog(args);
                        break;
                    case LABEL_DELETE:
                        result = handleLabelDeleteDataLog(args, pjp);
                        break;
                    case LABEL_CREATE:
                        result = handleLabelCreateDataLog(args, pjp);
                        break;
                    case CREATE_ATTACHMENT:
                        result = handleCreateAttachmentDataLog(args, pjp);
                        break;
                    case DELETE_ATTACHMENT:
                        handleDeleteAttachmentDataLog(args);
                        break;
                    case CREATE_COMMENT:
                        result = handleCreateCommentDataLog(args, pjp);
                        break;
                    case UPDATE_COMMENT:
                        handleUpdateCommentDataLog(args);
                        break;
                    case DELETE_COMMENT:
                        handleDeleteCommentDataLog(args);
                        break;
                    case CREATE_WORKLOG:
                        result = handleCreateWorkLogDataLog(args, pjp);
                        break;
                    case DELETE_WORKLOG:
                        result = handleDeleteWorkLogDataLog(args);
                        break;
                    default:
                        break;
                }
            } else {
                switch (dataLog.type()) {
                    case BATCH_TO_VERSION:
                        batchToVersionDataLog(args);
                        break;
                    case BATCH_REMOVE_VERSION:
                        batchRemoveVersionDataLog(args);
                        break;
                    case BATCH_TO_EPIC:
                        batchToEpicDataLog(args);
                        break;
                    case BATCH_STORY_TO_FEATURE:
                        batchStoryToFeatureDataLog(args);
                        break;
                    case UPDATE_EPIC_OF_STORY_BY_FEATURE:
                        updateEpicIdOfStoryByFeature(args);
                        break;
                    case UPDATE_EPIC_OF_STORY_BY_FEATURE_LIST:
                        updateEpicIdOfStoryByFeatureList(args);
                        break;
                    case BATCH_COMPONENT_DELETE:
                        batchComponentDeleteDataLog(args);
                        break;
                    case BATCH_REMOVE_SPRINT:
                        batchRemoveSprintDataLog(args);
                        break;
                    case BATCH_REMOVE_PI:
                        batchRemovePiDataLog(args);
                        break;
                    case BATCH_REMOVE_SPRINT_TO_TARGET:
                        batchRemoveSprintToTarget(args);
                        break;
                    case BATCH_REMOVE_PI_TO_TARGET:
                        batchRemovePiToTarget(args);
                        break;
                    case BATCH_DELETE_LABEL:
                        batchDeleteLabelDataLog(args);
                        break;
                    case BATCH_DELETE_VERSION:
                        batchDeleteVersionDataLog(args);
                        break;
                    case BATCH_DELETE_VERSION_BY_VERSION:
                        batchDeleteVersionByVersion(args);
                        break;
                    case BATCH_MOVE_TO_VERSION:
                        batchMoveVersionDataLog(args);
                        break;
                    case BATCH_REMOVE_SPRINT_BY_SPRINT_ID:
                        batchRemoveSprintBySprintId(args);
                        break;
                    case BATCH_UPDATE_ISSUE_STATUS:
                        batchUpdateIssueStatusDataLog(args);
                        break;
                    case BATCH_UPDATE_ISSUE_STATUS_TO_OTHER:
                        batchUpdateIssueStatusToOtherDataLog(args);
                        break;
                    case BATCH_VERSION_DELETE_BY_VERSION_IDS:
                        batchDeleteVersionByVersionIds(args);
                        break;
                    case BATCH_VERSION_DELETE_BY_IN_COMPLETE_ISSUE:
                        batchVersionDeleteByInCompleteIssue(args);
                        break;
                    case BATCH_DELETE_BY_VERSIONID:
                        batchDeleteByVersionId(args);
                        break;
                    case BATCH_UPDATE_ISSUE_EPIC_ID:
                        batchUpdateIssueEpicId(args);
                        break;
                    case BATCH_UPDATE_ISSUE_PRIORITY:
                        batchUpdateIssuePriority(args);
                        break;
                    case BATCH_UPDATE_STATUS_ID:
                        batchUpdateIssueStatusId(args);
                        break;
                    default:
                        break;
                }
            }
        } else {
            throw new CommonException(ERROR_UPDATE);
        }
        try {
            // 一切正常的情况下，继续执行被拦截的方法
            if (result == null) {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            throw new CommonException(ERROR_METHOD_EXECUTE, e);
        }
        return result;
    }

    private Object handleDeleteWorkLogDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        Long logId = (Long) args[1];
        if (logId != null && projectId != null) {
            WorkLogDO query = new WorkLogDO();
            query.setProjectId(projectId);
            query.setLogId(logId);
            WorkLogDO workLogDO = workLogMapper.selectOne(query);
            if (workLogDO != null) {
                createDataLog(workLogDO.getProjectId(), workLogDO.getIssueId(), FIELD_WORKLOGID,
                        workLogDO.getLogId().toString(), null, workLogDO.getLogId().toString(), null);
            }
        }
        return null;
    }

    private void batchUpdateIssueStatusId(Object[] args) {
        Long programId = (Long) args[0];
        Long updateStatusId = (Long) args[1];
        List<IssueDTO> issueDTOList = (List<IssueDTO>) args[2];
        if (programId != null && updateStatusId != null && issueDTOList != null && !issueDTOList.isEmpty()) {
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(ConvertUtil.getOrganizationId(programId)).getBody();
            StatusMapVO newStatus = statusMapDTOMap.get(updateStatusId);
            for (IssueDTO issueDTO : issueDTOList) {
                StatusMapVO oldStatus = statusMapDTOMap.get(issueDTO.getStatusId());
                createDataLog(programId, issueDTO.getIssueId(), FIELD_STATUS, oldStatus.getName(), newStatus.getName(), oldStatus.getId().toString(), newStatus.getId().toString());
            }
        }
    }

    private void batchUpdateIssueStatusToOtherDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        String applyType = (String) args[1];
        Long issueTypeId = (Long) args[2];
        Long oldStatusId = (Long) args[3];
        Long newStatusId = (Long) args[4];
        Long userId = (Long) args[5];
        if (projectId != null && Objects.nonNull(applyType) && issueTypeId != null && oldStatusId != null && newStatusId != null && !oldStatusId.equals(newStatusId)) {
            StatusMapVO oldStatus = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(projectId), oldStatusId).getBody();
            StatusMapVO newStatus = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(projectId), newStatusId).getBody();
            IssueStatusDTO oldStatusDO = issueStatusMapper.selectByStatusId(projectId, oldStatusId);
            IssueStatusDTO newStatusDO = issueStatusMapper.selectByStatusId(projectId, newStatusId);
            List<IssueDTO> issueDTOS = issueMapper.queryIssueWithCompleteInfoByStatusId(projectId, applyType, issueTypeId, oldStatusId);
            if (issueDTOS != null && !issueDTOS.isEmpty()) {
                dataLogMapper.batchCreateChangeStatusLogByIssueDOS(projectId, issueDTOS, userId, oldStatus, newStatus);
                if (!oldStatusDO.getCompleted().equals(newStatusDO.getCompleted())) {
                    dataLogMapper.batchCreateStatusLogByIssueDOS(projectId, issueDTOS, userId, newStatus, newStatusDO.getCompleted());
                }
                dataLogRedisUtil.handleBatchDeleteRedisCacheByChangeStatusId(issueDTOS, projectId);
            }
        }
    }

    private void batchUpdateIssuePriority(Object[] args) {
        Long organizationId = (Long) args[0];
        Long priorityId = (Long) args[1];
        Long changePriorityId = (Long) args[2];
        Long userId = (Long) args[3];
        List<Long> projectIds = (List) args[4];
        if (priorityId != null && Objects.nonNull(changePriorityId) && projectIds != null && !projectIds.isEmpty()) {
            List<PriorityVO> priorityVOList = issueFeignClient.queryByOrganizationIdList(organizationId).getBody();
            Map<Long, PriorityVO> priorityMap = priorityVOList.stream().collect(Collectors.toMap(PriorityVO::getId, Function.identity()));
            List<IssueDTO> issueDTOS = issueMapper.queryIssuesByPriorityId(priorityId, projectIds);
            if (issueDTOS != null && !issueDTOS.isEmpty()) {
                dataLogMapper.batchCreateChangePriorityLogByIssueDOs(issueDTOS, userId, priorityMap.get(priorityId).getName(), priorityMap.get(changePriorityId).getName());
            }
        }
    }

    private void batchUpdateIssueEpicId(Object[] args) {
        Long projectId = (Long) args[0];
        Long issueId = (Long) args[1];
        if (projectId != null && issueId != null) {
            IssueDTO query = new IssueDTO();
            query.setProjectId(projectId);
            query.setEpicId(issueId);
            List<IssueDTO> issueDTOList = issueMapper.select(query);
            issueDTOList.forEach(issueDO -> createIssueEpicLog(0L, issueDO));
        }
    }

    @SuppressWarnings("unchecked")
    private void batchRemoveSprintToTarget(Object[] args) {
        Long projectId = (Long) args[0];
        Long sprintId = (Long) args[1];
        List<Long> issueIds = (List<Long>) args[2];
        if (projectId != null && sprintId != null && issueIds != null && !issueIds.isEmpty()) {
            SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
            SprintNameDO sprintNameDO = new SprintNameDO();
            sprintNameDO.setSprintId(sprintId);
            sprintNameDO.setSprintName(sprintDTO.getSprintName());
            for (Long issueId : issueIds) {
                StringBuilder newSprintIdStr = new StringBuilder();
                StringBuilder newSprintNameStr = new StringBuilder();
                List<SprintNameDO> sprintNames = issueMapper.querySprintNameByIssueId(issueId);
                handleBatchCreateDataLogForSpring(sprintNames, sprintNameDO, newSprintNameStr, newSprintIdStr, sprintDTO, projectId, issueId);
            }
            dataLogRedisUtil.deleteByBatchRemoveSprintToTarget(sprintId, projectId, null);
        }
    }

    private void batchRemovePiToTarget(Object[] args) {
        Long programId = (Long) args[0];
        Long piId = (Long) args[1];
        List<Long> issueIds = (List<Long>) args[2];
        if (programId != null && piId != null && issueIds != null && !issueIds.isEmpty()) {
            for (Long issueId : issueIds) {
                List<PiNameDTO> piNameDTOList = piMapper.selectclosePiListByIssueId(programId, issueId);
                PiNameDTO currentPiNameDTO = piMapper.selectCurrentPiListByIssueId(programId, issueId);
                PiDTO targetPi = piMapper.selectByPrimaryKey(piId);
                String oldString = "";
                String oldvalue = "";
                String newString = "";
                String newValue = "";
                if (piNameDTOList != null && !piNameDTOList.isEmpty()) {
                    oldString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                    oldvalue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
                    newString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                    newValue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
                }
                if (currentPiNameDTO != null) {
                    oldString = oldString + ("".equals(oldString) ? currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName() : "," + currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName());
                    oldvalue = oldvalue + ("".equals(oldvalue) ? currentPiNameDTO.getId().toString() : "," + currentPiNameDTO.getId().toString());
                    newString = newString + ("".equals(newString) ? currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName() : "," + currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName());
                    newValue = newValue + ("".equals(newValue) ? currentPiNameDTO.getId().toString() : "," + currentPiNameDTO.getId().toString());
                }
                if (targetPi != null) {
                    newString = newString + ("".equals(newString) ? targetPi.getCode() + "-" + targetPi.getName() : "," + targetPi.getCode() + "-" + targetPi.getName());
                    newValue = newValue + ("".equals(newValue) ? targetPi.getId() : "," + targetPi.getId().toString());
                }
                createDataLog(programId, issueId, FIELD_PI,
                        "".equals(oldString) ? null : oldString,
                        "".equals(newString) ? null : newString,
                        "".equals(oldvalue) ? null : oldvalue,
                        "".equals(newValue) ? null : newValue);
            }

        }
    }

    private void batchDeleteByVersionId(Object[] args) {
        Long projectId = (Long) args[0];
        Long versionId = (Long) args[1];
        if (projectId != null && versionId != null) {
            List<VersionIssueDO> versionIssueRelDOS = productVersionMapper.queryVersionIssueByVersionId(projectId, versionId);
            handleBatchDeleteVersion(versionIssueRelDOS, projectId, versionId);
        }
    }

    private void batchVersionDeleteByInCompleteIssue(Object[] args) {
        Long projectId = (Long) args[0];
        Long versionId = (Long) args[1];
        if (projectId != null && versionId != null) {
            List<VersionIssueDO> versionIssues = productVersionMapper.queryInCompleteIssueByVersionId(projectId, versionId);
            handleBatchDeleteVersion(versionIssues, projectId, versionId);
        }
    }

    private void batchDeleteVersionByVersion(Object[] args) {
        ProductVersionE productVersionE = null;
        for (Object arg : args) {
            if (arg instanceof ProductVersionE) {
                productVersionE = (ProductVersionE) arg;
            }
        }
        if (productVersionE != null) {
            List<VersionIssueDO> versionIssues = productVersionMapper.queryIssueForLogByVersionIds(productVersionE.getProjectId(), Collections.singletonList(productVersionE.getVersionId()));
            handleBatchDeleteVersion(versionIssues, productVersionE.getProjectId(), productVersionE.getVersionId());
        }
    }

    private void handleBatchDeleteVersion(List<VersionIssueDO> versionIssues, Long projectId, Long versionId) {
        if (versionIssues != null && !versionIssues.isEmpty()) {
            versionIssues.forEach(versionIssueDO -> {
                String field = FIX_VERSION.equals(versionIssueDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                createDataLog(projectId, versionIssueDO.getIssueId(), field,
                        versionIssueDO.getName(), null, versionIssueDO.getVersionId().toString(), null);
            });
            dataLogRedisUtil.deleteByHandleBatchDeleteVersion(projectId, versionId);
        }
    }

    @SuppressWarnings("unchecked")
    private void batchDeleteVersionByVersionIds(Object[] args) {
        List<Long> versionIds = new ArrayList<>();
        Long projectId = null;
        for (Object arg : args) {
            if (arg instanceof List) {
                versionIds = (List) arg;
            }
            if (arg instanceof Long) {
                projectId = (Long) arg;
            }
        }
        if (projectId != null && !versionIds.isEmpty()) {
            List<VersionIssueDO> versionIssues = productVersionMapper.queryIssueForLogByVersionIds(projectId, versionIds);
            handleBatchDeleteVersion(versionIssues, projectId, null);
            dataLogRedisUtil.deleteByBatchDeleteVersionByVersionIds(projectId, versionIds);
        }
    }

    private void batchUpdateIssueStatusDataLog(Object[] args) {
        IssueStatusE issueStatusE = null;
        for (Object arg : args) {
            if (arg instanceof IssueStatusE) {
                issueStatusE = (IssueStatusE) arg;
            }
        }
        if (issueStatusE != null && issueStatusE.getCompleted() != null) {
            Long projectId = issueStatusE.getProjectId();
            IssueDTO query = new IssueDTO();
            query.setStatusId(issueStatusE.getStatusId());
            query.setProjectId(projectId);
            StatusMapVO statusMapVO = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(projectId), issueStatusE.getStatusId()).getBody();
            List<IssueDTO> issueDTOS = issueMapper.select(query);
            if (issueDTOS != null && !issueDTOS.isEmpty()) {
                Long userId = DetailsHelper.getUserDetails().getUserId();
                dataLogMapper.batchCreateStatusLogByIssueDOS(projectId, issueDTOS, userId, statusMapVO, issueStatusE.getCompleted());
                dataLogRedisUtil.handleBatchDeleteRedisCache(issueDTOS, projectId);
            }

        }
    }

    private void handleUpdateCommentDataLog(Object[] args) {
        IssueCommentE issueCommentE = null;
        for (Object arg : args) {
            if (arg instanceof IssueCommentE) {
                issueCommentE = (IssueCommentE) arg;
            }
        }
        if (issueCommentE != null) {
            IssueCommentDTO issueCommentDTO = issueCommentMapper.selectByPrimaryKey(issueCommentE.getCommentId());
            createDataLog(issueCommentDTO.getProjectId(), issueCommentDTO.getIssueId(), FIELD_COMMENT,
                    issueCommentDTO.getCommentText(), issueCommentE.getCommentText(), issueCommentE.getCommentId().toString(),
                    issueCommentE.getCommentId().toString());

        }
    }

    private Object handleCreateWorkLogDataLog(Object[] args, ProceedingJoinPoint pjp) {
        WorkLogE workLogE = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof WorkLogE) {
                workLogE = (WorkLogE) arg;
            }
        }
        if (workLogE != null) {
            try {
                result = pjp.proceed();
                workLogE = (WorkLogE) result;
                DataLogDTO dataLogDTO = dataLogMapper.selectLastWorkLogById(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_TIMESPENT);
                String oldString = null;
                String newString;
                String oldValue = null;
                String newValue;
                if (dataLogDTO != null) {
                    oldValue = dataLogDTO.getNewValue();
                    oldString = dataLogDTO.getNewString();
                    BigDecimal newTime = new BigDecimal(dataLogDTO.getNewValue());
                    newValue = newTime.add(workLogE.getWorkTime()).toString();
                    newString = newTime.add(workLogE.getWorkTime()).toString();
                } else {
                    newValue = workLogE.getWorkTime().toString();
                    newString = workLogE.getWorkTime().toString();
                }
                createDataLog(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_TIMESPENT,
                        oldString, newString, oldValue, newValue);
                createDataLog(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_WORKLOGID,
                        null, workLogE.getLogId().toString(), null, workLogE.getLogId().toString());
            } catch (Throwable e) {
                throw new CommonException(ERROR_METHOD_EXECUTE, e);
            }
        }
        return result;
    }

    private void batchRemoveSprintBySprintId(Object[] args) {
        Long projectId = (Long) args[0];
        Long sprintId = (Long) args[1];
        if (projectId != null && sprintId != null) {
            List<Long> moveIssueIds = sprintMapper.queryIssueIds(projectId, sprintId);
            handleBatchRemoveSprint(projectId, moveIssueIds, sprintId);
        }
    }

    @SuppressWarnings("unchecked")
    private void batchMoveVersionDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        Long targetVersionId = (Long) args[1];
        List<VersionIssueDO> versionIssueDOS = (List<VersionIssueDO>) args[2];
        if (projectId != null && targetVersionId != null && !versionIssueDOS.isEmpty()) {
            ProductVersionDO productVersionDO = productVersionMapper.selectByPrimaryKey(targetVersionId);
            if (productVersionDO == null) {
                throw new CommonException("error.productVersion.get");
            }
            for (VersionIssueDO versionIssueDO : versionIssueDOS) {
                String field = FIX_VERSION.equals(versionIssueDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                createDataLog(projectId, versionIssueDO.getIssueId(), field, null,
                        productVersionDO.getName(), null, targetVersionId.toString());
            }
            dataLogRedisUtil.deleteByHandleBatchDeleteVersion(projectId, productVersionDO.getVersionId());
        }
    }

    private void handleDeleteCommentDataLog(Object[] args) {
        IssueCommentDTO issueCommentDTO = null;
        for (Object arg : args) {
            if (arg instanceof IssueCommentDTO) {
                issueCommentDTO = (IssueCommentDTO) arg;
            }
        }
        if (issueCommentDTO != null) {
            createDataLog(issueCommentDTO.getProjectId(), issueCommentDTO.getIssueId(), FIELD_COMMENT,
                    issueCommentDTO.getCommentText(), null, issueCommentDTO.getCommentId().toString(), null);
        }
    }

    private Object handleCreateCommentDataLog(Object[] args, ProceedingJoinPoint pjp) {
        IssueCommentE issueCommentE = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof IssueCommentE) {
                issueCommentE = (IssueCommentE) arg;
            }
        }
        if (issueCommentE != null) {
            try {
                result = pjp.proceed();
                issueCommentE = (IssueCommentE) result;
                createDataLog(issueCommentE.getProjectId(), issueCommentE.getIssueId(), FIELD_COMMENT,
                        null, issueCommentE.getCommentText(), null, issueCommentE.getCommentId().toString());
            } catch (Throwable e) {
                throw new CommonException(ERROR_METHOD_EXECUTE, e);
            }
        }
        return result;
    }

    private void handleDeleteAttachmentDataLog(Object[] args) {
        Long attachmentId = null;
        for (Object arg : args) {
            if (arg instanceof Long) {
                attachmentId = (Long) arg;
            }
        }
        if (attachmentId != null) {
            IssueAttachmentDTO issueAttachmentDTO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
            createDataLog(issueAttachmentDTO.getProjectId(), issueAttachmentDTO.getIssueId(), FIELD_ATTACHMENT,
                    issueAttachmentDTO.getUrl(), null, issueAttachmentDTO.getAttachmentId().toString(), null);
        }
    }

    private Object handleCreateAttachmentDataLog(Object[] args, ProceedingJoinPoint pjp) {
        IssueAttachmentDTO issueAttachmentDTO = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof IssueAttachmentE) {
                issueAttachmentDTO = (IssueAttachmentDTO) arg;
            }
        }
        if (issueAttachmentDTO != null) {
            try {
                result = pjp.proceed();
                issueAttachmentDTO = (IssueAttachmentDTO) result;
                createDataLog(issueAttachmentDTO.getProjectId(), issueAttachmentDTO.getIssueId(), FIELD_ATTACHMENT,
                        null, issueAttachmentDTO.getUrl(), null, issueAttachmentDTO.getAttachmentId().toString());
            } catch (Throwable throwable) {
                throw new CommonException(ERROR_METHOD_EXECUTE, throwable);
            }
        }
        return result;
    }

    private void batchDeleteVersionDataLog(Object[] args) {
        VersionIssueRelE versionIssueRelE = null;
        for (Object arg : args) {
            if (arg instanceof VersionIssueRelE) {
                versionIssueRelE = (VersionIssueRelE) arg;
            }
        }
        if (versionIssueRelE != null) {
            List<ProductVersionDO> productVersionDOS = productVersionMapper.queryVersionRelByIssueIdAndTypeArchivedExceptInfluence(
                    versionIssueRelE.getProjectId(), versionIssueRelE.getIssueId(), versionIssueRelE.getRelationType());
            Long issueId = versionIssueRelE.getIssueId();
            String field = FIX_VERSION.equals(versionIssueRelE.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
            productVersionDOS.forEach(productVersionDO -> createDataLog(productVersionDO.getProjectId(), issueId, field,
                    productVersionDO.getName(), null, productVersionDO.getVersionId().toString(), null));
            dataLogRedisUtil.deleteByBatchDeleteVersionDataLog(versionIssueRelE.getProjectId(), productVersionDOS);
        }
    }

    private void handleVersionDeleteDataLog(Object[] args) {
        VersionIssueRelDO versionIssueRelDO = null;
        for (Object arg : args) {
            if (arg instanceof VersionIssueRelDO) {
                versionIssueRelDO = (VersionIssueRelDO) arg;
            }
        }
        if (versionIssueRelDO != null) {
            String field;
            if (versionIssueRelDO.getRelationType() == null) {
                field = FIELD_FIX_VERSION;
            } else {
                field = FIX_VERSION.equals(versionIssueRelDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
            }
            createDataLog(versionIssueRelDO.getProjectId(), versionIssueRelDO.getIssueId(), field,
                    productVersionMapper.selectByPrimaryKey(versionIssueRelDO.getVersionId()).getName(), null,
                    versionIssueRelDO.getVersionId().toString(), null);
            dataLogRedisUtil.deleteByHandleBatchDeleteVersion(versionIssueRelDO.getProjectId(), versionIssueRelDO.getVersionId());
        }
    }

    private Object handleLabelCreateDataLog(Object[] args, ProceedingJoinPoint pjp) {
        LabelIssueRelE labelIssueRelE = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof LabelIssueRelE) {
                labelIssueRelE = (LabelIssueRelE) arg;
            }
        }
        if (labelIssueRelE != null) {
            result = createLabelDataLog(labelIssueRelE.getIssueId(), labelIssueRelE.getProjectId(), pjp);
        }
        return result;
    }

    private Object createLabelDataLog(Long issueId, Long projectId, ProceedingJoinPoint pjp) {
        List<IssueLabelDTO> originLabels = issueMapper.selectLabelNameByIssueId(issueId);
        Object result = null;
        try {
            result = pjp.proceed();
            List<IssueLabelDTO> curLabels = issueMapper.selectLabelNameByIssueId(issueId);
            createDataLog(projectId, issueId, FIELD_LABELS, getOriginLabelNames(originLabels),
                    getOriginLabelNames(curLabels), null, null);
        } catch (Throwable e) {
            throw new CommonException(ERROR_METHOD_EXECUTE, e);
        }
        return result;
    }


    private Object handleLabelDeleteDataLog(Object[] args, ProceedingJoinPoint pjp) {
        LabelIssueRelDO labelIssueRelDO = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof LabelIssueRelDO) {
                labelIssueRelDO = (LabelIssueRelDO) arg;
            }
        }
        if (labelIssueRelDO != null) {
            result = createLabelDataLog(labelIssueRelDO.getIssueId(), labelIssueRelDO.getProjectId(), pjp);
        }
        return result;
    }

    private void batchDeleteLabelDataLog(Object[] args) {
        Long issueId = null;
        for (Object arg : args) {
            if (arg instanceof Long) {
                issueId = (Long) arg;
            }
        }
        if (issueId != null) {
            IssueDTO issueDTO = issueMapper.selectByPrimaryKey(issueId);
            List<IssueLabelDTO> originLabels = issueMapper.selectLabelNameByIssueId(issueId);
            createDataLog(issueDTO.getProjectId(), issueId, FIELD_LABELS, getOriginLabelNames(originLabels),
                    null, null, null);
        }
    }

    private String getOriginLabelNames(List<IssueLabelDTO> originLabels) {
        StringBuilder originLabelNames = new StringBuilder();
        int originIdx = 0;
        for (IssueLabelDTO label : originLabels) {
            if (originIdx == originLabels.size() - 1) {
                originLabelNames.append(label.getLabelName());
            } else {
                originLabelNames.append(label.getLabelName()).append(" ");
            }
        }
        return originLabelNames.length() == 0 ? null : originLabelNames.toString();
    }

    private void batchRemoveSprintDataLog(Object[] args) {
        BatchRemoveSprintE batchRemoveSprintE = null;
        for (Object arg : args) {
            if (arg instanceof BatchRemoveSprintE) {
                batchRemoveSprintE = (BatchRemoveSprintE) arg;
            }
        }
        if (batchRemoveSprintE != null) {
            handleBatchRemoveSprint(batchRemoveSprintE.getProjectId(), batchRemoveSprintE.getIssueIds(), batchRemoveSprintE.getSprintId());
        }
    }

    private void batchRemovePiDataLog(Object[] args) {
        BatchRemovePiE batchRemovePiE = null;
        for (Object arg : args) {
            if (arg instanceof BatchRemovePiE) {
                batchRemovePiE = (BatchRemovePiE) arg;
            }
        }
        if (batchRemovePiE != null) {
            Long programId = batchRemovePiE.getProgramId();
            Long piId = batchRemovePiE.getPiId();
            for (Long issueId : batchRemovePiE.getIssueIds()) {
                List<PiNameDTO> piNameDTOList = piMapper.selectclosePiListByIssueId(programId, issueId);
                PiNameDTO currentPiNameDTO = piMapper.selectCurrentPiListByIssueId(programId, issueId);
                PiDTO targetPi = piMapper.selectByPrimaryKey(piId);
                String oldString = "";
                String oldvalue = "";
                String newString = "";
                String newValue = "";
                if (piNameDTOList != null && !piNameDTOList.isEmpty()) {
                    oldString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                    oldvalue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
                    newString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                    newValue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
                }
                if (currentPiNameDTO != null) {
                    oldString = oldString + ("".equals(oldString) ? currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName() : "," + currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName());
                    oldvalue = oldvalue + ("".equals(oldvalue) ? currentPiNameDTO.getId().toString() : "," + currentPiNameDTO.getId().toString());
                }
                if (targetPi != null) {
                    newString = newString + ("".equals(newString) ? targetPi.getCode() + "-" + targetPi.getName() : "," + targetPi.getCode() + "-" + targetPi.getName());
                    newValue = newValue + ("".equals(newValue) ? targetPi.getId() : "," + targetPi.getId().toString());
                }
                createDataLog(programId, issueId, FIELD_PI,
                        "".equals(oldString) ? null : oldString,
                        "".equals(newString) ? null : newString,
                        "".equals(oldvalue) ? null : oldvalue,
                        "".equals(newValue) ? null : newValue);
            }
        }
    }

    private void handleBatchRemoveSprint(Long projectId, List<Long> issueIds, Long sprintId) {
        SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
        for (Long issueId : issueIds) {
            SprintNameDO activeSprintName = issueMapper.queryActiveSprintNameByIssueId(issueId);
            Long originSprintId = null;
            if (activeSprintName != null) {
                if (sprintId != null && sprintId.equals(activeSprintName.getSprintId())) {
                    continue;
                }
                originSprintId = activeSprintName.getSprintId();
            }
            dataLogRedisUtil.deleteByBatchRemoveSprintToTarget(sprintId, projectId, originSprintId);
            StringBuilder newSprintIdStr = new StringBuilder();
            StringBuilder newSprintNameStr = new StringBuilder();
            List<SprintNameDO> sprintNames = issueMapper.querySprintNameByIssueId(issueId);
            handleBatchCreateDataLogForSpring(sprintNames, activeSprintName, newSprintNameStr, newSprintIdStr, sprintDTO, projectId, issueId);
        }
    }

    private void handleBatchCreateDataLogForSpring(List<SprintNameDO> sprintNames, SprintNameDO activeSprintName,
                                                   StringBuilder newSprintNameStr, StringBuilder newSprintIdStr,
                                                   SprintDTO sprintDTO, Long projectId, Long issueId) {
        String oldSprintIdStr = sprintNames.stream().map(sprintName -> sprintName.getSprintId().toString()).collect(Collectors.joining(","));
        String oldSprintNameStr = sprintNames.stream().map(SprintNameDO::getSprintName).collect(Collectors.joining(","));
        handleSprintStringBuilder(sprintNames, activeSprintName, newSprintNameStr, newSprintIdStr, sprintDTO);
        String oldString = "".equals(oldSprintNameStr) ? null : oldSprintNameStr;
        String newString = newSprintNameStr.length() == 0 ? null : newSprintNameStr.toString();
        String oldValue = "".equals(oldSprintIdStr) ? null : oldSprintIdStr;
        String newValue = newSprintIdStr.length() == 0 ? null : newSprintIdStr.toString();
        if (!Objects.equals(oldValue, newValue)) {
            createDataLog(projectId, issueId, FIELD_SPRINT, oldString,
                    newString, oldValue, newValue);
        }
    }


    private void handleSprintStringBuilder(List<SprintNameDO> sprintNames, SprintNameDO activeSprintName,
                                           StringBuilder newSprintNameStr, StringBuilder newSprintIdStr, SprintDTO sprintDTO) {
        int idx = 0;
        for (SprintNameDO sprintName : sprintNames) {
            if (activeSprintName != null && activeSprintName.getSprintId().equals(sprintName.getSprintId())) {
                continue;
            }
            if (idx == 0) {
                newSprintNameStr.append(sprintName.getSprintName());
                newSprintIdStr.append(sprintName.getSprintId().toString());
                idx++;
            } else {
                newSprintNameStr.append(",").append(sprintName.getSprintName());
                newSprintIdStr.append(",").append(sprintName.getSprintId().toString());
            }
        }
        if (sprintDTO != null) {
            newSprintIdStr.append(newSprintIdStr.length() == 0 ? sprintDTO.getSprintId().toString() : "," + sprintDTO.getSprintId().toString());
            newSprintNameStr.append(newSprintNameStr.length() == 0 ? sprintDTO.getSprintName() : "," + sprintDTO.getSprintName());
        }
    }


    private void handleComponentDeleteDataLog(Object[] args) {
        ComponentIssueRelDO componentIssueRelDO = null;
        for (Object arg : args) {
            if (arg instanceof ComponentIssueRelDO) {
                componentIssueRelDO = (ComponentIssueRelDO) arg;
            }
        }
        if (componentIssueRelDO != null) {
            createDataLog(componentIssueRelDO.getProjectId(), componentIssueRelDO.getIssueId(),
                    FIELD_COMPONENT, issueComponentMapper.selectByPrimaryKey(componentIssueRelDO.getComponentId()).getName(), null,
                    componentIssueRelDO.getComponentId().toString(), null);
            redisUtil.deleteRedisCache(new String[]{PIECHART + componentIssueRelDO.getProjectId() + ':' + COMPONENT + "*"});
        }
    }

    private void batchComponentDeleteDataLog(Object[] args) {
        Long issueId = null;
        for (Object arg : args) {
            if (arg instanceof Long) {
                issueId = (Long) arg;
            }
        }
        if (issueId != null) {
            ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
            componentIssueRelDO.setIssueId(issueId);
            List<ComponentIssueRelDO> componentIssueRelDOList = componentIssueRelMapper.select(componentIssueRelDO);
            if (componentIssueRelDOList != null && !componentIssueRelDOList.isEmpty()) {
                componentIssueRelDOList.forEach(componentIssueRel -> createDataLog(componentIssueRel.getProjectId(), componentIssueRel.getIssueId(),
                        FIELD_COMPONENT, issueComponentMapper.selectByPrimaryKey(componentIssueRel.getComponentId()).getName(), null,
                        componentIssueRel.getComponentId().toString(), null));
                redisUtil.deleteRedisCache(new String[]{PIECHART + componentIssueRelDOList.get(0).getProjectId() + ':' + COMPONENT + "*"});
            }
        }
    }

    private void handleComponentCreateDataLog(Object[] args) {
        ComponentIssueRelE componentIssueRelE = null;
        for (Object arg : args) {
            if (arg instanceof ComponentIssueRelE) {
                componentIssueRelE = (ComponentIssueRelE) arg;
            }
        }
        if (componentIssueRelE != null) {
            createDataLog(componentIssueRelE.getProjectId(), componentIssueRelE.getIssueId(), FIELD_COMPONENT,
                    null, issueComponentMapper.selectByPrimaryKey(componentIssueRelE.getComponentId()).getName(),
                    null, componentIssueRelE.getComponentId().toString());
            redisUtil.deleteRedisCache(new String[]{PIECHART + componentIssueRelE.getProjectId() + ':' + COMPONENT + "*"});
        }
    }

    @SuppressWarnings("unchecked")
    private void batchToEpicDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        Long epicId = (Long) args[1];
        List<Long> issueIds = (List<Long>) args[2];
        if (projectId != null && epicId != null && issueIds != null && !issueIds.isEmpty()) {
            List<IssueDTO> issueDTOList = issueMapper.queryIssueEpicInfoByIssueIds(projectId, issueIds);
            issueDTOList.forEach(issueEpic -> createIssueEpicLog(epicId, issueEpic));
            redisUtil.deleteRedisCache(new String[]{
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + EPIC + ":" + epicId
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void batchStoryToFeatureDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        Long featureId = (Long) args[1];
        List<Long> issueIds = (List<Long>) args[2];
        Long epicId = (Long) args[3];
        if (projectId != null && epicId != null && issueIds != null && !issueIds.isEmpty()) {
            List<IssueDTO> issueDTOList = issueMapper.queryIssueEpicInfoByIssueIds(projectId, issueIds);
            issueDTOList.forEach(issueDO -> createIssueEpicLog(epicId, issueDO));
            redisUtil.deleteRedisCache(new String[]{
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + EPIC + ":" + epicId
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void updateEpicIdOfStoryByFeature(Object[] args) {
        Long featureId = (Long) args[0];
        Long epicId = (Long) args[1];
        if (featureId != null && epicId != null) {
            IssueDTO selectDO = new IssueDTO();
            selectDO.setTypeCode("story");
            selectDO.setFeatureId(featureId);
            List<IssueDTO> issueDTOList = issueMapper.select(selectDO);
            if (issueDTOList != null && !issueDTOList.isEmpty()) {
                issueDTOList.forEach(issueDO -> createIssueEpicLog(epicId, issueDO));
                List<Long> projectIds = issueDTOList.stream().map(IssueDTO::getProjectId).collect(Collectors.toList());
                projectIds.forEach(projectId ->
                        redisUtil.deleteRedisCache(new String[]{
                                BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + EPIC + ":" + epicId})
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void updateEpicIdOfStoryByFeatureList(Object[] args) {
        List<Long> featureIds = (List<Long>) args[0];
        Long epicId = (Long) args[1];
        if (featureIds != null && !featureIds.isEmpty() && epicId != null) {
            List<IssueDTO> issueDTOList = issueMapper.selectByFeatureIds(featureIds);
            if (issueDTOList != null && !issueDTOList.isEmpty()) {
                issueDTOList.forEach(issueDO -> createIssueEpicLog(epicId, issueDO));
                List<Long> projectIds = issueDTOList.stream().map(IssueDTO::getProjectId).collect(Collectors.toList());
                projectIds.forEach(projectId ->
                        redisUtil.deleteRedisCache(new String[]{
                                BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + EPIC + ":" + epicId})
                );
            }
        }
    }

    private void handleVersionCreateDataLog(Object[] args) {
        VersionIssueRelE versionIssueRelE = null;
        for (Object arg : args) {
            if (arg instanceof VersionIssueRelE) {
                versionIssueRelE = (VersionIssueRelE) arg;
            }
        }
        if (versionIssueRelE != null) {
            String field;
            if (versionIssueRelE.getRelationType() == null) {
                field = FIELD_FIX_VERSION;
            } else {
                field = FIX_VERSION.equals(versionIssueRelE.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
            }
            dataLogRedisUtil.deleteByHandleBatchDeleteVersion(versionIssueRelE.getProjectId(), versionIssueRelE.getVersionId());
            createDataLog(versionIssueRelE.getProjectId(), versionIssueRelE.getIssueId(), field,
                    null, productVersionMapper.selectByPrimaryKey(versionIssueRelE.getVersionId()).getName(),
                    null, versionIssueRelE.getVersionId().toString());
        }
    }

    @SuppressWarnings("unchecked")
    private void batchRemoveVersionDataLog(Object[] args) {
        Long projectId = null;
        List<Long> issueIds = null;
        for (Object arg : args) {
            if (arg instanceof Long) {
                projectId = (Long) arg;
            } else if (arg instanceof List) {
                issueIds = (List<Long>) arg;
            }
        }
        if (projectId != null && issueIds != null && !issueIds.isEmpty()) {
            handleBatchRemoveVersionDataLog(issueIds, projectId);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleBatchRemoveVersionDataLog(List<Long> issueIds, Long projectId) {
        Map map = new HashMap(issueIds.size());
        for (Long issueId : issueIds) {
            map.put(issueId, productVersionMapper.selectVersionRelsByIssueId(projectId, issueId));
        }
        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry<Long, List<ProductVersionDO>>) object;
            Long issueId = Long.parseLong(entry.getKey().toString());
            List<ProductVersionDO> versionIssueRelDOList = (List<ProductVersionDO>) entry.getValue();
            for (ProductVersionDO productVersionDO : versionIssueRelDOList) {
                String field;
                if (productVersionDO.getRelationType() == null) {
                    field = FIELD_FIX_VERSION;
                } else {
                    field = FIX_VERSION.equals(productVersionDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                }
                createDataLog(projectId, issueId, field, productVersionDO.getName(),
                        null, productVersionDO.getVersionId().toString(), null);
            }
            dataLogRedisUtil.deleteByBatchDeleteVersionDataLog(projectId, versionIssueRelDOList);
        }
    }


    private void batchToVersionDataLog(Object[] args) {
        VersionIssueRelE versionIssueRelE = null;
        for (Object arg : args) {
            if (arg instanceof VersionIssueRelE) {
                versionIssueRelE = (VersionIssueRelE) arg;
            }
        }
        if (versionIssueRelE != null) {
            ProductVersionDO productVersionDO = productVersionMapper.selectByPrimaryKey(versionIssueRelE.getVersionId());
            if (productVersionDO == null) {
                throw new CommonException("error.productVersion.get");
            }
            if (versionIssueRelE.getIssueIds() != null && !versionIssueRelE.getIssueIds().isEmpty()) {
                Long userId = DetailsHelper.getUserDetails().getUserId();
                dataLogMapper.batchCreateVersionDataLog(versionIssueRelE.getProjectId(), productVersionDO, versionIssueRelE.getIssueIds(), userId);
                redisUtil.deleteRedisCache(new String[]{VERSION_CHART + productVersionDO.getProjectId() + ':' + productVersionDO.getVersionId() + ":" + "*",
                        BURN_DOWN_COORDINATE_BY_TYPE + productVersionDO.getProjectId() + ":" + VERSION + ":" + productVersionDO.getVersionId()
                });
            }
        }
    }

    private Object handleIssueCreateDataLog(ProceedingJoinPoint pjp) {
        Object result;
        try {
            result = pjp.proceed();
            IssueConvertDTO issueConvertDTO = (IssueConvertDTO) result;
            if (issueConvertDTO != null) {
                //若创建issue的初始状态为已完成，生成日志
                IssueStatusDTO issueStatusDTO = issueStatusMapper.selectByStatusId(issueConvertDTO.getProjectId(), issueConvertDTO.getStatusId());
                Boolean condition = (issueStatusDTO.getCompleted() != null && issueStatusDTO.getCompleted());
                if (condition) {
                    StatusMapVO statusMapVO = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(issueConvertDTO.getProjectId()), issueConvertDTO.getStatusId()).getBody();
                    createDataLog(issueConvertDTO.getProjectId(), issueConvertDTO.getIssueId(), FIELD_RESOLUTION, null,
                            statusMapVO.getName(), null, issueStatusDTO.getStatusId().toString());
                }
                if (issueConvertDTO.getEpicId() != null && !issueConvertDTO.getEpicId().equals(0L)) {
                    //选择EPIC要生成日志
                    Long epicId = issueConvertDTO.getEpicId();
                    issueConvertDTO.setEpicId(null);
                    createIssueEpicLog(epicId, ConvertHelper.convert(issueConvertDTO, IssueDTO.class));
                }
                Boolean storyCondition = issueConvertDTO.getStoryPoints() != null && issueConvertDTO.getStoryPoints().compareTo(BigDecimal.ZERO) != 0;
                Boolean remainingTimeCondition = issueConvertDTO.getRemainingTime() != null && issueConvertDTO.getRemainingTime().compareTo(new BigDecimal(0)) > 0;
                if (storyCondition || remainingTimeCondition) {
                    IssueDTO originIssueDTO = new IssueDTO();
                    BeanUtils.copyProperties(issueConvertDTO, originIssueDTO);
                    if (storyCondition) {
                        BigDecimal zero = new BigDecimal(0);
                        originIssueDTO.setStoryPoints(zero);
                        handleStoryPointsLog(originIssueDTO, issueConvertDTO);
                    } else {
                        originIssueDTO.setRemainingTime(null);
                        handleCalculateRemainData(issueConvertDTO, originIssueDTO);
                    }
                }
                dataLogRedisUtil.deleteByHandleIssueCreateDataLog(issueConvertDTO, condition);
            }
        } catch (Throwable e) {
            throw new CommonException(ERROR_METHOD_EXECUTE, e);
        }
        return result;
    }

    private void handleSprintDataLog(Object[] args) {
        IssueSprintRelE issueSprintRelE = null;
        for (Object arg : args) {
            if (arg instanceof IssueSprintRelE) {
                issueSprintRelE = (IssueSprintRelE) arg;
            }
        }
        if (issueSprintRelE != null) {
            SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(issueSprintRelE.getSprintId());
            createDataLog(issueSprintRelE.getProjectId(), issueSprintRelE.getIssueId(),
                    FIELD_SPRINT, null, sprintDTO.getSprintName(), null, issueSprintRelE.getSprintId().toString());
            dataLogRedisUtil.deleteByHandleSprintDataLog(sprintDTO);
        }
    }

    private void handlePiDataLog(Object[] args) {
        PiFeatureE piFeatureE = null;
        for (Object arg : args) {
            if (arg instanceof PiFeatureE) {
                piFeatureE = (PiFeatureE) arg;
            }
        }
        if (piFeatureE != null) {
            List<PiNameDTO> piNameDTOList = piMapper.selectclosePiListByIssueId(piFeatureE.getProgramId(), piFeatureE.getIssueId());
            PiNameDTO currentPiNameDTO = piMapper.selectCurrentPiListByIssueId(piFeatureE.getProgramId(), piFeatureE.getIssueId());
            PiDTO targetPi = piMapper.selectByPrimaryKey(piFeatureE.getPiId());
            String oldString = "";
            String oldvalue = "";
            String newString = "";
            String newValue = "";
            if (piNameDTOList != null && !piNameDTOList.isEmpty()) {
                oldString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                oldvalue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
                newString += piNameDTOList.stream().map(piNameDO -> piNameDO.getCode() + "-" + piNameDO.getName()).collect(Collectors.joining(","));
                newValue += piNameDTOList.stream().map(piNameDO -> piNameDO.getId().toString()).collect(Collectors.joining(","));
            }
            if (currentPiNameDTO != null) {
                oldString = oldString + ("".equals(oldString) ? currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName() : "," + currentPiNameDTO.getCode() + "-" + currentPiNameDTO.getName());
                oldvalue = oldvalue + ("".equals(oldvalue) ? currentPiNameDTO.getId().toString() : "," + currentPiNameDTO.getId().toString());
            }
            if (targetPi != null) {
                newString = newString + ("".equals(newString) ? targetPi.getCode() + "-" + targetPi.getName() : "," + targetPi.getCode() + "-" + targetPi.getName());
                newValue = newValue + ("".equals(newValue) ? targetPi.getId() : "," + targetPi.getId().toString());
            }
            createDataLog(piFeatureE.getProgramId(), piFeatureE.getIssueId(), FIELD_PI,
                    "".equals(oldString) ? null : oldString,
                    "".equals(newString) ? null : newString,
                    "".equals(oldvalue) ? null : oldvalue,
                    "".equals(newValue) ? null : newValue);
        }
    }

    private void handleIssueDataLog(Object[] args) {
        IssueConvertDTO issueConvertDTO = null;
        List<String> field = null;
        for (Object arg : args) {
            if (arg instanceof IssueConvertDTO) {
                issueConvertDTO = (IssueConvertDTO) arg;
            } else if (arg instanceof String[]) {
                field = Arrays.asList((String[]) arg);
            }
        }
        if (issueConvertDTO != null && field != null && !field.isEmpty()) {
            IssueDTO originIssueDTO = issueMapper.selectByPrimaryKey(issueConvertDTO.getIssueId());
            handleIssueEpicName(field, originIssueDTO, issueConvertDTO);
            handleIssueSummary(field, originIssueDTO, issueConvertDTO);
            handleDescription(field, originIssueDTO, issueConvertDTO);
            handlePriority(field, originIssueDTO, issueConvertDTO);
            handleAssignee(field, originIssueDTO, issueConvertDTO);
            handleReporter(field, originIssueDTO, issueConvertDTO);
            handleStoryPoints(field, originIssueDTO, issueConvertDTO);
            handleIssueEpic(field, originIssueDTO, issueConvertDTO);
            handleRemainTime(field, originIssueDTO, issueConvertDTO);
            handleStatus(field, originIssueDTO, issueConvertDTO);
            handleRank(field, originIssueDTO, issueConvertDTO);
            handleType(field, originIssueDTO, issueConvertDTO);
        }
    }

    private void handleType(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(TYPE_CODE) && !Objects.equals(originIssueDTO.getTypeCode(), issueConvertDTO.getTypeCode())) {
            String originTypeName = issueFeignClient.queryIssueTypeById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), originIssueDTO.getIssueTypeId()).getBody().getName();
            String currentTypeName = issueFeignClient.queryIssueTypeById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), issueConvertDTO.getIssueTypeId()).getBody().getName();
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_ISSUETYPE, originTypeName, currentTypeName,
                    originIssueDTO.getIssueTypeId().toString(), issueConvertDTO.getIssueTypeId().toString());
            dataLogRedisUtil.deleteByHandleType(issueConvertDTO, originIssueDTO);
        }
    }

    private void handleRank(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(RANK_FIELD) && originIssueDTO.getRank() != null && issueConvertDTO.getRank() != null && !Objects.equals(originIssueDTO.getRank(), issueConvertDTO.getRank())) {
            if (originIssueDTO.getRank().compareTo(issueConvertDTO.getRank()) < 0) {
                createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                        FIELD_RANK, null, RANK_HIGHER, null, null);
            } else {
                createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                        FIELD_RANK, null, RANK_LOWER, null, null);
            }
        }
    }

    private void handleStatus(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(STATUS_ID) && !Objects.equals(originIssueDTO.getStatusId(), issueConvertDTO.getStatusId())) {
            StatusMapVO originStatusMapVO = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), originIssueDTO.getStatusId()).getBody();
            StatusMapVO currentStatusMapVO = stateMachineFeignClient.queryStatusById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), issueConvertDTO.getStatusId()).getBody();
            IssueStatusDTO originStatus = issueStatusMapper.selectByStatusId(originIssueDTO.getProjectId(), originIssueDTO.getStatusId());
            IssueStatusDTO currentStatus = issueStatusMapper.selectByStatusId(originIssueDTO.getProjectId(), issueConvertDTO.getStatusId());
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_STATUS, originStatusMapVO.getName(),
                    currentStatusMapVO.getName(), originIssueDTO.getStatusId().toString(), issueConvertDTO.getStatusId().toString());
            Boolean condition = (originStatus.getCompleted() != null && originStatus.getCompleted()) || (currentStatus.getCompleted() != null && currentStatus.getCompleted());
            if (condition) {
                //生成解决问题日志
                dataLogResolution(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), originStatus, currentStatus, originStatusMapVO, currentStatusMapVO);
            }
            //删除缓存
            dataLogRedisUtil.deleteByHandleStatus(issueConvertDTO, originIssueDTO, condition);
        }
    }

    private void handleRemainTime(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(REMAIN_TIME_FIELD) && (!Objects.equals(originIssueDTO.getRemainingTime(), issueConvertDTO.getRemainingTime()))) {
            handleCalculateRemainData(issueConvertDTO, originIssueDTO);
        }
    }

    private void handleCalculateRemainData(IssueConvertDTO issueConvertDTO, IssueDTO originIssueDTO) {
        String oldData;
        String newData;
        BigDecimal zero = new BigDecimal(0);
        if (issueConvertDTO.getRemainingTime() != null && issueConvertDTO.getRemainingTime().compareTo(zero) > 0) {
            oldData = originIssueDTO.getRemainingTime() == null ? null : originIssueDTO.getRemainingTime().toString();
            newData = issueConvertDTO.getRemainingTime().toString();
        } else if (issueConvertDTO.getRemainingTime() == null) {
            oldData = originIssueDTO.getRemainingTime() == null ? null : originIssueDTO.getRemainingTime().toString();
            newData = null;
        } else {
            oldData = originIssueDTO.getRemainingTime() == null ? null : originIssueDTO.getRemainingTime().toString();
            newData = zero.toString();
        }
        createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_TIMEESTIMATE, oldData, newData, oldData, newData);
        dataLogRedisUtil.deleteByHandleCalculateRemainData(issueConvertDTO, originIssueDTO);
    }

    private void deleteBurnDownCoordinateByTypeEpic(Long epicId, Long projectId, Long issueId) {
        if (epicId == null && issueId != null) {
            epicId = issueMapper.selectByPrimaryKey(issueId).getEpicId();
        }
        if (epicId != null && epicId != 0) {
            redisUtil.deleteRedisCache(new String[]{
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + EPIC + ":" + epicId
            });
        }
    }

    private void handleStoryPoints(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        Boolean condition = field.contains(STORY_POINTS_FIELD) && (!Objects.equals(originIssueDTO.getStoryPoints(), issueConvertDTO.getStoryPoints()));
        if (condition) {
            handleStoryPointsLog(originIssueDTO, issueConvertDTO);
        }
    }

    private void handleStoryPointsLog(IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        String oldString = null;
        String newString = null;
        if (originIssueDTO.getStoryPoints() != null) {
            oldString = originIssueDTO.getStoryPoints().toString();
        }
        if (issueConvertDTO.getStoryPoints() != null) {
            newString = issueConvertDTO.getStoryPoints().toString();
        }
        createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                FIELD_STORY_POINTS, oldString, newString, null, null);
        dataLogRedisUtil.deleteByHandleStoryPoints(issueConvertDTO, originIssueDTO);
    }


    private void handleReporter(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(REPORTER_ID_FIELD) && !Objects.equals(originIssueDTO.getReporterId(), issueConvertDTO.getReporterId())) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originIssueDTO.getReporterId() != null && originIssueDTO.getReporterId() != 0) {
                oldValue = originIssueDTO.getReporterId().toString();
                oldString = userRepository.queryUserNameByOption(originIssueDTO.getReporterId(), false).getRealName();
            }
            if (issueConvertDTO.getReporterId() != null && issueConvertDTO.getReporterId() != 0) {
                newValue = issueConvertDTO.getReporterId().toString();
                newString = userRepository.queryUserNameByOption(issueConvertDTO.getReporterId(), false).getRealName();
            }
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                    FIELD_REPORTER, oldString, newString, oldValue, newValue);
        }
    }

    private void handleAssignee(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(ASSIGNEE_ID_FIELD) && !Objects.equals(originIssueDTO.getAssigneeId(), issueConvertDTO.getAssigneeId())) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originIssueDTO.getAssigneeId() != null && originIssueDTO.getAssigneeId() != 0) {
                oldValue = originIssueDTO.getAssigneeId().toString();
                oldString = userRepository.queryUserNameByOption(originIssueDTO.getAssigneeId(), false).getRealName();
            }
            if (issueConvertDTO.getAssigneeId() != null && issueConvertDTO.getAssigneeId() != 0) {
                newValue = issueConvertDTO.getAssigneeId().toString();
                newString = userRepository.queryUserNameByOption(issueConvertDTO.getAssigneeId(), false).getRealName();
            }
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                    FIELD_ASSIGNEE, oldString, newString, oldValue, newValue);
            redisUtil.deleteRedisCache(new String[]{PIECHART + originIssueDTO.getProjectId() + ':' + FIELD_ASSIGNEE + "*"});
        }
    }

    private void handlePriority(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(PRIORITY_CODE_FIELD) && !Objects.equals(originIssueDTO.getPriorityId(), issueConvertDTO.getPriorityId())) {
            PriorityVO originPriorityVO = issueFeignClient.queryById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), originIssueDTO.getPriorityId()).getBody();
            PriorityVO currentPriorityVO = issueFeignClient.queryById(ConvertUtil.getOrganizationId(originIssueDTO.getProjectId()), issueConvertDTO.getPriorityId()).getBody();
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                    FIELD_PRIORITY, originPriorityVO.getName()
                    , currentPriorityVO.getName(), originIssueDTO.getProjectId().toString(), issueConvertDTO.getPriorityId().toString());
            redisUtil.deleteRedisCache(new String[]{PIECHART + originIssueDTO.getProjectId() + ':' + FIELD_PRIORITY + "*"});
        }
    }

    private void handleDescription(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(DESCRIPTION) && !Objects.equals(originIssueDTO.getDescription(), issueConvertDTO.getDescription())) {
            if (!FIELD_DESCRIPTION_NULL.equals(issueConvertDTO.getDescription())) {
                createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                        DESCRIPTION, originIssueDTO.getDescription(), issueConvertDTO.getDescription(), null, null);
            } else {
                createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                        DESCRIPTION, originIssueDTO.getDescription(), null, null, null);
            }
        }
    }

    private void handleIssueSummary(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(SUMMARY_FIELD) && !Objects.equals(originIssueDTO.getSummary(), issueConvertDTO.getSummary())) {
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                    SUMMARY_FIELD, originIssueDTO.getSummary(), issueConvertDTO.getSummary(), null, null);
        }
    }

    private void handleIssueEpicName(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(EPIC_NAME_FIELD) && !Objects.equals(originIssueDTO.getEpicName(), issueConvertDTO.getEpicName())) {
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(),
                    FIELD_EPIC_NAME, originIssueDTO.getEpicName(), issueConvertDTO.getEpicName(), null, null);
        }
    }

    private void handleIssueEpic(List<String> field, IssueDTO originIssueDTO, IssueConvertDTO issueConvertDTO) {
        if (field.contains(EPIC_ID_FIELD) && !Objects.equals(originIssueDTO.getEpicId(), issueConvertDTO.getEpicId())) {
            createIssueEpicLog(issueConvertDTO.getEpicId(), originIssueDTO);
        }
    }

    private void createIssueEpicLog(Long epicId, IssueDTO originIssueDTO) {
        ProjectInfoDO projectInfoDO = projectInfoMapper.queryByProjectId(originIssueDTO.getProjectId());
        if (projectInfoDO == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        if ((originIssueDTO.getEpicId() == null || originIssueDTO.getEpicId() == 0)) {
            if (!Objects.equals(epicId, 0L)) {
                dataLogCreateEpicId(epicId, originIssueDTO, projectInfoDO);
            }
        } else {
            dataLogChangeEpicId(epicId, originIssueDTO, projectInfoDO);
        }
    }

    private void dataLogResolution(Long projectId, Long issueId, IssueStatusDTO originStatus, IssueStatusDTO currentStatus, StatusMapVO originStatusMapVO, StatusMapVO currentStatusMapVO) {
        Boolean condition = (originStatus.getCompleted() == null || !originStatus.getCompleted()) || (currentStatus.getCompleted() == null || !currentStatus.getCompleted());
        if (condition) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originStatus.getCompleted() != null && originStatus.getCompleted()) {
                oldValue = originStatus.getStatusId().toString();
                oldString = originStatusMapVO.getName();
            } else if (currentStatus.getCompleted()) {
                newValue = currentStatus.getStatusId().toString();
                newString = currentStatusMapVO.getName();
            }
            createDataLog(projectId, issueId, FIELD_RESOLUTION, oldString, newString, oldValue, newValue);
            redisUtil.deleteRedisCache(new String[]{PIECHART + projectId + ':' + FIELD_RESOLUTION + "*"});
        }
    }

    private void dataLogCreateEpicId(Long epicId, IssueDTO originIssueDTO, ProjectInfoDO projectInfoDO) {
//        IssueDTO issueEpic = queryIssueByIssueIdAndProjectId(originIssueDTO.getProjectId(), epicId);
        IssueDTO issueEpic = issueMapper.selectByPrimaryKey(epicId);
        if (issueEpic == null) {
            throw new CommonException(ERROR_EPIC_NOT_FOUND);
        } else {
            ProjectInfoDO epicProject = projectInfoMapper.queryByProjectId(issueEpic.getProjectId());
            if (epicProject == null) {
                throw new CommonException(ERROR_EPIC_NOT_FOUND);
            }
            deleteBurnDownCoordinateByTypeEpic(issueEpic.getIssueId(), projectInfoDO.getProjectId(), null);
            createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_EPIC_LINK,
                    null, epicProject.getProjectCode() + "-" + issueEpic.getIssueNum(),
                    null, issueEpic.getIssueId().toString());
            createDataLog(epicProject.getProjectId(), epicId, FIELD_EPIC_CHILD,
                    null, projectInfoDO.getProjectCode() + "-" + originIssueDTO.getIssueNum(),
                    null, originIssueDTO.getIssueId().toString());
            dataLogRedisUtil.deleteByDataLogCreateEpicId(projectInfoDO.getProjectId(), issueEpic.getIssueId());

        }
    }

    private void dataLogChangeEpicId(Long epicId, IssueDTO originIssueDTO, ProjectInfoDO projectInfoDO) {
//        IssueDTO oldIssueEpic = queryIssueByIssueIdAndProjectId(originIssueDTO.getProjectId(), originIssueDTO.getEpicId());
        IssueDTO oldIssueEpic = issueMapper.selectByPrimaryKey(originIssueDTO.getEpicId());
        if (oldIssueEpic == null) {
            throw new CommonException(ERROR_EPIC_NOT_FOUND);
        } else {
            ProjectInfoDO oldEpicProject = projectInfoMapper.queryByProjectId(oldIssueEpic.getProjectId());
            if (oldEpicProject == null) {
                throw new CommonException(ERROR_EPIC_NOT_FOUND);
            }
            deleteBurnDownCoordinateByTypeEpic(originIssueDTO.getEpicId(), projectInfoDO.getProjectId(), null);
            if (epicId == null || epicId == 0) {
                createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_EPIC_LINK,
                        oldEpicProject.getProjectCode() + "-" + oldIssueEpic.getIssueNum(),
                        null, oldIssueEpic.getIssueId().toString(), null);
            } else {
//                IssueDTO newIssueEpic = queryIssueByIssueIdAndProjectId(originIssueDTO.getProjectId(), epicId);
                IssueDTO newIssueEpic = issueMapper.selectByPrimaryKey(epicId);
                if (newIssueEpic == null) {
                    throw new CommonException(ERROR_EPIC_NOT_FOUND);
                } else {
                    ProjectInfoDO newEpicProject = projectInfoMapper.queryByProjectId(newIssueEpic.getProjectId());
                    if (newEpicProject == null) {
                        throw new CommonException(ERROR_EPIC_NOT_FOUND);
                    }
                    deleteBurnDownCoordinateByTypeEpic(epicId, projectInfoDO.getProjectId(), null);
                    createDataLog(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), FIELD_EPIC_LINK,
                            oldEpicProject.getProjectCode() + "-" + oldIssueEpic.getIssueNum(),
                            newEpicProject.getProjectCode() + "-" + newIssueEpic.getIssueNum(),
                            oldIssueEpic.getIssueId().toString(), newIssueEpic.getIssueId().toString());
                    createDataLog(newEpicProject.getProjectId(), epicId, FIELD_EPIC_CHILD,
                            null, projectInfoDO.getProjectCode() + "-" + originIssueDTO.getIssueNum(),
                            null, originIssueDTO.getIssueId().toString());
                    dataLogRedisUtil.deleteByDataLogCreateEpicId(projectInfoDO.getProjectId(), newIssueEpic.getIssueId());
                }
            }
            createDataLog(oldEpicProject.getProjectId(), originIssueDTO.getEpicId(), FIELD_EPIC_CHILD,
                    projectInfoDO.getProjectCode() + "-" + originIssueDTO.getIssueNum(), null,
                    originIssueDTO.getIssueId().toString(), null);
            dataLogRedisUtil.deleteByDataLogCreateEpicId(projectInfoDO.getProjectId(), oldIssueEpic.getIssueId());
        }
    }

    private IssueDTO queryIssueByIssueIdAndProjectId(Long projectId, Long issueId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setIssueId(issueId);
        issueDTO.setProjectId(projectId);
        return issueMapper.selectOne(issueDTO);
    }

    private void createDataLog(Long projectId, Long issueId, String field, String oldString,
                               String newString, String oldValue, String newValue) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setIssueId(issueId);
        dataLogE.setField(field);
        dataLogE.setOldString(oldString);
        dataLogE.setNewString(newString);
        dataLogE.setOldValue(oldValue);
        dataLogE.setNewValue(newValue);
        dataLogRepository.create(dataLogE);
    }

}
