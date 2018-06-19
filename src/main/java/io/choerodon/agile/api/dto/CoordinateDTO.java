package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public class CoordinateDTO {

    private List<Float> yAxis;

    private List<Date> xAxis;

    public List<Float> getyAxis() {
        return yAxis;
    }

    public void setyAxis(List<Float> yAxis) {
        this.yAxis = yAxis;
    }

    public List<Date> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<Date> xAxis) {
        this.xAxis = xAxis;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
