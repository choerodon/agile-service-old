package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public class CoordinateE {

    private List<Integer> yAxis;

    private List<Date> xAxis;

    public List<Integer> getyAxis() {
        return yAxis;
    }

    public void setyAxis(List<Integer> yAxis) {
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

    public void initXAxis(SprintE sprintE) {
        if (sprintE.getActualEndDate() == null) {
            if (sprintE.getEndDate() != null) {
                this.xAxis = DateUtil.cutDate(null, sprintE.getStartDate(), sprintE.getEndDate());
            } else {
                this.xAxis = DateUtil.cutDate(null, sprintE.getStartDate(), new Date());
            }
        } else {
            this.xAxis = DateUtil.cutDate(null, sprintE.getStartDate(), sprintE.getEndDate());
        }
    }
}
