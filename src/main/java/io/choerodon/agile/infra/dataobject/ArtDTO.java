package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_art")
public class ArtDTO extends BaseDTO {

    public ArtDTO() {}

    public ArtDTO(Long programId, Long artId, String statusCode, Long objectVersionNumber) {
        this.programId = programId;
        this.id = artId;
        this.statusCode = statusCode;
        this.objectVersionNumber = objectVersionNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    private String seqNumber;

    @Column(name = "is_enabled")
    private Boolean enabled;

    private Long rteId;

    private Date startDate;

    private Date endDate;

    private Long ipWeeks;

    private String piCodePrefix;

    private Long piCodeNumber;

    private Long interationCount;

    private Long interationWeeks;

    private Long programId;

    private Long piCount;

    private String statusCode;

    private String sprintCompleteSetting;

    private Long objectVersionNumber;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(String seqNumber) {
        this.seqNumber = seqNumber;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getRteId() {
        return rteId;
    }

    public void setRteId(Long rteId) {
        this.rteId = rteId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setIpWeeks(Long ipWeeks) {
        this.ipWeeks = ipWeeks;
    }

    public Long getIpWeeks() {
        return ipWeeks;
    }

    public String getPiCodePrefix() {
        return piCodePrefix;
    }

    public void setPiCodePrefix(String piCodePrefix) {
        this.piCodePrefix = piCodePrefix;
    }

    public Long getPiCodeNumber() {
        return piCodeNumber;
    }

    public void setPiCodeNumber(Long piCodeNumber) {
        this.piCodeNumber = piCodeNumber;
    }

    public Long getInterationCount() {
        return interationCount;
    }

    public void setInterationCount(Long interationCount) {
        this.interationCount = interationCount;
    }

    public void setInterationWeeks(Long interationWeeks) {
        this.interationWeeks = interationWeeks;
    }

    public Long getInterationWeeks() {
        return interationWeeks;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setPiCount(Long piCount) {
        this.piCount = piCount;
    }

    public Long getPiCount() {
        return piCount;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setSprintCompleteSetting(String sprintCompleteSetting) {
        this.sprintCompleteSetting = sprintCompleteSetting;
    }

    public String getSprintCompleteSetting() {
        return sprintCompleteSetting;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
}
