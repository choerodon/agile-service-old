package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.QuickFilterE;
import io.choerodon.agile.domain.agile.repository.QuickFilterRepository;
import io.choerodon.agile.infra.dataobject.QuickFilterDO;
import io.choerodon.agile.infra.mapper.QuickFilterMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class QuickFilterRepositoryImpl implements QuickFilterRepository {

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Override
    public QuickFilterE create(QuickFilterE quickFilterE) {
        QuickFilterDO quickFilterDO = ConvertHelper.convert(quickFilterE, QuickFilterDO.class);
        if (quickFilterMapper.insert(quickFilterDO) != 1) {
            throw new CommonException("error.quickFilter.insert");
        }
        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterDO.getFilterId()), QuickFilterE.class);
    }

    @Override
    public QuickFilterE update(QuickFilterE quickFilterE) {
        QuickFilterDO quickFilterDO = ConvertHelper.convert(quickFilterE, QuickFilterDO.class);
        if (quickFilterMapper.updateByPrimaryKeySelective(quickFilterDO) != 1) {
            throw new CommonException("error.quickFilter.update");
        }
        return ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(quickFilterDO.getFilterId()), QuickFilterE.class);
    }

    @Override
    public void deleteById(Long filterId) {
        QuickFilterDO quickFilterDO = quickFilterMapper.selectByPrimaryKey(filterId);
        if (quickFilterDO == null) {
            throw new CommonException("error.quickFilter.get");
        }
        if (quickFilterMapper.deleteByPrimaryKey(filterId) != 1) {
            throw new CommonException("error.quickFilter.delete");
        }
    }

    @Override
    public int batchUpdateSequence(Integer sequence, Long projectId) {
        return quickFilterMapper.batchUpdateSequence(sequence, projectId);
    }

}
