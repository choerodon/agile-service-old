package io.choerodon.agile.domain.agile.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.AssigneeDistributeDTO;
import io.choerodon.agile.infra.dataobject.AssigneeDistributeDO;
import io.choerodon.core.convertor.ConvertorI;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  13:51 2018/9/4
 * Description:
 */
@Component
public class AssigneeDistributeConverter implements ConvertorI<Object, AssigneeDistributeDO, AssigneeDistributeDTO> {

    @Override
    public AssigneeDistributeDTO doToDto(AssigneeDistributeDO assigneeDistributeDO) {
        AssigneeDistributeDTO assigneeDistributeDTO = new AssigneeDistributeDTO();
        BeanUtils.copyProperties(assigneeDistributeDO, assigneeDistributeDTO);
        return assigneeDistributeDTO;
    }
}
