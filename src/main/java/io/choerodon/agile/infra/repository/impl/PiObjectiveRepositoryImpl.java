//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.PiObjectiveE;
//import io.choerodon.agile.infra.repository.PiObjectiveRepository;
//import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;
//import io.choerodon.agile.infra.mapper.PiObjectiveMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PiObjectiveRepositoryImpl implements PiObjectiveRepository {
//
//    @Autowired
//    private PiObjectiveMapper piObjectiveMapper;
//
//    @Override
//    public PiObjectiveE create(PiObjectiveE piObjectiveE) {
//        PiObjectiveDTO piObjectiveDTO = ConvertHelper.convert(piObjectiveE, PiObjectiveDTO.class);
//        if (piObjectiveMapper.insert(piObjectiveDTO) != 1) {
//            throw new CommonException("error.piObjective.insert");
//        }
//        return ConvertHelper.convert(piObjectiveMapper.selectByPrimaryKey(piObjectiveDTO.getId()), PiObjectiveE.class);
//    }
//
//    @Override
//    public PiObjectiveE updateBySelective(PiObjectiveE piObjectiveE) {
//        PiObjectiveDTO piObjectiveDTO = ConvertHelper.convert(piObjectiveE, PiObjectiveDTO.class);
//        if (piObjectiveMapper.updateByPrimaryKeySelective(piObjectiveDTO) != 1) {
//            throw new CommonException("error.piObjective.insert");
//        }
//        return ConvertHelper.convert(piObjectiveMapper.selectByPrimaryKey(piObjectiveDTO.getId()), PiObjectiveE.class);
//    }
//
//    @Override
//    public void delete(Long piObjectiveId) {
//        PiObjectiveDTO piObjectiveDTO = piObjectiveMapper.selectByPrimaryKey(piObjectiveId);
//        if (piObjectiveDTO == null) {
//            throw new CommonException("error.piObjective.exist");
//        }
//        if (piObjectiveMapper.delete(piObjectiveDTO) != 1) {
//            throw new CommonException("error.piObjective.delete");
//        }
//    }
//}
