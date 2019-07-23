package io.choerodon.agile.app.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.agile.app.service.RegisterInstanceService;
import org.springframework.stereotype.Component;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {

    private RegisterInstanceService registerInstanceService;

    public EurekaEventObserver(RegisterInstanceService registerInstanceService) {
        this.registerInstanceService = registerInstanceService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        registerInstanceService.instanceUpConsumer(payload);
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
        registerInstanceService.instanceDownConsumer(payload);
    }

}
