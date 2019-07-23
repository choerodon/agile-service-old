package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CumulativeFlowFilterVO {

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "看板id")
    private Long boardId;

    @ApiModelProperty(value = "快速搜索传值")
    private List<Long> quickFilterIds;

    @ApiModelProperty(value = "看板列id集合")
    private List<Long> columnIds;

    public List<Long> getQuickFilterIds() {
        return quickFilterIds;
    }

    public void setQuickFilterIds(List<Long> quickFilterIds) {
        this.quickFilterIds = quickFilterIds;
    }

    public List<Long> getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(List<Long> columnIds) {
        this.columnIds = columnIds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
