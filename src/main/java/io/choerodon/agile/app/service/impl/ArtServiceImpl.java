package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.ArtValidator;
import io.choerodon.agile.app.assembler.ArtAssembler;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.domain.agile.repository.ArtRepository;
import io.choerodon.agile.domain.agile.repository.PiRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArtServiceImpl implements ArtService {

    private static final String ART_TODO = "todo";
    private static final String ART_DOING = "doing";
    private static final String ART_STOP = "stop";
    private static final String PI_DONE = "done";

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

    @Autowired
    private PiRepository piRepository;

    @Override
    public ArtDTO createArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtCreate(artDTO);
        artDTO.setStatusCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        artDTO.setCode(ConvertUtil.getCode(programId));
        artDTO.setStatusCode(ART_TODO);
        ArtE artE = artRepository.create(ConvertHelper.convert(artDTO, ArtE.class));
        return ConvertHelper.convert(artE, ArtDTO.class);
    }

    private void startArtFirstPI(Long programId, Long artId) {
        PiDO piDO = piMapper.selectArtFirstPi(programId, artId);
        if (piDO != null) {
            PiDTO piDTO = new PiDTO();
            piDTO.setProgramId(programId);
            piDTO.setArtId(artId);
            piDTO.setId(piDO.getId());
            piDTO.setObjectVersionNumber(piDO.getObjectVersionNumber());
            piService.startPi(programId, piDTO);
        }
    }

    @Override
    public ArtDTO startArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtStart(artDTO);
        artRepository.updateBySelective(new ArtE(programId, artDTO.getId(), ART_DOING, artDTO.getObjectVersionNumber()));
        ArtDO artDO = artMapper.selectByPrimaryKey(artDTO.getId());
        piService.createPi(programId, artDO, artDO.getStartDate());
        // 开启ART第一个PI
        startArtFirstPI(programId, artDO.getId());
        return ConvertHelper.convert(artDO, ArtDTO.class);
    }

    @Override
    public ArtDTO stopArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtStop(artDTO);
        ArtE artE = artRepository.updateBySelective(new ArtE(programId, artDTO.getId(), ART_STOP, artDTO.getObjectVersionNumber()));
        List<PiDO> piDOList = piMapper.selectUnDonePiDOList(programId, artDTO.getId());
        if (piDOList != null) {
            piDOList.forEach(piDO -> {
                // deal uncomplete feature to target pi
                piService.dealUnCompleteFeature(programId, piDO.getId(), 0L);
                // deal projects' sprints complete
                piService.completeProjectsSprints(programId, piDO.getId());
                // update pi status: done
                PiE update = new PiE(programId, piDO.getId(), PI_DONE, piDO.getObjectVersionNumber());
                update.setActualStartDate(new Date());
                update.setActualEndDate(new Date());
                piRepository.updateBySelective(update);
            });
        }
        return ConvertHelper.convert(artE, ArtDTO.class);
    }

    private Boolean checkArtNameUpdate(Long programId, Long artId, String artName) {
        ArtDO artDO = artMapper.selectByPrimaryKey(artId);
        if (artName.equals(artDO.getName())) {
            return false;
        }
        ArtDO check = new ArtDO();
        check.setProgramId(programId);
        check.setName(artName);
        List<ArtDO> artDOList = artMapper.select(check);
        return artDOList != null && !artDOList.isEmpty();
    }

    @Override
    public ArtDTO updateArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtUpdate(artDTO);
        if (artDTO.getName() != null && checkArtNameUpdate(programId, artDTO.getId(), artDTO.getName())) {
            throw new CommonException("error.artName.exist");
        }
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
    public List<PiCalendarDTO> queryArtCalendar(Long programId, Long artId) {
        List<PiCalendarDO> piCalendarDOList = artMapper.selectArtCalendar(programId, artId);
        if (piCalendarDOList != null && !piCalendarDOList.isEmpty()) {
            return artAssembler.piCalendarDOToDTO(piCalendarDOList);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ArtStopDTO beforeStop(Long programId, Long id) {
        ArtStopDTO result = new ArtStopDTO();
        result.setActivePiDTO(ConvertHelper.convert(piMapper.selectActivePi(programId, id), PiDTO.class));
        result.setCompletedPiCount(piMapper.selectPiCountByOptions(programId, id, "done"));
        result.setTodoPiCount(piMapper.selectPiCountByOptions(programId, id, "todo"));
        result.setRelatedFeatureCount(piMapper.selectRelatedFeatureCount(programId, id));
        return result;
    }

    @Override
    public Boolean checkName(Long programId, String artName) {
        ArtDO artDO = new ArtDO();
        artDO.setProgramId(programId);
        artDO.setName(artName);
        List<ArtDO> artDOList = artMapper.select(artDO);
        return artDOList != null && !artDOList.isEmpty();
    }

    @Override
    public List<ArtDTO> queryAllArtList(Long programId) {
        List<ArtDO> artDOList = artMapper.selectArtList(programId);
        if (artDOList != null && !artDOList.isEmpty()) {
            return ConvertHelper.convertList(artDOList, ArtDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ArtDTO queryActiveArt(Long programId) {
        ArtDO artDO = artMapper.selectActiveArt(programId);
        if (artDO != null) {
            return ConvertHelper.convert(artDO, ArtDTO.class);
        } else {
            return new ArtDTO();
        }
    }
}
