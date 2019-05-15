package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.ProjectInfoE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
public interface ProjectInfoRepository {

    /**
     * 创建
     *
     * @param projectInfoE projectInfoE
     * @return ProjectInfoE
     */
    ProjectInfoE create(ProjectInfoE projectInfoE);

    /**
     * 根据projectId更新issueMaxNum+increase
     *
     * @param projectId projectId
     * @param increase  increase
     * @return int
     */
    int updateIssueMaxNum(Long projectId, Integer increase);

    /**
     * 更新
     *
     * @param projectInfoE projectInfoE
     * @return ProjectInfoE
     */
    ProjectInfoE update(ProjectInfoE projectInfoE);
}
