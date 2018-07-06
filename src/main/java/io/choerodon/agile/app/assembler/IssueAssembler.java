package io.choerodon.agile.app.assembler;

import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.common.utils.ColorUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.agile.api.dto.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class IssueAssembler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LookupValueMapper lookupValueMapper;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;

    private static final String ISSUE_STATUS_COLOR = "issue_status_color";

    /**
     * issueDetailDO转换到IssueDTO
     *
     * @param issueDetailDO issueDetailDO
     * @return IssueDTO
     */
    public IssueDTO issueDetailDoToDto(IssueDetailDO issueDetailDO) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueDetailDO, issueDTO);
        issueDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueDTO.setActiveSprint(sprintNameAssembler.doToDTO(issueDetailDO.getActiveSprint()));
        issueDTO.setCloseSprint(sprintNameAssembler.doListToDTO(issueDetailDO.getCloseSprint()));
        issueDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueDTO.setLabelIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getLabelIssueRelDOList(), LabelIssueRelDTO.class));
        issueDTO.setIssueAttachmentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueAttachmentDOList(), IssueAttachmentDTO.class));
        issueDTO.setIssueCommentDTOList(ConvertHelper.convertList(issueDetailDO.getIssueCommentDOList(), IssueCommentDTO.class));
        issueDTO.setStatusColor(ColorUtil.initializationStatusColor(issueDTO.getStatusCode(), lookupValueMap));
        issueDTO.setSubIssueDTOList(issueDoToSubIssueDto(issueDetailDO.getSubIssueDOList(), lookupValueMap));
        List<Long> assigneeIdList = Arrays.asList(issueDetailDO.getAssigneeId(), issueDetailDO.getReporterId());
        Map<Long, UserMessageDO> userMessageDOMap = userRepository.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        String assigneeName = userMessageDOMap.get(issueDTO.getAssigneeId()) != null ? userMessageDOMap.get(issueDTO.getAssigneeId()).getName() : null;
        String reporterName = userMessageDOMap.get(issueDTO.getReporterId()) != null ? userMessageDOMap.get(issueDTO.getReporterId()).getName() : null;
        issueDTO.setAssigneeName(assigneeName);
        issueDTO.setAssigneeImageUrl(assigneeName != null ? userMessageDOMap.get(issueDTO.getAssigneeId()).getImageUrl() : null);
        issueDTO.setReporterName(reporterName);
        issueDTO.setReporterImageUrl(reporterName != null ? userMessageDOMap.get(issueDTO.getReporterId()).getImageUrl() : null);
        return issueDTO;
    }

    /**
     * issueDO转换到IssueListDTO
     *
     * @param issueDOList issueDetailDO
     * @return IssueListDTO
     */
    public List<IssueListDTO> issueDoToIssueListDto(List<IssueDO> issueDOList) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        List<IssueListDTO> issueListDTOList = new ArrayList<>();
        List<Long> assigneeIds = issueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        issueDOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            IssueListDTO issueListDTO = new IssueListDTO();
            BeanUtils.copyProperties(issueDO, issueListDTO);
            issueListDTO.setAssigneeName(assigneeName);
            issueListDTO.setStatusColor(ColorUtil.initializationStatusColor(issueListDTO.getStatusCode(), lookupValueMap));
            issueListDTO.setImageUrl(imageUrl);
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
    public List<IssueSubListDTO> issueDoToSubIssueDto(List<IssueDO> issueDOList, Map<String, String> lookupValueMap) {
        List<IssueSubListDTO> subIssueDTOList = new ArrayList<>();
        List<Long> assigneeIds = issueDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        issueDOList.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueDO.getAssigneeId()).getImageUrl() : null;
            IssueSubListDTO subIssueDTO = new IssueSubListDTO();
            BeanUtils.copyProperties(issueDO, subIssueDTO);
            subIssueDTO.setAssigneeName(assigneeName);
            subIssueDTO.setImageUrl(imageUrl);
            subIssueDTO.setStatusColor(ColorUtil.initializationStatusColor(subIssueDTO.getStatusCode(), lookupValueMap));
            subIssueDTOList.add(subIssueDTO);
        });
        return subIssueDTOList;
    }

    /**
     * IssueUpdateDTO转换到IssueE
     *
     * @param issueUpdateDTO issueUpdateDTO
     * @return IssueE
     */
    public IssueE issueUpdateDtoToEntity(IssueUpdateDTO issueUpdateDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueUpdateDTO, issueE);
        return issueE;
    }

    /**
     * issueCreateDTO转换到IssueE
     *
     * @param issueCreateDTO issueCreateDTO
     * @return IssueE
     */
    public IssueE issueCreateDtoToIssueE(IssueCreateDTO issueCreateDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueCreateDTO, issueE);
        return issueE;
    }

    /**
     * issueSubCreateDTO转换到IssueE
     *
     * @param issueSubCreateDTO issueSubCreateDTO
     * @return IssueE
     */
    public IssueE issueSubCreateDtoToEntity(IssueSubCreateDTO issueSubCreateDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueSubCreateDTO, issueE);
        return issueE;
    }

    /**
     * issueDO转换到IssueEpicDTO
     *
     * @param issueDOList issueDOList
     * @return IssueEpicDTO
     */
    public List<IssueEpicDTO> doListToEpicDto(List<IssueDO> issueDOList) {
        List<IssueEpicDTO> issueEpicDTOList = new ArrayList<>();
        issueDOList.forEach(issueDO -> {
            IssueEpicDTO issueEpicDTO = new IssueEpicDTO();
            BeanUtils.copyProperties(issueDO, issueEpicDTO);
            issueEpicDTOList.add(issueEpicDTO);
        });
        return issueEpicDTOList;
    }

    /**
     * issueDetailDO转换到IssueSubDTO
     *
     * @param issueDetailDO issueDetailDO
     * @return IssueSubDTO
     */
    public IssueSubDTO issueDetailDoToIssueSubDto(IssueDetailDO issueDetailDO) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        IssueSubDTO issueSubDTO = new IssueSubDTO();
        BeanUtils.copyProperties(issueDetailDO, issueSubDTO);
        issueSubDTO.setComponentIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getComponentIssueRelDOList(), ComponentIssueRelDTO.class));
        issueSubDTO.setVersionIssueRelDTOList(ConvertHelper.convertList(issueDetailDO.getVersionIssueRelDOList(), VersionIssueRelDTO.class));
        issueSubDTO.setActiveSprint(sprintNameAssembler.doToDTO(issueDetailDO.getActiveSprint()));
        issueSubDTO.setCloseSprint(sprintNameAssembler.doListToDTO(issueDetailDO.getCloseSprint()));
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
        issueSubDTO.setStatusColor(ColorUtil.initializationStatusColor(issueSubDTO.getStatusCode(), lookupValueMap));
        return issueSubDTO;
    }

    public List<ExportIssuesDTO> exportIssuesDOListToExportIssuesDTO(List<ExportIssuesDO> exportIssues) {
        List<ExportIssuesDTO> exportIssuesDTOS = new ArrayList<>();
        List<Long> userIds = exportIssues.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(ExportIssuesDO::getAssigneeId).distinct().collect(Collectors.toList());
        userIds.addAll(exportIssues.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(ExportIssuesDO::getReporterId).distinct().collect(Collectors.toList()));
        userIds = userIds.stream().distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(userIds, true);
        exportIssues.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String reportName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
            ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
            BeanUtils.copyProperties(issueDO, exportIssuesDTO);
            exportIssuesDTO.setAssigneeName(assigneeName);
            exportIssuesDTO.setReporterName(reportName);
            exportIssuesDTOS.add(exportIssuesDTO);
        });
        return exportIssuesDTOS;
    }

    public ExportIssuesDTO exportIssuesDOToExportIssuesDTO(ExportIssuesDO exportIssue) {
        if (exportIssue == null) {
            return null;
        }
        ExportIssuesDTO exportIssuesDTO = new ExportIssuesDTO();
        List<Long> userIds = new ArrayList<>();
        userIds.add(exportIssue.getAssigneeId());
        if (!Objects.equals(exportIssue.getAssigneeId(), exportIssue.getReporterId())) {
            userIds.add(exportIssue.getReporterId());
        }
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(userIds, true);
        String assigneeName = usersMap.get(exportIssue.getAssigneeId()) != null ? usersMap.get(exportIssue.getAssigneeId()).getName() : null;
        String reportName = usersMap.get(exportIssue.getReporterId()) != null ? usersMap.get(exportIssue.getReporterId()).getName() : null;
        BeanUtils.copyProperties(exportIssue, exportIssuesDTO);
        exportIssuesDTO.setAssigneeName(assigneeName);
        exportIssuesDTO.setReporterName(reportName);
        return exportIssuesDTO;
    }


    public List<IssueNumDTO> issueNumDOToIssueNumDTO(List<IssueNumDO> issueNumDOList) {
        List<IssueNumDTO> issueNumDTOList = new ArrayList<>();
        issueNumDOList.forEach(issueNumDO -> {
            IssueNumDTO issueNumDTO = new IssueNumDTO();
            BeanUtils.copyProperties(issueNumDO, issueNumDTO);
            issueNumDTOList.add(issueNumDTO);
        });
        return issueNumDTOList;
    }

    public IssueCreateDTO issueDtoToIssueCreateDto(IssueDetailDO issueDetailDO) {
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        BeanUtils.copyProperties(issueDetailDO, issueCreateDTO);
        issueCreateDTO.setSprintId(0L);
        issueCreateDTO.setComponentIssueRelDTOList(copyComponentIssueRel(issueDetailDO.getComponentIssueRelDOList()));
        issueCreateDTO.setVersionIssueRelDTOList(copyVersionIssueRel(issueDetailDO.getVersionIssueRelDOList()));
        issueCreateDTO.setLabelIssueRelDTOList(copyLabelIssueRel(issueDetailDO.getLabelIssueRelDOList(), issueDetailDO.getProjectId()));
        return issueCreateDTO;
    }

    private List<ComponentIssueRelDTO> copyComponentIssueRel(List<ComponentIssueRelDO> componentIssueRelDOList) {
        List<ComponentIssueRelDTO> componentIssueRelDTOList = new ArrayList<>();
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
        List<LabelIssueRelDTO> labelIssueRelDTOList = new ArrayList<>();
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
        List<VersionIssueRelDTO> versionIssueRelDTOList = new ArrayList<>();
        versionIssueRelDOList.forEach(versionIssueRelDO -> {
            VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
            BeanUtils.copyProperties(versionIssueRelDO, versionIssueRelDTO);
            versionIssueRelDTO.setIssueId(null);
            versionIssueRelDTOList.add(versionIssueRelDTO);
        });
        return versionIssueRelDTOList;
    }

}
