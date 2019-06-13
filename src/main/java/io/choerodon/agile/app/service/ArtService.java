package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.ArtDTO;
import io.choerodon.agile.api.dto.ArtStopDTO;
import io.choerodon.agile.api.dto.PiCalendarDTO;
import io.choerodon.agile.api.dto.PiCreateDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface ArtService {

    ArtDTO createArt(Long programId, ArtDTO artDTO);

    ArtDTO startArt(Long programId, ArtDTO artDTO);

    ArtDTO stopArt(Long programId, ArtDTO artDTO, Boolean onlySelectEnable);

    ArtDTO updateArt(Long programId, ArtDTO artDTO);

    PageInfo<ArtDTO> queryArtList(Long programId, PageRequest pageRequest);

    ArtDTO queryArt(Long programId, Long id);

    void createOtherPi(Long programId, PiCreateDTO piCreateDTO);

    List<PiCalendarDTO> queryArtCalendar(Long programId, Long artId);

    ArtStopDTO beforeStop(Long programId, Long id);

    Boolean checkName(Long programId, String artName);

    List<ArtDTO> queryAllArtList(Long programId);

    ArtDTO queryActiveArt(Long programId);
}
