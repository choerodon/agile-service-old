package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_quick_filter")
public class QuickFilterDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filterId;

    private Long projectId;

    private String name;

    private String sqlQuery;

    private String expressQuery;

    @Column(name = "is_child_included")
    private Boolean childIncluded;

    private String description;

    private Integer sequence;

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getExpressQuery() {
        return expressQuery;
    }

    public void setExpressQuery(String expressQuery) {
        this.expressQuery = expressQuery;
    }

    public void setChildIncluded(Boolean childIncluded) {
        this.childIncluded = childIncluded;
    }

    public Boolean getChildIncluded() {
        return childIncluded;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
