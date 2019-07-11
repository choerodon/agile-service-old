package io.choerodon.agile.api.validator;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
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


    public void verifyCreateData(IssueCreateVO issueCreateVO, Long projectId, String applyType) {
        issueCreateVO.setProjectId(projectId);
        if (issueCreateVO.getTypeCode() == null) {
            throw new CommonException("error.IssueRule.typeCode");
        }
        if (issueCreateVO.getSummary() == null) {
            throw new CommonException("error.IssueRule.Summary");
        }
        if (issueCreateVO.getPriorityCode() == null) {
            throw new CommonException("error.IssueRule.PriorityCode");
        }
        if (issueCreateVO.getProjectId() == null) {
            throw new CommonException("error.IssueRule.ProjectId");
        }
        if (issueCreateVO.getEpicName() != null && !ISSUE_EPIC.equals(issueCreateVO.getTypeCode())) {
            throw new CommonException("error.IssueRule.EpicName");
        }
        if (issueCreateVO.getPriorityId() == null) {
            throw new CommonException("error.priorityId.isNull");
        }
        if (issueCreateVO.getIssueTypeId() == null) {
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
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setIssueId(Long.parseLong(issueUpdate.get(ISSUE_ID).toString()));
        issueDTO.setProjectId(projectId);
        issueDTO = issueMapper.selectByPrimaryKey(issueDTO);
        if (issueDTO == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        //不是epic类型的，不能修改颜色
        if (issueUpdate.get(COLOR) != null && !ISSUE_EPIC.equals(issueDTO.getTypeCode())) {
            throw new CommonException("error.IssueRule.color");
        }
        //不是epic类型的，不能修改epicName
        if (issueUpdate.get(EPIC_NAME) != null && !ISSUE_EPIC.equals(issueDTO.getTypeCode())) {
            throw new CommonException("error.IssueRule.EpicName");
        }
        //修改状态要有当前状态
        if (issueUpdate.get(STATUS_ID) != null && issueStatusMapper.selectByPrimaryKey(Long.parseLong(issueUpdate.get(STATUS_ID).toString())) == null) {
            throw new CommonException("error.IssueRule.statusId");
        }
        //不是故事无法修改feature
        if (issueUpdate.get(FEATURE_ID) != null && !STORY.equals(issueDTO.getTypeCode())) {
            throw new CommonException("error.issue.type");
        } else if (issueUpdate.get(EPIC_ID) != null
                && Long.parseLong(issueUpdate.get(EPIC_ID).toString()) != 0
                && issueUpdate.get(FEATURE_ID) != null
                && Long.parseLong(issueUpdate.get(FEATURE_ID).toString()) != 0) {
            IssueDTO issue = new IssueDTO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(Long.parseLong(issueUpdate.get(EPIC_ID).toString()));
            issue.setIssueId(Long.parseLong(issueUpdate.get(FEATURE_ID).toString()));
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                throw new CommonException("error.featureId.of.epic");
            }
        } else if (issueUpdate.get(EPIC_ID) != null
                && Long.parseLong(issueUpdate.get(EPIC_ID).toString()) != 0
                && STORY.equals(issueDTO.getTypeCode())
                && issueDTO.getFeatureId() != null
                && issueDTO.getFeatureId() != 0) {
            IssueDTO issue = new IssueDTO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(Long.parseLong(issueUpdate.get(EPIC_ID).toString()));
            issue.setIssueId(issueDTO.getFeatureId());
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                issueUpdate.put("featureId", 0);
            }
        } else if (issueUpdate.get(FEATURE_ID) != null
                && Long.parseLong(issueUpdate.get(FEATURE_ID).toString()) != 0
                && issueDTO.getEpicId() != null
                && issueDTO.getEpicId() != 0) {
            IssueDTO issue = new IssueDTO();
            issue.setProjectId(projectId);
            issue.setTypeCode(FEATURE);
            issue.setEpicId(issueDTO.getEpicId());
            issue.setIssueId(Long.parseLong(issueUpdate.get(FEATURE_ID).toString()));
            if (issueMapper.selectByPrimaryKey(issue) == null) {
                throw new CommonException("error.featureId.of.epic");
            }
        }

    }

    public void verifySubCreateData(IssueSubCreateVO issueSubCreateVO, Long projectId) {
        if (issueSubCreateVO.getParentIssueId() == null) {
            throw new CommonException("error.IssueRule.ParentIssueId");
        }
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setProjectId(projectId);
        issueDTO.setIssueId(issueSubCreateVO.getParentIssueId());
        IssueDTO query = issueMapper.selectOne(issueDTO);
        if (query != null) {
            issueSubCreateVO.setProjectId(projectId);
        } else {
            throw new CommonException("error.IssueRule.issueNoFound");
        }
    }

    public void judgeExist(Long projectId, Long epicId) {
        if (epicId != null && !Objects.equals(epicId, 0L)) {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setProjectId(projectId);
            issueDTO.setIssueId(epicId);
            if (issueMapper.selectByPrimaryKey(issueDTO) == null) {
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

    public IssueConvertDTO verifyUpdateTypeData(Long projectId, IssueUpdateTypeVO issueUpdateTypeVO) {
        if (issueUpdateTypeVO.getIssueId() == null) {
            throw new CommonException(ERROR_ISSUE_ID_NOT_FOUND);
        }
        if (issueUpdateTypeVO.getIssueTypeId() == null) {
            throw new CommonException("error.issuetypeId.isNull");
        }
        if (issueUpdateTypeVO.getTypeCode() == null) {
            throw new CommonException("error.IssueRule.typeCode");
        }
        if (issueUpdateTypeVO.getTypeCode().equals(ISSUE_EPIC) && issueUpdateTypeVO.getEpicName() == null) {
            throw new CommonException("error.IssueRule.epicName");
        }
        IssueConvertDTO issueConvertDTO = issueService.queryIssueByProjectIdAndIssueId(projectId, issueUpdateTypeVO.getIssueId());
        if (issueConvertDTO == null) {
            throw new CommonException("error.IssueUpdateTypeVO.issueDO");
        }
        if (issueUpdateTypeVO.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.subTask");
        }
        if (issueUpdateTypeVO.getTypeCode().equals(issueConvertDTO.getTypeCode())) {
            throw new CommonException("error.IssueRule.sameTypeCode");
        }
        Long originStateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, issueConvertDTO.getIssueTypeId()).getBody();
        Long currentStateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, issueUpdateTypeVO.getIssueTypeId()).getBody();
        if (originStateMachineId == null || currentStateMachineId == null) {
            throw new CommonException("error.IssueRule.stateMachineId");
        }
        if (!originStateMachineId.equals(currentStateMachineId)) {
            throw new CommonException("error.IssueRule.stateMachineId");
        }
        return issueConvertDTO;
    }

    public Boolean existVersionIssueRel(VersionIssueRelE versionIssueRelE) {
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setVersionId(versionIssueRelE.getVersionId());
        versionIssueRelDTO.setIssueId(versionIssueRelE.getIssueId());
        versionIssueRelDTO.setRelationType(versionIssueRelE.getRelationType());
        return versionIssueRelMapper.selectOne(versionIssueRelDTO) == null;
    }

    public Boolean existComponentIssueRel(ComponentIssueRelE componentIssueRelE) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        componentIssueRelDTO.setIssueId(componentIssueRelE.getIssueId());
        componentIssueRelDTO.setComponentId(componentIssueRelE.getComponentId());
        return componentIssueRelMapper.selectOne(componentIssueRelDTO) == null;
    }

    public Boolean existLabelIssue(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
        labelIssueRelDTO.setLabelId(labelIssueRelE.getLabelId());
        labelIssueRelDTO.setIssueId(labelIssueRelE.getIssueId());
        return labelIssueRelMapper.selectOne(labelIssueRelDTO) == null;
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

    public IssueConvertDTO verifyTransformedTask(Long projectId, IssueTransformTask issueTransformTask) {
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
        IssueConvertDTO issueConvertDTO = issueService.queryIssueByProjectIdAndIssueId(projectId, issueTransformTask.getIssueId());
        if (issueConvertDTO == null) {
            throw new CommonException("error.IssueUpdateTypeVO.issueDO");
        }
        if (issueTransformTask.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.subTask");
        }
        if (issueTransformTask.getTypeCode().equals(issueConvertDTO.getTypeCode())) {
            throw new CommonException("error.IssueRule.sameTypeCode");
        }
        return issueConvertDTO;
    }

    public void verifySubTask(Long parentIssueId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setIssueId(parentIssueId);
        IssueDTO query = issueMapper.selectByPrimaryKey(issueDTO);
        if (query == null) {
            throw new CommonException("error.IssueRule.issueNoFound");
        } else if (query.getTypeCode().equals(SUB_TASK)) {
            throw new CommonException("error.IssueRule.parentIssueId");
        }
    }

    public void verifyStoryPoints(IssueConvertDTO issueConvertDTO) {
        if (issueConvertDTO.getStoryPoints() != null && !(STORY.equals(issueConvertDTO.getTypeCode()) || "feature".equals(issueConvertDTO.getTypeCode()))) {
            throw new CommonException("error.IssueRule.onlyStory");
        }
    }

//    public static void checkStoryMapMove(StoryMapMoveDTO storyMapMoveDTO) {
//        if (storyMapMoveDTO.getSprintId() != null && storyMapMoveDTO.getVersionId() != null) {
//            throw new CommonException(ERROR_SPRINTIDANDVERSIONID_ALLNOTNULL);
//        }
//    }

    public static void checkParentIdUpdate(IssueDTO issueDTO, IssueDTO parentIssueDTO) {
        if (issueDTO == null) {
            throw new CommonException(ERROR_ISSUE_GET);
        }
        if (parentIssueDTO == null) {
            throw new CommonException(ERROR_PARENT_ISSUE_NOT_EXIST);
        }
        String typeCode = issueDTO.getTypeCode();
        if (!"sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_TYPECODE_ISSUBTASK);
        }
        typeCode = parentIssueDTO.getTypeCode();
        if ("sub_task".equals(typeCode)) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISSUBTASK);
        }
        if (SchemeApplyType.TEST.equals(issueDTO.getApplyType())) {
            throw new CommonException(ERROR_PARENT_ISSUE_ISTEST);
        }
    }


    public void checkIssueIdsAndVersionId(Long projectId, List<Long> issueIds, Long versionId) {
        if (issueIds.isEmpty()) {
            throw new CommonException("error.issueValidator.issueIdsNull");
        }
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setProjectId(projectId);
        productVersionDTO.setVersionId(versionId);
        if (productVersionMapper.selectByPrimaryKey(productVersionDTO) == null) {
            throw new CommonException("error.issueValidator.versionNotFound");
        }
    }

    public void checkIssueCreate(IssueCreateVO issueCreateVO, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        if (SchemeApplyType.AGILE.equals(applyType) && issueCreateVO.getEpicName() != null && issueService.checkEpicName(issueCreateVO.getProjectId(), issueCreateVO.getEpicName())) {
            throw new CommonException("error.epicName.exist");
        }
        if (issueCreateVO.getRankVO() != null) {
            RankVO rankVO = issueCreateVO.getRankVO();
            if (rankVO.getReferenceIssueId() == null) {
                throw new CommonException("error.referenceIssueId.isNull");
            }
            if (rankVO.getBefore() == null) {
                throw new CommonException("error.before.isNull");
            }
            if (rankVO.getType() == null) {
                throw new CommonException("error.type.isNull");
            }
            if (rankVO.getProjectId() == null) {
                throw new CommonException("error.projectId.isNull");
            }
        }
    }
}
