package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.FileOperationHistoryVO;
import io.choerodon.agile.app.service.ExcelService;
import io.choerodon.agile.infra.common.utils.ExcelUtil;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("下载导入模版")
    @GetMapping(value = "/download")
    public void download(@ApiParam(value = "项目id", required = true)
                         @PathVariable(name = "project_id") Long projectId,
                             @ApiParam(value = "组织id", required = true)
                         @RequestParam Long organizationId,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        excelService.download(projectId, organizationId, request, response);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导入issue")
    @PostMapping(value = "/import")
    public ResponseEntity batchImport(@ApiParam(value = "项目id", required = true)
                                      @PathVariable(name = "project_id") Long projectId,
                                      @ApiParam(value = "组织id", required = true)
                                      @RequestParam Long organizationId,
                                      @ApiParam(value = "user id", required = true)
                                      @RequestParam Long userId,
                                      @ApiParam(value = "导入文件", required = true)
                                      @RequestParam("file") MultipartFile file) {
        excelService.batchImport(projectId, organizationId, userId, ExcelUtil.getWorkbookFromMultipartFile(ExcelUtil.Mode.XSSF, file));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("取消导入")
    @PutMapping(value = "/cancel")
    public ResponseEntity cancelImport(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "file history id", required = true)
                                       @RequestParam Long id,
                                       @ApiParam(value = "objectVersionNumber", required = true)
                                       @RequestParam Long objectVersionNumber) {
        excelService.cancelImport(projectId, id, objectVersionNumber);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询最近的上传记录")
    @GetMapping(value = "/latest")
    public ResponseEntity<FileOperationHistoryVO> queryLatestRecode(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(excelService.queryLatestRecode(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.ImportHistoryRecode.get"));
    }

}
