package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.infra.common.utils.VerifyUpdateUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueRule issueRule;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issue")
    @PostMapping
    public ResponseEntity<IssueDTO> createIssue(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "创建issue对象", required = true)
                                                @RequestBody IssueCreateDTO issueCreateDTO) {
        issueRule.verifyCreateData(issueCreateDTO, projectId);
        return Optional.ofNullable(issueService.createIssue(issueCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.createIssue"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issue子任务")
    @PostMapping(value = "/sub_issue")
    public ResponseEntity<IssueSubDTO> createSubIssue(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "创建issue子任务对象", required = true)
                                                      @RequestBody IssueSubCreateDTO issueSubCreateDTO) {
        issueRule.verifySubCreateData(issueSubCreateDTO, projectId);
        return Optional.ofNullable(issueService.createSubIssue(issueSubCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.createSubIssue"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新issue")
    @PutMapping
    public ResponseEntity<IssueDTO> updateIssue(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "更新issue对象", required = true)
                                                @RequestBody JSONObject issueUpdate) {
        issueRule.verifyUpdateData(issueUpdate, projectId);
        IssueUpdateDTO issueUpdateDTO = new IssueUpdateDTO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(issueUpdate, issueUpdateDTO);
        return Optional.ofNullable(issueService.updateIssue(projectId, issueUpdateDTO, fieldList))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.updateIssue"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询单个issue")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<IssueDTO> queryIssue(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "issueId", required = true)
                                               @PathVariable Long issueId) {
        return Optional.ofNullable(issueService.queryIssue(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssue"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询单个子任务issue")
    @GetMapping(value = "/sub_issue/{issueId}")
    public ResponseEntity<IssueSubDTO> queryIssueSub(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "issueId", required = true)
                                                     @PathVariable Long issueId) {
        return Optional.ofNullable(issueService.queryIssueSub(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueSub"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表(不包含子任务,不含测试任务)")
    @CustomPageRequest
    @PostMapping(value = "/no_sub")
    public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(@ApiIgnore
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                          PageRequest pageRequest,
                                                                  @ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "查询参数", required = true)
                                                                  @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.listIssueWithoutSub(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithoutSub"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页搜索查询issue列表(包含子任务)")
    @CustomPageRequest
    @GetMapping(value = "/summary")
    public ResponseEntity<Page<IssueNumDTO>> queryIssueByOption(@ApiIgnore
                                                                @ApiParam(value = "分页信息", required = true)
                                                                @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                        PageRequest pageRequest,
                                                                @ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "issueId")
                                                                @RequestParam(required = false) Long issueId,
                                                                @ApiParam(value = "issueNum")
                                                                @RequestParam(required = false) String issueNum,
                                                                @ApiParam(value = "only active sprint", required = true)
                                                                @RequestParam Boolean onlyActiveSprint,
                                                                @ApiParam(value = "是否包含自身", required = true)
                                                                @RequestParam() Boolean self,
                                                                @ApiParam(value = "搜索内容", required = false)
                                                                @RequestParam(required = false) String content) {
        return Optional.ofNullable(issueService.queryIssueByOption(projectId, issueId, issueNum, onlyActiveSprint, self, content, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueByOption"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页搜索查询issue列表")
    @CustomPageRequest
    @GetMapping(value = "/agile/summary")
    public ResponseEntity<Page<IssueNumDTO>> queryIssueByOptionForAgile(@ApiIgnore
                                                                @ApiParam(value = "分页信息", required = true)
                                                                @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                        PageRequest pageRequest,
                                                                @ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "issueId")
                                                                @RequestParam(required = false) Long issueId,
                                                                @ApiParam(value = "issueNum")
                                                                @RequestParam(required = false) String issueNum,
                                                                @ApiParam(value = "是否包含自身", required = true)
                                                                @RequestParam() Boolean self,
                                                                @ApiParam(value = "搜索内容")
                                                                @RequestParam(required = false) String content) {
        return Optional.ofNullable(issueService.queryIssueByOptionForAgile(projectId, issueId, issueNum, self, content, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueByOptionForAgile"));
    }


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询epic")
    @GetMapping(value = "/epics")
    public ResponseEntity<List<EpicDataDTO>> listEpic(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listEpic(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Epic.listEpic"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("通过issueId删除")
    @DeleteMapping(value = "/{issueId}")
    public ResponseEntity deleteIssue(@ApiParam(value = "项目id", required = true)
                                      @PathVariable(name = "project_id") Long projectId,
                                      @ApiParam(value = "issueId", required = true)
                                      @PathVariable Long issueId) {
        issueService.deleteIssue(projectId, issueId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入版本")
    @PostMapping(value = "/to_version/{versionId}")
    public ResponseEntity<List<IssueSearchDTO>> batchIssueToVersion(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                    @ApiParam(value = "versionId", required = true)
                                                                    @PathVariable Long versionId,
                                                                    @ApiParam(value = "issue id", required = true)
                                                                    @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.batchIssueToVersion(projectId, versionId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToVersion"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入epic")
    @PostMapping(value = "/to_epic/{epicId}")
    public ResponseEntity<List<IssueSearchDTO>> batchIssueToEpic(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "epicId", required = true)
                                                                 @PathVariable Long epicId,
                                                                 @ApiParam(value = "issue id", required = true)
                                                                 @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.batchIssueToEpic(projectId, epicId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToEpic"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入冲刺")
    @PostMapping(value = "/to_sprint/{sprintId}")
    public ResponseEntity<List<IssueSearchDTO>> batchIssueToSprint(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "sprintId", required = true)
                                                                   @PathVariable Long sprintId,
                                                                   @ApiParam(value = "移卡信息", required = true)
                                                                   @RequestBody MoveIssueDTO moveIssueDTO) {
        return Optional.ofNullable(issueService.batchIssueToSprint(projectId, sprintId, moveIssueDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToSprint"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前项目下的epic，提供给列表下拉")
    @GetMapping(value = "/epics/select_data")
    public ResponseEntity<List<IssueEpicDTO>> listEpicSelectData(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listEpicSelectData(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueEpicList"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更改issue类型")
    @PostMapping("/update_type")
    public ResponseEntity<IssueDTO> updateIssueTypeCode(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "修改类型信息", required = true)
                                                        @RequestBody IssueUpdateTypeDTO issueUpdateTypeDTO) {
        IssueE issueE = issueRule.verifyUpdateTypeData(projectId, issueUpdateTypeDTO);
        return Optional.ofNullable(issueService.updateIssueTypeCode(issueE, issueUpdateTypeDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.updateIssueTypeCode"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue类型(type_code)查询issue列表(分页)")
    @CustomPageRequest
    @PostMapping(value = "/type_code/{typeCode}")
    public ResponseEntity<Page<IssueCommonDTO>> listByOptions(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                              @ApiParam(value = "typeCode", required = true)
                                                              @PathVariable String typeCode,
                                                              @ApiIgnore
                                                              @ApiParam(value = "分页信息", required = true)
                                                              @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Optional.ofNullable(issueService.listByOptions(projectId, typeCode, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueList.listByOptions"));
    }

    @ResponseBody
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出issue列表")
    @PostMapping(value = "/export")
    public void exportIssues(@ApiParam(value = "项目id", required = true)
                             @PathVariable(name = "project_id") Long projectId,
                             @ApiParam(value = "查询参数", required = true)
                             @RequestBody(required = false) SearchDTO searchDTO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        issueService.exportIssues(projectId, searchDTO, request, response);
    }

    @ResponseBody
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出issue详情")
    @PostMapping(value = "/export/{issueId}")
    public void exportIssue(@ApiParam(value = "项目id", required = true)
                            @PathVariable(name = "project_id") Long projectId,
                            @ApiParam(value = "issueId", required = true)
                            @PathVariable(name = "issueId") Long issueId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        issueService.exportIssue(projectId, issueId, request, response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("复制一个issue")
    @PostMapping("/{issueId}/clone_issue")
    public ResponseEntity<IssueDTO> cloneIssueByIssueId(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "issueId", required = true)
                                                        @PathVariable(name = "issueId") Long issueId,
                                                        @ApiParam(value = "复制条件", required = true)
                                                        @RequestBody CopyConditionDTO copyConditionDTO) {
        return Optional.ofNullable(issueService.cloneIssueByIssueId(projectId, issueId, copyConditionDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.cloneIssueByIssueId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("任务转换为子任务")
    @PostMapping("/transformed_sub_task")
    public ResponseEntity<IssueSubDTO> transformedSubTask(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "转换子任务信息", required = true)
                                                          @RequestBody IssueTransformSubTask issueTransformSubTask) {
        issueRule.verifyTransformedSubTask(issueTransformSubTask);
        return Optional.ofNullable(issueService.transformedSubTask(projectId, issueTransformSubTask))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.transformedSubTask"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue ids查询issue相关信息")
    @PostMapping("/issue_infos")
    public ResponseEntity<List<IssueInfoDTO>> listByIssueIds(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "issue ids", required = true)
                                                             @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.listByIssueIds(projectId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueNums.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表提供给测试模块用")
    @CustomPageRequest
    @PostMapping(value = "/test_component/no_sub")
    public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSubToTestComponent(@ApiIgnore
                                                                                 @ApiParam(value = "分页信息", required = true)
                                                                                 @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                         PageRequest pageRequest,
                                                                                 @ApiParam(value = "项目id", required = true)
                                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                                 @ApiParam(value = "查询参数", required = true)
                                                                                 @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.listIssueWithoutSubToTestComponent(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithoutSubToTestComponent"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据时间段查询问题类型的数量")
    @GetMapping(value = "/type/{typeCode}")
    public ResponseEntity<List<IssueCreationNumDTO>> queryIssueNumByTimeSlot(@ApiParam(value = "项目id", required = true)
                                                                             @PathVariable(name = "project_id") Long projectId,
                                                                             @ApiParam(value = "type code", required = true)
                                                                             @PathVariable String typeCode,
                                                                             @ApiParam(value = "时间段", required = true)
                                                                             @RequestParam Integer timeSlot) {
        return Optional.ofNullable(issueService.queryIssueNumByTimeSlot(projectId, typeCode, timeSlot))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.timeSlotCount.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "拖动epic位置")
    @PutMapping(value = "/epic_drag")
    public ResponseEntity<EpicDataDTO> dragEpic(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "排序对象", required = true)
                                                             @RequestBody EpicSequenceDTO epicSequenceDTO) {
        return Optional.ofNullable(issueService.dragEpic(projectId, epicSequenceDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueController.dragEpic"));
    }
}