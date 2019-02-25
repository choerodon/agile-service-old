package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PersonalFilterDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public interface PersonalFilterMapper extends BaseMapper<PersonalFilterDO> {
    List<PersonalFilterDO> queryByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("searchStr") String searchStr);
}
