package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.RankDTO;
import io.choerodon.agile.api.dto.StoryMapMoveDTO;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Autowired
    private IssueService issueService;


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
