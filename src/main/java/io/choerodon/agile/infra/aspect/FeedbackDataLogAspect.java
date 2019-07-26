package io.choerodon.agile.infra.aspect;

import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.app.service.LookupValueService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.annotation.FeedbackDataLog;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.agile.infra.dataobject.FeedbackDataLogDTO;
import io.choerodon.agile.infra.mapper.FeedbackAttachmentMapper;
import io.choerodon.agile.infra.mapper.FeedbackCommentMapper;
import io.choerodon.agile.infra.mapper.FeedbackDataLogMapper;
import io.choerodon.agile.infra.mapper.FeedbackMapper;
import io.choerodon.core.exception.CommonException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
@Aspect
@Transactional(rollbackFor = Exception.class)
public class FeedbackDataLogAspect {

    private static final String ERROR_METHOD_EXECUTE = "error.dataLogEpic.methodExecute";
    private static final String FEEDBACK = "feedback";
    private static final String STATUS = "status";
    private static final String TYPE = "type";
    private static final String SUMMARY = "summary";
    private static final String ASSIGNEE_ID = "assigneeId";
    private static final String DESCRIPTION = "description";
    private static final String UPDATE_COMMENT = "updateComment";
    private static final String CREATE_COMMENT = "createComment";
    private static final String DELETE_COMMENT = "deleteComment";
    private static final String CREATE_ATTACHMENT = "createAttachment";
    private static final String DELETE_ATTACHMENT = "deleteAttachment";
    private static final String FIELD_STATUS = "Status";
    private static final String FIELD_TYPE = "Type";
    private static final String FIELD_SUMMARY = "Summary";
    private static final String FIELD_ASSIGNEE = "Assignee";
    private static final String FIELD_DESCRIPTION = "Description";
    private static final String FIELD_DESCRIPTION_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_COMMENT_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_COMMENT = "Comment";
    private static final String FIELD_ATTACHMENT = "Attachment";

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private FeedbackDataLogMapper feedbackDataLogMapper;

    @Autowired
    private LookupValueService lookupValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private FeedbackCommentMapper feedbackCommentMapper;

    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;

