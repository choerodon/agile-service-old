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
}
