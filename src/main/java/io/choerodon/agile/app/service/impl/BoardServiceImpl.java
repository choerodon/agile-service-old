package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.BoardValidator;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.common.utils.SendMsgUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.dto.InputDTO;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

    private static final String CONTRAINT_NONE = "constraint_none";
    private static final String STORY_POINTS = "story_point";
    private static final String PARENT_CHILD = "parent_child";
    private static final String BOARD = "board";
    private static final String URL_TEMPLATE1 = "#/agile/issue?type=project&id=";
    private static final String URL_TEMPLATE2 = "&name=";
    private static final String URL_TEMPLATE3 = "&paramName=";
    private static final String URL_TEMPLATE4 = "&paramIssueId=";
    private static final String URL_TEMPLATE5 = "&paramOpenIssueId=";
    private static final String URL_TEMPLATE6 = "&organizationId=";
    private static final String PROJECT_ID = "projectId";
    private static final String RANK = "rank";
    private static final String UPDATE_STATUS_MOVE = "updateStatusMove";

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardColumnService boardColumnService;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private ColumnStatusRelRepository columnStatusRelRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private WorkCalendarRefMapper workCalendarRefMapper;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private PiMapper piMapper;

    @Autowired
    private PiFeatureRepository piFeatureRepository;

    @Autowired
    private PiFeatureMapper piFeatureMapper;

    @Autowired
    private ArtMapper artMapper;

    @Autowired
    private SendMsgUtil sendMsgUtil;

    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;

    @Autowired
    private RedisUtil redisUtil;


    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void create(Long projectId, String boardName) {
        if (checkName(projectId, boardName)) {
            throw new CommonException("error.boardName.exist");
        }
        BoardDTO boardResult = createBoard(projectId, boardName);
        boardColumnService.createColumnWithRelateStatus(boardResult);
    }

    private Boolean checkNameUpdate(Long projectId, Long boardId, String boardName) {
        BoardDTO boardDTO = boardMapper.selectByPrimaryKey(boardId);
        if (boardName.equals(boardDTO.getName())) {
            return false;
        }
        BoardDTO check = new BoardDTO();
        check.setProjectId(projectId);
        check.setName(boardName);
        List<BoardDTO> boardDTOList = boardMapper.select(check);
        return boardDTOList != null && !boardDTOList.isEmpty();
    }

    @Override
    public BoardVO update(Long projectId, Long boardId, BoardVO boardVO) {
        if (boardVO.getName() != null && checkNameUpdate(projectId, boardId, boardVO.getName())) {
            throw new CommonException("error.boardName.exist");
        }
        BoardValidator.checkUpdateBoard(projectId, boardVO);
        boardVO.setBoardId(boardId);
//        BoardE boardE = ConvertHelper.convert(boardVO, BoardE.class);
        if (boardMapper.updateByPrimaryKeySelective(modelMapper.map(boardVO, BoardDTO.class)) != 1) {
            throw new CommonException("error.board.update");
        }
        return modelMapper.map(boardMapper.selectByPrimaryKey(boardVO.getBoardId()), BoardVO.class);
    }

    @Override
    public void delete(Long projectId, Long boardId) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.setBoardId(boardId);
        List<BoardColumnDTO> boardColumnDTOList = boardColumnMapper.select(boardColumnDTO);
        for (BoardColumnDTO column : boardColumnDTOList) {
            ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
            columnStatusRelDTO.setColumnId(column.getColumnId());
            columnStatusRelDTO.setProjectId(projectId);
//            columnStatusRelRepository.delete(columnStatusRelE);
            if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
                return;
            }
            if (columnStatusRelMapper.delete(columnStatusRelDTO) == 0) {
                throw new CommonException("error.ColumnStatus.delete");
            }
            redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelDTO.getProjectId() + ':' + "*"});
//            boardColumnRepository.delete(column.getColumnId());
            if (boardColumnMapper.deleteByPrimaryKey(column.getColumnId()) != 1) {
                throw new CommonException("error.BoardColumn.delete");
            }
        }
