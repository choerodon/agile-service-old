package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.vo.IssueLabelVO;
import io.choerodon.agile.app.service.IssueLabelService;
import io.choerodon.agile.domain.agile.entity.IssueLabelE;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.IssueLabelDTO;
import io.choerodon.agile.infra.mapper.IssueLabelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@Service
public class IssueLabelServiceImpl implements IssueLabelService {

    private static final String INSERT_ERROR = "error.IssueLabel.insert";
    private static final String AGILE = "Agile:";
    private static final String LABEL = "label";
    private static final String PIE_CHART = AGILE + "PieChart";

    @Autowired
    private IssueLabelMapper issueLabelMapper;

    @Autowired
    private RedisUtil redisUtil;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<IssueLabelVO> listIssueLabel(Long projectId) {
        IssueLabelDTO issueLabelDTO = new IssueLabelDTO();
        issueLabelDTO.setProjectId(projectId);
        return modelMapper.map(issueLabelMapper.select(issueLabelDTO), new TypeToken<List<IssueLabelVO>>(){}.getType());
    }

    @Override
    public IssueLabelDTO createBase(IssueLabelDTO issueLabelDTO) {
        if (issueLabelMapper.insert(issueLabelDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + issueLabelDTO.getProjectId() + ':' + LABEL + "*"});
        return issueLabelMapper.selectByPrimaryKey(issueLabelDTO.getLabelId());
    }

    @Override
    public int labelGarbageCollection(Long projectId) {
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + projectId + ':' + LABEL + "*"});
        return issueLabelMapper.labelGarbageCollection(projectId);
    }
}