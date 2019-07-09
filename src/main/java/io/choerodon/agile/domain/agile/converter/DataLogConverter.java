package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.DataLogVO;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.infra.dataobject.DataLogDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DataLogConverter implements ConvertorI<DataLogE, DataLogDTO, DataLogVO> {

    @Override
    public DataLogE dtoToEntity(DataLogVO dataLogVO) {
        DataLogE dataLogE = new DataLogE();
        BeanUtils.copyProperties(dataLogVO, dataLogE);
        return dataLogE;
    }

    @Override
    public DataLogVO entityToDto(DataLogE dataLogE) {
        DataLogVO dataLogVO = new DataLogVO();
        BeanUtils.copyProperties(dataLogE, dataLogVO);
        return dataLogVO;
    }

    @Override
    public DataLogE doToEntity(DataLogDTO dataLogDTO) {
        DataLogE dataLogE = new DataLogE();
        BeanUtils.copyProperties(dataLogDTO, dataLogE);
        return dataLogE;
    }

    @Override
    public DataLogDTO entityToDo(DataLogE dataLogE) {
        DataLogDTO dataLogDTO = new DataLogDTO();
        BeanUtils.copyProperties(dataLogE, dataLogDTO);
        return dataLogDTO;
    }

    @Override
    public DataLogVO doToDto(DataLogDTO dataLogDTO) {
        DataLogVO dataLogVO = new DataLogVO();
        BeanUtils.copyProperties(dataLogDTO, dataLogVO);
        return dataLogVO;
    }

    @Override
    public DataLogDTO dtoToDo(DataLogVO dataLogVO) {
        DataLogDTO dataLogDTO = new DataLogDTO();
        BeanUtils.copyProperties(dataLogVO, dataLogDTO);
        return dataLogDTO;
    }
}
