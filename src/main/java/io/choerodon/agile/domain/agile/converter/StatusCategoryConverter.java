package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.StatusCategoryVO;
import io.choerodon.agile.infra.dataobject.StatusCategoryDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StatusCategoryConverter implements ConvertorI<Object, StatusCategoryDTO, StatusCategoryVO> {

    @Override
    public StatusCategoryVO doToDto(StatusCategoryDTO statusCategoryDTO) {
        StatusCategoryVO statusCategoryVO = new StatusCategoryVO();
        BeanUtils.copyProperties(statusCategoryDTO, statusCategoryVO);
        return statusCategoryVO;
    }
}
