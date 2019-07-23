package io.choerodon.agile.infra.config;

import feign.Contract;
import org.springframework.context.annotation.Bean;

/**
 * @author shinan.chen
 * @date 2018/9/27
 */
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

}