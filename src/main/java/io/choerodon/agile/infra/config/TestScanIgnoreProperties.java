package io.choerodon.agile.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/12
 */
@ConfigurationProperties("testScanIgnore")
public class TestScanIgnoreProperties {

    private Boolean enabled = false;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
