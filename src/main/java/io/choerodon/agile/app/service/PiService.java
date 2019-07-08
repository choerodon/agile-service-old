package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
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

    PiVO updatePi(Long programId, PiVO piVO);

    JSONObject queryBacklogAll(Long programId, Long organizationId, Map<String, Object> searchParamMap);

    PageInfo<PiVO> queryArtAll(Long programId, Long artId, PageRequest pageRequest);

    PiVO startPi(Long programId, PiVO piVO);

    PiCompleteCountDTO beforeClosePi(Long programId, Long piId, Long artId);

    PiVO closePi(Long programId, PiVO piVO);

    void dealUnCompleteFeature(Long programId, Long piId, Long targetPiId);

    void completeProjectsSprints(Long programId, Long piId, Boolean onlySelectEnable);

    void completeSprintsWithSelect(Long programId, Long piId, Long nextPiId, Long artId);

    List<SubFeatureDO> batchFeatureToPi(Long programId, Long piId, MoveIssueDTO moveIssueDTO);

    List<SubFeatureDO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds);

    List<PiNameDTO> queryAllOfProgram(Long programId);

    List<PiNameDTO> queryUnfinishedOfProgram(Long programId);

    List<PiWithFeatureDTO> queryRoadMapOfProgram(Long programId, Long organizationId);

}
