package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusCategoryDTO {

    @ApiModelProperty(value = "状态类别")
    private String categoryCode;

    @ApiModelProperty(value = "该状态类别下的问题数量")
    private Integer issueNum;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }
}
