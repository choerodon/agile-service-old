package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.FeedbackUpdateVO;
import io.choerodon.agile.api.vo.SearchVO;
import io.choerodon.agile.app.service.FeedbackService;
import io.choerodon.agile.infra.common.utils.VerifyUpdateUtil;
import io.choerodon.agile.infra.dataobject.FeedbackDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
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
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Permission(permissionPublic = true)
    @ApiOperation("创建feedback")
    @PostMapping("/feedback")
    public ResponseEntity<FeedbackDTO> createFeedback(@ApiParam(value = "feedback vo", required = true)
                                                      @RequestBody FeedbackDTO feedbackDTO) {
        return Optional.ofNullable(feedbackService.createFeedback(feedbackDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feedback.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("更新feedback")
    @PutMapping("/projects/{project_id}/feedback")
    public ResponseEntity<FeedbackDTO> updateFeedback(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "feedback vo", required = true)
                                                      @RequestBody JSONObject feedbackUpdate) {
        FeedbackUpdateVO feedbackUpdateVO = new FeedbackUpdateVO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(feedbackUpdate, feedbackUpdateVO);
        return Optional.ofNullable(feedbackService.updateFeedback(projectId, feedbackUpdateVO, fieldList))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feedback.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("根据项目id查询feedback")
    @PostMapping("/projects/{project_id}/feedback/list")
    public ResponseEntity<PageInfo<FeedbackDTO>> queryFeedbackByPage(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "页", required = false)
                                                                     @RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                                     @ApiParam(value = "每页数量", required = false)
                                                                     @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                                     @ApiParam(value = "search DTO", required = true)
                                                                     @RequestBody SearchVO searchVO) {
        return new ResponseEntity<>(feedbackService.queryFeedbackByPage(projectId, page, size, searchVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("查询feedback详情")
    @GetMapping("/projects/{project_id}/feedback/{id}")
    public ResponseEntity<FeedbackDTO> queryFeedbackById(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "组织id", required = true)
                                                         @RequestParam Long organizationId,
                                                         @ApiParam(value = "feedback id", required = true)
                                                         @PathVariable Long id) {
        return new ResponseEntity<>(feedbackService.queryFeedbackById(projectId, organizationId, id), HttpStatus.OK);
    }


}
