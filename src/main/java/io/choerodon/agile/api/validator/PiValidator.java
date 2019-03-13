package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.dto.PiDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/12.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiValidator {

    public static final String PI_DOING = "doing";
    public static final String PI_DONE = "done";

    public void checkPiStart(PiDTO piDTO) {
        if (piDTO.getArtId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (piDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectCersionNumber.null");
        }
        if (piDTO.getStatusCode() == null) {
            throw new CommonException("error.statusCode.null");
        }
        if (!PI_DOING.equals(piDTO.getStatusCode())) {
            throw new CommonException("error.statusCode.post");
        }
    }

    public void checkPiClose(PiDTO piDTO) {
        if (piDTO.getTargetPiId() == null) {
            throw new CommonException("error.targetPiId.null");
        }
        if (piDTO.getProgramId() == null) {
            throw new CommonException("error.programId.null");
        }
        if (piDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectCersionNumber.null");
        }
        if (piDTO.getStatusCode() == null) {
            throw new CommonException("error.statusCode.null");
        }
        if (!PI_DONE.equals(piDTO.getStatusCode())) {
            throw new CommonException("error.statusCode.post");
        }
    }

}
