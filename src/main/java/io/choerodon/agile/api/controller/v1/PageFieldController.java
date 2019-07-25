package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.vo.AdjustOrderVO;
import io.choerodon.agile.api.vo.PageFieldUpdateVO;
import io.choerodon.agile.api.vo.PageFieldVO;
import io.choerodon.agile.app.service.PageFieldService;
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
 * @since 2019/4/1
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}/page_field")
public class PageFieldController {

    @Autowired
    private PageFieldService pageFieldService;

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "根据页面编码获取页面字段列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listQuery(@ApiParam(value = "组织id", required = true)
                                                         @PathVariable("organization_id") Long organizationId,
                                                         @ApiParam(value = "页面编码", required = true)
                                                         @RequestParam String pageCode,
                                                         @ApiParam(value = "显示层级")
                                                         @RequestParam(required = false) String context) {
        return new ResponseEntity<>(pageFieldService.listQuery(organizationId, null, pageCode, context), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "调整顺序")
    @PostMapping(value = "/adjust_order")
    public ResponseEntity<PageFieldVO> adjustFieldOrder(@ApiParam(value = "组织id", required = true)
                                                         @PathVariable("organization_id") Long organizationId,
                                                        @ApiParam(value = "页面编码", required = true)
                                                         @RequestParam String pageCode,
                                                        @ApiParam(value = "调整顺序对象", required = true)
                                                         @RequestBody AdjustOrderVO adjustOrder) {
        return new ResponseEntity<>(pageFieldService.adjustFieldOrder(organizationId, null, pageCode, adjustOrder), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "修改页面字段")
    @PutMapping(value = "/{field_id}")
    public ResponseEntity<PageFieldVO> update(@ApiParam(value = "组织id", required = true)
                                               @PathVariable("organization_id") Long organizationId,
                                              @ApiParam(value = "字段id", required = true)
                                               @PathVariable("field_id") Long fieldId,
                                              @ApiParam(value = "页面编码", required = true)
                                               @RequestParam String pageCode,
                                              @ApiParam(value = "更新对象", required = true)
                                               @RequestBody @Valid PageFieldUpdateVO updateDTO) {
        return new ResponseEntity<>(pageFieldService.update(organizationId, null, pageCode, fieldId, updateDTO), HttpStatus.CREATED);
    }
}
