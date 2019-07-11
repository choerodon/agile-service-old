package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.IssueSprintRelE;
import io.choerodon.agile.infra.dataobject.IssueSprintRelDTO;
import io.choerodon.agile.infra.repository.IssueSprintRelRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@Component
public class IssueSprintRelRepositoryImpl implements IssueSprintRelRepository {

    private static final String INSERT_ERROR = "error.issueSprintRel.create";
    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper;

    @Override
    @DataLog(type = "sprint")
    public IssueSprintRelE createIssueSprintRel(IssueSprintRelE issueSprintRelE) {
        IssueSprintRelDTO issueSprintRelDTO = ConvertHelper.convert(issueSprintRelE, IssueSprintRelDTO.class);
        if (issueSprintRelMapper.insert(issueSprintRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueSprintRelMapper.selectOne(issueSprintRelDTO), IssueSprintRelE.class);
    }
}
