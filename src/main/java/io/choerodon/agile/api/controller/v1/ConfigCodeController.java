package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.ConfigCodeVO;
import io.choerodon.agile.app.service.ConfigCodeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author shinan.chen
 * @date 2018/10/10
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/config_code")
public class ConfigCodeController extends BaseController {

    @Autowired
    private ConfigCodeService configCodeService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取未配置的条件，验证，后置动作等列表")
    @GetMapping(value = "/{transform_id}")
    public ResponseEntity<List<ConfigCodeVO>> queryByTransformId(@PathVariable("organization_id") Long organizationId,
                                                                 @PathVariable("transform_id") Long transformId,
                                                                 @RequestParam String type) {
        return Optional.ofNullable(configCodeService.queryByTransformId(organizationId, transformId, type))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.configCode.queryByTransformId"));
    }

}
