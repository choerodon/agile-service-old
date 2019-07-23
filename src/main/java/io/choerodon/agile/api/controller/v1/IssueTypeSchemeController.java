package io.choerodon.agile.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.vo.IssueTypeSchemeSearchVO;
import io.choerodon.agile.api.vo.IssueTypeSchemeVO;
import io.choerodon.agile.api.vo.IssueTypeSchemeWithInfoVO;
import io.choerodon.agile.app.service.IssueTypeSchemeService;
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
import java.util.Map;
import java.util.Optional;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/issue_type_scheme")
public class IssueTypeSchemeController extends BaseController {

    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据id查询问题类型方案")
    @GetMapping(value = "/{id}")
    public ResponseEntity<IssueTypeSchemeVO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeSchemeId) {
        return new ResponseEntity<>(issueTypeSchemeService.queryById(organizationId, issueTypeSchemeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建问题类型方案")
    @PostMapping
    public ResponseEntity<IssueTypeSchemeVO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid IssueTypeSchemeVO issueTypeSchemeVO) {
        return new ResponseEntity<>(issueTypeSchemeService.create(organizationId, issueTypeSchemeVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改问题类型方案")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueTypeSchemeVO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeSchemeId,
                                                    @RequestBody @Valid IssueTypeSchemeVO issueTypeSchemeVO) {
        issueTypeSchemeVO.setId(issueTypeSchemeId);
        issueTypeSchemeVO.setOrganizationId(organizationId);
        return new ResponseEntity<>(issueTypeSchemeService.update(organizationId, issueTypeSchemeVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验问题类型方案是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeSchemeId) {
        return new ResponseEntity<>(issueTypeSchemeService.checkDelete(organizationId, issueTypeSchemeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除问题类型方案")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeSchemeId) {
        return new ResponseEntity<>(issueTypeSchemeService.delete(organizationId, issueTypeSchemeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "分页查询问题类型方案列表")
    @CustomPageRequest
    @PostMapping("/list")
    public ResponseEntity<PageInfo<IssueTypeSchemeWithInfoVO>> queryIssueTypeSchemeList(@ApiIgnore
                                                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                                        @ApiParam(value = "组织id", required = true)
                                                                                         @PathVariable("organization_id") Long organizationId,
                                                                                        @ApiParam(value = "组织id", required = true)
                                                                                         @RequestBody IssueTypeSchemeSearchVO issueTypeSchemeDTO) {
        return Optional.ofNullable(issueTypeSchemeService.queryIssueTypeSchemeList(pageRequest, organizationId, issueTypeSchemeDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueTypeSchemeList.get"));

    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验问题类型名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(issueTypeSchemeService.checkName(organizationId, name, id), HttpStatus.OK);
    }


}
