package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.BoardFeatureService;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.BoardFeatureRepository;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@Service
public class BoardFeatureServiceImpl implements BoardFeatureService {

    @Autowired
    private BoardFeatureMapper boardFeatureMapper;
    @Autowired
    private BoardDependMapper boardDependMapper;
    @Autowired
    private BoardFeatureRepository boardFeatureRepository;
    @Autowired
    private ArtMapper artMapper;
    @Autowired
    private PiMapper piMapper;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private BoardSprintAttrMapper boardSprintAttrMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private IssueFeignClient issueFeignClient;

    public static final String UPDATE_ERROR = "error.boardFeature.update";
    public static final String DELETE_ERROR = "error.boardFeature.deleteById";
    public static final String INSERT_ERROR = "error.boardFeature.create";
    public static final String EXIST_ERROR = "error.boardFeature.existData";
    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public BoardFeatureInfoDTO create(Long projectId, BoardFeatureCreateDTO createDTO) {
        BoardFeatureDO boardFeatureDO = modelMapper.map(createDTO, BoardFeatureDO.class);
        boardFeatureDO.setProgramId(projectId);
        checkExist(boardFeatureDO);
        handleRank(projectId, boardFeatureDO, createDTO.getBefore(), createDTO.getOutsetId());
        boardFeatureRepository.create(boardFeatureDO);
        return queryInfoById(projectId, boardFeatureDO.getId());
    }

