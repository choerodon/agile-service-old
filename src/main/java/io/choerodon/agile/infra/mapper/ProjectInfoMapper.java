package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
public interface ProjectInfoMapper extends Mapper<ProjectInfoDTO> {

    ProjectInfoDTO queryByProjectId(@Param("projectId") Long projectId);

    /**
     * 更新MaxNum
     *
     * @param projectId projectId
     * @param issueMaxNum  issueMaxNum
     * @return int
     */
    int updateIssueMaxNum(@Param("projectId") Long projectId, @Param("issueMaxNum") String issueMaxNum);

    void updateProjectAndIssues(@Param("projectId") Long projectId, @Param("creationDate1") Date creationDate1, @Param("creationDate2") Date creationDate2);

    String selectProjectCodeByProjectId(@Param("projectId") Long projectId);
}
