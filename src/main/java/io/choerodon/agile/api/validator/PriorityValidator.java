package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Component
public class PriorityValidator {


    public void createValidate(PriorityVO priorityVO) {
        if (StringUtils.isEmpty(priorityVO.getName())) {
            throw new CommonException("error.priority.create.name.empty");
        }
        if (StringUtils.isEmpty(priorityVO.getColour())) {
            throw new CommonException("error.priority.create.colour.empty");
        }

    }

    public void updateValidate(PriorityVO priorityVO) {
        if (priorityVO.getName() != null && priorityVO.getName().length() == 0) {
            throw new CommonException("error.priority.update.name.empty");
        }

    }


}
