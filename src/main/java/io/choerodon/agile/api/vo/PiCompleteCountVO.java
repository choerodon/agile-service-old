package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.dataobject.PiTodoDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/29.
 * Email: fuqianghuang01@gmail.com
 */
public class PiCompleteCountVO {

    @ApiModelProperty(value = "已完成的问题计数")
    private Long completedCount;

    @ApiModelProperty(value = "未完成的问题计数")
    private Long unCompletedCount;

    @ApiModelProperty(value = "未开启pi列表")
    private List<PiTodoDTO> piTodoDTOList;

    public Long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Long completedCount) {
        this.completedCount = completedCount;
    }

    public Long getUnCompletedCount() {
        return unCompletedCount;
    }

    public void setUnCompletedCount(Long unCompletedCount) {
        this.unCompletedCount = unCompletedCount;
    }

    public void setPiTodoDTOList(List<PiTodoDTO> piTodoDTOList) {
        this.piTodoDTOList = piTodoDTOList;
    }

    public List<PiTodoDTO> getPiTodoDTOList() {
        return piTodoDTOList;
    }
}
