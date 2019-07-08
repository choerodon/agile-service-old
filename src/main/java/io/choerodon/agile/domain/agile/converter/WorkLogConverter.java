package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.WorkLogDTO;
import io.choerodon.agile.domain.agile.entity.WorkLogE;
import io.choerodon.agile.infra.dataobject.WorkLogDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class WorkLogConverter implements ConvertorI<WorkLogE, WorkLogDO, WorkLogDTO> {

    @Override
    public WorkLogE dtoToEntity(WorkLogDTO workLogDTO) {
        WorkLogE workLogE = new WorkLogE();
        BeanUtils.copyProperties(workLogDTO, workLogE);
        return workLogE;
    }

    @Override
    public WorkLogDTO entityToDto(WorkLogE workLogE) {
        WorkLogDTO workLogDTO = new WorkLogDTO();
        BeanUtils.copyProperties(workLogE, workLogDTO);
        return workLogDTO;
    }

    @Override
    public WorkLogE doToEntity(WorkLogDO workLogDO) {
        WorkLogE workLogE = new WorkLogE();
        BeanUtils.copyProperties(workLogDO, workLogE);
        return workLogE;
    }

    @Override
    public WorkLogDO entityToDo(WorkLogE workLogE) {
        WorkLogDO workLogDO = new WorkLogDO();
        BeanUtils.copyProperties(workLogE, workLogDO);
        return workLogDO;
    }

    @Override
    public WorkLogDTO doToDto(WorkLogDO workLogDO) {
        WorkLogDTO workLogDTO = new WorkLogDTO();
        BeanUtils.copyProperties(workLogDO, workLogDTO);
        workLogDTO.setUserId(workLogDO.getCreatedBy());
        return workLogDTO;
    }

    @Override
    public WorkLogDO dtoToDo(WorkLogDTO workLogDTO) {
        WorkLogDO workLogDO = new WorkLogDO();
        BeanUtils.copyProperties(workLogDTO, workLogDO);
        return workLogDO;
    }
}
