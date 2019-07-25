package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ProjectPageFieldDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/3
 */
public interface ProjectPageFieldMapper extends Mapper<ProjectPageFieldDTO> {
    /**
     * 获取项目层自定义的记录，判断是否存在自定义
     *
     * @param organizationId
     * @param projectId
     * @return
     */
    ProjectPageFieldDTO queryOne(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId);

    /**
     * 根据组织id获取项目层自定义记录列表
     *
     * @param organizationId
     * @return
     */
    List<ProjectPageFieldDTO> queryByOrgId(@Param("organizationId") Long organizationId);

    /**
     * 若项目层自定义，则新增一条记录
     *
     * @param organizationId
     * @param projectId
     */
    void createOne(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId);
}
