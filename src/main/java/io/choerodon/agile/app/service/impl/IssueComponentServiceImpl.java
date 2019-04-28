package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IssueComponentValidator;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.domain.agile.repository.ComponentIssueRelRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.IssueComponentService;
import io.choerodon.agile.domain.agile.entity.IssueComponentE;
import io.choerodon.agile.domain.agile.repository.IssueComponentRepository;
import io.choerodon.agile.infra.dataobject.IssueComponentDO;
import io.choerodon.agile.infra.mapper.IssueComponentMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private static final String MANAGER = "manager";

    @Override
    public IssueComponentDTO create(Long projectId, IssueComponentDTO issueComponentDTO) {
        if (checkComponentName(projectId, issueComponentDTO.getName())) {
            throw new CommonException("error.componentName.exist");
        }
        IssueComponentValidator.checkCreateComponent(projectId, issueComponentDTO);
        IssueComponentE issueComponentE = ConvertHelper.convert(issueComponentDTO, IssueComponentE.class);
        return ConvertHelper.convert(issueComponentRepository.create(issueComponentE), IssueComponentDTO.class);
    }

    private Boolean checkNameUpdate(Long projectId, Long componentId, String componentName) {
        IssueComponentDO issueComponentDO = issueComponentMapper.selectByPrimaryKey(componentId);
        if (componentName.equals(issueComponentDO.getName())) {
            return false;
        }
        IssueComponentDO check = new IssueComponentDO();
        check.setProjectId(projectId);
        check.setName(componentName);
        List<IssueComponentDO> issueComponentDOList = issueComponentMapper.select(check);
        return issueComponentDOList != null && !issueComponentDOList.isEmpty();
    }

    @Override
    public IssueComponentDTO update(Long projectId, Long id, IssueComponentDTO issueComponentDTO) {
        if (checkNameUpdate(projectId, id, issueComponentDTO.getName())) {
            throw new CommonException("error.componentName.exist");
        }
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
    @SuppressWarnings("unchecked")
    public Page<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchDTO searchDTO, PageRequest pageRequest) {
        //处理用户搜索
        Boolean condition = handleSearchUser(searchDTO, projectId);
        if(condition){
            Page<ComponentForListDTO> componentForListDTOPage = ConvertPageHelper.convertPage(PageHelper.doPageAndSort(pageRequest, () ->
                    issueComponentMapper.queryComponentByOption(projectId, noIssueTest, componentId, searchDTO.getSearchArgs(),
                            searchDTO.getAdvancedSearchArgs(), searchDTO.getContents())), ComponentForListDTO.class);
            if ((componentForListDTOPage.getContent() != null) && !componentForListDTOPage.getContent().isEmpty()) {
                List<Long> assigneeIds = componentForListDTOPage.getContent().stream().filter(componentForListDTO -> componentForListDTO.getManagerId() != null
                        && !Objects.equals(componentForListDTO.getManagerId(), 0L)).map(ComponentForListDTO::getManagerId).distinct().collect(Collectors.toList());
                Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
                componentForListDTOPage.getContent().forEach(componentForListDTO -> {
                    UserMessageDO userMessageDO = usersMap.get(componentForListDTO.getManagerId());
                    String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
                    String assigneeLoginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
                    String assigneeRealName = userMessageDO != null ? userMessageDO.getRealName() : null;
                    String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
                    componentForListDTO.setManagerName(assigneeName);
                    componentForListDTO.setManagerLoginName(assigneeLoginName);
                    componentForListDTO.setManagerRealName(assigneeRealName);
                    componentForListDTO.setImageUrl(imageUrl);
                });
            }
            return componentForListDTOPage;
        }else{
            return new Page<>(new ArrayList<>(), new PageInfo(0,20) , 0);
        }

    }

    private Boolean handleSearchUser(SearchDTO searchDTO, Long projectId) {
        if (searchDTO.getSearchArgs() != null && searchDTO.getSearchArgs().get(MANAGER) != null) {
            String userName = (String) searchDTO.getSearchArgs().get(MANAGER);
            if (userName != null && !"" .equals(userName)) {
                List<UserDTO> userDTOS = userRepository.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchDTO.getAdvancedSearchArgs().put("managerId", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<IssueDTO> queryIssuesByComponentId(Long projectId, Long componentId) {
        return ConvertHelper.convertList(issueComponentMapper.queryIssuesByComponentId(projectId, componentId), IssueDTO.class);
    }

    @Override
    public List<ComponentForListDTO> listByProjectIdForTest(Long projectId, Long componentId, Boolean noIssueTest) {
        List<ComponentForListDTO> componentForListDTOList = ConvertHelper.convertList(
                issueComponentMapper.queryComponentWithIssueNum(projectId, componentId, noIssueTest), ComponentForListDTO.class);
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
    public Boolean checkComponentName(Long projectId, String componentName) {
        IssueComponentDO issueComponentDO = new IssueComponentDO();
        issueComponentDO.setProjectId(projectId);
        issueComponentDO.setName(componentName);
        List<IssueComponentDO> issueComponentDOList = issueComponentMapper.select(issueComponentDO);
        return issueComponentDOList != null && !issueComponentDOList.isEmpty();
    }
}
