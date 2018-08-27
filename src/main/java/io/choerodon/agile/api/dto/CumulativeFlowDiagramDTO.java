package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CumulativeFlowDiagramDTO implements Serializable {

    private Long columnId;

    private String color;

    private String name;

    private String categoryCode;

    private List<CoordinateDTO> coordinateDTOList;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CoordinateDTO> getCoordinateDTOList() {
        return coordinateDTOList;
    }

    public void setCoordinateDTOList(List<CoordinateDTO> coordinateDTOList) {
        this.coordinateDTOList = coordinateDTOList;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
