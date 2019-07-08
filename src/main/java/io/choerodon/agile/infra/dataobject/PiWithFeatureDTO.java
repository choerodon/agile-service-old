package io.choerodon.agile.infra.dataobject;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
public class PiWithFeatureDTO {

    private Long id;

    private String name;

    private String statusCode;

    private Date startDate;

    private Date endDate;

    private Long ArtId;

    private Long objectVersionNumber;

    private List<SubFeatureDTO> subFeatureDTOList;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setArtId(Long artId) {
        ArtId = artId;
    }

    public Long getArtId() {
        return ArtId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public List<SubFeatureDTO> getSubFeatureDTOList() {
        return subFeatureDTOList;
    }

    public void setSubFeatureDTOList(List<SubFeatureDTO> subFeatureDTOList) {
        this.subFeatureDTOList = subFeatureDTOList;
    }
}
