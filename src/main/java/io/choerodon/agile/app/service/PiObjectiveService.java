package io.choerodon.agile.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.PiObjectiveVO;
import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public interface PiObjectiveService {

    PiObjectiveDTO createPiObjective(Long programId, PiObjectiveDTO piObjectiveDTO);

    PiObjectiveDTO updatePiObjective(Long programId, PiObjectiveDTO piObjectiveDTO);

    void deletePiObjective(Long programId, Long id);

    PiObjectiveVO queryPiObjective(Long programId, Long id);

    JSONObject queryPiObjectiveList(Long programId, Long piId);

    List<PiObjectiveVO> queryPiObjectiveListByProject(Long projectId, Long piId);
}
