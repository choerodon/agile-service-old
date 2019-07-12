//package io.choerodon.agile.domain.agile.entity;
//
//import io.choerodon.agile.infra.common.utils.StringUtil;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/6/14
// */
//public class IssueLinkTypeE {
//
//    private Long linkTypeId;
//
//    private String linkName;
//
//    private String inWard;
//
//    private String outWard;
//
//    private Long objectVersionNumber;
//
//    private Long projectId;
//
//    public Long getLinkTypeId() {
//        return linkTypeId;
//    }
//
//    public void setLinkTypeId(Long linkTypeId) {
//        this.linkTypeId = linkTypeId;
//    }
//
//    public String getLinkName() {
//        return linkName;
//    }
//
//    public void setLinkName(String linkName) {
//        this.linkName = linkName;
//    }
//
//    public String getInWard() {
//        return inWard;
//    }
//
//    public void setInWard(String inWard) {
//        this.inWard = inWard;
//    }
//
//    public String getOutWard() {
//        return outWard;
//    }
//
//    public void setOutWard(String outWard) {
//        this.outWard = outWard;
//    }
//
//    public Long getObjectVersionNumber() {
//        return objectVersionNumber;
//    }
//
//    public void setObjectVersionNumber(Long objectVersionNumber) {
//        this.objectVersionNumber = objectVersionNumber;
//    }
//
//    public Long getProjectId() {
//        return projectId;
//    }
//
//    public void setProjectId(Long projectId) {
//        this.projectId = projectId;
//    }
//
//    @Override
//    public String toString() {
//        return StringUtil.getToString(this);
//    }
//
//    public void initDuplicate(Long projectId) {
//        this.projectId = projectId;
//        this.inWard = "被复制";
//        this.outWard = "复制";
//        this.linkName = "复制";
//    }
//
//    public void initBlocks(Long projectId) {
//        this.projectId = projectId;
//        this.inWard = "被阻塞";
//        this.outWard = "阻塞";
//        this.linkName = "阻塞";
//    }
//
//    public void initRelates(Long projectId) {
//        this.projectId = projectId;
//        this.inWard = "关联";
//        this.outWard = "关联";
//        this.linkName = "关联";
//    }
//}
