package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.validator.FeedbackValidator;
import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.app.service.FeedbackService;
import io.choerodon.agile.app.service.IFeedbackService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.utils.SendEmailUtil;
import io.choerodon.agile.infra.dataobject.ApplicationDTO;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.FeedbackAttachmentMapper;
import io.choerodon.agile.infra.mapper.FeedbackMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private static final String FIELD_STATUS = "status";
    private static final String FEEDBACK_DOING_STATUS = "doing";
    private static final String FEEDBACK_DONE_STATUS = "done";

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private SendEmailUtil sendEmailUtil;

    @Autowired
    private FeedbackValidator feedbackValidator;

    @Autowired
    private IamFeignClient iamFeignClient;

    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private IFeedbackService iFeedbackService;


    private void initByToken(FeedbackDTO feedbackDTO) {
        ApplicationDTO applicationDTO = iamFeignClient.getApplicationByToken(new ApplicationDTO(feedbackDTO.getToken())).getBody();
        Long projectId = applicationDTO.getProjectId();
        Long organizationId = applicationDTO.getOrganizationId();
        Long applicationId = applicationDTO.getId();
        if (projectId == null || organizationId == null || applicationId == null) {
            throw new CommonException("error.POAId.isNull");
        }
        feedbackDTO.setProjectId(projectId);
        feedbackDTO.setOrganizationId(organizationId);
        feedbackDTO.setApplicationId(applicationId);
    }

    private void initFeedbackNum(FeedbackDTO feedbackDTO) {
        ProjectInfoDTO projectInfoDTO = projectInfoMapper.queryByProjectId(feedbackDTO.getProjectId());
        if (projectInfoDTO == null) {
            throw new CommonException("error.projectInfo.notFound");
        }
        Long feedbackMaxNum = projectInfoDTO.getFeedbackMaxNum();
        feedbackMaxNum++;
        feedbackDTO.setFeedbackNum(feedbackMaxNum.toString());
        projectInfoMapper.updateFeedbackMaxNum(feedbackDTO.getProjectId(), feedbackMaxNum);
    }

    @Override
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        feedbackValidator.checkFeedbackCreate(feedbackDTO);
        initByToken(feedbackDTO);
        initFeedbackNum(feedbackDTO);
        feedbackDTO.setStatus("todo");
        return createBase(feedbackDTO);
    }


    private void feedbackUpdate(FeedbackUpdateVO feedbackUpdateVO, List<String> fieldList) {
        FeedbackDTO result = iFeedbackService.updateBase(feedbackUpdateVO, fieldList.toArray(new String[fieldList.size()]));
        // 反馈处理后发送邮件
        if (fieldList.contains(FIELD_STATUS) && FEEDBACK_DOING_STATUS.equals(feedbackUpdateVO.getStatus()) && result.getEmail() != null) {
            sendEmailUtil.feedbackDoing(result.getEmail(), result.getSummary(), result.getDescription());
        }
        // 反馈完成后发送邮件
        if (fieldList.contains(FIELD_STATUS) && FEEDBACK_DONE_STATUS.equals(feedbackUpdateVO.getStatus()) && result.getEmail() != null) {
            sendEmailUtil.feedbackDone(result.getEmail(), result.getSummary(), result.getDescription());
        }
    }

    @Override
    public FeedbackDTO updateFeedback(Long projectId, FeedbackUpdateVO feedbackUpdateVO, List<String> fieldList) {
        feedbackValidator.checkFeedbackUpdate(feedbackUpdateVO);
        if (!fieldList.isEmpty()) {
            feedbackUpdate(feedbackUpdateVO, fieldList);
        }
        return feedbackMapper.selectByPrimaryKey(feedbackUpdateVO.getId());
    }

    @Override
    public PageInfo<FeedbackDTO> queryFeedbackByPage(Long projectId, int page, int size, SearchVO searchVO) {
        PageInfo<FeedbackDTO> feedbackDTOPageInfo = PageHelper.startPage(page, size).doSelectPageInfo(() -> feedbackMapper.selectByPage(projectId, searchVO));
        if (feedbackDTOPageInfo.getList() != null && !feedbackDTOPageInfo.getList().isEmpty()) {
            List<Long> assigneeIds = feedbackDTOPageInfo.getList().stream().map(FeedbackDTO::getAssigneeId).collect(Collectors.toList());
            Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
                    assigneeIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), false);
            for (FeedbackDTO feedbackDTO : feedbackDTOPageInfo.getList()) {
                feedbackDTO.setAssignee(userMessageDOMap.get(feedbackDTO.getAssigneeId()));
            }
        }
        return feedbackDTOPageInfo;
    }

    @Override
    public FeedbackDTO queryFeedbackById(Long projectId, Long organizationId, Long id) {
        FeedbackDTO feedbackDTO = feedbackMapper.selectById(projectId, id);
        feedbackDTO.setFeedbackAttachmentDTOList(feedbackAttachmentMapper.selectByFeedbackId(projectId, id, "feedback"));
        List<Long> assigneeIds = new ArrayList<>();
        assigneeIds.add(feedbackDTO.getAssigneeId());
        Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
                assigneeIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), false);
        feedbackDTO.setAssignee(userMessageDOMap.get(feedbackDTO.getAssigneeId()));
        feedbackDTO.setApplicationDTO(iamFeignClient.queryByApplicationId(organizationId, feedbackDTO.getApplicationId(), false).getBody());
        return feedbackDTO;
    }

    @Override
    public FeedbackDTO createBase(FeedbackDTO feedbackDTO) {
        if (feedbackMapper.insert(feedbackDTO) != 1) {
            throw new CommonException("error.feedback.insert");
        }
        return feedbackMapper.selectByPrimaryKey(feedbackDTO.getId());
    }

}
