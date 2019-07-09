package io.choerodon.agile.api.vo;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.io.Serializable;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/26
 */
public class PieChartVO implements Serializable {

    private String name;

    private String loginName;

    private String realName;

    private String typeName;

    private Integer value;

    private Double percent;

    private JSONObject jsonObject;

    private PriorityDTO priorityDTO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
