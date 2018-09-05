package io.choerodon.agile.infra.dataobject;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  15:00 2018/9/5
 * Description:
 */
public class IssueStatus {
    private Integer issueNum;
    private String categoryCode;

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
