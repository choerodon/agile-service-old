package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.infra.dataobject.EpicWithFeatureDTO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDTO;
import io.choerodon.agile.infra.dataobject.StoryMapStoryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoryMapMapper {

    List<Long> selectEpicIdsByProgram(@Param("programId") Long programId);

    List<Long> selectEpicIdsByProject(@Param("projectId") Long projectId);

    List<EpicWithFeatureDTO> selectEpicWithFeatureList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds);

    List<FeatureCommonDTO> selectFeatureByNoEpicByProject(@Param("projectId") Long projectId);

    List<FeatureCommonDTO> selectFeatureByNoEpicByProgram(@Param("programId") Long programId, @Param("projectId") Long projectId);

    List<StoryMapStoryDTO> selectStoryList(@Param("projectId") Long projectId, @Param("epicIds") List<Long> epicIds, @Param("featureIds") List<Long> featureIds, @Param("searchVO") SearchVO searchVO);

    List<StoryMapStoryDTO> selectDemandStoryList(@Param("projectId") Long projectId, @Param("searchVO") SearchVO searchVO);
}
