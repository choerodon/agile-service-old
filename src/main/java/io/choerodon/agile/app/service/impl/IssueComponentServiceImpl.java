package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueComponentValidator;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.repository.ComponentIssueRelRepository;
import io.choerodon.agile.infra.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.IssueComponentService;
import io.choerodon.agile.infra.repository.IssueComponentRepository;
import io.choerodon.agile.infra.dataobject.IssueComponentDTO;
import io.choerodon.agile.infra.mapper.IssueComponentMapper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    private static final String AGILE = "Agile:";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String CPMPONENT = "component";

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

    @Autowired
    private RedisUtil redisUtil;


    private static final String MANAGER = "manager";

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public IssueComponentVO create(Long projectId, IssueComponentVO issueComponentVO) {
        if (checkComponentName(projectId, issueComponentVO.getName())) {
            throw new CommonException("error.componentName.exist");
        }
        IssueComponentValidator.checkCreateComponent(projectId, issueComponentVO);
        IssueComponentDTO issueComponentDTO = modelMapper.map(issueComponentVO, IssueComponentDTO.class);
        return ConvertHelper.convert(insertComponent(issueComponentDTO), IssueComponentVO.class);
    }

    private Boolean checkNameUpdate(Long projectId, Long componentId, String componentName) {
        IssueComponentDTO issueComponentDTO = issueComponentMapper.selectByPrimaryKey(componentId);
        if (componentName.equals(issueComponentDTO.getName())) {
            return false;
        }
        IssueComponentDTO check = new IssueComponentDTO();
        check.setProjectId(projectId);
        check.setName(componentName);
        List<IssueComponentDTO> issueComponentDTOList = issueComponentMapper.select(check);
        return issueComponentDTOList != null && !issueComponentDTOList.isEmpty();
    }

    @Override
    public IssueComponentVO update(Long projectId, Long id, IssueComponentVO issueComponentVO) {
        if (checkNameUpdate(projectId, id, issueComponentVO.getName())) {
            throw new CommonException("error.componentName.exist");
        }
        issueComponentVO.setComponentId(id);
        IssueComponentDTO issueComponentDTO = modelMapper.map(issueComponentVO, IssueComponentDTO.class);
        return modelMapper.map(updateComponent(issueComponentDTO), IssueComponentVO.class);
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
        deleteComponent(id);
    }

    @Override
    public IssueComponentVO queryComponentsById(Long projectId, Long id) {
        IssueComponentDTO issueComponentDTO = issueComponentMapper.selectByPrimaryKey(id);
        if (issueComponentDTO == null) {
            throw new CommonException("error.component.get");
        }
        return ConvertHelper.convert(issueComponentDTO, IssueComponentVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageInfo<ComponentForListDTO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchVO searchVO, PageRequest pageRequest) {
        //处理用户搜索
        Boolean condition = handleSearchUser(searchVO, projectId);
        if (condition) {
            PageInfo<ComponentForListDTO> componentForListDTOPage = ConvertPageHelper.convertPageInfo(PageHelper.startPage(pageRequest.getPage(),
                    pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() ->
                    issueComponentMapper.queryComponentByOption(projectId, noIssueTest, componentId, searchVO.getSearchArgs(),
                            searchVO.getAdvancedSearchArgs(), searchVO.getContents())), ComponentForListDTO.class);
            if ((componentForListDTOPage.getList() != null) && !componentForListDTOPage.getList().isEmpty()) {
                List<Long> assigneeIds = componentForListDTOPage.getList().stream().filter(componentForListDTO -> componentForListDTO.getManagerId() != null
                        && !Objects.equals(componentForListDTO.getManagerId(), 0L)).map(ComponentForListDTO::getManagerId).distinct().collect(Collectors.toList());
                Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
                componentForListDTOPage.getList().forEach(componentForListDTO -> {
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
        } else {
            return new PageInfo<>(new ArrayList<>());
        }

    }

    private Boolean handleSearchUser(SearchVO searchVO, Long projectId) {
        if (searchVO.getSearchArgs() != null && searchVO.getSearchArgs().get(MANAGER) != null) {
            String userName = (String) searchVO.getSearchArgs().get(MANAGER);
            if (userName != null && !"".equals(userName)) {
                List<UserDTO> userDTOS = userRepository.queryUsersByNameAndProjectId(projectId, userName);
                if (userDTOS != null && !userDTOS.isEmpty()) {
                    searchVO.getAdvancedSearchArgs().put("managerId", userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<IssueVO> queryIssuesByComponentId(Long projectId, Long componentId) {
        return ConvertHelper.convertList(issueComponentMapper.queryIssuesByComponentId(projectId, componentId), IssueVO.class);
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
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO();
        issueComponentDTO.setProjectId(projectId);
        issueComponentDTO.setName(componentName);
        List<IssueComponentDTO> issueComponentDTOList = issueComponentMapper.select(issueComponentDTO);
        return issueComponentDTOList != null && !issueComponentDTOList.isEmpty();
    }

    public IssueComponentDTO insertComponent(IssueComponentDTO issueComponentDTO) {
        if (issueComponentMapper.insert(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.insert");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
        return modelMapper.map(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentDTO.class);
    }

    public IssueComponentDTO updateComponent(IssueComponentDTO issueComponentDTO) {
        if (issueComponentMapper.updateByPrimaryKeySelective(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.update");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
        return modelMapper.map(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentDTO.class);
    }

    public void deleteComponent(Long id) {
        IssueComponentDTO issueComponentDTO = issueComponentMapper.selectByPrimaryKey(id);
        if (issueComponentDTO == null) {
            throw new CommonException("error.component.get");
        }
        if (issueComponentMapper.delete(issueComponentDTO) != 1) {
            throw new CommonException("error.component.delete");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
    }
}
