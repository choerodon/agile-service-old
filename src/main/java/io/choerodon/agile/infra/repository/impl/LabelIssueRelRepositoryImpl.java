package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.repository.LabelIssueRelRepository;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;
import io.choerodon.agile.infra.dataobject.LabelIssueRelDO;
import io.choerodon.agile.infra.mapper.LabelIssueRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@Component
public class LabelIssueRelRepositoryImpl implements LabelIssueRelRepository {

    private static final String UPDATE_ERROR = "error.LabelIssue.update";
    private static final String INSERT_ERROR = "error.LabelIssue.insert";

    @Autowired
    private LabelIssueRelMapper labelIssueRelMapper;

    @Override
    public LabelIssueRelE update(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDO labelIssueRelDO = ConvertHelper.convert(labelIssueRelE, LabelIssueRelDO.class);
        if (labelIssueRelMapper.updateByPrimaryKeySelective(labelIssueRelDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(labelIssueRelMapper.selectByPrimaryKey(labelIssueRelDO.getIssueId()), LabelIssueRelE.class);
    }

    @Override
    public List<LabelIssueRelE> create(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDO labelIssueRelDO = ConvertHelper.convert(labelIssueRelE, LabelIssueRelDO.class);
        if (labelIssueRelMapper.insert(labelIssueRelDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        LabelIssueRelDO labelIssueDO1 = new LabelIssueRelDO();
        labelIssueDO1.setIssueId(labelIssueRelDO.getIssueId());
        return ConvertHelper.convertList(labelIssueRelMapper.select(labelIssueDO1), LabelIssueRelE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        LabelIssueRelDO labelIssueDO1 = new LabelIssueRelDO();
        labelIssueDO1.setIssueId(issueId);
        return labelIssueRelMapper.delete(labelIssueDO1);
    }

}