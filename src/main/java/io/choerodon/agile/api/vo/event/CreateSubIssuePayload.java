package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueSubCreateVO;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateSubIssuePayload {
    IssueSubCreateVO issueSubCreateVO;
    IssueConvertDTO issueConvertDTO;
    ProjectInfoE projectInfoE;

    public CreateSubIssuePayload(IssueSubCreateVO issueSubCreateVO, IssueConvertDTO issueConvertDTO, ProjectInfoE projectInfoE) {
        this.issueSubCreateVO = issueSubCreateVO;
        this.issueConvertDTO = issueConvertDTO;
        this.projectInfoE = projectInfoE;
    }

    public IssueSubCreateVO getIssueSubCreateVO() {
        return issueSubCreateVO;
    }

    public void setIssueSubCreateVO(IssueSubCreateVO issueSubCreateVO) {
        this.issueSubCreateVO = issueSubCreateVO;
    }

    public IssueConvertDTO getIssueConvertDTO() {
        return issueConvertDTO;
    }

    public void setIssueConvertDTO(IssueConvertDTO issueConvertDTO) {
        this.issueConvertDTO = issueConvertDTO;
    }

    public ProjectInfoE getProjectInfoE() {
        return projectInfoE;
    }

    public void setProjectInfoE(ProjectInfoE projectInfoE) {
        this.projectInfoE = projectInfoE;
    }
}
