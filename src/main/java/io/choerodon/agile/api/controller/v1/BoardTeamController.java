package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.BoardTeamVO;
import io.choerodon.agile.api.vo.BoardTeamUpdateVO;
import io.choerodon.agile.app.service.BoardTeamService;
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
 * @since 2019/5/20
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board_team")
public class BoardTeamController {
    @Autowired
    private BoardTeamService boardTeamService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动公告板团队")
    @PutMapping(value = "/{boardTeamId}")
    public ResponseEntity<BoardTeamVO> update(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "boardTeamId", required = true)
                                               @PathVariable Long boardTeamId,
                                              @ApiParam(value = "updateDTO", required = true)
                                               @RequestBody BoardTeamUpdateVO updateVO) {
        return Optional.ofNullable(boardTeamService.update(projectId, boardTeamId, updateVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardTeam.update"));
    }
}
