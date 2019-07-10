package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.VelocitySprintVO;
import io.choerodon.agile.infra.dataobject.VelocitySprintDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/26.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class VelocityChartConverter implements ConvertorI<Object, VelocitySprintDO, VelocitySprintVO>{

    @Override
    public VelocitySprintVO doToDto(VelocitySprintDO velocitySprintDO) {
        VelocitySprintVO velocitySprintVO = new VelocitySprintVO();
        BeanUtils.copyProperties(velocitySprintDO, velocitySprintVO);
        return velocitySprintVO;
    }

}
