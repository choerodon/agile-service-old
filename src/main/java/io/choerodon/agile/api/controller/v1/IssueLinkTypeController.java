package io.choerodon.agile.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.domain.agile.rule.IssueLinkTypeRule;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_link_types")
public class IssueLinkTypeController {

    @Autowired
    private IssueLinkTypeService issueLinkTypeService;
    @Autowired
    private IssueLinkTypeRule issueLinkTypeRule;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目id查询issueLinkType")
    @GetMapping
    public ResponseEntity<Page<IssueLinkTypeDTO>> listIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                    @ApiParam(value = "不包含的issueLinkTypeId")
                                                                    @RequestParam(required = false) Long issueLinkTypeId,
                                                                    @ApiParam(value = "连接名称")
                                                                    @RequestParam(required = false) String linkName,
                                                                    @ApiParam(value = "分页信息", required = true)
                                                                    @SortDefault(value = "link_type_id", direction = Sort.Direction.DESC)
                                                                    @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(issueLinkTypeService.listIssueLinkType(projectId, issueLinkTypeId, linkName, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.listIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issueLinkTypeId查询issueLinkType")
    @GetMapping(value = "/{linkTypeId}")
    public ResponseEntity<IssueLinkTypeDTO> queryIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "linkTypeId", required = true)
                                                               @PathVariable(name = "linkTypeId") Long linkTypeId) {
        return Optional.ofNullable(issueLinkTypeService.queryIssueLinkType(projectId, linkTypeId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.queryIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("创建issueLinkType")
    @PostMapping
    public ResponseEntity<IssueLinkTypeDTO> createIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "创建issueLinkType对象", required = true)
                                                                @RequestBody IssueLinkTypeCreateDTO issueLinkTypeCreateDTO) {
        issueLinkTypeRule.verifyCreateData(issueLinkTypeCreateDTO, projectId);
        issueLinkTypeRule.verifyIssueLinkTypeName(projectId, issueLinkTypeCreateDTO.getLinkName(), null);
        return Optional.ofNullable(issueLinkTypeService.createIssueLinkType(issueLinkTypeCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.createIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("修改issueLinkType")
    @PutMapping
    public ResponseEntity<IssueLinkTypeDTO> updateIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "issueLinkType", required = true)
                                                                @RequestBody IssueLinkTypeDTO issueLinkTypeDTO) {
        issueLinkTypeRule.verifyUpdateData(issueLinkTypeDTO, projectId);
        issueLinkTypeRule.verifyIssueLinkTypeName(projectId, issueLinkTypeDTO.getLinkName(), issueLinkTypeDTO.getLinkTypeId());
        return Optional.ofNullable(issueLinkTypeService.updateIssueLinkType(issueLinkTypeDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.updateIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("删除issueLink")
    @DeleteMapping(value = "/{issueLinkTypeId}")
    public ResponseEntity deleteIssueLinkType(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "issueLinkType", required = true)
                                              @PathVariable(name = "issueLinkTypeId") Long issueLinkTypeId,
                                              @ApiParam(value = "转移到其他的类型上，如果为空则不转移，直接删除")
                                              @RequestParam(required = false) Long toIssueLinkTypeId) {
        issueLinkTypeRule.verifyDeleteData(issueLinkTypeId, toIssueLinkTypeId, projectId);
        issueLinkTypeService.deleteIssueLinkType(issueLinkTypeId, toIssueLinkTypeId, projectId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("IssueLinkType重名校验")
    @GetMapping(value = "/check_name")
    public ResponseEntity checkIssueLinkTypeName(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "issue_link_type_name", required = true)
                                                 @RequestParam(name = "issueLinkTypeName") String issueLinkTypeName,
                                                 @ApiParam(value = "issue_link_type_id", required = false)
                                                 @RequestParam(name = "issueLinkTypeId", required = false) Long issueLinkTypeId) {
        return new ResponseEntity<>(issueLinkTypeService.queryIssueLinkTypeName(projectId, issueLinkTypeName, issueLinkTypeId), HttpStatus.OK);
    }
}
