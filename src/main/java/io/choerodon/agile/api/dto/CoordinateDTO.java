package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CoordinateDTO {

    private Date date;

    private Integer issueCount;

    private ColumnChangeDTO columnChangeDTO;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public ColumnChangeDTO getColumnChangeDTO() {
        return columnChangeDTO;
    }

    public void setColumnChangeDTO(ColumnChangeDTO columnChangeDTO) {
        this.columnChangeDTO = columnChangeDTO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
