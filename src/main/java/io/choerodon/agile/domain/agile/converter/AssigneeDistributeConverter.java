package io.choerodon.agile.domain.agile.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.AssigneeDistributeVO;
import io.choerodon.agile.infra.dataobject.AssigneeDistributeDO;
import io.choerodon.core.convertor.ConvertorI;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  13:51 2018/9/4
 * Description:
 */
@Component
public class AssigneeDistributeConverter implements ConvertorI<Object, AssigneeDistributeDO, AssigneeDistributeVO> {

    @Override
    public AssigneeDistributeVO doToDto(AssigneeDistributeDO assigneeDistributeDO) {
        AssigneeDistributeVO assigneeDistributeVO = new AssigneeDistributeVO();
        BeanUtils.copyProperties(assigneeDistributeDO, assigneeDistributeVO);
        return assigneeDistributeVO;
    }
}