    @Override
    public BoardFeatureInfoDTO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateDTO updateDTO) {
        BoardFeatureDO boardFeatureDO = boardFeatureRepository.queryById(projectId, boardFeatureId);
        BoardFeatureDO update = modelMapper.map(updateDTO, BoardFeatureDO.class);
        update.setFeatureId(boardFeatureDO.getFeatureId());
        update.setProgramId(projectId);
        BoardFeatureDO select = boardFeatureMapper.selectOne(update);
        if (select != null && !select.getId().equals(boardFeatureId)) {
            throw new CommonException(EXIST_ERROR);
        }
        update.setId(boardFeatureId);
        handleRank(projectId, update, updateDTO.getBefore(), updateDTO.getOutsetId());
        boardFeatureRepository.update(update);
        return queryInfoById(projectId, boardFeatureId);
    }

    /**
     * 判断是否已经存在
     *
     * @param boardFeatureDO
     */
    private void checkExist(BoardFeatureDO boardFeatureDO) {
        if (!boardFeatureMapper.select(boardFeatureDO).isEmpty()) {
            throw new CommonException(EXIST_ERROR);
        }
    }

    /**
     * 处理rank值
     *
     * @param projectId
     * @param boardFeatureDO
     * @param before
     * @param outSetId
     */
    private void handleRank(Long projectId, BoardFeatureDO boardFeatureDO, Boolean before, Long outSetId) {
        if (outSetId.equals(0L)) {
            String rank = RankUtil.mid();
            boardFeatureDO.setRank(rank);
        } else if (before) {
            String outSetRank = queryById(projectId, outSetId).getRank();
            boardFeatureDO.setRank(RankUtil.genNext(outSetRank));
        } else {
            String outSetRank = queryById(projectId, outSetId).getRank();
            String rightRank = boardFeatureMapper.queryRightRank(boardFeatureDO, outSetRank);
            if (rightRank == null) {
                boardFeatureDO.setRank(RankUtil.genPre(outSetRank));
            } else {
                boardFeatureDO.setRank(RankUtil.between(outSetRank, rightRank));
            }
        }
    }

    @Override
    public BoardFeatureDTO queryById(Long projectId, Long boardFeatureId) {
        return modelMapper.map(boardFeatureRepository.queryById(projectId, boardFeatureId), BoardFeatureDTO.class);
    }

    @Override
    public BoardFeatureInfoDTO queryInfoById(Long projectId, Long boardFeatureId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        BoardFeatureInfoDTO info = boardFeatureRepository.queryInfoById(projectId, boardFeatureId);
        Map<Long, IssueTypeDTO> issueTypeMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        info.setIssueTypeDTO(issueTypeMap.get(info.getIssueTypeId()));
        return info;
    }

    @Override
    public void deleteById(Long projectId, Long boardFeatureId) {
        boardFeatureRepository.checkId(projectId, boardFeatureId);
        boardFeatureRepository.delete(boardFeatureId);
        //删除依赖关系
        boardDependMapper.deleteByBoardFeatureId(projectId, boardFeatureId);
    }

    @Override
    public ProgramBoardInfoDTO queryBoardInfo(Long projectId) {
        ProgramBoardInfoDTO programBoardInfo = new ProgramBoardInfoDTO();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);

        //判断是项目还是项目群，以此来获取programId
        Long programId = projectId;
        ProjectDTO program = userFeignClient.getGroupInfoByEnableProject(organizationId, projectId).getBody();
        if (program != null) {
            programId = program.getId();
        }

        //获取当前活跃的art
        ArtDO activeArt = artMapper.selectActiveArt(programId);
        if (activeArt == null) {
            return programBoardInfo;
        }
        //获取当前活跃的pi
        PiDO activePi = piMapper.selectActivePi(programId, activeArt.getId());
        if (activePi == null) {
            return programBoardInfo;
        }
        Long piId = activePi.getId();
        programBoardInfo.setPiId(piId);
        programBoardInfo.setPiCode(activePi.getCode() + "-" + activePi.getName());
        //获取冲刺信息
        Map<Long, Integer> columnWidthMap = boardSprintAttrMapper.queryByProgramId(programId).stream().collect(Collectors.toMap(BoardSprintAttrDO::getSprintId, BoardSprintAttrDO::getColumnWidth));
        List<SprintDO> sprints = sprintMapper.selectListByPiId(programId, piId);
        List<ProgramBoardSprintInfoDTO> sprintInfos = new ArrayList<>(sprints.size());
        for (SprintDO sprint : sprints) {
            ProgramBoardSprintInfoDTO sprintInfo = new ProgramBoardSprintInfoDTO();
            sprintInfo.setSprintId(sprint.getSprintId());
            sprintInfo.setSprintName(sprint.getSprintName());
            Integer columnWidth = columnWidthMap.getOrDefault(sprint.getSprintId(), 1);
            sprintInfo.setColumnWidth(columnWidth);
            sprintInfos.add(sprintInfo);
        }
        //获取团队信息
        List<ProjectRelationshipDTO> projectRelationships = userFeignClient.getProjUnderGroup(organizationId, programId, true).getBody();
        //获取公告板特性信息
        List<BoardFeatureInfoDTO> boardFeatureInfos = boardFeatureMapper.queryInfoByPiId(programId, piId);
        Map<Long, IssueTypeDTO> issueTypeMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        for (BoardFeatureInfoDTO boardFeatureInfo : boardFeatureInfos) {
            boardFeatureInfo.setIssueTypeDTO(issueTypeMap.get(boardFeatureInfo.getIssueTypeId()));
        }
        Map<Long, Map<Long, List<BoardFeatureInfoDTO>>> teamFeatureInfoMap = boardFeatureInfos.stream().collect(Collectors.groupingBy(BoardFeatureInfoDTO::getTeamProjectId, Collectors.groupingBy(BoardFeatureInfoDTO::getSprintId)));
        List<ProgramBoardTeamInfoDTO> teamProjects = new ArrayList<>(projectRelationships.size());
        handleTeamData(projectRelationships, teamFeatureInfoMap, teamProjects, sprints);
        //获取依赖关系
        List<BoardDependInfoDTO> boardDependInfos = boardDependMapper.queryInfoByPiId(programId, piId);
        programBoardInfo.setSprints(sprintInfos);
        programBoardInfo.setTeamProjects(teamProjects);
        programBoardInfo.setBoardDepends(boardDependInfos);
        return programBoardInfo;
    }

    /**
     * 处理团队具体数据
     *
     * @param projectRelationshipDTOs
     * @param teamFeatureInfoMap
     * @param teamProjects
     */
    private void handleTeamData(List<ProjectRelationshipDTO> projectRelationshipDTOs, Map<Long, Map<Long, List<BoardFeatureInfoDTO>>> teamFeatureInfoMap, List<ProgramBoardTeamInfoDTO> teamProjects, List<SprintDO> sprints) {
        for (ProjectRelationshipDTO relationshipDTO : projectRelationshipDTOs) {
            //获取每个团队每个冲刺的公告板特性信息
            Map<Long, List<BoardFeatureInfoDTO>> sprintFeatureInfoMap = teamFeatureInfoMap.get(relationshipDTO.getProjectId());
            List<ProgramBoardTeamSprintInfoDTO> teamSprintInfos = new ArrayList<>(sprints.size());
            if (sprintFeatureInfoMap != null) {
                for (SprintDO sprint : sprints) {
                    List<BoardFeatureInfoDTO> boardFeatures = sprintFeatureInfoMap.getOrDefault(sprint.getSprintId(), new ArrayList<>());
                    ProgramBoardTeamSprintInfoDTO teamSprintInfo = new ProgramBoardTeamSprintInfoDTO();
                    teamSprintInfo.setSprintId(sprint.getSprintId());
                    teamSprintInfo.setBoardFeatures(boardFeatures);
                    teamSprintInfos.add(teamSprintInfo);
                }
            } else {
                for (SprintDO sprint : sprints) {
                    ProgramBoardTeamSprintInfoDTO teamSprintInfo = new ProgramBoardTeamSprintInfoDTO();
                    teamSprintInfo.setSprintId(sprint.getSprintId());
                    teamSprintInfo.setBoardFeatures(new ArrayList<>());
                    teamSprintInfos.add(teamSprintInfo);
                }
            }
            ProgramBoardTeamInfoDTO teamProject = new ProgramBoardTeamInfoDTO();
            teamProject.setProjectId(relationshipDTO.getProjectId());
            teamProject.setProjectName(relationshipDTO.getProjName());
            teamProject.setTeamSprints(teamSprintInfos);
            teamProjects.add(teamProject);
        }
    }
}
