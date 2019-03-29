package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.MoveIssueDTO;
import io.choerodon.agile.api.dto.PiCompleteCountDTO;
import io.choerodon.agile.api.dto.PiDTO;
import io.choerodon.agile.app.service.PiService;
import io.choerodon.agile.infra.dataobject.SubFeatureDO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/pi")
public class PiController {

    @Autowired
    private PiService piService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("修改pi")
    @PutMapping
    public ResponseEntity<PiDTO> updatePi(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "pi dto", required = true)
                                          @RequestBody PiDTO piDTO) {
        return Optional.ofNullable(piService.updatePi(projectId, piDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.pi.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("待办页面查询所有pi详情")
    @PostMapping("/backlog_pi_list")
    public ResponseEntity<JSONObject> queryBacklogAll(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "组织id", required = true)
                                                      @RequestParam(required = true) Long organizationId,
                                                      @ApiParam(value = "查询参数", required = false)
                                                      @RequestBody(required = false) Map<String, Object> searchParamMap) {
        return Optional.ofNullable(piService.queryBacklogAll(projectId, organizationId, searchParamMap))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.backlogAll.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("pi页面查询所有pi简要列表")
    @GetMapping("/list")
    public ResponseEntity<Page<PiDTO>> queryAll(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "分页信息", required = true)
                                                @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(piService.queryAll(projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.piDTO.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("开启pi")
    @PostMapping("/start")
    public ResponseEntity<PiDTO> startPi(@ApiParam(value = "项目id", required = true)
                                         @PathVariable(name = "project_id") Long projectId,
                                         @ApiParam(value = "pi dto", required = true)
                                         @RequestBody PiDTO piDTO) {
        return Optional.ofNullable(piService.startPi(projectId, piDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.pi.start"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("关闭pi之前统计数量")
    @GetMapping("/before_close")
    public ResponseEntity<PiCompleteCountDTO> beforeClosePi(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "pi id", required = true)
                                                            @RequestParam Long piId,
                                                            @ApiParam(value = "art id", required = true)
                                                            @RequestParam Long artId) {
        return Optional.ofNullable(piService.beforeClosePi(projectId, piId, artId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.pi.beforeClose"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("关闭pi")
    @PostMapping("/close")
    public ResponseEntity<PiDTO> closePi(@ApiParam(value = "项目id", required = true)
                                         @PathVariable(name = "project_id") Long projectId,
                                         @ApiParam(value = "pi dto", required = true)
                                         @RequestBody PiDTO piDTO) {
        return Optional.ofNullable(piService.closePi(projectId, piDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.pi.close"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("feature批量拖动到pi")
    @PostMapping(value = "/to_pi/{piId}")
    public ResponseEntity<List<SubFeatureDO>> batchFeatureToPi(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "pi id", required = true)
                                                               @PathVariable Long piId,
                                                               @ApiParam(value = "移卡信息", required = true)
                                                               @RequestBody MoveIssueDTO moveIssueDTO) {
        return Optional.ofNullable(piService.batchFeatureToPi(projectId, piId, moveIssueDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feaature.batchToPi"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("feature批量拖动到epic")
    @PostMapping(value = "/to_epic/{epicId}")
    public ResponseEntity<List<SubFeatureDO>> batchFeatureToEpic(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "epic id", required = true)
                                                                 @PathVariable Long epicId,
                                                                 @ApiParam(value = "移卡信息", required = true)
                                                                 @RequestBody List<Long> featureIds) {
        return Optional.ofNullable(piService.batchFeatureToEpic(projectId, epicId, featureIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feature.batchToEpic"));
    }
}
