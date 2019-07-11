package io.choerodon.agile.api.vo;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/10
 */
public class BurnDownReportVO {

    @ApiModelProperty(value = "冲刺列表")
    private List<SprintBurnDownReportVO> sprintBurnDownReportVOS;

    @ApiModelProperty(value = "未完成的问题列表")
    private List<IssueBurnDownReportDTO> incompleteIssues;

    @ApiModelProperty(value = "史诗或版本的字段信息")
    private JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<SprintBurnDownReportVO> getSprintBurnDownReportVOS() {
        return sprintBurnDownReportVOS;
    }

    public void setSprintBurnDownReportVOS(List<SprintBurnDownReportVO> sprintBurnDownReportVOS) {
        this.sprintBurnDownReportVOS = sprintBurnDownReportVOS;
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
