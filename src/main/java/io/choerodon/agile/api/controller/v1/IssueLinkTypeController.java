package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.IssueLinkTypeDTO;
import io.choerodon.agile.app.service.IssueLinkTypeService;
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
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/issue_link_types")
public class IssueLinkTypeController {

    @Autowired
    private IssueLinkTypeService issueLinkTypeService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("根据issueId查询issueLink")
    @GetMapping
    public ResponseEntity<List<IssueLinkTypeDTO>> listIssueLinkType(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueLinkTypeService.listIssueLinkType())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLinkType.listIssueLinkType"));
    }
}
