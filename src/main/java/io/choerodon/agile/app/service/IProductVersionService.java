package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.ProductVersionDTO;
import io.choerodon.agile.infra.dataobject.VersionIssueDTO;

import java.util.Date;
import java.util.List;

public interface IProductVersionService {

    Boolean delete(ProductVersionDTO versionDTO);

    Boolean batchIssueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDTO> incompleteIssues, Date date, Long userId);

}
