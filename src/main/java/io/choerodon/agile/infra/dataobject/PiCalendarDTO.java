package io.choerodon.agile.infra.dataobject;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/14.
 * Email: fuqianghuang01@gmail.com
 */
public class PiCalendarDTO {

    private Long id;

    private String code;

    private String name;

    private String statusCode;

    private Date startDate;

    private Date endDate;

    private Long artId;

    private Long programId;

    List<SprintCalendarDTO> sprintCalendarDTOList;

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

    public List<SprintCalendarDTO> getSprintCalendarDTOList() {
        return sprintCalendarDTOList;
    }

    public void setSprintCalendarDTOList(List<SprintCalendarDTO> sprintCalendarDTOList) {
        this.sprintCalendarDTOList = sprintCalendarDTOList;
    }
}
