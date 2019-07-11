package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.FeatureE;
import io.choerodon.agile.infra.repository.FeatureRepository;
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
public class FeatureRepositoryImpl implements FeatureRepository {

    @Autowired
    private FeatureMapper featureMapper;

    @Override
    public FeatureE create(FeatureE featureE) {
        FeatureDTO featureDTO = ConvertHelper.convert(featureE, FeatureDTO.class);
        if (featureMapper.insert(featureDTO) != 1) {
            throw new CommonException("error.feature.insert");
        }
        return ConvertHelper.convert(featureMapper.selectByPrimaryKey(featureDTO.getId()), FeatureE.class);
    }

    @Override
    public FeatureE updateSelective(FeatureE featureE) {
        FeatureDTO featureDTO = ConvertHelper.convert(featureE, FeatureDTO.class);
        if (featureMapper.updateByPrimaryKeySelective(featureDTO) != 1) {
            throw new CommonException("error.feature.update");
        }
        return ConvertHelper.convert(featureMapper.selectByPrimaryKey(featureDTO.getId()), FeatureE.class);
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
