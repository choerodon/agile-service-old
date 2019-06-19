package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_pi_feature")
public class PiFeatureDO extends BaseDTO {

    private Long issueId;

    private Long piId;

    private Long programId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}
