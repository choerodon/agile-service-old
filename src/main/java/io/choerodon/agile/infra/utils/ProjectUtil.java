package io.choerodon.agile.infra.utils;

import io.choerodon.agile.api.vo.ProjectVO;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/7
 * 通过projectId获取organizationId
 */
@Component
public class ProjectUtil {
    @Autowired
    private IamFeignClient iamFeignClient;

    protected static final Map<Long, ProjectVO> map = new HashMap<>();

    public Long getOrganizationId(Long projectId) {
        return queryProject(projectId).getOrganizationId();
    }

    public String getCode(Long projectId) {
        return queryProject(projectId).getCode();
    }

    public String getName(Long projectId) {
        return queryProject(projectId).getName();
    }

    private ProjectVO queryProject(Long projectId) {
        ProjectVO projectVO = map.get(projectId);
        if (projectVO != null) {
            return projectVO;
        } else {
            projectVO = iamFeignClient.queryProject(projectId).getBody();
            if (projectVO != null) {
                map.put(projectId, projectVO);
                return projectVO;
            } else {
                throw new CommonException("error.queryProject.notFound");
            }
        }
    }
}
