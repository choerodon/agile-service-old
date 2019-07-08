package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.vo.PiVO;
import io.choerodon.agile.infra.dataobject.ArtDTO;
import io.choerodon.agile.infra.dataobject.PiDTO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.agile.infra.mapper.PiMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void checkPiStart(PiVO piVO) {
        if (piVO.getId() == null) {
            throw new CommonException("error.piId.null");
        }
        if (piVO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piVO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        ArtDTO artDTO = artMapper.selectByPrimaryKey(piVO.getArtId());
        if (artDTO == null || !ART_DOING.equals(artDTO.getStatusCode())) {
            throw new CommonException("error.art.nullOrNoActive");
        }
        PiDTO activePi = piMapper.selectActivePi(piVO.getProgramId(), piVO.getArtId());
        if (activePi != null) {
            throw new CommonException("error.activePi.exist");
        }
    }

    public void checkPiClose(PiVO piVO) {
        if (piVO.getTargetPiId() == null) {
            throw new CommonException("error.targetPiId.null");
        }
        if (piVO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piVO.getArtId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (piVO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectCersionNumber.null");
        }
    }

    public void checkDelete(Long programId, Long artId, Long piId) {
        PiDTO piDTO = new PiDTO();
        piDTO.setProgramId(programId);
        piDTO.setArtId(artId);
        piDTO.setId(piId);
        PiDTO result = piMapper.selectOne(piDTO);
        if (result == null) {
            throw new CommonException("error.PI.null");
        }
        if (!PI_TODO.equals(result.getStatusCode())) {
            throw new CommonException("error.unTodoPI.cannotdelete");
        }
    }

}
