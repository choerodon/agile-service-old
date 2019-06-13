package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.StoryMapWidthDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
public interface StoryMapWidthMapper extends Mapper<StoryMapWidthDO> {

    List<StoryMapWidthDO> selectByProjectId(@Param("projectId") Long projectId);

}
