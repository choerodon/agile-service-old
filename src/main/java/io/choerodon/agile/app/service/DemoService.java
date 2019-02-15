package io.choerodon.agile.app.service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
public interface DemoService {

    void demoInit(Long projectId, Long userId1, Long userId2);

    void demoDelete(Long projectId);

}
