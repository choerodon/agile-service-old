package io.choerodon.agile.app.service;

import io.choerodon.eureka.event.EurekaEventPayload;

public interface RegisterInstanceService {

    void instanceDownConsumer(final EurekaEventPayload payload);

    void instanceUpConsumer(final EurekaEventPayload payload);
}
