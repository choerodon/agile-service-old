package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.QuickFilterFieldDTO;
import io.choerodon.agile.app.service.QuickFilterFieldService;
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class QuickFilterFieldServiceImpl implements QuickFilterFieldService {

    @Autowired
    private QuickFilterFieldMapper quickFilterFieldMapper;

    @Override
    public List<QuickFilterFieldDTO> list(Long projectId) {
        return ConvertHelper.convertList(quickFilterFieldMapper.selectAll(), QuickFilterFieldDTO.class);
    }

}
