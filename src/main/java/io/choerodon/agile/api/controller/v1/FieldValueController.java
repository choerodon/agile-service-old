package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.FieldValueService;
import io.choerodon.agile.app.service.ObjectSchemeFieldService;
import io.choerodon.agile.app.service.PageFieldService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/field_value")
public class FieldValueController {

    @Autowired
    private PageFieldService pageFieldService;
    @Autowired
    private FieldValueService fieldValueService;
    @Autowired
    private ObjectSchemeFieldService objectSchemeFieldService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "界面上获取字段列表，带有字段选项")
    @PostMapping("/list")
    public ResponseEntity<List<PageFieldViewVO>> queryPageFieldViewList(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable("project_id") Long projectId,
                                                                        @ApiParam(value = "组织id", required = true)
                                                                        @RequestParam Long organizationId,
                                                                        @ApiParam(value = "参数对象", required = true)
                                                                        @RequestBody @Valid PageFieldViewParamVO paramDTO) {
        return new ResponseEntity<>(pageFieldService.queryPageFieldViewList(organizationId, projectId, paramDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据实例id从界面上获取字段列表，带有字段值、字段选项")
    @PostMapping("/list/{instance_id}")
    public ResponseEntity<List<PageFieldViewVO>> queryPageFieldViewListWithInstanceId(@ApiParam(value = "项目id", required = true)
                                                                                      @PathVariable("project_id") Long projectId,
                                                                                      @ApiParam(value = "实例id", required = true)
                                                                                      @PathVariable("instance_id") Long instanceId,
                                                                                      @ApiParam(value = "组织id", required = true)
                                                                                      @RequestParam Long organizationId,
                                                                                      @ApiParam(value = "参数对象", required = true)
                                                                                      @RequestBody @Valid PageFieldViewParamVO paramDTO) {
        return new ResponseEntity<>(pageFieldService.queryPageFieldViewListWithInstanceId(organizationId, projectId, instanceId, paramDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "创建实例时，批量创建字段值")
    @PostMapping("/{instance_id}")
    public ResponseEntity createFieldValues(@ApiParam(value = "项目id", required = true)
                                            @PathVariable("project_id") Long projectId,
                                            @ApiParam(value = "实例id", required = true)
                                            @PathVariable("instance_id") Long instanceId,
                                            @ApiParam(value = "组织id", required = true)
                                            @RequestParam Long organizationId,
                                            @ApiParam(value = "方案编码", required = true)
                                            @RequestParam String schemeCode,
                                            @ApiParam(value = "自定义字段列表（包含值）", required = true)
                                            @RequestBody List<PageFieldViewCreateVO> createDTOs) {
        fieldValueService.createFieldValues(organizationId, projectId, instanceId, schemeCode, createDTOs);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "快速创建实例时，批量创建字段值（默认值）")
    @PostMapping("/quick_create/{instance_id}")
    public ResponseEntity createFieldValuesWithQuickCreate(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable("project_id") Long projectId,
                                                           @ApiParam(value = "实例id", required = true)
                                                           @PathVariable("instance_id") Long instanceId,
                                                           @ApiParam(value = "组织id", required = true)
                                                           @RequestParam Long organizationId,
                                                           @ApiParam(value = "参数对象", required = true)
                                                           @RequestBody @Valid PageFieldViewParamVO paramDTO) {
        fieldValueService.createFieldValuesWithQuickCreate(organizationId, projectId, instanceId, paramDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "保存值/修改值")
    @PostMapping("/update/{instance_id}")
    public ResponseEntity<List<FieldValueVO>> updateFieldValue(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable("project_id") Long projectId,
                                                               @ApiParam(value = "组织id", required = true)
                                                               @RequestParam Long organizationId,
                                                               @ApiParam(value = "实例id", required = true)
                                                               @PathVariable("instance_id") Long instanceId,
                                                               @ApiParam(value = "字段id", required = true)
                                                               @RequestParam Long fieldId,
                                                               @ApiParam(value = "方案编码", required = true)
                                                               @RequestParam String schemeCode,
                                                               @ApiParam(value = "值对象列表", required = true)
                                                               @RequestBody PageFieldViewUpdateVO updateDTO) {
        return new ResponseEntity<>(fieldValueService.updateFieldValue(organizationId, projectId, instanceId, fieldId, schemeCode, updateDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "【敏捷专用】问题管理界面获取自定义字段表头")
    @GetMapping("/list/getFields")
    public ResponseEntity<List<AgileIssueHeadVO>> getIssueHeadForAgile(@ApiParam(value = "项目id", required = true)
                                                                       @PathVariable("project_id") Long projectId,
                                                                       @ApiParam(value = "组织id", required = true)
                                                                       @RequestParam Long organizationId,
                                                                       @ApiParam(value = "方案编码", required = true)
                                                                       @RequestParam String schemeCode) {
        return new ResponseEntity<>(objectSchemeFieldService.getIssueHeadForAgile(organizationId, projectId, schemeCode), HttpStatus.OK);
    }
}
