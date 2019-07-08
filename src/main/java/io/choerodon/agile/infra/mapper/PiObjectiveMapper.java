package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiObjectiveMapper extends Mapper<PiObjectiveDTO> {

    List<PiObjectiveDTO> selectPiObjectiveList(@Param("programId") Long programId, @Param("piId") Long piId, @Param("teamWithProgramIds") List<Long> teamWithProgramIds);

    List<PiObjectiveDTO> selectPiObjectiveListByProject(@Param("projectId") Long projectId, @Param("piId") Long piId);
}
