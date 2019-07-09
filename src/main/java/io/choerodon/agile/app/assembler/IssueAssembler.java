package io.choerodon.agile.app.assembler;

import com.google.common.collect.Lists;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.core.convertor.ConvertHelper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class IssueAssembler extends AbstractAssembler {

    @Autowired
    private UserService userService;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;

    /**
     * issueDetailDO转换到IssueDTO
     *
     * @param issueDetailDTO issueDetailDTO
     * @return IssueVO
     */
    public IssueVO issueDetailDoToDto(IssueDetailDTO issueDetailDTO, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        IssueVO issueVO = new IssueVO();
        BeanUtils.copyProperties(issueDetailDTO, issueVO);
        issueVO.setFeatureVO(ConvertHelper.convert(issueDetailDTO.getFeatureDO(), FeatureVO.class));
        issueVO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueVO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDTO.getActiveSprint(), SprintNameDTO.class));
        issueVO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDTO.getCloseSprint(), SprintNameDTO.class));
        issueVO.setActivePi(sprintNameAssembler.toTarget(issueDetailDTO.getActivePi(), PiNameVO.class));
        issueVO.setClosePi(sprintNameAssembler.toTargetList(issueDetailDTO.getClosePi(), PiNameVO.class));
        issueVO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueVO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueVO.setIssueAttachmentVOList(ConvertHelper.convertList(issueDetailDTO.getIssueAttachmentDTOList(), IssueAttachmentVO.class));
        issueVO.setIssueCommentVOList(ConvertHelper.convertList(issueDetailDTO.getIssueCommentDTOList(), IssueCommentVO.class));
        issueVO.setSubIssueDTOList(issueDoToSubIssueDto(issueDetailDTO.getSubIssueDTOList(), issueTypeDTOMap, statusMapDTOMap, priorityDTOMap));
        issueVO.setSubBugDTOList(issueDoToSubIssueDto(issueDetailDTO.getSubBugDOList(), issueTypeDTOMap, statusMapDTOMap, priorityDTOMap));
        issueVO.setPriorityVO(priorityDTOMap.get(issueVO.getPriorityId()));
        issueVO.setIssueTypeVO(issueTypeDTOMap.get(issueVO.getIssueTypeId()));
        issueVO.setStatusMapVO(statusMapDTOMap.get(issueVO.getStatusId()));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDTO.getAssigneeId());
        assigneeIdList.add(issueDetailDTO.getReporterId());
        assigneeIdList.add(issueDetailDTO.getCreatedBy());
        Boolean issueCommentCondition = issueVO.getIssueCommentVOList() != null && !issueVO.getIssueCommentVOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueVO.getIssueCommentVOList().stream().map(IssueCommentVO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDO> userMessageDOMap = userService.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        UserMessageDO assigneeUserDO = userMessageDOMap.get(issueVO.getAssigneeId());
        UserMessageDO reporterUserDO = userMessageDOMap.get(issueVO.getReporterId());
        String assigneeName = assigneeUserDO != null ? assigneeUserDO.getName() : null;
        String assigneeLoginName = assigneeUserDO != null ? assigneeUserDO.getLoginName() : null;
        String assigneeRealName = assigneeUserDO != null ? assigneeUserDO.getRealName() : null;
        String reporterName = reporterUserDO != null ? reporterUserDO.getName() : null;
        String reporterLoginName = reporterUserDO != null ? reporterUserDO.getLoginName() : null;
        String reporterRealName = reporterUserDO != null ? reporterUserDO.getRealName() : null;
        String createrName = userMessageDOMap.get(issueVO.getCreatedBy()) != null ? userMessageDOMap.get(issueVO.getCreatedBy()).getName() : null;
        issueVO.setCreaterEmail(userMessageDOMap.get(issueVO.getCreatedBy()) != null ? userMessageDOMap.get(issueVO.getCreatedBy()).getEmail() : null);
        issueVO.setAssigneeName(assigneeName);
        issueVO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueVO.getAssigneeId()).getImageUrl() : null);
        issueVO.setReporterName(reporterName);
        issueVO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueVO.getReporterId()).getImageUrl() : null);
        issueVO.setCreaterName(createrName);
        issueVO.setCreaterImageUrl(createrName != null ? userMessageDOMap.get(issueVO.getCreatedBy()).getImageUrl() : null);
        issueVO.setAssigneeLoginName(assigneeLoginName);
        issueVO.setAssigneeRealName(assigneeRealName);
        issueVO.setReporterLoginName(reporterLoginName);
        issueVO.setReporterRealName(reporterRealName);
        if (issueCommentCondition) {
            issueVO.getIssueCommentVOList().forEach(issueCommentDTO -> {
                issueCommentDTO.setUserName(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getName() : null);
                issueCommentDTO.setUserImageUrl(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getImageUrl() : null);
            });
        }
        return issueVO;
    }

    /**
     * issueDO转换到IssueListFieldKVDTO
     *
     * @param issueDTOList issueDetailDO
     * @return IssueListFieldKVVO
     */

    public List<IssueListFieldKVVO> issueDoToIssueListFieldKVDTO(List<IssueDTO> issueDTOList, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, Map<String, String>> foundationCodeValue) {
        List<IssueListFieldKVVO> issueListFieldKVDTOList = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(issueDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(IssueDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDO assigneeUserDO = usersMap.get(issueDO.getAssigneeId());
            UserMessageDO reporterUserDO = usersMap.get(issueDO.getReporterId());
            String assigneeName = assigneeUserDO != null ? assigneeUserDO.getName() : null;
            String assigneeLoginName = assigneeUserDO != null ? assigneeUserDO.getLoginName() : null;
            String assigneeRealName = assigneeUserDO != null ? assigneeUserDO.getRealName() : null;
            String reporterName = reporterUserDO != null ? reporterUserDO.getName() : null;
            String reporterLoginName = reporterUserDO != null ? reporterUserDO.getLoginName() : null;
            String reporterRealName = reporterUserDO != null ? reporterUserDO.getRealName() : null;
            String assigneeImageUrl = assigneeUserDO != null ? assigneeUserDO.getImageUrl() : null;
            String reporterImageUrl = reporterUserDO != null ? reporterUserDO.getImageUrl() : null;
            IssueListFieldKVVO issueListFieldKVVO = toTarget(issueDO, IssueListFieldKVVO.class);
            issueListFieldKVVO.setAssigneeName(assigneeName);
            issueListFieldKVVO.setAssigneeLoginName(assigneeLoginName);
            issueListFieldKVVO.setAssigneeRealName(assigneeRealName);
            issueListFieldKVVO.setReporterName(reporterName);
            issueListFieldKVVO.setReporterLoginName(reporterLoginName);
            issueListFieldKVVO.setReporterRealName(reporterRealName);
            issueListFieldKVVO.setPriorityVO(priorityMap.get(issueDO.getPriorityId()));
            issueListFieldKVVO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListFieldKVVO.setStatusMapVO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListFieldKVVO.setAssigneeImageUrl(assigneeImageUrl);
            issueListFieldKVVO.setReporterImageUrl(reporterImageUrl);
            issueListFieldKVVO.setVersionIssueRelDTOS(toTargetList(issueDO.getVersionIssueRelDOS(), VersionIssueRelDTO.class));
            issueListFieldKVVO.setIssueComponentBriefDTOS(toTargetList(issueDO.getIssueComponentBriefDOS(), IssueComponentBriefDTO.class));
            issueListFieldKVVO.setIssueSprintDTOS(toTargetList(issueDO.getIssueSprintDOS(), IssueSprintDTO.class));
            issueListFieldKVVO.setLabelIssueRelDTOS(toTargetList(issueDO.getLabelIssueRelDOS(), LabelIssueRelDTO.class));
            issueListFieldKVVO.setFoundationFieldValue(foundationCodeValue.get(issueDO.getIssueId()) != null ? foundationCodeValue.get(issueDO.getIssueId()) : new HashMap<>());
            issueListFieldKVDTOList.add(issueListFieldKVVO);
        });
        return issueListFieldKVDTOList;
    }


    /**
     * issueDO转换到IssueListDTO
     *
     * @param issueDTOList issueDetailDO
     * @return IssueListVO
     */
    public List<IssueListVO> issueDoToIssueListDto(List<IssueDTO> issueDTOList, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<IssueListVO> issueListDTOList = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(issueDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(IssueDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDO assigneeUserDO = usersMap.get(issueDO.getAssigneeId());
            UserMessageDO reporterUserDO = usersMap.get(issueDO.getReporterId());
            String assigneeName = assigneeUserDO != null ? assigneeUserDO.getName() : null;
            String assigneeLoginName = assigneeUserDO != null ? assigneeUserDO.getLoginName() : null;
            String assigneeRealName = assigneeUserDO != null ? assigneeUserDO.getRealName() : null;
            String reporterName = reporterUserDO != null ? reporterUserDO.getName() : null;
            String reporterLoginName = reporterUserDO != null ? reporterUserDO.getLoginName() : null;
            String reporterRealName = reporterUserDO != null ? reporterUserDO.getRealName() : null;
            String assigneeImageUrl = assigneeUserDO != null ? assigneeUserDO.getImageUrl() : null;
            String reporterImageUrl = reporterUserDO != null ? reporterUserDO.getImageUrl() : null;
            IssueListVO issueListVO = toTarget(issueDO, IssueListVO.class);
            issueListVO.setAssigneeName(assigneeName);
            issueListVO.setAssigneeLoginName(assigneeLoginName);
            issueListVO.setAssigneeRealName(assigneeRealName);
            issueListVO.setReporterName(reporterName);
            issueListVO.setReporterLoginName(reporterLoginName);
            issueListVO.setReporterRealName(reporterRealName);
            issueListVO.setPriorityVO(priorityMap.get(issueDO.getPriorityId()));
            issueListVO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListVO.setStatusMapVO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListVO.setAssigneeImageUrl(assigneeImageUrl);
            issueListVO.setReporterImageUrl(reporterImageUrl);
            issueListVO.setVersionIssueRelDTOS(toTargetList(issueDO.getVersionIssueRelDOS(), VersionIssueRelDTO.class));
            issueListVO.setIssueComponentBriefDTOS(toTargetList(issueDO.getIssueComponentBriefDOS(), IssueComponentBriefDTO.class));
            issueListVO.setIssueSprintDTOS(toTargetList(issueDO.getIssueSprintDOS(), IssueSprintDTO.class));
            issueListVO.setLabelIssueRelDTOS(toTargetList(issueDO.getLabelIssueRelDOS(), LabelIssueRelDTO.class));
            issueListDTOList.add(issueListVO);
        });
        return issueListDTOList;
    }

    /**
     * issueDO转换到subIssueDTO
     *
     * @param issueDTOList issueDTOList
     * @return SubIssueDTO
     */
    private List<IssueSubListDTO> issueDoToSubIssueDto(List<IssueDTO> issueDTOList, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        List<IssueSubListDTO> subIssueDTOList = new ArrayList<>(issueDTOList.size());
        List<Long> assigneeIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(assigneeIds, true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDO userMessageDO = usersMap.get(issueDO.getAssigneeId());
            String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
            String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
            String loginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
            String realName = userMessageDO != null ? userMessageDO.getRealName() : null;
            IssueSubListDTO subIssueDTO = new IssueSubListDTO();
            BeanUtils.copyProperties(issueDO, subIssueDTO);
            subIssueDTO.setAssigneeName(assigneeName);
            subIssueDTO.setImageUrl(imageUrl);
            subIssueDTO.setLoginName(loginName);
            subIssueDTO.setRealName(realName);
            subIssueDTO.setPriorityVO(priorityDTOMap.get(issueDO.getPriorityId()));
            subIssueDTO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            subIssueDTO.setStatusMapVO(statusMapDTOMap.get(issueDO.getStatusId()));
            subIssueDTOList.add(subIssueDTO);
        });
        return subIssueDTOList;
    }

    /**
     * issueDetailDO转换到IssueSubDTO
     *
     * @param issueDetailDTO issueDetailDTO
     * @return IssueSubVO
     */
    public IssueSubVO issueDetailDoToIssueSubDto(IssueDetailDTO issueDetailDTO) {
        IssueSubVO issueSubVO = new IssueSubVO();
        BeanUtils.copyProperties(issueDetailDTO, issueSubVO);
        issueSubVO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueSubVO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueSubVO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDTO.getActiveSprint(), SprintNameDTO.class));
        issueSubVO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDTO.getCloseSprint(), SprintNameDTO.class));
        issueSubVO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDTO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueSubVO.setIssueAttachmentVOList(ConvertHelper.convertList(issueDetailDTO.getIssueAttachmentDTOList(), IssueAttachmentVO.class));
        issueSubVO.setIssueCommentVOList(ConvertHelper.convertList(issueDetailDTO.getIssueCommentDTOList(), IssueCommentVO.class));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDTO.getAssigneeId());
        assigneeIdList.add(issueDetailDTO.getReporterId());
        assigneeIdList.add(issueDetailDTO.getCreatedBy());
        Boolean issueCommentCondition = issueSubVO.getIssueCommentVOList() != null && !issueSubVO.getIssueCommentVOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueSubVO.getIssueCommentVOList().stream().map(IssueCommentVO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDO> userMessageDOMap = userService.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        String assigneeName = userMessageDOMap.get(issueSubVO.getAssigneeId()) != null ? userMessageDOMap.get(issueSubVO.getAssigneeId()).getName() : null;
        String reporterName = userMessageDOMap.get(issueSubVO.getReporterId()) != null ? userMessageDOMap.get(issueSubVO.getReporterId()).getName() : null;
        String createrName = userMessageDOMap.get(issueSubVO.getCreatedBy()) != null ? userMessageDOMap.get(issueSubVO.getCreatedBy()).getName() : null;
        issueSubVO.setCreaterEmail(userMessageDOMap.get(issueSubVO.getCreatedBy()) != null ? userMessageDOMap.get(issueSubVO.getCreatedBy()).getEmail() : null);
        issueSubVO.setAssigneeName(assigneeName);
        issueSubVO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueSubVO.getAssigneeId()).getImageUrl() : null);
        issueSubVO.setReporterName(reporterName);
        issueSubVO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueSubVO.getReporterId()).getImageUrl() : null);
        issueSubVO.setCreaterName(createrName);
        issueSubVO.setCreaterImageUrl(createrName != null ? userMessageDOMap.get(issueSubVO.getCreatedBy()).getImageUrl() : null);
        if (issueCommentCondition) {
            issueSubVO.getIssueCommentVOList().forEach(issueCommentDTO -> {
                issueCommentDTO.setUserName(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getName() : null);
                issueCommentDTO.setUserImageUrl(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getImageUrl() : null);
            });
        }
        return issueSubVO;
    }

    public List<ExportIssuesDTO> exportIssuesDOListToExportIssuesDTO(List<ExportIssuesDO> exportIssues, Long projectId) {
        List<ExportIssuesDTO> exportIssuesDTOS = new ArrayList<>(exportIssues.size());
        Set<Long> userIds = exportIssues.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(ExportIssuesDO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(exportIssues.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(ExportIssuesDO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(new ArrayList<>(userIds), true);
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        exportIssues.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String assigneeRealName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getRealName() : null;
            String reporterName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
            String reporterRealName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getRealName() : null;
            ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
            BeanUtils.copyProperties(issueDO, exportIssuesDTO);
            exportIssuesDTO.setPriorityName(priorityDTOMap.get(issueDO.getPriorityId()) == null ? null : priorityDTOMap.get(issueDO.getPriorityId()).getName());
            exportIssuesDTO.setStatusName(statusMapDTOMap.get(issueDO.getStatusId()) == null ? null : statusMapDTOMap.get(issueDO.getStatusId()).getName());
            exportIssuesDTO.setTypeName(issueTypeDTOMap.get(issueDO.getIssueTypeId()) == null ? null : issueTypeDTOMap.get(issueDO.getIssueTypeId()).getName());
            exportIssuesDTO.setAssigneeName(assigneeName);
            exportIssuesDTO.setAssigneeRealName(assigneeRealName);
            exportIssuesDTO.setReporterName(reporterName);
            exportIssuesDTO.setReporterRealName(reporterRealName);
            exportIssuesDTOS.add(exportIssuesDTO);
        });
        return exportIssuesDTOS;
    }

    public IssueCreateVO issueDtoToIssueCreateDto(IssueDetailDTO issueDetailDTO) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        BeanUtils.copyProperties(issueDetailDTO, issueCreateVO);
        issueCreateVO.setSprintId(null);
        issueCreateVO.setRemainingTime(null);
        issueCreateVO.setComponentIssueRelDTOList(copyComponentIssueRel(issueDetailDTO.getComponentIssueRelDOList()));
        issueCreateVO.setVersionIssueRelDTOList(copyVersionIssueRel(issueDetailDTO.getVersionIssueRelDOList()));
        issueCreateVO.setLabelIssueRelDTOList(copyLabelIssueRel(issueDetailDTO.getLabelIssueRelDOList(), issueDetailDTO.getProjectId()));
        return issueCreateVO;
    }

    public IssueSubCreateVO issueDtoToIssueSubCreateDto(IssueDetailDTO issueDetailDTO) {
        IssueSubCreateVO issueSubCreateVO = new IssueSubCreateVO();
        BeanUtils.copyProperties(issueDetailDTO, issueSubCreateVO);
        issueSubCreateVO.setSprintId(null);
        issueSubCreateVO.setRemainingTime(null);
        issueSubCreateVO.setComponentIssueRelDTOList(copyComponentIssueRel(issueDetailDTO.getComponentIssueRelDOList()));
        issueSubCreateVO.setVersionIssueRelDTOList(copyVersionIssueRel(issueDetailDTO.getVersionIssueRelDOList()));
        issueSubCreateVO.setLabelIssueRelDTOList(copyLabelIssueRel(issueDetailDTO.getLabelIssueRelDOList(), issueDetailDTO.getProjectId()));
        return issueSubCreateVO;
    }

    private List<ComponentIssueRelDTO> copyComponentIssueRel(List<ComponentIssueRelDO> componentIssueRelDOList) {
        List<ComponentIssueRelDTO> componentIssueRelDTOList = new ArrayList<>(componentIssueRelDOList.size());
        componentIssueRelDOList.forEach(componentIssueRelDO -> {
            ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
            BeanUtils.copyProperties(componentIssueRelDO, componentIssueRelDTO);
            componentIssueRelDTO.setIssueId(null);
            componentIssueRelDTO.setObjectVersionNumber(null);
            componentIssueRelDTOList.add(componentIssueRelDTO);
        });
        return componentIssueRelDTOList;
    }

    private List<LabelIssueRelDTO> copyLabelIssueRel(List<LabelIssueRelDO> labelIssueRelDOList, Long projectId) {
        List<LabelIssueRelDTO> labelIssueRelDTOList = new ArrayList<>(labelIssueRelDOList.size());
        labelIssueRelDOList.forEach(labelIssueRelDO -> {
            LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
            BeanUtils.copyProperties(labelIssueRelDO, labelIssueRelDTO);
            labelIssueRelDTO.setIssueId(null);
            labelIssueRelDTO.setLabelName(null);
            labelIssueRelDTO.setObjectVersionNumber(null);
            labelIssueRelDTO.setProjectId(projectId);
            labelIssueRelDTOList.add(labelIssueRelDTO);
        });
        return labelIssueRelDTOList;
    }

    private List<VersionIssueRelDTO> copyVersionIssueRel(List<VersionIssueRelDO> versionIssueRelDOList) {
        List<VersionIssueRelDTO> versionIssueRelDTOList = new ArrayList<>(versionIssueRelDOList.size());
        versionIssueRelDOList.forEach(versionIssueRelDO -> {
            VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
            BeanUtils.copyProperties(versionIssueRelDO, versionIssueRelDTO);
            versionIssueRelDTO.setIssueId(null);
            versionIssueRelDTOList.add(versionIssueRelDTO);
        });
        return versionIssueRelDTOList;
    }

    public IssueSubCreateVO issueDtoToSubIssueCreateDto(IssueDetailDTO subIssueDetailDTO, Long parentIssueId) {
        IssueSubCreateVO issueCreateDTO = new IssueSubCreateVO();
        BeanUtils.copyProperties(subIssueDetailDTO, issueCreateDTO);
        String subSummary = "CLONE-" + subIssueDetailDTO.getSummary();
        issueCreateDTO.setSummary(subSummary);
        issueCreateDTO.setSprintId(null);
        issueCreateDTO.setIssueNum(null);
        issueCreateDTO.setParentIssueId(parentIssueId);
        issueCreateDTO.setComponentIssueRelDTOList(copyComponentIssueRel(subIssueDetailDTO.getComponentIssueRelDOList()));
        issueCreateDTO.setVersionIssueRelDTOList(copyVersionIssueRel(subIssueDetailDTO.getVersionIssueRelDOList()));
        issueCreateDTO.setLabelIssueRelDTOList(copyLabelIssueRel(subIssueDetailDTO.getLabelIssueRelDOList(), subIssueDetailDTO.getProjectId()));
        return issueCreateDTO;
    }

    public List<IssueComponentDetailDTO> issueComponentDetailDoToDto(Long projectId, List<IssueComponentDetailDO> issueComponentDetailDOS) {
        List<IssueComponentDetailDTO> issueComponentDetailDTOS = new ArrayList<>(issueComponentDetailDOS.size());
        if (!issueComponentDetailDOS.isEmpty()) {
            List<Long> userIds = issueComponentDetailDOS.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueComponentDetailDO::getAssigneeId).collect(Collectors.toList());
            userIds.addAll(issueComponentDetailDOS.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).
                    map(IssueComponentDetailDO::getReporterId).collect(Collectors.toList()));
            Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(userIds.stream().distinct().collect(Collectors.toList()), true);
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
            Map<Long, IssueTypeVO> issueTypeDTOMapAgile = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            issueTypeDTOMap.putAll(issueTypeDTOMapAgile);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            issueComponentDetailDOS.parallelStream().forEachOrdered(issueDO -> {
                String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
                String assigneeLoginName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getLoginName() : null;
                String assigneeRealName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getRealName() : null;
                String reporterName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
                String reporterLoginName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getLoginName() : null;
                String reporterRealName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getRealName() : null;
                String assigneeImageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
                String reporterImageUrl = reporterName != null ? usersMap.get(issueDO.getReporterId()).getImageUrl() : null;
                IssueComponentDetailDTO issueComponentDetailDTO = new IssueComponentDetailDTO();
                BeanUtils.copyProperties(issueDO, issueComponentDetailDTO);
                issueComponentDetailDTO.setAssigneeName(assigneeName);
                issueComponentDetailDTO.setAssigneeLoginName(assigneeLoginName);
                issueComponentDetailDTO.setAssigneeRealName(assigneeRealName);
                issueComponentDetailDTO.setReporterName(reporterName);
                issueComponentDetailDTO.setReporterLoginName(reporterLoginName);
                issueComponentDetailDTO.setReporterRealName(reporterRealName);
                issueComponentDetailDTO.setAssigneeImageUrl(assigneeImageUrl);
                issueComponentDetailDTO.setReporterImageUrl(reporterImageUrl);
                issueComponentDetailDTO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
                issueComponentDetailDTO.setStatusMapVO(statusMapDTOMap.get(issueDO.getStatusId()));
                issueComponentDetailDTO.setPriorityVO(priorityDTOMap.get(issueDO.getPriorityId()));
                issueComponentDetailDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
                issueComponentDetailDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
                issueComponentDetailDTO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
                issueComponentDetailDTOS.add(issueComponentDetailDTO);
            });
        }
        return issueComponentDetailDTOS;
    }

    public List<IssueListTestVO> issueDoToIssueTestListDto(List<IssueDTO> issueDTOList, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<IssueListTestVO> issueListTestVOS = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String assigneeImageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            IssueListTestVO issueListTestVO = toTarget(issueDO, IssueListTestVO.class);
            issueListTestVO.setAssigneeName(assigneeName);
            issueListTestVO.setPriorityVO(priorityMap.get(issueDO.getPriorityId()));
            issueListTestVO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListTestVO.setStatusMapVO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListTestVO.setAssigneeImageUrl(assigneeImageUrl);
            issueListTestVOS.add(issueListTestVO);
        });
        return issueListTestVOS;
    }

    public List<IssueNumVO> issueNumDoToDto(List<IssueNumDTO> issueNumDTOList, Long projectId) {
        List<IssueNumVO> issueNumVOS = new ArrayList<>(issueNumDTOList.size());
        if (!issueNumDTOList.isEmpty()) {
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            issueNumDTOList.forEach(issueDO -> {
                IssueNumVO issueNumVO = new IssueNumVO();
                BeanUtils.copyProperties(issueDO, issueNumVO);
                issueNumVO.setIssueTypeVO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
                issueNumVOS.add(issueNumVO);
            });
        }
        return issueNumVOS;
    }

    public List<UnfinishedIssueDTO> unfinishedIssueDoToDto(List<UnfinishedIssueDO> unfinishedIssueDOS, Long projectId) {
        List<UnfinishedIssueDTO> unfinishedIssueDTOS = new ArrayList<>(unfinishedIssueDOS.size());
        if (!unfinishedIssueDOS.isEmpty()) {
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            unfinishedIssueDOS.forEach(unfinishedIssueDO -> {
                UnfinishedIssueDTO unfinishedIssueDTO = toTarget(unfinishedIssueDO, UnfinishedIssueDTO.class);
                unfinishedIssueDTO.setIssueTypeVO(issueTypeDTOMap.get(unfinishedIssueDO.getIssueTypeId()));
                unfinishedIssueDTO.setStatusMapVO(statusMapDTOMap.get(unfinishedIssueDO.getStatusId()));
                unfinishedIssueDTO.setPriorityVO(priorityDTOMap.get(unfinishedIssueDO.getPriorityId()));
                unfinishedIssueDTOS.add(unfinishedIssueDTO);
            });

        }
        return unfinishedIssueDTOS;
    }

    public List<UndistributedIssueVO> undistributedIssueDOToDto(List<UndistributedIssueDTO> undistributedIssueDTOS, Long projectId) {
        List<UndistributedIssueVO> undistributedIssueVOS = new ArrayList<>(undistributedIssueDTOS.size());
        if (!undistributedIssueDTOS.isEmpty()) {
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            undistributedIssueDTOS.forEach(undistributedIssueDTO -> {
                UndistributedIssueVO undistributedIssueVO = toTarget(undistributedIssueDTO, UndistributedIssueVO.class);
                undistributedIssueVO.setIssueTypeVO(issueTypeDTOMap.get(undistributedIssueDTO.getIssueTypeId()));
                undistributedIssueVO.setStatusMapVO(statusMapDTOMap.get(undistributedIssueDTO.getStatusId()));
                undistributedIssueVO.setPriorityVO(priorityDTOMap.get(undistributedIssueDTO.getPriorityId()));
                undistributedIssueVOS.add(undistributedIssueVO);
            });

        }
        return undistributedIssueVOS;
    }


}
