package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ComponentForListDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.validator.IssueComponentValidator;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.domain.agile.repository.ComponentIssueRelRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.IssueComponentDTO;
import io.choerodon.agile.app.service.IssueComponentService;
import io.choerodon.agile.domain.agile.entity.IssueComponentE;
import io.choerodon.agile.domain.agile.repository.IssueComponentRepository;
import io.choerodon.agile.infra.dataobject.IssueComponentDO;
import io.choerodon.agile.infra.mapper.IssueComponentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueComponentServiceImpl implements IssueComponentService {

    @Autowired
    private IssueComponentRepository issueComponentRepository;

    @Autowired
    private IssueComponentMapper issueComponentMapper;

    @Autowired
    private ComponentIssueRelRepository componentIssueRelRepository;

    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public IssueComponentDTO create(Long projectId, IssueComponentDTO issueComponentDTO) {
//        if (!projectId.equals(issueComponentDTO.getProjectId())) {
//            throw new CommonException("error.projectId.notEqual");
//        }
        IssueComponentValidator.checkCreateComponent(projectId, issueComponentDTO);
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentDTO, IssueComponentE.class);
        return ConvertHelper.convert(issueComponentRepository.create(issueComponentE), IssueComponentDTO.class);
    }

    @Override
    public IssueComponentDTO update(Long projectId, Long id, IssueComponentDTO issueComponentDTO) {
        issueComponentDTO.setComponentId(id);
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentDTO, IssueComponentE.class);
        return ConvertHelper.convert(issueComponentRepository.update(issueComponentE), IssueComponentDTO.class);
    }

    private void unRelateIssueWithComponent(Long projectId, Long id) {
        componentIssueRelRepository.deleteByComponentId(projectId, id);
    }

    private void reRelateIssueWithComponent(Long projectId, Long id, Long relateComponentId) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        componentIssueRelDO.setProjectId(projectId);
        componentIssueRelDO.setComponentId(id);
        List<ComponentIssueRelDO> componentIssueRelDOList = componentIssueRelMapper.select(componentIssueRelDO);
        unRelateIssueWithComponent(projectId, id);
        for (ComponentIssueRelDO componentIssue : componentIssueRelDOList) {
            ComponentIssueRelE relate = new ComponentIssueRelE();
            relate.setProjectId(projectId);
            relate.setIssueId(componentIssue.getIssueId());
            relate.setComponentId(relateComponentId);
            componentIssueRelRepository.create(relate);
        }
    }

    @Override
    public void delete(Long projectId, Long id, Long relateComponentId) {
        if (relateComponentId == null) {
            unRelateIssueWithComponent(projectId, id);
        } else {
            reRelateIssueWithComponent(projectId, id, relateComponentId);
        }
        issueComponentRepository.delete(id);
    }

    @Override
    public IssueComponentDTO queryComponentsById(Long projectId, Long id) {
        IssueComponentDO issueComponentDO = issueComponentMapper.selectByPrimaryKey(id);
        if (issueComponentDO == null) {
            throw new CommonException("error.component.get");
        }
        return ConvertHelper.convert(issueComponentDO, IssueComponentDTO.class);
    }

    @Override
    public List<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest) {
        List<ComponentForListDTO> componentForListDTOList = ConvertHelper.convertList(
                issueComponentMapper.selectComponentWithIssueNum(projectId, componentId, noIssueTest), ComponentForListDTO.class);
        List<Long> assigneeIds = componentForListDTOList.stream().filter(componentForListDTO -> componentForListDTO.getManagerId() != null
                && !Objects.equals(componentForListDTO.getManagerId(), 0L)).map(ComponentForListDTO::getManagerId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        componentForListDTOList.forEach(componentForListDTO -> {
            String assigneeName = usersMap.get(componentForListDTO.getManagerId()) != null ? usersMap.get(componentForListDTO.getManagerId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(componentForListDTO.getManagerId()).getImageUrl() : null;
            componentForListDTO.setManagerName(assigneeName);
            componentForListDTO.setImageUrl(imageUrl);
        });
        return componentForListDTOList;
    }

    @Override
    public List<IssueDTO> queryIssuesByComponentId(Long projectId, Long componentId) {
        return ConvertHelper.convertList(issueComponentMapper.queryIssuesByComponentId(projectId, componentId), IssueDTO.class);
    }
}
