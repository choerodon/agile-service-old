package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class PriorityDistributeVO {

    @ApiModelProperty(value = "完成的问题总和")
    private Integer completedNum;

    @ApiModelProperty(value = "总问题数")
    private Integer totalNum;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityDTO priorityDTO;

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

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }
}
