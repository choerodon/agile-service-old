package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PiFeatureDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/26.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiFeatureMapper extends Mapper<PiFeatureDO> {

    Boolean selectExistByOptions(@Param("programId") Long programId, @Param("issueId") Long issueId);

    Boolean selectGivenExistByOptions(@Param("programId") Long programId, @Param("issueId") Long issueId, @Param("piId") Long piId);

    void deletePfRelationByOptions(@Param("programId") Long programId, @Param("issueId") Long issueId);
}
