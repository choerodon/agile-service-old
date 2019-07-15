package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.IssueCreateVO;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDTO;

/**
 * @author shinan.chen
 * @date 2018/11/7
 */
public class CreateIssuePayload {
    IssueCreateVO issueCreateVO;
    IssueConvertDTO issueConvertDTO;
    ProjectInfoDTO projectInfoDTO;

    public CreateIssuePayload(IssueCreateVO issueCreateVO, IssueConvertDTO issueConvertDTO, ProjectInfoDTO projectInfoDTO) {
        this.issueCreateVO = issueCreateVO;
        this.issueConvertDTO = issueConvertDTO;
        this.projectInfoDTO = projectInfoDTO;
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

    public void setProjectInfoDTO(ProjectInfoDTO projectInfoDTO) {
        this.projectInfoDTO = projectInfoDTO;
    }

    public ProjectInfoDTO getProjectInfoDTO() {
        return projectInfoDTO;
    }
}
