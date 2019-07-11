package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiDTO;
import io.choerodon.agile.infra.dataobject.PiNameDTO;
import io.choerodon.agile.infra.dataobject.SubFeatureDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiService {

    void createPi(Long programId, ArtDTO artDTO, Date startDate);

    PiDTO updatePi(Long programId, PiDTO piDTO);

    JSONObject queryBacklogAll(Long programId, Long organizationId, Map<String, Object> searchParamMap);

    PageInfo<PiDTO> queryArtAll(Long programId, Long artId, PageRequest pageRequest);

    PiDTO startPi(Long programId, PiVO piVO);

    PiCompleteCountVO beforeClosePi(Long programId, Long piId, Long artId);

    PiDTO closePi(Long programId, PiVO piVO);

    void dealUnCompleteFeature(Long programId, Long piId, Long targetPiId);

    void completeProjectsSprints(Long programId, Long piId, Boolean onlySelectEnable);

    void completeSprintsWithSelect(Long programId, Long piId, Long nextPiId, Long artId);

    List<SubFeatureDTO> batchFeatureToPi(Long programId, Long piId, MoveIssueVO moveIssueVO);

    List<SubFeatureDTO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds);

    List<PiNameDTO> queryAllOfProgram(Long programId);

    List<PiNameVO> queryUnfinishedOfProgram(Long programId);

    List<PiWithFeatureVO> queryRoadMapOfProgram(Long programId, Long organizationId);

    PiDTO createBase(PiDTO piDTO);

    PiDTO updateBySelectiveBase(PiDTO piDTO);


}
