package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ComponentForListDO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.IssueComponentDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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


    List queryIssuesByComponentId(@Param("projectId") Long projectId, @Param("componentId") Long componentId);

    /**
     * 根据参数查询模块id列表
     *
     * @param projectId          projectId
     * @param noIssueTest        noIssueTest
     * @param componentId        componentId
     * @param searchArgs         searchArgs
     * @param advancedSearchArgs advancedSearchArgs
     * @return Long
     */
    List<ComponentForListDO> queryComponentByOption(@Param("projectId") Long projectId, @Param("noIssueTest") Boolean noIssueTest,
                                                    @Param("componentId") Long componentId, @Param("searchArgs") Map<String, Object> searchArgs,
                                                    @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs, @Param("contents") List<String> contents);

    List<ComponentForListDO> queryComponentWithIssueNum(@Param("projectId") Long projectId, @Param("componentId") Long componentId,
                                                        @Param("noIssueTest") Boolean noIssueTest);

    List<IssueComponentDO> selectByProjectId(@Param("projectId") Long projectId);
}
