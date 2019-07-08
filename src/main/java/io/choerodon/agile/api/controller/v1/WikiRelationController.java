package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.WikiRelationDTO;
import io.choerodon.agile.app.service.WikiRelationService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/12/03.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/knowledge_relation")
public class WikiRelationController {

    @Autowired
    private WikiRelationService wikiRelationService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("添加knowledge relation")
    @PostMapping
    public ResponseEntity create(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "knowledge relation vo list", required = true)
                                 @RequestBody List<WikiRelationDTO> wikiRelationDTOList) {
        wikiRelationService.create(projectId, wikiRelationDTOList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue id查询knowledge relation")
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<JSONObject> queryByIssueId(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "issue id", required = true)
                                                     @PathVariable Long issueId) {
        return Optional.ofNullable(wikiRelationService.queryByIssueId(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.knowledgeRelationList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id删除knowledge relation")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "relation id", required = true)
                                     @PathVariable Long id) {
        wikiRelationService.deleteById(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}