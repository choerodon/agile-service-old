package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.IssueTypeDTO;
import io.choerodon.agile.infra.dataobject.StateMachineSchemeDTO;
import io.choerodon.agile.infra.enums.SchemeApplyType;
import io.choerodon.agile.infra.enums.SchemeType;
import io.choerodon.agile.infra.enums.StateMachineSchemeDeployStatus;
import io.choerodon.agile.infra.enums.StateMachineSchemeStatus;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.agile.infra.mapper.IssueTypeMapper;
import io.choerodon.agile.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.agile.infra.utils.ConvertUtils;
import io.choerodon.agile.infra.utils.PageUtil;
import io.choerodon.agile.infra.utils.ProjectUtil;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Service
public class StateMachineSchemeServiceImpl implements StateMachineSchemeService {

    private static final String WITHOUT_CONFIG_ISSUE_TYPE_NAME = "未分配类型";
    private static final String WITHOUT_CONFIG_ISSUE_TYPE_ICON = "style";
    private static final String WITHOUT_CONFIG_ISSUE_TYPE_COLOUR = "#808080";
    @Autowired
    private StateMachineSchemeMapper schemeMapper;
    @Autowired
    private StateMachineSchemeConfigService configService;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private IamFeignClient iamFeignClient;
    @Autowired
    private InitService initService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageInfo<StateMachineSchemeVO> pageQuery(Long organizationId, PageRequest pageRequest, StateMachineSchemeVO schemeVO, String params) {
        //查询出组织下的所有项目
        List<ProjectVO> projectVOS = iamFeignClient.queryProjectsByOrgId(organizationId, 1, 0).getBody().getList();
        Map<Long, ProjectVO> projectMap = projectVOS.stream().collect(Collectors.toMap(ProjectVO::getId, x -> x));
        //查询组织下的所有问题类型
        List<IssueTypeDTO> issueTypes = issueTypeMapper.queryByOrgId(organizationId);
        Map<Long, IssueTypeDTO> issueTypeMap = issueTypes.stream().collect(Collectors.toMap(IssueTypeDTO::getId, x -> x));
        //查询组织下的所有状态机
        List<StateMachineVO> stateMachineVOS = stateMachineService.queryByOrgId(organizationId);
        Map<Long, StateMachineVO> stateMachineVOMap = stateMachineVOS.stream().collect(Collectors.toMap(StateMachineVO::getId, x -> x));

        StateMachineSchemeDTO scheme = modelMapper.map(schemeVO, StateMachineSchemeDTO.class);
        PageInfo<StateMachineSchemeDTO> page = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> schemeMapper.fulltextSearch(scheme, params));

        List<StateMachineSchemeDTO> schemes = page.getList();
        List<StateMachineSchemeDTO> schemesWithConfigs = new ArrayList<>();
        if (!schemes.isEmpty()) {
            schemesWithConfigs = schemeMapper.queryByIdsWithConfig(organizationId, schemes.stream().map(StateMachineSchemeDTO::getId).collect(Collectors.toList()));
        }
        List<StateMachineSchemeVO> schemeVOS = ConvertUtils.convertStateMachineSchemesToVOS(schemesWithConfigs, projectMap);
        if (schemeVOS != null) {
            handleSchemeConfig(schemeVOS, issueTypeMap, stateMachineVOMap);
        }

