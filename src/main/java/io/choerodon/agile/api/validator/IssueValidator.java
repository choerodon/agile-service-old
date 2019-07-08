package io.choerodon.agile.api.validator;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;
import io.choerodon.agile.domain.agile.entity.VersionIssueRelE;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueValidator {

    @Autowired
    private ProductVersionMapper productVersionMapper;

    private static final String ERROR_ISSUE_GET = "error.issue.get";
    private static final String ERROR_TYPECODE_ISSUBTASK = "error.typeCode.isSubtask";
    private static final String ERROR_PARENT_ISSUE_ISSUBTASK = "error.parentIssue.isSubtask";
    private static final String ERROR_PARENT_ISSUE_ISTEST = "error.parentIssue.isTest";
    private static final String ERROR_SPRINTIDANDVERSIONID_ALLNOTNULL = "error.sprintIdAndVersionId.allNotNull";
    private static final String ERROR_PARENT_ISSUE_NOT_EXIST = "error.parentIssue.get";
    private static final String ISSUE_ID = "issueId";
    private static final String COLOR = "color";
    private static final String EPIC_NAME = "epicName";
    private static final String ISSUE_EPIC = "issue_epic";
    private static final String SUB_TASK = "sub_task";
    private static final String STORY = "story";
    private static final String FEATURE = "feature";
    private static final String STATUS_ID = "status_id";
    private static final String ERROR_ISSUE_ID_NOT_FOUND = "error.IssueRule.issueId";
    private static final String AGILE = "agile";
    private static final String EPIC_ID = "epicId";
    private static final String FEATURE_ID = "featureId";

    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;

    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;


    public void verifyCreateData(IssueCreateDTO issueCreateDTO, Long projectId, String applyType) {
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
        if (issueCreateDTO.getPriorityId() == null) {
            throw new CommonException("error.priorityId.isNull");
        }
        if (issueCreateDTO.getIssueTypeId() == null) {
            throw new CommonException("error.issueTypeId.isNull");
        }
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
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
        //不是故事无法修改feature
        if (issueUpdate.get(FEATURE_ID) != null && !STORY.equals(issueDO.getTypeCode())) {
            throw new CommonException("error.issue.type");
        } else if (issueUpdate.get(EPIC_ID) != null
                && Long.parseLong(issueUpdate.get(EPIC_ID).toString()) != 0
                && issueUpdate.get(FEATURE_ID) != null
                && Long.parseLong(issueUpdate.get(FEATURE_ID).toString()) != 0) {
            IssueDO issue = new IssueDO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(Long.parseLong(issueUpdate.get(EPIC_ID).toString()));
            issue.setIssueId(Long.parseLong(issueUpdate.get(FEATURE_ID).toString()));
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                throw new CommonException("error.featureId.of.epic");
            }
        } else if (issueUpdate.get(EPIC_ID) != null
                && Long.parseLong(issueUpdate.get(EPIC_ID).toString()) != 0
                && STORY.equals(issueDO.getTypeCode())
                && issueDO.getFeatureId() != null
                && issueDO.getFeatureId() != 0) {
            IssueDO issue = new IssueDO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(Long.parseLong(issueUpdate.get(EPIC_ID).toString()));
            issue.setIssueId(issueDO.getFeatureId());
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                issueUpdate.put("featureId", 0);
            }
        } else if (issueUpdate.get(FEATURE_ID) != null
                && Long.parseLong(issueUpdate.get(FEATURE_ID).toString()) != 0
                && issueDO.getEpicId() != null
                && issueDO.getEpicId() != 0) {
            IssueDO issue = new IssueDO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(issueDO.getEpicId());
            issue.setIssueId(Long.parseLong(issueUpdate.get(FEATURE_ID).toString()));
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                throw new CommonException("error.featureId.of.epic");
            }
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

    public void checkBatchStoryToFeature(Long featureId) {
        if (featureId != null && !Objects.equals(featureId, 0L)) {
            if (issueMapper.selectByPrimaryKey(featureId) == null) {
                throw new CommonException("error.feature.notFound");
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
        if (issueUpdateTypeDTO.getIssueTypeId() == null) {
            throw new CommonException("error.issuetypeId.isNull");
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
        if (issueUpdateTypeDTO.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.subTask");
        }
        if (issueUpdateTypeDTO.getTypeCode().equals(issueE.getTypeCode())) {
            throw new CommonException("error.IssueRule.sameTypeCode");
        }
        Long originStateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, issueE.getIssueTypeId()).getBody();
        Long currentStateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, issueUpdateTypeDTO.getIssueTypeId()).getBody();
        if (originStateMachineId == null || currentStateMachineId == null) {
            throw new CommonException("error.IssueRule.stateMachineId");
        }
        if (!originStateMachineId.equals(currentStateMachineId)) {
            throw new CommonException("error.IssueRule.stateMachineId");
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

    public void verifyTransformedSubTask(IssueTransformSubTask issueTransformSubTask) {
        if (issueTransformSubTask.getIssueId() == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        if (issueTransformSubTask.getParentIssueId() == null) {
            throw new CommonException("error.IssueRule.parentIssueId");
        }
        if (issueTransformSubTask.getObjectVersionNumber() == null) {
            throw new CommonException("error.IssueRule.objectVersionNumber");
        }
    }

    public IssueE verifyTransformedTask(Long projectId, IssueTransformTask issueTransformTask) {
        if (issueTransformTask.getIssueId() == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        if (issueTransformTask.getIssueTypeId() == null) {
            throw new CommonException("error.issuetypeId.isNull");
        }
        if (issueTransformTask.getTypeCode() == null) {
            throw new CommonException("error.IssueRule.typeCode");
        }
        if (issueTransformTask.getTypeCode().equals(ISSUE_EPIC) && issueTransformTask.getEpicName() == null) {
            throw new CommonException("error.IssueRule.epicName");
        }
        IssueE issueE = issueService.queryIssueByProjectIdAndIssueId(projectId, issueTransformTask.getIssueId());
        if (issueE == null) {
            throw new CommonException("error.IssueUpdateTypeDTO.issueDO");
        }
        if (issueTransformTask.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.subTask");
        }
        if (issueTransformTask.getTypeCode().equals(issueE.getTypeCode())) {
            throw new CommonException("error.IssueRule.sameTypeCode");
        }
        return issueE;
    }

    public void verifySubTask(Long parentIssueId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setIssueId(parentIssueId);
        IssueDO query = issueMapper.selectByPrimaryKey(issueDO);
        if (query == null) {
            throw new CommonException("error.IssueRule.issueNoFound");
        } else if (query.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.parentIssueId");
        }
    }

    public void verifyStoryPoints(IssueE issueE) {
        if (issueE.getStoryPoints() != null && !(STORY.equals(issueE.getTypeCode()) || "feature".equals(issueE.getTypeCode()))) {
            throw new CommonException("error.IssueRule.onlyStory");
        }
    }

    public static void checkStoryMapMove(StoryMapMoveDTO storyMapMoveDTO) {
        if (storyMapMoveDTO.getSprintId() != null && storyMapMoveDTO.getVersionId() != null) {
            throw new CommonException(ERROR_SPRINTIDANDVERSIONID_ALLNOTNULL);
        }
    }

    public static void checkParentIdUpdate(IssueDO issueDO, IssueDO parentIssueDO) {
        if (issueDO == null) {
            throw new CommonException(ERROR_ISSUE_GET);
        }
        if (parentIssueDO == null) {
            throw new CommonException(ERROR_PARENT_ISSUE_NOT_EXIST);
        }
        String typeCode = issueDO.getTypeCode();
        if (!"sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_TYPECODE_ISSUBTASK);
        }
        typeCode = parentIssueDO.getTypeCode();
        if ("sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISSUBTASK);
        }
        if (SchemeApplyType.TEST.equals(issueDO.getApplyType())) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISTEST);
        }
    }


    public void checkIssueIdsAndVersionId(Long projectId, List<Long> issueIds, Long versionId) {
        if (issueIds.isEmpty()) {
            throw new CommonException("error.issueValidator.issueIdsNull");
        }
        ProductVersionDO productVersionDO = new ProductVersionDO();
        productVersionDO.setProjectId(projectId);
        productVersionDO.setVersionId(versionId);
        if (productVersionMapper.selectByPrimaryKey(productVersionDO) == null) {
            throw new CommonException("error.issueValidator.versionNotFound");
        }
    }

    public void checkIssueCreate(IssueCreateDTO issueCreateDTO, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        if (SchemeApplyType.AGILE.equals(applyType) && issueCreateDTO.getEpicName() != null && issueService.checkEpicName(issueCreateDTO.getProjectId(), issueCreateDTO.getEpicName())) {
            throw new CommonException("error.epicName.exist");
        }
        if (issueCreateDTO.getRankDTO() != null) {
            RankDTO rankDTO = issueCreateDTO.getRankDTO();
            if (rankDTO.getReferenceIssueId() == null) {
                throw new CommonException("error.referenceIssueId.isNull");
            }
            if (rankDTO.getBefore() == null) {
                throw new CommonException("error.before.isNull");
            }
            if (rankDTO.getType() == null) {
                throw new CommonException("error.type.isNull");
            }
            if (rankDTO.getProjectId() == null) {
                throw new CommonException("error.projectId.isNull");
            }
        }
    }
}
