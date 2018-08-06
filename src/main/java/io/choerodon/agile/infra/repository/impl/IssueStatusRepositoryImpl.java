package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.domain.agile.repository.IssueStatusRepository;
import io.choerodon.agile.infra.dataobject.IssueStatusDO;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueStatusRepositoryImpl implements IssueStatusRepository {

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr.substring(10);
    }

    @Override
    public IssueStatusE create(IssueStatusE issueStatusE) {
        IssueStatusDO issueStatusDO = ConvertHelper.convert(issueStatusE, IssueStatusDO.class);
        if (issueStatusMapper.insert(issueStatusDO) != 1) {
            throw new CommonException("error.IssueStatus.insert");
        }
        return ConvertHelper.convert(issueStatusMapper.selectByPrimaryKey(issueStatusDO.getId()), IssueStatusE.class);
    }

    @Override
    public IssueStatusE update(IssueStatusE issueStatusE) {
        IssueStatusDO issueStatusDO = ConvertHelper.convert(issueStatusE, IssueStatusDO.class);
        if (issueStatusMapper.updateByPrimaryKeySelective(issueStatusDO) != 1) {
            throw new CommonException("error.status.update");
        }
        return ConvertHelper.convert(issueStatusMapper.selectByPrimaryKey(issueStatusDO.getId()), IssueStatusE.class);
    }

    @Override
    public void delete(IssueStatusE issueStatusE) {
        IssueStatusDO issueStatusDO = ConvertHelper.convert(issueStatusE, IssueStatusDO.class);
        if (issueStatusMapper.delete(issueStatusDO) != 1) {
            throw new CommonException("error.status.delete");
        }
    }

    @Override
    public Boolean checkSameStatus(Long projectId, String statusName) {
        return issueStatusMapper.checkSameStatus(projectId, statusName) != 0;
    }
}
