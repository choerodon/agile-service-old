package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.infra.dataobject.IssueSprintRelDTO;
import io.choerodon.agile.app.service.IssueSprintRelService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@Service
public class IssueSprintRelServiceImpl implements IssueSprintRelService {

    private static final String INSERT_ERROR = "error.issueSprintRel.create";
    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper;

    @Override
    @DataLog(type = "sprint")
    public IssueSprintRelDTO createIssueSprintRel(IssueSprintRelDTO issueSprintRelDTO) {
        if (issueSprintRelMapper.insert(issueSprintRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return issueSprintRelMapper.selectOne(issueSprintRelDTO);
    }
}
