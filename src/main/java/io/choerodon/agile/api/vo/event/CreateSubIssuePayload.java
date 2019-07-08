package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueSubCreateDTO;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateSubIssuePayload {
    IssueSubCreateDTO issueSubCreateDTO;
    IssueE issueE;
    ProjectInfoE projectInfoE;

    public CreateSubIssuePayload(IssueSubCreateDTO issueSubCreateDTO, IssueE issueE, ProjectInfoE projectInfoE) {
        this.issueSubCreateDTO = issueSubCreateDTO;
        this.issueE = issueE;
        this.projectInfoE = projectInfoE;
    }

    public IssueSubCreateDTO getIssueSubCreateDTO() {
        return issueSubCreateDTO;
    }

    public void setIssueSubCreateDTO(IssueSubCreateDTO issueSubCreateDTO) {
        this.issueSubCreateDTO = issueSubCreateDTO;
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
