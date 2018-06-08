package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.IssueComponentDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueComponentMapper extends BaseMapper<IssueComponentDO> {

    /**
     * 重名校验
     *
     * @param name      name
     * @param projectId projectId
     * @return Boolean
     */
    Boolean checkNameExist(@Param("name") String name, @Param("projectId") Long projectId);

    /**
     * 根据名称和项目id查询模块id
     *
     * @param name      name
     * @param projectId projectId
     * @return Long
     */
    Long queryComponentIdByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    List selectComponentWithIssueNum(@Param("projectId") Long projectId, @Param("componentId") Long componentId);

    List queryIssuesByComponentId(@Param("projectId") Long projectId, @Param("componentId") Long componentId);
}
