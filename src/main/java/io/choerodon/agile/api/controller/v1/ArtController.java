package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.ArtDTO;
import io.choerodon.agile.api.vo.ArtStopDTO;
import io.choerodon.agile.api.vo.PiCalendarDTO;
import io.choerodon.agile.api.vo.PiCreateDTO;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建art")
    @PostMapping
    public ResponseEntity<ArtDTO> createArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art vo", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.createArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("开始art")
    @PostMapping("/start")
    public ResponseEntity<ArtDTO> startArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art vo", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.startArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.start"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("stop art")
    @PostMapping("/stop")
    public ResponseEntity<ArtDTO> stopArt(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "art vo", required = true)
                                           @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.stopArt(projectId, artDTO, true))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.stop"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改art")
    @PutMapping
    public ResponseEntity<ArtDTO> updateArt(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "art vo", required = true)
                                            @RequestBody ArtDTO artDTO) {
        return Optional.ofNullable(artService.updateArt(projectId, artDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.art.update"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询art列表")
    @GetMapping("/list")
    public ResponseEntity<PageInfo<ArtDTO>> queryArtList(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "分页信息", required = true)
                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                     @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(artService.queryArtList(projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建art pi")
    @PostMapping("/create_other_pi")
    public ResponseEntity createOtherPi(@ApiParam(value = "项目id", required = true)
                                        @PathVariable(name = "project_id") Long projectId,
                                        @ApiParam(value = "pi create vo", required = true)
                                        @RequestBody PiCreateDTO piCreateDTO) {
        artService.createOtherPi(projectId, piCreateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询art日历")
    @GetMapping("/art_calendar")
    public ResponseEntity<List<PiCalendarDTO>> queryArtCalendar(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "art id")
                                                                @RequestParam Long id) {
        return Optional.ofNullable(artService.queryArtCalendar(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.artCalendar.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询art列表,不分页")
    @GetMapping("/all")
    public ResponseEntity<List<ArtDTO>> queryAllArtList(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(artService.queryAllArtList(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.allArtList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询当前活跃art")
    @GetMapping("/active")
    public ResponseEntity<ArtDTO> queryActiveArt(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(artService.queryActiveArt(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.activeArt.get"));
    }

}
