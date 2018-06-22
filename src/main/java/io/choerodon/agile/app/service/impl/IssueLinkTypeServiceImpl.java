package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.assembler.IssueLinkTypeAssembler;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import io.choerodon.agile.domain.agile.repository.IssueLinkTypeRepository;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueLinkTypeServiceImpl implements IssueLinkTypeService {

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;
    @Autowired
    private IssueLinkTypeRepository issueLinkTypeRepository;
    @Autowired
    private IssueLinkTypeAssembler issueLinkTypeAssembler;

    @Override
    public List<IssueLinkTypeDTO> listIssueLinkType() {
        return ConvertHelper.convertList(issueLinkTypeMapper.selectAll(), IssueLinkTypeDTO.class);
    }

    @Override
    public IssueLinkTypeDTO createIssueLinkType(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO) {
        IssueLinkTypeE issueLinkTypeE = issueLinkTypeAssembler.createDtoToE(issueLinkTypeCreateDTO);
        return ConvertHelper.convert(issueLinkTypeRepository.create(issueLinkTypeE), IssueLinkTypeDTO.class);
    }

    @Override
    public IssueLinkTypeDTO updateIssueLinkType(IssueLinkTypeDTO issueLinkTypeDTO) {
        return ConvertHelper.convert(issueLinkTypeRepository.update(ConvertHelper.convert(issueLinkTypeDTO, IssueLinkTypeE.class)), IssueLinkTypeDTO.class);
    }

    @Override
    public int deleteIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId) {
        if(toIssueLinkTypeId!=null){
            issueLinkTypeRepository.batchUpdateRelToIssueLinkType(issueLinkTypeId,toIssueLinkTypeId);
        }else{
            issueLinkTypeRepository.deleteIssueLinkTypeRel(issueLinkTypeId);
        }
        return issueLinkTypeRepository.delete(issueLinkTypeId);
    }
}
