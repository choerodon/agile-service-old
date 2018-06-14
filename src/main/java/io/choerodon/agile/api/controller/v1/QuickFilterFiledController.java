package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.QuickFilterFiledDTO;
import io.choerodon.agile.app.service.QuickFilterFiledService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/quick_filter_filed")
public class QuickFilterFiledController {

    @Autowired
    private QuickFilterFiledService quickFilterFiledService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("查询quick filter filed列表")
    @GetMapping
    public ResponseEntity<List<QuickFilterFiledDTO>> list(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(quickFilterFiledService.list(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilterFiled.list"));
    }

}
