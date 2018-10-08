package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.infra.common.utils.ColorUtil;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkAssembler extends AbstractAssembler{

    @Autowired
    private LookupValueMapper lookupValueMapper;

    private static final String ISSUE_STATUS_COLOR = "issue_status_color";

    public List<IssueLinkDTO> issueLinkDoToDto(List<IssueLinkDO> issueLinkDOList) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        lookupValueDO.setTypeCode(ISSUE_STATUS_COLOR);
        Map<String, String> lookupValueMap = lookupValueMapper.select(lookupValueDO).stream().collect(Collectors.toMap(LookupValueDO::getValueCode, LookupValueDO::getName));
        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>(issueLinkDOList.size());
        issueLinkDOList.forEach(issueLinkDO -> {
            IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
            BeanUtils.copyProperties(issueLinkDO, issueLinkDTO);
            issueLinkDTO.setStatusColor(ColorUtil.initializationStatusColor(issueLinkDTO.getStatusCode(), lookupValueMap));
            issueLinkDTOList.add(issueLinkDTO);
        });
        return issueLinkDTOList;
    }
}
