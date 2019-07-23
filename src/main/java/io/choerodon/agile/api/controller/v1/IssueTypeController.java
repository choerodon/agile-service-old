package io.choerodon.agile.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.vo.IssueTypeSearchVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.IssueTypeWithInfoVO;
import io.choerodon.agile.app.service.IssueTypeService;
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
 * @date 2018/8/8
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/issue_type")
public class IssueTypeController extends BaseController {

    @Autowired
    private IssueTypeService issueTypeService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据id查询问题类型")
    @GetMapping(value = "/{id}")
    public ResponseEntity<IssueTypeVO> queryIssueTypeById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.queryById(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建问题类型")
    @PostMapping
    public ResponseEntity<IssueTypeVO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid IssueTypeVO issueTypeVO) {
        return new ResponseEntity<>(issueTypeService.create(organizationId, issueTypeVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改问题类型")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueTypeVO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId,
                                              @RequestBody @Valid IssueTypeVO issueTypeVO) {
        issueTypeVO.setId(issueTypeId);
        issueTypeVO.setOrganizationId(organizationId);
        return new ResponseEntity<>(issueTypeService.update(issueTypeVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除问题类型")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.delete(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "分页查询问题类型列表")
    @CustomPageRequest
    @PostMapping("/list")
    public ResponseEntity<PageInfo<IssueTypeWithInfoVO>> queryIssueTypeList(@ApiIgnore
                                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                            @ApiParam(value = "组织id", required = true)
                                                                         @PathVariable("organization_id") Long organizationId,
                                                                            @ApiParam(value = "issueTypeSearchDTO", required = true)
                                                                         @RequestBody IssueTypeSearchVO issueTypeSearchVO) {
        return Optional.ofNullable(issueTypeService.queryIssueTypeList(pageRequest, organizationId, issueTypeSearchVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueTypeList.get"));

    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验问题类型是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.checkDelete(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验问题类型名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(issueTypeService.checkName(organizationId, name, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("获取问题类型列表")
    @GetMapping(value = "/types")
    public ResponseEntity<List<IssueTypeVO>> queryByOrgId(@PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(issueTypeService.queryByOrgId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryByOrgId"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询所有问题类型及关联的方案")
    @GetMapping(value = "/query_issue_type_with_state_machine")
    public ResponseEntity<List<IssueTypeVO>> queryIssueTypeByStateMachineSchemeId(@PathVariable("organization_id") Long organizationId,
                                                                                  @RequestParam("schemeId") Long schemeId) {
        return new ResponseEntity<>(issueTypeService.queryIssueTypeByStateMachineSchemeId(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据组织id查询类型，map")
    @GetMapping(value = "/type_map")
    public ResponseEntity<Map<Long, IssueTypeVO>> listIssueTypeMap(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(issueTypeService.listIssueTypeMap(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "迁移组织层问题类型数据")
    @PostMapping(value = "/init_data")
    public ResponseEntity<Map<Long, Map<String, Long>>> initIssueTypeData(@PathVariable("organization_id") Long organizationId,
                                                                          @RequestBody List<Long> orgIds) {
        return new ResponseEntity<>(issueTypeService.initIssueTypeData(organizationId, orgIds), HttpStatus.OK);
    }
}
