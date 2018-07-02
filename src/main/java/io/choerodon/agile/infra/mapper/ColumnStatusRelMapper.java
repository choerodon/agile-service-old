package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.ColumnStatusRelDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/15.
 * Email: fuqianghuang01@gmail.com
 */
public interface ColumnStatusRelMapper extends BaseMapper<ColumnStatusRelDO> {

    /**
     * 根据issueId和列id集合查询columnStatus关系，用于统计积累流图
     *
     * @param issueId   issueId
     * @param columnIds columnIds
     * @return ColumnStatusRelDO
     */
    ColumnStatusRelDO queryByIssueIdAndColumnIds(@Param("issueId") Long issueId, @Param("columnIds")List<Long> columnIds);
}
