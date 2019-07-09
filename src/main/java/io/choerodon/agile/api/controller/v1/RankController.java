package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.RankVO;
import io.choerodon.agile.app.service.RankService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/24.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("史诗、特性移动")
    @PostMapping
    public ResponseEntity epicAndFeatureRank(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "epic and feature rank DTO", required = true)
                                             @RequestBody RankVO rankVO) {
        rankService.epicAndFeatureRank(projectId, rankVO);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
