package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.service.IssueLinkTypeService;
import io.choerodon.agile.domain.agile.rule.IssueLinkTypeRule;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/issue_link_types")
public class IssueLinkTypeController {

    @Autowired
    private IssueLinkTypeService issueLinkTypeService;
    @Autowired
    private IssueLinkTypeRule issueLinkTypeRule;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("根据项目id查询issueLinkType")
    @GetMapping
    public ResponseEntity<List<IssueLinkTypeDTO>> listIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                    @ApiParam(value = "不包含的issueLinkTypeId")
                                                                    @RequestParam(required = false) Long issueLinkTypeId) {
        return Optional.ofNullable(issueLinkTypeService.listIssueLinkType(projectId, issueLinkTypeId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.listIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT)
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

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("创建issueLinkType")
    @PostMapping
    public ResponseEntity<IssueLinkTypeDTO> createIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "创建issueLinkType对象", required = true)
                                                                @RequestBody IssueLinkTypeCreateDTO issueLinkTypeCreateDTO) {
        issueLinkTypeRule.verifyCreateData(issueLinkTypeCreateDTO, projectId);
        return Optional.ofNullable(issueLinkTypeService.createIssueLinkType(issueLinkTypeCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.createIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("修改issueLink")
    @PutMapping
    public ResponseEntity<IssueLinkTypeDTO> updateIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "issueLinkType", required = true)
                                                                @RequestBody IssueLinkTypeDTO issueLinkTypeDTO) {
        issueLinkTypeRule.verifyUpdateData(issueLinkTypeDTO, projectId);
        return Optional.ofNullable(issueLinkTypeService.updateIssueLinkType(issueLinkTypeDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.updateIssueLinkType"));
    }

    @Permission(level = ResourceLevel.PROJECT)
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
}
