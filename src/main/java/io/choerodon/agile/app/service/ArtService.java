package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.ArtVO;
import io.choerodon.agile.api.vo.ArtStopVO;
import io.choerodon.agile.api.vo.PiCreateDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface ArtService {

    ArtDTO createArt(Long programId, ArtDTO artDTO);

    ArtDTO startArt(Long programId, ArtVO artVO);

    ArtDTO stopArt(Long programId, ArtVO artVO, Boolean onlySelectEnable);

    ArtDTO updateArt(Long programId, ArtVO artVO);

    PageInfo<ArtDTO> queryArtList(Long programId, PageRequest pageRequest);

    ArtVO queryArt(Long programId, Long id);

    void createOtherPi(Long programId, PiCreateDTO piCreateDTO);

    List<PiCalendarDTO> queryArtCalendar(Long programId, Long artId);

    ArtStopVO beforeStop(Long programId, Long id);

    Boolean checkName(Long programId, String artName);

    List<ArtVO> queryAllArtList(Long programId);

    ArtVO queryActiveArt(Long programId);
}
