package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/29.
 * Email: fuqianghuang01@gmail.com
 */
public class ArtStopDTO {

    private PiDTO activePiDTO;

    private Long completedPiCount;

    private Long todoPiCount;

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
