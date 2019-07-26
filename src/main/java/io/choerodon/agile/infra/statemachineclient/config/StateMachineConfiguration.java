package io.choerodon.agile.infra.statemachineclient.config;

import io.choerodon.agile.infra.statemachineclient.ClientProcessor;
import io.choerodon.agile.infra.statemachineclient.StateMachineApplicationContextHelper;
import io.choerodon.agile.infra.statemachineclient.client.StateMachineClient;
import io.choerodon.agile.infra.statemachineclient.dto.PropertyData;
import io.choerodon.agile.infra.statemachineclient.service.ClientService;
import io.choerodon.agile.infra.statemachineclient.service.impl.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/22
 */
@Configuration
public class StateMachineConfiguration {

    @Bean
    public StateMachineApplicationContextHelper stateMachineApplicationContextHelper() {
        return new StateMachineApplicationContextHelper();
    }

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean("stateMachinePropertyData")
    public PropertyData stateMachinePropertyData() {
        PropertyData stateMachinePropertyData = new PropertyData();
        stateMachinePropertyData.setServiceName(serviceName);
        return stateMachinePropertyData;
    }

    @Bean("clientService")
    public ClientService clientService() {
        return new ClientServiceImpl();
    }


    @Bean("ClientProcessor")
    public ClientProcessor clientProcessor(StateMachineApplicationContextHelper stateMachineApplicationContextHelper, PropertyData stateMachinePropertyData) {
        return new ClientProcessor(stateMachineApplicationContextHelper, stateMachinePropertyData);
    }

    @Bean
    public StateMachineClient stateMachineClient(ClientService clientService, PropertyData stateMachinePropertyData) {
        return new StateMachineClient(clientService, stateMachinePropertyData);
    }
}
