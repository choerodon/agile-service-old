package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.ArtDTO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ArtValidator {

    private static final String ART_DOING = "doing";

    @Autowired
    private ArtMapper artMapper;

    public void checkArtCreate(ArtDTO artDTO) {
        if (artDTO.getName() == null) {
            throw new CommonException("error.name.null");
        }
        if (artDTO.getStartDate() == null) {
            throw new CommonException("error.startDate.null");
        }
        if (artDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (artDTO.getPiCount() == null) {
            throw new CommonException("error.piCount.null");
        }
    }

    public void checkArtStart(ArtDTO artDTO) {
        if (artDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (artDTO.getId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (artDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.artObjectVersionNumber.null");
        }
    }

    public void checkArtStop(ArtDTO artDTO) {
        if (artDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (artDTO.getId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (artDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.artObjectVersionNumber.null");
        }
    }

    public void checkArtUpdate(ArtDTO artDTO) {
        if (artDTO.getId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (artDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
    }
}
