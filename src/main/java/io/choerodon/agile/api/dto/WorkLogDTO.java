package io.choerodon.agile.api.dto;


import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
public class WorkLogDTO {

    private Long logId;

    private BigDecimal workTime;

    private Date startDate;

    private String description;

    private Long issueId;

    private Long projectId;

    private Long objectVersionNumber;

    private String residualPrediction;

    private BigDecimal predictionTime;

    private Long userId;

    private String userName;

    private Date lastUpdateDate;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public void setWorkTime(BigDecimal workTime) {
        this.workTime = workTime;
    }

    public BigDecimal getWorkTime() {
        return workTime;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setResidualPrediction(String residualPrediction) {
        this.residualPrediction = residualPrediction;
    }

    public String getResidualPrediction() {
        return residualPrediction;
    }

    public void setPredictionTime(BigDecimal predictionTime) {
        this.predictionTime = predictionTime;
    }

    public BigDecimal getPredictionTime() {
        return predictionTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
