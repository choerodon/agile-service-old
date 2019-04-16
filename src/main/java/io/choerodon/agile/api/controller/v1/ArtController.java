package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ArtDTO;
import io.choerodon.agile.api.dto.ArtStopDTO;
import io.choerodon.agile.api.dto.PiCreateDTO;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.infra.dataobject.PiCalendarDO;
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
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/art")
public class ArtController {

    @Autowired
    private ArtService artService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建art")
    @PostMapping
    public ResponseEntity<ArtDTO> createArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art dto", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.createArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.create"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("开始art")
    @PostMapping("/start")
    public ResponseEntity<ArtDTO> startArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art dto", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.startArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.start"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("stop art")
    @PostMapping("/stop")
    public ResponseEntity<ArtDTO> stopArt(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "art dto", required = true)
                                           @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.stopArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.stop"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改art")
    @PutMapping
    public ResponseEntity<ArtDTO> updateArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art dto", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.updateArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除art")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteArt(@ApiParam(value = "项目id", required = true)
                                    @PathVariable(name = "project_id") Long projectId,
                                    @ApiParam(value = "art id", required = true)
                                    @PathVariable Long id) {
        artService.deleteArt(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询art列表")
    @GetMapping("/list")
    public ResponseEntity<Page<ArtDTO>> queryArtList(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "分页信息", required = true)
                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                     @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(artService.queryArtList(projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询单个art")
    @GetMapping
    public ResponseEntity<ArtDTO> queryArt(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "art id")
                                           @RequestParam Long id) {
        return Optional.ofNullable(artService.queryArt(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.art.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建art pi")
    @PostMapping("/create_other_pi")
    public ResponseEntity createOtherPi(@ApiParam(value = "项目id", required = true)
                                        @PathVariable(name = "project_id") Long projectId,
                                        @ApiParam(value = "pi create dto", required = true)
                                        @RequestBody PiCreateDTO piCreateDTO) {
        artService.createOtherPi(projectId, piCreateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询art日历")
    @GetMapping("/art_calendar")
    public ResponseEntity<List<PiCalendarDO>> queryArtCalendar(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "art id")
                                                               @RequestParam Long id) {
        return Optional.ofNullable(artService.queryArtCalendar(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artCalendar.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("stop art之前调用")
    @GetMapping("/before_stop")
    public ResponseEntity<ArtStopDTO> beforeStop(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "art id")
                                                 @RequestParam Long id) {
        return Optional.ofNullable(artService.beforeStop(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.beforeComplete.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("ART重名校验")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "art name", required = true)
                                             @RequestParam String artName) {
        return Optional.ofNullable(artService.checkName(projectId, artName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artName.check"));
    }

}
