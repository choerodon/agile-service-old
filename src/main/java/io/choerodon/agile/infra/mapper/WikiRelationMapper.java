package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.WikiRelationDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
public interface WikiRelationMapper extends Mapper<WikiRelationDTO> {

    void updateByOptions(@Param("id") Long id, @Param("spaceId") Long spaceId);
}
