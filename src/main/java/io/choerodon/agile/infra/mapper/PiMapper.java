package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PiDO;
import io.choerodon.agile.infra.dataobject.PiWithFeatureDO;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
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

    List<PiDO> selectPiList(@Param("programId") Long programId);

    List<Long> queryFeatureIdOrderByRankDesc(@Param("programId") Long programId, @Param("piId") Long piId);

    List<Long> queryFeatureIds(@Param("programId") Long programId, @Param("piId") Long piId);

    List<SubFeatureDO> selectFeatureIdByFeatureIds(@Param("programId") Long programId, @Param("featureIds") List<Long> featureIds);

    PiDO selectActivePi(@Param("programId") Long programId);
}
