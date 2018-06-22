package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO;
import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/22
 */
@Component
public class IssueLinkTypeAssembler {

    public IssueLinkTypeE createDtoToE(IssueLinkTypeCreateDTO issueLinkTypeCreateDTO){
        IssueLinkTypeE issueLinkTypeE = new IssueLinkTypeE();
        BeanUtils.copyProperties(issueLinkTypeCreateDTO,issueLinkTypeE);
        return issueLinkTypeE;
    }
}
