package io.choerodon.agile.app.assembler;

import com.google.common.collect.Lists;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * issueDetailDO转换到IssueDTO
     *
     * @param issueDetailDTO issueDetailDTO
     * @return IssueVO
     */
    public IssueVO issueDetailDoToDto(IssueDetailDTO issueDetailDTO, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        IssueVO issueVO = new IssueVO();
        BeanUtils.copyProperties(issueDetailDTO, issueVO);
        issueVO.setFeatureVO(issueDetailDTO.getFeatureDTO() != null ? modelMapper.map(issueDetailDTO.getFeatureDTO(), FeatureVO.class) : null);
        issueVO.setComponentIssueRelVOList(modelMapper.map(issueDetailDTO.getComponentIssueRelDTOList(), new TypeToken<List<ComponentIssueRelVO>>(){}.getType()));
        issueVO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDTO.getActiveSprint(), SprintNameVO.class));
        issueVO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDTO.getCloseSprint(), SprintNameVO.class));
        issueVO.setActivePi(sprintNameAssembler.toTarget(issueDetailDTO.getActivePi(), PiNameVO.class));
        issueVO.setClosePi(sprintNameAssembler.toTargetList(issueDetailDTO.getClosePi(), PiNameVO.class));
        issueVO.setVersionIssueRelVOList(modelMapper.map(issueDetailDTO.getVersionIssueRelDTOList(), new TypeToken<List<VersionIssueRelVO>>(){}.getType()));
        issueVO.setLabelIssueRelVOList(modelMapper.map(issueDetailDTO.getLabelIssueRelDTOList(), new TypeToken<List<LabelIssueRelVO>>(){}.getType()));
        issueVO.setIssueAttachmentVOList(modelMapper.map(issueDetailDTO.getIssueAttachmentDTOList(), new TypeToken<List<IssueAttachmentVO>>(){}.getType()));
        issueVO.setIssueCommentVOList(modelMapper.map(issueDetailDTO.getIssueCommentDTOList(), new TypeToken<List<IssueCommentVO>>(){}.getType()));
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
        Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
                assigneeIdList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), true);
        UserMessageDTO assigneeUserDO = userMessageDOMap.get(issueVO.getAssigneeId());
        UserMessageDTO reporterUserDO = userMessageDOMap.get(issueVO.getReporterId());
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
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDTO assigneeUserDO = usersMap.get(issueDO.getAssigneeId());
            UserMessageDTO reporterUserDO = usersMap.get(issueDO.getReporterId());
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
            issueListFieldKVVO.setVersionIssueRelVOS(toTargetList(issueDO.getVersionIssueRelDTOS(), VersionIssueRelVO.class));
            issueListFieldKVVO.setIssueComponentBriefVOS(toTargetList(issueDO.getIssueComponentBriefDTOS(), IssueComponentBriefVO.class));
            issueListFieldKVVO.setIssueSprintVOS(toTargetList(issueDO.getIssueSprintDTOS(), IssueSprintVO.class));
            issueListFieldKVVO.setLabelIssueRelVOS(toTargetList(issueDO.getLabelIssueRelDTOS(), LabelIssueRelVO.class));
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
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDTO assigneeUserDO = usersMap.get(issueDO.getAssigneeId());
            UserMessageDTO reporterUserDO = usersMap.get(issueDO.getReporterId());
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
            issueListVO.setVersionIssueRelVOS(toTargetList(issueDO.getVersionIssueRelDTOS(), VersionIssueRelVO.class));
            issueListVO.setIssueComponentBriefVOS(toTargetList(issueDO.getIssueComponentBriefDTOS(), IssueComponentBriefVO.class));
            issueListVO.setIssueSprintVOS(toTargetList(issueDO.getIssueSprintDTOS(), IssueSprintVO.class));
            issueListVO.setLabelIssueRelVOS(toTargetList(issueDO.getLabelIssueRelDTOS(), LabelIssueRelVO.class));
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
    private List<IssueSubListVO> issueDoToSubIssueDto(List<IssueDTO> issueDTOList, Map<Long, IssueTypeVO> issueTypeDTOMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, PriorityVO> priorityDTOMap) {
        List<IssueSubListVO> subIssueDTOList = new ArrayList<>(issueDTOList.size());
        List<Long> assigneeIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(assigneeIds, true);
        issueDTOList.forEach(issueDO -> {
            UserMessageDTO userMessageDTO = usersMap.get(issueDO.getAssigneeId());
            String assigneeName = userMessageDTO != null ? userMessageDTO.getName() : null;
            String imageUrl = userMessageDTO != null ? userMessageDTO.getImageUrl() : null;
            String loginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
            String realName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
            IssueSubListVO subIssueDTO = new IssueSubListVO();
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
        issueSubVO.setComponentIssueRelVOList(modelMapper.map(issueDetailDTO.getComponentIssueRelDTOList(), new TypeToken<List<ComponentIssueRelVO>>(){}.getType()));
        issueSubVO.setVersionIssueRelVOList(modelMapper.map(issueDetailDTO.getVersionIssueRelDTOList(), new TypeToken<List<VersionIssueRelVO>>(){}.getType()));
        issueSubVO.setActiveSprint(sprintNameAssembler.toTarget(issueDetailDTO.getActiveSprint(), SprintNameVO.class));
        issueSubVO.setCloseSprint(sprintNameAssembler.toTargetList(issueDetailDTO.getCloseSprint(), SprintNameVO.class));
        issueSubVO.setLabelIssueRelVOList(modelMapper.map(issueDetailDTO.getLabelIssueRelDTOList(), new TypeToken<List<LabelIssueRelVO>>(){}.getType()));
        issueSubVO.setIssueAttachmentVOList(modelMapper.map(issueDetailDTO.getIssueAttachmentDTOList(), new TypeToken<List<IssueAttachmentVO>>(){}.getType()));
        issueSubVO.setIssueCommentVOList(modelMapper.map(issueDetailDTO.getIssueCommentDTOList(), new TypeToken<List<IssueCommentVO>>(){}.getType()));
        List<Long> assigneeIdList = new ArrayList<>();
        assigneeIdList.add(issueDetailDTO.getAssigneeId());
        assigneeIdList.add(issueDetailDTO.getReporterId());
        assigneeIdList.add(issueDetailDTO.getCreatedBy());
        Boolean issueCommentCondition = issueSubVO.getIssueCommentVOList() != null && !issueSubVO.getIssueCommentVOList().isEmpty();
        if (issueCommentCondition) {
            assigneeIdList.addAll(issueSubVO.getIssueCommentVOList().stream().map(IssueCommentVO::getUserId).collect(Collectors.toList()));
        }
        Map<Long, UserMessageDTO> userMessageDOMap = userService.queryUsersMap(
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

    public List<ExportIssuesVO> exportIssuesDOListToExportIssuesDTO(List<ExportIssuesDTO> exportIssues, Long projectId) {
        List<ExportIssuesVO> exportIssuesVOS = new ArrayList<>(exportIssues.size());
        Set<Long> userIds = exportIssues.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(ExportIssuesDTO::getAssigneeId).collect(Collectors.toSet());
        userIds.addAll(exportIssues.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).map(ExportIssuesDTO::getReporterId).collect(Collectors.toSet()));
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(new ArrayList<>(userIds), true);
        Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
        Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
        exportIssues.forEach(issueDO -> {
            String assigneeName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getName() : null;
            String assigneeRealName = usersMap.get(issueDO.getAssigneeId()) != null ? usersMap.get(issueDO.getAssigneeId()).getRealName() : null;
            String reporterName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getName() : null;
            String reporterRealName = usersMap.get(issueDO.getReporterId()) != null ? usersMap.get(issueDO.getReporterId()).getRealName() : null;
            ExportIssuesVO exportIssuesVO = new ExportIssuesVO();
            BeanUtils.copyProperties(issueDO, exportIssuesVO);
            exportIssuesVO.setPriorityName(priorityDTOMap.get(issueDO.getPriorityId()) == null ? null : priorityDTOMap.get(issueDO.getPriorityId()).getName());
            exportIssuesVO.setStatusName(statusMapDTOMap.get(issueDO.getStatusId()) == null ? null : statusMapDTOMap.get(issueDO.getStatusId()).getName());
            exportIssuesVO.setTypeName(issueTypeDTOMap.get(issueDO.getIssueTypeId()) == null ? null : issueTypeDTOMap.get(issueDO.getIssueTypeId()).getName());
            exportIssuesVO.setAssigneeName(assigneeName);
            exportIssuesVO.setAssigneeRealName(assigneeRealName);
            exportIssuesVO.setReporterName(reporterName);
            exportIssuesVO.setReporterRealName(reporterRealName);
            exportIssuesVOS.add(exportIssuesVO);
        });
        return exportIssuesVOS;
    }

    public IssueCreateVO issueDtoToIssueCreateDto(IssueDetailDTO issueDetailDTO) {
        IssueCreateVO issueCreateVO = new IssueCreateVO();
        BeanUtils.copyProperties(issueDetailDTO, issueCreateVO);
        issueCreateVO.setSprintId(null);
        issueCreateVO.setRemainingTime(null);
        issueCreateVO.setComponentIssueRelVOList(copyComponentIssueRel(issueDetailDTO.getComponentIssueRelDTOList()));
        issueCreateVO.setVersionIssueRelVOList(copyVersionIssueRel(issueDetailDTO.getVersionIssueRelDTOList()));
        issueCreateVO.setLabelIssueRelVOList(copyLabelIssueRel(issueDetailDTO.getLabelIssueRelDTOList(), issueDetailDTO.getProjectId()));
        return issueCreateVO;
    }

    public IssueSubCreateVO issueDtoToIssueSubCreateDto(IssueDetailDTO issueDetailDTO) {
        IssueSubCreateVO issueSubCreateVO = new IssueSubCreateVO();
        BeanUtils.copyProperties(issueDetailDTO, issueSubCreateVO);
        issueSubCreateVO.setSprintId(null);
        issueSubCreateVO.setRemainingTime(null);
        issueSubCreateVO.setComponentIssueRelVOList(copyComponentIssueRel(issueDetailDTO.getComponentIssueRelDTOList()));
        issueSubCreateVO.setVersionIssueRelVOList(copyVersionIssueRel(issueDetailDTO.getVersionIssueRelDTOList()));
        issueSubCreateVO.setLabelIssueRelVOList(copyLabelIssueRel(issueDetailDTO.getLabelIssueRelDTOList(), issueDetailDTO.getProjectId()));
        return issueSubCreateVO;
    }

    private List<ComponentIssueRelVO> copyComponentIssueRel(List<ComponentIssueRelDTO> componentIssueRelDTOList) {
        List<ComponentIssueRelVO> componentIssueRelVOList = new ArrayList<>(componentIssueRelDTOList.size());
        componentIssueRelDTOList.forEach(componentIssueRelDO -> {
            ComponentIssueRelVO componentIssueRelVO = new ComponentIssueRelVO();
            BeanUtils.copyProperties(componentIssueRelDO, componentIssueRelVO);
            componentIssueRelVO.setIssueId(null);
            componentIssueRelVO.setObjectVersionNumber(null);
            componentIssueRelVOList.add(componentIssueRelVO);
        });
        return componentIssueRelVOList;
    }

    private List<LabelIssueRelVO> copyLabelIssueRel(List<LabelIssueRelDTO> labelIssueRelDTOList, Long projectId) {
        List<LabelIssueRelVO> labelIssueRelVOList = new ArrayList<>(labelIssueRelDTOList.size());
        labelIssueRelDTOList.forEach(labelIssueRelDO -> {
            LabelIssueRelVO labelIssueRelVO = new LabelIssueRelVO();
            BeanUtils.copyProperties(labelIssueRelDO, labelIssueRelVO);
            labelIssueRelVO.setIssueId(null);
            labelIssueRelVO.setLabelName(null);
            labelIssueRelVO.setObjectVersionNumber(null);
            labelIssueRelVO.setProjectId(projectId);
            labelIssueRelVOList.add(labelIssueRelVO);
        });
        return labelIssueRelVOList;
    }

    private List<VersionIssueRelVO> copyVersionIssueRel(List<VersionIssueRelDTO> versionIssueRelDTOList) {
        List<VersionIssueRelVO> versionIssueRelVOList = new ArrayList<>(versionIssueRelDTOList.size());
        versionIssueRelDTOList.forEach(versionIssueRelDO -> {
            VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
            BeanUtils.copyProperties(versionIssueRelDO, versionIssueRelVO);
            versionIssueRelVO.setIssueId(null);
            versionIssueRelVOList.add(versionIssueRelVO);
        });
        return versionIssueRelVOList;
    }

    public IssueSubCreateVO issueDtoToSubIssueCreateDto(IssueDetailDTO subIssueDetailDTO, Long parentIssueId) {
        IssueSubCreateVO issueCreateDTO = new IssueSubCreateVO();
        BeanUtils.copyProperties(subIssueDetailDTO, issueCreateDTO);
        String subSummary = "CLONE-" + subIssueDetailDTO.getSummary();
        issueCreateDTO.setSummary(subSummary);
        issueCreateDTO.setSprintId(null);
        issueCreateDTO.setIssueNum(null);
        issueCreateDTO.setParentIssueId(parentIssueId);
        issueCreateDTO.setComponentIssueRelVOList(copyComponentIssueRel(subIssueDetailDTO.getComponentIssueRelDTOList()));
        issueCreateDTO.setVersionIssueRelVOList(copyVersionIssueRel(subIssueDetailDTO.getVersionIssueRelDTOList()));
        issueCreateDTO.setLabelIssueRelVOList(copyLabelIssueRel(subIssueDetailDTO.getLabelIssueRelDTOList(), subIssueDetailDTO.getProjectId()));
        return issueCreateDTO;
    }

    public List<IssueComponentDetailDTO> issueComponentDetailDoToDto(Long projectId, List<IssueComponentDetailInfoDTO> issueComponentDetailInfoDTOS) {
        List<IssueComponentDetailDTO> issueComponentDetailDTOS = new ArrayList<>(issueComponentDetailInfoDTOS.size());
        if (!issueComponentDetailInfoDTOS.isEmpty()) {
            List<Long> userIds = issueComponentDetailInfoDTOS.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueComponentDetailInfoDTO::getAssigneeId).collect(Collectors.toList());
            userIds.addAll(issueComponentDetailInfoDTOS.stream().filter(issue -> issue.getReporterId() != null && !Objects.equals(issue.getReporterId(), 0L)).
                    map(IssueComponentDetailInfoDTO::getReporterId).collect(Collectors.toList()));
            Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(userIds.stream().distinct().collect(Collectors.toList()), true);
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.TEST);
            Map<Long, IssueTypeVO> issueTypeDTOMapAgile = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            issueTypeDTOMap.putAll(issueTypeDTOMapAgile);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            issueComponentDetailInfoDTOS.parallelStream().forEachOrdered(issueDO -> {
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
                issueComponentDetailDTO.setComponentIssueRelVOList(modelMapper.map(issueDO.getComponentIssueRelDTOList(), new TypeToken<List<ComponentIssueRelVO>>(){}.getType()));
                issueComponentDetailDTO.setVersionIssueRelVOList(modelMapper.map(issueDO.getVersionIssueRelDTOList(),new TypeToken<List<VersionIssueRelVO>>(){}.getType()));
                issueComponentDetailDTO.setLabelIssueRelVOList(modelMapper.map(issueDO.getLabelIssueRelDTOList(), new TypeToken<List<LabelIssueRelVO>>(){}.getType()));
                issueComponentDetailDTOS.add(issueComponentDetailDTO);
            });
        }
        return issueComponentDetailDTOS;
    }

    public List<IssueListTestVO> issueDoToIssueTestListDto(List<IssueDTO> issueDTOList, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<IssueListTestVO> issueListTestVOS = new ArrayList<>(issueDTOList.size());
        Set<Long> userIds = issueDTOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueDTO::getAssigneeId).collect(Collectors.toSet());
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(Lists.newArrayList(userIds), true);
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

    public List<UnfinishedIssueVO> unfinishedIssueDoToDto(List<UnfinishedIssueDTO> unfinishedIssueDTOS, Long projectId) {
        List<UnfinishedIssueVO> unfinishedIssueVOS = new ArrayList<>(unfinishedIssueDTOS.size());
        if (!unfinishedIssueDTOS.isEmpty()) {
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            Map<Long, StatusMapVO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityVO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            unfinishedIssueDTOS.forEach(unfinishedIssueDTO -> {
                UnfinishedIssueVO unfinishedIssueVO = toTarget(unfinishedIssueDTO, UnfinishedIssueVO.class);
                unfinishedIssueVO.setIssueTypeVO(issueTypeDTOMap.get(unfinishedIssueDTO.getIssueTypeId()));
                unfinishedIssueVO.setStatusMapVO(statusMapDTOMap.get(unfinishedIssueDTO.getStatusId()));
                unfinishedIssueVO.setPriorityVO(priorityDTOMap.get(unfinishedIssueDTO.getPriorityId()));
                unfinishedIssueVOS.add(unfinishedIssueVO);
            });

        }
        return unfinishedIssueVOS;
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
