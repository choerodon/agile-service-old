package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.FeedbackDataLogService;
import io.choerodon.agile.infra.dataobject.FeedbackDataLogDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/feedback_data_log")
public class FeedbackDataLogController {

    @Autowired
    private FeedbackDataLogService feedbackDataLogService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("根据反馈id查询活动日志")
    @GetMapping
    public ResponseEntity<List<FeedbackDataLogDTO>> listByFeedbackId(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "feedback id", required = true)
                                                                     @RequestParam Long feedbackId) {
        return new ResponseEntity<>(feedbackDataLogService.listByFeedbackId(projectId, feedbackId), HttpStatus.OK);
    }
}
