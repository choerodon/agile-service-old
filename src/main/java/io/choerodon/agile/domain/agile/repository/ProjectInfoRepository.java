package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.infra.dataobject.ProjectInfoDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
public interface ProjectInfoRepository {

    /**
     * 创建
     *
     * @param projectInfoDO projectInfoDO
     * @return ProjectInfoDO
     */
    ProjectInfoDO create(ProjectInfoDO projectInfoDO);

    /**
     * 根据projectId更新issueMaxNum+1
     *
     * @param projectId projectId
     * @return int
     */
    int updateIssueMaxNum(Long projectId);
}
