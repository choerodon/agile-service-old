package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardSprintAttrDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public interface BoardSprintAttrMapper extends Mapper<BoardSprintAttrDTO> {
    List<BoardSprintAttrDTO> queryByProgramId(@Param("programId") Long programId);
}
