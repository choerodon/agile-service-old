package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.infra.common.utils.VerifyUpdateUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.agile.app.service.ProductVersionService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/product_version")
public class ProductVersionController {

    private static final String CREATE_ERROR = "error.version.create";
    private static final String UPDATE_ERROR = "error.version.update";
    private static final String DELETE_ERROR = "error.version.delete";
    private static final String QUERY_ERROR = "error.version.query";
    private static final String CHECK_ERROR = "error.version.check";
    private static final String QUERY_VERSION_ERROR = "error.versionData.query";
    private static final String VERSION_STATISTICS_ERROR = "error.versionStatistics.query";
    private static final String QUERY_PLAN_VERSION_NAME_ERROR = "error.planVersionName.query";
    private static final String QUERY_VERSION_NAME_ERROR = "error.versionName.query";
    private static final String QUERY_ISSUE_ERROR = "error.issue.query";
    private static final String RELEASE_ERROR = "error.productVersion.release";
    private static final String REVOKE_RELEASE_ERROR = "error.productVersion.revokeRelease";
    private static final String ARCHIVED_ERROR = "error.productVersion.archived";
    private static final String REVOKE_ARCHIVED_ERROR = "error.productVersion.revokeArchived";

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("创建version")
    @PostMapping
    public ResponseEntity<ProductVersionDetailDTO> createVersion(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "releasePlan信息", required = true)
                                                                 @RequestBody @Valid ProductVersionCreateDTO versionCreateDTO) {
        return Optional.ofNullable(productVersionService.createVersion(projectId, versionCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(CREATE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("更新version")
    @PutMapping(value = "/{versionId}")
    public ResponseEntity<ProductVersionDetailDTO> updateVersion(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "versionId", required = true)
                                                                 @PathVariable Long versionId,
                                                                 @ApiParam(value = "version信息", required = true)
                                                                 @RequestBody JSONObject versionUpdateDTO) {
        ProductVersionUpdateDTO productVersionUpdate = new ProductVersionUpdateDTO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(versionUpdateDTO, productVersionUpdate);
        return Optional.ofNullable(productVersionService.updateVersion(projectId, versionId, productVersionUpdate, fieldList))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(UPDATE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("根据id删除version")
    @DeleteMapping(value = "/{versionId}")
    public ResponseEntity<Boolean> deleteVersion(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "versionId", required = true)
                                                 @PathVariable Long versionId,
                                                 @ApiParam(value = "修复版本issue目标版本", required = false)
                                                 @RequestParam(required = false, name = "fixTargetVersionId") Long fixTargetVersionId,
                                                 @ApiParam(value = "影响版本issue目标版本", required = false)
                                                 @RequestParam(required = false, name = "influenceTargetVersionId") Long influenceTargetVersionId) {
        return Optional.ofNullable(productVersionService.deleteVersion(projectId, versionId, fixTargetVersionId, influenceTargetVersionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.NO_CONTENT))
                .orElseThrow(() -> new CommonException(DELETE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @CustomPageRequest
    @ApiOperation(value = "根据项目id查找version")
    @PostMapping(value = "/versions")
    public ResponseEntity<Page<ProductVersionPageDTO>> listByOptions(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "查询参数", required = false)
                                                                     @RequestBody(required = false) Map<String, Object> searchParamMap,
                                                                     @ApiParam(value = "分页信息", required = true)
                                                                     @SortDefault(value = "versionId", direction = Sort.Direction.DESC)
                                                                     @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(productVersionService.queryByProjectId(projectId, pageRequest, searchParamMap))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "是否重名")
    @GetMapping(value = "/{name}/check")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "name", required = true)
                                             @PathVariable String name) {
        return Optional.ofNullable(productVersionService.repeatName(projectId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(CHECK_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "backlog页面查询所有版本")
    @GetMapping
    public ResponseEntity<List<ProductVersionDataDTO>> queryVersionByProjectId(@ApiParam(value = "项目id", required = true)
                                                                               @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(productVersionService.queryVersionByprojectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_VERSION_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "版本详情及issue统计信息")
    @GetMapping(value = "/{versionId}")
    public ResponseEntity<ProductVersionStatisticsDTO> queryVersionStatisticsByVersionId(@ApiParam(value = "项目id", required = true)
                                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                                         @ApiParam(value = "versionId", required = true)
                                                                                         @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.queryVersionStatisticsByVersionId(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(VERSION_STATISTICS_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "获取版本下指定状态的issue")
    @GetMapping(value = "/{versionId}/issues")
    public ResponseEntity<List<IssueListDTO>> queryByVersionIdAndStatusCode(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                            @ApiParam(value = "versionId", required = true)
                                                                            @PathVariable Long versionId,
                                                                            @ApiParam(value = "issue状态码", required = false)
                                                                            @RequestParam(required = false) String statusCode) {
        return Optional.ofNullable(productVersionService.queryIssueByVersionIdAndStatusCode(projectId, versionId, statusCode))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ISSUE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询规划中版本名及要发布版本未完成issue统计")
    @GetMapping(value = "/{versionId}/plan_names")
    public ResponseEntity<VersionMessageDTO> queryReleaseMessageByVersionId(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                            @ApiParam(value = "versionId", required = true)
                                                                            @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.queryReleaseMessageByVersionId(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_PLAN_VERSION_NAME_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "发布版本")
    @PostMapping(value = "/release")
    public ResponseEntity<ProductVersionDetailDTO> releaseVersion(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "发布版本信息", required = true)
                                                                  @RequestBody @Valid ProductVersionReleaseDTO productVersionRelease) {
        return Optional.ofNullable(productVersionService.releaseVersion(projectId, productVersionRelease))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(RELEASE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "撤销发布版本")
    @PostMapping(value = "/{versionId}/revoke_release")
    public ResponseEntity<ProductVersionDetailDTO> revokeReleaseVersion(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "版本id", required = true)
                                                                        @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.revokeReleaseVersion(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(REVOKE_RELEASE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "归档版本")
    @PostMapping(value = "/{versionId}/archived")
    public ResponseEntity<ProductVersionDetailDTO> archivedVersion(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "版本id", required = true)
                                                                   @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.archivedVersion(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(ARCHIVED_ERROR));
    }


    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "撤销归档版本")
    @PostMapping(value = "/{versionId}/revoke_archived")
    public ResponseEntity<ProductVersionDetailDTO> revokeArchivedVersion(@ApiParam(value = "项目id", required = true)
                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                         @ApiParam(value = "版本id", required = true)
                                                                         @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.revokeArchivedVersion(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(REVOKE_ARCHIVED_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询所有版本名及要删除版本issue统计")
    @GetMapping(value = "/{versionId}/names")
    public ResponseEntity<VersionMessageDTO> queryDeleteMessageByVersionId(@ApiParam(value = "项目id", required = true)
                                                                           @PathVariable(name = "project_id") Long projectId,
                                                                           @ApiParam(value = "versionId", required = true)
                                                                           @PathVariable Long versionId) {
        return Optional.ofNullable(productVersionService.queryDeleteMessageByVersionId(projectId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_VERSION_NAME_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据状态查询档版本名")
    @PostMapping(value = "/names")
    public ResponseEntity<List<ProductVersionNameDTO>> queryNameByOptions(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          @ApiParam(value = "状态列表", required = false)
                                                                          @RequestBody(required = false) List<String> statusCodes) {
        return Optional.ofNullable(productVersionService.queryNameByOptions(projectId, statusCodes))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_VERSION_NAME_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据项目id查找version列表")
    @GetMapping(value = "/versions")
    public ResponseEntity<List<ProductVersionDTO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(productVersionService.listByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "合并版本")
    @PostMapping(value = "/merge")
    public ResponseEntity<Boolean> mergeVersion(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "合并版本信息", required = true)
                                                @RequestBody @Valid ProductVersionMergeDTO productVersionMergeDTO) {
        return Optional.ofNullable(productVersionService.mergeVersion(projectId, productVersionMergeDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(REVOKE_ARCHIVED_ERROR));
    }
}
