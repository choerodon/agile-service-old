package io.choerodon.agile.app.assembler;

import com.google.common.collect.Lists;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;

    /**
     * issueDetailDO转换到IssueDTO
     *
     * @param issueDetailDO issueDetailDO
     * @return IssueVO
     */
    public IssueVO issueDetailDoToDto(IssueDetailDO issueDetailDO, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, PriorityDTO> priorityDTOMap) {
        IssueVO issueVO = new IssueVO();
        BeanUtils.copyProperties(issueDetailDO, issueVO);
        issueVO.setFeatureDTO(ConvertHelper.convert(issueDetailDO.getFeatureDO(), FeatureDTO.class));
        issueVO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueVO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDO.getActiveSprint(), SprintNameDTO.class));
        issueVO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDO.getCloseSprint(), SprintNameDTO.class));
        issueVO.setActivePi(sprintNameAssembler.toTarget(issueDetailDO.getActivePi(), PiNameVO.class));
        issueVO.setClosePi(sprintNameAssembler.toTargetList(issueDetailDO.getClosePi(), PiNameVO.class));
        issueVO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueVO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueVO.setIssueAttachmentVOList(ConvertHelper.convertList(issueDetailDO.getIssueAttachmentDTOList(), IssueAttachmentVO.class));
        issueVO.setIssueCommentVOList(ConvertHelper.convertList(issueDetailDO.getIssueCommentDTOList(), IssueCommentVO.class));
        issueVO.setSubIssueDTOList(issueDoToSubIssueDto(issueDetailDO.getSubIssueDTOList(), issueTypeDTOMap, statusMapDTOMap, priorityDTOMap));
        issueVO.setSubBugDTOList(issueDoToSubIssueDto(issueDetailDO.getSubBugDOList(), issueTypeDTOMap, statusMapDTOMap, priorityDTOMap));
        issueVO.setPriorityDTO(priorityDTOMap.get(issueVO.getPriorityId()));
        issueVO.setIssueTypeDTO(issueTypeDTOMap.get(issueVO.getIssueTypeId()));
        issueVO.setStatusMapDTO(statusMapDTOMap.get(issueVO.getStatusId()));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDO.getAssigneeId());
        assigneeIdList.add(issueDetailDO.getReporterId());
        assigneeIdList.add(issueDetailDO.getCreatedBy());
        Boolean issueCommentCondition = issueVO.getIssueCommentVOList() != null && !issueVO.getIssueCommentVOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueVO.getIssueCommentVOList().stream().map(IssueCommentVO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
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
     * @return IssueListFieldKVDTO
     */

    public List<IssueListFieldKVDTO> issueDoToIssueListFieldKVDTO(List<IssueDTO> issueDTOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, Map<String, String>> foundationCodeValue) {
        List<IssueListFieldKVDTO> issueListFieldKVDTOList = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(issueDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(IssueDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(Lists.newArrayList(userIds), true);
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
            IssueListFieldKVDTO issueListFieldKVDTO = toTarget(issueDO, IssueListFieldKVDTO.class);
            issueListFieldKVDTO.setAssigneeName(assigneeName);
            issueListFieldKVDTO.setAssigneeLoginName(assigneeLoginName);
            issueListFieldKVDTO.setAssigneeRealName(assigneeRealName);
            issueListFieldKVDTO.setReporterName(reporterName);
            issueListFieldKVDTO.setReporterLoginName(reporterLoginName);
            issueListFieldKVDTO.setReporterRealName(reporterRealName);
            issueListFieldKVDTO.setPriorityDTO(priorityMap.get(issueDO.getPriorityId()));
            issueListFieldKVDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListFieldKVDTO.setStatusMapDTO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListFieldKVDTO.setAssigneeImageUrl(assigneeImageUrl);
            issueListFieldKVDTO.setReporterImageUrl(reporterImageUrl);
            issueListFieldKVDTO.setVersionIssueRelDTOS(toTargetList(issueDO.getVersionIssueRelDOS(), VersionIssueRelDTO.class));
            issueListFieldKVDTO.setIssueComponentBriefDTOS(toTargetList(issueDO.getIssueComponentBriefDOS(), IssueComponentBriefDTO.class));
            issueListFieldKVDTO.setIssueSprintDTOS(toTargetList(issueDO.getIssueSprintDOS(), IssueSprintDTO.class));
            issueListFieldKVDTO.setLabelIssueRelDTOS(toTargetList(issueDO.getLabelIssueRelDOS(), LabelIssueRelDTO.class));
            issueListFieldKVDTO.setFoundationFieldValue(foundationCodeValue.get(issueDO.getIssueId()) != null ? foundationCodeValue.get(issueDO.getIssueId()) : new HashMap<>());
            issueListFieldKVDTOList.add(issueListFieldKVDTO);
        });
        return issueListFieldKVDTOList;
    }


    /**
     * issueDO转换到IssueListDTO
     *
     * @param issueDTOList issueDetailDO
     * @return IssueListDTO
     */
    public List<IssueListDTO> issueDoToIssueListDto(List<IssueDTO> issueDTOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<IssueListDTO> issueListDTOList = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(issueDTOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(IssueDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(Lists.newArrayList(userIds), true);
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
            IssueListDTO issueListDTO = toTarget(issueDO, IssueListDTO.class);
            issueListDTO.setAssigneeName(assigneeName);
            issueListDTO.setAssigneeLoginName(assigneeLoginName);
            issueListDTO.setAssigneeRealName(assigneeRealName);
            issueListDTO.setReporterName(reporterName);
            issueListDTO.setReporterLoginName(reporterLoginName);
            issueListDTO.setReporterRealName(reporterRealName);
            issueListDTO.setPriorityDTO(priorityMap.get(issueDO.getPriorityId()));
            issueListDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListDTO.setStatusMapDTO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListDTO.setAssigneeImageUrl(assigneeImageUrl);
            issueListDTO.setReporterImageUrl(reporterImageUrl);
            issueListDTO.setVersionIssueRelDTOS(toTargetList(issueDO.getVersionIssueRelDOS(), VersionIssueRelDTO.class));
            issueListDTO.setIssueComponentBriefDTOS(toTargetList(issueDO.getIssueComponentBriefDOS(), IssueComponentBriefDTO.class));
            issueListDTO.setIssueSprintDTOS(toTargetList(issueDO.getIssueSprintDOS(), IssueSprintDTO.class));
            issueListDTO.setLabelIssueRelDTOS(toTargetList(issueDO.getLabelIssueRelDOS(), LabelIssueRelDTO.class));
            issueListDTOList.add(issueListDTO);
        });
        return issueListDTOList;
    }

    /**
     * issueDO转换到subIssueDTO
     *
     * @param issueDTOList issueDTOList
     * @return SubIssueDTO
     */
    private List<IssueSubListDTO> issueDoToSubIssueDto(List<IssueDTO> issueDTOList, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, PriorityDTO> priorityDTOMap) {
        List<IssueSubListDTO> subIssueDTOList = new ArrayList<>(issueDTOList.size());
        List<Long> assigneeIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
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
            subIssueDTO.setPriorityDTO(priorityDTOMap.get(issueDO.getPriorityId()));
            subIssueDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            subIssueDTO.setStatusMapDTO(statusMapDTOMap.get(issueDO.getStatusId()));
            subIssueDTOList.add(subIssueDTO);
        });
        return subIssueDTOList;
    }

    /**
     * issueDetailDO转换到IssueSubDTO
     *
     * @param issueDetailDO issueDetailDO
     * @return IssueSubDTO
     */
    public IssueSubDTO issueDetailDoToIssueSubDto(IssueDetailDO issueDetailDO) {
        IssueSubDTO issueSubDTO = new IssueSubDTO();
        BeanUtils.copyProperties(issueDetailDO, issueSubDTO);
        issueSubDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueSubDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueSubDTO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDO.getActiveSprint(), SprintNameDTO.class));
        issueSubDTO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDO.getCloseSprint(), SprintNameDTO.class));
        issueSubDTO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueSubDTO.setIssueAttachmentVOList(ConvertHelper.convertList(issueDetailDO.getIssueAttachmentDTOList(), IssueAttachmentVO.class));
        issueSubDTO.setIssueCommentVOList(ConvertHelper.convertList(issueDetailDO.getIssueCommentDTOList(), IssueCommentVO.class));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDO.getAssigneeId());
        assigneeIdList.add(issueDetailDO.getReporterId());
        assigneeIdList.add(issueDetailDO.getCreatedBy());
        Boolean issueCommentCondition = issueSubDTO.getIssueCommentVOList() != null && !issueSubDTO.getIssueCommentVOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueSubDTO.getIssueCommentVOList().stream().map(IssueCommentVO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        String assigneeName = userMessageDOMap.get(issueSubDTO.getAssigneeId()) != null ? userMessageDOMap.get(issueSubDTO.getAssigneeId()).getName() : null;
        String reporterName = userMessageDOMap.get(issueSubDTO.getReporterId()) != null ? userMessageDOMap.get(issueSubDTO.getReporterId()).getName() : null;
        String createrName = userMessageDOMap.get(issueSubDTO.getCreatedBy()) != null ? userMessageDOMap.get(issueSubDTO.getCreatedBy()).getName() : null;
        issueSubDTO.setCreaterEmail(userMessageDOMap.get(issueSubDTO.getCreatedBy()) != null ? userMessageDOMap.get(issueSubDTO.getCreatedBy()).getEmail() : null);
        issueSubDTO.setAssigneeName(assigneeName);
        issueSubDTO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueSubDTO.getAssigneeId()).getImageUrl() : null);
        issueSubDTO.setReporterName(reporterName);
        issueSubDTO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueSubDTO.getReporterId()).getImageUrl() : null);
        issueSubDTO.setCreaterName(createrName);
        issueSubDTO.setCreaterImageUrl(createrName != null ? userMessageDOMap.get(issueSubDTO.getCreatedBy()).getImageUrl() : null);
        if (issueCommentCondition) {
            issueSubDTO.getIssueCommentVOList().forEach(issueCommentDTO -> {
                issueCommentDTO.setUserName(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getName() : null);
                issueCommentDTO.setUserImageUrl(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getImageUrl() : null);
            });
        }
        return issueSubDTO;
    }

    public List<ExportIssuesDTO> exportIssuesDOListToExportIssuesDTO(List<ExportIssuesDO> exportIssues, Long projectId) {
        List<ExportIssuesDTO> exportIssuesDTOS = new ArrayList<>(exportIssues.size());
        Set<Long> userIds = exportIssues.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(ExportIssuesDO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(exportIssues.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(ExportIssuesDO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(new ArrayList<>(userIds), true);
        Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
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

    public IssueCreateDTO issueDtoToIssueCreateDto(IssueDetailDO issueDetailDO) {
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        BeanUtils.copyProperties(issueDetailDO, issueCreateDTO);
        issueCreateDTO.setSprintId(null);
        issueCreateDTO.setRemainingTime(null);
        issueCreateDTO.setComponentIssueRelDTOList(copyComponentIssueRel(issueDetailDO.getComponentIssueRelDOList()));
        issueCreateDTO.setVersionIssueRelDTOList(copyVersionIssueRel(issueDetailDO.getVersionIssueRelDOList()));
        issueCreateDTO.setLabelIssueRelDTOList(copyLabelIssueRel(issueDetailDO.getLabelIssueRelDOList(), issueDetailDO.getProjectId()));
        return issueCreateDTO;
    }

    public IssueSubCreateDTO issueDtoToIssueSubCreateDto(IssueDetailDO issueDetailDO) {
        IssueSubCreateDTO issueSubCreateDTO = new IssueSubCreateDTO();
        BeanUtils.copyProperties(issueDetailDO, issueSubCreateDTO);
        issueSubCreateDTO.setSprintId(null);
        issueSubCreateDTO.setRemainingTime(null);
        issueSubCreateDTO.setComponentIssueRelDTOList(copyComponentIssueRel(issueDetailDO.getComponentIssueRelDOList()));
        issueSubCreateDTO.setVersionIssueRelDTOList(copyVersionIssueRel(issueDetailDO.getVersionIssueRelDOList()));
        issueSubCreateDTO.setLabelIssueRelDTOList(copyLabelIssueRel(issueDetailDO.getLabelIssueRelDOList(), issueDetailDO.getProjectId()));
        return issueSubCreateDTO;
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

    public IssueSubCreateDTO issueDtoToSubIssueCreateDto(IssueDetailDO subIssueDetailDO, Long parentIssueId) {
        IssueSubCreateDTO issueCreateDTO = new IssueSubCreateDTO();
        BeanUtils.copyProperties(subIssueDetailDO, issueCreateDTO);
        String subSummary = "CLONE-" + subIssueDetailDO.getSummary();
        issueCreateDTO.setSummary(subSummary);
        issueCreateDTO.setSprintId(null);
        issueCreateDTO.setIssueNum(null);
        issueCreateDTO.setParentIssueId(parentIssueId);
        issueCreateDTO.setComponentIssueRelDTOList(copyComponentIssueRel(subIssueDetailDO.getComponentIssueRelDOList()));
        issueCreateDTO.setVersionIssueRelDTOList(copyVersionIssueRel(subIssueDetailDO.getVersionIssueRelDOList()));
        issueCreateDTO.setLabelIssueRelDTOList(copyLabelIssueRel(subIssueDetailDO.getLabelIssueRelDOList(), subIssueDetailDO.getProjectId()));
        return issueCreateDTO;
    }

    public List<IssueComponentDetailDTO> issueComponentDetailDoToDto(Long projectId, List<IssueComponentDetailDO> issueComponentDetailDOS) {
        List<IssueComponentDetailDTO> issueComponentDetailDTOS = new ArrayList<>(issueComponentDetailDOS.size());
        if (!issueComponentDetailDOS.isEmpty()) {
            List<Long> userIds = issueComponentDetailDOS.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueComponentDetailDO::getAssigneeId).collect(Collectors.toList());
            userIds.addAll(issueComponentDetailDOS.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).
                    map(IssueComponentDetailDO::getReporterId).collect(Collectors.toList()));
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(userIds.stream().distinct().collect(Collectors.toList()), true);
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
            Map<Long, IssueTypeDTO> issueTypeDTOMapAgile = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            issueTypeDTOMap.putAll(issueTypeDTOMapAgile);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
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
                issueComponentDetailDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
                issueComponentDetailDTO.setStatusMapDTO(statusMapDTOMap.get(issueDO.getStatusId()));
                issueComponentDetailDTO.setPriorityDTO(priorityDTOMap.get(issueDO.getPriorityId()));
                issueComponentDetailDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
                issueComponentDetailDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
                issueComponentDetailDTO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
                issueComponentDetailDTOS.add(issueComponentDetailDTO);
            });
        }
        return issueComponentDetailDTOS;
    }

    public List<IssueListTestDTO> issueDoToIssueTestListDto(List<IssueDTO> issueDTOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<IssueListTestDTO> issueListTestDTOS = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String assigneeImageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            IssueListTestDTO issueListTestDTO = toTarget(issueDO, IssueListTestDTO.class);
            issueListTestDTO.setAssigneeName(assigneeName);
            issueListTestDTO.setPriorityDTO(priorityMap.get(issueDO.getPriorityId()));
            issueListTestDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
            issueListTestDTO.setStatusMapDTO(statusMapDTOMap.get(issueDO.getStatusId()));
            issueListTestDTO.setAssigneeImageUrl(assigneeImageUrl);
            issueListTestDTOS.add(issueListTestDTO);
        });
        return issueListTestDTOS;
    }

    public List<IssueNumDTO> issueNumDoToDto(List<IssueNumDO> issueNumDOList, Long projectId) {
        List<IssueNumDTO> issueNumDTOS = new ArrayList<>(issueNumDOList.size());
        if (!issueNumDOList.isEmpty()) {
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            issueNumDOList.forEach(issueDO -> {
                IssueNumDTO issueNumDTO = new IssueNumDTO();
                BeanUtils.copyProperties(issueDO, issueNumDTO);
                issueNumDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDO.getIssueTypeId()));
                issueNumDTOS.add(issueNumDTO);
            });
        }
        return issueNumDTOS;
    }

    public List<UnfinishedIssueDTO> unfinishedIssueDoToDto(List<UnfinishedIssueDO> unfinishedIssueDOS, Long projectId) {
        List<UnfinishedIssueDTO> unfinishedIssueDTOS = new ArrayList<>(unfinishedIssueDOS.size());
        if (!unfinishedIssueDOS.isEmpty()) {
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            unfinishedIssueDOS.forEach(unfinishedIssueDO -> {
                UnfinishedIssueDTO unfinishedIssueDTO = toTarget(unfinishedIssueDO, UnfinishedIssueDTO.class);
                unfinishedIssueDTO.setIssueTypeDTO(issueTypeDTOMap.get(unfinishedIssueDO.getIssueTypeId()));
                unfinishedIssueDTO.setStatusMapDTO(statusMapDTOMap.get(unfinishedIssueDO.getStatusId()));
                unfinishedIssueDTO.setPriorityDTO(priorityDTOMap.get(unfinishedIssueDO.getPriorityId()));
                unfinishedIssueDTOS.add(unfinishedIssueDTO);
            });

        }
        return unfinishedIssueDTOS;
    }

    public List<UndistributedIssueDTO> undistributedIssueDOToDto(List<UndistributedIssueDO> undistributedIssueDOS, Long projectId) {
        List<UndistributedIssueDTO> undistributedIssueDTOS = new ArrayList<>(undistributedIssueDOS.size());
        if (!undistributedIssueDOS.isEmpty()) {
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            undistributedIssueDOS.forEach(undistributedIssueDO -> {
                UndistributedIssueDTO undistributedIssueDTO = toTarget(undistributedIssueDO, UndistributedIssueDTO.class);
                undistributedIssueDTO.setIssueTypeDTO(issueTypeDTOMap.get(undistributedIssueDO.getIssueTypeId()));
                undistributedIssueDTO.setStatusMapDTO(statusMapDTOMap.get(undistributedIssueDO.getStatusId()));
                undistributedIssueDTO.setPriorityDTO(priorityDTOMap.get(undistributedIssueDO.getPriorityId()));
                undistributedIssueDTOS.add(undistributedIssueDTO);
            });

        }
        return undistributedIssueDTOS;
    }


}
