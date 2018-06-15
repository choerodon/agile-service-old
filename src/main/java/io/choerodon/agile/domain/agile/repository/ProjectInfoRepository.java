package io.choerodon.agile.domain.agile.repository;

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
     * 根据projectId更新issueMaxNum+1
     *
     * @param projectId projectId
     * @return int
     */
    int updateIssueMaxNum(Long projectId);

    /**
     * 更新
     *
     * @param projectInfoE projectInfoE
     * @return ProjectInfoE
     */
    ProjectInfoE update(ProjectInfoE projectInfoE);
}
