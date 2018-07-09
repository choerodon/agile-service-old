package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.repository.IssueSprintRelRepository;
import io.choerodon.agile.infra.dataobject.IssueSprintRelDO;
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@Component
@Transactional(rollbackFor = CommonException.class)
public class IssueSprintRelRepositoryImpl implements IssueSprintRelRepository {

    private static final String INSERT_ERROR = "error.issueSprintRel.create";
    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper;

    @Override
    public IssueSprintRelDO createIssueSprintRel(IssueSprintRelDO issueSprintRelDO) {
        if (issueSprintRelMapper.insert(issueSprintRelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return issueSprintRelMapper.selectOne(issueSprintRelDO);
    }
}
