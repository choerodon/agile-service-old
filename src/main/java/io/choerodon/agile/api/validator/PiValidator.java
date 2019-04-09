package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.dto.PiDTO;
import io.choerodon.agile.domain.agile.entity.PiE;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.dataobject.PiDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.agile.infra.mapper.PiMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiValidator {

    public static final String PI_DOING = "doing";
    public static final String PI_DONE = "done";
    public static final String PI_TODO = "todo";
    public static final String ART_DOING = "doing";

    @Autowired
    private ArtMapper artMapper;

    @Autowired
    private PiMapper piMapper;

    public void checkPiStart(PiDTO piDTO) {
        if (piDTO.getId() == null) {
            throw new CommonException("error.piId.null");
        }
        if (piDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        ArtDO artDO = artMapper.selectByPrimaryKey(piDTO.getArtId());
        if (artDO == null || !ART_DOING.equals(artDO.getStatusCode())) {
            throw new CommonException("error.art.nullOrNoActive");
        }
        PiDO activePi = piMapper.selectActivePi(piDTO.getProgramId(), piDTO.getArtId());
        if (activePi != null) {
            throw new CommonException("error.activePi.exist");
        }
    }

    public void checkPiClose(PiDTO piDTO) {
        if (piDTO.getTargetPiId() == null) {
            throw new CommonException("error.targetPiId.null");
        }
        if (piDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piDTO.getArtId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (piDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectCersionNumber.null");
        }
    }

    public void checkDelete(Long programId, Long artId, Long piId) {
        PiDO piDO = new PiDO();
        piDO.setProgramId(programId);
        piDO.setArtId(artId);
        piDO.setId(piId);
        PiDO result = piMapper.selectOne(piDO);
        if (result == null) {
            throw new CommonException("error.PI.null");
        }
        if (!PI_TODO.equals(result.getCode())) {
            throw new CommonException("error.unTodoPI.cannotdelete");
        }
    }

}
