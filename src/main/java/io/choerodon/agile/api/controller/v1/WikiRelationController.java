package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.WikiMenuDTO;
import io.choerodon.agile.api.dto.WikiRelationDTO;
import io.choerodon.agile.app.service.WikiRelationService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
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
@RequestMapping(value = "/v1/projects/{project_id}/wiki_relation")
public class WikiRelationController {

    @Autowired
    private WikiRelationService wikiRelationService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("添加wiki relation")
    @PostMapping
    public ResponseEntity create(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "wiki relation dto list", required = true)
                                 @RequestBody List<WikiRelationDTO> wikiRelationDTOList) {
        wikiRelationService.create(projectId, wikiRelationDTOList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue id查询wiki relation")
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<JSONObject> create(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "issue id", required = true)
                                                        @PathVariable Long issueId) {
        return Optional.ofNullable(wikiRelationService.queryByIssueId(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.wikiRelationList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id删除wiki relation")
    @DeleteMapping("/{id}")
    public ResponseEntity update(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "wiki id", required = true)
                                 @PathVariable Long id) {
        wikiRelationService.deleteById(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询wiki menus列表")
    @PostMapping("/menus")
    public ResponseEntity<String> queryWikiMenus(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "wiki menu dto", required = true)
                                                                @RequestBody WikiMenuDTO wikiMenuDTO) {
        return Optional.ofNullable(wikiRelationService.queryWikiMenus(projectId, wikiMenuDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.wikiMenus.get"));
    }
}