package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.ArtDTO;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        if (artDTO.getCode() == null) {
            throw new CommonException("error.code.null");
        }
        if (artDTO.getName() == null) {
            throw new CommonException("error.name.null");
        }
        if (artDTO.getStartDate() == null) {
            throw new CommonException("error.startDate.null");
        }
        if (artDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
    }

    public void checkArtUpdate(Long programId, ArtDTO artDTO) {
        if (artDTO.getId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (artDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (artDTO.getStatusCode() != null && ART_DOING.equals(artDTO.getStatusCode())) {
            ArtDO check = new ArtDO();
            check.setProgramId(programId);
            check.setStatusCode(ART_DOING);
            List<ArtDO> artDOList = artMapper.select(check);
            if (artDOList != null && !artDOList.isEmpty()) {
                throw new CommonException("error.artDoing.moreOne");
            }
        }
    }
}
