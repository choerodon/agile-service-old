package io.choerodon.agile.configure;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.NoticeSendDTO;
import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Mockito.when(instanceFeignClient.startInstance(Matchers.anyLong(),Matchers.anyString(), Matchers.anyLong(),Matchers.any(InputDTO.class)))
                .thenReturn(new ResponseEntity<>(executeResult, HttpStatus.OK));
        return instanceFeignClient;
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
        epic.setTypeCode("epic");
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
        return issueFeignClient;
    }

    @Bean
    @Primary
    NotifyFeignClient notifyFeignClient() {
        NotifyFeignClient notifyFeignClient = Mockito.mock(NotifyFeignClientFallback.class);
        return notifyFeignClient;
    }
}
