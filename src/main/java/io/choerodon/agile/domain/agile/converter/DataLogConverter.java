package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.DataLogDTO;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.dataobject.DataLogDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DataLogConverter implements ConvertorI<DataLogE, DataLogDO, DataLogDTO> {

    @Override
    public DataLogE dtoToEntity(DataLogDTO dataLogDTO) {
        DataLogE dataLogE = new DataLogE();
        BeanUtils.copyProperties(dataLogDTO, dataLogE);
        return dataLogE;
    }

    @Override
    public DataLogDTO entityToDto(DataLogE dataLogE) {
        DataLogDTO dataLogDTO = new DataLogDTO();
        BeanUtils.copyProperties(dataLogE, dataLogDTO);
        return dataLogDTO;
    }

    @Override
    public DataLogE doToEntity(DataLogDO dataLogDO) {
        DataLogE dataLogE = new DataLogE();
        BeanUtils.copyProperties(dataLogDO, dataLogE);
        return dataLogE;
    }

    @Override
    public DataLogDO entityToDo(DataLogE dataLogE) {
        DataLogDO dataLogDO = new DataLogDO();
        BeanUtils.copyProperties(dataLogE, dataLogDO);
        return dataLogDO;
    }

    @Override
    public DataLogDTO doToDto(DataLogDO dataLogDO) {
        DataLogDTO dataLogDTO = new DataLogDTO();
        BeanUtils.copyProperties(dataLogDO, dataLogDTO);
        return dataLogDTO;
    }

    @Override
    public DataLogDO dtoToDo(DataLogDTO dataLogDTO) {
        DataLogDO dataLogDO = new DataLogDO();
        BeanUtils.copyProperties(dataLogDTO, dataLogDO);
        return dataLogDO;
    }
}
