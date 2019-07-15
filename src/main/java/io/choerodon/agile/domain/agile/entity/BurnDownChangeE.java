//package io.choerodon.agile.domain.agile.entity;
//
//import io.choerodon.agile.infra.common.utils.StringUtil;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/6/15
// */
//public class BurnDownChangeE {
//
//    private SprintStartDataE sprintStartDataE;
//
//    private SprintEndDataE sprintEndDataE;
//
//    private List<IssueChangeDataE> issueChangeDataEList;
//
//    public SprintStartDataE getSprintStartDataE() {
//        return sprintStartDataE;
//    }
//
//    public void setSprintStartDataE(SprintStartDataE sprintStartDataE) {
//        this.sprintStartDataE = sprintStartDataE;
//    }
//
//    public SprintEndDataE getSprintEndDataE() {
//        return sprintEndDataE;
//    }
//
//    public void setSprintEndDataE(SprintEndDataE sprintEndDataE) {
//        this.sprintEndDataE = sprintEndDataE;
//    }
//
//    public List<IssueChangeDataE> getIssueChangeDataEList() {
//        return issueChangeDataEList;
//    }
//
//    public void setIssueChangeDataEList(List<IssueChangeDataE> issueChangeDataEList) {
//        this.issueChangeDataEList = issueChangeDataEList;
//    }
//
//    public class SprintStartDataE {
//
//        private Date startDate;
//
//        private List<IssueChangeDataE> issueChangeDataEList;
//
//        public Date getStartDate() {
//            return startDate;
//        }
//
//        public void setStartDate(Date startDate) {
//            this.startDate = startDate;
//        }
//
//        public List<IssueChangeDataE> getIssueChangeDataEList() {
//            return issueChangeDataEList;
//        }
//
//        public void setIssueChangeDataEList(List<IssueChangeDataE> issueChangeDataEList) {
//            this.issueChangeDataEList = issueChangeDataEList;
//        }
//    }
//
//    public class IssueChangeDataE {
//
//        private String issueNum;
//
//        private String issueId;
//
//        private String changeField;
//
//        private String changeDetail;
//
//        private Integer oldData;
//
//        private Integer newData;
//
//        private Date changeDate;
//
//        public String getIssueNum() {
//            return issueNum;
//        }
//
//        public void setIssueNum(String issueNum) {
//            this.issueNum = issueNum;
//        }
//
//        public String getIssueId() {
//            return issueId;
//        }
//
//        public void setIssueId(String issueId) {
//            this.issueId = issueId;
//        }
//
//        public String getChangeField() {
//            return changeField;
//        }
//
//        public void setChangeField(String changeField) {
//            this.changeField = changeField;
//        }
//
//        public String getChangeDetail() {
//            return changeDetail;
//        }
//
//        public void setChangeDetail(String changeDetail) {
//            this.changeDetail = changeDetail;
//        }
//
//        public Integer getOldData() {
//            return oldData;
//        }
//
//        public void setOldData(Integer oldData) {
//            this.oldData = oldData;
//        }
//
//        public Integer getNewData() {
//            return newData;
//        }
//
//        public void setNewData(Integer newData) {
//            this.newData = newData;
//        }
//
//        public Date getChangeDate() {
//            return changeDate;
//        }
//
//        public void setChangeDate(Date changeDate) {
//            this.changeDate = changeDate;
//        }
//    }
//
//    public class SprintEndDataE {
//
//        private Date endDate;
//
//        private List<IssueChangeDataE> issueChangeDataEList;
//
//        public Date getEndDate() {
//            return endDate;
//        }
//
//        public void setEndDate(Date endDate) {
//            this.endDate = endDate;
//        }
//
//        public List<IssueChangeDataE> getIssueChangeDataEList() {
//            return issueChangeDataEList;
//        }
//
//        public void setIssueChangeDataEList(List<IssueChangeDataE> issueChangeDataEList) {
//            this.issueChangeDataEList = issueChangeDataEList;
//        }
//    }
//
//    @Override
//    public String toString() {
//        return StringUtil.getToString(this);
//    }
//}
