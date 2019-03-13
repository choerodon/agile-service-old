package io.choerodon.agile.api.dto;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public class ArtDTO {

    private Long id;

    private String code;

    private String name;

    private String seqNumber;

    private Boolean enabled;

    private Long rteId;

    private Date startDate;

    private Date endDate;

    private Long ipWorkdays;

    private String piCodePrefix;

    private Long piCodeNumber;

    private Long interationCount;

    private Long interationWorkdays;

    private Long programId;

    private Long objectVersionNumber;

    @Transient
    private String rteName;

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

    public Long getIpWorkdays() {
        return ipWorkdays;
    }

    public void setIpWorkdays(Long ipWorkdays) {
        this.ipWorkdays = ipWorkdays;
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

    public Long getInterationWorkdays() {
        return interationWorkdays;
    }

    public void setInterationWorkdays(Long interationWorkdays) {
        this.interationWorkdays = interationWorkdays;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setRteName(String rteName) {
        this.rteName = rteName;
    }

    public String getRteName() {
        return rteName;
    }
}
