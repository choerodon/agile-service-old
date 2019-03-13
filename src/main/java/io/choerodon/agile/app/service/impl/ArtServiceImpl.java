package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ArtDTO;
import io.choerodon.agile.api.validator.ArtValidator;
import io.choerodon.agile.app.assembler.ArtAssembler;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.domain.agile.repository.ArtRepository;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ArtServiceImpl implements ArtService {

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private ArtMapper artMapper;

    @Autowired
    private ArtAssembler artAssembler;

    @Autowired
    private ArtValidator artValidator;

    @Autowired
    private PiService piService;

    @Override
    public ArtDTO createArt(Long ProgramId, ArtDTO artDTO) {
        return ConvertHelper.convert(artRepository.create(ConvertHelper.convert(artDTO, ArtE.class)), ArtDTO.class);
    }

    @Override
    public ArtDTO updateArt(Long ProgramId, ArtDTO artDTO) {
        return ConvertHelper.convert(artRepository.updateBySelective(ConvertHelper.convert(artDTO, ArtE.class)), ArtDTO.class);
    }

    @Override
    public void deleteArt(Long programId, Long id) {
        artRepository.delete(id);
    }

    @Override
    public Page<ArtDTO> queryArtList(Long programId, PageRequest pageRequest) {
        Page<ArtDO> artDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                artMapper.selectArtList(programId));
        Page<ArtDTO> dtoPage = new Page<>();
        dtoPage.setNumber(artDOPage.getNumber());
        dtoPage.setSize(artDOPage.getSize());
        dtoPage.setTotalElements(artDOPage.getTotalElements());
        dtoPage.setTotalPages(artDOPage.getTotalPages());
        dtoPage.setNumberOfElements(artDOPage.getNumberOfElements());
        if (artDOPage.getContent() != null && !artDOPage.getContent().isEmpty()) {
            dtoPage.setContent(ConvertHelper.convertList(artDOPage.getContent(), ArtDTO.class));
        }
        return dtoPage;
    }

    @Override
    public ArtDTO queryArt(Long programId, Long id) {
        ArtDO artDO = artMapper.selectByPrimaryKey(id);
        if (artDO == null) {
            throw new CommonException("error.art.select");
        }
        return artAssembler.artDOTODTO(artDO);
    }

    @Override
    public ArtDTO releaseArt(Long programId, Long artId, Long piNumber) {
        // check art exist
        if (!artValidator.checkArtExistAndEbaled(programId, artId)) {
            throw new CommonException("error.art.existOrEnabled");
        }
        // auto create pi with piNumber
        ArtDO artDO = artMapper.selectByPrimaryKey(artId);
        piService.createPi(programId, piNumber, artDO);
        // release art
        ArtDO artDORe = artMapper.selectByPrimaryKey(artId);
        ArtE artE = new ArtE(programId, artId, true, artDORe.getObjectVersionNumber());
        return ConvertHelper.convert(artRepository.updateBySelective(artE), ArtDTO.class);
    }
}
