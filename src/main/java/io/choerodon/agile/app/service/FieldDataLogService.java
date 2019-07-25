package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.FieldDataLogCreateVO;
import io.choerodon.agile.api.vo.FieldDataLogVO;
import io.choerodon.agile.infra.dataobject.FieldDataLogDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
public interface FieldDataLogService {

    FieldDataLogDTO baseCreate(FieldDataLogDTO create);

    FieldDataLogVO createDataLog(Long projectId, String schemeCode, FieldDataLogCreateVO create);

    void deleteByFieldId(Long projectId, Long fieldId);

    List<FieldDataLogVO> queryByInstanceId(Long projectId, Long instanceId, String schemeCode);
}
