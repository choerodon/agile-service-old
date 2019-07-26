package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.LookupTypeVO;
import io.choerodon.agile.app.service.LookupTypeService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/lookup_types")
public class LookupTypeController {


    @Autowired
    private LookupTypeService lookupTypeService;

    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @ApiOperation("查询所有lookup type类型")
    @GetMapping
    public ResponseEntity<List<LookupTypeVO>> listLookupType(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(lookupTypeService.listLookupType(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.lookupTypeList.get"));
    }

}