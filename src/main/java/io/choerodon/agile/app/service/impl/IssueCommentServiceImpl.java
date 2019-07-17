package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.vo.IssueCommentCreateVO;
import io.choerodon.agile.api.vo.IssueCommentUpdateVO;
import io.choerodon.agile.api.vo.IssueCommentVO;
import io.choerodon.agile.app.assembler.IssueCommentAssembler;
import io.choerodon.agile.app.service.IIssueCommentService;
import io.choerodon.agile.app.service.IssueCommentService;
import io.choerodon.agile.infra.dataobject.IssueCommentDTO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueCommentServiceImpl implements IssueCommentService {

    @Autowired
    private IssueCommentAssembler issueCommentAssembler;
    @Autowired
    private IssueCommentMapper issueCommentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private IIssueCommentService iIssueCommentService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public IssueCommentVO createIssueComment(Long projectId, IssueCommentCreateVO issueCommentCreateVO) {
        IssueCommentDTO issueCommentDTO = issueCommentAssembler.toTarget(issueCommentCreateVO, IssueCommentDTO.class);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        issueCommentDTO.setUserId(customUserDetails.getUserId());
        issueCommentDTO.setProjectId(projectId);
        return queryByProjectIdAndCommentId(projectId, iIssueCommentService.createBase(issueCommentDTO).getCommentId());
    }

    @Override
    public IssueCommentVO updateIssueComment(IssueCommentUpdateVO issueCommentUpdateVO, List<String> fieldList, Long projectId) {
        if (fieldList != null && !fieldList.isEmpty()) {
            IssueCommentDTO issueCommentDTO = issueCommentAssembler.toTarget(issueCommentUpdateVO, IssueCommentDTO.class);
            iIssueCommentService.updateBase(issueCommentDTO, fieldList.toArray(new String[fieldList.size()]));
            return queryByProjectIdAndCommentId(projectId, issueCommentDTO.getCommentId());
        } else {
            return null;
        }
    }

    @Override
    public List<IssueCommentVO> queryIssueCommentList(Long projectId, Long issueId) {
        return modelMapper.map(issueCommentMapper.queryIssueCommentList(projectId, issueId), new TypeToken<List<IssueCommentVO>>(){}.getType());
    }

    private IssueCommentDTO getCommentById(Long projectId, Long commentId) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        issueCommentDTO.setProjectId(projectId);
        issueCommentDTO.setCommentId(commentId);
        issueCommentDTO = issueCommentMapper.selectOne(issueCommentDTO);
        if (issueCommentDTO == null) {
            throw new CommonException("error.comment.get");
        }
        return issueCommentDTO;
    }

    @Override
    public int deleteIssueComment(Long projectId, Long commentId) {
        IssueCommentDTO issueCommentDTO = getCommentById(projectId, commentId);
        return iIssueCommentService.deleteBase(issueCommentDTO);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        issueCommentDTO.setIssueId(issueId);
        return issueCommentMapper.delete(issueCommentDTO);
    }

    private IssueCommentVO queryByProjectIdAndCommentId(Long projectId, Long commentId) {
        IssueCommentDTO issueCommentDTO = new IssueCommentDTO();
        issueCommentDTO.setProjectId(projectId);
        issueCommentDTO.setCommentId(commentId);
        IssueCommentVO issueCommentVO = modelMapper.map(issueCommentMapper.selectOne(issueCommentDTO), IssueCommentVO.class);
        issueCommentVO.setUserName(userService.queryUserNameByOption(issueCommentVO.getUserId(), true).getRealName());
        issueCommentVO.setUserImageUrl(userService.queryUserNameByOption(issueCommentVO.getUserId(), true).getImageUrl());
        return modelMapper.map(issueCommentMapper.selectOne(issueCommentDTO), IssueCommentVO.class);
    }

//    @DataLog(type = "createComment")
//    public IssueCommentDTO create(IssueCommentDTO issueCommentDTO) {
//        if (issueCommentMapper.insert(issueCommentDTO) != 1) {
//            throw new CommonException(INSERT_ERROR);
//        }
//        return modelMapper.map(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentDTO.class);
//    }
//
//    @DataLog(type = "updateComment")
//    public IssueCommentDTO update(IssueCommentDTO issueCommentDTO, String[] fieldList) {
//        Criteria criteria = new Criteria();
//        criteria.update(fieldList);
//        if (issueCommentMapper.updateByPrimaryKeyOptions(issueCommentDTO, criteria) != 1) {
//            throw new CommonException(UPDATE_ERROR);
//        }
//        return modelMapper.map(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentDTO.class);
//    }
//
//    @DataLog(type = "deleteComment")
//    public int delete(IssueCommentDTO issueCommentDTO) {
//        int isDelete = issueCommentMapper.delete(issueCommentDTO);
//        if (isDelete != 1) {
//            throw new CommonException(DELETE_ERROR);
//        }
//        return isDelete;
//    }
}