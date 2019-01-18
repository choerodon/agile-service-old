package io.choerodon.agile.infra.dataobject;

import java.math.BigDecimal;

/**
 * Created by jian_zhang02@163.com on 2018/5/30.
 */
public class IssueCountDO {
    private Long id;
    private String name;
    private Integer issueCount;
    private BigDecimal storyPointCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public void setStoryPointCount(BigDecimal storyPointCount) {
        this.storyPointCount = storyPointCount;
    }

    public BigDecimal getStoryPointCount() {
        return storyPointCount;
    }
}
