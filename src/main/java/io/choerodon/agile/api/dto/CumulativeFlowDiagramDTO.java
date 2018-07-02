package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CumulativeFlowDiagramDTO {

    private List<ColumnChangeDTO> columnChangeDTOList;

    private List<ColumnDTO> columnDTOList;

    public List<ColumnChangeDTO> getColumnChangeDTOList() {
        return columnChangeDTOList;
    }

    public void setColumnChangeDTOList(List<ColumnChangeDTO> columnChangeDTOList) {
        this.columnChangeDTOList = columnChangeDTOList;
    }

    public List<ColumnDTO> getColumnDTOList() {
        return columnDTOList;
    }

    public void setColumnDTOList(List<ColumnDTO> columnDTOList) {
        this.columnDTOList = columnDTOList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
