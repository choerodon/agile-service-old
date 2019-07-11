package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.ArtValidator;
import io.choerodon.agile.app.assembler.ArtAssembler;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.repository.PiRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDTO;
import io.choerodon.agile.infra.dataobject.PiDTO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.agile.infra.mapper.PiMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    private final ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public ArtDTO createArt(Long programId, ArtDTO artDTO) {
        artValidator.checkArtCreate(artDTO);
        artDTO.setStatusCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        artDTO.setCode(ConvertUtil.getCode(programId));
        artDTO.setStatusCode(ART_TODO);
        if (artMapper.insert(artDTO) != 1) {
            throw new CommonException("error.art.insert");
        }
        return artMapper.selectByPrimaryKey(artDTO.getId());
    }

    private void startArtFirstPI(Long programId, Long artId) {
        PiDTO piDTO = piMapper.selectArtFirstPi(programId, artId);
        if (piDTO != null) {
            PiVO piVO = new PiVO();
            piVO.setProgramId(programId);
            piVO.setArtId(artId);
            piVO.setId(piDTO.getId());
            piVO.setObjectVersionNumber(piDTO.getObjectVersionNumber());
            piService.startPi(programId, piVO);
        }
    }

    @Override
    public ArtDTO startArt(Long programId, ArtVO artVO) {
        artValidator.checkArtStart(artVO);
        if (artMapper.updateByPrimaryKeySelective(new ArtDTO(programId, artVO.getId(), ART_DOING, artVO.getObjectVersionNumber())) != 1) {
            throw new CommonException("error.art.update");
        }
        ArtDTO artDTO = artMapper.selectByPrimaryKey(artVO.getId());
        piService.createPi(programId, artDTO, artDTO.getStartDate());
        // 开启ART第一个PI
        startArtFirstPI(programId, artDTO.getId());
        return artDTO;
    }

    @Override
    public ArtDTO stopArt(Long programId, ArtVO artVO, Boolean onlySelectEnable) {
        artValidator.checkArtStop(artVO);
        if (artMapper.updateByPrimaryKeySelective(new ArtDTO(programId, artVO.getId(), ART_STOP, artVO.getObjectVersionNumber())) != 1) {
            throw new CommonException("error.art.update");
        }
        List<PiDTO> piDTOList = piMapper.selectUnDonePiDOList(programId, artVO.getId());
        if (piDTOList != null) {
            piDTOList.forEach(piDO -> {
                // deal uncomplete feature to target pi
                piService.dealUnCompleteFeature(programId, piDO.getId(), 0L);
                // deal projects' sprints complete
                piService.completeProjectsSprints(programId, piDO.getId(), onlySelectEnable);
                // update pi status: done
                PiE update = new PiE(programId, piDO.getId(), PI_DONE, piDO.getObjectVersionNumber());
                update.setActualStartDate(new Date());
                update.setActualEndDate(new Date());
                piRepository.updateBySelective(update);
            });
        }
        return artMapper.selectByPrimaryKey(artVO.getId());
    }

    private Boolean checkArtNameUpdate(Long programId, Long artId, String artName) {
        ArtDTO artDTO = artMapper.selectByPrimaryKey(artId);
        if (artName.equals(artDTO.getName())) {
            return false;
        }
        ArtDTO check = new ArtDTO();
        check.setProgramId(programId);
        check.setName(artName);
        List<ArtDTO> artDTOList = artMapper.select(check);
        return artDTOList != null && !artDTOList.isEmpty();
    }

    @Override
    public ArtDTO updateArt(Long programId, ArtVO artVO) {
        artValidator.checkArtUpdate(artVO);
        if (artVO.getName() != null && checkArtNameUpdate(programId, artVO.getId(), artVO.getName())) {
            throw new CommonException("error.artName.exist");
        }
        ArtDTO artDTO = new ArtDTO();
        BeanUtils.copyProperties(artVO, artDTO);
        if (artMapper.updateByPrimaryKeySelective(artDTO) != 1) {
            throw new CommonException("error.art.update");
        }
        artDTO.setCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        return artDTO;
    }


    @Override
    public PageInfo<ArtDTO> queryArtList(Long programId, PageRequest pageRequest) {
        PageInfo<ArtDTO> artDTOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize()).doSelectPageInfo(() -> artMapper.selectArtList(programId));
        if (artDTOPage.getList() != null && !artDTOPage.getList().isEmpty()) {
            return artDTOPage;
        } else {
            return new PageInfo<>(new ArrayList<>());
        }
    }

    @Override
    public ArtVO queryArt(Long programId, Long id) {
        ArtDTO artDTO = artMapper.selectByPrimaryKey(id);
        if (artDTO == null) {
            throw new CommonException("error.art.select");
        }
        artDTO.setCode(projectInfoMapper.selectProjectCodeByProjectId(programId));
        return artAssembler.artDOTODTO(artDTO);
    }

    @Override
    public void createOtherPi(Long programId, PiCreateVO piCreateVO) {
        Long artId = piCreateVO.getArtId();
        Date startDate = piCreateVO.getStartDate();
        // auto create pi with piNumber
        ArtDTO artDTO = artMapper.selectByPrimaryKey(artId);
        piService.createPi(programId, artDTO, startDate);
    }

    @Override
    public List<PiCalendarDTO> queryArtCalendar(Long programId, Long artId) {
        List<PiCalendarDTO> piCalendarDTOList = artMapper.selectArtCalendar(programId, artId);
        if (piCalendarDTOList != null && !piCalendarDTOList.isEmpty()) {
            return piCalendarDTOList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ArtStopVO beforeStop(Long programId, Long id) {
        ArtStopVO result = new ArtStopVO();
        result.setActivePiVO(modelMapper.map(piMapper.selectActivePi(programId, id), PiVO.class));
        result.setCompletedPiCount(piMapper.selectPiCountByOptions(programId, id, "done"));
        result.setTodoPiCount(piMapper.selectPiCountByOptions(programId, id, "todo"));
        result.setRelatedFeatureCount(piMapper.selectRelatedFeatureCount(programId, id));
        return result;
    }

    @Override
    public Boolean checkName(Long programId, String artName) {
        ArtDTO artDTO = new ArtDTO();
        artDTO.setProgramId(programId);
        artDTO.setName(artName);
        List<ArtDTO> artDTOList = artMapper.select(artDTO);
        return artDTOList != null && !artDTOList.isEmpty();
    }

    @Override
    public List<ArtVO> queryAllArtList(Long programId) {
        List<ArtDTO> artDTOList = artMapper.selectArtList(programId);
        if (artDTOList != null && !artDTOList.isEmpty()) {
            return modelMapper.map(artDTOList, new TypeToken<List<ArtVO>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ArtVO queryActiveArt(Long programId) {
        ArtDTO artDTO = artMapper.selectActiveArt(programId);
        if (artDTO != null) {
            return modelMapper.map(artDTO, ArtVO.class);
        } else {
            return new ArtVO();
        }
    }
}
