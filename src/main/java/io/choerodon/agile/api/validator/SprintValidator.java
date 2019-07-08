package io.choerodon.agile.api.validator;

import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/5.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SprintValidator {

    @Autowired
    private SprintMapper sprintMapper;

    public void checkSprintStartInProgram(SprintE sprintE) {
        Long sprintId = sprintE.getSprintId();
        SprintDO sprintDO = sprintMapper.selectByPrimaryKey(sprintId);
        if (sprintDO == null) {
            throw new CommonException("error.sprint.get");
        }
        if (sprintDO.getPiId() != null) {
            Date nowDate = new Date();
            if (nowDate.before(sprintE.getEndDate())) {
                sprintE.setStartDate(nowDate);
            }
        }
    }

}
