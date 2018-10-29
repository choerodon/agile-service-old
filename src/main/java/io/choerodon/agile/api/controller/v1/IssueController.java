package io.choerodon.agile.api.controller.v1;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.rule.IssueRule;
import io.choerodon.agile.infra.common.utils.VerifyUpdateUtil;
import io.choerodon.agile.infra.dataobject.IssueComponentDetailDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issues")
public class IssueController {

    private IssueService issueService;

    @Autowired
    private IssueRule issueRule;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Autowired
    private IssueValidator issueValidator;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

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
    @ApiOperation("分页过滤查询issue列表(包含子任务,不含测试任务)")
    @CustomPageRequest
    @PostMapping(value = "/include_sub")
    public ResponseEntity<Page<IssueListDTO>> listIssueWithSub(@ApiIgnore
                                                               @ApiParam(value = "分页信息", required = true)
                                                               @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                       PageRequest pageRequest,
                                                               @ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "查询参数", required = true)
                                                               @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.listIssueWithSub(projectId, searchDTO, pageRequest))
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

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("故事地图查询epic")
    @GetMapping(value = "/storymap/epics")
    public ResponseEntity<List<StoryMapEpicDTO>> listStoryMapEpic(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "show done epic", required = false)
                                                                  @RequestParam(required = false) Boolean showDoneEpic,
                                                                  @ApiParam(value = "search item，my problem", required = false)
                                                                  @RequestParam(required = false) Long assigneeId,
                                                                  @ApiParam(value = "search item，only story", required = false)
                                                                  @RequestParam(required = false) Boolean onlyStory,
                                                                  @ApiParam(value = "quick filter", required = false)
                                                                  @RequestParam(required = false) List<Long> quickFilterIds) {
        return Optional.ofNullable(issueService.listStoryMapEpic(projectId, showDoneEpic, assigneeId, onlyStory, quickFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Epic.listStoryMapEpic"));
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

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("批量删除Issue,给测试")
    @DeleteMapping(value = "/to_version_test")
    public ResponseEntity batchDeleteIssues(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "issue id", required = true)
                                            @RequestBody List<Long> issueIds) {
        issueService.batchDeleteIssues(projectId, issueIds);
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
    @ApiOperation("批量替换issue版本,给测试")
    @PostMapping(value = "/to_version_test/{versionId}")
    public ResponseEntity batchIssueToVersionTest(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "versionId", required = true)
                                                  @PathVariable Long versionId,
                                                  @ApiParam(value = "issue id", required = true)
                                                  @RequestBody List<Long> issueIds) {
        issueService.batchIssueToVersionTest(projectId, versionId, issueIds);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
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
    @ApiOperation("分页过滤查询issue列表, 测试项目接口，过滤linked issue")
    @CustomPageRequest
    @PostMapping(value = "/test_component/filter_linked")
    public ResponseEntity<Page<IssueListDTO>> listIssueWithLinkedIssues(@ApiIgnore
                                                                        @ApiParam(value = "分页信息", required = true)
                                                                        @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                PageRequest pageRequest,
                                                                        @ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "查询参数", required = true)
                                                                        @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.listIssueWithLinkedIssues(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithBlockedIssues"));
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

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计issue相关信息（测试模块用）")
    @PostMapping(value = "/test_component/statistic")
    public ResponseEntity<List<PieChartDTO>> issueStatistic(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "查询类型(version、component、label)", required = true)
                                                            @RequestParam String type,
                                                            @ApiParam(value = "需要排除的issue类型列表")
                                                            @RequestBody List<String> issueTypes) {
        return Optional.ofNullable(issueService.issueStatistic(projectId, type, issueTypes))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.issueStatistic"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表(不包含子任务，包含详情),测试模块用")
    @CustomPageRequest
    @PostMapping(value = "/test_component/no_sub_detail")
    public ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(@ApiIgnore
                                                                                   @ApiParam(value = "分页信息", required = true)
                                                                                   @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                           PageRequest pageRequest,
                                                                                   @ApiParam(value = "项目id", required = true)
                                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                                   @ApiParam(value = "查询参数", required = true)
                                                                                   @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.listIssueWithoutSubDetail(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithoutSubDetail"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("故事地图查询issues,type:'sprint, version, none', pageType:'storymap,backlog'")
    @GetMapping(value = "/storymap/issues")
    public ResponseEntity<List<StoryMapIssueDTO>> listIssuesByProjectId(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "type:sprint, version, none", required = true)
                                                                        @RequestParam String type,
                                                                        @ApiParam(value = "故事页面or待办页面 pageType:storymap,backlog", required = true)
                                                                        @RequestParam String pageType,
                                                                        @ApiParam(value = "search item，my problem", required = false)
                                                                        @RequestParam(required = false) Long assigneeId,
                                                                        @ApiParam(value = "search item，only story", required = false)
                                                                        @RequestParam(required = false) Boolean onlyStory,
                                                                        @ApiParam(value = "quick filter", required = false)
                                                                        @RequestParam(required = false) List<Long> quickFilterIds) {
        return Optional.ofNullable(issueService.listIssuesByProjectId(projectId, type, pageType, assigneeId, onlyStory, quickFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssuesByProjectId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("故事地图移动卡片")
    @PostMapping(value = "/storymap/move")
    public ResponseEntity storymapMove(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "story map move dto", required = true)
                                       @RequestBody StoryMapMoveDTO storyMapMoveDTO) {
        issueService.storymapMove(projectId, storyMapMoveDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更改父任务")
    @PostMapping(value = "/update_parent")
    public ResponseEntity<IssueDTO> updateIssueParentId(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "issue parent id update dto", required = true)
                                                        @RequestBody IssueUpdateParentIdDTO issueUpdateParentIdDTO) {
        return Optional.ofNullable(issueService.issueParentIdUpdate(projectId, issueUpdateParentIdDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueParentId.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计当前项目下未完成的任务数，包括故事、任务、缺陷")
    @GetMapping(value = "/count")
    public ResponseEntity<JSONObject> countUnResolveByProjectId(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.countUnResolveByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.countUnResolveIssue.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据条件过滤查询返回issueIds，测试项目接口")
    @PostMapping(value = "/issue_ids")
    public ResponseEntity<List<Long>> queryIssueIdsByOptions(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "查询参数", required = true)
                                                             @RequestBody SearchDTO searchDTO) {
        return Optional.ofNullable(issueService.queryIssueIdsByOptions(projectId, searchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueIds.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询未分配的问题，类型为story,task,bug")
    @GetMapping(value = "/undistributed")
    public ResponseEntity<List<UndistributedIssueDTO>> queryUnDistributedIssues(@ApiParam(value = "项目id", required = true)
                                                                                @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.queryUnDistributedIssues(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.UndistributedIssueList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询经办人未完成的问题，类型为story,task,bug")
    @GetMapping(value = "/unfinished/{assignee_id}")
    public ResponseEntity<List<UnfinishedIssueDTO>> queryUnfinishedIssues(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          @ApiParam(value = "经办人id", required = true)
                                                                          @PathVariable(name = "assignee_id") Long assigneeId) {
        return Optional.ofNullable(issueService.queryUnfinishedIssues(projectId, assigneeId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.UnfinishedIssueList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询用户故事地图泳道")
    @GetMapping(value = "/storymap/swim_lane")
    public ResponseEntity<String> querySwimLaneCode(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.querySwimLaneCode(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.querySwimLaneCode.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试服务用，批量复制issue并生成版本信息")
    @PostMapping("/batch_clone_issue/{versionId}")
    public ResponseEntity<List<Long>> cloneIssuesByVersionId(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "versionId", required = true)
                                                             @PathVariable Long versionId,
                                                             @ApiParam(value = "复制的issueIds", required = true)
                                                             @RequestBody List<Long> issueIds) {
        issueValidator.checkIssueIdsAndVersionId(projectId, issueIds, versionId);
        return Optional.ofNullable(issueService.cloneIssuesByVersionId(projectId, versionId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.cloneIssuesByVersionId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试服务用，issue按照项目分组接口")
    @GetMapping("/list_issues_by_project")
    public ResponseEntity<List<IssueProjectDTO>> queryIssueTestGroupByProject(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.queryIssueTestGroupByProject())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryIssueTestGroupByProject"));
    }

}