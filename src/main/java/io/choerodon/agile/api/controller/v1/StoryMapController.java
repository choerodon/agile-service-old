package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.agile.api.dto.StoryMapDragDTO;
import io.choerodon.agile.app.service.StoryMapService;
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

import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/story_map")
public class StoryMapController {

    @Autowired
    private StoryMapService storyMapService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询故事地图整体")
    @PostMapping("/main")
    public ResponseEntity<JSONObject> queryStoryMap(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                    @ApiParam(value = "组织id", required = true)
                                                    @RequestParam Long organizationId,
                                                    @ApiParam(value = "search DTO", required = true)
                                                    @RequestBody SearchDTO searchDTO) {
        return Optional.ofNullable(storyMapService.queryStoryMap(projectId, organizationId, searchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.storyMap.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询故事地图需求池")
    @PostMapping("/demand")
    public ResponseEntity<JSONObject> queryStoryMapDemand(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "search DTO", required = true)
                                                          @RequestBody SearchDTO searchDTO) {
        return Optional.ofNullable(storyMapService.queryStoryMapDemand(projectId, searchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.storyMapDemand.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("故事地图移动卡片")
    @PostMapping(value = "/move")
    public ResponseEntity storyMapMove(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "story map drag DTO", required = true)
                                       @RequestBody StoryMapDragDTO storyMapDragDTO) {
        storyMapService.storyMapMove(projectId, storyMapDragDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
