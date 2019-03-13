package io.choerodon.agile.api.dto;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
public class PiDTO {

    private Long id;

    private String code;

    private String name;

    private String seqNumber;

    private String statusCode;

    private Date startDate;

    private Date endDate;

    private Long artId;

    private Long programId;

    private Long objectVersionNumber;

    @Transient
    private Long targetPiId;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getArtId() {
        return artId;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
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

    public void setTargetPiId(Long targetPiId) {
        this.targetPiId = targetPiId;
    }

    public Long getTargetPiId() {
        return targetPiId;
    }
}
