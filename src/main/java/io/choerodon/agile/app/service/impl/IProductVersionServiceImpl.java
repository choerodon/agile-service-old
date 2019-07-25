package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IProductVersionService;
import io.choerodon.agile.infra.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.ProductVersionDTO;
import io.choerodon.agile.infra.dataobject.VersionIssueDTO;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IProductVersionServiceImpl implements IProductVersionService {

    private static final String DELETE_ERROR = "error.version.delete";

    @Autowired
    private ProductVersionMapper versionMapper;

    @Override
    @DataLog(type = "batchDeleteVersionByVersion", single = false)
    public Boolean delete(ProductVersionDTO versionDTO) {
        if (versionMapper.delete(versionDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return true;
    }

    @Override
    @DataLog(type = "batchMoveVersion", single = false)
    public Boolean batchIssueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDTO> versionIssues, Date date, Long userId) {
        versionMapper.issueToDestination(projectId, targetVersionId, versionIssues, date, userId);
        return true;
    }
}
