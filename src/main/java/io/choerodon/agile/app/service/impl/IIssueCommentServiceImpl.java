package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IIssueCommentService;
import io.choerodon.agile.infra.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.IssueCommentDTO;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class IIssueCommentServiceImpl implements IIssueCommentService {

    private static final String UPDATE_ERROR = "error.IssueComment.update";
    private static final String INSERT_ERROR = "error.IssueComment.insert";
    private static final String DELETE_ERROR = "error.IssueComment.delete";

    @Autowired
    private IssueCommentMapper issueCommentMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    @DataLog(type = "createComment")
    public IssueCommentDTO createBase(IssueCommentDTO issueCommentDTO) {
        if (issueCommentMapper.insert(issueCommentDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return modelMapper.map(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentDTO.class);
    }

    @Override
    @DataLog(type = "updateComment")
    public IssueCommentDTO updateBase(IssueCommentDTO issueCommentDTO, String[] fieldList) {
        Criteria criteria = new Criteria();
        criteria.update(fieldList);
        if (issueCommentMapper.updateByPrimaryKeyOptions(issueCommentDTO, criteria) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return modelMapper.map(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentDTO.class);
    }

    @Override
    @DataLog(type = "deleteComment")
    public int deleteBase(IssueCommentDTO issueCommentDTO) {
        int isDelete = issueCommentMapper.delete(issueCommentDTO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }
}
