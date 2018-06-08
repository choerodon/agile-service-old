package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueBranchDTO;
import io.choerodon.agile.app.service.IssueBranchService;
import io.choerodon.agile.domain.agile.entity.IssueBranchE;
import io.choerodon.agile.domain.agile.repository.IssueBranchRepository;
import io.choerodon.agile.infra.dataobject.IssueBranchDO;
import io.choerodon.agile.infra.mapper.IssueBranchMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IssueBranchServiceImpl implements IssueBranchService {

    @Autowired
    private IssueBranchRepository issueBranchRepository;

    @Autowired
    private IssueBranchMapper issueBranchMapper;

    @Override
    public IssueBranchDTO create(Long projectId, IssueBranchDTO issueBranchDTO) {
        if (!projectId.equals(issueBranchDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        IssueBranchE issueBranchE = ConvertHelper.convert(issueBranchDTO, IssueBranchE.class);
        return ConvertHelper.convert(issueBranchRepository.create(issueBranchE), IssueBranchDTO.class);
    }

    @Override
    public IssueBranchDTO update(Long projectId, Long branchId, IssueBranchDTO issueBranchDTO) {
        if (!projectId.equals(issueBranchDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        issueBranchDTO.setBranchId(branchId);
        IssueBranchE issueBranchE = ConvertHelper.convert(issueBranchDTO, IssueBranchE.class);
        return ConvertHelper.convert(issueBranchRepository.update(issueBranchE), IssueBranchDTO.class);
    }

    @Override
    public void delete(Long projectId, Long branchId) {
        issueBranchRepository.delete(branchId);
    }

    @Override
    public IssueBranchDTO queryIssueBranchById(Long projectId, Long branchId) {
        IssueBranchDO issueBranchDO = new IssueBranchDO();
        issueBranchDO.setProjectId(projectId);
        issueBranchDO.setBranchId(branchId);
        return ConvertHelper.convert(issueBranchMapper.selectOne(issueBranchDO), IssueBranchDTO.class);
    }
}
