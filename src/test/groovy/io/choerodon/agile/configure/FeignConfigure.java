package io.choerodon.agile.configure;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
import io.choerodon.agile.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import io.choerodon.statemachine.feign.InstanceFeignClientFallback;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        projectDTO.setOrganizationId(1L);
        Mockito.when(userFeignClient.queryProject(1L)).thenReturn(new ResponseEntity<>(projectDTO, HttpStatus.OK));
        return userFeignClient;
    }

    @Bean
    @Primary
    InstanceFeignClient instanceFeignClient() {
        return Mockito.mock(InstanceFeignClientFallback.class);
    }

    @Bean
    @Primary
    IssueFeignClient issueFeignClient() {
        IssueFeignClient issueFeignClient = Mockito.mock(IssueFeignClientFallback.class);
        Mockito.when(issueFeignClient.queryStateMachineId(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        return issueFeignClient;
    }
}
