package io.choerodon.agile.infra.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.custom.extra.ExtraData;
import io.choerodon.swagger.custom.extra.ExtraDataManager;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/23.
 * Email: fuqianghuang01@gmail.com
 */
@ChoerodonExtraData
public class CustomExtraDataManager implements ExtraDataManager {
    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName("agile");
        choerodonRouteData.setPath("/agile/**");
        choerodonRouteData.setServiceId("agile-service");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}