package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueComponentValidator;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.ComponentForListDTO;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDTO;
import io.choerodon.agile.app.service.ComponentIssueRelService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import io.choerodon.agile.infra.mapper.ComponentIssueRelMapper;
import io.choerodon.core.convertor.ConvertPageHelper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.IssueComponentService;
import io.choerodon.agile.infra.dataobject.IssueComponentDTO;
import io.choerodon.agile.infra.mapper.IssueComponentMapper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private IssueComponentMapper issueComponentMapper;

    @Autowired
    private ComponentIssueRelService componentIssueRelService;

    @Autowired
    private ComponentIssueRelMapper componentIssueRelMapper;

    @Autowired
    private UserService userService;

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
        return modelMapper.map(createBase(issueComponentDTO), IssueComponentVO.class);
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
        return modelMapper.map(updateBase(issueComponentDTO), IssueComponentVO.class);
    }

    private void unRelateIssueWithComponent(Long projectId, Long id) {
        componentIssueRelService.deleteByComponentId(projectId, id);
    }

    private void reRelateIssueWithComponent(Long projectId, Long id, Long relateComponentId) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        componentIssueRelDTO.setProjectId(projectId);
        componentIssueRelDTO.setComponentId(id);
        List<ComponentIssueRelDTO> componentIssueRelDTOList = componentIssueRelMapper.select(componentIssueRelDTO);
        unRelateIssueWithComponent(projectId, id);
        for (ComponentIssueRelDTO componentIssue : componentIssueRelDTOList) {
            ComponentIssueRelDTO relate = new ComponentIssueRelDTO();
            relate.setProjectId(projectId);
            relate.setIssueId(componentIssue.getIssueId());
            relate.setComponentId(relateComponentId);
            componentIssueRelService.create(relate);
        }
    }

    @Override
    public void delete(Long projectId, Long id, Long relateComponentId) {
        if (relateComponentId == null) {
            unRelateIssueWithComponent(projectId, id);
        } else {
            reRelateIssueWithComponent(projectId, id, relateComponentId);
        }
        deleteBase(id);
    }

    @Override
    public IssueComponentVO queryComponentsById(Long projectId, Long id) {
        IssueComponentDTO issueComponentDTO = issueComponentMapper.selectByPrimaryKey(id);
        if (issueComponentDTO == null) {
            throw new CommonException("error.component.get");
        }
        return modelMapper.map(issueComponentDTO, IssueComponentVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageInfo<ComponentForListVO> queryComponentByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchVO searchVO, PageRequest pageRequest) {
        //处理用户搜索
        Boolean condition = handleSearchUser(searchVO, projectId);
        if (condition) {
            PageInfo<ComponentForListDTO> componentForListDTOPage = PageHelper.startPage(pageRequest.getPage(),
                    pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() ->
                    issueComponentMapper.queryComponentByOption(projectId, noIssueTest, componentId, searchVO.getSearchArgs(),
                            searchVO.getAdvancedSearchArgs(), searchVO.getContents()));
            PageInfo<ComponentForListVO> componentForListVOPageInfo = modelMapper.map(componentForListDTOPage, new TypeToken<PageInfo>(){}.getType());
            componentForListVOPageInfo.setList(modelMapper.map(componentForListDTOPage.getList(), new TypeToken<List<ComponentForListVO>>(){}.getType()));
            if ((componentForListVOPageInfo.getList() != null) && !componentForListVOPageInfo.getList().isEmpty()) {
                List<Long> assigneeIds = componentForListVOPageInfo.getList().stream().filter(componentForListVO -> componentForListVO.getManagerId() != null
                        && !Objects.equals(componentForListVO.getManagerId(), 0L)).map(ComponentForListVO::getManagerId).distinct().collect(Collectors.toList());
                Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(assigneeIds, true);
                componentForListVOPageInfo.getList().forEach(componentForListVO -> {
                    UserMessageDTO userMessageDTO = usersMap.get(componentForListVO.getManagerId());
                    String assigneeName = userMessageDTO != null ? userMessageDTO.getName() : null;
                    String assigneeLoginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
                    String assigneeRealName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
                    String imageUrl = userMessageDTO != null ? userMessageDTO.getImageUrl() : null;
                    componentForListVO.setManagerName(assigneeName);
                    componentForListVO.setManagerLoginName(assigneeLoginName);
                    componentForListVO.setManagerRealName(assigneeRealName);
                    componentForListVO.setImageUrl(imageUrl);
                });
            }
            return componentForListVOPageInfo;
        } else {
            return new PageInfo<>(new ArrayList<>());
        }

    }

    private Boolean handleSearchUser(SearchVO searchVO, Long projectId) {
        if (searchVO.getSearchArgs() != null && searchVO.getSearchArgs().get(MANAGER) != null) {
            String userName = (String) searchVO.getSearchArgs().get(MANAGER);
            if (userName != null && !"".equals(userName)) {
                List<UserVO> userVOS = userService.queryUsersByNameAndProjectId(projectId, userName);
                if (userVOS != null && !userVOS.isEmpty()) {
                    searchVO.getAdvancedSearchArgs().put("managerId", userVOS.stream().map(UserVO::getId).collect(Collectors.toList()));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<IssueVO> queryIssuesByComponentId(Long projectId, Long componentId) {
        return modelMapper.map(issueComponentMapper.queryIssuesByComponentId(projectId, componentId), new TypeToken<List<IssueVO>>(){}.getType());
    }

    @Override
    public List<ComponentForListVO> listByProjectIdForTest(Long projectId, Long componentId, Boolean noIssueTest) {
        List<ComponentForListVO> componentForListDTOList = modelMapper.map(
                issueComponentMapper.queryComponentWithIssueNum(projectId, componentId, noIssueTest), new TypeToken<List<ComponentForListVO>>(){}.getType());
        List<Long> assigneeIds = componentForListDTOList.stream().filter(componentForListVO -> componentForListVO.getManagerId() != null
                && !Objects.equals(componentForListVO.getManagerId(), 0L)).map(ComponentForListVO::getManagerId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(assigneeIds, true);
        componentForListDTOList.forEach(componentForListVO -> {
            String assigneeName = usersMap.get(componentForListVO.getManagerId()) != null ? usersMap.get(componentForListVO.getManagerId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(componentForListVO.getManagerId()).getImageUrl() : null;
            componentForListVO.setManagerName(assigneeName);
            componentForListVO.setImageUrl(imageUrl);
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

    @Override
    public IssueComponentDTO createBase(IssueComponentDTO issueComponentDTO) {
        if (issueComponentMapper.insert(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.insert");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
        return modelMapper.map(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentDTO.class);
    }

    @Override
    public IssueComponentDTO updateBase(IssueComponentDTO issueComponentDTO) {
        if (issueComponentMapper.updateByPrimaryKeySelective(issueComponentDTO) != 1) {
            throw new CommonException("error.scrum_issue_component.update");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueComponentDTO.getProjectId() + ':' + CPMPONENT + "*"});
        return modelMapper.map(issueComponentMapper.selectByPrimaryKey(issueComponentDTO.getComponentId()), IssueComponentDTO.class);
    }

    @Override
    public void deleteBase(Long id) {
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
