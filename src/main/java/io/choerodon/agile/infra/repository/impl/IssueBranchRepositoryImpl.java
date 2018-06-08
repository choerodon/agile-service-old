package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.IssueBranchE;
import io.choerodon.agile.domain.agile.repository.IssueBranchRepository;
import io.choerodon.agile.infra.dataobject.IssueBranchDO;
import io.choerodon.agile.infra.mapper.IssueBranchMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueBranchRepositoryImpl implements IssueBranchRepository {

    @Autowired
    private IssueBranchMapper issueBranchMapper;

    @Override
    public IssueBranchE create(IssueBranchE issueBranchE) {
        IssueBranchDO issueBranchDO = ConvertHelper.convert(issueBranchE, IssueBranchDO.class);
        if (issueBranchMapper.insert(issueBranchDO) != 1) {
            throw new CommonException("error.issueBranch.insert");
        }
        return ConvertHelper.convert(issueBranchMapper.selectByPrimaryKey(issueBranchDO.getBranchId()), IssueBranchE.class);
    }

    @Override
    public IssueBranchE update(IssueBranchE issueBranchE) {
        IssueBranchDO issueBranchDO = ConvertHelper.convert(issueBranchE, IssueBranchDO.class);
        if (issueBranchMapper.updateByPrimaryKeySelective(issueBranchDO) != 1) {
            throw new CommonException("error.issueBranch.update");
        }
        return ConvertHelper.convert(issueBranchMapper.selectByPrimaryKey(issueBranchDO.getBranchId()), IssueBranchE.class);
    }

    @Override
    public void delete(Long branchId) {
        IssueBranchDO issueBranchDO = issueBranchMapper.selectByPrimaryKey(branchId);
        if (issueBranchDO == null) {
            throw new CommonException("error.issueBranch.get");
        }
        if (issueBranchMapper.delete(issueBranchDO) != 1) {
            throw new CommonException("error.issueBranch.delete");
        }
    }

}
