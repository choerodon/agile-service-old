package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.assembler.NoticeMessageAssembler;
import io.choerodon.agile.app.service.NoticeService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.MessageDTO;
import io.choerodon.agile.infra.dataobject.MessageDetailDO;
import io.choerodon.agile.infra.mapper.NoticeDetailMapper;
import io.choerodon.agile.infra.mapper.NoticeMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final String USERS = "users";

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeDetailMapper noticeDetailMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeMessageAssembler noticeMessageAssembler;

    private void getIds(List<MessageDTO> result, List<Long> ids) {
        for (MessageDTO messageDTO : result) {
            if (USERS.equals(messageDTO.getNoticeType()) && messageDTO.getEnable() && messageDTO.getUser() != null && messageDTO.getUser().length() != 0 && !"null".equals(messageDTO.getUser())) {
                String[] strs = messageDTO.getUser().split(",");
                for (String str : strs) {
                    Long id = Long.parseLong(str);
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                }
            }
        }
    }

    @Override
    public List<MessageVO> queryByProjectId(Long projectId) {
        List<MessageDTO> result = new ArrayList<>();
        List<MessageDTO> originMessageList = noticeMapper.selectAll();
        List<MessageDTO> changeMessageList = noticeMapper.selectChangeMessageByProjectId(projectId);
        for (MessageDTO messageDTO : originMessageList) {
            int flag = 0;
            for (MessageDTO changeMessageDTO : changeMessageList) {
                if (messageDTO.getEvent().equals(changeMessageDTO.getEvent()) && messageDTO.getNoticeType().equals(changeMessageDTO.getNoticeType())) {
                    flag = 1;
                    result.add(changeMessageDTO);
                    break;
                }
            }
            if (flag == 0) {
                result.add(messageDTO);
            }
        }
        List<Long> ids = new ArrayList<>();
        getIds(result, ids);
        return noticeMessageAssembler.messageDOToIDTO(result, ids);
    }

    @Override
    public void updateNotice(Long projectId, List<MessageVO> messageVOList) {
        for (MessageVO messageVO : messageVOList) {
            MessageDetailDO messageDetailDO = new MessageDetailDO();
            messageDetailDO.setProjectId(projectId);
            messageDetailDO.setEnable(messageVO.getEnable());
            messageDetailDO.setEvent(messageVO.getEvent());
            messageDetailDO.setNoticeType(messageVO.getNoticeType());
            messageDetailDO.setNoticeName(messageVO.getNoticeName());
            messageDetailDO.setUser(messageVO.getUser());
            if (noticeMapper.selectChangeMessageByDetail(projectId, messageVO.getEvent(), messageVO.getNoticeType()) == null) {
                if (noticeDetailMapper.insert(messageDetailDO) != 1) {
                    throw new CommonException("error.messageDetailDO.insert");
                }
            } else {
                messageDetailDO.setId(messageVO.getId());
                messageDetailDO.setObjectVersionNumber(messageVO.getObjectVersionNumber());
                if (noticeDetailMapper.updateByPrimaryKeySelective(messageDetailDO) != 1) {
                    throw new CommonException("error.messageDetailDO.update");
                }
            }
        }
    }

    private void addUsersByReporter(List<String> res, List<Long> result, IssueVO issueVO) {
        if (res.contains("reporter") && !result.contains(issueVO.getReporterId())) {
            result.add(issueVO.getReporterId());
        }
    }

    private void addUsersByAssigneer(List<String> res, List<Long> result, IssueVO issueVO, String event) {
        if (res.contains("assigneer") && issueVO.getAssigneeId() != null && !result.contains(issueVO.getAssigneeId()) && !"issue_created".equals(event)) {
            result.add(issueVO.getAssigneeId());
        }
    }

    private void addUsersByProjectOwner(Long projectId, List<String> res, List<Long> result) {
        if (res.contains("project_owner")) {
            RoleAssignmentSearchVO roleAssignmentSearchVO = new RoleAssignmentSearchVO();
            Long roleId = null;
            List<RoleVO> roleVOS = userService.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchVO);
            for (RoleVO roleVO : roleVOS) {
                if ("role/project/default/project-owner".equals(roleVO.getCode())) {
                    roleId = roleVO.getId();
                    break;
                }
            }
            if (roleId != null) {
                PageInfo<UserDTO> userDTOS = userService.pagingQueryUsersByRoleIdOnProjectLevel(0, 300,roleId, projectId, roleAssignmentSearchVO);
                for (UserDTO userDTO : userDTOS.getList()) {
                    if (!result.contains(userDTO.getId())) {
                        result.add(userDTO.getId());
                    }
                }
            }
        }
    }

    private void addUsersByUsers (List<String> res, List<Long> result, String[] users) {
        if (res.contains(USERS) && users != null && users.length != 0) {
            for (String str : users) {
                if (!result.contains(Long.parseLong(str))) {
                    result.add(Long.parseLong(str));
                }
            }
        }
    }


    private String[] judgeUserType(MessageDTO changeMessageDTO, List<String> res) {
        String[] users = null;
        if (changeMessageDTO.getEnable()) {
            res.add(changeMessageDTO.getNoticeType());
            users = USERS.equals(changeMessageDTO.getNoticeType()) && changeMessageDTO.getUser() != null && changeMessageDTO.getUser().length() != 0 && !"null".equals(changeMessageDTO.getUser()) ? changeMessageDTO.getUser().split(",") : null;
        }
        return users;
    }

    @Override
    public List<Long> queryUserIdsByProjectId(Long projectId, String event, IssueVO issueVO) {
        List<MessageDTO> originMessageList = noticeMapper.selectByEvent(event);
        List<MessageDTO> changeMessageList = noticeMapper.selectByProjectIdAndEvent(projectId, event);
        List<String> res = new ArrayList<>();
        String[] users = null;
        for (MessageDTO messageDTO : originMessageList) {
            int flag = 0;
            for (MessageDTO changeMessageDTO : changeMessageList) {
                if (messageDTO.getEvent().equals(changeMessageDTO.getEvent()) && messageDTO.getNoticeType().equals(changeMessageDTO.getNoticeType())) {
                    flag = 1;
                    users = judgeUserType(changeMessageDTO, res);
                    break;
                }
            }
            if (flag == 0 && messageDTO.getEnable()) {
                res.add(messageDTO.getNoticeType());
            }
        }
        List<Long> result = new ArrayList<>();
        addUsersByReporter(res, result, issueVO);
        addUsersByAssigneer(res, result, issueVO, event);
        addUsersByProjectOwner(projectId, res, result);
        addUsersByUsers(res, result, users);
        return result;
    }
}
