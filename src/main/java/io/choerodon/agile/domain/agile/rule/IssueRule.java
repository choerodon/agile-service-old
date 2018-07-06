package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueSubCreateDTO;
import io.choerodon.agile.api.dto.IssueTransformSubTask;
import io.choerodon.agile.api.dto.IssueUpdateTypeDTO;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class IssueRule {

    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueStatusMapper issueStatusMapper;
    @Autowired
    private IssueService issueService;
    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;
    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;
    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;

    private static final String ISSUE_ID = "issueId";
    private static final String COLOR = "color";
    private static final String EPIC_NAME = "epicName";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String SUB_TASK = "sub_task";
    private static final String STATUS_ID = "status_id";
    private static final String ERROR_ISSUE_ID_NOT_FOUND = "error.IssueRule.issueId";

    public void verifyCreateData(IssueCreateDTO issueCreateDTO, Long projectId) {
        issueCreateDTO.setProjectId(projectId);
        if (issueCreateDTO.getTypeCode() == null) {
            throw new CommonException("error.IssueRule.typeCode");
        }
        if (issueCreateDTO.getSummary() == null) {
            throw new CommonException("error.IssueRule.Summary");
        }
        if (issueCreateDTO.getPriorityCode() == null) {
            throw new CommonException("error.IssueRule.PriorityCode");
        }
        if (issueCreateDTO.getProjectId() == null) {
            throw new CommonException("error.IssueRule.ProjectId");
        }
        if (issueCreateDTO.getEpicName() != null && !ISSUE_EPIC.equals(issueCreateDTO.getTypeCode())) {
            throw new CommonException("error.IssueRule.EpicName");
        }
    }

    public void verifyUpdateData(JSONObject issueUpdate, Long projectId) {
        if (issueUpdate.get(ISSUE_ID) == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        IssueDO issueDO = new IssueDO();
        issueDO.setIssueId(Long.parseLong(issueUpdate.get(ISSUE_ID).toString()));
        issueDO.setProjectId(projectId);
        issueDO = issueMapper.selectByPrimaryKey(issueDO);
        if (issueDO == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        //不是epic类型的，不能修改颜色
        if (issueUpdate.get(COLOR) != null && !ISSUE_EPIC.equals(issueDO.getTypeCode())) {
            throw new CommonException("error.IssueRule.color");
        }
        //不是epic类型的，不能修改epicName
        if (issueUpdate.get(EPIC_NAME) != null && !ISSUE_EPIC.equals(issueDO.getTypeCode())) {
            throw new CommonException("error.IssueRule.EpicName");
        }
        //修改状态要有当前状态
        if (issueUpdate.get(STATUS_ID) != null && issueStatusMapper.selectByPrimaryKey(Long.parseLong(issueUpdate.get(STATUS_ID).toString())) == null) {
            throw new CommonException("error.IssueRule.statusId");
        }
    }

    public void verifySubCreateData(IssueSubCreateDTO issueSubCreateDTO, Long projectId) {
        if (issueSubCreateDTO.getParentIssueId() == null) {
            throw new CommonException("error.IssueRule.ParentIssueId");
        }
        IssueDO issueDO = new IssueDO();
        issueDO.setProjectId(projectId);
        issueDO.setIssueId(issueSubCreateDTO.getParentIssueId());
        IssueDO query = issueMapper.selectOne(issueDO);
        if (query != null) {
            issueSubCreateDTO.setProjectId(projectId);
        } else {
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }

    public void judgeExist(Long projectId, Long epicId) {
        if (epicId != null && !Objects.equals(epicId, 0L)) {
            IssueDO issueDO = new IssueDO();
            issueDO.setProjectId(projectId);
            issueDO.setIssueId(epicId);
            if (issueMapper.selectByPrimaryKey(issueDO) == null) {
                throw new CommonException("error.epic.notFound");
            }
        }
    }

    public void verifyLabelIssueData(LabelIssueRelE labelIssueRelE) {
        if (labelIssueRelE.getProjectId() == null) {
            throw new CommonException("error.label.ProjectId");
        } else if (labelIssueRelE.getLabelName() == null && labelIssueRelE.getLabelId() == null) {
            throw new CommonException("error.label.LabelName");
        }
    }

    public void verifyVersionIssueRelData(VersionIssueRelE versionIssueRelE) {
        if (versionIssueRelE.getName() == null && versionIssueRelE.getVersionId() == null) {
            throw new CommonException("error.versionIssueRel.Name");
        }
    }

    public void verifyComponentIssueRelData(ComponentIssueRelE componentIssueRelE) {
        if (componentIssueRelE.getComponentId() == null && componentIssueRelE.getName() == null) {
            throw new CommonException("error.componentIssueRelE.Name");
        }
    }

    public IssueE verifyUpdateTypeData(Long projectId, IssueUpdateTypeDTO issueUpdateTypeDTO) {
        if (issueUpdateTypeDTO.getIssueId() == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        if (issueUpdateTypeDTO.getTypeCode() == null) {
            throw new CommonException("error.IssueRule.typeCode");
        }
        if (issueUpdateTypeDTO.getTypeCode().equals(ISSUE_EPIC) && issueUpdateTypeDTO.getEpicName() == null) {
            throw new CommonException("error.IssueRule.epicName");
        }
        IssueE issueE = issueService.queryIssueByProjectIdAndIssueId(projectId, issueUpdateTypeDTO.getIssueId());
        if (issueE == null) {
            throw new CommonException("error.IssueUpdateTypeDTO.issueDO");
        }
        if (issueUpdateTypeDTO.getTypeCode().equals(issueE.getTypeCode())) {
            throw new CommonException("error.IssueRule.sameTypeCode");
        }
        if (issueE.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.subTask");
        }
        return issueE;
    }

    public Boolean existVersionIssueRel(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO();
        versionIssueRelDO.setVersionId(versionIssueRelE.getVersionId());
        versionIssueRelDO.setIssueId(versionIssueRelE.getIssueId());
        versionIssueRelDO.setRelationType(versionIssueRelE.getRelationType());
        return versionIssueRelMapper.selectOne(versionIssueRelDO) == null;
    }

    public Boolean existComponentIssueRel(ComponentIssueRelE componentIssueRelE) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        componentIssueRelDO.setIssueId(componentIssueRelE.getIssueId());
        componentIssueRelDO.setComponentId(componentIssueRelE.getComponentId());
        return componentIssueRelMapper.selectOne(componentIssueRelDO) == null;
    }

    public Boolean existLabelIssue(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO();
        labelIssueRelDO.setLabelId(labelIssueRelE.getLabelId());
        labelIssueRelDO.setIssueId(labelIssueRelE.getIssueId());
        return labelIssueRelMapper.selectOne(labelIssueRelDO) == null;
    }

    public void verifyTransformedSubTask(Long projectId,IssueTransformSubTask issueTransformSubTask) {
        if(issueTransformSubTask.getIssueId()==null){
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        if(issueTransformSubTask.getParentIssueId()==null){
            throw new CommonException("error.IssueRule.parentIssueId");
        }
        if(issueMapper.queryIssueByIssueId(projectId,issueTransformSubTask.getParentIssueId())==null){
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }
}
