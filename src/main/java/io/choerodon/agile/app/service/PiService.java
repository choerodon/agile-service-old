package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.MoveIssueDTO;
import io.choerodon.agile.api.dto.PiCompleteCountDTO;
import io.choerodon.agile.api.dto.PiDTO;
import io.choerodon.agile.api.dto.PiNameDTO;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiService {

    void createPi(Long programId, ArtDO artDO, Date startDate);

    PiDTO updatePi(Long programId, PiDTO piDTO);

    JSONObject queryBacklogAll(Long programId, Long organizationId, Map<String, Object> searchParamMap);

    Page<PiDTO> queryArtAll(Long programId, Long artId, PageRequest pageRequest);

    PiDTO startPi(Long programId, PiDTO piDTO);

    PiCompleteCountDTO beforeClosePi(Long programId, Long piId, Long artId);

    PiDTO closePi(Long programId, PiDTO piDTO);

    List<SubFeatureDO> batchFeatureToPi(Long programId, Long piId, MoveIssueDTO moveIssueDTO);

    List<SubFeatureDO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds);

    void deleteById(Long programId, Long piId, Long artId);

    List<PiNameDTO> queryAllOfProgram(Long programId);
}
