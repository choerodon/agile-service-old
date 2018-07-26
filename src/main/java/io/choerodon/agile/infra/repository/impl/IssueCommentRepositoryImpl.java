package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.service.IIssueCommentService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;
import io.choerodon.agile.domain.agile.repository.IssueCommentRepository;
import io.choerodon.agile.infra.dataobject.IssueCommentDO;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
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

    @Autowired
    private IIssueCommentService iIssueCommentService;

    @Override
    @DataLog(type = "updateComment")
    public IssueCommentE update(IssueCommentE issueCommentE, String[] fieldList) {
        IssueCommentDO issueCommentDO = ConvertHelper.convert(issueCommentE, IssueCommentDO.class);
        if (iIssueCommentService.updateOptional(issueCommentDO, fieldList) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(issueCommentMapper.selectByPrimaryKey(issueCommentDO.getCommentId()), IssueCommentE.class);
    }

    @Override
    @DataLog(type = "createComment")
    public IssueCommentE create(IssueCommentE issueCommentE) {
        IssueCommentDO issueCommentDO = ConvertHelper.convert(issueCommentE, IssueCommentDO.class);
        if (issueCommentMapper.insert(issueCommentDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueCommentMapper.selectByPrimaryKey(issueCommentDO.getCommentId()), IssueCommentE.class);
    }

    @Override
    @DataLog(type = "deleteComment")
    public int delete(IssueCommentDO issueCommentDO) {
        int isDelete = issueCommentMapper.delete(issueCommentDO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }
}