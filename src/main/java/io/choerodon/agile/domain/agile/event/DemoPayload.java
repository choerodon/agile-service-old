package io.choerodon.agile.domain.agile.event;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/14.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoPayload {

    private Long projectId;

    private Long organizationId;

    private Long versionId;

    private Long userId;

    private List<Long> testIssueIds;

    private Date dateOne;   //第一个迭代第六个工作日

    private Date dateTwo;   //第一个迭代第八个工作日

    private Date dateThree; //第一个迭代第十个工作日

    private Date dateFour;  //第二个迭代第一个工作日

    private Date dateFive;  //第二个迭代第三个工作日

    private Date dateSix;   //第二个迭代第五个工作日

    public void setTestIssueIds(List<Long> testIssueIds) {
        this.testIssueIds = testIssueIds;
    }

    public List<Long> getTestIssueIds() {
        return testIssueIds;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Date getDateOne() {
        return dateOne;
    }

    public void setDateOne(Date dateOne) {
        this.dateOne = dateOne;
    }

    public Date getDateTwo() {
        return dateTwo;
    }

    public void setDateTwo(Date dateTwo) {
        this.dateTwo = dateTwo;
    }

    public Date getDateThree() {
        return dateThree;
    }

    public void setDateThree(Date dateThree) {
        this.dateThree = dateThree;
    }

    public Date getDateFour() {
        return dateFour;
    }

    public void setDateFour(Date dateFour) {
        this.dateFour = dateFour;
    }

    public Date getDateFive() {
        return dateFive;
    }

    public void setDateFive(Date dateFive) {
        this.dateFive = dateFive;
    }

    public Date getDateSix() {
        return dateSix;
    }

    public void setDateSix(Date dateSix) {
        this.dateSix = dateSix;
    }
}
