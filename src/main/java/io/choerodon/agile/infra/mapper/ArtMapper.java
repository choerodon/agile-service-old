package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.dataobject.PiCalendarDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface ArtMapper extends Mapper<ArtDO> {

    List<ArtDO> selectArtList(@Param("programId") Long programId);

    ArtDO selectActiveArt(@Param("programId") Long programId);

    List<PiCalendarDO> selectArtCalendar(@Param("programId") Long programId, @Param("artId") Long artId);
}
