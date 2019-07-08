package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.BoardFeatureService;
import io.choerodon.agile.app.service.BoardTeamService;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.BoardFeatureRepository;
import io.choerodon.agile.infra.repository.BoardTeamRepository;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
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
    private BoardTeamRepository boardTeamRepository;
    @Autowired
    private BoardTeamService boardTeamService;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private IssueFeignClient issueFeignClient;

    private static final String UPDATE_ERROR = "error.boardFeature.update";
    private static final String DELETE_ERROR = "error.boardFeature.deleteById";
    private static final String INSERT_ERROR = "error.boardFeature.create";
    private static final String EXIST_ERROR = "error.boardFeature.existData";
    private static final String SPRINT_NOTFOUND_ERROR = "error.sprint.notFound";
    private static final String TEAM_NOTFOUND_ERROR = "error.teamProject.notFound";
    private static final String PROJECTRELATIONSHIPS = "projectRelationships";
    private static final String SPRINTS = "sprints";
    private static final String BOARDFEATURES = "boardFeatures";

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
        Long objectVersionNumber = updateDTO.getObjectVersionNumber();
        BoardFeatureDO boardFeatureDO = boardFeatureRepository.queryById(projectId, boardFeatureId);
        BoardFeatureDO update = modelMapper.map(updateDTO, BoardFeatureDO.class);
        update.setFeatureId(boardFeatureDO.getFeatureId());
        update.setProgramId(projectId);
        update.setObjectVersionNumber(null);
        BoardFeatureDO select = boardFeatureMapper.selectOne(update);
        if (select != null && !select.getId().equals(boardFeatureId)) {
            throw new CommonException(EXIST_ERROR);
        }
        update.setId(boardFeatureId);
        update.setObjectVersionNumber(objectVersionNumber);
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
    public void deleteByFeatureId(Long projectId, Long featureId) {
        //删除boardDepend
        boardDependMapper.deleteByFeatureId(projectId, featureId);
        //删除boardFeature
        boardFeatureMapper.deleteByFeatureId(projectId, featureId);
    }

    @Override
    public ProgramBoardInfoDTO queryBoardInfo(Long programId, ProgramBoardFilterDTO boardFilter) {
        ProgramBoardInfoDTO programBoardInfo = new ProgramBoardInfoDTO();
        Long organizationId = ConvertUtil.getOrganizationId(programId);
        //获取当前活跃的art
        ArtDTO activeArt = artMapper.selectActiveArt(programId);
        if (activeArt == null) {
            return programBoardInfo;
        }
        //获取当前活跃的pi
        PiDTO activePi = piMapper.selectActivePi(programId, activeArt.getId());
        if (activePi == null) {
            return programBoardInfo;
        }
        Long piId = activePi.getId();
        programBoardInfo.setPiId(piId);
        programBoardInfo.setPiCode(activePi.getCode() + "-" + activePi.getName());
        //获取冲刺信息
        Map<Long, Integer> columnWidthMap = boardSprintAttrMapper.queryByProgramId(programId).stream().collect(Collectors.toMap(BoardSprintAttrDO::getSprintId, BoardSprintAttrDO::getColumnWidth));
        List<SprintDO> sprints = sprintMapper.selectListByPiId(programId, piId);
        programBoardInfo.setFilterSprintList(modelMapper.map(sprints, new TypeToken<List<SprintDTO>>() {
        }.getType()));
        //获取团队信息
        List<ProjectRelationshipDTO> projectRelationships = userFeignClient.getProjUnderGroup(organizationId, programId, true).getBody();
        List<TeamProjectDTO> filterTeamList = new ArrayList<>(projectRelationships.size());
        for (ProjectRelationshipDTO rel : projectRelationships) {
            TeamProjectDTO teamProjectDTO = new TeamProjectDTO();
            teamProjectDTO.setTeamProjectId(rel.getProjectId());
            teamProjectDTO.setName(rel.getProjName());
            filterTeamList.add(teamProjectDTO);
        }
        programBoardInfo.setFilterTeamList(filterTeamList);
        //获取依赖关系，只获取有效团队的依赖关系
        List<BoardDependInfoDTO> boardDependInfos = boardDependMapper.queryInfoByPiId(programId, piId, projectRelationships.stream().map(ProjectRelationshipDTO::getProjectId).collect(Collectors.toList()));
        //获取公告板特性信息
        List<BoardFeatureInfoDTO> boardFeatureInfos = boardFeatureMapper.queryInfoByPiId(programId, piId).stream().filter(x -> x.getIssueTypeId() != null).collect(Collectors.toList());

        Map<String, Object> result = handleBoardFilter(boardFilter, boardDependInfos, boardFeatureInfos, sprints, projectRelationships);
        boardFeatureInfos = (List<BoardFeatureInfoDTO>) result.get(BOARDFEATURES);
        sprints = (List<SprintDO>) result.get(SPRINTS);
        projectRelationships = (List<ProjectRelationshipDTO>) result.get(PROJECTRELATIONSHIPS);

        List<ProgramBoardSprintInfoDTO> sprintInfos = new ArrayList<>(sprints.size());
        for (SprintDO sprint : sprints) {
            ProgramBoardSprintInfoDTO sprintInfo = new ProgramBoardSprintInfoDTO();
            sprintInfo.setSprintId(sprint.getSprintId());
            sprintInfo.setSprintName(sprint.getSprintName());
            Integer columnWidth = columnWidthMap.getOrDefault(sprint.getSprintId(), 1);
            sprintInfo.setColumnWidth(columnWidth);
            sprintInfos.add(sprintInfo);
        }
        Map<Long, IssueTypeDTO> issueTypeMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        for (BoardFeatureInfoDTO boardFeatureInfo : boardFeatureInfos) {
            boardFeatureInfo.setIssueTypeDTO(issueTypeMap.get(boardFeatureInfo.getIssueTypeId()));
        }
        Map<Long, Map<Long, List<BoardFeatureInfoDTO>>> teamFeatureInfoMap = boardFeatureInfos.stream().collect(Collectors.groupingBy(BoardFeatureInfoDTO::getTeamProjectId, Collectors.groupingBy(BoardFeatureInfoDTO::getSprintId)));
        List<ProgramBoardTeamInfoDTO> teamProjects = new ArrayList<>(projectRelationships.size());
        handleTeamData(programId, projectRelationships, teamFeatureInfoMap, teamProjects, sprints);
        Collections.sort(teamProjects, Comparator.comparing(ProgramBoardTeamInfoDTO::getRank).reversed());
        programBoardInfo.setSprints(sprintInfos);
        programBoardInfo.setTeamProjects(teamProjects);
        programBoardInfo.setBoardDepends(boardDependInfos);
        return programBoardInfo;
    }

    /**
     * 根据筛选器处理数据
     *
     * @param boardFilter
     * @param boardDependInfos
     * @param boardFeatureInfos
     * @param sprints
     * @param projectRelationships
     * @return
     */
    private Map<String, Object> handleBoardFilter(ProgramBoardFilterDTO boardFilter, List<BoardDependInfoDTO> boardDependInfos, List<BoardFeatureInfoDTO> boardFeatureInfos, List<SprintDO> sprints, List<ProjectRelationshipDTO> projectRelationships) {
        Map<String, Object> result = new HashMap<>(3);
        //只显示有依赖关系的公告板特性
        if (boardFilter.getOnlyDependFeature()) {
            Set<Long> boardFeatureIds = boardDependInfos.stream().map(BoardDependInfoDTO::getBoardFeatureId).collect(Collectors.toSet());
            boardFeatureIds.addAll(boardDependInfos.stream().map(BoardDependInfoDTO::getDependBoardFeatureId).collect(Collectors.toSet()));
            Map<Long, BoardFeatureInfoDTO> map = boardFeatureInfos.stream().collect(Collectors.toMap(BoardFeatureInfoDTO::getId, x -> x));
            boardFeatureInfos = new ArrayList<>(boardFeatureIds.size());
            for (Long boardFeatureId : boardFeatureIds) {
                boardFeatureInfos.add(map.get(boardFeatureId));
            }
        }
        result.put(BOARDFEATURES, boardFeatureInfos);
        //只筛选某些冲刺的公告板特性
        if (!CollectionUtils.isEmpty(boardFilter.getSprintIds())) {
            Map<Long, SprintDO> map = sprints.stream().collect(Collectors.toMap(SprintDO::getSprintId, x -> x));
            sprints = new ArrayList<>(boardFilter.getSprintIds().size());
            for (Long sprintId : boardFilter.getSprintIds()) {
                SprintDO sprintDO = map.get(sprintId);
                if (sprintDO != null) {
                    sprints.add(sprintDO);
                } else {
                    throw new CommonException(SPRINT_NOTFOUND_ERROR);
                }
            }
        }
        result.put(SPRINTS, sprints);
        //只筛选某些团队的公告板特性
        if (!boardFilter.getOnlyOtherTeamDependFeature() && !CollectionUtils.isEmpty(boardFilter.getTeamProjectIds())) {
            Map<Long, ProjectRelationshipDTO> map = projectRelationships.stream().collect(Collectors.toMap(ProjectRelationshipDTO::getProjectId, x -> x));
            projectRelationships = new ArrayList<>(boardFilter.getTeamProjectIds().size());
            for (Long teamProjectId : boardFilter.getTeamProjectIds()) {
                ProjectRelationshipDTO relationshipDTO = map.get(teamProjectId);
                if (relationshipDTO != null) {
                    projectRelationships.add(relationshipDTO);
                } else {
                    throw new CommonException(TEAM_NOTFOUND_ERROR);
                }
            }
        }
        //只显示其他团队与当前所选团队有依赖关系的公告板特性
        if (boardFilter.getOnlyOtherTeamDependFeature() && !CollectionUtils.isEmpty(boardFilter.getTeamProjectIds())) {
            List<BoardFeatureInfoDTO> newFeatures = new ArrayList<>(boardFeatureInfos.size());
            //当前团队feature
            List<BoardFeatureInfoDTO> myTeamInfos = boardFeatureInfos.stream().filter(info -> boardFilter.getTeamProjectIds().contains(info.getTeamProjectId())).collect(Collectors.toList());
            Map<Long, BoardFeatureInfoDTO> myTeamMap = myTeamInfos.stream().collect(Collectors.toMap(BoardFeatureInfoDTO::getId, x -> x));
            newFeatures.addAll(myTeamInfos);
            List<Long> boardFeatureIds = newFeatures.stream().map(BoardFeatureInfoDTO::getId).collect(Collectors.toList());
            //其他团队feature
            List<BoardFeatureInfoDTO> otherTeamInfos = boardFeatureInfos.stream().filter(info -> !boardFilter.getTeamProjectIds().contains(info.getTeamProjectId())).collect(Collectors.toList());
            Map<Long, BoardFeatureInfoDTO> otherTeamMap = otherTeamInfos.stream().collect(Collectors.toMap(BoardFeatureInfoDTO::getId, x -> x));
            for (BoardDependInfoDTO dependInfo : boardDependInfos) {
                Long boardFeatureId = dependInfo.getBoardFeatureId();
                Long dependBoardFeatureId = dependInfo.getDependBoardFeatureId();
                boolean dependOther = myTeamMap.get(boardFeatureId) != null && otherTeamMap.get(dependBoardFeatureId) != null;
                if (dependOther) {
                    if (!boardFeatureIds.contains(dependBoardFeatureId)) {
                        boardFeatureIds.add(dependBoardFeatureId);
                        newFeatures.add(otherTeamMap.get(dependBoardFeatureId));
                    }
                }
                boolean otherDepend = myTeamMap.get(dependBoardFeatureId) != null && otherTeamMap.get(boardFeatureId) != null;
                if (otherDepend) {
                    if (!boardFeatureIds.contains(boardFeatureId)) {
                        boardFeatureIds.add(boardFeatureId);
                        newFeatures.add(otherTeamMap.get(boardFeatureId));
                    }
                }
            }
            //计算出要展示的团队
            Map<Long, ProjectRelationshipDTO> teamMap = projectRelationships.stream().collect(Collectors.toMap(ProjectRelationshipDTO::getProjectId, x -> x));
            projectRelationships = new ArrayList<>();
            List<Long> sprintIds = sprints.stream().map(SprintDO::getSprintId).collect(Collectors.toList());
            List<Long> projectIds = newFeatures.stream().filter(x -> sprintIds.contains(x.getSprintId())).map(BoardFeatureInfoDTO::getTeamProjectId).distinct().collect(Collectors.toList());
            if (projectIds.isEmpty()) {
                for (Long teamProjectId : boardFilter.getTeamProjectIds()) {
                    projectRelationships.add(teamMap.get(teamProjectId));
                }
            }
            for (Long teamProjectId : projectIds) {
                ProjectRelationshipDTO relationshipDTO = teamMap.get(teamProjectId);
                if (relationshipDTO != null) {
                    projectRelationships.add(relationshipDTO);
                }
            }
            result.put(BOARDFEATURES, newFeatures);
        }
        result.put(PROJECTRELATIONSHIPS, projectRelationships);
        return result;
    }

    /**
     * 处理团队具体数据
     *
     * @param programId
     * @param projectRelationshipDTOs
     * @param teamFeatureInfoMap
     * @param teamProjects
     */
    private void handleTeamData(Long programId, List<ProjectRelationshipDTO> projectRelationshipDTOs, Map<Long, Map<Long, List<BoardFeatureInfoDTO>>> teamFeatureInfoMap, List<ProgramBoardTeamInfoDTO> teamProjects, List<SprintDO> sprints) {
        Map<Long, BoardTeamDO> teamMap = boardTeamRepository.queryByProgramId(programId).stream().collect(Collectors.toMap(BoardTeamDO::getTeamProjectId, x -> x));
        for (ProjectRelationshipDTO relationshipDTO : projectRelationshipDTOs) {
            //判断boardTeam数据是否存在
            BoardTeamDO team = teamMap.get(relationshipDTO.getProjectId());
            if (team == null) {
                team = boardTeamService.create(programId, relationshipDTO.getProjectId());
            }
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
            teamProject.setObjectVersionNumber(team.getObjectVersionNumber());
            teamProject.setRank(team.getRank());
            teamProject.setBoardTeamId(team.getId());
            teamProject.setTeamSprints(teamSprintInfos);
            teamProjects.add(teamProject);
        }
    }
}
