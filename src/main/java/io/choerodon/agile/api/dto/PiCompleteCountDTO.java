package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/29.
 * Email: fuqianghuang01@gmail.com
 */
public class PiCompleteCountDTO {

    private Long completedCount;

    private Long unCompletedCount;

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
