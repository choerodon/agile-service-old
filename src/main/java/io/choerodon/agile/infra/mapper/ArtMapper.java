package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface ArtMapper extends Mapper<ArtDTO> {

    List<ArtDTO> selectArtList(@Param("programId") Long programId);

    ArtDTO selectActiveArt(@Param("programId") Long programId);

    List<PiCalendarDTO> selectArtCalendar(@Param("programId") Long programId, @Param("artId") Long artId);
}
