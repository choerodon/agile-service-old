package io.choerodon.agile.api.dto;

import io.choerodon.agile.domain.agile.entity.BurnDownChangeE;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public class BurnDownChangeDTO {

    private BurnDownChangeE.SprintStartDataE sprintStartDataE;

    private BurnDownChangeE.SprintEndDataE sprintEndDataE;

    private List<BurnDownChangeE.IssueChangeE> issueChangeEList;

    public BurnDownChangeE.SprintStartDataE getSprintStartDataE() {
        return sprintStartDataE;
    }

    public void setSprintStartDataE(BurnDownChangeE.SprintStartDataE sprintStartDataE) {
        this.sprintStartDataE = sprintStartDataE;
    }

    public BurnDownChangeE.SprintEndDataE getSprintEndDataE() {
        return sprintEndDataE;
    }

    public void setSprintEndDataE(BurnDownChangeE.SprintEndDataE sprintEndDataE) {
        this.sprintEndDataE = sprintEndDataE;
    }

    public List<BurnDownChangeE.IssueChangeE> getIssueChangeEList() {
        return issueChangeEList;
    }

    public void setIssueChangeEList(List<BurnDownChangeE.IssueChangeE> issueChangeEList) {
        this.issueChangeEList = issueChangeEList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
