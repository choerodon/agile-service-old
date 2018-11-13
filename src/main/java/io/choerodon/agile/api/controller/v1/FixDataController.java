package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/13.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/fix_data")
public class FixDataController {

    @Autowired
    private IssueStatusService issueStatusService;

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER, InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation("迁移数据，查询所有状态，执行1")
    @GetMapping(value = "/move_status")
    public ResponseEntity moveStatus(Boolean isFixStatus) {
        issueStatusService.moveStatus(isFixStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_DEVELOPER, InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation("迁移数据，查询所有状态，执行2")
    @GetMapping(value = "/update_all_data")
    public ResponseEntity updateAllData() {
        issueStatusService.updateAllData();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
