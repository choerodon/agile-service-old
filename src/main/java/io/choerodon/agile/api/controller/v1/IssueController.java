package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.infra.dataobject.IssueNumDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.StateMachineClientService;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.infra.utils.VerifyUpdateUtil;
import io.choerodon.agile.infra.dataobject.IssueComponentDetailDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
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

    private IssueService issueService;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;
    @Autowired
    private IssueValidator issueValidator;
    @Autowired
    private StateMachineClientService stateMachineClientService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issue")
    @PostMapping
    public ResponseEntity<IssueVO> createIssue(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "应用类型", required = true)
                                                @RequestParam(value = "applyType") String applyType,
                                               @ApiParam(value = "创建issue对象", required = true)
                                                @RequestBody IssueCreateVO issueCreateVO) {
        issueValidator.verifyCreateData(issueCreateVO, projectId, applyType);
        return Optional.ofNullable(stateMachineClientService.createIssue(issueCreateVO, applyType))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.createIssue"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("史诗名称重复校验")
    @GetMapping(value = "/check_epic_name")
    public ResponseEntity<Boolean> checkEpicName(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "史诗名称", required = true)
                                                 @RequestParam String epicName) {
        return Optional.ofNullable(issueService.checkEpicName(projectId, epicName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.checkEpicName.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issue子任务")
    @PostMapping(value = "/sub_issue")
    public ResponseEntity<IssueSubVO> createSubIssue(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "创建issue子任务对象", required = true)
                                                      @RequestBody IssueSubCreateVO issueSubCreateVO) {
        issueValidator.verifySubCreateData(issueSubCreateVO, projectId);
        return Optional.ofNullable(stateMachineClientService.createSubIssue(issueSubCreateVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.createSubIssue"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新issue")
    @PutMapping
    public ResponseEntity<IssueVO> updateIssue(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "更新issue对象", required = true)
                                                @RequestBody JSONObject issueUpdate) {
        issueValidator.verifyUpdateData(issueUpdate, projectId);
        IssueUpdateVO issueUpdateVO = new IssueUpdateVO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(issueUpdate, issueUpdateVO);
        if (issueUpdate.get("featureVO") != null) {
            issueUpdateVO.setFeatureVO(JSONObject.parseObject(JSON.toJSONString(issueUpdate.get("featureVO")), FeatureVO.class));
        }
        return Optional.ofNullable(issueService.updateIssue(projectId, issueUpdateVO, fieldList))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.updateIssue"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新issue的状态")
    @PutMapping("/update_status")
    public ResponseEntity<IssueVO> updateIssueStatus(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "转换id", required = true)
                                                      @RequestParam Long transformId,
                                                     @ApiParam(value = "问题id", required = true)
                                                      @RequestParam Long issueId,
                                                     @ApiParam(value = "版本号", required = true)
                                                      @RequestParam Long objectVersionNumber,
                                                     @ApiParam(value = "应用类型", required = true)
                                                      @RequestParam String applyType) {
        return Optional.ofNullable(issueService.updateIssueStatus(projectId, issueId, transformId, objectVersionNumber, applyType))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.updateIssueStatus"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询单个issue")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<IssueVO> queryIssue(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "issueId", required = true)
                                               @PathVariable Long issueId,
                                              @ApiParam(value = "组织id", required = true)
                                               @RequestParam(required = false) Long organizationId) {
        return Optional.ofNullable(issueService.queryIssue(projectId, issueId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssue"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询单个子任务issue")
    @GetMapping(value = "/sub_issue/{issueId}")
    public ResponseEntity<IssueSubVO> queryIssueSub(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                    @ApiParam(value = "组织id", required = true)
                                                     @RequestParam Long organizationId,
                                                    @ApiParam(value = "issueId", required = true)
                                                     @PathVariable Long issueId) {
        return Optional.ofNullable(issueService.queryIssueSub(projectId, organizationId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueSub"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页查询问题列表，包含子任务")
    @PostMapping(value = "/include_sub")
    public ResponseEntity<PageInfo<IssueListFieldKVVO>> listIssueWithSub(@ApiIgnore
                                                               @ApiParam(value = "分页信息", required = true)
                                                               @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                       PageRequest pageRequest,
                                                                         @ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                                         @ApiParam(value = "查询参数", required = true)
                                                               @RequestBody(required = false) SearchVO searchVO,
                                                                         @ApiParam(value = "查询参数", required = true)
                                                               @RequestParam(required = false) Long organizationId) {
        return Optional.ofNullable(issueService.listIssueWithSub(projectId, searchVO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithSub"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页搜索查询issue列表(包含子任务)")
    @CustomPageRequest
    @GetMapping(value = "/summary")
    public ResponseEntity<PageInfo<IssueNumVO>> queryIssueByOption(@ApiIgnore
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页搜索查询issue列表")
    @CustomPageRequest
    @GetMapping(value = "/agile/summary")
    public ResponseEntity<PageInfo<IssueNumVO>> queryIssueByOptionForAgile(@ApiIgnore
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


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询epic")
    @GetMapping(value = "/epics")
    public ResponseEntity<List<EpicDataVO>> listEpic(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listEpic(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Epic.listEpic"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("program查询epic")
    @GetMapping(value = "/program/epics")
    public ResponseEntity<List<EpicDataVO>> listProgramEpic(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listProgramEpic(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.programEpic.listEpic"));
    }

//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("故事地图查询epic")
//    @GetMapping(value = "/storymap/epics")
//    public ResponseEntity<List<StoryMapEpicDTO>> listStoryMapEpic(@ApiParam(value = "项目id", required = true)
//                                                                  @PathVariable(name = "project_id") Long projectId,
//                                                                  @ApiParam(value = "组织id", required = true)
//                                                                  @RequestParam Long organizationId,
//                                                                  @ApiParam(value = "show done epic", required = false)
//                                                                  @RequestParam(required = false) Boolean showDoneEpic,
//                                                                  @ApiParam(value = "search item，my problem", required = false)
//                                                                  @RequestParam(required = false) Long assigneeId,
//                                                                  @ApiParam(value = "search item，only story", required = false)
//                                                                  @RequestParam(required = false) Boolean onlyStory,
//                                                                  @ApiParam(value = "quick filter", required = false)
//                                                                  @RequestParam(required = false) List<Long> quickFilterIds) {
//        return Optional.ofNullable(issueService.listStoryMapEpic(projectId, organizationId, showDoneEpic, assigneeId, onlyStory, quickFilterIds))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.Epic.listStoryMapEpic"));
//    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("通过issueId删除")
    @DeleteMapping(value = "/{issueId}")
    public ResponseEntity deleteIssue(@ApiParam(value = "项目id", required = true)
                                      @PathVariable(name = "project_id") Long projectId,
                                      @ApiParam(value = "issueId", required = true)
                                      @PathVariable Long issueId) {
        issueService.deleteIssue(projectId, issueId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("批量删除Issue,给测试")
    @DeleteMapping(value = "/to_version_test")
    public ResponseEntity batchDeleteIssues(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "issue id", required = true)
                                            @RequestBody List<Long> issueIds) {
        issueService.batchDeleteIssues(projectId, issueIds);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入版本")
    @PostMapping(value = "/to_version/{versionId}")
    public ResponseEntity<List<IssueSearchVO>> batchIssueToVersion(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "versionId", required = true)
                                                                    @PathVariable Long versionId,
                                                                   @ApiParam(value = "issue id", required = true)
                                                                    @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.batchIssueToVersion(projectId, versionId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToVersion"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入epic")
    @PostMapping(value = "/to_epic/{epicId}")
    public ResponseEntity<List<IssueSearchVO>> batchIssueToEpic(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "epicId", required = true)
                                                                 @PathVariable Long epicId,
                                                                @ApiParam(value = "issue id", required = true)
                                                                 @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.batchIssueToEpic(projectId, epicId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToEpic"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("story批量加入feature")
    @PostMapping(value = "/to_feature/{featureId}")
    public ResponseEntity batchStoryToFeature(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "featureId", required = true)
                                              @PathVariable Long featureId,
                                              @ApiParam(value = "issue id", required = true)
                                              @RequestBody List<Long> issueIds) {
        issueService.batchStoryToFeature(projectId, featureId, issueIds);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("issue批量加入冲刺")
    @PostMapping(value = "/to_sprint/{sprintId}")
    public ResponseEntity<List<IssueSearchVO>> batchIssueToSprint(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "sprintId", required = true)
                                                                   @PathVariable Long sprintId,
                                                                  @ApiParam(value = "移卡信息", required = true)
                                                                   @RequestBody MoveIssueVO moveIssueVO) {
        return Optional.ofNullable(issueService.batchIssueToSprint(projectId, sprintId, moveIssueVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.batchToSprint"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前项目下的epic，提供给列表下拉")
    @GetMapping(value = "/epics/select_data")
    public ResponseEntity<List<IssueEpicVO>> listEpicSelectData(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listEpicSelectData(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueEpicList"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前项目下的feature，提供给列表下拉")
    @GetMapping(value = "/feature/select_data")
    public ResponseEntity<List<IssueFeatureVO>> listFeatureSelectData(@ApiParam(value = "项目id", required = true)
                                                                       @PathVariable(name = "project_id") Long projectId,
                                                                      @ApiParam(value = "组织id", required = true)
                                                                       @RequestParam Long organizationId,
                                                                      @ApiParam(value = "史诗id", required = false)
                                                                       @RequestParam(required = false) Long epicId) {
        return Optional.ofNullable(issueService.listFeatureSelectData(projectId, organizationId, epicId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssueFeatureList"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前项目下的feature，包括详细统计信息")
    @GetMapping(value = "/features")
    public ResponseEntity<List<IssueFeatureVO>> listFeature(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "组织id", required = true)
                                                             @RequestParam Long organizationId) {
        return Optional.ofNullable(issueService.listFeature(projectId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.listFeature.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目群下的epic，提供给列表下拉")
    @GetMapping(value = "/epics/select_program_data")
    public ResponseEntity<List<IssueEpicVO>> listEpicSelectProgramData(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.listEpicSelectProgramData(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryProgramIssueEpicList"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更改issue类型")
    @PostMapping("/update_type")
    public ResponseEntity<IssueVO> updateIssueTypeCode(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "组织id", required = true)
                                                        @RequestParam Long organizationId,
                                                       @ApiParam(value = "修改类型信息", required = true)
                                                        @RequestBody IssueUpdateTypeVO issueUpdateTypeVO) {
        IssueConvertDTO issueConvertDTO = issueValidator.verifyUpdateTypeData(projectId, issueUpdateTypeVO);
        return Optional.ofNullable(issueService.updateIssueTypeCode(issueConvertDTO, issueUpdateTypeVO, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.updateIssueTypeCode"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("任务转换为子任务")
    @PostMapping("/transformed_sub_task")
    public ResponseEntity<IssueSubVO> transformedSubTask(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "组织id", required = true)
                                                          @RequestParam Long organizationId,
                                                         @ApiParam(value = "转换子任务信息", required = true)
                                                          @RequestBody IssueTransformSubTask issueTransformSubTask) {
        issueValidator.verifyTransformedSubTask(issueTransformSubTask);
        return Optional.ofNullable(issueService.transformedSubTask(projectId, organizationId, issueTransformSubTask))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.transformedSubTask"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("子任务转换为任务")
    @PostMapping("/transformed_task")
    public ResponseEntity<IssueVO> transformedTask(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "组织id", required = true)
                                                    @RequestParam Long organizationId,
                                                   @ApiParam(value = "转换任务信息", required = true)
                                                    @RequestBody IssueTransformTask issueTransformTask) {
        IssueConvertDTO issueConvertDTO = issueValidator.verifyTransformedTask(projectId, issueTransformTask);
        return Optional.ofNullable(issueService.transformedTask(issueConvertDTO, issueTransformTask, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.transformedTask"));
    }

    @ResponseBody
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出issue列表")
    @PostMapping(value = "/export")
    public void exportIssues(@ApiParam(value = "项目id", required = true)
                             @PathVariable(name = "project_id") Long projectId,
                             @ApiParam(value = "组织id", required = true)
                             @RequestParam Long organizationId,
                             @ApiParam(value = "查询参数", required = true)
                             @RequestBody(required = false) SearchVO searchVO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        issueService.exportIssues(projectId, searchVO, request, response, organizationId);
    }

    @ResponseBody
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出feature列表")
    @PostMapping(value = "/program/export")
    public void exportProgramIssues(@ApiParam(value = "项目id", required = true)
                                    @PathVariable(name = "project_id") Long projectId,
                                    @ApiParam(value = "组织id", required = true)
                                    @RequestParam Long organizationId,
                                    @ApiParam(value = "查询参数", required = true)
                                    @RequestBody(required = false) SearchVO searchVO,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        issueService.exportProgramIssues(projectId, searchVO, request, response, organizationId);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("复制一个issue")
    @PostMapping("/{issueId}/clone_issue")
    public ResponseEntity<IssueVO> cloneIssueByIssueId(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "issueId", required = true)
                                                        @PathVariable(name = "issueId") Long issueId,
                                                       @ApiParam(value = "组织id", required = true)
                                                        @RequestParam Long organizationId,
                                                       @ApiParam(value = "应用类型", required = true)
                                                        @RequestParam(value = "applyType") String applyType,
                                                       @ApiParam(value = "复制条件", required = true)
                                                        @RequestBody CopyConditionVO copyConditionVO) {
        return Optional.ofNullable(issueService.cloneIssueByIssueId(projectId, issueId, copyConditionVO, organizationId, applyType))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.cloneIssueByIssueId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue ids查询issue相关信息")
    @PostMapping("/issue_infos")
    public ResponseEntity<List<IssueInfoVO>> listByIssueIds(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "issue ids", required = true)
                                                             @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueService.listByIssueIds(projectId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueNums.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表提供给测试模块用")
    @CustomPageRequest
    @PostMapping(value = "/test_component/no_sub")
    public ResponseEntity<PageInfo<IssueListTestVO>> listIssueWithoutSubToTestComponent(@ApiIgnore
                                                                                     @ApiParam(value = "分页信息", required = true)
                                                                                     @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                             PageRequest pageRequest,
                                                                                        @ApiParam(value = "项目id", required = true)
                                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                                        @ApiParam(value = "组织id", required = true)
                                                                                     @RequestParam Long organizationId,
                                                                                        @ApiParam(value = "查询参数", required = true)
                                                                                     @RequestBody(required = false) SearchVO searchVO) {
        return Optional.ofNullable(issueService.listIssueWithoutSubToTestComponent(projectId, searchVO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithoutSubToTestComponent"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表, 测试项目接口，过滤linked issue")
    @CustomPageRequest
    @PostMapping(value = "/test_component/filter_linked")
    public ResponseEntity<PageInfo<IssueListTestWithSprintVersionVO>> listIssueWithLinkedIssues(@ApiIgnore
                                                                                             @ApiParam(value = "分页信息", required = true)
                                                                                             @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                                     PageRequest pageRequest,
                                                                                                @ApiParam(value = "项目id", required = true)
                                                                                             @PathVariable(name = "project_id") Long projectId,
                                                                                                @ApiParam(value = "组织id", required = true)
                                                                                             @RequestParam Long organizationId,
                                                                                                @ApiParam(value = "查询参数", required = true)
                                                                                             @RequestBody(required = false) SearchVO searchVO) {
        return Optional.ofNullable(issueService.listIssueWithLinkedIssues(projectId, searchVO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithBlockedIssues"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据时间段查询问题类型的数量")
    @GetMapping(value = "/type/{typeCode}")
    public ResponseEntity<List<IssueCreationNumVO>> queryIssueNumByTimeSlot(@ApiParam(value = "项目id", required = true)
                                                                             @PathVariable(name = "project_id") Long projectId,
                                                                            @ApiParam(value = "type code", required = true)
                                                                             @PathVariable String typeCode,
                                                                            @ApiParam(value = "时间段", required = true)
                                                                             @RequestParam Integer timeSlot) {
        return Optional.ofNullable(issueService.queryIssueNumByTimeSlot(projectId, typeCode, timeSlot))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.timeSlotCount.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "拖动epic位置")
    @PutMapping(value = "/epic_drag")
    public ResponseEntity<EpicDataVO> dragEpic(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "排序对象", required = true)
                                                @RequestBody EpicSequenceVO epicSequenceVO) {
        return Optional.ofNullable(issueService.dragEpic(projectId, epicSequenceVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueController.dragEpic"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计issue相关信息（测试模块用）")
    @PostMapping(value = "/test_component/statistic")
    public ResponseEntity<List<PieChartVO>> issueStatistic(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "查询类型(version、component、label)", required = true)
                                                            @RequestParam String type,
                                                           @ApiParam(value = "需要排除的issue类型列表")
                                                            @RequestBody List<String> issueTypes) {
        return Optional.ofNullable(issueService.issueStatistic(projectId, type, issueTypes))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.issueStatistic"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页过滤查询issue列表(不包含子任务，包含详情),测试模块用")
    @CustomPageRequest
    @PostMapping(value = "/test_component/no_sub_detail")
    public ResponseEntity<PageInfo<IssueComponentDetailDTO>> listIssueWithoutSubDetail(@ApiIgnore
                                                                                   @ApiParam(value = "分页信息", required = true)
                                                                                   @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                                           PageRequest pageRequest,
                                                                                       @ApiParam(value = "项目id", required = true)
                                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                                       @ApiParam(value = "查询参数", required = true)
                                                                                   @RequestBody(required = false) SearchVO searchVO) {
        return Optional.ofNullable(issueService.listIssueWithoutSubDetail(projectId, searchVO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listIssueWithoutSubDetail"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更改父任务")
    @PostMapping(value = "/update_parent")
    public ResponseEntity<IssueVO> updateIssueParentId(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "issue parent id update vo", required = true)
                                                        @RequestBody IssueUpdateParentIdVO issueUpdateParentIdVO) {
        return Optional.ofNullable(issueService.issueParentIdUpdate(projectId, issueUpdateParentIdVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueParentId.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计当前项目下未完成的任务数，包括故事、任务、缺陷")
    @GetMapping(value = "/count")
    public ResponseEntity<JSONObject> countUnResolveByProjectId(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.countUnResolveByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.countUnResolveIssue.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据条件过滤查询返回issueIds，测试项目接口")
    @PostMapping(value = "/issue_ids")
    public ResponseEntity<List<Long>> queryIssueIdsByOptions(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "查询参数", required = true)
                                                             @RequestBody SearchVO searchVO) {
        return Optional.ofNullable(issueService.queryIssueIdsByOptions(projectId, searchVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueIds.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询未分配的问题，类型为story,task,bug")
    @GetMapping(value = "/undistributed")
    public ResponseEntity<PageInfo<UndistributedIssueVO>> queryUnDistributedIssues(@ApiParam(value = "项目id", required = true)
                                                                                @PathVariable(name = "project_id") Long projectId,
                                                                                   @ApiParam(value = "分页信息", required = true)
                                                                                @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(issueService.queryUnDistributedIssues(projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.UndistributedIssueList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询经办人未完成的问题，类型为story,task,bug")
    @GetMapping(value = "/unfinished/{assignee_id}")
    public ResponseEntity<List<UnfinishedIssueVO>> queryUnfinishedIssues(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                         @ApiParam(value = "经办人id", required = true)
                                                                          @PathVariable(name = "assignee_id") Long assigneeId) {
        return Optional.ofNullable(issueService.queryUnfinishedIssues(projectId, assigneeId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.UnfinishedIssueList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询用户故事地图泳道")
    @GetMapping(value = "/storymap/swim_lane")
    public ResponseEntity<String> querySwimLaneCode(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.querySwimLaneCode(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.querySwimLaneCode.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("【测试专用】批量复制issue并生成版本信息")
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("【测试专用】issue按照项目分组接口")
    @GetMapping("/list_issues_by_project")
    public ResponseEntity<List<IssueProjectVO>> queryIssueTestGroupByProject(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueService.queryIssueTestGroupByProject())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryIssueTestGroupByProject"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询feature列表")
    @PostMapping(value = "/program")
    public ResponseEntity<PageInfo<FeatureCommonVO>> queryFeatureList(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                      @ApiParam(value = "组织id", required = true)
                                                                   @RequestParam Long organizationId,
                                                                      @ApiParam(value = "分页信息", required = true)
                                                                   @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                   @ApiIgnore PageRequest pageRequest,
                                                                      @ApiParam(value = "搜索DTO", required = true)
                                                                   @RequestBody SearchVO searchVO) {
        return Optional.ofNullable(issueService.queryFeatureList(projectId, organizationId, pageRequest, searchVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryFeatureList"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前pi的特性列表")
    @GetMapping(value = "/program/query_by_pi_id")
    public ResponseEntity<List<FeatureCommonVO>> queryFeatureListByPiId(@ApiParam(value = "项目id", required = true)
                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "组织id", required = true)
                                                                         @RequestParam Long organizationId,
                                                                        @ApiParam(value = "piId", required = true)
                                                                         @RequestParam Long piId) {
        return Optional.ofNullable(issueService.queryFeatureListByPiId(projectId, organizationId, piId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryFeatureListByPiId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("【测试专用】根据issueNum查询issue")
    @PostMapping(value = "/query_by_issue_num")
    public ResponseEntity<IssueNumDTO> queryIssueByIssueNum(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "issue编号", required = true)
                                                            @RequestBody String issueNum) {
        return Optional.ofNullable(issueService.queryIssueByIssueNum(projectId, issueNum))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryIssueByIssueNum"));
    }
}