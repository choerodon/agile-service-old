package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/15.
 * Email: fuqianghuang01@gmail.com
 */
public interface ColumnStatusRelMapper extends Mapper<ColumnStatusRelDTO> {

    /**
     * 根据statusTo为null的issueId集合和列id集合查询columnStatus关系，用于统计累积流图
     *
     * @param statusToNullIssueIds statusToNullIssueIds
     * @param columnIds            columnIds
     * @return ColumnStatusRelDTO
     */
    List<ColumnStatusRelDTO> queryByIssueIdAndColumnIds(@Param("statusToNullIssueIds") List<Long> statusToNullIssueIds, @Param("columnIds") List<Long> columnIds);

    Long selectOneStatusIdByCategory(@Param("programId") Long programId, @Param("categoryCode") String categoryCode);
}
