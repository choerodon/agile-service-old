package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.IssueTypeSchemeSearchVO;
import io.choerodon.agile.api.vo.IssueTypeSchemeVO;
import io.choerodon.agile.api.vo.IssueTypeSchemeWithInfoVO;
import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.app.service.IssueTypeSchemeService;
import io.choerodon.agile.app.service.IssueTypeService;
import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.app.service.ProjectConfigService;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.enums.InitIssueType;
import io.choerodon.agile.infra.enums.SchemeApplyType;
import io.choerodon.agile.infra.enums.SchemeType;
import io.choerodon.agile.infra.mapper.IssueTypeMapper;
import io.choerodon.agile.infra.mapper.IssueTypeSchemeConfigMapper;
import io.choerodon.agile.infra.mapper.IssueTypeSchemeMapper;
import io.choerodon.agile.infra.mapper.ProjectConfigMapper;
import io.choerodon.agile.infra.utils.PageUtil;
import io.choerodon.agile.infra.utils.ProjectUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/10
 */
@Component
@RefreshScope
public class IssueTypeSchemeServiceImpl implements IssueTypeSchemeService {

    private static final String ERROR_SCHEME_CREATE = "error.issueTypeScheme.create";
    @Autowired
    private IssueTypeSchemeMapper issueTypeSchemeMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public IssueTypeSchemeDTO baseCreate(IssueTypeSchemeDTO scheme) {
        if (issueTypeSchemeMapper.insert(scheme) != 1) {
            throw new CommonException(ERROR_SCHEME_CREATE);
        }
        return issueTypeSchemeMapper.selectByPrimaryKey(scheme.getId());
    }

    @Override
    public IssueTypeSchemeVO queryById(Long organizationId, Long issueTypeSchemeId) {
        IssueTypeSchemeDTO issueTypeScheme = issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeId);
        if (issueTypeScheme != null) {
            IssueTypeSchemeVO issueTypeSchemeVO = modelMapper.map(issueTypeScheme, IssueTypeSchemeVO.class);
            //根据方案配置表获取 问题类型
            List<IssueTypeDTO> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, issueTypeSchemeId);
            issueTypeSchemeVO.setIssueTypes(modelMapper.map(issueTypes, new TypeToken<List<IssueTypeVO>>() {
            }.getType()));
            return issueTypeSchemeVO;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IssueTypeSchemeVO create(Long organizationId, IssueTypeSchemeVO issueTypeSchemeVO) {
        //创建的均为通用的
        issueTypeSchemeVO.setApplyType(SchemeApplyType.COMMON);

        if (!checkName(organizationId, issueTypeSchemeVO.getName(), null)) {
            throw new CommonException("error.issueTypeScheme.name.exist");
        }

        issueTypeSchemeVO.setOrganizationId(organizationId);
        IssueTypeSchemeDTO issueTypeScheme = modelMapper.map(issueTypeSchemeVO, IssueTypeSchemeDTO.class);
        baseCreate(issueTypeScheme);
        //创建方案配置
        createConfig(organizationId, issueTypeScheme.getId(), issueTypeSchemeVO.getIssueTypes());

        return queryById(organizationId, issueTypeScheme.getId());
    }

    @Override
    public IssueTypeSchemeVO update(Long organizationId, IssueTypeSchemeVO issueTypeSchemeVO) {
        issueTypeSchemeVO.setApplyType(issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeVO.getId()).getApplyType());
        if (issueTypeSchemeVO.getName() != null && !checkName(organizationId, issueTypeSchemeVO.getName(), issueTypeSchemeVO.getId())) {
            throw new CommonException("error.issueTypeScheme.name.exist");
        }

        IssueTypeSchemeDTO issueTypeScheme = modelMapper.map(issueTypeSchemeVO, IssueTypeSchemeDTO.class);
        int isUpdate = issueTypeSchemeMapper.updateByPrimaryKeySelective(issueTypeScheme);
        if (isUpdate != 1) {
            throw new CommonException("error.issueTypeScheme.update");
        }
        //更新方案配置,等待校验[toDo]

