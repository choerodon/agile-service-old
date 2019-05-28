package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/5/28.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/project_invoke_program")
public class ProjectInvokeProgramController {

    @Autowired
    private PiService piService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private DataLogService dataLogService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardFeatureService boardFeatureService;

    @Autowired
    private ArtService artService;


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询PI路线图")
    @GetMapping(value = "/road_map")
    public ResponseEntity<List<PiWithFeatureDTO>> queryRoadMapOfProgram(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "项目群id", required = true)
                                                                        @RequestParam Long programId,
                                                                        @ApiParam(value = "组织id", required = true)
                                                                        @RequestParam Long organizationId) {
        return Optional.ofNullable(piService.queryRoadMapOfProgram(programId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.roadMap.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询单个issue")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<IssueDTO> queryIssue(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "项目群id", required = true)
                                               @RequestParam Long programId,
                                               @ApiParam(value = "issueId", required = true)
                                               @PathVariable Long issueId,
                                               @ApiParam(value = "组织id", required = true)
                                               @RequestParam(required = false) Long organizationId) {
        return Optional.ofNullable(issueService.queryIssue(programId, issueId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryIssue"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询DataLog列表")
    @GetMapping(value = "/datalog")
    public ResponseEntity<List<DataLogDTO>> listByIssueId(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "项目群id", required = true)
                                                          @RequestParam Long programId,
                                                          @ApiParam(value = "issue id", required = true)
                                                          @RequestParam Long issueId) {
        return Optional.ofNullable(dataLogService.listByIssueId(programId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.dataLogList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据projectId查询项目下的board")
    @GetMapping("/board")
    public ResponseEntity<List<BoardDTO>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "项目群id", required = true)
                                                           @RequestParam Long programId) {
        return Optional.ofNullable(boardService.queryByProjectId(programId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.boardList.get"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("all board data for program")
    @PostMapping(value = "/{boardId}/all_data_program/{organization_id}")
    public ResponseEntity<JSONObject> queryByOptionsInProgram(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                              @ApiParam(value = "项目群id", required = true)
                                                              @RequestParam Long programId,
                                                              @ApiParam(value = "agile board id", required = true)
                                                              @PathVariable Long boardId,
                                                              @ApiParam(value = "组织id", required = true)
                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                              @ApiParam(name = "search DTO", required = false)
                                                              @RequestBody SearchDTO searchDTO) {
        return Optional.ofNullable(boardService.queryByOptionsInProgram(programId, boardId, organizationId, searchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.programBoard.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取公告板所有信息")
    @GetMapping(value = "/query_board_info")
    public ResponseEntity<ProgramBoardInfoDTO> queryBoardInfo(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                              @ApiParam(value = "项目群id", required = true)
                                                              @RequestParam Long programId) {
        return Optional.ofNullable(boardFeatureService.queryBoardInfo(programId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardFeature.queryBoardInfo"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询当前活跃art")
    @GetMapping("/art/active")
    public ResponseEntity<ArtDTO> queryActiveArt(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "项目群id", required = true)
                                                 @RequestParam Long programId) {
        return Optional.ofNullable(artService.queryActiveArt(programId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.activeArt.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询art日历")
    @GetMapping("/art_calendar")
    public ResponseEntity<List<PiCalendarDTO>> queryArtCalendar(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "项目群id", required = true)
                                                                @RequestParam Long programId,
                                                                @ApiParam(value = "art id")
                                                                @RequestParam Long id) {
        return Optional.ofNullable(artService.queryArtCalendar(programId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artCalendar.get"));
    }
}