package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/29.
 * Email: fuqianghuang01@gmail.com
 */
public class ArtStopDTO {

    @ApiModelProperty(value = "活跃pi")
    private PiDTO activePiDTO;

    @ApiModelProperty(value = "完成的pi数量")
    private Long completedPiCount;

    @ApiModelProperty(value = "未开启的pi数量")
    private Long todoPiCount;

    @ApiModelProperty(value = "所有pi关联的feature数量")
    private Long relatedFeatureCount;

    public PiDTO getActivePiDTO() {
        return activePiDTO;
    }

    public void setActivePiDTO(PiDTO activePiDTO) {
        this.activePiDTO = activePiDTO;
    }

    public Long getCompletedPiCount() {
        return completedPiCount;
    }

    public void setCompletedPiCount(Long completedPiCount) {
        this.completedPiCount = completedPiCount;
    }

    public void setTodoPiCount(Long todoPiCount) {
        this.todoPiCount = todoPiCount;
    }

    public Long getTodoPiCount() {
        return todoPiCount;
    }

    public Long getRelatedFeatureCount() {
        return relatedFeatureCount;
    }

    public void setRelatedFeatureCount(Long relatedFeatureCount) {
        this.relatedFeatureCount = relatedFeatureCount;
    }
}
