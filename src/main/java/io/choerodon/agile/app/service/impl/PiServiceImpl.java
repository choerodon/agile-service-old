package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.PiValidator;
import io.choerodon.agile.app.assembler.PiAssembler;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
import io.choerodon.agile.domain.agile.entity.BatchRemovePiE;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.dataobject.SprintConvertDTO;
import io.choerodon.agile.infra.common.utils.*;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.dataobject.SubFeatureDTO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.ArtRepository;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.infra.repository.PiRepository;
import io.choerodon.agile.infra.repository.SprintRepository;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;
import io.choerodon.statemachine.feign.InstanceFeignClient;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

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

    private Boolean judgeBeWork(Date date, List<WorkCalendarHolidayRefVO> calendarDays) {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        String dateString = formatter.format(date);
        int week = getWeekOfDate(date);
        for (WorkCalendarHolidayRefVO value : calendarDays) {
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
        List<WorkCalendarHolidayRefVO> calendarDays = workCalendarHolidayRefService.queryByYearIncludeLastAndNext(getCurrentYear());
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
        ArtDTO artDTO = new ArtDTO();
        artDTO.setId(artId);
        artDTO.setProgramId(programId);
        artDTO.setPiCodeNumber(piCodeNumber);
        artDTO.setObjectVersionNumber(objectVersionNumber);
//        artRepository.updateBySelective(artE);
        if (artMapper.updateByPrimaryKeySelective(artDTO) != 1) {
            throw new CommonException("error.art.update");
        }
    }

    private Long getPiWorkDays(ArtDTO artDTO) {
        Long ipWeeks = artDTO.getIpWeeks();
        Long interationCount = artDTO.getInterationCount();
        Long interationWeeks = artDTO.getInterationWeeks();
        return interationCount * interationWeeks * 7 + ipWeeks * 7;
    }

    private void setPiStartAndEndDate(PiE piE, Long piWorkDays, Date startDate) {
        startDate = formatDate(startDate);
        Date endDate = getSpecifyTimeByOneTime(startDate, piWorkDays.intValue());
        piE.setStartDate(startDate);
        piE.setEndDate(endDate);
    }

    private void createSprintTemplate(Long programId, PiE piRes, ArtDTO artDTO, List<Long> sprintIds) {
        Date startDate = piRes.getStartDate();
        Long interationCount = artDTO.getInterationCount();
        Long interationWeeks = artDTO.getInterationWeeks();
        Date endDate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        for (int i = 0; i < interationCount; i++) {
            SprintConvertDTO temp = sprintService.create(new SprintConvertDTO(programId, String.valueOf(System.currentTimeMillis()), startDate, endDate, "sprint_planning", piRes.getId()));
            sprintIds.add(temp.getSprintId());
            startDate = endDate;
            endDate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        }
    }

    @Override
    public void createPi(Long programId, ArtDTO artDTO, Date startDate) {
        Long piCodeNumber = artDTO.getPiCodeNumber();
        Long piWorkDays = getPiWorkDays(artDTO);
        List<Long> sprintIds = new ArrayList<>();
        for (int i = 0; i < artDTO.getPiCount(); i++) {
            PiE piE = new PiE();
            piE.setCode(artDTO.getPiCodePrefix());
            piE.setName(piCodeNumber.toString());
            piCodeNumber++;
            piE.setArtId(artDTO.getId());
            piE.setStatusCode(PI_TODO);
            piE.setProgramId(programId);
            setPiStartAndEndDate(piE, piWorkDays, startDate);
            PiE piRes = piRepository.create(piE);
            // create sprint template
            createSprintTemplate(programId, piRes, artDTO, sprintIds);
            startDate = piE.getEndDate();
        }
        sprintService.updateSprintNameByBatch(programId, sprintIds);
        updateArtPiCodeNumber(programId, artDTO.getId(), piCodeNumber, artDTO.getObjectVersionNumber());
    }

    @Override
    public PiDTO updatePi(Long programId, PiDTO piDTO) {
        if (piMapper.updateByPrimaryKeySelective(piDTO) != 1) {
            throw new CommonException("error.pi.update");
        }
        return piMapper.selectByPrimaryKey(piDTO.getId());
    }

    private ArtDTO getActiveArt(Long programId) {
        ArtDTO artDTO = artMapper.selectActiveArt(programId);
        if (artDTO == null) {
            return null;
        } else {
            return artDTO;
        }
    }

    private void setStatusIsCompleted(Long projectId, Map<Long, StatusMapVO> statusMapDTOMap) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        Map<Long, Boolean> statusCompletedMap = issueStatusMapper.select(issueStatusDTO).stream().collect(Collectors.toMap(IssueStatusDTO::getStatusId, IssueStatusDTO::getCompleted));
        statusMapDTOMap.entrySet().forEach(entry -> entry.getValue().setCompleted(statusCompletedMap.getOrDefault(entry.getKey(), false)));
    }

    @Override
    public JSONObject queryBacklogAll(Long programId, Long organizationId, Map<String, Object> searchParamMap) {
        // return result by JSONObject
        JSONObject result = new JSONObject();
        // get statusMap and issueTypeMap by organizationId
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        // set status completed
        setStatusIsCompleted(programId, statusMapDTOMap);
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        // query backlog with all feature
        List<SubFeatureDTO> backlogFeatures = piMapper.selectBacklogNoPiList(programId, StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)));
        result.put("backlogAllFeatures", backlogFeatures != null && !backlogFeatures.isEmpty() ? piAssembler.subFeatureDOTODTO(backlogFeatures, statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
        // query active art with all pi
        ArtDTO activeArt = getActiveArt(programId);
        if (activeArt == null) {
            return result;
        }
        List<PiWithFeatureDTO> piWithFeatureDTOList = piMapper.selectBacklogPiList(programId, activeArt.getId(), StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)));
        result.put("allPiList", piWithFeatureDTOList != null && !piWithFeatureDTOList.isEmpty() ? piAssembler.piWithFeatureDOTODTO(piWithFeatureDTOList, statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
        return result;
    }

    @Override
    public PageInfo<PiDTO> queryArtAll(Long programId, Long artId, PageRequest pageRequest) {
        PageInfo<PiDTO> piDTOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> piMapper.selectPiListInArt(programId, artId));
        if (piDTOPage.getList() != null && !piDTOPage.getList().isEmpty()) {
            return piDTOPage;
        } else {
            return new PageInfo<>(new ArrayList<>());
        }
    }

    private void createSprintWhenStartPi(Long programId, Long piId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(programId);
        sprintDTO.setPiId(piId);
        List<SprintDTO> sprintDTOList = sprintMapper.select(sprintDTO);
        List<ProjectRelationshipVO> projectRelationshipVOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        for (ProjectRelationshipVO projectRelationshipVO : projectRelationshipVOList) {
            Long projectId = projectRelationshipVO.getProjectId();
            for (SprintDTO sprint : sprintDTOList) {
                sprintService.create(new SprintConvertDTO(projectId, sprint.getSprintName(), sprint.getStartDate(), sprint.getEndDate(), sprint.getStatusCode(), sprint.getPiId()));
            }
        }
    }

    @Override
    public PiDTO startPi(Long programId, PiVO piVO) {
        piValidator.checkPiStart(piVO);
        // create sprint in each project
        createSprintWhenStartPi(programId, piVO.getId());
        // update pi status: doing
        if (piMapper.updateByPrimaryKeySelective(new PiDTO(programId, piVO.getId(), PI_DOING, new Date(), piVO.getObjectVersionNumber())) != 1) {
            throw new CommonException("error.pi.update");
        }
        // update issue status
        List<IssueDTO> issueDTOList = issueMapper.selectStatusChangeIssueByPiId(programId, piVO.getId());
        Long updateStatusId = piVO.getUpdateStatusId();
        if (updateStatusId != null) {
            if (issueDTOList != null && !issueDTOList.isEmpty()) {
                CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
                issueRepository.updateStatusIdBatch(programId, updateStatusId, issueDTOList, customUserDetails.getUserId(), new Date());
            }
        }
        return piMapper.selectByPrimaryKey(piVO.getId());
    }

    @Override
    public PiCompleteCountVO beforeClosePi(Long programId, Long piId, Long artId) {
        PiCompleteCountVO result = new PiCompleteCountVO();
        result.setCompletedCount(piMapper.selectFeatureCount(programId, piId, true));
        result.setUnCompletedCount(piMapper.selectFeatureCount(programId, piId, false));
        result.setPiTodoDTOList(piMapper.selectTodoPi(programId, artId));
        return result;
    }

    private void beforeRankInProgram(Long programId, Long targetSprintId, List<MoveIssueDTO> moveIssueDTOS, List<Long> moveIssueIds) {
        if (moveIssueIds.isEmpty()) {
            return;
        }
        String minRank = piMapper.queryPiMinRank(programId, targetSprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueIds) {
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueIds) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
            }
        }
    }

    @Override
    public void dealUnCompleteFeature(Long programId, Long piId, Long targetPiId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDTO> moveIssueDTOS = new ArrayList<>();
        List<Long> moveFeatureRankIds = piMapper.queryFeatureIdOrderByRankDesc(programId, piId);
        beforeRankInProgram(programId, targetPiId, moveIssueDTOS, moveFeatureRankIds);
        if (moveIssueDTOS.isEmpty()) {
            return;
        }
        List<Long> moveFeatureIds = piMapper.queryFeatureIds(programId, piId);
        // batch update status
        Long organizationId = ConvertUtil.getOrganizationId(programId);
        //获取状态机id
        Long issueTypeId = null;
        List<IssueTypeVO> issueTypeVOList = issueFeignClient.queryIssueTypesByProjectId(programId, "program").getBody();
        for (IssueTypeVO issueTypeVO : issueTypeVOList) {
            if ("feature".equals(issueTypeVO.getTypeCode())) {
                issueTypeId = issueTypeVO.getId();
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
        issueRepository.batchUpdateFeatureRank(programId, moveIssueDTOS);
    }

    @Override
    public void completeProjectsSprints(Long programId, Long piId, Boolean onlySelectEnable) {
        List<ProjectRelationshipVO> projectRelationshipVOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, onlySelectEnable).getBody();
        if (projectRelationshipVOList == null || projectRelationshipVOList.isEmpty()) {
            return;
        }
        for (ProjectRelationshipVO projectRelationshipVO : projectRelationshipVOList) {
            Long projectId = projectRelationshipVO.getProjectId();
            List<Long> sprintIds = sprintMapper.selectNotDoneByPiId(projectId, piId);
            if (sprintIds != null && !sprintIds.isEmpty()) {
                for (Long sprintId : sprintIds) {
                    SprintCompleteVO sprintCompleteVO = new SprintCompleteVO();
                    sprintCompleteVO.setProjectId(projectId);
                    sprintCompleteVO.setSprintId(sprintId);
                    sprintCompleteVO.setIncompleteIssuesDestination(0L);
                    sprintService.completeSprint(projectId, sprintCompleteVO);
                }
            }
        }
    }

    @Override
    public void completeSprintsWithSelect(Long programId, Long piId, Long nextPiId, Long artId) {
        List<ProjectRelationshipVO> projectRelationshipVOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(programId), programId, true).getBody();
        if (projectRelationshipVOList == null || projectRelationshipVOList.isEmpty()) {
            return;
        }
        ArtDTO artDTO = artMapper.selectByPrimaryKey(artId);
        for (ProjectRelationshipVO projectRelationshipVO : projectRelationshipVOList) {
            Long projectId = projectRelationshipVO.getProjectId();
            List<Long> sprintIds = sprintMapper.selectNotDoneByPiId(projectId, piId);
            SprintDTO newSprint = sprintMapper.selectFirstSprintByPiId(projectId, nextPiId);
            for (Long sprintId : sprintIds) {
                SprintCompleteVO sprintCompleteVO = new SprintCompleteVO();
                sprintCompleteVO.setProjectId(projectId);
                sprintCompleteVO.setSprintId(sprintId);
                sprintCompleteVO.setIncompleteIssuesDestination(SPRINT_COMPLETE_SETTING_NEXT_SPRINT.equals(artDTO.getSprintCompleteSetting()) ? newSprint.getSprintId() : 0L);
                sprintService.completeSprint(projectId, sprintCompleteVO);
            }
        }
    }

    private void autoCreatePi(Long programId, Long artId) {
        List<PiDTO> restPi = piMapper.selectUnDonePiDOList(programId, artId);
        ArtDTO artDTO = artMapper.selectByPrimaryKey(artId);
        int restPiCount = restPi.size();
        int artPiCount = artDTO.getPiCount().intValue();
        PiDTO lastPi = piMapper.selectLastPi(programId, artId);
        Date startDate = lastPi.getEndDate();
        if (restPiCount < artPiCount) {
            Long piCodeNumber = artDTO.getPiCodeNumber();
            Long piWorkDays = getPiWorkDays(artDTO);
            List<Long> sprintIds = new ArrayList<>();
            for (int i = 0; i < artPiCount - restPiCount; i++) {
                PiE piE = new PiE();
                piE.setCode(artDTO.getPiCodePrefix());
                piE.setName(piCodeNumber.toString());
                piCodeNumber++;
                piE.setStatusCode(PI_TODO);
                piE.setArtId(artDTO.getId());
                piE.setProgramId(programId);
                setPiStartAndEndDate(piE, piWorkDays, startDate);
                PiE piRes = piRepository.create(piE);
                // create sprint template
                createSprintTemplate(programId, piRes, artDTO, sprintIds);
                startDate = piE.getEndDate();
            }
            sprintService.updateSprintNameByBatch(programId, sprintIds);
            updateArtPiCodeNumber(programId, artDTO.getId(), piCodeNumber, artDTO.getObjectVersionNumber());
        }
    }

    @Override
    public PiDTO closePi(Long programId, PiVO piVO) {
        piValidator.checkPiClose(piVO);
        // deal uncomplete feature to target pi
        dealUnCompleteFeature(piVO.getProgramId(), piVO.getId(), piVO.getTargetPiId());
        // update pi status: done
        if (piMapper.updateByPrimaryKeySelective(new PiDTO(programId, piVO.getId(), PI_DONE, piVO.getObjectVersionNumber(), new Date())) != 1) {
            throw new CommonException("error.pi.update");
        }
        // auto start next PI
        PiDTO nextPi = piMapper.selectNextPi(programId, piVO.getArtId(), piVO.getId());
        if (nextPi != null) {
            PiVO nextStartPi = new PiVO();
            nextStartPi.setProgramId(programId);
            nextStartPi.setArtId(piVO.getArtId());
            nextStartPi.setId(nextPi.getId());
            nextStartPi.setObjectVersionNumber(nextPi.getObjectVersionNumber());
            nextStartPi.setUpdateStatusId(piVO.getUpdateStatusId());
            startPi(programId, nextStartPi);
        }
        // deal projects' sprints complete
        completeSprintsWithSelect(programId, piVO.getId(), nextPi.getId(), piVO.getArtId());
        autoCreatePi(programId, piVO.getArtId());
        PiDTO piDTO = piMapper.selectByPrimaryKey(piVO.getId());
        sendMsgUtil.sendPmAndEmailAfterPiComplete(programId, piDTO);
        return piDTO;
    }

    private void noOutsetBeforeRank(Long programId, Long piId, MoveIssueVO moveIssueVO, List<MoveIssueDTO> moveIssueDTOS) {
        String minRank = piMapper.queryPiMinRank(programId, piId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueVO.getIssueIds()) {
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
            }
        }
    }

    private void outsetBeforeRank(Long programId, Long piId, MoveIssueVO moveIssueVO, List<MoveIssueDTO> moveIssueDTOS) {
        String rightRank = issueMapper.queryRankByProgram(programId, moveIssueVO.getOutsetIssueId());
        String leftRank = issueMapper.queryLeftRankByProgram(programId, piId, rightRank);
        if (leftRank == null) {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                rightRank = RankUtil.genPre(rightRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, rightRank));
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                rightRank = RankUtil.between(leftRank, rightRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, rightRank));
            }
        }
    }

    private void beforeRank(Long programId, Long piId, MoveIssueVO moveIssueVO, List<MoveIssueDTO> moveIssueDTOS) {
        moveIssueVO.setIssueIds(issueMapper.queryFeatureIdOrderByRankDesc(programId, moveIssueVO.getIssueIds()));
        if (moveIssueVO.getOutsetIssueId() == null || Objects.equals(moveIssueVO.getOutsetIssueId(), 0L)) {
            noOutsetBeforeRank(programId, piId, moveIssueVO, moveIssueDTOS);
        } else {
            outsetBeforeRank(programId, piId, moveIssueVO, moveIssueDTOS);
        }
    }

    private void afterRank(Long programId, Long piId, MoveIssueVO moveIssueVO, List<MoveIssueDTO> moveIssueDTOS) {
        moveIssueVO.setIssueIds(issueMapper.queryFeatureIdOrderByRankAsc(programId, moveIssueVO.getIssueIds()));
        String leftRank = issueMapper.queryRankByProgram(programId, moveIssueVO.getOutsetIssueId());
        String rightRank = issueMapper.queryRightRankByProgram(programId, piId, leftRank);
        if (rightRank == null) {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                leftRank = RankUtil.genNext(leftRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, leftRank));
            }
        } else {
            for (Long issueId : moveIssueVO.getIssueIds()) {
                leftRank = RankUtil.between(leftRank, rightRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, leftRank));
            }
        }
    }

    private void batchUpdateStatus(Long programId, Long piId, List<Long> moveIssueIdsFilter, Long updateStatusId, String categoryCode, Long userId) {
        List<IssueDTO> moveIssues = issueMapper.selectFeatureByMoveIssueIds(programId, moveIssueIdsFilter, categoryCode, piId);
        if (moveIssues == null || moveIssues.isEmpty()) {
            return;
        }
        issueRepository.updateStatusIdBatch(programId, updateStatusId, moveIssues, userId, new Date());
    }

    @Override
    public List<SubFeatureDTO> batchFeatureToPi(Long programId, Long piId, MoveIssueVO moveIssueVO) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDTO> moveIssueDTOS = new ArrayList<>();
        if (moveIssueVO.getBefore()) {
            beforeRank(programId, piId, moveIssueVO, moveIssueDTOS);
        } else {
            afterRank(programId, piId, moveIssueVO, moveIssueDTOS);
        }
        issueRepository.batchUpdateFeatureRank(programId, moveIssueDTOS);
        List<Long> moveIssueIds = moveIssueVO.getIssueIds();
        List<SubFeatureDTO> featureDTOList = piMapper.selectFeatureIdByFeatureIds(programId, moveIssueIds).stream().filter(subFeatureDO -> subFeatureDO.getPiId() == null ? piId != 0 : !subFeatureDO.getPiId().equals(piId)).collect(Collectors.toList());
        if (featureDTOList != null && !featureDTOList.isEmpty()) {
            List<Long> moveIssueIdsFilter = featureDTOList.stream().map(SubFeatureDTO::getIssueId).collect(Collectors.toList());
            // batch update status
            if (moveIssueVO.getUpdateStatusId() != null) {
                batchUpdateStatus(programId, piId, moveIssueIdsFilter, moveIssueVO.getUpdateStatusId(), moveIssueVO.getStatusCategoryCode(), customUserDetails.getUserId());
            }
            BatchRemovePiE batchRemovePiE = new BatchRemovePiE(programId, piId, moveIssueIdsFilter);
            issueRepository.removeFeatureFromPiByIssueIds(batchRemovePiE);
            if (piId != null && !Objects.equals(piId, 0L)) {
                issueRepository.batchFeatureToPi(programId, piId, moveIssueIdsFilter, new Date(), customUserDetails.getUserId());
            }
            return featureDTOList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SubFeatureDTO> batchFeatureToEpic(Long programId, Long epicId, List<Long> featureIds) {
        issueRepository.batchFeatureToEpic(programId, epicId, featureIds);
        issueRepository.updateEpicIdOfStoryByFeatureList(featureIds, epicId);
        return piMapper.selectFeatureIdByFeatureIds(programId, featureIds);
    }

    @Override
    public List<PiNameDTO> queryAllOfProgram(Long programId) {
        List<PiNameDTO> piNameDTOList = piMapper.selectAllOfProgram(programId);
        if (piNameDTOList != null && !piNameDTOList.isEmpty()) {
            return piNameDTOList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<PiNameVO> queryUnfinishedOfProgram(Long programId) {
        ArtDTO activeArt = artMapper.selectActiveArt(programId);
        if (activeArt == null) {
            return new ArrayList<>();
        }
        List<PiDTO> piDTOList = piMapper.selectUnDonePiDOList(programId, activeArt.getId());
        if (piDTOList != null && !piDTOList.isEmpty()) {
            return modelMapper.map(piDTOList, new TypeToken<List<PiNameVO>>(){}.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<PiWithFeatureVO> queryRoadMapOfProgram(Long programId, Long organizationId) {
        ArtDTO activeArt = getActiveArt(programId);
        if (activeArt == null) {
            return new ArrayList<>();
        }
        List<PiWithFeatureDTO> piWithFeatureDTOList = piMapper.selectRoadMapPiList(programId, activeArt.getId());
        if (piWithFeatureDTOList != null && !piWithFeatureDTOList.isEmpty()) {
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            setStatusIsCompleted(programId, statusMapDTOMap);
            Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            return piAssembler.piWithFeatureDOTODTO(piWithFeatureDTOList, statusMapDTOMap, issueTypeDTOMap);
        } else {
            return new ArrayList<>();
        }
    }
}
