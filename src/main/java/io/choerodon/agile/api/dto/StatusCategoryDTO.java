package io.choerodon.agile.api.dto;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusCategoryDTO {

    private String categoryCode;

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
