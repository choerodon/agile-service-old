package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiMapper extends BaseMapper<PiDO> {

    Boolean hasPiIssue(@Param("programId") Long programId, @Param("piId") Long piId);

    String queryPiMaxRank(@Param("programId") Long programId, @Param("piId") Long piId);

    String queryPiMinRank(@Param("programId") Long programId, @Param("piId") Long piId);

    List<PiWithFeatureDO> selectBacklogPiList(@Param("programId") Long programId, @Param("artId") Long artId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    List<SubFeatureDO> selectBacklogNoPiList(@Param("programId") Long programId, @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    PiDO selectLastPi(@Param("programId") Long programId, @Param("artId") Long artId);

    List<PiDO> selectPiListInArt(@Param("programId") Long programId, @Param("artId") Long artId);

    List<Long> queryFeatureIdOrderByRankDesc(@Param("programId") Long programId, @Param("piId") Long piId);

    List<Long> queryFeatureIds(@Param("programId") Long programId, @Param("piId") Long piId);

    List<SubFeatureDO> selectFeatureIdByFeatureIds(@Param("programId") Long programId, @Param("featureIds") List<Long> featureIds);

    PiDO selectActivePi(@Param("programId") Long programId, @Param("artId") Long artId);

    List<PiDO> selectTodoPiDOList(@Param("programId") Long programId, @Param("artId") Long artId);

    List<PiDO> selectUnDonePiDOList(@Param("programId") Long programId, @Param("artId") Long artId);

    Long selectFeatureCount(@Param("programId") Long programId, @Param("piId") Long piId, @Param("isCompleted") Boolean isCompleted);

    List<PiTodoDO> selectTodoPi(@Param("programId") Long programId, @Param("artId") Long artId);

    Long selectPiCountByOptions(@Param("programId") Long programId, @Param("artId") Long artId, @Param("statusCode") String statusCode);

    Long selectRelatedFeatureCount(@Param("programId") Long programId, @Param("artId") Long artId);

    PiDO selectArtFirstPi(@Param("programId") Long programId, @Param("artId") Long artId);

    PiDO selectNextPi(@Param("programId") Long programId, @Param("artId") Long artId, @Param("piId") Long piId);

    List<Long> selectNextListPi(@Param("programId") Long programId, @Param("artId") Long artId, @Param("piId") Long piId);

    List<PiNameDO> selectAllOfProgram(@Param("programId") Long programId);

    List<PiNameDO> selectclosePiListByIssueId(@Param("programId") Long programId, @Param("issueId") Long issueId);

    PiNameDO selectCurrentPiListByIssueId(@Param("programId") Long programId, @Param("issueId") Long issueId);

    List<PiWithFeatureDO> selectRoadMapPiList(@Param("programId") Long programId, @Param("artId") Long artId);
}
