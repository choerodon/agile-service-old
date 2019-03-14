package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ArtDTO;
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
    @ApiOperation("发布art")
    @PostMapping("/release_art")
    public ResponseEntity<ArtDTO> releaseArt(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "art id", required = true)
                                             @RequestParam Long artId,
                                             @ApiParam(value = "pi number", required = true)
                                             @RequestParam Long piNumber) {
        return Optional.ofNullable(artService.releaseArt(projectId, artId, piNumber))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.release"));
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

}
