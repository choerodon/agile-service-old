//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
//import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
//import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
//import io.choerodon.agile.infra.repository.IssueLinkTypeRepository;
//import io.choerodon.agile.infra.mapper.IssueLinkMapper;
//import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/6/14
// */
//@Component
//public class IssueLinkTypeRepositoryImpl implements IssueLinkTypeRepository {
//
//    private static final String UPDATE_ERROR = "error.IssueLinkType.update";
//    private static final String INSERT_ERROR = "error.IssueLinkType.create";
//
//    @Autowired
//    private IssueLinkTypeMapper issueLinkTypeMapper;
//    @Autowired
//    private IssueLinkMapper issueLinkMapper;
//
//    @Override
//    public IssueLinkTypeE update(IssueLinkTypeE issueLinkTypeE) {
//        IssueLinkTypeDTO issueLinkTypeDTO = ConvertHelper.convert(issueLinkTypeE, IssueLinkTypeDTO.class);
//        if (issueLinkTypeMapper.updateByPrimaryKeySelective(issueLinkTypeDTO) != 1) {
//            throw new CommonException(UPDATE_ERROR);
//        }
//        return ConvertHelper.convert(issueLinkTypeMapper.selectByPrimaryKey(issueLinkTypeDTO.getLinkTypeId()), IssueLinkTypeE.class);
//    }
//
//    @Override
//    public IssueLinkTypeE create(IssueLinkTypeE issueLinkTypeE) {
//        IssueLinkTypeDTO issueLinkTypeDTO = ConvertHelper.convert(issueLinkTypeE, IssueLinkTypeDTO.class);
//        if (issueLinkTypeMapper.insert(issueLinkTypeDTO) != 1) {
//            throw new CommonException(INSERT_ERROR);
//        }
//        IssueLinkTypeDTO query = new IssueLinkTypeDTO();
//        query.setLinkTypeId(issueLinkTypeDTO.getLinkTypeId());
//        return ConvertHelper.convert(issueLinkTypeMapper.selectByPrimaryKey(issueLinkTypeDTO.getLinkTypeId()), IssueLinkTypeE.class);
//    }
//
//    @Override
//    public int delete(Long linkTypeId, Long projectId) {
//        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
//        issueLinkTypeDTO.setLinkTypeId(linkTypeId);
//        issueLinkTypeDTO.setProjectId(projectId);
//        return issueLinkTypeMapper.delete(issueLinkTypeDTO);
//    }
//
//    @Override
//    public int deleteIssueLinkTypeRel(Long issueLinkTypeId) {
//        IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
//        issueLinkDTO.setLinkTypeId(issueLinkTypeId);
//        return issueLinkMapper.delete(issueLinkDTO);
//    }
//
//    @Override
//    public int batchUpdateRelToIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId) {
//        return issueLinkMapper.batchUpdateRelToIssueLinkType(issueLinkTypeId, toIssueLinkTypeId);
//    }
//}
