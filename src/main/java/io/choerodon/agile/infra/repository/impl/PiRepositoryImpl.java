package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.domain.agile.repository.PiRepository;
import io.choerodon.agile.infra.dataobject.PiDO;
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
        PiDO piDO = ConvertHelper.convert(piE, PiDO.class);
        if (piMapper.insert(piDO) != 1) {
            throw new CommonException("error.pi.insert");
        }
        return ConvertHelper.convert(piMapper.selectByPrimaryKey(piDO.getId()), PiE.class);
    }

    @Override
    public PiE updateBySelective(PiE piE) {
        PiDO piDO = ConvertHelper.convert(piE, PiDO.class);
        if (piMapper.updateByPrimaryKeySelective(piDO) != 1) {
            throw new CommonException("error.pi.update");
        }
        return ConvertHelper.convert(piMapper.selectByPrimaryKey(piDO.getId()), PiE.class);
    }
}
