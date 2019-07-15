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
//    @Autowired
//    private BoardFeatureRepository boardFeatureRepository;
    @Autowired
    private ArtMapper artMapper;
    @Autowired
    private PiMapper piMapper;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private BoardSprintAttrMapper boardSprintAttrMapper;
//    @Autowired
//    private BoardTeamRepository boardTeamRepository;
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
    private static final String ERROR_BOARDFEATURE_ILLEGAL = "error.boardFeature.illegal";
    private static final String ERROR_BOARDFEATURE_CREATE = "error.boardFeature.create";
    private static final String ERROR_BOARDFEATURE_DELETE = "error.boardFeature.delete";
    private static final String ERROR_BOARDFEATURE_NOTFOUND = "error.boardFeature.notFound";
    private static final String ERROR_BOARDFEATURE_UPDATE = "error.boardFeature.update";

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public BoardFeatureInfoVO create(Long projectId, BoardFeatureCreateVO createVO) {
        BoardFeatureDTO boardFeatureDTO = modelMapper.map(createVO, BoardFeatureDTO.class);
        boardFeatureDTO.setProgramId(projectId);
        checkExist(boardFeatureDTO);
        handleRank(projectId, boardFeatureDTO, createVO.getBefore(), createVO.getOutsetId());
//        boardFeatureRepository.create(boardFeatureDTO);
        if (boardFeatureMapper.insert(boardFeatureDTO) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_CREATE);
        }
        return queryInfoById(projectId, boardFeatureDTO.getId());
    }

    @Override
    public BoardFeatureInfoVO update(Long projectId, Long boardFeatureId, BoardFeatureUpdateVO updateVO) {
        Long objectVersionNumber = updateVO.getObjectVersionNumber();
        BoardFeatureDTO boardFeatureDTO = queryByIdAndCheck(projectId, boardFeatureId);
        BoardFeatureDTO update = modelMapper.map(updateVO, BoardFeatureDTO.class);
        update.setFeatureId(boardFeatureDTO.getFeatureId());
        update.setProgramId(projectId);
        update.setObjectVersionNumber(null);
        BoardFeatureDTO select = boardFeatureMapper.selectOne(update);
        if (select != null && !select.getId().equals(boardFeatureId)) {
            throw new CommonException(EXIST_ERROR);
        }
        update.setId(boardFeatureId);
        update.setObjectVersionNumber(objectVersionNumber);
        handleRank(projectId, update, updateVO.getBefore(), updateVO.getOutsetId());
//        boardFeatureRepository.update(update);
        if (boardFeatureMapper.updateByPrimaryKeySelective(update) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_UPDATE);
        }
        return queryInfoById(projectId, boardFeatureId);
    }

    /**
     * 判断是否已经存在
     *
     * @param boardFeatureDTO
     */
    private void checkExist(BoardFeatureDTO boardFeatureDTO) {
        if (!boardFeatureMapper.select(boardFeatureDTO).isEmpty()) {
            throw new CommonException(EXIST_ERROR);
        }
    }

    /**
     * 处理rank值
     *
     * @param projectId
     * @param boardFeatureDTO
     * @param before
     * @param outSetId
     */
    private void handleRank(Long projectId, BoardFeatureDTO boardFeatureDTO, Boolean before, Long outSetId) {
        if (outSetId.equals(0L)) {
            String rank = RankUtil.mid();
            boardFeatureDTO.setRank(rank);
        } else if (before) {
            String outSetRank = queryById(projectId, outSetId).getRank();
            boardFeatureDTO.setRank(RankUtil.genNext(outSetRank));
        } else {
            String outSetRank = queryById(projectId, outSetId).getRank();
            String rightRank = boardFeatureMapper.queryRightRank(boardFeatureDTO, outSetRank);
            if (rightRank == null) {
                boardFeatureDTO.setRank(RankUtil.genPre(outSetRank));
            } else {
                boardFeatureDTO.setRank(RankUtil.between(outSetRank, rightRank));
            }
        }
    }

    @Override
    public BoardFeatureVO queryById(Long projectId, Long boardFeatureId) {
        return modelMapper.map(queryByIdAndCheck(projectId, boardFeatureId), BoardFeatureVO.class);
    }

    @Override
    public BoardFeatureInfoVO queryInfoById(Long projectId, Long boardFeatureId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        BoardFeatureInfoVO info = queryInfoByIdAndCheck(projectId, boardFeatureId);
        Map<Long, IssueTypeVO> issueTypeMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        info.setIssueTypeVO(issueTypeMap.get(info.getIssueTypeId()));
        return info;
    }

    @Override
    public void deleteById(Long projectId, Long boardFeatureId) {
//        boardFeatureRepository.checkId(projectId, boardFeatureId);
        checkId(projectId, boardFeatureId);
//        boardFeatureRepository.delete(boardFeatureId);
        if (boardFeatureMapper.deleteByPrimaryKey(boardFeatureId) != 1) {
            throw new CommonException(ERROR_BOARDFEATURE_DELETE);
        }
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
    public ProgramBoardInfoVO queryBoardInfo(Long programId, ProgramBoardFilterVO boardFilter) {
        ProgramBoardInfoVO programBoardInfo = new ProgramBoardInfoVO();
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
        Map<Long, Integer> columnWidthMap = boardSprintAttrMapper.queryByProgramId(programId).stream().collect(Collectors.toMap(BoardSprintAttrDTO::getSprintId, BoardSprintAttrDTO::getColumnWidth));
        List<SprintDTO> sprints = sprintMapper.selectListByPiId(programId, piId);
        programBoardInfo.setFilterSprintList(modelMapper.map(sprints, new TypeToken<List<SprintVO>>() {
        }.getType()));
        //获取团队信息
        List<ProjectRelationshipVO> projectRelationships = userFeignClient.getProjUnderGroup(organizationId, programId, true).getBody();
        List<TeamProjectVO> filterTeamList = new ArrayList<>(projectRelationships.size());
        for (ProjectRelationshipVO rel : projectRelationships) {
            TeamProjectVO teamProjectVO = new TeamProjectVO();
            teamProjectVO.setTeamProjectId(rel.getProjectId());
            teamProjectVO.setName(rel.getProjName());
            filterTeamList.add(teamProjectVO);
        }
        programBoardInfo.setFilterTeamList(filterTeamList);
        //获取依赖关系，只获取有效团队的依赖关系
        List<BoardDependInfoVO> boardDependInfos = boardDependMapper.queryInfoByPiId(programId, piId, projectRelationships.stream().map(ProjectRelationshipVO::getProjectId).collect(Collectors.toList()));
        //获取公告板特性信息
        List<BoardFeatureInfoVO> boardFeatureInfos = boardFeatureMapper.queryInfoByPiId(programId, piId).stream().filter(x -> x.getIssueTypeId() != null).collect(Collectors.toList());

        Map<String, Object> result = handleBoardFilter(boardFilter, boardDependInfos, boardFeatureInfos, sprints, projectRelationships);
        boardFeatureInfos = (List<BoardFeatureInfoVO>) result.get(BOARDFEATURES);
        sprints = (List<SprintDTO>) result.get(SPRINTS);
        projectRelationships = (List<ProjectRelationshipVO>) result.get(PROJECTRELATIONSHIPS);

        List<ProgramBoardSprintInfoVO> sprintInfos = new ArrayList<>(sprints.size());
        for (SprintDTO sprint : sprints) {
            ProgramBoardSprintInfoVO sprintInfo = new ProgramBoardSprintInfoVO();
            sprintInfo.setSprintId(sprint.getSprintId());
            sprintInfo.setSprintName(sprint.getSprintName());
            Integer columnWidth = columnWidthMap.getOrDefault(sprint.getSprintId(), 1);
            sprintInfo.setColumnWidth(columnWidth);
            sprintInfos.add(sprintInfo);
        }
        Map<Long, IssueTypeVO> issueTypeMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        for (BoardFeatureInfoVO boardFeatureInfo : boardFeatureInfos) {
            boardFeatureInfo.setIssueTypeVO(issueTypeMap.get(boardFeatureInfo.getIssueTypeId()));
        }
        Map<Long, Map<Long, List<BoardFeatureInfoVO>>> teamFeatureInfoMap = boardFeatureInfos.stream().collect(Collectors.groupingBy(BoardFeatureInfoVO::getTeamProjectId, Collectors.groupingBy(BoardFeatureInfoVO::getSprintId)));
        List<ProgramBoardTeamInfoVO> teamProjects = new ArrayList<>(projectRelationships.size());
        handleTeamData(programId, projectRelationships, teamFeatureInfoMap, teamProjects, sprints);
        Collections.sort(teamProjects, Comparator.comparing(ProgramBoardTeamInfoVO::getRank).reversed());
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
    private Map<String, Object> handleBoardFilter(ProgramBoardFilterVO boardFilter, List<BoardDependInfoVO> boardDependInfos, List<BoardFeatureInfoVO> boardFeatureInfos, List<SprintDTO> sprints, List<ProjectRelationshipVO> projectRelationships) {
        Map<String, Object> result = new HashMap<>(3);
        //只显示有依赖关系的公告板特性
        if (boardFilter.getOnlyDependFeature()) {
            Set<Long> boardFeatureIds = boardDependInfos.stream().map(BoardDependInfoVO::getBoardFeatureId).collect(Collectors.toSet());
            boardFeatureIds.addAll(boardDependInfos.stream().map(BoardDependInfoVO::getDependBoardFeatureId).collect(Collectors.toSet()));
            Map<Long, BoardFeatureInfoVO> map = boardFeatureInfos.stream().collect(Collectors.toMap(BoardFeatureInfoVO::getId, x -> x));
            boardFeatureInfos = new ArrayList<>(boardFeatureIds.size());
            for (Long boardFeatureId : boardFeatureIds) {
                boardFeatureInfos.add(map.get(boardFeatureId));
            }
        }
        result.put(BOARDFEATURES, boardFeatureInfos);
        //只筛选某些冲刺的公告板特性
        if (!CollectionUtils.isEmpty(boardFilter.getSprintIds())) {
            Map<Long, SprintDTO> map = sprints.stream().collect(Collectors.toMap(SprintDTO::getSprintId, x -> x));
            sprints = new ArrayList<>(boardFilter.getSprintIds().size());
            for (Long sprintId : boardFilter.getSprintIds()) {
                SprintDTO sprintDTO = map.get(sprintId);
                if (sprintDTO != null) {
                    sprints.add(sprintDTO);
                } else {
                    throw new CommonException(SPRINT_NOTFOUND_ERROR);
                }
            }
        }
        result.put(SPRINTS, sprints);
        //只筛选某些团队的公告板特性
        if (!boardFilter.getOnlyOtherTeamDependFeature() && !CollectionUtils.isEmpty(boardFilter.getTeamProjectIds())) {
            Map<Long, ProjectRelationshipVO> map = projectRelationships.stream().collect(Collectors.toMap(ProjectRelationshipVO::getProjectId, x -> x));
            projectRelationships = new ArrayList<>(boardFilter.getTeamProjectIds().size());
            for (Long teamProjectId : boardFilter.getTeamProjectIds()) {
                ProjectRelationshipVO relationshipDTO = map.get(teamProjectId);
                if (relationshipDTO != null) {
                    projectRelationships.add(relationshipDTO);
                } else {
                    throw new CommonException(TEAM_NOTFOUND_ERROR);
                }
            }
        }
        //只显示其他团队与当前所选团队有依赖关系的公告板特性
        if (boardFilter.getOnlyOtherTeamDependFeature() && !CollectionUtils.isEmpty(boardFilter.getTeamProjectIds())) {
            List<BoardFeatureInfoVO> newFeatures = new ArrayList<>(boardFeatureInfos.size());
            //当前团队feature
            List<BoardFeatureInfoVO> myTeamInfos = boardFeatureInfos.stream().filter(info -> boardFilter.getTeamProjectIds().contains(info.getTeamProjectId())).collect(Collectors.toList());
            Map<Long, BoardFeatureInfoVO> myTeamMap = myTeamInfos.stream().collect(Collectors.toMap(BoardFeatureInfoVO::getId, x -> x));
            newFeatures.addAll(myTeamInfos);
            List<Long> boardFeatureIds = newFeatures.stream().map(BoardFeatureInfoVO::getId).collect(Collectors.toList());
            //其他团队feature
            List<BoardFeatureInfoVO> otherTeamInfos = boardFeatureInfos.stream().filter(info -> !boardFilter.getTeamProjectIds().contains(info.getTeamProjectId())).collect(Collectors.toList());
            Map<Long, BoardFeatureInfoVO> otherTeamMap = otherTeamInfos.stream().collect(Collectors.toMap(BoardFeatureInfoVO::getId, x -> x));
            for (BoardDependInfoVO dependInfo : boardDependInfos) {
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
            Map<Long, ProjectRelationshipVO> teamMap = projectRelationships.stream().collect(Collectors.toMap(ProjectRelationshipVO::getProjectId, x -> x));
            projectRelationships = new ArrayList<>();
            List<Long> sprintIds = sprints.stream().map(SprintDTO::getSprintId).collect(Collectors.toList());
            List<Long> projectIds = newFeatures.stream().filter(x -> sprintIds.contains(x.getSprintId())).map(BoardFeatureInfoVO::getTeamProjectId).distinct().collect(Collectors.toList());
            if (projectIds.isEmpty()) {
                for (Long teamProjectId : boardFilter.getTeamProjectIds()) {
                    projectRelationships.add(teamMap.get(teamProjectId));
                }
            }
            for (Long teamProjectId : projectIds) {
                ProjectRelationshipVO relationshipDTO = teamMap.get(teamProjectId);
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
     * @param projectRelationshipVOS
     * @param teamFeatureInfoMap
     * @param teamProjects
     */
    private void handleTeamData(Long programId, List<ProjectRelationshipVO> projectRelationshipVOS, Map<Long, Map<Long, List<BoardFeatureInfoVO>>> teamFeatureInfoMap, List<ProgramBoardTeamInfoVO> teamProjects, List<SprintDTO> sprints) {
        Map<Long, BoardTeamDTO> teamMap = boardTeamService.queryByProgramId(programId).stream().collect(Collectors.toMap(BoardTeamDTO::getTeamProjectId, x -> x));
        for (ProjectRelationshipVO relationshipDTO : projectRelationshipVOS) {
            //判断boardTeam数据是否存在
            BoardTeamDTO team = teamMap.get(relationshipDTO.getProjectId());
            if (team == null) {
                team = boardTeamService.create(programId, relationshipDTO.getProjectId());
            }
            //获取每个团队每个冲刺的公告板特性信息
            Map<Long, List<BoardFeatureInfoVO>> sprintFeatureInfoMap = teamFeatureInfoMap.get(relationshipDTO.getProjectId());
            List<ProgramBoardTeamSprintInfoVO> teamSprintInfos = new ArrayList<>(sprints.size());
            if (sprintFeatureInfoMap != null) {
                for (SprintDTO sprint : sprints) {
                    List<BoardFeatureInfoVO> boardFeatures = sprintFeatureInfoMap.getOrDefault(sprint.getSprintId(), new ArrayList<>());
                    ProgramBoardTeamSprintInfoVO teamSprintInfo = new ProgramBoardTeamSprintInfoVO();
                    teamSprintInfo.setSprintId(sprint.getSprintId());
                    teamSprintInfo.setBoardFeatures(boardFeatures);
                    teamSprintInfos.add(teamSprintInfo);
                }
            } else {
                for (SprintDTO sprint : sprints) {
                    ProgramBoardTeamSprintInfoVO teamSprintInfo = new ProgramBoardTeamSprintInfoVO();
                    teamSprintInfo.setSprintId(sprint.getSprintId());
                    teamSprintInfo.setBoardFeatures(new ArrayList<>());
                    teamSprintInfos.add(teamSprintInfo);
                }
            }
            ProgramBoardTeamInfoVO teamProject = new ProgramBoardTeamInfoVO();
            teamProject.setProjectId(relationshipDTO.getProjectId());
            teamProject.setProjectName(relationshipDTO.getProjName());
            teamProject.setObjectVersionNumber(team.getObjectVersionNumber());
            teamProject.setRank(team.getRank());
            teamProject.setBoardTeamId(team.getId());
            teamProject.setTeamSprints(teamSprintInfos);
            teamProjects.add(teamProject);
        }
    }

    public BoardFeatureDTO queryByIdAndCheck(Long projectId, Long boardFeatureId) {
        BoardFeatureDTO boardFeature = boardFeatureMapper.selectByPrimaryKey(boardFeatureId);
        if (boardFeature == null) {
            throw new CommonException(ERROR_BOARDFEATURE_NOTFOUND);
        }
        if (!boardFeature.getProgramId().equals(projectId)) {
            throw new CommonException(ERROR_BOARDFEATURE_ILLEGAL);
        }
        return boardFeature;
    }

    public BoardFeatureInfoVO queryInfoByIdAndCheck(Long projectId, Long boardFeatureId) {
        BoardFeatureInfoVO boardFeature = boardFeatureMapper.queryInfoById(projectId, boardFeatureId);
        if (boardFeature == null) {
            throw new CommonException(ERROR_BOARDFEATURE_NOTFOUND);
        }
        if (!boardFeature.getProgramId().equals(projectId)) {
            throw new CommonException(ERROR_BOARDFEATURE_ILLEGAL);
        }
        return boardFeature;
    }

    @Override
    public void checkId(Long projectId, Long boardFeatureId) {
        queryByIdAndCheck(projectId, boardFeatureId);
    }
}
