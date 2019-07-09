package io.choerodon.agile.configure;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.impl.IssueServiceImpl;
import io.choerodon.agile.app.service.impl.ProductVersionServiceImpl;
import io.choerodon.agile.infra.feign.*;
import io.choerodon.agile.infra.feign.fallback.FoundationFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.NotifyFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.StateMachineTransformDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import io.choerodon.statemachine.feign.InstanceFeignClientFallback;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/12
 */
@Configuration
public class FeignConfigure {
    @Bean
    @Primary
    UserFeignClient userFeignClient() {
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClientFallback.class);
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        projectDTO.setName("test");
        projectDTO.setOrganizationId(1L);
        Mockito.when(userFeignClient.queryProject(Matchers.anyLong())).thenReturn(new ResponseEntity<>(projectDTO, HttpStatus.OK));
        List<ProjectRelationshipDTO> rel = new ArrayList<>(2);
        ProjectRelationshipDTO rel1 = new ProjectRelationshipDTO();
        rel1.setProjectId(2L);
        rel1.setUserCount(2);
        rel1.setParentId(1L);
        rel1.setEnabled(true);
        rel1.setProgramId(1L);
        rel1.setProjCode("test1");
        rel1.setProjName("敏捷项目1");
        ProjectRelationshipDTO rel2 = new ProjectRelationshipDTO();
        rel2.setProjectId(3L);
        rel2.setUserCount(2);
        rel2.setParentId(1L);
        rel2.setEnabled(true);
        rel2.setProgramId(1L);
        rel2.setProjCode("test2");
        rel2.setProjName("敏捷项目2");
        rel.add(rel1);
        rel.add(rel2);
        Mockito.when(userFeignClient.getProjUnderGroup(Matchers.anyLong(), Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(new ResponseEntity<>(rel, HttpStatus.OK));
        return userFeignClient;
    }

    @Bean
    @Primary
    InstanceFeignClient instanceFeignClient() {
        InstanceFeignClient instanceFeignClient = Mockito.mock(InstanceFeignClientFallback.class);
        Mockito.when(instanceFeignClient.queryInitStatusId(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setSuccess(true);
        Mockito.when(instanceFeignClient.startInstance(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong(), Matchers.any(InputDTO.class)))
                .thenReturn(new ResponseEntity<>(executeResult, HttpStatus.OK));
        Mockito.when(instanceFeignClient.executeTransform(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong(), Matchers.any(InputDTO.class)))
                .thenReturn(new ResponseEntity<>(executeResult, HttpStatus.OK));
        StateMachineTransformDTO stateMachineTransformDTO = new StateMachineTransformDTO();
        Mockito.when(instanceFeignClient.queryInitTransform(Matchers.anyLong(), Matchers.anyLong()))
                .thenReturn(new ResponseEntity<>(stateMachineTransformDTO, HttpStatus.OK));
        return instanceFeignClient;
    }

    @Bean
    @Primary
    StateMachineFeignClient stateMachineFeignClient() {
        StateMachineFeignClient stateMachineFeignClient = Mockito.mock(StateMachineFeignClient.class);
        Map<Long, StatusMapVO> statusMapDTOMap = new HashMap<>(3);
        StatusMapVO todoStatus = new StatusMapVO();
        todoStatus.setId(1L);
        todoStatus.setName("待处理");
        todoStatus.setDescription("待处理");
        todoStatus.setOrganizationId(1L);
        todoStatus.setType("todo");
        todoStatus.setCode("todo");
        StatusMapVO doingStatus = new StatusMapVO();
        doingStatus.setId(2L);
        doingStatus.setName("处理中");
        doingStatus.setDescription("处理中");
        doingStatus.setOrganizationId(1L);
        doingStatus.setType("doing");
        doingStatus.setCode("doing");
        StatusMapVO doneStatus = new StatusMapVO();
        doneStatus.setId(3L);
        doneStatus.setName("完成");
        doneStatus.setDescription("完成");
        doneStatus.setOrganizationId(1L);
        doneStatus.setType("done");
        doneStatus.setCode("done");
        statusMapDTOMap.put(1L, todoStatus);
        statusMapDTOMap.put(2L, doingStatus);
        statusMapDTOMap.put(3L, doneStatus);
        Mockito.when(stateMachineFeignClient.queryAllStatusMap(Matchers.anyLong())).thenReturn(new ResponseEntity<>(statusMapDTOMap, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryStatusById(Matchers.anyLong(), Matchers.eq(1L))).thenReturn(new ResponseEntity<>(todoStatus, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryStatusById(Matchers.anyLong(), Matchers.eq(2L))).thenReturn(new ResponseEntity<>(doingStatus, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryStatusById(Matchers.anyLong(), Matchers.eq(3L))).thenReturn(new ResponseEntity<>(doneStatus, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryInitStatusIds(Matchers.anyLong(), Matchers.anyListOf(Long.class)))
                .thenReturn(new ResponseEntity<>(statusMapDTOMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey)), HttpStatus.OK));
        Map<Long, Status> statusMap = new HashMap<>();
        Status status1 = new Status();
        status1.setId(1L);
        status1.setName("待处理");
        status1.setOrganizationId(1L);
        status1.setType("todo");
        Status status2 = new Status();
        status2.setId(2L);
        status2.setName("处理中");
        status2.setOrganizationId(1L);
        status2.setType("doing");
        Status status3 = new Status();
        status3.setId(3L);
        status3.setName("已完成");
        status3.setOrganizationId(1L);
        status3.setType("done");
        statusMap.put(1L, status1);
        statusMap.put(2L, status2);
        statusMap.put(3L, status3);
        Mockito.when(stateMachineFeignClient.batchStatusGet(Matchers.any(List.class))).thenReturn(new ResponseEntity(statusMap, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryStatusById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(todoStatus, HttpStatus.OK));
        return stateMachineFeignClient;
    }

    @Bean
    @Primary
    IssueFeignClient issueFeignClient() {
        IssueFeignClient issueFeignClient = Mockito.mock(IssueFeignClientFallback.class);
        List<IssueTypeVO> issueTypeVOS = new ArrayList<>(3);
        IssueTypeVO story = new IssueTypeVO();
        story.setIcon("icon");
        story.setId(1L);
        story.setName("XX");
        story.setOrganizationId(1L);
        story.setTypeCode("story");
        issueTypeVOS.add(story);
        IssueTypeVO task = new IssueTypeVO();
        task.setIcon("icon");
        task.setId(2L);
        task.setName("XX");
        task.setOrganizationId(1L);
        task.setTypeCode("task");
        issueTypeVOS.add(task);
        IssueTypeVO bug = new IssueTypeVO();
        bug.setIcon("icon");
        bug.setId(3L);
        bug.setName("XX");
        bug.setOrganizationId(1L);
        bug.setTypeCode("bug");
        issueTypeVOS.add(bug);
        IssueTypeVO epic = new IssueTypeVO();
        epic.setIcon("icon");
        epic.setId(4L);
        epic.setName("XX");
        epic.setOrganizationId(1L);
        epic.setTypeCode("issue_epic");
        issueTypeVOS.add(epic);
        IssueTypeVO subTask = new IssueTypeVO();
        subTask.setIcon("icon");
        subTask.setId(5L);
        subTask.setName("XX");
        subTask.setOrganizationId(1L);
        subTask.setTypeCode("sub_task");
        issueTypeVOS.add(subTask);
        IssueTypeVO issueTest = new IssueTypeVO();
        issueTest.setIcon("icon");
        issueTest.setId(6L);
        issueTest.setName("XX");
        issueTest.setOrganizationId(1L);
        issueTest.setTypeCode("issue_test");
        issueTypeVOS.add(issueTest);
        Mockito.when(issueFeignClient.queryStateMachineId(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypesByProjectId(Matchers.anyLong(), Matchers.anyString())).thenReturn(new ResponseEntity<>(issueTypeVOS, HttpStatus.OK));
        Map<Long, PriorityDTO> priorityDTOMap = new HashMap<>(3);
        PriorityDTO low = new PriorityDTO();
        low.setId(1L);
        low.setName("高");
        low.setOrganizationId(1L);
        low.setColour("#00000");
        low.setDefault(true);
        PriorityDTO high = new PriorityDTO();
        high.setId(2L);
        high.setName("中");
        high.setOrganizationId(1L);
        high.setColour("#00000");
        high.setDefault(true);
        PriorityDTO middle = new PriorityDTO();
        middle.setId(3L);
        middle.setName("低");
        middle.setOrganizationId(1L);
        middle.setColour("#00000");
        middle.setDefault(true);
        priorityDTOMap.put(1L, low);
        priorityDTOMap.put(2L, high);
        priorityDTOMap.put(3L, middle);
        List<PriorityDTO> list = new ArrayList<>();
        list.add(low);
        list.add(high);
        list.add(middle);
        Mockito.when(issueFeignClient.queryByOrganizationIdList(Matchers.anyLong())).thenReturn(new ResponseEntity<>(list, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryByOrganizationId(Matchers.anyLong())).thenReturn(new ResponseEntity<>(priorityDTOMap, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(low, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(1L))).thenReturn(new ResponseEntity<>(story, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(2L))).thenReturn(new ResponseEntity<>(task, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(3L))).thenReturn(new ResponseEntity<>(bug, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(4L))).thenReturn(new ResponseEntity<>(epic, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(5L))).thenReturn(new ResponseEntity<>(subTask, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(6L))).thenReturn(new ResponseEntity<>(issueTest, HttpStatus.OK));
        Mockito.when(issueFeignClient.listIssueTypeMap(Matchers.anyLong())).thenReturn(new ResponseEntity<>(issueTypeVOS.stream().collect(Collectors.toMap(IssueTypeVO::getId,
                Function.identity())), HttpStatus.OK));
        List<IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIdDTOS = new ArrayList<>();
        issueTypeVOS.forEach(issueTypeDTO -> {
            IssueTypeWithStateMachineIdDTO issueTypeWithStateMachineIdDTO = new IssueTypeWithStateMachineIdDTO();
            BeanUtils.copyProperties(issueTypeDTO, issueTypeWithStateMachineIdDTO);
            issueTypeWithStateMachineIdDTO.setInitStatusId(1L);
            issueTypeWithStateMachineIdDTO.setStateMachineId(1L);
            issueTypeWithStateMachineIdDTOS.add(issueTypeWithStateMachineIdDTO);
        });
        Mockito.when(issueFeignClient.queryIssueTypesWithStateMachineIdByProjectId(Matchers.anyLong(), Matchers.anyString())).thenReturn(new ResponseEntity<>(issueTypeWithStateMachineIdDTOS, HttpStatus.OK));
        StatusInfoVO statusInfoVO = new StatusInfoVO();
        statusInfoVO.setId(10000L);
        statusInfoVO.setName("statusName");
        Mockito.when(issueFeignClient.createStatusForAgile(Matchers.anyLong(), Matchers.anyString(), Matchers.any(StatusInfoVO.class))).thenReturn(new ResponseEntity<>(statusInfoVO, HttpStatus.OK));
        List<StatusMapVO> statusMapVOList = new ArrayList<>();
        StatusMapVO statusMapVO1 = new StatusMapVO();
        statusMapVO1.setOrganizationId(1L);
        statusMapVO1.setId(1L);
        statusMapVO1.setType("todo");
        statusMapVO1.setName("待处理");
        StatusMapVO statusMapVO2 = new StatusMapVO();
        statusMapVO2.setOrganizationId(1L);
        statusMapVO2.setId(2L);
        statusMapVO2.setType("doing");
        statusMapVO2.setName("处理中");
        StatusMapVO statusMapVO3 = new StatusMapVO();
        statusMapVO3.setOrganizationId(1L);
        statusMapVO3.setId(3L);
        statusMapVO3.setType("done");
        statusMapVO3.setName("已完成");
        statusMapVOList.add(statusMapVO1);
        statusMapVOList.add(statusMapVO2);
        statusMapVOList.add(statusMapVO3);
        Mockito.when(issueFeignClient.queryStatusByProjectId(Mockito.anyLong(), Mockito.anyString())).thenReturn(new ResponseEntity<>(statusMapVOList, HttpStatus.OK));
        return issueFeignClient;
    }

    @Bean
    @Primary
    NotifyFeignClient notifyFeignClient() {
        NotifyFeignClient notifyFeignClient = Mockito.mock(NotifyFeignClientFallback.class);
        return notifyFeignClient;
    }

    @Bean
    SagaClient sagaClient() {
        SagaClient sagaClient = Mockito.mock(SagaClient.class);
        Mockito.when(sagaClient.startSaga(Matchers.anyString(), Matchers.any(StartInstanceDTO.class))).thenReturn(new SagaInstanceDTO());
        IssueServiceImpl issueService = ApplicationContextHelper.getSpringFactory().getBean(IssueServiceImpl.class);
        ProductVersionServiceImpl productVersionService = ApplicationContextHelper.getSpringFactory().getBean(ProductVersionServiceImpl.class);
        issueService.setSagaClient(sagaClient);
        productVersionService.setSagaClient(sagaClient);
        return sagaClient;
    }

    @Bean
    @Primary
    FoundationFeignClient foundationFeignClient() {
        FoundationFeignClient foundationFeignClient = Mockito.mock(FoundationFeignClientFallback.class);
        Mockito.when(foundationFeignClient.queryDataLogByInstanceId(Matchers.anyLong(), Matchers.anyLong(), Matchers.anyString())).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));
        return foundationFeignClient;
    }

}
