package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/17
 */
public class IssuePriorityDistributionChartDTO {

    private String name;

    private Integer totalCount;

    private Integer doneCount;

    private PriorityDTO priorityDTO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(Integer doneCount) {
        this.doneCount = doneCount;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
