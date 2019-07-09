package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.infra.dataobject.EpicWithFeatureDO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDTO;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoryMapMapper {

    List<Long> selectEpicIdsByProgram(@Param("programId") Long programId);

    List<Long> selectEpicIdsByProject(@Param("projectId") Long projectId);

    List<EpicWithFeatureDO> selectEpicWithFeatureList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<FeatureCommonDTO> selectFeatureByNoEpicByProject(@Param("projectId") Long projectId);

    List<FeatureCommonDTO> selectFeatureByNoEpicByProgram(@Param("programId") Long programId, @Param("projectId") Long projectId);

    List<StoryMapStoryDO> selectStoryList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds, @Param("featureIds") List<Long> featureIds, @Param("searchVO") SearchVO searchVO);

    List<StoryMapStoryDO> selectDemandStoryList(@Param("projectId") Long projectId, @Param("searchVO") SearchVO searchVO);
}
