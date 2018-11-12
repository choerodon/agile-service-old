package io.choerodon.agile.configure;

import io.choerodon.agile.infra.config.FeignConfig;
import org.springframework.context.annotation.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/9
 */
@Configuration
@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = FeignConfig.class)})
public class FeignConfigure {

}
