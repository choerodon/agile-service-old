package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class CumulativeFlowDiagramVO implements Serializable {

    @ApiModelProperty(value = "列id")
    private Long columnId;

    @ApiModelProperty(value = "列颜色")
    private String color;

    @ApiModelProperty(value = "列名称")
    private String name;

    @ApiModelProperty(value = "列类别")
    private String categoryCode;

    @ApiModelProperty(value = "该列的坐标信息，包含时间和问题计数")
    private List<CoordinateVO> coordinateVOList;

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

    public List<CoordinateVO> getCoordinateVOList() {
        return coordinateVOList;
    }

    public void setCoordinateVOList(List<CoordinateVO> coordinateVOList) {
        this.coordinateVOList = coordinateVOList;
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
