package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.EpicWithFeatureDO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoryMapMapper {

    List<Long> selectEpicIdsByProgram(@Param("programId") Long programId);

    List<Long> selectEpicIdsByProject(@Param("projectId") Long projectId);

    List<EpicWithFeatureDO> selectEpicWithFeatureList(@Param("epicIds") List<Long> epicIds);

    List<StoryMapStoryDO> selectStoryList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds, @Param("featureIds") List<Long> featureIds);

    List<StoryMapStoryDO> selectDemandStoryList(@Param("projectId") Long projectId);
}
