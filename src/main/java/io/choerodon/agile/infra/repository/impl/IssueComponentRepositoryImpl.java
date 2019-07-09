package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueComponentE;
import io.choerodon.agile.infra.repository.IssueComponentRepository;
import io.choerodon.agile.infra.dataobject.IssueComponentDTO;
import io.choerodon.agile.infra.mapper.IssueComponentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueComponentRepositoryImpl implements IssueComponentRepository {

    @Autowired
    private IssueComponentMapper issueComponentMapper;
    @Autowired
    private RedisUtil redisUtil;

    private static final String AGILE = "Agile:";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String CPMPONENT = "component";

    @Override
    public IssueComponentE create(IssueComponentE issueComponentE) {
        IssueComponentDTO issueComponentDTO = ConvertHelper.convert(issueComponentE, IssueComponentDTO.class);
        if (issueComponentMapper.insert(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.insert");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentE.getProjectId() + ':' + CPMPONENT + "*"});
        return ConvertHelper.convert(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentE.class);
    }

    @Override
    public IssueComponentE update(IssueComponentE issueComponentE) {
        IssueComponentDTO issueComponentDTO = ConvertHelper.convert(issueComponentE, IssueComponentDTO.class);
        if (issueComponentMapper.updateByPrimaryKeySelective(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.update");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentE.getProjectId() + ':' + CPMPONENT + "*"});
        return ConvertHelper.convert(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentE.class);
    }

    @Override
    public void delete(Long id) {
        IssueComponentDTO issueComponentDTO = issueComponentMapper.selectByPrimaryKey(id);
        if (issueComponentDTO == null) {
            throw new CommonException("error.component.get");
        }
        if (issueComponentMapper.delete(issueComponentDTO) != 1) {
            throw new CommonException("error.component.delete");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
    }
}
