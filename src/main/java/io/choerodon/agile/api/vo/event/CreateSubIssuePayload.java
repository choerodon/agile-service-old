package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueSubCreateVO;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateSubIssuePayload {
    IssueSubCreateVO issueSubCreateVO;
    IssueConvertDTO issueConvertDTO;
    ProjectInfoDTO projectInfoDTO;

    public CreateSubIssuePayload(IssueSubCreateVO issueSubCreateVO, IssueConvertDTO issueConvertDTO, ProjectInfoDTO projectInfoDTO) {
        this.issueSubCreateVO = issueSubCreateVO;
        this.issueConvertDTO = issueConvertDTO;
        this.projectInfoDTO = projectInfoDTO;
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

    public void setProjectInfoDTO(ProjectInfoDTO projectInfoDTO) {
        this.projectInfoDTO = projectInfoDTO;
    }

    public ProjectInfoDTO getProjectInfoDTO() {
        return projectInfoDTO;
    }
}
