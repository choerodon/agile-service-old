package io.choerodon.agile.infra.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@ConfigurationProperties("workCalendarHoliday")
public class WorkCalendarHolidayProperties {

    private Boolean enabled = false;

    private String type = "";

    private String apiKey = "";

    private String corn = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }
}