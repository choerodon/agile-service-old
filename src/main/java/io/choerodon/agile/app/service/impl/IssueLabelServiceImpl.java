package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.vo.IssueLabelVO;
import io.choerodon.agile.app.service.IssueLabelService;
import io.choerodon.agile.infra.dataobject.IssueLabelDTO;
import io.choerodon.agile.infra.mapper.IssueLabelMapper;
import io.choerodon.core.convertor.ConvertHelper;
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

    @Autowired
    private IssueLabelMapper issueLabelMapper;

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
}