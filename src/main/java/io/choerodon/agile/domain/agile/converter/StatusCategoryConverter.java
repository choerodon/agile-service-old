package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.StatusCategoryDTO;
import io.choerodon.agile.infra.dataobject.StatusCategoryDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StatusCategoryConverter implements ConvertorI<Object, StatusCategoryDO, StatusCategoryDTO> {

    @Override
    public StatusCategoryDTO doToDto(StatusCategoryDO statusCategoryDO) {
        StatusCategoryDTO statusCategoryDTO = new StatusCategoryDTO();
        BeanUtils.copyProperties(statusCategoryDO, statusCategoryDTO);
        return statusCategoryDTO;
    }
}
