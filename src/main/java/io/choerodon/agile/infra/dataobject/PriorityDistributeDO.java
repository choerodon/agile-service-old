package io.choerodon.agile.infra.dataobject;


import io.choerodon.agile.api.dto.PriorityDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class PriorityDistributeDO {

    private Long issueId;

//    private String priorityCode;

    private String categoryCode;

    private Long priorityId;

    private PriorityDTO priorityDTO;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

//    public String getPriorityCode() {
//        return priorityCode;
//    }
//
//    public void setPriorityCode(String priorityCode) {
//        this.priorityCode = priorityCode;
//    }


    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
