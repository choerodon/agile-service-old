package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardSprintAttrDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrMapper extends BaseMapper<BoardSprintAttrDO> {
    List<BoardSprintAttrDO> queryByProgramId(@Param("programId") Long programId);
}
