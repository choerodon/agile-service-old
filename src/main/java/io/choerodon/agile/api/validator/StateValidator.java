package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.infra.enums.StatusType;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Component
public class StateValidator {

    public void validate(StatusVO statusVO) {
        if (StringUtils.isEmpty(statusVO.getName())) {
            throw new CommonException("error.status.name.empty");
        }
        if (StringUtils.isEmpty(statusVO.getType())) {
            throw new CommonException("error.status.type.empty");
        }

        if (!EnumUtil.contain(StatusType.class, statusVO.getType())) {
            throw new CommonException("error.status.type.illegal");
        }
    }
}
