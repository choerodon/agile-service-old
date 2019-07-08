package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.LookupTypeWithValuesDTO;
import io.choerodon.agile.app.service.LookupValueService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/lookup_values")
public class LookupValueController {

    @Autowired
    private LookupValueService lookupValueService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据type code查询其下的value值")
    @GetMapping(value = "/{typeCode}")
    public ResponseEntity<LookupTypeWithValuesDTO> queryLookupValueByCode(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          @ApiParam(value = "type code", required = true)
                                                                          @PathVariable String typeCode) {
        return Optional.ofNullable(lookupValueService.queryLookupValueByCode(projectId, typeCode))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.lookupValueList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询列约束下的value值")
    @GetMapping(value = "/constraint/list")
    public ResponseEntity<LookupTypeWithValuesDTO> queryConstraintLookupValue(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(lookupValueService.queryConstraintLookupValue(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.lookupValueList.get"));
    }

}