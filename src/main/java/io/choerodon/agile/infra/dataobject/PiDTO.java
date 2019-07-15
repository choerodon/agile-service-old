package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_pi")
public class PiDTO extends BaseDTO {

    public PiDTO() {}

    public PiDTO(Long programId, Long id, String statusCode, Date actualStartDate, Long objectVersionNumber) {
        this.programId = programId;
        this.id = id;
        this.statusCode = statusCode;
        this.actualStartDate = actualStartDate;
        this.objectVersionNumber = objectVersionNumber;
    }

    public PiDTO(Long programId, Long id, String statusCode, Long objectVersionNumber, Date actualEndDate) {
        this.programId = programId;
        this.id = id;
        this.statusCode = statusCode;
        this.objectVersionNumber = objectVersionNumber;
        this.actualEndDate = actualEndDate;
    }

    public PiDTO(Long programId, Long id, String statusCode, Long objectVersionNumber) {
        this.programId = programId;
        this.id = id;
        this.statusCode = statusCode;
        this.objectVersionNumber = objectVersionNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String seqNumber;

    private String statusCode;

    private Date startDate;

    private Date endDate;

    private Date actualStartDate;

    private Date actualEndDate;

    private Long artId;

    private Long programId;

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

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
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
