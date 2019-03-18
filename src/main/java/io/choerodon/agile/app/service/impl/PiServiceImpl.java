package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.PiValidator;
import io.choerodon.agile.app.assembler.PiAssembler;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
import io.choerodon.agile.domain.agile.entity.ArtE;
import io.choerodon.agile.domain.agile.entity.BatchRemovePiE;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.domain.agile.repository.ArtRepository;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.PiRepository;
import io.choerodon.agile.domain.agile.repository.SprintRepository;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class PiServiceImpl implements PiService {

    public static final String PI_TODO = "todo";
    private static final String ADVANCED_SEARCH_ARGS = "advancedSearchArgs";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY = "yyyy";

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
        Long ipWorkdays = artDO.getIpWorkdays();
        Long interationCount = artDO.getInterationCount();
        Long interationWeeks = artDO.getInterationWeeks();
        return interationCount * interationWeeks * 7 + ipWorkdays;
    }

    private void setPiStartAndEndDate(Long programId, Long artId, PiE piE, Long piWorkDays) {
        PiDO lastPi = piMapper.selectLastPi(programId, artId);
        Date startDate = null;
        Date endDate = null;
        if (lastPi == null) {
            startDate = formatDate(new Date());
            endDate = getSpecifyTimeByOneTime(new Date(), piWorkDays.intValue());
        } else {
            startDate = formatDate(lastPi.getEndDate());
            endDate = getSpecifyTimeByOneTime(lastPi.getEndDate(), piWorkDays.intValue());
        }
        piE.setStartDate(startDate);
        piE.setEndDate(endDate);
    }

    private void createSprintTemplate(Long programId, PiE piRes, ArtDO artDO) {
        Date startDate = piRes.getStartDate();
        Long interationCount = artDO.getInterationCount();
        Long interationWeeks = artDO.getInterationWeeks();
        Date enddate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        for (int i = 0; i < interationCount; i++) {
            sprintService.createSprint(programId, piRes.getId(), startDate, enddate);
            startDate = enddate;
            enddate = getSpecifyTimeByOneTime(startDate, interationWeeks.intValue() * 7);
        }
    }

    @Override
    public void createPi(Long programId, Long piNumber, ArtDO artDO) {
        Long piCodeNumber = artDO.getPiCodeNumber();
        Long piWorkDays = getPiWorkDays(artDO);
        for (int i = 0;i < piNumber; i++) {
            PiE piE = new PiE();
            piE.setCode(artDO.getPiCodePrefix());
            piE.setName(piCodeNumber.toString());
            piCodeNumber++;
            piE.setArtId(artDO.getId());
            piE.setStatusCode(PI_TODO);
            piE.setProgramId(programId);
            setPiStartAndEndDate(programId, artDO.getId(), piE, piWorkDays);
            PiE piRes = piRepository.create(piE);
            // create sprint template
            createSprintTemplate(programId, piRes, artDO);
        }
        updateArtPiCodeNumber(programId, artDO.getId(), piCodeNumber, artDO.getObjectVersionNumber());
    }

    @Override
    public PiDTO updatePi(Long programId, PiDTO piDTO) {
        return ConvertHelper.convert(piRepository.updateBySelective(ConvertHelper.convert(piDTO, PiE.class)), PiDTO.class);
    }

    private Long getActiveArt(Long programId) {
        ArtDO artDO = artMapper.selectActiveArt(programId);
        if (artDO == null) {
            return null;
        } else {
            return artDO.getId();
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
        Long activeArtId = getActiveArt(programId);
        if (activeArtId == null) {
            return result;
        }
        List<PiWithFeatureDO> piWithFeatureDOList = piMapper.selectBacklogPiList(programId, activeArtId, StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)));
        result.put("allPiList", piWithFeatureDOList != null && !piWithFeatureDOList.isEmpty() ? piAssembler.piWithFeatureDOTODTO(piWithFeatureDOList, statusMapDTOMap, issueTypeDTOMap) : new ArrayList<>());
        return result;
    }

    @Override
    public Page<PiDTO> queryAll(Long programId, PageRequest pageRequest) {
        Page<PiDO> piDOPage = PageHelper.doPageAndSort(pageRequest, () ->
                piMapper.selectPiList(programId));
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
        List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(programId).getBody();
        for (ProjectRelationshipDTO projectRelationshipDTO : projectRelationshipDTOList) {
            Long projectId = projectRelationshipDTO.getProjectId();
            for (SprintDO sprint : sprintDOList) {
                SprintE sprintE = new SprintE();
                sprintE.setPiId(sprint.getPiId());
                sprintE.setStartDate(sprint.getStartDate());
                sprintE.setEndDate(sprint.getEndDate());
                sprintE.setSprintName(sprint.getSprintName());
                sprintE.setProjectId(projectId);
                sprintE.setStatusCode(sprint.getStatusCode());
                sprintRepository.createSprint(sprintE);
            }
        }
    }

    @Override
    public PiDTO startPi(Long programId, PiDTO piDTO) {
        piValidator.checkPiStart(piDTO);
        // create sprint in each project
        createSprintWhenStartPi(programId, piDTO.getId());
        // update pi status: doing
        PiE piE = new PiE();
        piE.setProgramId(programId);
        piE.setId(piDTO.getId());
        piE.setStatusCode(piDTO.getStatusCode());
        piE.setObjectVersionNumber(piDTO.getObjectVersionNumber());
        return ConvertHelper.convert(piRepository.updateBySelective(piE), PiDTO.class);
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

    private void dealUnCompleteFeature(Long programId, Long piId, Long targetPiId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        List<Long> moveFeatureRankIds = piMapper.queryFeatureIdOrderByRankDesc(programId, piId);
        beforeRankInProgram(programId, targetPiId, moveIssueDOS, moveFeatureRankIds);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        List<Long> moveFeatureIds = piMapper.queryFeatureIds(programId, piId);
        if (targetPiId != null && !Objects.equals(targetPiId, 0L)) {
            issueRepository.featureToDestinationByIdsClosePi(programId, targetPiId, moveFeatureIds, new Date(), customUserDetails.getUserId());
        }
        issueRepository.batchUpdateFeatureRank(programId, moveIssueDOS);
    }

    @Override
    public PiDTO closePi(Long programId, PiDTO piDTO) {
        piValidator.checkPiClose(piDTO);
        // deal uncomplete feature to target pi
        dealUnCompleteFeature(piDTO.getProgramId(), piDTO.getId(), piDTO.getTargetPiId());
        // update pi status: done
        PiE piE = new PiE();
        piE.setProgramId(programId);
        piE.setId(piDTO.getId());
        piE.setStatusCode(piDTO.getStatusCode());
        return ConvertHelper.convert(piRepository.updateBySelective(piE), PiDTO.class);
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
}
