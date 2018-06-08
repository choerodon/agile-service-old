package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@Component
public interface ProjectInfoMapper extends BaseMapper<ProjectInfoDO> {

    /**
     * 更新MaxNum+1
     *
     * @param projectId projectId
     * @return int
     */
    int updateIssueMaxNum(@Param("projectId") Long projectId);
}
