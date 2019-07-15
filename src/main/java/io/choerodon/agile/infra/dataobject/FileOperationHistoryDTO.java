package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_file_operation_history")
public class FileOperationHistoryDTO extends BaseDTO {

    public FileOperationHistoryDTO() {}

    public FileOperationHistoryDTO(Long projectId, Long userId, String action, Long successCount, Long failCount, String status) {
        this.projectId = projectId;
        this.userId = userId;
        this.action = action;
        this.successCount = successCount;
        this.failCount = failCount;
        this.status = status;
    }

    public FileOperationHistoryDTO(Long projectId, Long id, String action, String status, Long objectVersionNumber) {
        this.projectId = projectId;
        this.id = id;
        this.action = action;
        this.status = status;
        this.objectVersionNumber = objectVersionNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private Long userId;

    private String action;

    private Long successCount;

    private Long failCount;

    private String status;

    private String fileUrl;

    private Long objectVersionNumber;

    @Transient
    private Double process;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setProcess(Double process) {
        this.process = process;
    }

    public Double getProcess() {
        return process;
    }
}
