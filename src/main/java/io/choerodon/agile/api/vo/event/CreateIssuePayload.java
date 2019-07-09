package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueCreateVO;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateIssuePayload {
    IssueCreateVO issueCreateVO;
    IssueConvertDTO issueConvertDTO;
    ProjectInfoE projectInfoE;

    public CreateIssuePayload(IssueCreateVO issueCreateVO, IssueConvertDTO issueConvertDTO, ProjectInfoE projectInfoE) {
        this.issueCreateVO = issueCreateVO;
        this.issueConvertDTO = issueConvertDTO;
        this.projectInfoE = projectInfoE;
    }

    public IssueCreateVO getIssueCreateVO() {
        return issueCreateVO;
    }

    public void setIssueCreateVO(IssueCreateVO issueCreateVO) {
        this.issueCreateVO = issueCreateVO;
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
