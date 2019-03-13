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

    @Autowired
    private ArtMapper artMapper;

    public Boolean checkArtExistAndEbaled(Long programId, Long artId) {
        ArtDO artDO = new ArtDO();
        artDO.setId(artId);
        artDO.setProgramId(programId);
        ArtDO res = artMapper.selectOne(artDO);
        if (res == null) {
            return false;
        }
        if (!res.getEnabled()) {
            return false;
        }
        return true;
    }

    public Boolean checkHasActiveArt(Long programId) {
        ArtDO artDO = new ArtDO();
        artDO.setProgramId(programId);
        artDO.setEnabled(true);
        ArtDO res = artMapper.selectOne(artDO);
        if (res == null) {
            return false;
        }
        return true;
    }

    public Boolean checkHasArt(Long programId) {
        ArtDO artDO = new ArtDO();
        artDO.setProgramId(programId);
        List<ArtDO> res = artMapper.select(artDO);
        if (res != null && !res.isEmpty()) {
            return false;
        }
        return true;
    }

    public void checkArtUpdate(Long programId, ArtDTO artDTO) {
        if (artDTO.getId() == null) {
            throw new CommonException("error.artId.null");
        }
        if (artDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        if (artDTO.getEnabled()) {
            ArtDO artDO = artMapper.selectByPrimaryKey(artDTO.getId());
            if (artDTO.getEnabled().compareTo(artDO.getEnabled()) == 0) {
                return;
            }
            ArtDO check = new ArtDO();
            check.setProgramId(programId);
            check.setEnabled(true);
            List<ArtDO> artDOList = artMapper.select(check);
            if (artDOList != null && !artDOList.isEmpty()) {
                throw new CommonException("error.artEnabled.moreOne");
            }
        }
    }
}
