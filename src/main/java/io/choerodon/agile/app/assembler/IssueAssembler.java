package io.choerodon.agile.app.assembler;

import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.domain.agile.repository.UserRepository;
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
     * @return IssueDTO
     */
    public IssueDTO issueDetailDoToDto(IssueDetailDO issueDetailDO, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, PriorityDTO> priorityDTOMap) {
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueDetailDO, issueDTO);
        issueDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueDTO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDO.getActiveSprint(), SprintNameDTO.class));
        issueDTO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDO.getCloseSprint(), SprintNameDTO.class));
        issueDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueDTO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueDTO.setIssueAttachmentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueAttachmentDOList(), IssueAttachmentDTO.class));
        issueDTO.setIssueCommentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueCommentDOList(), IssueCommentDTO.class));
        issueDTO.setSubIssueDTOList(issueDoToSubIssueDto(issueDetailDO.getSubIssueDOList(), issueTypeDTOMap, statusMapDTOMap, priorityDTOMap));
        issueDTO.setPriorityDTO(priorityDTOMap.get(issueDTO.getPriorityId()));
        issueDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueDTO.getIssueTypeId()));
        issueDTO.setStatusMapDTO(statusMapDTOMap.get(issueDTO.getStatusId()));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDO.getAssigneeId());
        assigneeIdList.add(issueDetailDO.getReporterId());
        Boolean issueCommentCondition = issueDTO.getIssueCommentDTOList() != null && !issueDTO.getIssueCommentDTOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueDTO.getIssueCommentDTOList().stream().map(IssueCommentDTO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        String assigneeName = userMessageDOMap.get(issueDTO.getAssigneeId()) != null ? userMessageDOMap.get(issueDTO.getAssigneeId()).getName() : null;
        String reporterName = userMessageDOMap.get(issueDTO.getReporterId()) != null ? userMessageDOMap.get(issueDTO.getReporterId()).getName() : null;
        issueDTO.setAssigneeName(assigneeName);
        issueDTO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueDTO.getAssigneeId()).getImageUrl() : null);
        issueDTO.setReporterName(reporterName);
        issueDTO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueDTO.getReporterId()).getImageUrl() : null);
        if (issueCommentCondition) {
            issueDTO.getIssueCommentDTOList().forEach(issueCommentDTO -> {
                issueCommentDTO.setUserName(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getName() : null);
                issueCommentDTO.setUserImageUrl(userMessageDOMap.get(issueCommentDTO.getUserId()) != null ? userMessageDOMap.get(issueCommentDTO.getUserId()).getImageUrl() : null);
            });
        }
        return issueDTO;
    }

    /**
     * issueDO转换到IssueListDTO
     *
     * @param issueDOList issueDetailDO
     * @return IssueListDTO
     */
    public List<IssueListDTO> issueDoToIssueListDto(List<IssueDO> issueDOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<IssueListDTO> issueListDTOList = new ArrayList<>(issueDOList.size());
        Set<Long> userIds = issueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(issueDOList.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(IssueDO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String reporterName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
            String assigneeImageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            String reporterImageUrl = reporterName != null ? usersMap.get(issueDO.getReporterId()).getImageUrl() : null;
            IssueListDTO issueListDTO = toTarget(issueDO, IssueListDTO.class);
            issueListDTO.setAssigneeName(assigneeName);
            issueListDTO.setReporterName(reporterName);
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
     * @param issueDOList issueDOList
     * @return SubIssueDTO
     */
    private List<IssueSubListDTO> issueDoToSubIssueDto(List<IssueDO> issueDOList, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, PriorityDTO> priorityDTOMap) {
        List<IssueSubListDTO> subIssueDTOList = new ArrayList<>(issueDOList.size());
        List<Long> assigneeIds = issueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        issueDOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            IssueSubListDTO subIssueDTO = new IssueSubListDTO();
            BeanUtils.copyProperties(issueDO, subIssueDTO);
            subIssueDTO.setAssigneeName(assigneeName);
            subIssueDTO.setImageUrl(imageUrl);
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
        issueSubDTO.setIssueAttachmentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueAttachmentDOList(), IssueAttachmentDTO.class));
        issueSubDTO.setIssueCommentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueCommentDOList(), IssueCommentDTO.class));
        List<Long> assigneeIdList = Arrays.asList(issueDetailDO.getAssigneeId(), issueDetailDO.getReporterId());
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        String assigneeName = userMessageDOMap.get(issueSubDTO.getAssigneeId()) != null ? userMessageDOMap.get(issueSubDTO.getAssigneeId()).getName() : null;
        String reporterName = userMessageDOMap.get(issueSubDTO.getReporterId()) != null ? userMessageDOMap.get(issueSubDTO.getReporterId()).getName() : null;
        issueSubDTO.setAssigneeName(assigneeName);
        issueSubDTO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueSubDTO.getAssigneeId()).getImageUrl() : null);
        issueSubDTO.setReporterName(reporterName);
        issueSubDTO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueSubDTO.getReporterId()).getImageUrl() : null);
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
            String reportName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
            ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
            BeanUtils.copyProperties(issueDO, exportIssuesDTO);
            exportIssuesDTO.setAssigneeName(assigneeName);
            exportIssuesDTO.setPriorityName(priorityDTOMap.get(issueDO.getPriorityId()).getName());
            exportIssuesDTO.setStatusName(statusMapDTOMap.get(issueDO.getStatusId()).getName());
            exportIssuesDTO.setTypeName(issueTypeDTOMap.get(issueDO.getIssueTypeId()).getName());
            exportIssuesDTO.setReporterName(reportName);
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
                String reporterName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
                String assigneeImageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
                String reporterImageUrl = reporterName != null ? usersMap.get(issueDO.getReporterId()).getImageUrl() : null;
                IssueComponentDetailDTO issueComponentDetailDTO = new IssueComponentDetailDTO();
                BeanUtils.copyProperties(issueDO, issueComponentDetailDTO);
                issueComponentDetailDTO.setAssigneeName(assigneeName);
                issueComponentDetailDTO.setReporterName(reporterName);
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

    public List<IssueListTestDTO> issueDoToIssueTestListDto(List<IssueDO> issueDOList, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<IssueListTestDTO> issueListTestDTOS = new ArrayList<>(issueDOList.size());
        Set<Long> userIds = issueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDO::getAssigneeId).collect(Collectors.toSet());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDOList.forEach(issueDO -> {
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
