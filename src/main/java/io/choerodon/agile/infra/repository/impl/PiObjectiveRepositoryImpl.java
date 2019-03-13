package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.PiObjectiveE;
import io.choerodon.agile.domain.agile.repository.PiObjectiveRepository;
import io.choerodon.agile.infra.dataobject.PiObjectiveDO;
import io.choerodon.agile.infra.mapper.PiObjectiveMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PiObjectiveRepositoryImpl implements PiObjectiveRepository {

    @Autowired
    private PiObjectiveMapper piObjectiveMapper;

    @Override
    public PiObjectiveE create(PiObjectiveE piObjectiveE) {
        PiObjectiveDO piObjectiveDO = ConvertHelper.convert(piObjectiveE, PiObjectiveDO.class);
        if (piObjectiveMapper.insert(piObjectiveDO) != 1) {
            throw new CommonException("error.piObjective.insert");
        }
        return ConvertHelper.convert(piObjectiveMapper.selectByPrimaryKey(piObjectiveDO.getId()), PiObjectiveE.class);
    }

    @Override
    public PiObjectiveE updateBySelective(PiObjectiveE piObjectiveE) {
        PiObjectiveDO piObjectiveDO = ConvertHelper.convert(piObjectiveE, PiObjectiveDO.class);
        if (piObjectiveMapper.updateByPrimaryKeySelective(piObjectiveDO) != 1) {
            throw new CommonException("error.piObjective.insert");
        }
        return ConvertHelper.convert(piObjectiveMapper.selectByPrimaryKey(piObjectiveDO.getId()), PiObjectiveE.class);
    }

    @Override
    public void delete(Long piObjectiveId) {
        PiObjectiveDO piObjectiveDO = piObjectiveMapper.selectByPrimaryKey(piObjectiveId);
        if (piObjectiveDO == null) {
            throw new CommonException("error.piObjective.exist");
        }
        if (piObjectiveMapper.delete(piObjectiveDO) != 1) {
            throw new CommonException("error.piObjective.delete");
        }
    }
}
