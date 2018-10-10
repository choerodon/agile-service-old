package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.NoticeService;
import io.choerodon.agile.infra.dataobject.MessageDO;
import io.choerodon.agile.infra.dataobject.MessageDetailDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.NoticeDetailMapper;
import io.choerodon.agile.infra.mapper.NoticeMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
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

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeDetailMapper noticeDetailMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public List<MessageDTO> queryByProjectId(Long projectId) {
        List<MessageDO> result = new ArrayList<>();
        List<MessageDO> originMessageList = noticeMapper.selectAll();
        List<MessageDO> changeMessageList = noticeMapper.selectChangeMessageByProjectId(projectId);
        for (MessageDO messageDO : originMessageList) {
            int flag = 0;
            for (MessageDO changeMessageDO : changeMessageList) {
                if (messageDO.getEvent().equals(changeMessageDO.getEvent()) && messageDO.getNoticeType().equals(changeMessageDO.getNoticeType())) {
                    flag = 1;
                    result.add(changeMessageDO);
                    break;
                }
            }
            if (flag == 0) {
                result.add(messageDO);
            }
        }
        return ConvertHelper.convertList(result, MessageDTO.class);
    }

    @Override
    public void updateNotice(Long projectId, List<MessageDTO> messageDTOList) {
        for (MessageDTO messageDTO : messageDTOList) {
            MessageDetailDO messageDetailDO = new MessageDetailDO();
            messageDetailDO.setProjectId(projectId);
            messageDetailDO.setEnable(messageDTO.getEnable());
            messageDetailDO.setEvent(messageDTO.getEvent());
            messageDetailDO.setNoticeType(messageDTO.getNoticeType());
            messageDetailDO.setNoticeName(messageDTO.getNoticeName());
            messageDetailDO.setUser(messageDTO.getUser());
            if (noticeMapper.selectChangeMessageByDetail(projectId, messageDTO.getEvent(), messageDTO.getNoticeType()) == null) {
                if (noticeDetailMapper.insert(messageDetailDO) != 1) {
                    throw new CommonException("error.messageDetailDO.insert");
                }
            } else {
                messageDetailDO.setId(messageDTO.getId());
                messageDetailDO.setObjectVersionNumber(messageDTO.getObjectVersionNumber());
                if (noticeDetailMapper.updateByPrimaryKeySelective(messageDetailDO) != 1) {
                    throw new CommonException("error.messageDetailDO.update");
                }
            }
        }
    }

    private void addUsersByReporter(List<String> res, List<Long> result, IssueDTO issueDTO) {
        if (res.contains("reporter") && !result.contains(issueDTO.getReporterId())) {
            result.add(issueDTO.getReporterId());
        }
    }

    private void addUsersByAssigneer(List<String> res, List<Long> result, IssueDTO issueDTO) {
        if (res.contains("assigneer") && issueDTO.getAssigneeId() != null && !result.contains(issueDTO.getAssigneeId())) {
            result.add(issueDTO.getAssigneeId());
        }
    }

    private void addUsersByProjectOwner(Long projectId, List<String> res, List<Long> result) {
        if (res.contains("project_owner")) {
            RoleAssignmentSearchDTO roleAssignmentSearchDTO = new RoleAssignmentSearchDTO();
            Long roleId = null;
            List<RoleDTO> roleDTOS = userFeignClient.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchDTO).getBody();
            for (RoleDTO roleDTO : roleDTOS) {
                if ("role/project/default/project-owner".equals(roleDTO.getCode())) {
                    roleId = roleDTO.getId();
                    break;
                }
            }
            if (roleId != null) {
                Page<UserDTO> userDTOS = userFeignClient.pagingQueryUsersByRoleIdOnProjectLevel(0, 200,roleId, projectId, roleAssignmentSearchDTO).getBody();
                for (UserDTO userDTO : userDTOS) {
                    if (!result.contains(userDTO.getId())) {
                        result.add(userDTO.getId());
                    }
                }
            }
        }
    }

    private void addUsersByUsers (List<String> res, List<Long> result, String[] users) {
        if (res.contains("users") && users != null && users.length != 0) {
            for (String str : users) {
                if (!result.contains(Long.parseLong(str))) {
                    result.add(Long.parseLong(str));
                }
            }
        }
    }

    private String[] judgeUserType(MessageDO changeMessageDO, List<String> res) {
        String[] users = null;
        if (changeMessageDO.getEnable()) {
            res.add(changeMessageDO.getNoticeType());
            users = "users".equals(changeMessageDO.getNoticeType()) && changeMessageDO.getUser() != null && !"null".equals(changeMessageDO.getUser()) ? changeMessageDO.getUser().split(",") : null;
        }
        return users;
    }

    @Override
    public List<Long> queryUserIdsByProjectId(Long projectId, String event, IssueDTO issueDTO) {
        List<MessageDO> originMessageList = noticeMapper.selectByEvent(event);
        List<MessageDO> changeMessageList = noticeMapper.selectByProjectIdAndEvent(projectId, event);
        List<String> res = new ArrayList<>();
        String[] users = null;
        for (MessageDO messageDO : originMessageList) {
            int flag = 0;
            for (MessageDO changeMessageDO : changeMessageList) {
                if (messageDO.getEvent().equals(changeMessageDO.getEvent()) && messageDO.getNoticeType().equals(changeMessageDO.getNoticeType())) {
                    flag = 1;
                    users = judgeUserType(changeMessageDO, res);
                    break;
                }
            }
            if (flag == 0 && messageDO.getEnable()) {
                res.add(messageDO.getNoticeType());
            }
        }
        List<Long> result = new ArrayList<>();
        addUsersByReporter(res, result, issueDTO);
        addUsersByAssigneer(res, result, issueDTO);
        addUsersByProjectOwner(projectId, res, result);
        addUsersByUsers(res, result, users);
        return result;
    }
}
