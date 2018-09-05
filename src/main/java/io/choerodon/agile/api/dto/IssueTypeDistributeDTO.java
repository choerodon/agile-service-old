package io.choerodon.agile.api.dto;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:12 2018/9/4
 * Description:
 */
public class IssueTypeDistributeDTO {
    private String typeCode;

    private Integer issueNum;

    private Double percent;

    private String categoryCode;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
