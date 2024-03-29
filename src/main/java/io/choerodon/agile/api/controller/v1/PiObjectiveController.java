package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.PiObjectiveVO;
import io.choerodon.agile.app.service.PiObjectiveService;
import io.choerodon.agile.infra.dataobject.PiObjectiveDTO;
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
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/pi_objective")
public class PiObjectiveController {

    @Autowired
    private PiObjectiveService piObjectiveService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("创建PI objective")
    @PostMapping
    public ResponseEntity<PiObjectiveDTO> createPiObjective(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "pi objective vo", required = true)
                                                            @RequestBody PiObjectiveDTO piObjectiveDTO) {
        return Optional.ofNullable(piObjectiveService.createPiObjective(projectId, piObjectiveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.piObjective.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("修改PI objective")
    @PutMapping
    public ResponseEntity<PiObjectiveDTO> updatePiObjective(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "pi objective vo", required = true)
                                                            @RequestBody PiObjectiveDTO piObjectiveDTO) {
        return Optional.ofNullable(piObjectiveService.updatePiObjective(projectId, piObjectiveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.piObjective.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("删除PI objective")
    @DeleteMapping("/{id}")
    public ResponseEntity deletePiObjective(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "pi objective id", required = true)
                                            @PathVariable Long id) {
        piObjectiveService.deletePiObjective(projectId, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询单个PI objective")
    @GetMapping
    public ResponseEntity<PiObjectiveVO> queryPiObjective(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "pi objective id", required = true)
                                                           @RequestParam Long id) {
        return Optional.ofNullable(piObjectiveService.queryPiObjective(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.piObjective.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询PI objective列表")
    @GetMapping("/list")
    public ResponseEntity<JSONObject> queryPiObjectiveList(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "pi id", required = true)
                                                           @RequestParam Long piId) {
        return Optional.ofNullable(piObjectiveService.queryPiObjectiveList(projectId, piId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.piObjectiveList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("项目层下查询PI objective列表")
    @GetMapping("/list_by_project")
    public ResponseEntity<List<PiObjectiveVO>> queryPiObjectiveListByProject(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "project_id") Long projectId,
                                                                             @ApiParam(value = "pi id", required = true)
                                                                              @RequestParam Long piId) {
        return Optional.ofNullable(piObjectiveService.queryPiObjectiveListByProject(projectId, piId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.piObjectiveListByProject.get"));
    }


}
