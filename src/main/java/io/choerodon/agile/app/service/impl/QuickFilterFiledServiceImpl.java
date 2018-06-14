package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.QuickFilterFiledDTO;
import io.choerodon.agile.app.service.QuickFilterFiledService;
import io.choerodon.agile.app.service.QuickFilterService;
import io.choerodon.agile.infra.dataobject.QuickFilterFiledDO;
import io.choerodon.agile.infra.mapper.QuickFilterFiledMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class QuickFilterFiledServiceImpl implements QuickFilterFiledService {

    @Autowired
    private QuickFilterFiledMapper quickFilterFiledMapper;

    @Override
    public List<QuickFilterFiledDTO> list(Long projectId) {
        return ConvertHelper.convertList(quickFilterFiledMapper.selectAll(), QuickFilterFiledDTO.class);
    }

}