        issueTypeSchemeConfigMapper.deleteBySchemeId(organizationId, issueTypeSchemeVO.getId());
        createConfig(organizationId, issueTypeScheme.getId(), issueTypeSchemeVO.getIssueTypes());

        return queryById(organizationId, issueTypeScheme.getId());
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long issueTypeSchemeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        IssueTypeSchemeDTO issueTypeScheme = issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeId);
        if (issueTypeScheme == null) {
            throw new CommonException("error.issueTypeScheme.notFound");
        }
        if (!issueTypeScheme.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.issueTypeScheme.illegal");
        }
        //判断要删除的issueTypeScheme是否有使用中的项目【toDo】


        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long issueTypeSchemeId) {
        Map<String, Object> result = checkDelete(organizationId, issueTypeSchemeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = issueTypeSchemeMapper.deleteByPrimaryKey(issueTypeSchemeId);
            if (isDelete != 1) {
                throw new CommonException("error.issueType.delete");
            }
            issueTypeSchemeConfigMapper.deleteBySchemeId(organizationId, issueTypeSchemeId);
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        IssueTypeSchemeDTO select = new IssueTypeSchemeDTO();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = issueTypeSchemeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public void createConfig(Long organizationId, Long issueTypeSchemeId, List<IssueTypeVO> issueTypeVOS) {
        if (issueTypeVOS != null && !issueTypeVOS.isEmpty()) {
            int sequence = 0;
            for (IssueTypeVO issueType : issueTypeVOS) {
                if (issueTypeMapper.selectByPrimaryKey(issueType.getId()) != null) {
                    IssueTypeSchemeConfigDTO config = new IssueTypeSchemeConfigDTO();
                    config.setIssueTypeId(issueType.getId());
                    config.setOrganizationId(organizationId);
                    config.setSchemeId(issueTypeSchemeId);
                    config.setSequence(BigDecimal.valueOf(sequence));
                    issueTypeSchemeConfigMapper.insert(config);
                } else {
                    throw new CommonException("error.issueType.notFound");
                }
                sequence++;
            }
        } else {
            throw new CommonException("error.issueType.null");
        }
    }

    @Override
    public void initByConsumeCreateProject(Long projectId, String projectCode) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        IssueTypeDTO query = new IssueTypeDTO();
        query.setOrganizationId(organizationId);
        query.setInitialize(true);
        List<IssueTypeDTO> issueTypes = issueTypeMapper.select(query);
        //处理老的组织没有创建的数据
        issueTypes = initOrganizationIssueType(organizationId, issueTypes);
        Map<String, IssueTypeDTO> issueTypeMap = issueTypes.stream().collect(Collectors.toMap(IssueTypeDTO::getTypeCode, x -> x));
        //初始化敏捷问题类型方案
        initScheme(projectId, organizationId, projectCode + "默认类型方案【敏捷】", issueTypeMap.get(InitIssueType.STORY.getTypeCode()).getId(), SchemeApplyType.AGILE, issueTypeMap);
        //初始化测试问题类型方案
        initScheme(projectId, organizationId, projectCode + "默认类型方案【测试】", issueTypeMap.get(InitIssueType.TEST.getTypeCode()).getId(), SchemeApplyType.TEST, issueTypeMap);
    }

    @Override
    public void initByConsumeCreateProgram(Long projectId, String projectCode) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        IssueTypeDTO query = new IssueTypeDTO();
        query.setOrganizationId(organizationId);
        query.setInitialize(true);
        List<IssueTypeDTO> issueTypes = issueTypeMapper.select(query);
        //处理老的组织没有创建的数据
        issueTypes = initOrganizationIssueType(organizationId, issueTypes);
        Map<String, IssueTypeDTO> issueTypeMap = issueTypes.stream().collect(Collectors.toMap(IssueTypeDTO::getTypeCode, x -> x));
        //初始化项目群问题类型方案
        initScheme(projectId, organizationId, projectCode + "默认类型方案【项目群】", issueTypeMap.get(InitIssueType.FEATURE.getTypeCode()).getId(), SchemeApplyType.PROGRAM, issueTypeMap);
    }

    private List<IssueTypeDTO> initOrganizationIssueType(Long organizationId, List<IssueTypeDTO> issueTypes) {
        if (issueTypes == null || issueTypes.isEmpty()) {
            //注册组织初始化问题类型
            issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
            //注册组织初始化优先级
            priorityService.initProrityByOrganization(Collections.singletonList(organizationId));
            IssueTypeDTO query = new IssueTypeDTO();
            query.setOrganizationId(organizationId);
            query.setInitialize(true);
            return issueTypeMapper.select(query);
        } else {
            return issueTypes;
        }
    }

    @Override
    public PageInfo<IssueTypeSchemeWithInfoVO> queryIssueTypeSchemeList(PageRequest pageRequest, Long organizationId, IssueTypeSchemeSearchVO issueTypeSchemeSearchVO) {
        PageInfo<Long> issueTypeSchemeIdsPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueTypeSchemeMapper.selectIssueTypeSchemeIds(organizationId, issueTypeSchemeSearchVO));
        List<IssueTypeSchemeWithInfoVO> issueTypeSchemeWithInfoVOList = new ArrayList<>(issueTypeSchemeIdsPage.getList().size());
        if (issueTypeSchemeIdsPage.getList() != null && !issueTypeSchemeIdsPage.getList().isEmpty()) {
            List<IssueTypeSchemeWithInfoDTO> issueTypeSchemeWithInfoList = issueTypeSchemeMapper.queryIssueTypeSchemeList(organizationId, issueTypeSchemeIdsPage.getList());
            issueTypeSchemeWithInfoVOList = modelMapper.map(issueTypeSchemeWithInfoList, new TypeToken<List<IssueTypeSchemeWithInfoVO>>() {
            }.getType());
            for (IssueTypeSchemeWithInfoVO type : issueTypeSchemeWithInfoVOList) {
                for (ProjectWithInfoDTO projectWithInfo : type.getProjectWithInfoList()) {
                    projectWithInfo.setProjectName(projectUtil.getName(projectWithInfo.getProjectId()));
                }
            }
        }

        return PageUtil.buildPageInfoWithPageInfoList(issueTypeSchemeIdsPage, issueTypeSchemeWithInfoVOList);
    }

    /**
     * 初始化方案
     *
     * @param projectId
     * @param organizationId
     * @param name
     * @param defaultIssueTypeId
     * @param schemeApplyType
     * @param issueTypeMap
     */
    private void initScheme(Long projectId, Long organizationId, String name, Long defaultIssueTypeId, String schemeApplyType, Map<String, IssueTypeDTO> issueTypeMap) {
        //初始化敏捷问题类型方案
        IssueTypeSchemeDTO issueTypeScheme = new IssueTypeSchemeDTO();
        issueTypeScheme.setName(name);
        issueTypeScheme.setDefaultIssueTypeId(defaultIssueTypeId);
        issueTypeScheme.setApplyType(schemeApplyType);
        issueTypeScheme.setOrganizationId(organizationId);
        issueTypeScheme.setDescription(name);
        //保证幂等性
        List<IssueTypeSchemeDTO> issueTypeSchemes = issueTypeSchemeMapper.select(issueTypeScheme);
        if (issueTypeSchemes.isEmpty()) {
            baseCreate(issueTypeScheme);
            Integer sequence = 0;
            for (InitIssueType initIssueType : InitIssueType.listByApplyType(schemeApplyType)) {
                sequence++;
                IssueTypeDTO issueType = issueTypeMap.get(initIssueType.getTypeCode());
                IssueTypeSchemeConfigDTO schemeConfig = new IssueTypeSchemeConfigDTO(issueTypeScheme.getId(), issueType.getId(), organizationId, BigDecimal.valueOf(sequence));
                if (issueTypeSchemeConfigMapper.insert(schemeConfig) != 1) {
                    throw new CommonException("error.issueTypeSchemeConfig.create");
                }
            }
            //创建与项目的关联关系
            projectConfigService.create(projectId, issueTypeScheme.getId(), SchemeType.ISSUE_TYPE, schemeApplyType);
        }
    }
}
