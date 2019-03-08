package io.choerodon.agile.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/8.
 * Email: fuqianghuang01@gmail.com
 */
@Configuration
public class SecurityContextHolderConfig {
    public SecurityContextHolderConfig() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
