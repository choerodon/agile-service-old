package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/4
 */
@Table(name = "agile_user_setting")
public class UserSettingDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingId;

    private Long userId;

    private Long projectId;

    private Long boardId;

    @Column(name = "is_default_board")
    private Boolean defaultBoard;

    private String typeCode;

    private String swimlaneBasedCode;

    private String storymapSwimlaneCode;

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Boolean getDefaultBoard() {
        return defaultBoard;
    }

    public void setDefaultBoard(Boolean defaultBoard) {
        this.defaultBoard = defaultBoard;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSwimlaneBasedCode() {
        return swimlaneBasedCode;
    }

    public void setSwimlaneBasedCode(String swimlaneBasedCode) {
        this.swimlaneBasedCode = swimlaneBasedCode;
    }

    public String getStorymapSwimlaneCode() {
        return storymapSwimlaneCode;
    }

    public void setStorymapSwimlaneCode(String storymapSwimlaneCode) {
        this.storymapSwimlaneCode = storymapSwimlaneCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
