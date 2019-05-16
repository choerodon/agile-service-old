package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.MessageDTO;
import io.choerodon.agile.app.service.NoticeService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/10/8.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目下的通知")
    @GetMapping
    public ResponseEntity<List<MessageDTO>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(noticeService.queryByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.message.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新项目下的通知")
    @PutMapping
    public ResponseEntity updateNotice(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "修改的设置", required = true)
                                       @RequestBody List<MessageDTO> messageDTOList) {
        noticeService.updateNotice(projectId, messageDTOList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