    @Pointcut("bean(*ServiceImpl) && @annotation(io.choerodon.agile.infra.annotation.FeedbackDataLog)")
    public void feedbackPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("feedbackPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Object result = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        FeedbackDataLog feedbackDataLog = method.getAnnotation(FeedbackDataLog.class);
        Object[] args = pjp.getArgs();
        if (feedbackDataLog != null && args != null) {
            switch (feedbackDataLog.type()) {
                case FEEDBACK:
                    dealFeedbackDataLog(args);
                    break;
                case CREATE_COMMENT:
                    result = dealFeedbackCommentCreate(args, pjp);
                    break;
                case UPDATE_COMMENT:
                    dealFeedbackCommentUpdate(args);
                    break;
                case DELETE_COMMENT:
                    dealFeedbackCommentDelete(args);
                    break;
                case CREATE_ATTACHMENT:
                    result = dealFeedbackAttachmentCreate(args, pjp);
                    break;
                case DELETE_ATTACHMENT:
                    dealFeedbackAttachmentDelete(args);
                    break;
                default:
                    break;
            }
        }
        try {
            if (result == null) {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            throw new CommonException(ERROR_METHOD_EXECUTE, e);
        }
        return result;
    }


    private void dealFeedbackDataLog(Object[] args) {
        FeedbackUpdateVO feedbackUpdateVO = null;
        List<String> fieldList = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackUpdateVO) {
                feedbackUpdateVO = (FeedbackUpdateVO) arg;
            } else if (arg instanceof String[]) {
                fieldList = Arrays.asList((String[]) arg);
            }
        }
        if (feedbackUpdateVO != null && fieldList != null && !fieldList.isEmpty()) {
            FeedbackDTO originFeedbackDTO = feedbackMapper.selectByPrimaryKey(feedbackUpdateVO.getId());
            dealFeedbackStatus(originFeedbackDTO, feedbackUpdateVO, fieldList);
            dealFeedbackType(originFeedbackDTO, feedbackUpdateVO, fieldList);
            dealFeedbackSummary(originFeedbackDTO, feedbackUpdateVO, fieldList);
            dealFeedbackAssignee(originFeedbackDTO, feedbackUpdateVO, fieldList);
            dealFeedbackDescription(originFeedbackDTO, feedbackUpdateVO, fieldList);
        }
    }

    private void dealFeedbackStatus(FeedbackDTO originFeedbackDTO, FeedbackUpdateVO newFeedbackDTO, List<String> fieldList) {
        if (fieldList.contains(STATUS) && !Objects.equals(originFeedbackDTO.getStatus(), newFeedbackDTO.getStatus())) {
            Map<String, String> statusMap = lookupValueService.queryMapByTypeCode("feedback_status_category");
            createDataLog(originFeedbackDTO.getProjectId(), FIELD_STATUS, originFeedbackDTO.getStatus(), statusMap.get(originFeedbackDTO.getStatus()),
                    newFeedbackDTO.getStatus(), statusMap.get(newFeedbackDTO.getStatus()), originFeedbackDTO.getId());
        }
    }

    private void dealFeedbackType(FeedbackDTO originFeedbackDTO, FeedbackUpdateVO newFeedbackDTO, List<String> fieldList) {
        if (fieldList.contains(TYPE) && !Objects.equals(originFeedbackDTO.getType(), newFeedbackDTO.getType())) {
            Map<String, String> typeMap = lookupValueService.queryMapByTypeCode("feedback_type_category");
            createDataLog(originFeedbackDTO.getProjectId(), FIELD_TYPE, originFeedbackDTO.getType(), typeMap.get(originFeedbackDTO.getType()),
                    newFeedbackDTO.getType(), typeMap.get(newFeedbackDTO.getType()), originFeedbackDTO.getId());
        }
    }

    private void dealFeedbackSummary(FeedbackDTO originFeedbackDTO, FeedbackUpdateVO newFeedbackDTO, List<String> fieldList) {
        if (fieldList.contains(SUMMARY) && !Objects.equals(originFeedbackDTO.getSummary(), newFeedbackDTO.getSummary())) {
            createDataLog(originFeedbackDTO.getProjectId(), FIELD_SUMMARY, null, originFeedbackDTO.getSummary(),
                    null, newFeedbackDTO.getSummary(), originFeedbackDTO.getId());
        }
    }

    private void dealFeedbackAssignee(FeedbackDTO originFeedbackDTO, FeedbackUpdateVO newFeedbackDTO, List<String> fieldList) {
        if (fieldList.contains(ASSIGNEE_ID) && !Objects.equals(originFeedbackDTO.getAssigneeId(), newFeedbackDTO.getAssigneeId())) {
            String oldvalue = null;
            String oldString = null;
            String newValue = null;
            String newString = null;
            if (originFeedbackDTO.getAssigneeId() != null && !Objects.equals(originFeedbackDTO.getAssigneeId(), 0L)) {
                oldvalue = originFeedbackDTO.getAssigneeId().toString();
                oldString = userService.queryUserNameByOption(originFeedbackDTO.getAssigneeId(), false).getRealName();
            }
            if (newFeedbackDTO.getAssigneeId() != null && !Objects.equals(newFeedbackDTO.getAssigneeId(), 0L)) {
                newValue = newFeedbackDTO.getAssigneeId().toString();
                newString = userService.queryUserNameByOption(newFeedbackDTO.getAssigneeId(), false).getRealName();
            }
            createDataLog(originFeedbackDTO.getProjectId(), FIELD_ASSIGNEE, oldvalue, oldString,
                    newValue, newString, originFeedbackDTO.getId());
        }
    }

    private void dealFeedbackDescription(FeedbackDTO originFeedbackDTO, FeedbackUpdateVO newFeedbackDTO, List<String> fieldList) {
        if (fieldList.contains(DESCRIPTION) && !Objects.equals(originFeedbackDTO.getDescription(), newFeedbackDTO.getDescription())) {
            String oldString = null;
            String newString = null;
            if (!FIELD_DESCRIPTION_NULL.equals(originFeedbackDTO.getDescription())) {
                oldString = originFeedbackDTO.getDescription();
            }
            if (!FIELD_DESCRIPTION_NULL.equals(newFeedbackDTO.getDescription())) {
                newString = newFeedbackDTO.getDescription();
            }
            createDataLog(originFeedbackDTO.getProjectId(), FIELD_DESCRIPTION, null, oldString, null, newString, originFeedbackDTO.getId());
        }
    }


    private void dealFeedbackCommentUpdate(Object[] args) {
        FeedbackCommentDTO feedbackCommentDTO = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackCommentDTO) {
                feedbackCommentDTO = (FeedbackCommentDTO) arg;
            }
        }
        if (feedbackCommentDTO != null) {
            FeedbackCommentDTO originFeedbackCommentDTO = feedbackCommentMapper.selectByPrimaryKey(feedbackCommentDTO.getId());
            if (!Objects.equals(originFeedbackCommentDTO.getContent(), feedbackCommentDTO.getContent())) {
                String oldString = null;
                String newString = null;
                if (!Objects.equals(originFeedbackCommentDTO.getContent(), FIELD_COMMENT_NULL)) {
                    oldString = originFeedbackCommentDTO.getContent();
                }
                if (!Objects.equals(feedbackCommentDTO.getContent(), FIELD_COMMENT_NULL)) {
                    newString = feedbackCommentDTO.getContent();
                }
                createDataLog(originFeedbackCommentDTO.getProjectId(), FIELD_COMMENT, originFeedbackCommentDTO.getId().toString(), oldString,
                        feedbackCommentDTO.getId().toString(), newString, originFeedbackCommentDTO.getFeedbackId());
            }
        }
    }

    private Object dealFeedbackCommentCreate(Object[] args, ProceedingJoinPoint pjp) {
        FeedbackCommentDTO feedbackCommentDTO = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackCommentDTO) {
                feedbackCommentDTO = (FeedbackCommentDTO) arg;
            }
        }
        if (feedbackCommentDTO != null) {
            try {
                result = pjp.proceed();
                feedbackCommentDTO = (FeedbackCommentDTO) result;
                createDataLog(feedbackCommentDTO.getProjectId(), FIELD_COMMENT, null, null,
                        feedbackCommentDTO.getId().toString(), feedbackCommentDTO.getContent(), feedbackCommentDTO.getFeedbackId());
            } catch (Throwable e) {
                throw new CommonException(ERROR_METHOD_EXECUTE, e);
            }
        }
        return result;
    }

    private void dealFeedbackCommentDelete(Object[] args) {
        FeedbackCommentDTO feedbackCommentDTO = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackCommentDTO) {
                feedbackCommentDTO = (FeedbackCommentDTO) arg;
            }
        }
        if (feedbackCommentDTO != null) {
            FeedbackCommentDTO originFeedbackCommentDTO = feedbackCommentMapper.selectByPrimaryKey(feedbackCommentDTO.getId());
            String oldString = null;
            if (!Objects.equals(originFeedbackCommentDTO.getContent(), FIELD_COMMENT_NULL)) {
                oldString = originFeedbackCommentDTO.getContent();
            }
            createDataLog(feedbackCommentDTO.getProjectId(), FIELD_COMMENT, feedbackCommentDTO.getId().toString(), oldString,
                    null, null, originFeedbackCommentDTO.getFeedbackId());
        }
    }

    private Object dealFeedbackAttachmentCreate(Object[] args, ProceedingJoinPoint pjp) {
        FeedbackAttachmentDTO feedbackAttachmentDTO = null;
        Object result = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackAttachmentDTO) {
                feedbackAttachmentDTO = (FeedbackAttachmentDTO) arg;
            }
        }
        if (feedbackAttachmentDTO != null) {
            try {
                result = pjp.proceed();
                feedbackAttachmentDTO = (FeedbackAttachmentDTO) result;
                createDataLog(feedbackAttachmentDTO.getProjectId(), FIELD_ATTACHMENT, null, null,
                        feedbackAttachmentDTO.getId().toString(), feedbackAttachmentDTO.getFileName(), feedbackAttachmentDTO.getFeedbackId());
            } catch (Throwable e) {
                throw new CommonException(ERROR_METHOD_EXECUTE, e);
            }
        }
        return result;
    }

    private void dealFeedbackAttachmentDelete(Object[] args) {
        FeedbackAttachmentDTO feedbackAttachmentDTO = null;
        for (Object arg : args) {
            if (arg instanceof FeedbackAttachmentDTO) {
                feedbackAttachmentDTO = (FeedbackAttachmentDTO) arg;
            }
        }
        if (feedbackAttachmentDTO != null) {
            FeedbackAttachmentDTO originFeedbackAttachmentDTO = feedbackAttachmentMapper.selectByPrimaryKey(feedbackAttachmentDTO.getId());
            createDataLog(feedbackAttachmentDTO.getProjectId(), FIELD_ATTACHMENT, originFeedbackAttachmentDTO.getId().toString(), originFeedbackAttachmentDTO.getFileName(),
                    null, null, originFeedbackAttachmentDTO.getFeedbackId());

        }
    }

    private void createDataLog(Long projectId, String field, String oldValue, String oldString,
                               String newValue, String newString, Long feedbackId) {
        FeedbackDataLogDTO feedbackDataLogDTO = new FeedbackDataLogDTO();
        feedbackDataLogDTO.setProjectId(projectId);
        feedbackDataLogDTO.setField(field);
        feedbackDataLogDTO.setOldValue(oldValue);
        feedbackDataLogDTO.setOldString(oldString);
        feedbackDataLogDTO.setNewValue(newValue);
        feedbackDataLogDTO.setNewString(newString);
        feedbackDataLogDTO.setFeedbackId(feedbackId);
        if (feedbackDataLogMapper.insert(feedbackDataLogDTO) != 1) {
            throw new CommonException("error.dataLog.insert");
        }
    }

}
