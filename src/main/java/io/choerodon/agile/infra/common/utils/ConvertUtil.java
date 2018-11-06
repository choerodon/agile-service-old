package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
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

    private static final Map<Long, ProjectDTO> ORGANIZATION_MAP = new ConcurrentHashMap<>();

    /**
     * 根据projectId获取issue类型Map
     *
     * @param projectId projectId
     * @return IssueTypeMap
     */
    public static Map<Long, IssueTypeDTO> getIssueTypeMap(Long projectId, String serviceCode) {
        if (serviceCode == null) {
            serviceCode = SchemeApplyType.AGILE;
        }
        List<IssueTypeDTO> issueTypeDTOS = SpringBeanUtil.getBean(IssueFeignClient.class).queryIssueTypesByProjectId(projectId, serviceCode).getBody();
        return issueTypeDTOS.stream().collect(Collectors.toMap(IssueTypeDTO::getId, Function.identity()));
    }

    /**
     * 根据projectId获取issue状态Map
     *
     * @param projectId projectId
     * @return StatusMap
     */
    public static Map<Long, StatusMapDTO> getIssueStatusMap(Long projectId) {
        Long organizationId = getOrganizationId(projectId);
        return SpringBeanUtil.getBean(StateMachineFeignClient.class).queryAllStatusMap(organizationId).getBody();
    }

    /**
     * 根据projectId获取issue优先级Map
     *
     * @param projectId projectId
     * @return PriorityDTOMap
     */
    public static Map<Long, PriorityDTO> getIssuePriorityMap(Long projectId) {
        Long organizationId = getOrganizationId(projectId);
        return SpringBeanUtil.getBean(IssueFeignClient.class).queryByOrganizationId(organizationId).getBody();
    }

    public static Long getOrganizationId(Long projectId) {
        return queryProject(projectId).getOrganizationId();
    }

    public static String getCode(Long projectId) {
        return queryProject(projectId).getCode();
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


}