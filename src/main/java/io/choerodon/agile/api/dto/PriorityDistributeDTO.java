package io.choerodon.agile.api.dto;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class PriorityDistributeDTO {

    public PriorityDistributeDTO(String priorityCode, Integer completedNum, Integer totalNum) {
        this.priorityCode = priorityCode;
        this.completedNum = completedNum;
        this.totalNum = totalNum;
    }

    private String priorityCode;

    private Integer completedNum;

    private Integer totalNum;

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public Integer getCompletedNum() {
        return completedNum;
    }

    public void setCompletedNum(Integer completedNum) {
        this.completedNum = completedNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
