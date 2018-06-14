package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.QuickFilterFiledDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterFiledService {

    List<QuickFilterFiledDTO> list(Long projectId);
}
