package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/11.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_pi_objective")
public class PiObjectiveDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long planBv;

    private Long actualBv;

    @Column(name = "is_stretch")
    private Boolean stretch;

    private String levelCode;

    private Long projectId;

    private Long piId;

    private Long programId;

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

    public Long getPlanBv() {
        return planBv;
    }

    public void setPlanBv(Long planBv) {
        this.planBv = planBv;
    }

    public Long getActualBv() {
        return actualBv;
    }

    public void setActualBv(Long actualBv) {
        this.actualBv = actualBv;
    }

    public Boolean getStretch() {
        return stretch;
    }

    public void setStretch(Boolean stretch) {
        this.stretch = stretch;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}
