package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkAssembler extends AbstractAssembler {

    public List<IssueLinkDTO> issueLinkDoToDto(Long projectId, List<IssueLinkDO> issueLinkDOList) {
        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>(issueLinkDOList.size());
        if (!issueLinkDOList.isEmpty()) {
            Map<Long, IssueTypeDTO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId);
            Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
            Map<Long, PriorityDTO> priorityDTOMap = ConvertUtil.getIssuePriorityMap(projectId);
            issueLinkDOList.forEach(issueLinkDO -> {
                IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
                BeanUtils.copyProperties(issueLinkDO, issueLinkDTO);
                issueLinkDTO.setIssueTypeDTO(issueTypeDTOMap.get(issueLinkDO.getIssueTypeId()));
                issueLinkDTO.setStatusMapDTO(statusMapDTOMap.get(issueLinkDO.getStatusId()));
                issueLinkDTO.setPriorityDTO(priorityDTOMap.get(issueLinkDO.getPriorityId()));
                issueLinkDTOList.add(issueLinkDTO);
            });
        }
        return issueLinkDTOList;
    }
}
