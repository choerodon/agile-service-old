//package io.choerodon.agile.domain.agile.entity;
//
//import io.choerodon.core.oauth.DetailsHelper;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/7/4
// */
//public class UserSettingE {
//
//    private Long settingId;
//
//    private Long userId;
//
//    private Long projectId;
//
//    private Long boardId;
//
//    private Boolean defaultBoard;
//
//    private String typeCode;
//
//    private String swimlaneBasedCode;
//
//    private String storymapSwimlaneCode;
//
//    private Long objectVersionNumber;
//
//    public Long getSettingId() {
//        return settingId;
//    }
//
//    public void setSettingId(Long settingId) {
//        this.settingId = settingId;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
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
//    public Long getBoardId() {
//        return boardId;
//    }
//
//    public void setBoardId(Long boardId) {
//        this.boardId = boardId;
//    }
//
//    public Boolean getDefaultBoard() {
//        return defaultBoard;
//    }
//
//    public void setDefaultBoard(Boolean defaultBoard) {
//        this.defaultBoard = defaultBoard;
//    }
//
//    public String getTypeCode() {
//        return typeCode;
//    }
//
//    public void setTypeCode(String typeCode) {
//        this.typeCode = typeCode;
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
//    public String getSwimlaneBasedCode() {
//        return swimlaneBasedCode;
//    }
//
//    public void setSwimlaneBasedCode(String swimlaneBasedCode) {
//        this.swimlaneBasedCode = swimlaneBasedCode;
//    }
//
//    public String getStorymapSwimlaneCode() {
//        return storymapSwimlaneCode;
//    }
//
//    public void setStorymapSwimlaneCode(String storymapSwimlaneCode) {
//        this.storymapSwimlaneCode = storymapSwimlaneCode;
//    }
//
//    public void initUserSetting(Long projectId) {
//        this.projectId = projectId;
//        this.userId = DetailsHelper.getUserDetails().getUserId();
//    }
//}
