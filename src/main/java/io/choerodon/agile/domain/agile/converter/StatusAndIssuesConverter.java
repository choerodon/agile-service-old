package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.StatusAndIssuesDTO;
import io.choerodon.agile.infra.dataobject.StatusAndIssuesDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/17.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StatusAndIssuesConverter implements ConvertorI<Object, StatusAndIssuesDO, StatusAndIssuesDTO> {

    @Override
    public StatusAndIssuesDTO doToDto(StatusAndIssuesDO statusAndIssuesDO) {
        StatusAndIssuesDTO statusAndIssuesDTO = new StatusAndIssuesDTO();
        BeanUtils.copyProperties(statusAndIssuesDO, statusAndIssuesDTO);
        return statusAndIssuesDTO;
    }

    @Override
    public StatusAndIssuesDO dtoToDo(StatusAndIssuesDTO statusAndIssuesDTO) {
        StatusAndIssuesDO statusAndIssuesDO = new StatusAndIssuesDO();
        BeanUtils.copyProperties(statusAndIssuesDTO, statusAndIssuesDO);
        return statusAndIssuesDO;
    }
}
