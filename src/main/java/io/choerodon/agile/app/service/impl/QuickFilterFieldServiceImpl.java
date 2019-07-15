package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.QuickFilterFieldVO;
import io.choerodon.agile.app.service.QuickFilterFieldService;
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class QuickFilterFieldServiceImpl implements QuickFilterFieldService {

    @Autowired
    private QuickFilterFieldMapper quickFilterFieldMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<QuickFilterFieldVO> list(Long projectId) {
        return modelMapper.map(quickFilterFieldMapper.selectAll(), new TypeToken<List<QuickFilterFieldVO>>(){}.getType());
    }

}
