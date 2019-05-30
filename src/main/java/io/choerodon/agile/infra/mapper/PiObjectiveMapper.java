package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PiObjectiveDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiObjectiveMapper extends BaseMapper<PiObjectiveDO> {

    List<PiObjectiveDO> selectPiObjectiveList(@Param("programId") Long programId, @Param("piId") Long piId, @Param("teamWithProgramIds") List<Long> teamWithProgramIds);

    List<PiObjectiveDO> selectPiObjectiveListByProject(@Param("projectId") Long projectId, @Param("piId") Long piId);
}
