package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.QuickFilterDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterMapper extends BaseMapper<QuickFilterDO> {

    List<String> selectSqlQueryByIds(@Param("quickFilterIds") List<Long> quickFilterIds);

    /**
     * 批量更新排序
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @param add       add
     * @param filterId  filterId
     * @return int
     */
    int batchUpdateSequence(@Param("sequence") Integer sequence, @Param("projectId") Long projectId, @Param("add") Integer add, @Param("filterId") Long filterId);

    /**
     * 查询最大的序号
     *
     * @param projectId projectId
     * @return Integer
     */
    Integer queryMaxSequenceByProject(@Param("projectId") Long projectId);

    /**
     * 获取当前排序后的最大一个
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @return Integer
     */
    Integer queryMaxAfterSequence(@Param("sequence") Integer sequence, @Param("projectId") Long projectId);

    /**
     * 获取当前排序前的最小一个
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @return Integer
     */
    Integer queryMinBeforeSequence(@Param("sequence") Integer sequence, @Param("projectId") Long projectId);

    /**
     * 根据项目id查询快速筛选，通过sequence排序
     *
     * @param projectId  projectId
     * @param filterName filterName
     * @param contents   contents
     * @return QuickFilterDO
     */
    List<QuickFilterDO> queryFiltersByProjectId(@Param("projectId") Long projectId, @Param("filterName") String filterName, @Param("contents") List<String> contents);
}
