package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueLabelE;
import io.choerodon.agile.domain.agile.repository.IssueLabelRepository;
import io.choerodon.agile.infra.dataobject.IssueLabelDO;
import io.choerodon.agile.infra.mapper.IssueLabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@Component
public class IssueLabelRepositoryImpl implements IssueLabelRepository {

    private static final String INSERT_ERROR = "error.IssueLabel.insert";

    @Autowired
    private IssueLabelMapper issueLabelMapper;

    @Override
    public IssueLabelE create(IssueLabelE issueLabelE) {
        IssueLabelDO issueLabelDO = ConvertHelper.convert(issueLabelE, IssueLabelDO.class);
        if (issueLabelMapper.insert(issueLabelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueLabelMapper.selectByPrimaryKey(issueLabelDO.getLabelId()), IssueLabelE.class);
    }

    @Override
    public int labelGarbageCollection() {
        return issueLabelMapper.labelGarbageCollection();
    }

}