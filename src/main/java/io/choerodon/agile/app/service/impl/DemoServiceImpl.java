package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.api.vo.event.DemoPayload;
import io.choerodon.agile.api.vo.event.OrganizationRegisterEventPayload;
import io.choerodon.agile.infra.dataobject.BoardColumnDTO;
import io.choerodon.agile.infra.dataobject.BoardDTO;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static final String AGILE_APPLYTYPE = "agile";
    private static final String TEST_APPLYTYPE = "test";

    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private IssueService issueService;

    @Autowired
    private StateMachineClientService stateMachineClientService;

    @Autowired
    private IamFeignClient iamFeignClient;

    @Autowired
    private IssueStatusService issueStatusService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private DataLogMapper dataLogMapper;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private WorkCalendarHolidayRefService workCalendarHolidayRefService;

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Autowired
    private IssueComponentService issueComponentService;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private SagaClient sagaClient;

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    @Autowired
    private IssueLinkService issueLinkService;


    private void setIssueTypeMap(Map<String, IssueTypeWithStateMachineIdVO> issueTypeMap, List<IssueTypeWithStateMachineIdVO> issueTypes) {
        for (IssueTypeWithStateMachineIdVO issueTypeWithStateMachineIdVO : issueTypes) {
            issueTypeMap.put(issueTypeWithStateMachineIdVO.getTypeCode(), issueTypeWithStateMachineIdVO);
        }
    }

    private IssueVO createEpic(Long projectId, String epicName, String summary, PriorityVO defaultPriority, Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap, Long reporterId) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setEpicName(epicName);
        issueCreateVO.setSummary(summary);
        issueCreateVO.setIssueTypeId(agileIssueTypeMap.get("issue_epic").getId());
        issueCreateVO.setPriorityId(defaultPriority.getId());
        issueCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueCreateVO.setTypeCode(agileIssueTypeMap.get("issue_epic").getTypeCode());
        issueCreateVO.setReporterId(reporterId);
        return stateMachineClientService.createIssue(issueCreateVO, AGILE_APPLYTYPE);
    }

    private IssueVO createStory(Long projectId, String summary, PriorityVO defaultPriority, Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap, Long sprintId, BigDecimal storyPoint, Long epicId, Long reporterId) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setSummary(summary);
        issueCreateVO.setIssueTypeId(agileIssueTypeMap.get("story").getId());
        issueCreateVO.setPriorityId(defaultPriority.getId());
        issueCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueCreateVO.setTypeCode(agileIssueTypeMap.get("story").getTypeCode());
        issueCreateVO.setSprintId(sprintId);
        issueCreateVO.setStoryPoints(storyPoint);
        issueCreateVO.setEpicId(epicId);
        issueCreateVO.setReporterId(reporterId);
        return stateMachineClientService.createIssue(issueCreateVO, AGILE_APPLYTYPE);
    }

    private IssueVO createTask(Long projectId, String summary, PriorityVO defaultPriority, Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap, Long sprintId, Long reporterId) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setSummary(summary);
        issueCreateVO.setIssueTypeId(agileIssueTypeMap.get("task").getId());
        issueCreateVO.setPriorityId(defaultPriority.getId());
        issueCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueCreateVO.setTypeCode(agileIssueTypeMap.get("task").getTypeCode());
        issueCreateVO.setSprintId(sprintId);
        issueCreateVO.setReporterId(reporterId);
        return stateMachineClientService.createIssue(issueCreateVO, AGILE_APPLYTYPE);
    }

    private IssueVO createBug(Long projectId, String summary, PriorityVO defaultPriority, Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap, Long sprintId, Long reporterId) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setSummary(summary);
        issueCreateVO.setIssueTypeId(agileIssueTypeMap.get("bug").getId());
        issueCreateVO.setPriorityId(defaultPriority.getId());
        issueCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueCreateVO.setTypeCode(agileIssueTypeMap.get("bug").getTypeCode());
        issueCreateVO.setSprintId(sprintId);
        issueCreateVO.setReporterId(reporterId);
        return stateMachineClientService.createIssue(issueCreateVO, AGILE_APPLYTYPE);
    }

    private IssueVO createTest(Long projectId, String summary, PriorityVO defaultPriority, Map<String, IssueTypeWithStateMachineIdVO> testIssueTypeMap) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        issueCreateVO.setProjectId(projectId);
        issueCreateVO.setSummary(summary);
        issueCreateVO.setIssueTypeId(testIssueTypeMap.get("issue_test").getId());
        issueCreateVO.setPriorityId(defaultPriority.getId());
        issueCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueCreateVO.setTypeCode(testIssueTypeMap.get("issue_test").getTypeCode());
        return stateMachineClientService.createIssue(issueCreateVO, TEST_APPLYTYPE);
    }

    private IssueSubVO createSubTask(Long projectId, String summary, PriorityVO defaultPriority, Long sprintId, Long parentIssueId, Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap, Long reporterId) {
        IssueSubCreateVO issueSubCreateVO = new IssueSubCreateVO();
        issueSubCreateVO.setProjectId(projectId);
        issueSubCreateVO.setSummary(summary);
        issueSubCreateVO.setPriorityId(defaultPriority.getId());
        issueSubCreateVO.setPriorityCode("priority-" + defaultPriority.getId());
        issueSubCreateVO.setSprintId(sprintId);
        issueSubCreateVO.setParentIssueId(parentIssueId);
        issueSubCreateVO.setIssueTypeId(agileIssueTypeMap.get("sub_task").getId());
        issueSubCreateVO.setReporterId(reporterId);
        return stateMachineClientService.createSubIssue(issueSubCreateVO);
    }

    private Date getSpecifyTimeByOneTime(Date date, int amount) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, amount);
        Date d = c.getTime();
        Date res = null;
        try {
            res = format.parse(format.format(d));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return res;
    }

    private void updateFixVersion(Long projectId, Long issueId, Long versionId) {
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setIssueId(issueId);
        issueUpdateVO.setVersionType("fix");
        List<VersionIssueRelVO> versionIssueRelVOList = new ArrayList<>();
        VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
        versionIssueRelVO.setVersionId(versionId);
        versionIssueRelVOList.add(versionIssueRelVO);
        issueUpdateVO.setVersionIssueRelVOList(versionIssueRelVOList);
        List<String> fieldList = new ArrayList<>();
        issueService.updateIssue(projectId, issueUpdateVO, fieldList);
    }

    private void updateComponent(Long projectId, Long issueId, Long componentId) {
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setIssueId(issueId);
        List<ComponentIssueRelVO> componentIssueRelVOList = new ArrayList<>();
        ComponentIssueRelVO componentIssueRelVO = new ComponentIssueRelVO();
        componentIssueRelVO.setIssueId(issueId);
        componentIssueRelVO.setComponentId(componentId);
        componentIssueRelVO.setProjectId(projectId);
        componentIssueRelVOList.add(componentIssueRelVO);
        issueUpdateVO.setComponentIssueRelVOList(componentIssueRelVOList);
        List<String> fieldList = new ArrayList<>();
        issueService.updateIssue(projectId, issueUpdateVO, fieldList);
    }

    private void updateLabel(Long projectId, Long issueId, String labelName) {
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setIssueId(issueId);
        List<LabelIssueRelVO> labelIssueRelVOList = new ArrayList<>();
        LabelIssueRelVO labelIssueRelVO = new LabelIssueRelVO();
        labelIssueRelVO.setProjectId(projectId);
        labelIssueRelVO.setLabelName(labelName);
        labelIssueRelVOList.add(labelIssueRelVO);
        issueUpdateVO.setLabelIssueRelVOList(labelIssueRelVOList);
        List<String> fieldList = new ArrayList<>();
        issueService.updateIssue(projectId, issueUpdateVO, fieldList);
    }

    private void updateRemainTime(Long projectId, Long issueId, BigDecimal remainingTime, Long objectVersionNumber) {
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setIssueId(issueId);
        issueUpdateVO.setRemainingTime(remainingTime);
        issueUpdateVO.setObjectVersionNumber(objectVersionNumber);
        List<String> fieldList = new ArrayList<>();
        fieldList.add("remainingTime");
        issueService.updateIssue(projectId, issueUpdateVO, fieldList);
    }

    private void updatePriority(Long projectId, Long issueId, Long priorityId, Long objectVersionNumber) {
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        issueUpdateVO.setIssueId(issueId);
        issueUpdateVO.setPriorityId(priorityId);
        issueUpdateVO.setObjectVersionNumber(objectVersionNumber);
        List<String> fieldList = new ArrayList<>();
        fieldList.add("priorityId");
        issueService.updateIssue(projectId, issueUpdateVO, fieldList);
    }

    /**
     * 封装返回 转换名称：转化ID 的Map
     *
     * @param transformMap
     * @param transformVOList
     */
    private void setTransformMap(Map<String, Long> transformMap, List<TransformVO> transformVOList) {
        for (TransformVO transformVO : transformVOList) {
            transformMap.put(transformVO.getName(), transformVO.getId());
        }
    }

    private void completeIssue(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, Long sprintId, Long statusId) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setProjectId(projectId);
        BoardDTO boardRes = boardMapper.selectOne(boardDTO);

        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.setProjectId(projectId);
        boardColumnDTO.setBoardId(boardRes.getBoardId());
        List<BoardColumnDTO> boardColumnList = boardColumnMapper.select(boardColumnDTO);
        Map<String, Long> columnMap = new HashMap<>();
        for (BoardColumnDTO boardColumn : boardColumnList) {
            columnMap.put(boardColumn.getCategoryCode(), boardColumn.getColumnId());
        }

        IssueMoveVO issueMoveVO = new IssueMoveVO();
        issueMoveVO.setBefore(true);
        issueMoveVO.setBoardId(boardRes.getBoardId());
        issueMoveVO.setIssueId(issueId);
        issueMoveVO.setObjectVersionNumber(objectVersionNumber);
        issueMoveVO.setRankFlag(false);
        issueMoveVO.setSprintId(sprintId);
        issueMoveVO.setStatusId(statusId);
        boardService.move(projectId, issueId, transformId, issueMoveVO, true);
    }

    private void setPriorityMap(List<PriorityVO> priorityVOList, Map<String, Long> priorityMap) {
        for (PriorityVO priorityVO : priorityVOList) {
            priorityMap.put(priorityVO.getName(), priorityVO.getId());
        }
    }

    private void startSprint(Long projectId, Long sprintId, Long objectVersionNumber, Date startDate, Date endDate) {
        SprintUpdateVO sprintUpdateVO = new SprintUpdateVO();
        sprintUpdateVO.setSprintId(sprintId);
        sprintUpdateVO.setObjectVersionNumber(objectVersionNumber);
        sprintUpdateVO.setProjectId(projectId);
        sprintUpdateVO.setStartDate(startDate);
        sprintUpdateVO.setEndDate(endDate);
        sprintService.startSprint(projectId, sprintUpdateVO);
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
        if (w < 0)
            w = 0;
        return w;
    }

    /**
     * 获取当前年分
     *
     * @return
     */
    public Integer getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return Integer.parseInt(sdf.format(date));
    }

    private Boolean judgeBeWork(Date date, List<WorkCalendarHolidayRefVO> calendarDays) {
        int week = getWeekOfDate(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
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

    /**
     * 获取第一个冲刺的工作日期
     *
     * @param endDate
     * @return
     */
    private List<Date> getWorkDays(Date endDate) {
        List<WorkCalendarHolidayRefVO> calendarDays = workCalendarHolidayRefService.queryByYearIncludeLastAndNext(getCurrentYear());
        int sumDays = 0;
        int ago = -1;
        List<Date> result = new ArrayList<>();
        while (true) {
            Date date = getSpecifyTimeByOneTime(endDate, ago);
            if (judgeBeWork(date, calendarDays)) {
                sumDays++;
                result.add(date);
            }
            if (sumDays == 14) {
                break;
            }
            ago--;
        }
        return result;
    }

    /**
     * 获取第二个冲刺的工作日期
     *
     * @param startDate
     * @return
     */
    private List<Date> getWorkDaysAfter(Date startDate) {
        List<WorkCalendarHolidayRefVO> calendarDays = workCalendarHolidayRefService.queryByYearIncludeLastAndNext(getCurrentYear());
        int sumDays = 0;
        int ago = 0;
        List<Date> result = new ArrayList<>();
        while (true) {
            Date date = getSpecifyTimeByOneTime(startDate, ago);
            if (judgeBeWork(date, calendarDays)) {
                sumDays++;
                result.add(date);
            }
            if (sumDays == 10) {
                break;
            }
            ago++;
        }
        return result;
    }

    private IssueComponentVO createComponent(Long projectId, String name) {
        IssueComponentVO issueComponentVO = new IssueComponentVO();
        issueComponentVO.setProjectId(projectId);
        issueComponentVO.setManagerId(0L);
        issueComponentVO.setName(name);
        issueComponentVO.setDefaultAssigneeRole("无");
        return issueComponentService.create(projectId, issueComponentVO);
    }

    /**
     * 更新项目时间及issue创建时间
     *
     * @param projectId
     */
    private void updateProjectAndIssues(Long projectId, Date date1, Date date2) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(projectId);
        ProjectInfoDTO projectResult = projectInfoMapper.selectOne(projectInfoDTO);
        if (projectResult == null) {
            throw new CommonException("error.project.get");
        }
        projectInfoMapper.updateProjectAndIssues(projectId, date1, date2);
    }

    /**
     * 日期相加减
     * @param cur
     * @return
     */
    public Date getNewDate(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);   //设置时间
        c.add(Calendar.DATE, 30); //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime(); //结果
        return date;
    }


    @Saga(code = "agile-demo-for-test", description = "为测试提供demo数据", inputSchemaClass = DemoPayload.class)
    @Override
    public OrganizationRegisterEventPayload demoInit(OrganizationRegisterEventPayload demoProjectPayload) {

        Long projectId = demoProjectPayload.getProject().getId();
        Long userId1 = demoProjectPayload.getUser().getId();
        Long userId2 = demoProjectPayload.getUserA().getId();

        // 查询项目信息
        ProjectVO projectVO = iamFeignClient.queryProject(projectId).getBody();
        Long organizationId = projectVO.getOrganizationId();

        // 创建第一个冲刺
        SprintDetailVO sprintDetailVO = sprintService.createSprint(projectId);
        Long sprintId1 = sprintDetailVO.getSprintId();

        // 创建第二个冲刺
        SprintDetailVO sprintDetailVO2 = sprintService.createSprint(projectId);
        Long sprintId2 = sprintDetailVO2.getSprintId();

        // 更新冲刺名称
        SprintUpdateVO updateName1 = new SprintUpdateVO();
        updateName1.setProjectId(projectId);
        updateName1.setSprintId(sprintDetailVO.getSprintId());
        updateName1.setObjectVersionNumber(sprintDetailVO.getObjectVersionNumber());
        updateName1.setSprintName("Sprint 1");
        sprintDetailVO = sprintService.updateSprint(projectId, updateName1);

        SprintUpdateVO updateName2 = new SprintUpdateVO();
        updateName2.setProjectId(projectId);
        updateName2.setSprintId(sprintDetailVO2.getSprintId());
        updateName2.setObjectVersionNumber(sprintDetailVO2.getObjectVersionNumber());
        updateName2.setSprintName("Sprint 2");
        sprintDetailVO2 = sprintService.updateSprint(projectId, updateName2);

        // 创建版本
        ProductVersionCreateVO productVersionCreateVO = new ProductVersionCreateVO();
        productVersionCreateVO.setName("v1.0");
        productVersionCreateVO.setProjectId(projectId);
        productVersionCreateVO.setStartDate(new Date());
        productVersionCreateVO.setExpectReleaseDate(getNewDate(new Date()));
        ProductVersionDetailVO productVersionDetailVO = productVersionService.createVersion(projectId, productVersionCreateVO);


        // 查询issue type类型
        List<IssueTypeWithStateMachineIdVO> agileIssueTypes = issueFeignClient.queryIssueTypesWithStateMachineIdByProjectId(projectId, AGILE_APPLYTYPE).getBody();
        Map<String, IssueTypeWithStateMachineIdVO> agileIssueTypeMap = new HashMap<>();
        setIssueTypeMap(agileIssueTypeMap, agileIssueTypes);

        // 查询测试的issue type类型
        List<IssueTypeWithStateMachineIdVO> testIssueTypes = issueFeignClient.queryIssueTypesWithStateMachineIdByProjectId(projectId, TEST_APPLYTYPE).getBody();
        Map<String, IssueTypeWithStateMachineIdVO> testIssueTypeMap = new HashMap<>();
        setIssueTypeMap(testIssueTypeMap, testIssueTypes);

        // 查询优先级列表
        PriorityVO defaultPriority = issueFeignClient.queryDefaultByOrganizationId(organizationId).getBody();


        // 创建史诗
        IssueVO epic1 = createEpic(projectId, "账户管理", "账户管理", defaultPriority, agileIssueTypeMap, userId1);
        IssueVO epic2 = createEpic(projectId, "商品查看", "产品信息的查看、搜索", defaultPriority, agileIssueTypeMap, userId1);
        IssueVO epic3 = createEpic(projectId, "商品购买", "购买信息、地址、数量等信息的填写确认", defaultPriority, agileIssueTypeMap, userId1);
        IssueVO epic4 = createEpic(projectId, "支付", "支付", defaultPriority, agileIssueTypeMap, userId2);
        IssueVO epic5 = createEpic(projectId, "退货", "退货申请、受理", defaultPriority, agileIssueTypeMap, userId2);


        // 创建故事及子任务
        IssueVO story1 = createStory(projectId, "账户登录", defaultPriority, agileIssueTypeMap, sprintId1, new BigDecimal(2), epic1.getIssueId(), userId1);
        IssueSubVO subtask1 = createSubTask(projectId, "账户登录后端接口编写", defaultPriority, sprintId1, story1.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask2 = createSubTask(projectId, "登录页面开发", defaultPriority, sprintId1, story1.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story2 = createStory(projectId, "商品列表", defaultPriority, agileIssueTypeMap, sprintId1, new BigDecimal(4), epic2.getIssueId(), userId1);
        IssueSubVO subtask3 = createSubTask(projectId, "商品列表查询接口实现", defaultPriority, sprintId1, story2.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask4 = createSubTask(projectId, "商品列表页面开发", defaultPriority, sprintId1, story2.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story3 = createStory(projectId, "商品详情查看", defaultPriority, agileIssueTypeMap, sprintId1, new BigDecimal(5), epic2.getIssueId(), userId1);
        IssueSubVO subtask5 = createSubTask(projectId, "商品详情页面开发", defaultPriority, sprintId1, story3.getIssueId(), agileIssueTypeMap, userId2);
        IssueSubVO subtask6 = createSubTask(projectId, "根据商品id查询商品详情接口", defaultPriority, sprintId1, story3.getIssueId(), agileIssueTypeMap, userId1);

        IssueVO story4 = createStory(projectId, "提交订单", defaultPriority, agileIssueTypeMap, sprintId1, new BigDecimal(10), epic3.getIssueId(), userId2);
        IssueSubVO subtask7 = createSubTask(projectId, "提交订单逻辑实现", defaultPriority, sprintId1, story4.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask8 = createSubTask(projectId, "商品订单提交页面开发", defaultPriority, sprintId1, story4.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story5 = createStory(projectId, "维护配送信息", defaultPriority, agileIssueTypeMap, sprintId1, new BigDecimal(1), epic1.getIssueId(), userId2);
        IssueSubVO subtask9 = createSubTask(projectId, "维护配送查询接口实现", defaultPriority, sprintId1, story5.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask10 = createSubTask(projectId, "配送信息展示页面开发", defaultPriority, sprintId1, story5.getIssueId(), agileIssueTypeMap, userId2);
        IssueSubVO subtask11 = createSubTask(projectId, "配送信息维护页面开发", defaultPriority, sprintId1, story5.getIssueId(), agileIssueTypeMap, userId2);

        // 创建bug
        IssueVO bug1 = createBug(projectId, "提交订单后台空指针报错修复", defaultPriority, agileIssueTypeMap, sprintId1, userId1);

        IssueVO story6 = createStory(projectId, "商品搜索", defaultPriority, agileIssueTypeMap, sprintId2, new BigDecimal(3), epic2.getIssueId(), userId1);
        IssueSubVO subtask16 = createSubTask(projectId, "后端-商品搜索接口实现", defaultPriority, sprintId2, story6.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask17 = createSubTask(projectId, "前端-增加商品搜索功能", defaultPriority, sprintId2, story6.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story7 = createStory(projectId, "添加商品到购物车", defaultPriority, agileIssueTypeMap, sprintId2, new BigDecimal(3), epic3.getIssueId(), userId2);
        IssueSubVO subtask18 = createSubTask(projectId, "后端-商品添加到购物车接口实现", defaultPriority, sprintId2, story7.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask19 = createSubTask(projectId, "前端-商品添加到购物车功能", defaultPriority, sprintId2, story7.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story8 = createStory(projectId, "支付宝支付", defaultPriority, agileIssueTypeMap, sprintId2, new BigDecimal(5), epic4.getIssueId(), userId2);
        IssueSubVO subtask20 = createSubTask(projectId, "后端-支付页面增加支付宝支付", defaultPriority, sprintId2, story8.getIssueId(), agileIssueTypeMap, userId1);
        IssueSubVO subtask21 = createSubTask(projectId, "前端-支付页面增加支付宝支付", defaultPriority, sprintId2, story8.getIssueId(), agileIssueTypeMap, userId2);

        IssueVO story9 = createStory(projectId, "支持微信支付", defaultPriority, agileIssueTypeMap, 0L, null, epic4.getIssueId(), userId1);
        IssueVO story10 = createStory(projectId, "支持信用卡支付", defaultPriority, agileIssueTypeMap, 0L, null, epic4.getIssueId(), userId1);
        IssueVO story11 = createStory(projectId, "退货申请", defaultPriority, agileIssueTypeMap, 0L, null, epic5.getIssueId(), userId2);
        IssueVO story12 = createStory(projectId, "退款", defaultPriority, agileIssueTypeMap, 0L, null, epic5.getIssueId(), userId2);


        // 创建任务及子任务
        IssueVO task2 = createTask(projectId, "订单配送信息查看", defaultPriority, agileIssueTypeMap, sprintId2, userId1);
        IssueVO task3 = createTask(projectId, "环境准备", defaultPriority, agileIssueTypeMap, sprintId1, userId1);

        IssueVO task1 = createTask(projectId, "UI设计", defaultPriority, agileIssueTypeMap, sprintId1, userId2);
        IssueSubVO subtask12 = createSubTask(projectId, "UI-购物车页面", defaultPriority, sprintId1, task1.getIssueId(), agileIssueTypeMap, userId2);
        IssueSubVO subtask13 = createSubTask(projectId, "UI-支付页面", defaultPriority, sprintId1, task1.getIssueId(), agileIssueTypeMap, userId2);
        IssueSubVO subtask14 = createSubTask(projectId, "UI-商品搜索页面", defaultPriority, sprintId1, task1.getIssueId(), agileIssueTypeMap, userId2);
        IssueSubVO subtask15 = createSubTask(projectId, "UI-收货地址选择页面", defaultPriority, sprintId1, task1.getIssueId(), agileIssueTypeMap, userId2);


        // 创建测试问题
        IssueVO test1 = createTest(projectId, "用户登录", defaultPriority, testIssueTypeMap);
        IssueVO test2 = createTest(projectId, "登录错误操作", defaultPriority, testIssueTypeMap);
        IssueVO test3 = createTest(projectId, "用户维护配送信息", defaultPriority, testIssueTypeMap);
        IssueVO test4 = createTest(projectId, "通过商品详情快速下单", defaultPriority, testIssueTypeMap);

        // 更新优先级
        List<PriorityVO> priorityVOList = issueFeignClient.queryByOrganizationIdList(organizationId).getBody();
        Map<String, Long> priorityMap = new HashMap<>();
        setPriorityMap(priorityVOList, priorityMap);
        updatePriority(projectId, task3.getIssueId(), priorityMap.get("高"), 1L);
        updatePriority(projectId, task1.getIssueId(), priorityMap.get("高"), 1L);
        updatePriority(projectId, story1.getIssueId(), priorityMap.get("高"), 1L);
        updatePriority(projectId, story2.getIssueId(), priorityMap.get("高"), 1L);
        updatePriority(projectId, story6.getIssueId(), priorityMap.get("低"), 1L);
        updatePriority(projectId, story8.getIssueId(), priorityMap.get("低"), 1L);
        updatePriority(projectId, story9.getIssueId(), priorityMap.get("低"), 1L);
        updatePriority(projectId, story10.getIssueId(), priorityMap.get("低"), 1L);
        updatePriority(projectId, subtask1.getIssueId(), priorityMap.get("高"), 1L);

        // 更新remainTime时间
        updateRemainTime(projectId, subtask8.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask7.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask6.getIssueId(), new BigDecimal(3), 1L);
        updateRemainTime(projectId, subtask5.getIssueId(), new BigDecimal(8), 1L);
        updateRemainTime(projectId, subtask4.getIssueId(), new BigDecimal(3), 1L);
        updateRemainTime(projectId, subtask3.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask2.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask1.getIssueId(), new BigDecimal(6), 2L);
        updateRemainTime(projectId, subtask9.getIssueId(), new BigDecimal(3), 1L);
        updateRemainTime(projectId, subtask10.getIssueId(), new BigDecimal(5), 1L);
        updateRemainTime(projectId, subtask11.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, task3.getIssueId(), new BigDecimal(8), 2L);
        updateRemainTime(projectId, task1.getIssueId(), new BigDecimal(12), 2L);
        updateRemainTime(projectId, subtask12.getIssueId(), new BigDecimal(5), 1L);
        updateRemainTime(projectId, subtask13.getIssueId(), new BigDecimal(2), 1L);
        updateRemainTime(projectId, subtask14.getIssueId(), new BigDecimal(2), 1L);
        updateRemainTime(projectId, subtask15.getIssueId(), new BigDecimal(3), 1L);

        updateRemainTime(projectId, subtask16.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask17.getIssueId(), new BigDecimal(8), 1L);
        updateRemainTime(projectId, subtask18.getIssueId(), new BigDecimal(10), 1L);
        updateRemainTime(projectId, subtask19.getIssueId(), new BigDecimal(6), 1L);
        updateRemainTime(projectId, subtask20.getIssueId(), new BigDecimal(10), 1L);
        updateRemainTime(projectId, subtask21.getIssueId(), new BigDecimal(8), 1L);

        updateRemainTime(projectId, bug1.getIssueId(), new BigDecimal(2), 1L);


        // 更新版本
        updateFixVersion(projectId, task2.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story1.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story2.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story3.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story4.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story5.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story6.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story7.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, story8.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, task3.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask1.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask2.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask3.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask4.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask5.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask6.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask7.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask8.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask9.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask10.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask11.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask12.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask13.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask14.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask15.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask16.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask17.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask18.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask19.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask20.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, subtask21.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, test1.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, test2.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, test3.getIssueId(), productVersionDetailVO.getVersionId());
        updateFixVersion(projectId, test4.getIssueId(), productVersionDetailVO.getVersionId());

        // 创建issueLink
        List<IssueLinkCreateVO> issueLinkCreateVOList = new ArrayList<>();
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        issueLinkTypeDTO.setProjectId(projectId);
        List<IssueLinkTypeDTO> issueLinkTypeDTOS = issueLinkTypeMapper.select(issueLinkTypeDTO);
        Long wantLinkTypeId = null;
        for (IssueLinkTypeDTO issueLinkTypeDTO1 : issueLinkTypeDTOS) {
            if ("阻塞".equals(issueLinkTypeDTO1.getLinkName())) {
                wantLinkTypeId = issueLinkTypeDTO1.getLinkTypeId();
            }
        }
        IssueLinkCreateVO issueLinkCreateVO = new IssueLinkCreateVO();
        issueLinkCreateVO.setLinkTypeId(wantLinkTypeId);
        issueLinkCreateVO.setIssueId(test4.getIssueId());
        issueLinkCreateVO.setLinkedIssueId(story4.getIssueId());
        issueLinkCreateVOList.add(issueLinkCreateVO);
        issueLinkService.createIssueLinkList(issueLinkCreateVOList, story4.getIssueId(), projectId);

        // 完成冲刺1所有的issue
        List<IssueStatusVO> issueStatusVOS = issueStatusService.queryIssueStatusList(projectId);
        Map<String, Long> statusMap = new HashMap<>();
        for (IssueStatusVO issueStatusVO : issueStatusVOS) {
            statusMap.put(issueStatusVO.getCategoryCode(), issueStatusVO.getStatusId());
        }
        Long currentStatusId1 = story1.getStatusId();
        List<TransformVO> transformVOList1 = issueFeignClient.queryTransformsByProjectId(projectId, currentStatusId1, story1.getIssueId(), story1.getIssueTypeId(), "agile").getBody();
        Map<String, Long> transformMap1 = new HashMap<>();
        setTransformMap(transformMap1, transformVOList1);
        Long completeTransformId1 = transformMap1.get("全部转换到已完成");
        Long doingTransformId1 = transformMap1.get("全部转换到处理中");


        Long currentStatusId2 = task1.getStatusId();
        List<TransformVO> transformVOList2 = issueFeignClient.queryTransformsByProjectId(projectId, currentStatusId2, task1.getIssueId(), task1.getIssueTypeId(), "agile").getBody();
        Map<String, Long> transformMap2 = new HashMap<>();
        setTransformMap(transformMap2, transformVOList2);
        Long completeTransformId2 = transformMap2.get("全部转换到已完成");
        Long doingTransformId2 = transformMap2.get("全部转换到处理中");

        Long currentStatusId4 = bug1.getStatusId();
        List<TransformVO> transformVOList4 = issueFeignClient.queryTransformsByProjectId(projectId, currentStatusId4, bug1.getIssueId(), bug1.getIssueTypeId(), "agile").getBody();
        Map<String, Long> transformMap4 = new HashMap<>();
        setTransformMap(transformMap4, transformVOList4);
        Long completeTransformId4 = transformMap4.get("全部转换到已完成");
        Long doingTransformId4 = transformMap4.get("全部转换到已完成");


        Long currentStatusId3 = subtask1.getStatusId();
        List<TransformVO> transformVOList3 = issueFeignClient.queryTransformsByProjectId(projectId, currentStatusId3, subtask1.getIssueId(), subtask1.getIssueTypeId(), "agile").getBody();
        Map<String, Long> transformMap3 = new HashMap<>();
        setTransformMap(transformMap3, transformVOList3);
        Long completeTransformId3 = transformMap3.get("全部转换到已完成");
        Long doingTransformId3 = transformMap3.get("全部转换到处理中");


        // 完成冲刺1的所有issue
        completeIssue(projectId, story1.getIssueId(), doingTransformId1, story1.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, story2.getIssueId(), doingTransformId1, story2.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, story3.getIssueId(), doingTransformId1, story3.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, story4.getIssueId(), doingTransformId1, story4.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, story5.getIssueId(), doingTransformId1, story5.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, task1.getIssueId(), doingTransformId2, task1.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, task3.getIssueId(), doingTransformId2, task3.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask1.getIssueId(), doingTransformId3, subtask1.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask2.getIssueId(), doingTransformId3, subtask2.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask3.getIssueId(), doingTransformId3, subtask3.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask4.getIssueId(), doingTransformId3, subtask4.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask5.getIssueId(), doingTransformId3, subtask5.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask6.getIssueId(), doingTransformId3, subtask6.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask7.getIssueId(), doingTransformId3, subtask7.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask8.getIssueId(), doingTransformId3, subtask8.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask9.getIssueId(), doingTransformId3, subtask9.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask10.getIssueId(), doingTransformId3, subtask10.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask11.getIssueId(), doingTransformId3, subtask11.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask12.getIssueId(), doingTransformId3, subtask12.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask13.getIssueId(), doingTransformId3, subtask13.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask14.getIssueId(), doingTransformId3, subtask14.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, subtask15.getIssueId(), doingTransformId3, subtask15.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));
        completeIssue(projectId, bug1.getIssueId(), doingTransformId4, bug1.getObjectVersionNumber(), sprintId1, statusMap.get("doing"));

        completeIssue(projectId, story1.getIssueId(), completeTransformId1, story1.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, story2.getIssueId(), completeTransformId1, story2.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, story3.getIssueId(), completeTransformId1, story3.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, story4.getIssueId(), completeTransformId1, story4.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, story5.getIssueId(), completeTransformId1, story5.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, task1.getIssueId(), completeTransformId2, task1.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, task3.getIssueId(), completeTransformId2, task3.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask1.getIssueId(), completeTransformId3, subtask1.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask2.getIssueId(), completeTransformId3, subtask2.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask3.getIssueId(), completeTransformId3, subtask3.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask4.getIssueId(), completeTransformId3, subtask4.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask5.getIssueId(), completeTransformId3, subtask5.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask6.getIssueId(), completeTransformId3, subtask6.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask7.getIssueId(), completeTransformId3, subtask7.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask8.getIssueId(), completeTransformId3, subtask8.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask9.getIssueId(), completeTransformId3, subtask9.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask10.getIssueId(), completeTransformId3, subtask10.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask11.getIssueId(), completeTransformId3, subtask11.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask12.getIssueId(), completeTransformId3, subtask12.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask13.getIssueId(), completeTransformId3, subtask13.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask14.getIssueId(), completeTransformId3, subtask14.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, subtask15.getIssueId(), completeTransformId3, subtask15.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));
        completeIssue(projectId, bug1.getIssueId(), completeTransformId4, bug1.getObjectVersionNumber()+1, sprintId1, statusMap.get("done"));

        // 完成冲刺2的任务
        completeIssue(projectId, subtask16.getIssueId(), doingTransformId3, subtask16.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, subtask16.getIssueId(), completeTransformId3, subtask16.getObjectVersionNumber()+1, sprintId2, statusMap.get("done"));

        completeIssue(projectId, subtask17.getIssueId(), doingTransformId3, subtask17.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, subtask17.getIssueId(), completeTransformId3, subtask17.getObjectVersionNumber()+1, sprintId2, statusMap.get("done"));

        completeIssue(projectId, story6.getIssueId(), doingTransformId1, story6.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, story6.getIssueId(), completeTransformId1, story6.getObjectVersionNumber()+1, sprintId2, statusMap.get("done"));

        completeIssue(projectId, subtask21.getIssueId(), doingTransformId3, subtask21.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, subtask18.getIssueId(), doingTransformId3, subtask18.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, subtask19.getIssueId(), doingTransformId3, subtask19.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));

        completeIssue(projectId, task2.getIssueId(), doingTransformId2, task2.getObjectVersionNumber(), sprintId2, statusMap.get("doing"));
        completeIssue(projectId, task2.getIssueId(), completeTransformId2, task2.getObjectVersionNumber()+1, sprintId2, statusMap.get("done"));


        // 创建模块与关联模块
        IssueComponentVO issueComponentVO1 = createComponent(projectId, "用户模块");
        IssueComponentVO issueComponentVO2 = createComponent(projectId, "订单模块");
        IssueComponentVO issueComponentVO3 = createComponent(projectId, "商品模块");
        IssueComponentVO issueComponentVO4 = createComponent(projectId, "设计模块");
        IssueComponentVO issueComponentVO5 = createComponent(projectId, "环境");
        IssueComponentVO issueComponentVO6 = createComponent(projectId, "购买模块");
        IssueComponentVO issueComponentVO7 = createComponent(projectId, "支付模块");
        IssueComponentVO issueComponentVO8 = createComponent(projectId, "售后");
        updateComponent(projectId, story1.getIssueId(), issueComponentVO1.getComponentId());
        updateComponent(projectId, story5.getIssueId(), issueComponentVO1.getComponentId());
        updateComponent(projectId, story4.getIssueId(), issueComponentVO2.getComponentId());
        updateComponent(projectId, story2.getIssueId(), issueComponentVO3.getComponentId());
        updateComponent(projectId, story6.getIssueId(), issueComponentVO3.getComponentId());
        updateComponent(projectId, task1.getIssueId(), issueComponentVO4.getComponentId());
        updateComponent(projectId, task3.getIssueId(), issueComponentVO5.getComponentId());
        updateComponent(projectId, story7.getIssueId(), issueComponentVO6.getComponentId());
        updateComponent(projectId, story8.getIssueId(), issueComponentVO7.getComponentId());
        updateComponent(projectId, story9.getIssueId(), issueComponentVO7.getComponentId());
        updateComponent(projectId, story10.getIssueId(), issueComponentVO7.getComponentId());
        updateComponent(projectId, story11.getIssueId(), issueComponentVO8.getComponentId());
        updateComponent(projectId, story12.getIssueId(), issueComponentVO1.getComponentId());

        // 创建及关联标签
        updateLabel(projectId, subtask1.getIssueId(), "后端");
        updateLabel(projectId, subtask3.getIssueId(), "后端");
        updateLabel(projectId, subtask6.getIssueId(), "后端");
        updateLabel(projectId, subtask7.getIssueId(), "后端");
        updateLabel(projectId, subtask9.getIssueId(), "后端");
        updateLabel(projectId, subtask2.getIssueId(), "前端");
        updateLabel(projectId, subtask4.getIssueId(), "前端");
        updateLabel(projectId, subtask5.getIssueId(), "前端");
        updateLabel(projectId, subtask8.getIssueId(), "前端");
        updateLabel(projectId, subtask10.getIssueId(), "前端");
        updateLabel(projectId, subtask11.getIssueId(), "前端");
        updateLabel(projectId, story1.getIssueId(), "用户账户");
        updateLabel(projectId, subtask1.getIssueId(), "用户账户");
        updateLabel(projectId, subtask2.getIssueId(), "用户账户");
        updateLabel(projectId, task1.getIssueId(), "UI设计");
        updateLabel(projectId, subtask12.getIssueId(), "UI设计");
        updateLabel(projectId, subtask13.getIssueId(), "UI设计");
        updateLabel(projectId, subtask14.getIssueId(), "UI设计");
        updateLabel(projectId, subtask15.getIssueId(), "UI设计");
        updateLabel(projectId, task3.getIssueId(), "部署");

        // 开启冲刺1
        List<Date> workDays = getWorkDays(new Date());
        startSprint(projectId, sprintId1, sprintDetailVO.getObjectVersionNumber(), workDays.get(11), workDays.get(2));

        // 更新fix版本时间
        versionIssueRelMapper.updateDemoVersionIssueTime(projectId, workDays.get(12));

        // 完成冲刺1
        SprintCompleteVO sprintCompleteVO = new SprintCompleteVO();
        sprintCompleteVO.setProjectId(projectId);
        sprintCompleteVO.setSprintId(sprintId1);
        sprintCompleteVO.setIncompleteIssuesDestination(0L);
        sprintService.completeSprint(projectId, sprintCompleteVO);


        // 更新经办人
        issueMapper.updateAssigneeIdBySpecify(projectId, story1.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, story2.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, story3.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, story4.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, story5.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, task1.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, task2.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, task3.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, bug1.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask8.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask7.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask6.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask5.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask4.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask3.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask2.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask1.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask9.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask10.getIssueId(), userId1, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask11.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask12.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask13.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask14.getIssueId(), userId2, workDays.get(12), workDays.get(12));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask15.getIssueId(), userId2, workDays.get(12), workDays.get(12));

        issueMapper.updateTestIssue(projectId, test1.getIssueId(), userId2, workDays.get(2), workDays.get(2));
        issueMapper.updateTestIssue(projectId, test2.getIssueId(), userId2, workDays.get(2), workDays.get(2));
        issueMapper.updateTestIssue(projectId, test3.getIssueId(), userId2, workDays.get(2), workDays.get(2));
        issueMapper.updateTestIssue(projectId, test4.getIssueId(), userId2, workDays.get(2), workDays.get(2));

        issueMapper.updateAssigneeIdBySpecify(projectId, story6.getIssueId(), userId1, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, story7.getIssueId(), userId2, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, story8.getIssueId(), userId1, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask16.getIssueId(), userId2, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask17.getIssueId(), userId1, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask18.getIssueId(), userId2, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask19.getIssueId(), userId1, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask20.getIssueId(), userId2, workDays.get(3), workDays.get(3));
        issueMapper.updateAssigneeIdBySpecify(projectId, subtask21.getIssueId(), userId1, workDays.get(3), workDays.get(3));

        // 更新项目创建时间
        updateProjectAndIssues(projectId, workDays.get(13), workDays.get(12));

        // 更新日志
        dataLogMapper.updateExpStatusRtDataLog(projectId, task1.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, task1.getIssueId(), workDays.get(11), workDays.get(11), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, task1.getIssueId(), workDays.get(9), workDays.get(9), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, task3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, task3.getIssueId(), workDays.get(10), workDays.get(10), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, task3.getIssueId(), workDays.get(6), workDays.get(6), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask12.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask12.getIssueId(), workDays.get(11), workDays.get(11), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask12.getIssueId(), workDays.get(9), workDays.get(9), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask13.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask13.getIssueId(), workDays.get(7), workDays.get(7), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask13.getIssueId(), workDays.get(6), workDays.get(6), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask14.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask14.getIssueId(), workDays.get(8), workDays.get(8), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask14.getIssueId(), workDays.get(10), workDays.get(10), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask15.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask15.getIssueId(), workDays.get(6), workDays.get(6), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask15.getIssueId(), workDays.get(4), workDays.get(4), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask1.getIssueId(), workDays.get(11), workDays.get(11), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask1.getIssueId(), workDays.get(10), workDays.get(10), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask2.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask2.getIssueId(), workDays.get(11), workDays.get(11), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask2.getIssueId(), workDays.get(10), workDays.get(10), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, story1.getIssueId(), workDays.get(10), workDays.get(10), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, story1.getIssueId(), workDays.get(10), workDays.get(10), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask3.getIssueId(), workDays.get(9), workDays.get(9), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask3.getIssueId(), workDays.get(8), workDays.get(8), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask4.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask4.getIssueId(), workDays.get(10), workDays.get(10), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask4.getIssueId(), workDays.get(9), workDays.get(9), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story2.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, story2.getIssueId(), workDays.get(9), workDays.get(9), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, story2.getIssueId(), workDays.get(8), workDays.get(8), userId1);


        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask5.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask5.getIssueId(), workDays.get(9), workDays.get(9), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask5.getIssueId(), workDays.get(8), workDays.get(8), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask6.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask6.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask6.getIssueId(), workDays.get(9), workDays.get(9), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, story3.getIssueId(), workDays.get(8), workDays.get(8), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, story3.getIssueId(), workDays.get(7), workDays.get(7), userId1);


        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask7.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask7.getIssueId(), workDays.get(4), workDays.get(4), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask7.getIssueId(), workDays.get(3), workDays.get(3), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask8.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask8.getIssueId(), workDays.get(4), workDays.get(4), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask8.getIssueId(), workDays.get(3), workDays.get(3), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story4.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, story4.getIssueId(), workDays.get(4), workDays.get(4), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, story4.getIssueId(), workDays.get(3), workDays.get(3), userId2);


        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask9.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask9.getIssueId(), workDays.get(4), workDays.get(4), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask9.getIssueId(), workDays.get(3), workDays.get(3), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask10.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask10.getIssueId(), workDays.get(7), workDays.get(7), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask10.getIssueId(), workDays.get(6), workDays.get(6), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask11.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask11.getIssueId(), workDays.get(6), workDays.get(6), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask11.getIssueId(), workDays.get(5), workDays.get(5), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story5.getIssueId(), workDays.get(12), workDays.get(12), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, story5.getIssueId(), workDays.get(3), workDays.get(3), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, story5.getIssueId(), workDays.get(2), workDays.get(2), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, bug1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, bug1.getIssueId(), workDays.get(3), workDays.get(3), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, bug1.getIssueId(), workDays.get(2), workDays.get(2), userId1);


        List<Date> dateAfters = getWorkDaysAfter(workDays.get(1));


        // 更新冲刺2的日志
        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask16.getIssueId(), workDays.get(2), workDays.get(2), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask16.getIssueId(), dateAfters.get(0), dateAfters.get(0), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask16.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask17.getIssueId(), workDays.get(2), workDays.get(2), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask17.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId2);
        dataLogMapper.updateStatusRtDataLog(projectId, subtask17.getIssueId(), dateAfters.get(2), dateAfters.get(2), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, task2.getIssueId(), workDays.get(2), workDays.get(2), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, task2.getIssueId(), dateAfters.get(0), dateAfters.get(2), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, task2.getIssueId(), dateAfters.get(2), dateAfters.get(2), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask21.getIssueId(), workDays.get(2), workDays.get(2), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask21.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask18.getIssueId(), workDays.get(2), workDays.get(2), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask18.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask19.getIssueId(), workDays.get(2), workDays.get(2), userId2);
        dataLogMapper.updateStatusDingDataLog(projectId, subtask19.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story6.getIssueId(), workDays.get(2), workDays.get(2), userId1);
        dataLogMapper.updateStatusDingDataLog(projectId, story6.getIssueId(), dateAfters.get(1), dateAfters.get(1), userId1);
        dataLogMapper.updateStatusRtDataLog(projectId, story6.getIssueId(), dateAfters.get(2), dateAfters.get(2), userId1);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story7.getIssueId(), workDays.get(2), workDays.get(2), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, story8.getIssueId(), workDays.get(2), workDays.get(2), userId2);

        dataLogMapper.updateExpStatusRtDataLog(projectId, subtask20.getIssueId(), workDays.get(2), workDays.get(2), userId1);


        // 更新其它日志
        dataLogMapper.updateDemoEpicDataLog(projectId, epic1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, epic2.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, epic3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, epic4.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, epic5.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story2.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story4.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story5.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story6.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story7.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story8.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story9.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story10.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story11.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, story12.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, task1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, task2.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, task3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask1.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask2.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask3.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask4.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask5.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask6.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask7.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask8.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask9.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask10.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask11.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask12.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask13.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask14.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask15.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask16.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask17.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask18.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask19.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask20.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, subtask21.getIssueId(), workDays.get(12), workDays.get(12), userId1);
        dataLogMapper.updateDemoEpicDataLog(projectId, bug1.getIssueId(), workDays.get(12), workDays.get(12), userId1);

        issueMapper.updateDemoCreaterBySpecify(projectId, epic1.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, epic2.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, epic3.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, epic4.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, epic5.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story1.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story2.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story3.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story4.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story5.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story6.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story7.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story8.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story9.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story10.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story11.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, story12.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, task1.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, task2.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, task3.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask1.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask2.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask3.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask4.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask5.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask6.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask7.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask8.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask9.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask10.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask11.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask12.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask13.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask14.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask15.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask16.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask17.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask18.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask19.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask20.getIssueId(), userId1);
        issueMapper.updateDemoCreaterBySpecify(projectId, subtask21.getIssueId(), userId1);



        // 开启冲刺2
        startSprint(projectId, sprintId2, sprintDetailVO2.getObjectVersionNumber(), dateAfters.get(0), dateAfters.get(9));

        // 发送saga到测试
        OrganizationRegisterEventPayload.TestData testData = new OrganizationRegisterEventPayload.TestData();
        List<Long> testIssueIds = new ArrayList<>();
        testIssueIds.add(bug1.getIssueId());
        testIssueIds.add(test1.getIssueId());
        testIssueIds.add(test2.getIssueId());
        testIssueIds.add(test3.getIssueId());
        testIssueIds.add(test4.getIssueId());
        testData.setTestIssueIds(testIssueIds);
        testData.setVersionId(productVersionDetailVO.getVersionId());
        testData.setDateOne(workDays.get(6));
        testData.setDateTwo(workDays.get(4));
        testData.setDateThree(workDays.get(2));
        testData.setDateFour(dateAfters.get(0));
        testData.setDateFive(dateAfters.get(2));
        testData.setDateSix(dateAfters.get(4));
        demoProjectPayload.setTestData(testData);
        return demoProjectPayload;
    }

}