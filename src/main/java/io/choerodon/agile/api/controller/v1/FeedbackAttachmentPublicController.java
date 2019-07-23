package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.FeedbackAttachmentService;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/feedback_attachment")
public class FeedbackAttachmentPublicController {

    @Autowired
    private FeedbackAttachmentService feedbackAttachmentService;

    @Permission(permissionPublic = true)
    @ApiOperation("上传附件")
    @PostMapping
    public ResponseEntity<List<FeedbackAttachmentDTO>> uploadAttachmentPublic(@ApiParam(value = "feedback id", required = true)
                                                                              @RequestParam Long feedbackId,
                                                                              @ApiParam(value = "token", required = true)
                                                                              @RequestParam String token,
                                                                              HttpServletRequest request) {
        return Optional.ofNullable(feedbackAttachmentService.uploadAttachmentPublic(feedbackId, token, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachmentPublic.upload"));
    }
}
