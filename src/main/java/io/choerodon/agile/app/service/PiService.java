package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.MoveIssueDTO;
import io.choerodon.agile.api.dto.PiDTO;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;
import java.util.Map;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiService {

    void createPi(Long programId, Long piNumber, ArtDO artDO);

    PiDTO updatePi(Long programId, PiDTO piDTO);

    JSONObject queryBacklogAll(Long programId, Map<String, Object> searchParamMap);

    Page<PiDTO> queryAll(Long programId, PageRequest pageRequest);

    PiDTO startPi(Long programId, PiDTO piDTO);

    PiDTO closePi(Long programId, PiDTO piDTO);

    List<SubFeatureDO> batchFeatureToPi(Long programId, Long piId, MoveIssueDTO moveIssueDTO);

    List<SubFeatureDO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds);
}
