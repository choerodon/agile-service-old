package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterDTO {

    private Long filterId;

    private Long projectId;

    private String name;

    private String expressQuery;

    private List<QuickFilterValueDTO> quickFilterValueDTOList;

    private List<String> relationOperations;

    private Long objectVersionNumber;

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

    public String getExpressQuery() {
        return expressQuery;
    }

    public void setExpressQuery(String expressQuery) {
        this.expressQuery = expressQuery;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setQuickFilterValueDTOList(List<QuickFilterValueDTO> quickFilterValueDTOList) {
        this.quickFilterValueDTOList = quickFilterValueDTOList;
    }

    public List<QuickFilterValueDTO> getQuickFilterValueDTOList() {
        return quickFilterValueDTOList;
    }

    public void setRelationOperations(List<String> relationOperations) {
        this.relationOperations = relationOperations;
    }

    public List<String> getRelationOperations() {
        return relationOperations;
    }
}
