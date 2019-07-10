package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.BoardSprintAttrVO;
import io.choerodon.agile.app.service.BoardSprintAttrService;
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
 * @author shinan.chen
 * @since 2019/5/14
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board_sprint_attr")
public class BoardSprintAttrController {
    @Autowired
    private BoardSprintAttrService boardSprintAttrService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改公告板冲刺列宽")
    @GetMapping(value = "/update")
    public ResponseEntity<BoardSprintAttrVO> updateColumnWidth(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "sprintId", required = true)
                                                                @RequestParam Long sprintId,
                                                               @ApiParam(value = "columnWidth", required = true)
                                                                @RequestParam Integer columnWidth) {
        return Optional.ofNullable(boardSprintAttrService.updateColumnWidth(projectId, sprintId, columnWidth))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardSprintAttr.updateColumnWidth"));
    }
}
