package io.choerodon.agile.infra.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/12
 */
@Configuration
@EnableFeignClients("io.choerodon")
public class FeignConfig {
}
