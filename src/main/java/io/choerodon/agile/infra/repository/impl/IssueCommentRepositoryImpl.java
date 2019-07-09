package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;
import io.choerodon.agile.infra.repository.IssueCommentRepository;
import io.choerodon.agile.infra.dataobject.IssueCommentDTO;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
import io.choerodon.mybatis.entity.Criteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Component
public class IssueCommentRepositoryImpl implements IssueCommentRepository {

    private static final String UPDATE_ERROR = "error.IssueComment.update";
    private static final String INSERT_ERROR = "error.IssueComment.insert";
    private static final String DELETE_ERROR = "error.IssueComment.delete";

    @Autowired
    private IssueCommentMapper issueCommentMapper;

    @Override
    @DataLog(type = "updateComment")
    public IssueCommentE update(IssueCommentE issueCommentE, String[] fieldList) {
        IssueCommentDTO issueCommentDTO = ConvertHelper.convert(issueCommentE, IssueCommentDTO.class);
        Criteria criteria = new Criteria();
        criteria.update(fieldList);
        if (issueCommentMapper.updateByPrimaryKeyOptions(issueCommentDTO, criteria) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentE.class);
    }

    @Override
    @DataLog(type = "createComment")
    public IssueCommentE create(IssueCommentE issueCommentE) {
        IssueCommentDTO issueCommentDTO = ConvertHelper.convert(issueCommentE, IssueCommentDTO.class);
        if (issueCommentMapper.insert(issueCommentDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueCommentMapper.selectByPrimaryKey(issueCommentDTO.getCommentId()), IssueCommentE.class);
    }

    @Override
    @DataLog(type = "deleteComment")
    public int delete(IssueCommentDTO issueCommentDTO) {
        int isDelete = issueCommentMapper.delete(issueCommentDTO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }
}