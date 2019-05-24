package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
public interface ProjectInfoMapper extends BaseMapper<ProjectInfoDO> {

    ProjectInfoDO queryByProjectId(@Param("projectId") Long projectId);

    /**
     * 更新MaxNum
     *
     * @param projectId projectId
     * @param issueMaxNum  issueMaxNum
     * @return int
     */
    int updateIssueMaxNum(@Param("projectId") Long projectId, @Param("issueMaxNum") String issueMaxNum);

    void updateProjectInfo(@Param("projectId") Long projectId, @Param("creationDate1") Date creationDate1);

    void updateProjectIssuesInfo(@Param("projectId") Long projectId, @Param("creationDate2") Date creationDate2);

    String selectProjectCodeByProjectId(@Param("projectId") Long projectId);
}
