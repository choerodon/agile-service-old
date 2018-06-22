package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.dto.IssueLabelDTO;
import io.choerodon.agile.app.service.IssueLabelService;
import io.choerodon.agile.infra.dataobject.IssueLabelDO;
import io.choerodon.agile.infra.mapper.IssueLabelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<IssueLabelDTO> listIssueLabel(Long projectId) {
        IssueLabelDO issueLabelDO = new IssueLabelDO();
        issueLabelDO.setProjectId(projectId);
        return ConvertHelper.convertList(issueLabelMapper.select(issueLabelDO), IssueLabelDTO.class);
    }
}