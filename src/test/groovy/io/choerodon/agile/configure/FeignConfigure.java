package io.choerodon.agile.configure;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.NotifyFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
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

import java.util.*;
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
        Mockito.when(userFeignClient.queryProject(1L)).thenReturn(new ResponseEntity<>(projectDTO, HttpStatus.OK));
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
        return instanceFeignClient;
    }

    @Bean
    @Primary
    StateMachineFeignClient stateMachineFeignClient() {
        StateMachineFeignClient stateMachineFeignClient = Mockito.mock(StateMachineFeignClient.class);
        Map<Long, StatusMapDTO> statusMapDTOMap = new HashMap<>(3);
        StatusMapDTO todoStatus = new StatusMapDTO();
        todoStatus.setId(1L);
        todoStatus.setName("待办");
        todoStatus.setDescription("待办");
        todoStatus.setOrganizationId(1L);
        todoStatus.setType("todo");
        todoStatus.setCode("todo");
        StatusMapDTO doingStatus = new StatusMapDTO();
        doingStatus.setId(2L);
        doingStatus.setName("处理中");
        doingStatus.setDescription("处理中");
        doingStatus.setOrganizationId(1L);
        doingStatus.setType("doing");
        doingStatus.setCode("doing");
        StatusMapDTO doneStatus = new StatusMapDTO();
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
        return stateMachineFeignClient;
    }

    @Bean
    @Primary
    IssueFeignClient issueFeignClient() {
        IssueFeignClient issueFeignClient = Mockito.mock(IssueFeignClientFallback.class);
        List<IssueTypeDTO> issueTypeDTOS = new ArrayList<>(3);
        IssueTypeDTO story = new IssueTypeDTO();
        story.setIcon("icon");
        story.setId(1L);
        story.setName("XX");
        story.setOrganizationId(1L);
        story.setTypeCode("story");
        issueTypeDTOS.add(story);
        IssueTypeDTO task = new IssueTypeDTO();
        task.setIcon("icon");
        task.setId(2L);
        task.setName("XX");
        task.setOrganizationId(1L);
        task.setTypeCode("task");
        issueTypeDTOS.add(task);
        IssueTypeDTO bug = new IssueTypeDTO();
        bug.setIcon("icon");
        bug.setId(3L);
        bug.setName("XX");
        bug.setOrganizationId(1L);
        bug.setTypeCode("bug");
        issueTypeDTOS.add(bug);
        IssueTypeDTO epic = new IssueTypeDTO();
        epic.setIcon("icon");
        epic.setId(4L);
        epic.setName("XX");
        epic.setOrganizationId(1L);
        epic.setTypeCode("issue_epic");
        issueTypeDTOS.add(epic);
        IssueTypeDTO subTask = new IssueTypeDTO();
        subTask.setIcon("icon");
        subTask.setId(5L);
        subTask.setName("XX");
        subTask.setOrganizationId(1L);
        subTask.setTypeCode("sub_task");
        issueTypeDTOS.add(subTask);
        IssueTypeDTO issueTest = new IssueTypeDTO();
        issueTest.setIcon("icon");
        issueTest.setId(6L);
        issueTest.setName("XX");
        issueTest.setOrganizationId(1L);
        issueTest.setTypeCode("issue_test");
        issueTypeDTOS.add(issueTest);
        Mockito.when(issueFeignClient.queryStateMachineId(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypesByProjectId(Matchers.anyLong(), Matchers.anyString())).thenReturn(new ResponseEntity<>(issueTypeDTOS, HttpStatus.OK));
        Map<Long, PriorityDTO> priorityDTOMap = new HashMap<>(3);
        PriorityDTO low = new PriorityDTO();
        low.setId(1L);
        low.setName("低");
        low.setOrganizationId(1L);
        low.setColour("#00000");
        low.setDefault(true);
        PriorityDTO high = new PriorityDTO();
        high.setId(2L);
        high.setName("高");
        high.setOrganizationId(1L);
        high.setColour("#00000");
        high.setDefault(true);
        PriorityDTO middle = new PriorityDTO();
        middle.setId(3L);
        middle.setName("中");
        middle.setOrganizationId(1L);
        middle.setColour("#00000");
        middle.setDefault(true);
        priorityDTOMap.put(1L, low);
        priorityDTOMap.put(2L, high);
        priorityDTOMap.put(3L, middle);
        Mockito.when(issueFeignClient.queryByOrganizationId(Matchers.anyLong())).thenReturn(new ResponseEntity<>(priorityDTOMap, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(low, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(1L))).thenReturn(new ResponseEntity<>(story, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(2L))).thenReturn(new ResponseEntity<>(task, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(3L))).thenReturn(new ResponseEntity<>(bug, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(4L))).thenReturn(new ResponseEntity<>(epic, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(5L))).thenReturn(new ResponseEntity<>(subTask, HttpStatus.OK));
        Mockito.when(issueFeignClient.queryIssueTypeById(Matchers.anyLong(), Matchers.eq(6L))).thenReturn(new ResponseEntity<>(issueTest, HttpStatus.OK));
        Mockito.when(issueFeignClient.listIssueTypeMap(Matchers.anyLong())).thenReturn(new ResponseEntity<>(issueTypeDTOS.stream().collect(Collectors.toMap(IssueTypeDTO::getId,
                Function.identity())), HttpStatus.OK));
        List<IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIdDTOS = new ArrayList<>();
        issueTypeDTOS.forEach(issueTypeDTO -> {
            IssueTypeWithStateMachineIdDTO issueTypeWithStateMachineIdDTO = new IssueTypeWithStateMachineIdDTO();
            BeanUtils.copyProperties(issueTypeDTO, issueTypeWithStateMachineIdDTO);
            issueTypeWithStateMachineIdDTO.setInitStatusId(1L);
            issueTypeWithStateMachineIdDTO.setStateMachineId(1L);
            issueTypeWithStateMachineIdDTOS.add(issueTypeWithStateMachineIdDTO);
        });
        Mockito.when(issueFeignClient.queryIssueTypesWithStateMachineIdByProjectId(Matchers.anyLong(), Matchers.anyString())).thenReturn(new ResponseEntity<>(issueTypeWithStateMachineIdDTOS, HttpStatus.OK));
        return issueFeignClient;
    }

    @Bean
    @Primary
    NotifyFeignClient notifyFeignClient() {
        NotifyFeignClient notifyFeignClient = Mockito.mock(NotifyFeignClientFallback.class);
        return notifyFeignClient;
    }
}
