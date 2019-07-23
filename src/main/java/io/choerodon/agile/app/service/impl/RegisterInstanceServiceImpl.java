package io.choerodon.agile.app.service.impl;

import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.agile.api.vo.PropertyData;
import io.choerodon.agile.app.service.ConfigCodeService;
import io.choerodon.agile.app.service.RegisterInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class RegisterInstanceServiceImpl implements RegisterInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterInstanceServiceImpl.class);
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConfigCodeService configCodeService;
    @Value("${choerodon.eureka.event.target-services}")
    private String[] targetServices;
    @Override
    public void instanceDownConsumer(final EurekaEventPayload payload) {
        logger.info("服务下线：{}", payload.getAppName());
    }

    @Override
    public void instanceUpConsumer(final EurekaEventPayload payload) {
        logger.info("服务上线：{}", payload.getAppName());
        if (Arrays.stream(targetServices).anyMatch(x -> x.equals(payload.getAppName()))) {
            PropertyData propertyData = fetchPropertyData(payload.getInstanceAddress());
            if (propertyData == null) {
                throw new RemoteAccessException("error.instanceUpConsumer.fetchPropertyData");
            } else {
                //处理获取到的新启动服务的数据
                configCodeService.handlePropertyData(propertyData);
            }
        }
    }

    private PropertyData fetchPropertyData(String address) {
        ResponseEntity<PropertyData> response = restTemplate.getForEntity("http://"
                + address + "/statemachine/load_config_code", PropertyData.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RemoteAccessException("error.fetchPropertyData.statusCodeNot2XX");
        }
    }
}
