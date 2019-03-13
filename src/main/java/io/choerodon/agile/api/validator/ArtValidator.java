package io.choerodon.agile.api.validator;

import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

}
