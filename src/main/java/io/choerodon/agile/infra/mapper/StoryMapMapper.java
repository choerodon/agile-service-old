package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.agile.infra.dataobject.EpicWithFeatureDO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoryMapMapper {

    List<Long> selectEpicIdsByProgram(@Param("programId") Long programId);

    List<Long> selectEpicIdsByProject(@Param("projectId") Long projectId);

    List<EpicWithFeatureDO> selectEpicWithFeatureList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<FeatureCommonDO> selectFeatureByNoEpicByProject(@Param("projectId") Long projectId);

    List<FeatureCommonDO> selectFeatureByNoEpicByProgram(@Param("programId") Long programId, @Param("projectId") Long projectId);

    List<StoryMapStoryDO> selectStoryList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds, @Param("featureIds") List<Long> featureIds, @Param("searchDTO") SearchDTO searchDTO);

    List<StoryMapStoryDO> selectDemandStoryList(@Param("projectId") Long projectId, @Param("searchDTO") SearchDTO searchDTO);
}
