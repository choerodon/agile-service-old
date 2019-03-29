package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ArtDTO;
import io.choerodon.agile.api.dto.PiCreateDTO;
import io.choerodon.agile.api.validator.ArtValidator;
import io.choerodon.agile.app.assembler.ArtAssembler;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.domain.agile.repository.ArtRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.dataobject.PiCalendarDO;
import io.choerodon.agile.infra.dataobject.PiDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.agile.infra.mapper.PiMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class ArtServiceImpl implements ArtService {

    private static final String ART_TODO = "todo";

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

    @Autowired
    private PiMapper piMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public ArtDTO createArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtCreate(artDTO);
        artDTO.setStatusCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        artDTO.setCode(ConvertUtil.getCode(programId));
        ArtE artE = artRepository.create(ConvertHelper.convert(artDTO, ArtE.class));
        piService.createPi(programId, ConvertHelper.convert(artE, ArtDO.class), new Date());
        return ConvertHelper.convert(artE, ArtDTO.class);
    }

    @Override
    public ArtDTO updateArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtUpdate(programId, artDTO);
        ArtE result = artRepository.updateBySelective(ConvertHelper.convert(artDTO, ArtE.class));
        result.setCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        return ConvertHelper.convert(result, ArtDTO.class);
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
        artDO.setCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        return artAssembler.artDOTODTO(artDO);
    }

    @Override
    public void createOtherPi(Long programId, PiCreateDTO piCreateDTO) {
        Long artId = piCreateDTO.getArtId();
        Date startDate = piCreateDTO.getStartDate();
        // auto create pi with piNumber
        ArtDO artDO = artMapper.selectByPrimaryKey(artId);
        piService.createPi(programId, artDO, startDate);
    }

    @Override
    public List<PiCalendarDO> queryArtCalendar(Long programId, Long artId) {
        return artMapper.selectArtCalendar(programId, artId);
    }

    @Override
    public Boolean beforeComplete(Long programId, Long id) {
        List<PiDO> piDOList = piMapper.selectNotDonePi(programId, id);
        return piDOList == null || piDOList.isEmpty();
    }
}
