package io.choerodon.agile.domain.agile.event;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateIssuePayload {
    IssueCreateDTO issueCreateDTO;
    IssueE issueE;
    ProjectInfoE projectInfoE;

    public IssueCreateDTO getIssueCreateDTO() {
        return issueCreateDTO;
    }

    public void setIssueCreateDTO(IssueCreateDTO issueCreateDTO) {
        this.issueCreateDTO = issueCreateDTO;
    }

    public IssueE getIssueE() {
        return issueE;
    }

    public void setIssueE(IssueE issueE) {
        this.issueE = issueE;
    }

    public ProjectInfoE getProjectInfoE() {
        return projectInfoE;
    }

    public void setProjectInfoE(ProjectInfoE projectInfoE) {
        this.projectInfoE = projectInfoE;
    }
}
