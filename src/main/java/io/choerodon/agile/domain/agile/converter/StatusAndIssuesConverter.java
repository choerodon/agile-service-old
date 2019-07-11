package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.StatusAndIssuesVO;
import io.choerodon.agile.infra.dataobject.StatusAndIssuesDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/17.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StatusAndIssuesConverter implements ConvertorI<Object, StatusAndIssuesDTO, StatusAndIssuesVO> {

    @Override
    public StatusAndIssuesVO doToDto(StatusAndIssuesDTO statusAndIssuesDTO) {
        StatusAndIssuesVO statusAndIssuesVO = new StatusAndIssuesVO();
        BeanUtils.copyProperties(statusAndIssuesDTO, statusAndIssuesVO);
        return statusAndIssuesVO;
    }

    @Override
    public StatusAndIssuesDTO dtoToDo(StatusAndIssuesVO statusAndIssuesVO) {
        StatusAndIssuesDTO statusAndIssuesDTO = new StatusAndIssuesDTO();
        BeanUtils.copyProperties(statusAndIssuesVO, statusAndIssuesDTO);
        return statusAndIssuesDTO;
    }
}
