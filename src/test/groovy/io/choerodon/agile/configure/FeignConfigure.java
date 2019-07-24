package io.choerodon.agile.configure;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.impl.IssueServiceImpl;
import io.choerodon.agile.app.service.impl.ProductVersionServiceImpl;
import io.choerodon.agile.infra.feign.*;
import io.choerodon.agile.infra.feign.fallback.NotifyFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.IamFeignClientFallback;
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
    IamFeignClient iamFeignClient() {
        IamFeignClient iamFeignClient = Mockito.mock(IamFeignClientFallback.class);
        ProjectVO projectVO = new ProjectVO();
        projectVO.setId(1L);
        projectVO.setName("test");
        projectVO.setOrganizationId(1L);
        Mockito.when(iamFeignClient.queryProject(Matchers.anyLong())).thenReturn(new ResponseEntity<>(projectVO, HttpStatus.OK));
        List<ProjectRelationshipVO> rel = new ArrayList<>(2);
        ProjectRelationshipVO rel1 = new ProjectRelationshipVO();
        rel1.setProjectId(2L);
        rel1.setUserCount(2);
        rel1.setParentId(1L);
        rel1.setEnabled(true);
        rel1.setProgramId(1L);
        rel1.setProjCode("test1");
        rel1.setProjName("敏捷项目1");
        ProjectRelationshipVO rel2 = new ProjectRelationshipVO();
        rel2.setProjectId(3L);
        rel2.setUserCount(2);
        rel2.setParentId(1L);
        rel2.setEnabled(true);
        rel2.setProgramId(1L);
        rel2.setProjCode("test2");
        rel2.setProjName("敏捷项目2");
        rel.add(rel1);
        rel.add(rel2);
        Mockito.when(iamFeignClient.getProjUnderGroup(Matchers.anyLong(), Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(new ResponseEntity<>(rel, HttpStatus.OK));
        return iamFeignClient;
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
}
