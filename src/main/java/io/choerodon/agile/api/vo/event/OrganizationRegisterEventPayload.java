package io.choerodon.agile.api.vo.event;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/27.
 * Email: fuqianghuang01@gmail.com
 */
public class OrganizationRegisterEventPayload {
    private Organization organization;
    private User user;
    private User userA;
    private User userB;
    private Project project;
    private TestData testData;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }

    public TestData getTestData() {
        return testData;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public static class User {
        private Long id;
        private String loginName;
        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Project {
        private Long id;
        private String code;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Organization {
        private Long id;
        private String code;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TestData {
        private List<Long> testIssueIds;
        private Long versionId;
        private Date dateOne;   //第一个迭代第六个工作日
        private Date dateTwo;   //第一个迭代第八个工作日
        private Date dateThree; //第一个迭代第十个工作日
        private Date dateFour;  //第二个迭代第一个工作日
        private Date dateFive;  //第二个迭代第三个工作日
        private Date dateSix;   //第二个迭代第五个工作日

        public List<Long> getTestIssueIds() {
            return testIssueIds;
        }

        public void setTestIssueIds(List<Long> testIssueIds) {
            this.testIssueIds = testIssueIds;
        }

        public Long getVersionId() {
            return versionId;
        }

        public void setVersionId(Long versionId) {
            this.versionId = versionId;
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

}
