package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.validator.PriorityValidator;
import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.infra.utils.ParamUtils;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author cong.cheng
 * @date 2018/8/21
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/priority")
public class PriorityController {
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private PriorityValidator priorityValidator;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "展示页面")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<List<PriorityVO>> selectAll(@PathVariable("organization_id") Long organizationId,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String description,
                                                      @RequestParam(required = false) String colour,
                                                      @RequestParam(required = false) Boolean isDefault,
                                                      @RequestParam(required = false) String[] param) {
        PriorityVO priorityVO = new PriorityVO();
        priorityVO.setOrganizationId(organizationId);
        priorityVO.setName(name);
        priorityVO.setDescription(description);
        priorityVO.setColour(colour);
        priorityVO.setDefault(isDefault);
        return new ResponseEntity<>(priorityService.selectAll(priorityVO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建优先级")
    @PostMapping
    public ResponseEntity<PriorityVO> create(@PathVariable("organization_id") Long organizationId, @RequestBody PriorityVO priorityVO) {
        priorityValidator.createValidate(priorityVO);
        return new ResponseEntity<>(priorityService.create(organizationId, priorityVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新优先级")
    @PutMapping(value = "/{priority_id}")
    public ResponseEntity<PriorityVO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("priority_id") Long priorityId,
                                             @RequestBody @Valid PriorityVO priorityVO) {
        priorityVO.setId(priorityId);
        priorityVO.setOrganizationId(organizationId);
        priorityValidator.updateValidate(priorityVO);
        return new ResponseEntity<>(priorityService.update(priorityVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验优先级名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name) {
        return Optional.ofNullable(priorityService.checkName(organizationId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priorityName.check"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新展示顺序")
    @PutMapping(value = "/sequence")
    public ResponseEntity<List<PriorityVO>> updateByList(@PathVariable("organization_id") Long organizationId,
                                                         @RequestBody List<PriorityVO> list) {

        return new ResponseEntity<>(priorityService.updateByList(list, organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据组织id查询优先级,map")
    @GetMapping("/list")
    public ResponseEntity<Map<Long, PriorityVO>> queryByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                       @PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(priorityService.queryByOrganizationId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priorityList.get"));

    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据组织id查询默认优先级")
    @GetMapping("/default")
    public ResponseEntity<PriorityVO> queryDefaultByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                   @PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(priorityService.queryDefaultByOrganizationId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priority.get"));

    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据组织id查询优先级,list")
    @GetMapping("/list_by_org")
    public ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(@ApiParam(value = "组织id", required = true)
                                                                      @PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(priorityService.queryByOrganizationIdList(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priorityList.get"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "生效/失效优先级")
    @GetMapping("/enable/{id}")
    public ResponseEntity<PriorityVO> enablePriority(@PathVariable("organization_id") Long organizationId,
                                                     @ApiParam(value = "id", required = true)
                                                     @PathVariable Long id,
                                                     @RequestParam(required = false) Boolean enable) {
        return new ResponseEntity<>(priorityService.enablePriority(organizationId, id, enable), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验删除优先级")
    @GetMapping("/check_delete/{id}")
    public ResponseEntity<Long> checkDelete(@PathVariable("organization_id") Long organizationId,
                                            @ApiParam(value = "id", required = true)
                                            @PathVariable Long id) {
        return new ResponseEntity<>(priorityService.checkDelete(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除优先级")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long priorityId,
                                 @RequestParam(required = false) Long changePriorityId) {
        priorityService.delete(organizationId, priorityId, changePriorityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
