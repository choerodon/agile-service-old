package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.StoryMapWidthVO;
import io.choerodon.agile.app.service.StoryMapWidthService;
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
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/story_map_width")
public class StoryMapWidthController {

    @Autowired
    private StoryMapWidthService storyMapWidthService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("创建故事地图列宽度")
    @PostMapping
    public ResponseEntity<StoryMapWidthVO> create(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "故事地图宽度DTO", required = true)
                                                          @RequestBody StoryMapWidthVO storyMapWidthVO) {
        return Optional.ofNullable(storyMapWidthService.create(projectId, storyMapWidthVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.storyMapWidth.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("更新故事地图列宽度")
    @PutMapping
    public ResponseEntity<StoryMapWidthVO> update(@ApiParam(value = "项目id", required = true)
                                                   @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "故事地图宽度DTO", required = true)
                                                   @RequestBody StoryMapWidthVO storyMapWidthVO) {
        return Optional.ofNullable(storyMapWidthService.update(projectId, storyMapWidthVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.storyMapWidth.update"));
    }
}