        return PageUtil.buildPageInfoWithPageInfoList(page, schemeVOS);
    }

    @Override
    public StateMachineSchemeVO create(Long organizationId, StateMachineSchemeVO schemeVO) {
        if (checkName(organizationId, schemeVO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
        schemeVO.setStatus(StateMachineSchemeStatus.CREATE);
        StateMachineSchemeDTO scheme = modelMapper.map(schemeVO, StateMachineSchemeDTO.class);
        scheme.setOrganizationId(organizationId);
        int isInsert = schemeMapper.insert(scheme);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineScheme.create");
        }

        //创建一个defaultConfig
        StateMachineVO stateMachineVO = stateMachineService.queryDefaultStateMachine(organizationId);
        configService.createDefaultConfig(organizationId, scheme.getId(), stateMachineVO.getId());

        scheme = schemeMapper.selectByPrimaryKey(scheme);
        return modelMapper.map(scheme, StateMachineSchemeVO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long schemeId, String name) {
        StateMachineSchemeDTO scheme = new StateMachineSchemeDTO();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        StateMachineSchemeDTO res = schemeMapper.selectOne(scheme);
        return res != null && !schemeId.equals(res.getId());
    }

    @Override
    public StateMachineSchemeVO update(Long organizationId, Long schemeId, StateMachineSchemeVO schemeVO) {
        if (checkNameUpdate(organizationId, schemeId, schemeVO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
        schemeVO.setId(schemeId);
        schemeVO.setOrganizationId(organizationId);
        StateMachineSchemeDTO scheme = modelMapper.map(schemeVO, StateMachineSchemeDTO.class);
        int isUpdate = schemeMapper.updateByPrimaryKeySelective(scheme);
        if (isUpdate != 1) {
            throw new CommonException("error.stateMachineScheme.update");
        }
        scheme = schemeMapper.selectByPrimaryKey(scheme);
        return modelMapper.map(scheme, StateMachineSchemeVO.class);
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public Boolean delete(Long organizationId, Long schemeId) {
        StateMachineSchemeDTO scheme = schemeMapper.selectByPrimaryKey(schemeId);
        if (!scheme.getStatus().equals(StateMachineSchemeStatus.CREATE)) {
            throw new CommonException("error.stateMachineScheme.delete.illegal");
        }
        if (schemeId == null) {
            throw new CommonException("error.stateMachineScheme.delete.schemeId.null");
        }
        int isDelete = schemeMapper.deleteByPrimaryKey(schemeId);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachineScheme.delete");
        }
        //删除方案配置信息
        configService.deleteBySchemeId(organizationId, schemeId);
        return true;
    }

    @Override
    public StateMachineSchemeVO querySchemeWithConfigById(Boolean isDraft, Long organizationId, Long schemeId) {
        StateMachineSchemeDTO scheme = schemeMapper.selectByPrimaryKey(schemeId);
        if (scheme == null) {
            throw new CommonException("error.stateMachineScheme.notFound");
        }
        StateMachineSchemeVO schemeVO = modelMapper.map(scheme, StateMachineSchemeVO.class);
        //处理配置信息
        List<StateMachineSchemeConfigVO> configs = configService.queryBySchemeId(isDraft, organizationId, schemeId);
        Map<Long, List<IssueTypeDTO>> map = new HashMap<>(configs.size());
        //取默认配置到第一个
        Long defaultStateMachineId = null;
        for (StateMachineSchemeConfigVO config : configs) {
            List<IssueTypeDTO> issueTypes = map.get(config.getStateMachineId());
            if (issueTypes == null) {
                issueTypes = new ArrayList<>();
            }
            IssueTypeDTO issueType;
            if (!config.getDefault()) {
                issueType = issueTypeMapper.selectByPrimaryKey(config.getIssueTypeId());
            } else {
                //若为默认配置，则匹配的是所有为分配的问题类型
                issueType = new IssueTypeDTO();
                issueType.setName(WITHOUT_CONFIG_ISSUE_TYPE_NAME);
                issueType.setIcon(WITHOUT_CONFIG_ISSUE_TYPE_ICON);
                issueType.setColour(WITHOUT_CONFIG_ISSUE_TYPE_COLOUR);
                defaultStateMachineId = config.getStateMachineId();
            }
            issueTypes.add(issueType);
            map.put(config.getStateMachineId(), issueTypes);
        }

        List<StateMachineSchemeConfigViewVO> viewVOS = new ArrayList<>();
        //处理默认配置
        viewVOS.add(handleDefaultConfig(organizationId, defaultStateMachineId, map));
        for (Map.Entry<Long, List<IssueTypeDTO>> entry : map.entrySet()) {
            Long stateMachineId = entry.getKey();
            List<IssueTypeDTO> issueTypes = entry.getValue();
            StateMachineVO stateMachineVO = stateMachineService.queryStateMachineById(organizationId, stateMachineId);
            StateMachineSchemeConfigViewVO viewVO = new StateMachineSchemeConfigViewVO();
            viewVO.setStateMachineVO(stateMachineVO);
            List<IssueTypeVO> issueTypeVOS = modelMapper.map(issueTypes, new TypeToken<List<IssueTypeVO>>() {
            }.getType());
            viewVO.setIssueTypeVOS(issueTypeVOS);
            viewVOS.add(viewVO);
        }
        schemeVO.setViewVOS(viewVOS);
        return schemeVO;
    }

    /**
     * 处理默认配置到首位
     *
     * @param organizationId
     * @param defaultStateMachineId
     * @param map
     * @return
     */
    private StateMachineSchemeConfigViewVO handleDefaultConfig(Long organizationId, Long defaultStateMachineId, Map<Long, List<IssueTypeDTO>> map) {
        StateMachineSchemeConfigViewVO firstVO = new StateMachineSchemeConfigViewVO();
        StateMachineVO stateMachineVO = stateMachineService.queryStateMachineById(organizationId, defaultStateMachineId);
        firstVO.setStateMachineVO(stateMachineVO);
        firstVO.setIssueTypeVOS(modelMapper.map(map.get(defaultStateMachineId), new TypeToken<ArrayList<IssueTypeVO>>() {
        }.getType()));
        map.remove(defaultStateMachineId);
        return firstVO;
    }

    /**
     * 方案列表填充配置数据
     *
     * @param schemeVOS
     * @param issueTypeMap
     * @param stateMachineVOMap
     */
    private void handleSchemeConfig(List<StateMachineSchemeVO> schemeVOS, Map<Long, IssueTypeDTO> issueTypeMap, Map<Long, StateMachineVO> stateMachineVOMap) {
        schemeVOS.stream().map(StateMachineSchemeVO::getConfigVOS).filter(Objects::nonNull).forEach(machineSchemeVOS -> {
            for (StateMachineSchemeConfigVO configVO : machineSchemeVOS) {
                if (!configVO.getDefault()) {
                    IssueTypeDTO issueType = issueTypeMap.get(configVO.getIssueTypeId());
                    if (issueType != null) {
                        configVO.setIssueTypeName(issueType.getName());
                        configVO.setIssueTypeIcon(issueType.getIcon());
                        configVO.setIssueTypeColour(issueType.getColour());
                    }
                } else {
                    //若为默认配置，则匹配的是所有为分配的问题类型
                    configVO.setIssueTypeName(WITHOUT_CONFIG_ISSUE_TYPE_NAME);
                    configVO.setIssueTypeIcon(WITHOUT_CONFIG_ISSUE_TYPE_ICON);
                    configVO.setIssueTypeColour(WITHOUT_CONFIG_ISSUE_TYPE_COLOUR);
                }
                StateMachineVO stateMachineVO = stateMachineVOMap.get(configVO.getStateMachineId());
                if (stateMachineVO != null) {
                    configVO.setStateMachineName(stateMachineVO.getName());
                }
            }
        });
    }

    @Override
    public Boolean checkName(Long organizationId, String name) {
        StateMachineSchemeDTO scheme = new StateMachineSchemeDTO();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        StateMachineSchemeDTO res = schemeMapper.selectOne(scheme);
        return res != null;
    }

    @Override
    public List<StateMachineSchemeVO> querySchemeByStateMachineId(Long organizationId, Long stateMachineId) {
        List<Long> deploySchemeIds = configService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        List<Long> draftSchemeIds = configService.querySchemeIdsByStateMachineId(true, organizationId, stateMachineId);
        deploySchemeIds.addAll(draftSchemeIds);
        deploySchemeIds = deploySchemeIds.stream().distinct().collect(Collectors.toList());
        if (!deploySchemeIds.isEmpty()) {
            List<StateMachineSchemeDTO> stateMachineSchemes = schemeMapper.queryByIds(organizationId, deploySchemeIds);
            if (stateMachineSchemes != null && !stateMachineSchemes.isEmpty()) {
                return modelMapper.map(stateMachineSchemes, new TypeToken<List<StateMachineSchemeVO>>() {
                }.getType());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void initByConsumeCreateProject(ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //创建敏捷状态机方案
        initScheme(projectCode + "默认状态机方案【敏捷】", SchemeApplyType.AGILE, projectEvent);
        //创建测试状态机方案
        initScheme(projectCode + "默认状态机方案【测试】", SchemeApplyType.TEST, projectEvent);
    }

    @Override
    public void initByConsumeCreateProgram(ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //创建项目群状态机方案
        initScheme(projectCode + "默认状态机方案【项目群】", SchemeApplyType.PROGRAM, projectEvent);
    }

    /**
     * 初始化状态机方案
     *
     * @param name
     * @param schemeApplyType
     * @param projectEvent
     */
    private void initScheme(String name, String schemeApplyType, ProjectEvent projectEvent) {
        Long projectId = projectEvent.getProjectId();
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineId = initService.createStateMachineWithCreateProject(organizationId, schemeApplyType, projectEvent);

        StateMachineSchemeDTO scheme = new StateMachineSchemeDTO();
        scheme.setStatus(StateMachineSchemeStatus.CREATE);
        scheme.setName(name);
        scheme.setDescription(name);
        scheme.setOrganizationId(organizationId);
        //保证幂等性
        List<StateMachineSchemeDTO> stateMachines = schemeMapper.select(scheme);
        if (stateMachines.isEmpty()) {
            int isInsert = schemeMapper.insert(scheme);
            if (isInsert != 1) {
                throw new CommonException("error.stateMachineScheme.create");
            }
            //创建默认状态机配置
            configService.createDefaultConfig(organizationId, scheme.getId(), stateMachineId);
            //创建与项目的关联关系
            projectConfigService.create(projectId, scheme.getId(), SchemeType.STATE_MACHINE, schemeApplyType);
        }
    }

    @Override
    public void activeSchemeWithRefProjectConfig(Long schemeId) {
        StateMachineSchemeDTO scheme = schemeMapper.selectByPrimaryKey(schemeId);
        //活跃状态机方案
        if (scheme.getStatus().equals(StateMachineSchemeStatus.CREATE)) {
            scheme.setStatus(StateMachineSchemeStatus.ACTIVE);
            Criteria criteria = new Criteria();
            criteria.update("status");
            int result = schemeMapper.updateByPrimaryKeyOptions(scheme, criteria);
            if (result != 1) {
                throw new CommonException("error.stateMachineScheme.activeScheme");
            }
        }
        //复制草稿配置到发布
        configService.copyDraftToDeploy(false, scheme.getOrganizationId(), schemeId);
        //活跃方案下的所有新建状态机
        List<StateMachineSchemeConfigVO> configs = configService.queryBySchemeId(false, scheme.getOrganizationId(), schemeId);
        stateMachineService.activeStateMachines(scheme.getOrganizationId(), configs.stream().map(StateMachineSchemeConfigVO::getStateMachineId).distinct().collect(Collectors.toList()));
    }

    @Override
    public Boolean updateDeployProgress(Long organizationId, Long schemeId, Integer deployProgress) {
        int update = schemeMapper.updateDeployProgress(organizationId, schemeId, deployProgress);
        if (update == 1) {
            //若已完成，更新发布状态
            if (deployProgress.equals(100)) {
                schemeMapper.updateDeployStatus(organizationId, schemeId, StateMachineSchemeDeployStatus.DONE);
            }
            return true;
        } else {
            return false;
        }
    }
}
