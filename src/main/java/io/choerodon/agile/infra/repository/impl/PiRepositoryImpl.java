package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.repository.PiRepository;
import io.choerodon.agile.infra.dataobject.PiDTO;
import io.choerodon.agile.infra.mapper.PiMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiRepositoryImpl implements PiRepository {

    @Autowired
    private PiMapper piMapper;

    @Override
    public PiE create(PiE piE) {
        PiDTO piDTO = ConvertHelper.convert(piE, PiDTO.class);
        if (piMapper.insert(piDTO) != 1) {
            throw new CommonException("error.pi.insert");
        }
        return ConvertHelper.convert(piMapper.selectByPrimaryKey(piDTO.getId()), PiE.class);
    }

    @Override
    public PiE updateBySelective(PiE piE) {
        PiDTO piDTO = ConvertHelper.convert(piE, PiDTO.class);
        if (piMapper.updateByPrimaryKeySelective(piDTO) != 1) {
            throw new CommonException("error.pi.update");
        }
        return ConvertHelper.convert(piMapper.selectByPrimaryKey(piDTO.getId()), PiE.class);
    }

    @Override
    public void delete(Long piId) {
        PiDTO piDTO = piMapper.selectByPrimaryKey(piId);
        if (piDTO == null) {
            throw new CommonException("error.PI.null");
        }
        if (piMapper.delete(piDTO) != 1) {
            throw new CommonException("error.PI.delete");
        }
    }
}
