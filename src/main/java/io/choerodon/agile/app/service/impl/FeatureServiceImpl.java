package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.domain.agile.entity.FeatureE;
import io.choerodon.agile.app.service.FeatureService;
import io.choerodon.agile.infra.dataobject.FeatureDTO;
import io.choerodon.agile.infra.mapper.FeatureMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    private FeatureMapper featureMapper;

    @Override
    public FeatureDTO create(FeatureDTO featureDTO) {
        if (featureMapper.insert(featureDTO) != 1) {
            throw new CommonException("error.feature.insert");
        }
        return featureMapper.selectByPrimaryKey(featureDTO.getId());
    }

    @Override
    public FeatureDTO updateSelective(FeatureDTO featureDTO) {
        if (featureMapper.updateByPrimaryKeySelective(featureDTO) != 1) {
            throw new CommonException("error.feature.update");
        }
        return featureMapper.selectByPrimaryKey(featureDTO.getId());
    }

    @Override
    public void delete(Long issueId) {
        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setIssueId(issueId);
        FeatureDTO res = featureMapper.selectOne(featureDTO);
        if (res == null) {
            throw new CommonException("error.feature.exist");
        }
        if (featureMapper.delete(res) != 1) {
            throw new CommonException("error.feature.insert");
        }
    }
}
