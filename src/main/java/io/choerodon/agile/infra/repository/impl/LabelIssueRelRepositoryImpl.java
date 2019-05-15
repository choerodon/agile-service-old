package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.repository.LabelIssueRelRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;
import io.choerodon.agile.infra.dataobject.LabelIssueRelDO;
import io.choerodon.agile.infra.mapper.LabelIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@Component
public class LabelIssueRelRepositoryImpl implements LabelIssueRelRepository {

    private static final String INSERT_ERROR = "error.LabelIssue.insert";

    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;

    @Override
    @DataLog(type = "labelCreate")
    public LabelIssueRelE create(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDO labelIssueRelDO = ConvertHelper.convert(labelIssueRelE, LabelIssueRelDO.class);
        if (labelIssueRelMapper.insert(labelIssueRelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        LabelIssueRelDO query = new LabelIssueRelDO();
        query.setIssueId(labelIssueRelDO.getIssueId());
        query.setLabelId(labelIssueRelDO.getLabelId());
        return ConvertHelper.convert(labelIssueRelMapper.selectOne(query), LabelIssueRelE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        LabelIssueRelDO labelIssueDO1 = new LabelIssueRelDO();
        labelIssueDO1.setIssueId(issueId);
        return labelIssueRelMapper.delete(labelIssueDO1);
    }

    @Override
    @DataLog(type = "batchDeleteLabel", single = false)
    public int batchDeleteByIssueId(Long issueId) {
        LabelIssueRelDO delete = new LabelIssueRelDO();
        delete.setIssueId(issueId);
        return labelIssueRelMapper.delete(delete);
    }

    @Override
    @DataLog(type = "labelDelete")
    public int delete(LabelIssueRelDO labelIssueRelDO) {
        return labelIssueRelMapper.delete(labelIssueRelDO);
    }

}