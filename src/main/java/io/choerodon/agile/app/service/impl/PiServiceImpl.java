package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.PiValidator;
import io.choerodon.agile.app.assembler.PiAssembler;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.infra.common.utils.*;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PiServiceImpl implements PiService {

    private static final String PI_TODO = "todo";
    private static final String PI_DOING = "doing";
    private static final String PI_DONE = "done";
    private static final String ADVANCED_SEARCH_ARGS = "advancedSearchArgs";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY = "yyyy";
    private static final String ART_DOING = "doing";
    private static final String ART_DONE = "done";
    private static final String STATUS_ID = "statusId";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.issueStateMachine.notFound";
    private static final String ERROR_ISSUE_STATUS_NOT_FOUND = "error.createIssue.issueStatusNotFound";
    private static final String SPRINT_COMPLETE_SETTING_BACKLOG = "backlog";
    private static final String SPRINT_COMPLETE_SETTING_NEXT_SPRINT = "next_sprint";

    @Autowired
    private PiRepository piRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private PiMapper piMapper;

    @Autowired
    private ArtMapper artMapper;

    @Autowired
    private WorkCalendarHolidayRefService workCalendarHolidayRefService;

    @Autowired
    private PiValidator piValidator;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private PiAssembler piAssembler;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private InstanceFeignClient instanceFeignClient;

    @Autowired
    private SendMsgUtil sendMsgUtil;

    /**
     * 获取当前年分
     *
     * @return
     */
    public Integer getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY);
        Date date = new Date();
        return Integer.parseInt(sdf.format(date));
    }

    private Date formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Date res = null;
        Date d = c.getTime();
        try {
            res = format.parse(format.format(d));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return res;
    }

    private Date getSpecifyTimeByOneTime(Date date, int amount) {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, amount);
        Date res = null;
        Date d = c.getTime();
        try {
            res = format.parse(format.format(d));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return res;
    }

    /**
     * 判断一个日期是星期几
     *
     * @param date
     * @return
     */
    private int getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    private Boolean judgeBeWork(Date date, List<WorkCalendarHolidayRefDTO> calendarDays) {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        String dateString = formatter.format(date);
        int week = getWeekOfDate(date);
        for (WorkCalendarHolidayRefDTO value : calendarDays) {
            if (dateString.equals(value.getHoliday()) && Objects.equals(value.getStatus(), 1)) {
                return true;
            } else if (dateString.equals(value.getHoliday()) && Objects.equals(value.getStatus(), 0)) {
                return false;
            }
        }
        if (week != 0 && week != 6) {
            return true;
        }
        return false;
    }

    private List<Date> getWorkDaysAfter(Date startDate, Long piWorkDays, Boolean overlap) {
        List<WorkCalendarHolidayRefDTO> calendarDays = workCalendarHolidayRefService.queryByYearIncludeLastAndNext(getCurrentYear());
        int ago = 0;
        int sumDays = overlap ? 0 : 1;
        List<Date> result = new ArrayList<>();
        while (true) {
            Date date = getSpecifyTimeByOneTime(startDate, ago);
            if (judgeBeWork(date, calendarDays)) {
                sumDays++;
                result.add(date);
            }
            if (sumDays == piWorkDays) {
                break;
            }
            ago++;
        }
        return result;
    }

    private void updateArtPiCodeNumber(Long programId, Long artId, Long piCodeNumber, Long objectVersionNumber) {
        ArtE artE = new ArtE();
        artE.setId(artId);
        artE.setProgramId(programId);
        artE.setPiCodeNumber(piCodeNumber);
        artE.setObjectVersionNumber(objectVersionNumber);
        artRepository.updateBySelective(artE);
    }

    private Long getPiWorkDays(ArtDO artDO) {
        Long ipWeeks = artDO.getIpWeeks();
        Long interationCount = artDO.getInterationCount();
        Long interationWeeks = artDO.getInterationWeeks();
        return interationCount * interationWeeks * 7 + ipWeeks * 7;
    }

    private void setPiStartAndEndDate(PiE piE, Long piWorkDays, Date startDate) {
        startDate = formatDate(startDate);
        Date endDate = getSpecifyTimeByOneTime(startDate, piWorkDays.intValue());
        piE.setStartDate(startDate);
        piE.setEndDate(endDate);
    }

    private void createSprintTemplate(Long programId, PiE piRes, ArtDO artDO, List<Long> sprintIds) {
        Date startDate = piRes.getStartDate();
        Long interationCount = artDO.getInterationCount();
        Long interationWeeks = artDO.getInterationWeeks();
        Date endDate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        for (int i = 0; i < interationCount; i++) {
            SprintE temp = sprintRepository.createSprint(new SprintE(programId, String.valueOf(System.currentTimeMillis()), startDate, endDate, "sprint_planning", piRes.getId()));
            sprintIds.add(temp.getSprintId());
            startDate = endDate;
            endDate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        }
    }

    @Override
    public void createPi(Long programId, ArtDO artDO, Date startDate) {
        Long piCodeNumber = artDO.getPiCodeNumber();
        Long piWorkDays = getPiWorkDays(artDO);
        List<Long> sprintIds = new ArrayList<>();
        for (int i = 0; i < artDO.getPiCount(); i++) {
            PiE piE = new PiE();
            piE.setCode(artDO.getPiCodePrefix());
            piE.setName(piCodeNumber.toString());
            piCodeNumber++;
            piE.setArtId(artDO.getId());
            piE.setStatusCode(PI_TODO);
            piE.setProgramId(programId);
            setPiStartAndEndDate(piE, piWorkDays, startDate);
            PiE piRes = piRepository.create(piE);
            // create sprint template
            createSprintTemplate(programId, piRes, artDO, sprintIds);
            startDate = piE.getEndDate();
        }
        sprintRepository.updateSprintNameByBatch(programId, sprintIds);
        updateArtPiCodeNumber(programId, artDO.getId(), piCodeNumber, artDO.getObjectVersionNumber());
    }

    @Override
    public PiDTO updatePi(Long programId, PiDTO piDTO) {
        return ConvertHelper.convert(piRepository.updateBySelective(ConvertHelper.convert(piDTO, PiE.class)), PiDTO.class);
    }

    private ArtDO getActiveArt(Long programId) {
        ArtDO artDO = artMapper.selectActiveArt(programId);
        if (artDO == null) {
            return null;
        } else {
            return artDO;
        }
    }

    private void setStatusIsCompleted(Long projectId, Map<Long, StatusMapDTO> statusMapDTOMap) {
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        issueStatusDO.setProjectId(projectId);
        Map<Long, Boolean> statusCompletedMap = issueStatusMapper.select(issueStatusDO).stream().collect(Collectors.toMap(IssueStatusDO::getStatusId, IssueStatusDO::getCompleted));
        statusMapDTOMap.entrySet().forEach(entry -> entry.getValue().setCompleted(statusCompletedMap.getOrDefault(entry.getKey(), false)));
    }

    @Override
    public JSONObject queryBacklogAll(Long programId, Long organizationId, Map<String, Object> searchParamMap) {
        // return result by JSONObject
        JSONObject result = new JSONObject();
        // get statusMap and issueTypeMap by organizationId
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        // set status completed
        setStatusIsCompleted(programId, statusMapDTOMap);
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        // query backlog with all feature
        List<SubFeatureDO> backlogFeatures = piMapper.selectBacklogNoPiList(programId, StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)));
        result.put("backlogAllFeatures", backlogFeatures != null && !backlogFeatures.isEmpty() ? piAssembler.subFeatureDOTODTO(backlogFeatures, statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
        // query active art with all pi
        ArtDO activeArt = getActiveArt(programId);
        if (activeArt == null) {
            return result;
        }
        List<PiWithFeatureDO> piWithFeatureDOList = piMapper.selectBacklogPiList(programId, activeArt.getId(), StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)));
        result.put("allPiList", piWithFeatureDOList != null && !piWithFeatureDOList.isEmpty() ? piAssembler.piWithFeatureDOTODTO(piWithFeatureDOList, statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
        return result;
    }

    @Override
    public Page<PiDTO> queryArtAll(Long programId, Long artId, PageRequest pageRequest) {
        Page<PiDO> piDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                piMapper.selectPiListInArt(programId, artId));
        Page<PiDTO> dtoPage = new Page<>();
        dtoPage.setNumber(piDOPage.getNumber());
        dtoPage.setSize(piDOPage.getSize());
        dtoPage.setTotalElements(piDOPage.getTotalElements());
        dtoPage.setTotalPages(piDOPage.getTotalPages());
        dtoPage.setNumberOfElements(piDOPage.getNumberOfElements());
        if (piDOPage.getContent() != null && !piDOPage.getContent().isEmpty()) {
            dtoPage.setContent(ConvertHelper.convertList(piDOPage.getContent(), PiDTO.class));
        }
        return dtoPage;
    }

    private void createSprintWhenStartPi(Long programId, Long piId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(programId);
        sprintDO.setPiId(piId);
        List<SprintDO> sprintDOList = sprintMapper.select(sprintDO);
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        for (ProjectRelationshipDTO projectRelationshipDTO : projectRelationshipDTOList) {
            Long projectId = projectRelationshipDTO.getProjectId();
            for (SprintDO sprint : sprintDOList) {
                sprintRepository.createSprint(new SprintE(projectId, sprint.getSprintName(), sprint.getStartDate(), sprint.getEndDate(), sprint.getStatusCode(), sprint.getPiId()));
            }
        }
    }

    @Override
    public PiDTO startPi(Long programId, PiDTO piDTO) {
        piValidator.checkPiStart(piDTO);
        // create sprint in each project
        createSprintWhenStartPi(programId, piDTO.getId());
        // update pi status: doing
        PiE piE = new PiE(programId, piDTO.getId(), PI_DOING, piDTO.getObjectVersionNumber());
        piE.setActualStartDate(new Date());
        PiE result = piRepository.updateBySelective(piE);
        // update issue status
        List<IssueDO> issueDOList = issueMapper.selectStatusChangeIssueByPiId(programId, piDTO.getId());
        Long updateStatusId = piDTO.getUpdateStatusId();
        if (updateStatusId != null) {
            if (issueDOList != null && !issueDOList.isEmpty()) {
                CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
                issueRepository.updateStatusIdBatch(programId, updateStatusId, issueDOList, customUserDetails.getUserId(), new Date());
            }
        }
        return ConvertHelper.convert(result, PiDTO.class);
    }

    @Override
    public PiCompleteCountDTO beforeClosePi(Long programId, Long piId, Long artId) {
        PiCompleteCountDTO result = new PiCompleteCountDTO();
        result.setCompletedCount(piMapper.selectFeatureCount(programId, piId, true));
        result.setUnCompletedCount(piMapper.selectFeatureCount(programId, piId, false));
        result.setPiTodoDTOList(ConvertHelper.convertList(piMapper.selectTodoPi(programId, artId), PiTodoDTO.class));
        return result;
    }

    private void beforeRankInProgram(Long programId, Long targetSprintId, List<MoveIssueDO> moveIssueDOS, List<Long> moveIssueIds) {
        if (moveIssueIds.isEmpty()) {
            return;
        }
        String minRank = piMapper.queryPiMinRank(programId, targetSprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueIds) {
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueIds) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
            }
        }
    }

    @Override
    public void dealUnCompleteFeature(Long programId, Long piId, Long targetPiId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        List<Long> moveFeatureRankIds = piMapper.queryFeatureIdOrderByRankDesc(programId, piId);
        beforeRankInProgram(programId, targetPiId, moveIssueDOS, moveFeatureRankIds);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        List<Long> moveFeatureIds = piMapper.queryFeatureIds(programId, piId);
        // batch update status
        Long organizationId = ConvertUtil.getOrganizationId(programId);
        //获取状态机id
        Long issueTypeId = null;
        List<IssueTypeDTO> issueTypeDTOList = issueFeignClient.queryIssueTypesByProjectId(programId, "program").getBody();
        for (IssueTypeDTO issueTypeDTO : issueTypeDTOList) {
            if ("feature".equals(issueTypeDTO.getTypeCode())) {
                issueTypeId = issueTypeDTO.getId();
                break;
            }
        }
        Long stateMachineId = issueFeignClient.queryStateMachineId(programId, "program", issueTypeId).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        batchUpdateStatus(programId, targetPiId, moveFeatureIds, initStatusId, "prepare", customUserDetails.getUserId());
        if (targetPiId != null && !Objects.equals(targetPiId, 0L)) {
            issueRepository.featureToDestinationByIdsClosePi(programId, targetPiId, moveFeatureIds, new Date(), customUserDetails.getUserId());
        }
        issueRepository.batchUpdateFeatureRank(programId, moveIssueDOS);
    }

    @Override
    public void completeProjectsSprints(Long programId, Long piId) {
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        if (projectRelationshipDTOList == null || projectRelationshipDTOList.isEmpty()) {
            return;
        }
        for (ProjectRelationshipDTO projectRelationshipDTO : projectRelationshipDTOList) {
            Long projectId = projectRelationshipDTO.getProjectId();
            List<Long> sprintIds = sprintMapper.selectNotDoneByPiId(projectId, piId);
            for (Long sprintId : sprintIds) {
                SprintCompleteDTO sprintCompleteDTO = new SprintCompleteDTO();
                sprintCompleteDTO.setProjectId(projectId);
                sprintCompleteDTO.setSprintId(sprintId);
                sprintCompleteDTO.setIncompleteIssuesDestination(0L);
                sprintService.completeSprint(projectId, sprintCompleteDTO);
            }
        }
    }

    @Override
    public void completeSprintsWithSelect(Long programId, Long piId, Long nextPiId, Long artId) {
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        if (projectRelationshipDTOList == null || projectRelationshipDTOList.isEmpty()) {
            return;
        }
        ArtDO artDO = artMapper.selectByPrimaryKey(artId);
        for (ProjectRelationshipDTO projectRelationshipDTO : projectRelationshipDTOList) {
            Long projectId = projectRelationshipDTO.getProjectId();
            List<Long> sprintIds = sprintMapper.selectNotDoneByPiId(projectId, piId);
            SprintDO newSprint = sprintMapper.selectFirstSprintByPiId(projectId, nextPiId);
            for (Long sprintId : sprintIds) {
                SprintCompleteDTO sprintCompleteDTO = new SprintCompleteDTO();
                sprintCompleteDTO.setProjectId(projectId);
                sprintCompleteDTO.setSprintId(sprintId);
                sprintCompleteDTO.setIncompleteIssuesDestination(SPRINT_COMPLETE_SETTING_NEXT_SPRINT.equals(artDO.getSprintCompleteSetting()) ? newSprint.getSprintId() : 0L);
                sprintService.completeSprint(projectId, sprintCompleteDTO);
            }
        }
    }

    private void autoCreatePi(Long programId, Long artId) {
        List<PiDO> restPi = piMapper.selectUnDonePiDOList(programId, artId);
        ArtDO artDO = artMapper.selectByPrimaryKey(artId);
        int restPiCount = restPi.size();
        int artPiCount = artDO.getPiCount().intValue();
        PiDO lastPi = piMapper.selectLastPi(programId, artId);
        Date startDate = lastPi.getEndDate();
        if (restPiCount < artPiCount) {
            Long piCodeNumber = artDO.getPiCodeNumber();
            Long piWorkDays = getPiWorkDays(artDO);
            List<Long> sprintIds = new ArrayList<>();
            for (int i = 0; i < artPiCount - restPiCount; i++) {
                PiE piE = new PiE();
                piE.setCode(artDO.getPiCodePrefix());
                piE.setName(piCodeNumber.toString());
                piCodeNumber++;
                piE.setStatusCode(PI_TODO);
                piE.setArtId(artDO.getId());
                piE.setProgramId(programId);
                setPiStartAndEndDate(piE, piWorkDays, startDate);
                PiE piRes = piRepository.create(piE);
                // create sprint template
                createSprintTemplate(programId, piRes, artDO, sprintIds);
                startDate = piE.getEndDate();
            }
            sprintRepository.updateSprintNameByBatch(programId, sprintIds);
            updateArtPiCodeNumber(programId, artDO.getId(), piCodeNumber, artDO.getObjectVersionNumber());
        }
    }

    @Override
    public PiDTO closePi(Long programId, PiDTO piDTO) {
        piValidator.checkPiClose(piDTO);
        // deal uncomplete feature to target pi
        dealUnCompleteFeature(piDTO.getProgramId(), piDTO.getId(), piDTO.getTargetPiId());
        // update pi status: done
        PiE update = new PiE(programId, piDTO.getId(), PI_DONE, piDTO.getObjectVersionNumber());
        update.setActualEndDate(new Date());
        PiE piE = piRepository.updateBySelective(update);
        // auto start next PI
        PiDO nextPi = piMapper.selectNextPi(programId, piDTO.getArtId(), piDTO.getId());
        if (nextPi != null) {
            PiDTO nextStartPi = new PiDTO();
            nextStartPi.setProgramId(programId);
            nextStartPi.setArtId(piDTO.getArtId());
            nextStartPi.setId(nextPi.getId());
            nextStartPi.setObjectVersionNumber(nextPi.getObjectVersionNumber());
            nextStartPi.setUpdateStatusId(piDTO.getUpdateStatusId());
            startPi(programId, nextStartPi);
        }
        // deal projects' sprints complete
        completeSprintsWithSelect(programId, piDTO.getId(), nextPi.getId(), piDTO.getArtId());
        autoCreatePi(programId, piDTO.getArtId());
        sendMsgUtil.sendPmAndEmailAfterPiComplete(programId, piE);
        return ConvertHelper.convert(piE, PiDTO.class);
    }

    private void noOutsetBeforeRank(Long programId, Long piId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        String minRank = piMapper.queryPiMinRank(programId, piId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
            }
        }
    }

    private void outsetBeforeRank(Long programId, Long piId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        String rightRank = issueMapper.queryRankByProgram(programId, moveIssueDTO.getOutsetIssueId());
        String leftRank = issueMapper.queryLeftRankByProgram(programId, piId, rightRank);
        if (leftRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                rightRank = RankUtil.genPre(rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                rightRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, rightRank));
            }
        }
    }

    private void beforeRank(Long programId, Long piId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueDTO.setIssueIds(issueMapper.queryFeatureIdOrderByRankDesc(programId, moveIssueDTO.getIssueIds()));
        if (moveIssueDTO.getOutsetIssueId() == null || Objects.equals(moveIssueDTO.getOutsetIssueId(), 0L)) {
            noOutsetBeforeRank(programId, piId, moveIssueDTO, moveIssueDOS);
        } else {
            outsetBeforeRank(programId, piId, moveIssueDTO, moveIssueDOS);
        }
    }

    private void afterRank(Long programId, Long piId, MoveIssueDTO moveIssueDTO, List<MoveIssueDO> moveIssueDOS) {
        moveIssueDTO.setIssueIds(issueMapper.queryFeatureIdOrderByRankAsc(programId, moveIssueDTO.getIssueIds()));
        String leftRank = issueMapper.queryRankByProgram(programId, moveIssueDTO.getOutsetIssueId());
        String rightRank = issueMapper.queryRightRankByProgram(programId, piId, leftRank);
        if (rightRank == null) {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                leftRank = RankUtil.genNext(leftRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        } else {
            for (Long issueId : moveIssueDTO.getIssueIds()) {
                leftRank = RankUtil.between(leftRank, rightRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, leftRank));
            }
        }
    }

    private void batchUpdateStatus(Long programId, Long piId, List<Long> moveIssueIdsFilter, Long updateStatusId, String categoryCode, Long userId) {
        List<IssueDO> moveIssues = issueMapper.selectFeatureByMoveIssueIds(programId, moveIssueIdsFilter, categoryCode, piId);
        if (moveIssues == null || moveIssues.isEmpty()) {
            return;
        }
        issueRepository.updateStatusIdBatch(programId, updateStatusId, moveIssues, userId, new Date());
    }

    @Override
    public List<SubFeatureDO> batchFeatureToPi(Long programId, Long piId, MoveIssueDTO moveIssueDTO) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        if (moveIssueDTO.getBefore()) {
            beforeRank(programId, piId, moveIssueDTO, moveIssueDOS);
        } else {
            afterRank(programId, piId, moveIssueDTO, moveIssueDOS);
        }
        issueRepository.batchUpdateFeatureRank(programId, moveIssueDOS);
        List<Long> moveIssueIds = moveIssueDTO.getIssueIds();
        List<SubFeatureDO> featureDOList = piMapper.selectFeatureIdByFeatureIds(programId, moveIssueIds).stream().filter(subFeatureDO -> subFeatureDO.getPiId() == null ? piId != 0 : !subFeatureDO.getPiId().equals(piId)).collect(Collectors.toList());
        if (featureDOList != null && !featureDOList.isEmpty()) {
            List<Long> moveIssueIdsFilter = featureDOList.stream().map(SubFeatureDO::getIssueId).collect(Collectors.toList());
            // batch update status
            if (moveIssueDTO.getUpdateStatusId() != null) {
                batchUpdateStatus(programId, piId, moveIssueIdsFilter, moveIssueDTO.getUpdateStatusId(), moveIssueDTO.getStatusCategoryCode(), customUserDetails.getUserId());
            }
            BatchRemovePiE batchRemovePiE = new BatchRemovePiE(programId, piId, moveIssueIdsFilter);
            issueRepository.removeFeatureFromPiByIssueIds(batchRemovePiE);
            if (piId != null && !Objects.equals(piId, 0L)) {
                issueRepository.batchFeatureToPi(programId, piId, moveIssueIdsFilter, new Date(), customUserDetails.getUserId());
            }
            return featureDOList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SubFeatureDO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds) {
        issueRepository.batchFeatureToEpic(programId, epicId, featureIds);
        return piMapper.selectFeatureIdByFeatureIds(programId, featureIds);
    }

    @Override
    public List<PiNameDTO> queryAllOfProgram(Long programId) {
        List<PiNameDO> piNameDOList = piMapper.selectAllOfProgram(programId);
        if (piNameDOList != null && !piNameDOList.isEmpty()) {
            return ConvertHelper.convertList(piNameDOList, PiNameDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<PiNameDTO> queryUnfinishedOfProgram(Long programId) {
        ArtDO activeArt = artMapper.selectActiveArt(programId);
        if (activeArt == null) {
            return new ArrayList<>();
        }
        List<PiDO> piDOList = piMapper.selectUnDonePiDOList(programId, activeArt.getId());
        if (piDOList != null && !piDOList.isEmpty())  {
            return ConvertHelper.convertList(piDOList, PiNameDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<PiWithFeatureDTO> queryRoadMapOfProgram(Long programId, Long organizationId) {
        ArtDO activeArt = getActiveArt(programId);
        if (activeArt == null) {
            return new ArrayList<>();
        }
        List<PiWithFeatureDO> piWithFeatureDOList = piMapper.selectRoadMapPiList(programId, activeArt.getId());
        if (piWithFeatureDOList != null && !piWithFeatureDOList.isEmpty()) {
            Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            setStatusIsCompleted(programId, statusMapDTOMap);
            Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            return piAssembler.piWithFeatureDOTODTO(piWithFeatureDOList, statusMapDTOMap, issueTypeDTOMap);
        } else {
            return new ArrayList<>();
        }
    }
}
