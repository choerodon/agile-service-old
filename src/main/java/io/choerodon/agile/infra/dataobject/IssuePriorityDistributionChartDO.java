package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/17
 */
public class IssuePriorityDistributionChartDO {

    private Long priorityId;

    private Integer totalCount;

    private Integer doneCount;

    private PriorityVO priorityVO;

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
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
