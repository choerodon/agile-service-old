package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.IssueStatusDTO;
import io.choerodon.agile.api.dto.StatusAndIssuesDTO;
import io.choerodon.agile.api.dto.StatusDTO;
import io.choerodon.agile.api.dto.StatusMoveDTO;
import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.agile.infra.dataobject.StatusForMoveDataDO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_status")
public class IssueStatusController {

    private static final String ERROR_STATUS_GET = "error.status.get";

    @Autowired
    private IssueStatusService issueStatusService;

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("status创建")
    @PostMapping
    public ResponseEntity<IssueStatusDTO> createStatus(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "issue status object", required = true)
                                                       @RequestBody IssueStatusDTO issueStatusDTO) {
        return Optional.ofNullable(issueStatusService.create(projectId, issueStatusDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.status.create"));
    }

//    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("查询项目下未对应的状态")
//    @GetMapping(value = "/list_by_options")
//    public ResponseEntity<List<StatusAndIssuesDTO>> listUnCorrespondStatus(@ApiParam(value = "项目id", required = true)
//                                                                           @PathVariable(name = "project_id") Long projectId,
//                                                                           @ApiParam(value = "board id", required = true)
//                                                                           @RequestParam Long boardId) {
//        return Optional.ofNullable(issueStatusService.queryUnCorrespondStatus(projectId, boardId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException(ERROR_STATUS_GET));
//    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目下未对应的状态")
    @GetMapping(value = "/list_by_options")
    public ResponseEntity<List<StatusAndIssuesDTO>> listUnCorrespondStatus(@ApiParam(value = "项目id", required = true)
                                                                           @PathVariable(name = "project_id") Long projectId,
                                                                           @ApiParam(value = "board id", required = true)
                                                                           @RequestParam Long boardId) {
        return Optional.ofNullable(issueStatusService.queryUnCorrespondStatus(projectId, boardId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(ERROR_STATUS_GET));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("状态移动至列中")
    @PostMapping(value = "/{id}/move_to_column")
    public ResponseEntity<IssueStatusDTO> moveStatusToColumn(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "状态id", required = true)
                                                             @PathVariable Long id,
                                                             @ApiParam(value = "status move object", required = true)
                                                             @RequestBody StatusMoveDTO statusMoveDTO) {
        return Optional.ofNullable(issueStatusService.moveStatusToColumn(projectId, id, statusMoveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(ERROR_STATUS_GET));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("状态移动至未对应")
    @PostMapping(value = "/{id}/move_to_uncorrespond")
    public ResponseEntity<IssueStatusDTO> moveStatusToUnCorrespond(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "状态id", required = true)
                                                                   @PathVariable Long id,
                                                                   @ApiParam(value = "status move object", required = true)
                                                                   @RequestBody StatusMoveDTO statusMoveDTO) {
        return Optional.ofNullable(issueStatusService.moveStatusToUnCorrespond(projectId, id, statusMoveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(ERROR_STATUS_GET));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("删除未对应的状态")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteStatus(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "状态code", required = true)
                                       @PathVariable Long id) {
        issueStatusService.deleteStatus(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目下的issue状态")
    @GetMapping(value = "/list")
    public ResponseEntity<List<IssueStatusDTO>> listStatusByProjectId(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueStatusService.queryIssueStatusList(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.status.queryIssueStatusList"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("更新状态")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueStatusDTO> updateStatus(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "状态", required = true)
                                                       @RequestBody IssueStatusDTO issueStatusDTO) {
        return Optional.ofNullable(issueStatusService.updateStatus(projectId, issueStatusDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.status.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目id查询状态列表")
    @CustomPageRequest
    @GetMapping(value = "/statuses")
    public ResponseEntity<Page<StatusDTO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiIgnore
                                                           @ApiParam(value = "分页信息", required = true)
                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Optional.ofNullable(issueStatusService.listByProjectId(projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.statusList.get"));
    }


    @ApiOperation("迁移数据，查询所有状态")
    @GetMapping(value = "/move_status")
    public ResponseEntity moveStatus(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId) {
        issueStatusService.moveStatus(projectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
