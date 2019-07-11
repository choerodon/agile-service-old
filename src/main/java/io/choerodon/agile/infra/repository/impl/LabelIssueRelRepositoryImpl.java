package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.dataobject.LabelIssueRelDTO;
import io.choerodon.agile.infra.repository.LabelIssueRelRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;
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
        LabelIssueRelDTO labelIssueRelDTO = ConvertHelper.convert(labelIssueRelE, LabelIssueRelDTO.class);
        if (labelIssueRelMapper.insert(labelIssueRelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        LabelIssueRelDTO query = new LabelIssueRelDTO();
        query.setIssueId(labelIssueRelDTO.getIssueId());
        query.setLabelId(labelIssueRelDTO.getLabelId());
        return ConvertHelper.convert(labelIssueRelMapper.selectOne(query), LabelIssueRelE.class);
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        LabelIssueRelDTO labelIssueDO1 = new LabelIssueRelDTO();
        labelIssueDO1.setIssueId(issueId);
        return labelIssueRelMapper.delete(labelIssueDO1);
    }

    @Override
    @DataLog(type = "batchDeleteLabel", single = false)
    public int batchDeleteByIssueId(Long issueId) {
        LabelIssueRelDTO delete = new LabelIssueRelDTO();
        delete.setIssueId(issueId);
        return labelIssueRelMapper.delete(delete);
    }

    @Override
    @DataLog(type = "labelDelete")
    public int delete(LabelIssueRelDTO labelIssueRelDTO) {
        return labelIssueRelMapper.delete(labelIssueRelDTO);
    }

}