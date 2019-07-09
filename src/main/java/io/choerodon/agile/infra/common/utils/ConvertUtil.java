package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.exception.CommonException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/31
 */
public class ConvertUtil {

    private ConvertUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<Long, ProjectDTO> ORGANIZATION_MAP = new ConcurrentHashMap<>();

    /**
     * 根据projectId获取issue类型Map
     *
     * @param projectId projectId
     * @return IssueTypeMap
     */
    public static Map<Long, IssueTypeVO> getIssueTypeMap(Long projectId, String applyType) {
        List<IssueTypeVO> issueTypeVOS = SpringBeanUtil.getBean(IssueFeignClient.class).queryIssueTypesByProjectId(projectId, applyType).getBody();
        return issueTypeVOS.stream().collect(Collectors.toMap(IssueTypeVO::getId, Function.identity()));
    }

    /**
     * 根据projectId获取issue状态Map
     *
     * @param projectId projectId
     * @return StatusMap
     */
    public static Map<Long, StatusMapVO> getIssueStatusMap(Long projectId) {
        Long organizationId = getOrganizationId(projectId);
        return SpringBeanUtil.getBean(StateMachineFeignClient.class).queryAllStatusMap(organizationId).getBody();
    }

    /**
     * 根据projectId获取issue优先级Map
     *
     * @param projectId projectId
     * @return PriorityDTOMap
     */
    public static Map<Long, PriorityVO> getIssuePriorityMap(Long projectId) {
        Long organizationId = getOrganizationId(projectId);
        return SpringBeanUtil.getBean(IssueFeignClient.class).queryByOrganizationId(organizationId).getBody();
    }

    public static Long getOrganizationId(Long projectId) {
        return queryProject(projectId).getOrganizationId();
    }

    public static String getCode(Long projectId) {
        return queryProject(projectId).getCode();
    }

    public static String getName(Long projectId) {
        return queryProject(projectId).getName();
    }

    private static ProjectDTO queryProject(Long projectId) {
        ProjectDTO projectDTO = ORGANIZATION_MAP.get(projectId);
        if (projectDTO != null) {
            return projectDTO;
        } else {
            projectDTO = SpringBeanUtil.getBean(UserFeignClient.class).queryProject(projectId).getBody();
            if (projectDTO != null) {
                ORGANIZATION_MAP.put(projectId, projectDTO);
                return projectDTO;
            } else {
                throw new CommonException("error.queryProject.notFound");
            }
        }
    }

    public static Map<Long, IssueTypeWithStateMachineIdDTO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
        List<IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIdDTOS = SpringBeanUtil.getBean(IssueFeignClient.class).queryIssueTypesWithStateMachineIdByProjectId(projectId, applyType)
                .getBody();
        Map<Long, Long> statusIdMap = SpringBeanUtil.getBean(StateMachineFeignClient.class).queryInitStatusIds(getOrganizationId(projectId), issueTypeWithStateMachineIdDTOS
                .stream().map(IssueTypeWithStateMachineIdDTO::getStateMachineId).collect(Collectors.toList())).getBody();
        issueTypeWithStateMachineIdDTOS.forEach(issueTypeWithStateMachineIdDTO -> issueTypeWithStateMachineIdDTO.setInitStatusId(statusIdMap.get(issueTypeWithStateMachineIdDTO.getStateMachineId())));
        return issueTypeWithStateMachineIdDTOS.stream().collect(Collectors.toMap(IssueTypeWithStateMachineIdDTO::getId, Function.identity()));
    }


}