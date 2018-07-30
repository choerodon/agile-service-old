package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.api.dto.SprintNameDTO;
import io.choerodon.agile.app.assembler.SprintNameAssembler;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日志切面
 *
 * @author dinghuang123@gmail.com
 * @since 2018/7/23
 */
@Aspect
@Component
public class LogDataAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogDataAspect.class);

    private static final String ISSUE = "issue";
    private static final String ISSUE_CREATE = "issueCreate";
    private static final String SPRINT = "sprint";
    private static final String VERSION_CREATE = "versionCreate";
    private static final String COMPONENT_CREATE = "componentCreate";
    private static final String COMPONENT_DELETE = "componentDelete";
    private static final String LABEL_DELETE = "labelDelete";
    private static final String LABEL_CREATE = "labelCreate";
    private static final String VERSION_DELETE = "versionDelete";
    private static final String BATCH_DELETE_VERSION = "batchDeleteVersion";
    private static final String BATCH_COMPONENT_DELETE = "batchComponentDelete";
    private static final String BATCH_TO_VERSION = "batchToVersion";
    private static final String BATCH_REMOVE_VERSION = "batchRemoveVersion";
    private static final String BATCH_TO_EPIC = "batchToEpic";
    private static final String BATCH_REMOVE_SPRINT = "batchRemoveSprint";
    private static final String BATCH_REMOVE_SPRINT_BY_SPRINT_ID = "batchRemoveSprintBySprintId";
    private static final String BATCH_DELETE_LABEL = "batchDeleteLabel";
    private static final String CREATE_ATTACHMENT = "createAttachment";
    private static final String DELETE_ATTACHMENT = "deleteAttachment";
    private static final String CREATE_COMMENT = "createComment";
    private static final String UPDATE_COMMENT = "updateComment";
    private static final String DELETE_COMMENT = "deleteComment";
    private static final String CREATE_WORKLOG = "createWorkLog";
    private static final String EPIC_NAME_FIELD = "epicName";
    private static final String FIELD_EPIC_NAME = "Epic Name";
    private static final String SUMMARY_FIELD = "summary";
    private static final String DESCRIPTION = "description";
    private static final String FIELD_DESCRIPTION_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_PRIORITY = "priority";
    private static final String PRIORITY_CODE_FIELD = "priorityCode";
    private static final String FIELD_ASSIGNEE = "assignee";
    private static final String ASSIGNEE_ID_FIELD = "assigneeId";
    private static final String REPORTER_ID_FIELD = "reporterId";
    private static final String FIELD_REPORTER = "reporter";
    private static final String SPRINT_ID_FIELD = "sprintId";
    private static final String FIELD_SPRINT = "Sprint";
    private static final String STORY_POINTS_FIELD = "storyPoints";
    private static final String EPIC_ID_FIELD = "epicId";
    private static final String FIELD_STORY_POINTS = "Story Points";
    private static final String ERROR_PROJECT_INFO_NOT_FOUND = "error.createIssue.projectInfoNotFound";
    private static final String ERROR_EPIC_NOT_FOUND = "error.dataLogEpic.epicNotFound";
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
    private static final String EXCEPTION = "Exception";

    @Autowired
    private IssueStatusMapper issueStatusMapper;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueLabelMapper issueLabelMapper;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;
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
                    case BATCH_COMPONENT_DELETE:
                        batchComponentDeleteDataLog(args);
                        break;
                    case BATCH_REMOVE_SPRINT:
                        batchRemoveSprintDataLog(args);
                        break;
                    case BATCH_DELETE_LABEL:
                        batchDeleteLabelDataLog(args);
                        break;
                    case BATCH_DELETE_VERSION:
                        batchDeleteVersionDataLog(args);
                        break;
                    case BATCH_MOVE_TO_VERSION:
                        batchMoveVersionDataLog(args);
                        break;
                    case BATCH_REMOVE_SPRINT_BY_SPRINT_ID:
                        batchRemoveSprintBySprintId(args);
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
            logger.info("exception: ", e);
            throw new CommonException(ERROR_UPDATE);
        }
        return result;
    }

    private void handleUpdateCommentDataLog(Object[] args) {
        IssueCommentE issueCommentE = null;
        for (Object arg : args) {
            if (arg instanceof IssueCommentE) {
                issueCommentE = (IssueCommentE) arg;
            }
        }
        if (issueCommentE != null) {
            IssueCommentDO issueCommentDO = issueCommentMapper.selectByPrimaryKey(issueCommentE.getCommentId());
            createDataLog(issueCommentDO.getProjectId(), issueCommentE.getIssueId(), FIELD_COMMENT,
                    issueCommentDO.getCommentText(), issueCommentE.getCommentText(), issueCommentE.getCommentId().toString(),
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
                DataLogDO dataLogDO = dataLogMapper.selectLastWorkLogById(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_TIMESPENT);
                String oldString = null;
                String newString;
                String oldValue = null;
                String newValue;
                if (dataLogDO != null) {
                    oldValue = dataLogDO.getNewValue();
                    oldString = dataLogDO.getNewString();
                    BigDecimal newTime = new BigDecimal(dataLogDO.getNewValue());
                    newValue = newTime.add(workLogE.getWorkTime()).toString();
                    newString = newTime.add(workLogE.getWorkTime()).toString();
                } else {
                    newValue = workLogE.getWorkTime().toString();
                    newString = workLogE.getWorkTime().toString();
                }
                createDataLog(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_TIMESPENT,
                        oldString, newString, oldValue, newValue);
                createDataLog(workLogE.getProjectId(), workLogE.getIssueId(), FIELD_WORKLOGID,
                        workLogE.getLogId().toString(), null, workLogE.getLogId().toString(), null);
            } catch (Throwable throwable) {
                logger.info(EXCEPTION, throwable);
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
            //批量sql
            for (VersionIssueDO versionIssueDO : versionIssueDOS) {
                String field = FIX_VERSION.equals(versionIssueDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                createDataLog(projectId, versionIssueDO.getIssueId(), field, null,
                        productVersionDO.getName(), null, targetVersionId.toString());
            }
        }
    }

    private void handleDeleteCommentDataLog(Object[] args) {
        IssueCommentDO issueCommentDO = null;
        for (Object arg : args) {
            if (arg instanceof IssueCommentDO) {
                issueCommentDO = (IssueCommentDO) arg;
            }
        }
        if (issueCommentDO != null) {
            createDataLog(issueCommentDO.getProjectId(), issueCommentDO.getIssueId(), FIELD_COMMENT,
                    issueCommentDO.getCommentText(), null, issueCommentDO.getCommentId().toString(), null);
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
            } catch (Throwable throwable) {
                logger.info(EXCEPTION, throwable);
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
            IssueAttachmentDO issueAttachmentDO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
            createDataLog(issueAttachmentDO.getProjectId(), issueAttachmentDO.getIssueId(), FIELD_ATTACHMENT,
                    issueAttachmentDO.getUrl(), null, issueAttachmentDO.getAttachmentId().toString(), null);
        }
    }

    private Object handleCreateAttachmentDataLog(Object[] args, ProceedingJoinPoint pjp) {
        IssueAttachmentE issueAttachmentE = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof IssueAttachmentE) {
                issueAttachmentE = (IssueAttachmentE) arg;
            }
        }
        if (issueAttachmentE != null) {
            try {
                result = pjp.proceed();
                issueAttachmentE = (IssueAttachmentE) result;
                createDataLog(issueAttachmentE.getProjectId(), issueAttachmentE.getIssueId(), FIELD_ATTACHMENT,
                        null, issueAttachmentE.getUrl(), null, issueAttachmentE.getAttachmentId().toString());
            } catch (Throwable throwable) {
                logger.info(EXCEPTION, throwable);
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
            List<ProductVersionDO> productVersionDOS = productVersionMapper.queryVersionRelByIssueIdAndType(
                    versionIssueRelE.getProjectId(), versionIssueRelE.getIssueId(), versionIssueRelE.getRelationType());
            Long issueId = versionIssueRelE.getIssueId();
            String field = FIX_VERSION.equals(versionIssueRelE.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
            productVersionDOS.forEach(productVersionDO -> createDataLog(productVersionDO.getProjectId(), issueId, field,
                    productVersionDO.getName(), null, productVersionDO.getVersionId().toString(), null));
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
            String field = FIX_VERSION.equals(versionIssueRelDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
            createDataLog(versionIssueRelDO.getProjectId(), versionIssueRelDO.getIssueId(), field,
                    productVersionMapper.selectByPrimaryKey(versionIssueRelDO.getVersionId()).getName(), null,
                    versionIssueRelDO.getVersionId().toString(), null);
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
            result = createLabelDataLog(labelIssueRelE.getLabelId(), labelIssueRelE.getIssueId(), pjp);
        }
        return result;
    }

    private Object createLabelDataLog(Long labelId, Long issueId, ProceedingJoinPoint pjp) {
        IssueLabelDO issueLabelDO = issueLabelMapper.selectByPrimaryKey(labelId);
        List<IssueLabelDO> originLabels = issueMapper.selectLabelNameByIssueId(issueId);
        Object result = null;
        try {
            result = pjp.proceed();
            List<IssueLabelDO> curLabels = issueMapper.selectLabelNameByIssueId(issueId);
            createDataLog(issueLabelDO.getProjectId(), issueId, FIELD_LABELS, getOriginLabelNames(originLabels),
                    getOriginLabelNames(curLabels), null, null);
        } catch (Throwable throwable) {
            logger.info(EXCEPTION, throwable);
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
            result = createLabelDataLog(labelIssueRelDO.getLabelId(), labelIssueRelDO.getIssueId(), pjp);
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
            IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
            List<IssueLabelDO> originLabels = issueMapper.selectLabelNameByIssueId(issueId);
            createDataLog(issueDO.getProjectId(), issueId, FIELD_LABELS, getOriginLabelNames(originLabels),
                    null, null, null);
        }
    }

    private String getOriginLabelNames(List<IssueLabelDO> originLabels) {
        StringBuilder originLabelNames = new StringBuilder();
        int originIdx = 0;
        for (IssueLabelDO label : originLabels) {
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

    private void handleBatchRemoveSprint(Long projectId, List<Long> issueIds, Long sprintId) {
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        for (Long issueId : issueIds) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(issueId));
            if (activeSprintName != null && sprintId.equals(activeSprintName.getSprintId())) {
                continue;
            }
            StringBuilder newSprintIdStr = new StringBuilder();
            StringBuilder newSprintNameStr = new StringBuilder();
            List<SprintNameDTO> sprintNames = sprintNameAssembler.doListToDTO(issueMapper.querySprintNameByIssueId(issueId));
            String oldSprintIdStr = sprintNames.stream().map(sprintName -> sprintName.getSprintId().toString()).collect(Collectors.joining(","));
            String oldSprintNameStr = sprintNames.stream().map(SprintNameDTO::getSprintName).collect(Collectors.joining(","));
            handleSprintStringBuilder(sprintNames, activeSprintName, newSprintNameStr, newSprintIdStr, sprintDO);
            String oldString = "".equals(oldSprintNameStr) ? null : oldSprintNameStr;
            String newString = newSprintNameStr.length() == 0 ? null : newSprintNameStr.toString();
            String oldValue = "".equals(oldSprintIdStr) ? null : oldSprintIdStr;
            String newValue = newSprintIdStr.length() == 0 ? null : newSprintIdStr.toString();
            createDataLog(projectId, issueId, FIELD_SPRINT, oldString,
                    newString, oldValue, newValue);
        }
    }

    private void handleSprintStringBuilder(List<SprintNameDTO> sprintNames, SprintNameDTO activeSprintName,
                                           StringBuilder newSprintNameStr, StringBuilder newSprintIdStr, SprintDO sprintDO) {
        int idx = 0;
        for (SprintNameDTO sprintName : sprintNames) {
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
        if (sprintDO != null) {
            newSprintIdStr.append(newSprintIdStr.length() == 0 ? sprintDO.getSprintId().toString() : "," + sprintDO.getSprintId().toString());
            newSprintNameStr.append(newSprintNameStr.length() == 0 ? sprintDO.getSprintName() : "," + sprintDO.getSprintName());
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
        }
    }

    @SuppressWarnings("unchecked")
    private void batchToEpicDataLog(Object[] args) {
        Long projectId = (Long) args[0];
        Long epicId = (Long) args[1];
        List<Long> issueIds = (List<Long>) args[2];
        if (projectId != null && epicId != null && issueIds != null && !issueIds.isEmpty()) {
            List<IssueDO> issueDOList = issueMapper.queryIssueEpicInfoByIssueIds(projectId, issueIds);
            issueDOList.forEach(issueEpic -> createIssueEpicLog(epicId, issueEpic));
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
            String field = FIX_VERSION.equals(versionIssueRelE.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
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
        Map map = new HashMap();
        for (Long issueId : issueIds) {
            map.put(issueId, productVersionMapper.selectVersionRelsByIssueId(projectId, issueId));
        }
        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry<Long, List<ProductVersionDO>>) object;
            Long issueId = Long.parseLong(entry.getKey().toString());
            List<ProductVersionDO> versionIssueRelDOList = (List<ProductVersionDO>) entry.getValue();
            for (ProductVersionDO productVersionDO : versionIssueRelDOList) {
                String field = FIX_VERSION.equals(productVersionDO.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                createDataLog(projectId, issueId, field, productVersionDO.getName(),
                        null, productVersionDO.getVersionId().toString(), null);
            }
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
            for (Long issueId : versionIssueRelE.getIssueIds()) {
                String field = FIX_VERSION.equals(versionIssueRelE.getRelationType()) ? FIELD_FIX_VERSION : FIELD_VERSION;
                createDataLog(versionIssueRelE.getProjectId(), issueId, field, null,
                        productVersionDO.getName(), null, productVersionDO.getVersionId().toString());
            }
        }
    }

    private Object handleIssueCreateDataLog(ProceedingJoinPoint pjp) {
        Object result;
        try {
            result = pjp.proceed();
            IssueE issueE = (IssueE) result;
            if (issueE != null) {
                //若创建issue的初始状态为已完成，生成日志
                IssueStatusDO issueStatusDO = issueStatusMapper.selectByPrimaryKey(issueE.getStatusId());
                if ((issueStatusDO.getCompleted() != null && issueStatusDO.getCompleted())) {
                    createDataLog(issueE.getProjectId(), issueE.getIssueId(), FIELD_RESOLUTION, null,
                            issueStatusDO.getName(), null, issueStatusDO.getId().toString());
                }
                if (issueE.getEpicId() != null && !issueE.getEpicId().equals(0L)) {
                    //选择EPIC要生成日志
                    Long epicId = issueE.getEpicId();
                    issueE.setEpicId(null);
                    createIssueEpicLog(epicId, ConvertHelper.convert(issueE, IssueDO.class));
                }
            }

        } catch (Throwable throwable) {
            logger.info(ERROR_UPDATE, throwable);
            throw new CommonException(ERROR_UPDATE);
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
            SprintDO sprintDO = sprintMapper.selectByPrimaryKey(issueSprintRelE.getSprintId());
            createDataLog(issueSprintRelE.getProjectId(), issueSprintRelE.getIssueId(),
                    FIELD_SPRINT, null, issueSprintRelE.getSprintId().toString(), null, sprintDO.getSprintName());
        }
    }

    private void handleIssueDataLog(Object[] args) {
        IssueE issueE = null;
        List<String> field = null;
        for (Object arg : args) {
            if (arg instanceof IssueE) {
                issueE = (IssueE) arg;
            } else if (arg instanceof String[]) {
                field = Arrays.asList((String[]) arg);
            }
        }
        if (issueE != null && field != null && !field.isEmpty()) {
            IssueDO originIssueDO = issueMapper.selectByPrimaryKey(issueE.getIssueId());
            handleIssueEpicName(field, originIssueDO, issueE);
            handleIssueSummary(field, originIssueDO, issueE);
            handleDescription(field, originIssueDO, issueE);
            handlePriority(field, originIssueDO, issueE);
            handleAssignee(field, originIssueDO, issueE);
            handleReporter(field, originIssueDO, issueE);
            handleStoryPoints(field, originIssueDO, issueE);
            handleIssueEpic(field, originIssueDO, issueE);
            handleRemainTime(field, originIssueDO, issueE);
            handleStatus(field, originIssueDO, issueE);
            handleRank(field, originIssueDO, issueE);
            handleType(field, originIssueDO, issueE);
        }
    }

    private void handleType(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(TYPE_CODE) && !Objects.equals(originIssueDO.getTypeCode(), issueE.getTypeCode())) {
            String originTypeName = lookupValueMapper.selectNameByValueCode(originIssueDO.getTypeCode());
            String currentTypeName = lookupValueMapper.selectNameByValueCode(issueE.getTypeCode());
            DataLogE dataLogE = new DataLogE();
            dataLogE.setField(FIELD_ISSUETYPE);
            dataLogE.setIssueId(originIssueDO.getIssueId());
            dataLogE.setProjectId(originIssueDO.getProjectId());
            dataLogE.setOldString(originTypeName);
            dataLogE.setNewString(currentTypeName);
            dataLogRepository.create(dataLogE);
        }
    }

    private void handleRank(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(RANK_FIELD) && !Objects.equals(originIssueDO.getRank(), issueE.getRank())) {
            SprintNameDTO activeSprintName = sprintNameAssembler.doToDTO(issueMapper.queryActiveSprintNameByIssueId(originIssueDO.getIssueId()));
            Long sprintId = null;
            if (field.contains(SPRINT_ID_FIELD)) {
                sprintId = issueE.getSprintId();
            } else if (activeSprintName != null) {
                sprintId = activeSprintName.getSprintId();
            }
            if ((issueE.getOriginSprintId() != null && issueE.getSprintId().equals(issueE.getOriginSprintId()))) {
                createRankDataLog(sprintId, activeSprintName, originIssueDO, issueE.getRank());
            }
        }
    }

    private void createRankDataLog(Long sprintId, SprintNameDTO activeSprintName, IssueDO originIssueDO, String rank) {
        Boolean condition = (sprintId != null) && ((sprintId == 0 && activeSprintName == null) || (activeSprintName != null && sprintId.equals(activeSprintName.getSprintId())));
        if (condition && originIssueDO.getRank() != null && rank != null) {
            if (originIssueDO.getRank().compareTo(rank) < 0) {
                createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                        FIELD_RANK, null, RANK_HIGHER, null, null);
            } else {
                createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                        FIELD_RANK, null, RANK_LOWER, null, null);
            }
        }
    }


    private void handleStatus(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(STATUS_ID) && !Objects.equals(originIssueDO.getStatusId(), issueE.getStatusId())) {
            if (originIssueDO.getStatusId().equals(issueE.getStatusId())) {
                return;
            }
            IssueStatusDO originStatus = issueStatusMapper.selectByPrimaryKey(originIssueDO.getStatusId());
            IssueStatusDO currentStatus = issueStatusMapper.selectByPrimaryKey(issueE.getStatusId());
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_STATUS, originStatus.getName(), currentStatus.getName(),
                    originIssueDO.getStatusId().toString(),
                    issueE.getStatusId().toString());
            Boolean condition = (originStatus.getCompleted() != null && originStatus.getCompleted()) || (currentStatus.getCompleted() != null && currentStatus.getCompleted());
            if (condition) {
                dataLogResolution(originIssueDO.getProjectId(), originIssueDO.getIssueId(), originStatus, currentStatus);
            }
        }
    }

    private void handleRemainTime(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(REMAIN_TIME_FIELD) && (!Objects.equals(originIssueDO.getRemainingTime(), issueE.getRemainingTime()))) {
            handleCalculateRemainData(issueE, originIssueDO);
        }
    }

    private void handleCalculateRemainData(IssueE issueE, IssueDO originIssueDO) {
        String oldData;
        String newData;
        BigDecimal zero = new BigDecimal(0);
        if (issueE.getRemainingTime() != null && issueE.getRemainingTime().compareTo(zero) > 0) {
            oldData = originIssueDO.getRemainingTime() == null ? null : originIssueDO.getRemainingTime().toString();
            newData = issueE.getRemainingTime().toString();
        } else if (issueE.getRemainingTime() == null) {
            oldData = originIssueDO.getRemainingTime() == null ? null : originIssueDO.getRemainingTime().toString();
            newData = null;
        } else {
            oldData = originIssueDO.getRemainingTime() == null ? null : originIssueDO.getRemainingTime().toString();
            newData = zero.toString();
        }
        createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                FIELD_TIMEESTIMATE, oldData, newData, oldData, newData);
    }

    private void handleStoryPoints(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        Boolean condition = field.contains(STORY_POINTS_FIELD) && (!Objects.equals(originIssueDO.getStoryPoints(), issueE.getStoryPoints()));
        if (condition) {
            String oldString = null;
            String newString = null;
            if (originIssueDO.getStoryPoints() != null) {
                oldString = originIssueDO.getStoryPoints().toString();
            }
            if (issueE.getStoryPoints() != null) {
                newString = issueE.getStoryPoints().toString();
            }
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_STORY_POINTS, oldString, newString, null, null);
        }
    }

    private void handleReporter(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(REPORTER_ID_FIELD) && !Objects.equals(originIssueDO.getReporterId(), issueE.getReporterId())) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originIssueDO.getReporterId() != null && originIssueDO.getReporterId() != 0) {
                oldValue = originIssueDO.getReporterId().toString();
                oldString = userRepository.queryUserNameByOption(originIssueDO.getReporterId(), false).getRealName();
            }
            if (issueE.getReporterId() != 0) {
                newValue = issueE.getReporterId().toString();
                newString = userRepository.queryUserNameByOption(issueE.getReporterId(), false).getRealName();
            }
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_REPORTER, oldString, newString, oldValue, newValue);
        }
    }

    private void handleAssignee(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(ASSIGNEE_ID_FIELD) && !Objects.equals(originIssueDO.getAssigneeId(), issueE.getAssigneeId())) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originIssueDO.getAssigneeId() != null && originIssueDO.getAssigneeId() != 0) {
                oldValue = originIssueDO.getAssigneeId().toString();
                oldString = userRepository.queryUserNameByOption(originIssueDO.getAssigneeId(), false).getRealName();
            }
            if (issueE.getAssigneeId() != 0) {
                newValue = issueE.getAssigneeId().toString();
                newString = userRepository.queryUserNameByOption(issueE.getAssigneeId(), false).getRealName();
            }
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_ASSIGNEE, oldString, newString, oldValue, newValue);
        }
    }

    private void handlePriority(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(PRIORITY_CODE_FIELD) && !Objects.equals(originIssueDO.getPriorityCode(), issueE.getPriorityCode())) {
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_PRIORITY, lookupValueMapper.selectNameByValueCode(originIssueDO.getPriorityCode())
                    , lookupValueMapper.selectNameByValueCode(issueE.getPriorityCode()), null, null);
        }
    }

    private void handleDescription(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(DESCRIPTION) && !Objects.equals(originIssueDO.getDescription(), issueE.getDescription())) {
            if (!FIELD_DESCRIPTION_NULL.equals(issueE.getDescription())) {
                createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                        DESCRIPTION, originIssueDO.getDescription(), issueE.getDescription(), null, null);
            } else {
                createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                        DESCRIPTION, originIssueDO.getDescription(), null, null, null);
            }
        }
    }

    private void handleIssueSummary(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(SUMMARY_FIELD) && !Objects.equals(originIssueDO.getSummary(), issueE.getSummary())) {
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    SUMMARY_FIELD, originIssueDO.getSummary(), issueE.getSummary(), null, null);
        }
    }

    private void handleIssueEpicName(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(EPIC_NAME_FIELD) && !Objects.equals(originIssueDO.getEpicName(), issueE.getEpicName())) {
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(),
                    FIELD_EPIC_NAME, originIssueDO.getEpicName(), issueE.getEpicName(), null, null);
        }
    }

    private void handleIssueEpic(List<String> field, IssueDO originIssueDO, IssueE issueE) {
        if (field.contains(EPIC_ID_FIELD) && !Objects.equals(originIssueDO.getEpicId(), issueE.getEpicId())) {
            createIssueEpicLog(issueE.getEpicId(), originIssueDO);
        }
    }

    private void createIssueEpicLog(Long epicId, IssueDO originIssueDO) {
        ProjectInfoDO query = new ProjectInfoDO();
        query.setProjectId(originIssueDO.getProjectId());
        ProjectInfoDO projectInfoDO = projectInfoMapper.selectOne(query);
        if (projectInfoDO == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        if ((originIssueDO.getEpicId() == null || originIssueDO.getEpicId() == 0)) {
            dataLogCreateEpicId(epicId, originIssueDO, projectInfoDO);
        } else {
            dataLogChangeEpicId(epicId, originIssueDO, projectInfoDO);
        }
    }

    private void dataLogResolution(Long projectId, Long issueId, IssueStatusDO originStatus, IssueStatusDO currentStatus) {
        Boolean condition = (originStatus.getCompleted() == null || !originStatus.getCompleted()) || (currentStatus.getCompleted() == null || !currentStatus.getCompleted());
        if (condition) {
            String oldValue = null;
            String newValue = null;
            String oldString = null;
            String newString = null;
            if (originStatus.getCompleted()) {
                oldValue = originStatus.getId().toString();
                oldString = originStatus.getName();
            } else if (currentStatus.getCompleted()) {
                newValue = currentStatus.getId().toString();
                newString = currentStatus.getName();
            }
            createDataLog(projectId, issueId, FIELD_RESOLUTION, oldString, newString, oldValue, newValue);
        }
    }

    private void dataLogCreateEpicId(Long epicId, IssueDO originIssueDO, ProjectInfoDO projectInfoDO) {
        IssueDO issueEpic = queryIssueByIssueIdAndProjectId(originIssueDO.getProjectId(), epicId);
        if (issueEpic == null) {
            throw new CommonException(ERROR_EPIC_NOT_FOUND);
        } else {
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(), FIELD_EPIC_LINK,
                    null, projectInfoDO.getProjectCode() + "-" + issueEpic.getIssueNum(),
                    null, issueEpic.getIssueId().toString());
            createDataLog(originIssueDO.getProjectId(), epicId, FIELD_EPIC_CHILD,
                    null, projectInfoDO.getProjectCode() + "-" + originIssueDO.getIssueNum(),
                    null, originIssueDO.getIssueId().toString());
        }
    }

    private void dataLogChangeEpicId(Long epicId, IssueDO originIssueDO, ProjectInfoDO projectInfoDO) {
        IssueDO oldIssueEpic = queryIssueByIssueIdAndProjectId(originIssueDO.getProjectId(), originIssueDO.getEpicId());
        if (oldIssueEpic == null) {
            throw new CommonException(ERROR_EPIC_NOT_FOUND);
        } else {
            if (epicId == null || epicId == 0) {
                createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(), FIELD_EPIC_LINK,
                        projectInfoDO.getProjectCode() + "-" + oldIssueEpic.getIssueNum(),
                        null, oldIssueEpic.getIssueId().toString(), null);
            } else {
                IssueDO newIssueEpic = queryIssueByIssueIdAndProjectId(originIssueDO.getProjectId(), epicId);
                if (newIssueEpic == null) {
                    throw new CommonException(ERROR_EPIC_NOT_FOUND);
                } else {
                    createDataLog(originIssueDO.getProjectId(), originIssueDO.getIssueId(), FIELD_EPIC_LINK,
                            projectInfoDO.getProjectCode() + "-" + oldIssueEpic.getIssueNum(),
                            projectInfoDO.getProjectCode() + "-" + newIssueEpic.getIssueNum(),
                            oldIssueEpic.getIssueId().toString(), newIssueEpic.getIssueId().toString());
                    createDataLog(originIssueDO.getProjectId(), epicId, FIELD_EPIC_CHILD,
                            null, projectInfoDO.getProjectCode() + "-" + originIssueDO.getIssueNum(),
                            null, originIssueDO.getIssueId().toString());
                }
            }
            createDataLog(originIssueDO.getProjectId(), originIssueDO.getEpicId(), FIELD_EPIC_CHILD,
                    projectInfoDO.getProjectCode() + "-" + originIssueDO.getIssueNum(), null,
                    originIssueDO.getIssueId().toString(), null);
        }
    }

    private IssueDO queryIssueByIssueIdAndProjectId(Long projectId, Long issueId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setIssueId(issueId);
        issueDO.setProjectId(projectId);
        return issueMapper.selectOne(issueDO);
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
