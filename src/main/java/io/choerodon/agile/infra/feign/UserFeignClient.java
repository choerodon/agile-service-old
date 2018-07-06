package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = "iam-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    /**
     * 根据id查询用户信息
     *
     * @param userId userId
     * @return UserDO
     */
    @RequestMapping(value = "/v1/users/{user_id}/info", method = RequestMethod.GET)
    ResponseEntity<UserDO> queryUserById(@PathVariable(name = "user_id") Long userId);

    @RequestMapping(value = "/v1/users/ids", method = RequestMethod.POST)
    ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids);

    /**
     * 按照Id查询项目
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @GetMapping(value = "/v1/projects/{id}")
    ResponseEntity<ProjectDTO> queryProject(@PathVariable("id") Long id);
}

