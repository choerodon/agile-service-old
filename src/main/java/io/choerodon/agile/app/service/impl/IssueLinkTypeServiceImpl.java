package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueLinkTypeSearchDTO;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;

import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.base.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.assembler.IssueLinkTypeAssembler;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import io.choerodon.agile.infra.repository.IssueLinkTypeRepository;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.core.convertor.ConvertHelper;

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
    public PageInfo<IssueLinkTypeDTO> listIssueLinkType(Long projectId, Long issueLinkTypeId, IssueLinkTypeSearchDTO issueLinkTypeSearchDTO, PageRequest pageRequest) {
        return PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).
                doSelectPageInfo(() -> issueLinkTypeMapper.queryIssueLinkTypeByProjectId(projectId, issueLinkTypeId, issueLinkTypeSearchDTO.getLinkName(), issueLinkTypeSearchDTO.getContents()));
    }

    @Override
    public IssueLinkTypeDTO createIssueLinkType(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO) {
        IssueLinkTypeE issueLinkTypeE = issueLinkTypeAssembler.toTarget(issueLinkTypeCreateDTO, IssueLinkTypeE.class);
        return ConvertHelper.convert(issueLinkTypeRepository.create(issueLinkTypeE), IssueLinkTypeDTO.class);
    }

    @Override
    public IssueLinkTypeDTO updateIssueLinkType(IssueLinkTypeDTO issueLinkTypeDTO) {
        return ConvertHelper.convert(issueLinkTypeRepository.update(ConvertHelper.convert(issueLinkTypeDTO, IssueLinkTypeE.class)), IssueLinkTypeDTO.class);
    }

    @Override
    public int deleteIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId, Long projectId) {
        if (toIssueLinkTypeId != null) {
            issueLinkTypeRepository.batchUpdateRelToIssueLinkType(issueLinkTypeId, toIssueLinkTypeId);
        } else {
            issueLinkTypeRepository.deleteIssueLinkTypeRel(issueLinkTypeId);
        }
        return issueLinkTypeRepository.delete(issueLinkTypeId, projectId);
    }

    @Override
    public IssueLinkTypeDTO queryIssueLinkType(Long projectId, Long linkTypeId) {
        IssueLinkTypeDO issueLinkTypeDO = new IssueLinkTypeDO();
        issueLinkTypeDO.setProjectId(projectId);
        issueLinkTypeDO.setLinkTypeId(linkTypeId);
        return ConvertHelper.convert(issueLinkTypeMapper.selectOne(issueLinkTypeDO), IssueLinkTypeDTO.class);
    }

    @Override
    public void initIssueLinkType(Long projectId) {
        IssueLinkTypeE duplicate = new IssueLinkTypeE();
        duplicate.initDuplicate(projectId);
        IssueLinkTypeE blocks = new IssueLinkTypeE();
        blocks.initBlocks(projectId);
        IssueLinkTypeE relates = new IssueLinkTypeE();
        relates.initRelates(projectId);
        issueLinkTypeRepository.create(duplicate);
        issueLinkTypeRepository.create(blocks);
        issueLinkTypeRepository.create(relates);
    }

    @Override
    public boolean queryIssueLinkTypeName(Long projectId, String issueLinkTypeName, Long issueLinkTypeId) {
        return issueLinkTypeMapper.queryIssueLinkTypeName(projectId, issueLinkTypeName, issueLinkTypeId) == 0;
    }
}
