package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.vo.ObjectSchemeFieldCreateVO;
import io.choerodon.agile.api.vo.ObjectSchemeFieldDetailVO;
import io.choerodon.agile.api.vo.ObjectSchemeFieldUpdateVO;
import io.choerodon.agile.app.service.ObjectSchemeFieldService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}/object_scheme_field")
public class ObjectSchemeFieldController {

    @Autowired
    private ObjectSchemeFieldService objectSchemeFieldService;

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "根据方案编码获取字段列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listQuery(@ApiParam(value = "组织id", required = true)
                                                         @PathVariable("organization_id") Long organizationId,
                                                         @ApiParam(value = "方案编码", required = true)
                                                         @RequestParam String schemeCode) {
        return new ResponseEntity<>(objectSchemeFieldService.listQuery(organizationId, null, schemeCode), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "创建字段")
    @PostMapping
    public ResponseEntity<ObjectSchemeFieldDetailVO> create(@ApiParam(value = "组织id", required = true)
                                                             @PathVariable("organization_id") Long organizationId,
                                                            @ApiParam(value = "字段对象", required = true)
                                                             @RequestBody @Valid ObjectSchemeFieldCreateVO fieldCreateDTO) {
        return new ResponseEntity<>(objectSchemeFieldService.create(organizationId, null, fieldCreateDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "查询字段详情")
    @GetMapping(value = "/{field_id}")
    public ResponseEntity<ObjectSchemeFieldDetailVO> queryById(@ApiParam(value = "组织id", required = true)
                                                                @PathVariable("organization_id") Long organizationId,
                                                               @ApiParam(value = "字段id", required = true)
                                                                @PathVariable("field_id") Long fieldId) {
        return new ResponseEntity<>(objectSchemeFieldService.queryById(organizationId, null, fieldId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "删除字段")
    @DeleteMapping(value = "/{field_id}")
    public ResponseEntity delete(@ApiParam(value = "组织id", required = true)
                                 @PathVariable("organization_id") Long organizationId,
                                 @PathVariable("field_id") Long fieldId) {
        objectSchemeFieldService.delete(organizationId, null, fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "修改字段")
    @PutMapping(value = "/{field_id}")
    public ResponseEntity<ObjectSchemeFieldDetailVO> update(@ApiParam(value = "组织id", required = true)
                                                             @PathVariable("organization_id") Long organizationId,
                                                            @ApiParam(value = "字段id", required = true)
                                                             @PathVariable("field_id") Long fieldId,
                                                            @RequestBody @Valid ObjectSchemeFieldUpdateVO updateDTO) {
        return new ResponseEntity<>(objectSchemeFieldService.update(organizationId, null, fieldId, updateDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "校验字段名称是否已使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "组织id", required = true)
                                             @PathVariable("organization_id") Long organizationId,
                                             @ApiParam(value = "字段名称", required = true)
                                             @RequestParam("name") String name,
                                             @ApiParam(value = "方案编码", required = true)
                                             @RequestParam String schemeCode) {
        return new ResponseEntity<>(objectSchemeFieldService.checkName(organizationId, null, name, schemeCode), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "校验字段编码是否已使用")
    @GetMapping(value = "/check_code")
    public ResponseEntity<Boolean> checkCode(@ApiParam(value = "组织id", required = true)
                                             @PathVariable("organization_id") Long organizationId,
                                             @ApiParam(value = "字段编码", required = true)
                                             @RequestParam("code") String code,
                                             @ApiParam(value = "方案编码", required = true)
                                             @RequestParam String schemeCode) {
        return new ResponseEntity<>(objectSchemeFieldService.checkCode(organizationId, null, code, schemeCode), HttpStatus.OK);
    }
}
