package io.choerodon.agile.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.validator.StateValidator;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.ProjectConfigService;
import io.choerodon.agile.app.service.StatusService;
import io.choerodon.agile.infra.dataobject.StatusDTO;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author shinan.chen
 * @since 2018/9/27
 */
@RestController
@RequestMapping(value = "/v1")
public class StatusController extends BaseController {

    @Autowired
    private StatusService statusService;
    @Autowired
    private StateValidator stateValidator;
    @Autowired
    private ProjectConfigService projectConfigService;

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "分页查询状态列表")
    @CustomPageRequest
    @PostMapping("/organizations/{organization_id}/status/list")
    public ResponseEntity<PageInfo<StatusWithInfoVO>> queryStatusList(@ApiIgnore
                                                                      @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                      @ApiParam(value = "组织id", required = true)
                                                                      @PathVariable("organization_id") Long organizationId,
                                                                      @ApiParam(value = "status search dto", required = true)
                                                                      @RequestBody StatusSearchVO statusSearchVO) {
        return Optional.ofNullable(statusService.queryStatusList(pageRequest, organizationId, statusSearchVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.statusList.get"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建状态")
    @PostMapping("/organizations/{organization_id}/status")
    public ResponseEntity<StatusVO> create(@PathVariable("organization_id") Long organizationId,
                                           @RequestBody StatusVO statusVO) {
        stateValidator.validate(statusVO);
        return new ResponseEntity<>(statusService.create(organizationId, statusVO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新状态")
    @PutMapping(value = "/organizations/{organization_id}/status/{status_id}")
    public ResponseEntity<StatusVO> update(@PathVariable("organization_id") Long organizationId,
                                           @PathVariable("status_id") Long statusId,
                                           @RequestBody @Valid StatusVO statusVO) {
        statusVO.setId(statusId);
        statusVO.setOrganizationId(organizationId);
        stateValidator.validate(statusVO);
        return new ResponseEntity<>(statusService.update(statusVO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除状态")
    @DeleteMapping(value = "/organizations/{organization_id}/status/{status_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId,
                                          @PathVariable("status_id") Long statusId) {
        return new ResponseEntity<>(statusService.delete(organizationId, statusId), HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据id查询状态对象")
    @GetMapping(value = "/organizations/{organization_id}/status/{status_id}")
    public ResponseEntity<StatusInfoVO> queryStatusById(@PathVariable("organization_id") Long organizationId,
                                                        @PathVariable("status_id") Long statusId) {
        return new ResponseEntity<>(statusService.queryStatusById(organizationId, statusId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询状态机下的所有状态")
    @PostMapping(value = "/organizations/{organization_id}/status/query_by_state_machine_id")
    public ResponseEntity<List<StatusVO>> queryByStateMachineIds(@PathVariable("organization_id") Long organizationId,
                                                                 @RequestBody @Valid List<Long> stateMachineIds) {
        return new ResponseEntity<>(statusService.queryByStateMachineIds(organizationId, stateMachineIds), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询组织下的所有状态")
    @GetMapping(value = "/organizations/{organization_id}/status/query_all")
    public ResponseEntity<List<StatusVO>> queryAllStatus(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(statusService.queryAllStatus(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询组织下的所有状态,返回map")
    @GetMapping(value = "/organizations/{organization_id}/status/list_map")
    public ResponseEntity<Map<Long, StatusMapVO>> queryAllStatusMap(
            @ApiParam(value = "组织id", required = true)
            @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(statusService.queryAllStatusMap(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "校验状态名字是否未被使用")
    @GetMapping(value = "/organizations/{organization_id}/status/check_name")
    public ResponseEntity<StatusCheckVO> checkName(@PathVariable("organization_id") Long organizationId,
                                                   @RequestParam("name") String name) {
        return Optional.ofNullable(statusService.checkName(organizationId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.statusName.check"));

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "校验状态名字是否未被使用,项目层")
    @GetMapping(value = "/projects/{project_id}/status/project_check_name")
    public ResponseEntity<StatusCheckVO> checkName(@PathVariable("project_id") Long projectId,
                                                   @RequestParam("organization_id") Long organizationId,
                                                   @RequestParam("name") String name) {
        return Optional.ofNullable(statusService.checkName(organizationId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.statusName.check"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据ids批量查询状态")
    @PostMapping(value = "/status/batch")
    public ResponseEntity<Map<Long, StatusDTO>> batchStatusGet(@ApiParam(value = "状态ids", required = true)
                                                               @RequestBody List<Long> ids) {
        return Optional.ofNullable(statusService.batchStatusGet(ids))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.status.get"));
    }
}