//        boardRepository.delete(boardId);
        if (boardMapper.deleteByPrimaryKey(boardId) != 1) {
            throw new CommonException("error.board.delete");
        }
        //删除默认看板UserSetting
        UserSettingDTO userSettingDTO = new UserSettingDTO();
        userSettingDTO.setProjectId(projectId);
        userSettingDTO.setTypeCode(BOARD);
        userSettingDTO.setBoardId(boardId);
        userSettingDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
        userSettingMapper.delete(userSettingDTO);
        //更新第一个为默认
        List<BoardVO> boardVOS = queryByProjectId(projectId);
        if (!boardVOS.isEmpty()) {
            Long defaultBoardId = boardVOS.get(0).getBoardId();
            handleUserSetting(defaultBoardId, projectId);
        }
    }

    @Override
    public BoardVO queryScrumBoardById(Long projectId, Long boardId) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setProjectId(projectId);
        boardDTO.setBoardId(boardId);
        return modelMapper.map(boardMapper.selectOne(boardDTO), BoardVO.class);
    }

    public JSONObject putColumnData(List<ColumnAndIssueDTO> columns) {
        JSONObject columnsData = new JSONObject();
        columnsData.put("columns", columns);
        return columnsData;
    }

    private void addIssueInfos(IssueForBoardDO issue, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Map<Long, PriorityVO> priorityMap, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, List<Long>> parentWithSubs) {
        if (issue.getParentIssueId() != null && issue.getParentIssueId() != 0 && !parentIds.contains(issue.getParentIssueId())) {
            parentIds.add(issue.getParentIssueId());
        } else if (issue.getRelateIssueId() != null && issue.getRelateIssueId() != 0 && !parentIds.contains(issue.getRelateIssueId())) {
            parentIds.add(issue.getRelateIssueId());
        } else {
            ids.add(issue.getIssueId());
        }
        if (issue.getAssigneeId() != null && !assigneeIds.contains(issue.getAssigneeId())) {
            assigneeIds.add(issue.getAssigneeId());
        }
        if (issue.getEpicId() != null && !epicIds.contains(issue.getEpicId())) {
            epicIds.add(issue.getEpicId());
        }
        if ("sub_task".equals(issue.getTypeCode()) && issue.getParentIssueId() != null) {
            List<Long> subtaskIds = null;
            subtaskIds = parentWithSubs.get(issue.getParentIssueId());
            if (subtaskIds == null) {
                subtaskIds = new ArrayList<>();
            }
            subtaskIds.add(issue.getIssueId());
            parentWithSubs.put(issue.getParentIssueId(), subtaskIds);
        }
        if ("bug".equals(issue.getTypeCode()) && issue.getRelateIssueId() != null) {
            List<Long> subBugIds = parentWithSubs.get(issue.getRelateIssueId());
            if (subBugIds == null) {
                subBugIds = new ArrayList<>();
            }
            subBugIds.add(issue.getIssueId());
            parentWithSubs.put(issue.getRelateIssueId(), subBugIds);
        }
        issue.setPriorityVO(priorityMap.get(issue.getPriorityId()));
        issue.setIssueTypeVO(issueTypeDTOMap.get(issue.getIssueTypeId()));
        if (issue.getStayDate() != null) {
            issue.setStayDay(DateUtil.differentDaysByMillisecond(issue.getStayDate(), new Date()));
        } else {
            issue.setStayDay(0);
        }
    }

    private void getDatas(List<SubStatusDTO> subStatusDTOS, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Long organizationId, Map<Long, List<Long>> parentWithSubs, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        subStatusDTOS.forEach(subStatusDTO -> subStatusDTO.getIssues().forEach(issueForBoardDO -> addIssueInfos(issueForBoardDO, parentIds, assigneeIds, ids, epicIds, priorityMap, issueTypeDTOMap, parentWithSubs)));
    }

    public void putDatasAndSort(List<ColumnAndIssueDTO> columns, List<Long> parentIds, List<Long> assigneeIds, Long boardId, List<Long> epicIds, Boolean condition, Long organizationId, Map<Long, List<Long>> parentWithSubss, Map<Long, StatusMapVO> statusMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<Long> issueIds = new ArrayList<>();
        for (ColumnAndIssueDTO column : columns) {
            List<SubStatusDTO> subStatusDTOS = column.getSubStatusDTOS();
            fillStatusData(subStatusDTOS, statusMap);
            getDatas(subStatusDTOS, parentIds, assigneeIds, issueIds, epicIds, organizationId, parentWithSubss, issueTypeDTOMap);
            Collections.sort(subStatusDTOS, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
        //选择故事泳道选择仅我的任务后，子任务经办人为自己，父任务经办人不为自己的情况
        if (condition) {
            handleParentIdsWithSubIssues(parentIds, issueIds, columns, boardId);
        }
        Collections.sort(parentIds);
        Collections.sort(assigneeIds);
    }

    private void fillStatusData(List<SubStatusDTO> subStatusDTOS, Map<Long, StatusMapVO> statusMap) {
        for (SubStatusDTO subStatusDTO : subStatusDTOS) {
            StatusMapVO status = statusMap.get(subStatusDTO.getStatusId());
            subStatusDTO.setCategoryCode(status.getType());
            subStatusDTO.setName(status.getName());
            Collections.sort(subStatusDTO.getIssues(), Comparator.comparing(IssueForBoardDO::getIssueId));
        }
    }

    private void handleParentIdsWithSubIssues(List<Long> parentIds, List<Long> issueIds, List<ColumnAndIssueDTO> columns, Long boardId) {
        if (parentIds != null && !parentIds.isEmpty()) {
            List<Long> subNoParentIds = new ArrayList<>();
            parentIds.forEach(id -> {
                if (!issueIds.contains(id)) {
                    subNoParentIds.add(id);
                }
            });
            if (!subNoParentIds.isEmpty()) {
                List<ColumnAndIssueDTO> subNoParentColumns = boardColumnMapper.queryColumnsByIssueIds(subNoParentIds, boardId);
                subNoParentColumns.forEach(columnAndIssueDTO -> handleSameColumn(columns, columnAndIssueDTO));
            }
        }
    }

    private void handleSameColumn(List<ColumnAndIssueDTO> columns, ColumnAndIssueDTO columnAndIssueDTO) {
        Optional<ColumnAndIssueDTO> sameColumn = columns.stream().filter(columnAndIssue -> columnAndIssue.getColumnId().equals(columnAndIssueDTO.getColumnId()))
                .findFirst();
        if (sameColumn.isPresent()) {
            sameColumn.get().getSubStatusDTOS().forEach(subStatusDTO -> columnAndIssueDTO.getSubStatusDTOS().forEach(s -> {
                if (subStatusDTO.getId().equals(s.getId())) {
                    subStatusDTO.getIssues().addAll(s.getIssues());
                }
            }));
        } else {
            columns.add(columnAndIssueDTO);
        }
    }


    private SprintDTO getActiveSprint(Long projectId) {
        return sprintService.getActiveSprint(projectId);
    }

    private BoardSprintVO putCurrentSprint(SprintDTO activeSprint, Long organizationId) {
        if (activeSprint != null) {
            BoardSprintVO boardSprintVO = new BoardSprintVO();
            boardSprintVO.setSprintId(activeSprint.getSprintId());
            boardSprintVO.setSprintName(activeSprint.getSprintName());
            if (activeSprint.getEndDate() != null) {
                Date startDate = new Date();
                if (activeSprint.getStartDate().after(startDate)) {
                    startDate = activeSprint.getStartDate();
                }
                boardSprintVO.setDayRemain(dateUtil.getDaysBetweenDifferentDate(startDate, activeSprint.getEndDate(),
                        workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()),
                        workCalendarRefMapper.queryWorkBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()), organizationId));
            }
            return boardSprintVO;
        }
        return null;
    }

    private String getQuickFilter(List<Long> quickFilterIds) {
        List<String> sqlQuerys = quickFilterMapper.selectSqlQueryByIds(quickFilterIds);
        if (sqlQuerys.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder("select issue_id from agile_issue where ");
        int idx = 0;
        for (String filter : sqlQuerys) {
            if (idx == 0) {
                sql.append(" ( " + filter + " ) ");
                idx += 1;
            } else {
                sql.append(" and " + " ( " + filter + " ) ");
            }
        }
        return sql.toString();
    }

    private List<Long> sortAndJudgeCompleted(Long projectId, List<Long> parentIds) {
        if (parentIds != null && !parentIds.isEmpty()) {
            return boardColumnMapper.sortAndJudgeCompleted(projectId, parentIds);
        } else {
            return new ArrayList<>();
        }
    }

    private List<ParentIssueDTO> getParentIssues(Long projectId, List<Long> parentIds, Map<Long, StatusMapVO> statusMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        if (parentIds == null || parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<ParentIssueDTO> parentIssueDTOList = boardColumnMapper.queryParentIssuesByIds(projectId, parentIds);
        for (ParentIssueDTO parentIssueDTO : parentIssueDTOList) {
            parentIssueDTO.setStatusMapVO(statusMap.get(parentIssueDTO.getStatusId()));
            parentIssueDTO.setIssueTypeVO(issueTypeDTOMap.get(parentIssueDTO.getIssueTypeId()));
        }
        return parentIssueDTOList;
    }

    private List<ColumnIssueNumDTO> getAllColumnNum(Long projectId, Long boardId, Long activeSprintId) {
        BoardDTO boardDTO = boardMapper.selectByPrimaryKey(boardId);
        if (!CONTRAINT_NONE.equals(boardDTO.getColumnConstraint())) {
            return boardColumnMapper.getAllColumnNum(projectId, boardId, activeSprintId, boardDTO.getColumnConstraint());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds) {
        JSONObject jsonObject = new JSONObject(true);
        SprintDTO activeSprint = getActiveSprint(projectId);
        Long activeSprintId = null;
        if (activeSprint != null) {
            activeSprintId = activeSprint.getSprintId();
        }
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<Long> assigneeIds = new ArrayList<>();
        List<Long> parentIds = new ArrayList<>();
        List<Long> epicIds = new ArrayList<>();
        List<ColumnAndIssueDTO> columns = boardColumnMapper.selectColumnsByBoardId(projectId, boardId, activeSprintId, assigneeId, onlyStory, filterSql, assigneeFilterIds);
        Boolean condition = assigneeId != null && onlyStory;
        Map<Long, List<Long>> parentWithSubs = new HashMap<>();
        Map<Long, StatusMapVO> statusMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        putDatasAndSort(columns, parentIds, assigneeIds, boardId, epicIds, condition, organizationId, parentWithSubs, statusMap, issueTypeDTOMap);
        jsonObject.put("parentIds", parentIds);
        jsonObject.put("parentIssues", getParentIssues(projectId, parentIds, statusMap, issueTypeDTOMap));
        jsonObject.put("assigneeIds", assigneeIds);
        jsonObject.put("parentWithSubs", parentWithSubs);
        jsonObject.put("parentCompleted", sortAndJudgeCompleted(projectId, parentIds));
        jsonObject.put("epicInfo", !epicIds.isEmpty() ? boardColumnMapper.selectEpicBatchByIds(epicIds) : null);
        jsonObject.put("allColumnNum", getAllColumnNum(projectId, boardId, activeSprintId));
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(assigneeIds, true);
        Comparator<IssueForBoardDO> comparator = Comparator.comparing(IssueForBoardDO::getRank, nullsFirst(naturalOrder()));
        columns.forEach(columnAndIssueDTO -> columnAndIssueDTO.getSubStatusDTOS().forEach(subStatusDTO -> {
                    subStatusDTO.getIssues().forEach(issueForBoardDO -> {
                        UserMessageDTO userMessageDTO = usersMap.get(issueForBoardDO.getAssigneeId());
                        String assigneeName = userMessageDTO != null ? userMessageDTO.getName() : null;
                        String assigneeLoginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
                        String assigneeRealName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
                        String imageUrl = userMessageDTO != null ? userMessageDTO.getImageUrl() : null;
                        issueForBoardDO.setAssigneeName(assigneeName);
                        issueForBoardDO.setAssigneeLoginName(assigneeLoginName);
                        issueForBoardDO.setAssigneeRealName(assigneeRealName);
                        issueForBoardDO.setImageUrl(imageUrl);
                    });
                    subStatusDTO.getIssues().sort(comparator);
                }
        ));
        jsonObject.put("columnsData", putColumnData(columns));
        jsonObject.put("currentSprint", putCurrentSprint(activeSprint, organizationId));
        //处理用户默认看板设置，保存最近一次的浏览
        handleUserSetting(boardId, projectId);
        return jsonObject;
    }

    private void handleUserSetting(Long boardId, Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserSettingDTO userSettingDTO = new UserSettingDTO();
        userSettingDTO.setProjectId(projectId);
        userSettingDTO.setTypeCode(BOARD);
        userSettingDTO.setBoardId(boardId);
        userSettingDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
        UserSettingDTO query = userSettingMapper.selectOne(userSettingDTO);
        if (query == null) {
            userSettingDTO.setDefaultBoard(true);
            userSettingDTO.setSwimlaneBasedCode("swimlane_none");
//            userSettingRepository.create(ConvertHelper.convert(userSettingDTO, UserSettingE.class));
            int insert = userSettingMapper.insert(userSettingDTO);
            if (insert != 1) {
                throw new CommonException("error.userSetting.create");
            }
//            userSettingRepository.updateOtherBoardNoDefault(boardId, projectId, userId);
            userSettingMapper.updateOtherBoardNoDefault(boardId,projectId,userId);
        } else if (!query.getDefaultBoard()) {
            query.setDefaultBoard(true);
//            userSettingRepository.update(ConvertHelper.convert(query, UserSettingE.class));
            if (userSettingMapper.selectByPrimaryKey(query) == null) {
                throw new CommonException("error.userSetting.notFound");
            }
            int update = userSettingMapper.updateByPrimaryKey(query);
            if (update != 1) {
                throw new CommonException("error.userSetting.update");
            }
//            userSettingRepository.updateOtherBoardNoDefault(boardId, projectId, userId);
            userSettingMapper.updateOtherBoardNoDefault(boardId,projectId,userId);
        }
    }

    private BoardDTO createBoard(Long projectId, String boardName) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setProjectId(projectId);
        boardDTO.setColumnConstraint(CONTRAINT_NONE);
        boardDTO.setDayInColumn(false);
        boardDTO.setEstimationStatistic(STORY_POINTS);
        boardDTO.setName(boardName);
        boardDTO.setSwimlaneBasedCode(PARENT_CHILD);
//        return boardRepository.create(boardE);
        if (boardMapper.insert(boardDTO) != 1) {
            throw new CommonException("error.board.insert");
        }
        return boardMapper.selectByPrimaryKey(boardDTO.getBoardId());
    }

    @Override
    public void initBoard(Long projectId, String boardName, List<StatusPayload> statusPayloads) {
        BoardDTO boardResult = createBoard(projectId, boardName);
        boardColumnService.initBoardColumns(projectId, boardResult.getBoardId(), statusPayloads);
    }

    @Override
    public IssueMoveVO move(Long projectId, Long issueId, Long transformId, IssueMoveVO issueMoveVO, Boolean isDemo) {
        //执行状态机转换
        if (isDemo) {
            stateMachineService.executeTransformForDemo(projectId, issueId, transformId, issueMoveVO.getObjectVersionNumber(),
                    SchemeApplyType.AGILE, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleIssueMoveRank(projectId, issueMoveVO))));
        } else {
            stateMachineService.executeTransform(projectId, issueId, transformId, issueMoveVO.getObjectVersionNumber(),
                    SchemeApplyType.AGILE, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleIssueMoveRank(projectId, issueMoveVO))));
        }
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(issueId);
        IssueMoveVO result = modelMapper.map(issueDTO, IssueMoveVO.class);
        sendMsgUtil.sendMsgByIssueMoveComplete(projectId, issueMoveVO, issueDTO);
        return result;
    }

    @Override
    public FeatureMoveVO moveByProgram(Long projectId, Long issueId, Long transformId, FeatureMoveVO featureMoveVO) {
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(issueId);
        stateMachineService.executeTransform(projectId, issueId, transformId, featureMoveVO.getObjectVersionNumber(),
                SchemeApplyType.PROGRAM, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleFeatureMoveRank(projectId, issueDTO))));
        issueDTO = issueMapper.selectByPrimaryKey(issueId);
        // deal pi of feature
        if (featureMoveVO.getPiChange() != null && featureMoveVO.getPiChange()) {
            Long piId = featureMoveVO.getPiId();
            if (piId != null && piId != 0) {
                if (piFeatureMapper.selectExistByOptions(projectId, issueId)) {
                    piFeatureMapper.deletePfRelationByOptions(projectId, issueId);
                }
                if (!piFeatureMapper.selectGivenExistByOptions(projectId, issueId, piId)) {
                   insertPiFeature(new PiFeatureDTO(issueId, piId, projectId));
                }
            }
        }
        return ConvertHelper.convert(issueDTO, FeatureMoveVO.class);
    }

    @DataLog(type = "pi")
    public PiFeatureDTO insertPiFeature(PiFeatureDTO piFeatureDTO) {
        if (piFeatureMapper.insert(piFeatureDTO) != 1) {
            throw new CommonException("error.piFeatureDTO.insert");
        }
        return piFeatureDTO;
    }

    private JSONObject handleFeatureMoveRank(Long projectId, IssueDTO issueDTO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RANK, issueDTO.getRank());
        jsonObject.put(PROJECT_ID, projectId);
        return jsonObject;
    }

    private JSONObject handleIssueMoveRank(Long projectId, IssueMoveVO issueMoveVO) {
        JSONObject jsonObject = new JSONObject();
        if (issueMoveVO.getRank()) {
            String rank;
            if (issueMoveVO.getBefore()) {
                if (issueMoveVO.getOutsetIssueId() == null || Objects.equals(issueMoveVO.getOutsetIssueId(), 0L)) {
                    String minRank = sprintMapper.queryMinRank(projectId, issueMoveVO.getSprintId());
                    if (minRank == null) {
                        rank = RankUtil.mid();
                    } else {
                        rank = RankUtil.genPre(minRank);
                    }
                } else {
                    String rightRank = issueMapper.queryRank(projectId, issueMoveVO.getOutsetIssueId());
                    if (rightRank == null) {
                        //处理子任务没有rank的旧数据
                        rightRank = handleSubIssueNotRank(projectId, issueMoveVO.getOutsetIssueId(), issueMoveVO.getSprintId());
                    }
                    String leftRank = issueMapper.queryLeftRank(projectId, issueMoveVO.getSprintId(), rightRank);
                    if (leftRank == null) {
                        rank = RankUtil.genPre(rightRank);
                    } else {
                        rank = RankUtil.between(leftRank, rightRank);
                    }
                }
            } else {
                String leftRank = issueMapper.queryRank(projectId, issueMoveVO.getOutsetIssueId());
                if (leftRank == null) {
                    leftRank = handleSubIssueNotRank(projectId, issueMoveVO.getOutsetIssueId(), issueMoveVO.getSprintId());
                }
                String rightRank = issueMapper.queryRightRank(projectId, issueMoveVO.getSprintId(), leftRank);
                if (rightRank == null) {
                    rank = RankUtil.genNext(leftRank);
                } else {
                    rank = RankUtil.between(leftRank, rightRank);
                }
            }
            jsonObject.put(RANK, rank);
            jsonObject.put(PROJECT_ID, projectId);
            return jsonObject;
        } else {
            return null;
        }
    }

    private String handleSubIssueNotRank(Long projectId, Long outsetIssueId, Long sprintId) {
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(outsetIssueId);
        issueDTO.setRank(sprintMapper.queryMaxRank(projectId, sprintId));
        issueMapper.updateByPrimaryKeySelective(issueDTO);
        return issueDTO.getRank();
    }

    @Override
    public List<BoardVO> queryByProjectId(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return modelMapper.map(boardMapper.queryByProjectIdWithUser(userId, projectId), new TypeToken<List<BoardVO>>(){}.getType());
    }

    @Override
    public UserSettingVO queryUserSettingBoard(Long projectId, Long boardId) {
        UserSettingDTO userSettingDTO = queryUserSettingBoardByBoardId(projectId, boardId, DetailsHelper.getUserDetails().getUserId());
        if (userSettingDTO == null) {
            UserSettingDTO userSetting = new UserSettingDTO();
            userSetting.setProjectId(projectId);
            userSetting.setBoardId(boardId);
            userSetting.setTypeCode(BOARD);
            userSetting.setUserId(DetailsHelper.getUserDetails().getUserId());
            userSetting.setSwimlaneBasedCode("swimlane_none");
            userSetting.setDefaultBoard(false);
//            return ConvertHelper.convert(userSettingRepository.create(userSettingE), UserSettingVO.class);
            int insert = userSettingMapper.insert(userSettingDTO);
            if (insert != 1) {
                throw new CommonException("error.userSetting.create");
            }
            return modelMapper.map(userSettingMapper.selectByPrimaryKey(userSettingDTO.getSettingId()), UserSettingVO.class);
        } else {
            return modelMapper.map(userSettingDTO, UserSettingVO.class);
        }
    }

    @Override
    public UserSettingVO updateUserSettingBoard(Long projectId, Long boardId, String swimlaneBasedCode) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Long userId = customUserDetails.getUserId();
        UserSettingE userSettingE = ConvertHelper.convert(queryUserSettingBoardByBoardId(projectId, boardId, userId), UserSettingE.class);
        if (userSettingE == null) {
            userSettingE = new UserSettingE();
            userSettingE.setDefaultBoard(false);
            userSettingE.setTypeCode(BOARD);
            userSettingE.setProjectId(projectId);
            userSettingE.setBoardId(boardId);
            userSettingE.setUserId(userId);
            userSettingE.setSwimlaneBasedCode(swimlaneBasedCode);
            userSettingE = userSettingRepository.create(userSettingE);
        } else {
            userSettingE.setSwimlaneBasedCode(swimlaneBasedCode);
            userSettingE = userSettingRepository.update(userSettingE);
        }
        return ConvertHelper.convert(userSettingE, UserSettingVO.class);
    }

    private UserSettingDTO queryUserSettingBoardByBoardId(Long projectId, Long boardId, Long userId) {
        UserSettingDTO userSettingDTO = new UserSettingDTO();
        userSettingDTO.setProjectId(projectId);
        userSettingDTO.setBoardId(boardId);
        userSettingDTO.setTypeCode(BOARD);
        userSettingDTO.setUserId(userId);
        return userSettingMapper.selectOne(userSettingDTO);
    }

    @Override
    public Boolean checkName(Long projectId, String boardName) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setProjectId(projectId);
        boardDTO.setName(boardName);
        List<BoardDTO> boardDTOList = boardMapper.select(boardDTO);
        return boardDTOList != null && !boardDTOList.isEmpty();
    }

    @Override
    public void initBoardByProgram(Long projectId, String boardName, List<StatusPayload> statusPayloads) {
        BoardDTO boardResult = createBoard(projectId, boardName);
        boardColumnService.initBoardColumnsByProgram(projectId, boardResult.getBoardId(), statusPayloads);
    }

    private void setColumnDeatil(List<ColumnAndIssueDTO> columns, Map<Long, StatusMapVO> statusMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        for (ColumnAndIssueDTO column : columns) {
            List<SubStatusDTO> subStatusDTOS = column.getSubStatusDTOS();
            fillStatusData(subStatusDTOS, statusMap);
            for (SubStatusDTO subStatusDTO : subStatusDTOS) {
                List<IssueForBoardDO> issueForBoardDOS = subStatusDTO.getIssues();
                for (IssueForBoardDO issueForBoardDO : issueForBoardDOS) {
                    issueForBoardDO.setIssueTypeVO(issueTypeDTOMap.get(issueForBoardDO.getIssueTypeId()));
                }
            }
            Collections.sort(subStatusDTOS, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
    }

    @Override
    public JSONObject queryByOptionsInProgram(Long projectId, Long boardId, Long organizationId, SearchVO searchVO) {
        JSONObject result = new JSONObject(true);
        ArtDTO activeArtDTO = artMapper.selectActiveArt(projectId);
        PiDTO piDTO = null;
        if (activeArtDTO != null) {
            piDTO = piMapper.selectActivePi(projectId, activeArtDTO.getId());
        }
        Long activePiId = null;
        if (piDTO != null) {
            activePiId = piDTO.getId();
        }
        List<Long> epicIds = new ArrayList<>();
        List<ColumnAndIssueDTO> columns = boardColumnMapper.selectBoardByProgram(projectId, boardId, activePiId, searchVO);
        columns.forEach(columnAndIssueDTO -> {
            columnAndIssueDTO.getSubStatusDTOS().forEach(subStatusDTO -> {
                subStatusDTO.getIssues().forEach(issueForBoardDO -> {
                    if (issueForBoardDO.getEpicId() != null && !epicIds.contains(issueForBoardDO.getEpicId())) {
                        epicIds.add(issueForBoardDO.getEpicId());
                    }
                });
            });
        });
        // get status map from organization
        Map<Long, StatusMapVO> statusMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        // reset status info
        setColumnDeatil(columns, statusMap, issueTypeDTOMap);
        columns.sort(Comparator.comparing(ColumnAndIssueDTO::getSequence));
        result.put("columnsData", columns);
        result.put("activePi", piDTO);
        result.put("epicInfo", !epicIds.isEmpty() ? boardColumnMapper.selectEpicBatchByIds(epicIds) : null);
        handleUserSetting(boardId, projectId);
        return result;
    }
}
