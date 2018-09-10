package io.choerodon.agile.api.dto;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/10
 */
public class BurnDownReportDTO {

    private List<SprintBurnDownReportDTO> sprintBurnDownReportDTOS;

    private List<IssueBurnDownReportDTO> incompleteIssues;

    private JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<SprintBurnDownReportDTO> getSprintBurnDownReportDTOS() {
        return sprintBurnDownReportDTOS;
    }

    public void setSprintBurnDownReportDTOS(List<SprintBurnDownReportDTO> sprintBurnDownReportDTOS) {
        this.sprintBurnDownReportDTOS = sprintBurnDownReportDTOS;
    }

    public List<IssueBurnDownReportDTO> getIncompleteIssues() {
        return incompleteIssues;
    }

    public void setIncompleteIssues(List<IssueBurnDownReportDTO> incompleteIssues) {
        this.incompleteIssues = incompleteIssues;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
