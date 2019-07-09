package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.assembler.NoticeMessageAssembler;
import io.choerodon.agile.app.service.NoticeService;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.MessageDO;
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
    private UserRepository userRepository;

    @Autowired
    private NoticeMessageAssembler noticeMessageAssembler;

    private void getIds(List<MessageDO> result, List<Long> ids) {
        for (MessageDO messageDO : result) {
            if (USERS.equals(messageDO.getNoticeType()) && messageDO.getEnable() && messageDO.getUser() != null && messageDO.getUser().length() != 0 && !"null".equals(messageDO.getUser())) {
                String[] strs = messageDO.getUser().split(",");
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
        List<Long> ids = new ArrayList<>();
        getIds(result, ids);
        return noticeMessageAssembler.messageDOToIDTO(result, ids);
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
            RoleAssignmentSearchDTO roleAssignmentSearchDTO = new RoleAssignmentSearchDTO();
            Long roleId = null;
            List<RoleDTO> roleDTOS = userRepository.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchDTO);
            for (RoleDTO roleDTO : roleDTOS) {
                if ("role/project/default/project-owner".equals(roleDTO.getCode())) {
                    roleId = roleDTO.getId();
                    break;
                }
            }
            if (roleId != null) {
                PageInfo<UserDTO> userDTOS = userRepository.pagingQueryUsersByRoleIdOnProjectLevel(0, 300,roleId, projectId, roleAssignmentSearchDTO);
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


    private String[] judgeUserType(MessageDO changeMessageDO, List<String> res) {
        String[] users = null;
        if (changeMessageDO.getEnable()) {
            res.add(changeMessageDO.getNoticeType());
            users = USERS.equals(changeMessageDO.getNoticeType()) && changeMessageDO.getUser() != null && changeMessageDO.getUser().length() != 0 && !"null".equals(changeMessageDO.getUser()) ? changeMessageDO.getUser().split(",") : null;
        }
        return users;
    }

    @Override
    public List<Long> queryUserIdsByProjectId(Long projectId, String event, IssueVO issueVO) {
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
        addUsersByReporter(res, result, issueVO);
        addUsersByAssigneer(res, result, issueVO, event);
        addUsersByProjectOwner(projectId, res, result);
        addUsersByUsers(res, result, users);
        return result;
    }
}
