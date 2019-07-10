package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.QuickFilterFieldVO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterFieldService {

    List<QuickFilterFieldVO> list(Long projectId);
}
