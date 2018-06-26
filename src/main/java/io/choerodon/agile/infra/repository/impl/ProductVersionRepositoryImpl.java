package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.dataobject.VersionIssueDO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.converter.ProductVersionConverter;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.domain.agile.repository.ProductVersionRepository;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.mybatis.helper.OptionalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Component
public class ProductVersionRepositoryImpl implements ProductVersionRepository {

    @Autowired
    private ProductVersionMapper versionMapper;
    @Autowired
    private ProductVersionConverter versionConverter;

    private static final String INSERT_ERROR = "error.version.insert";
    private static final String DELETE_ERROR = "error.version.delete";
    private static final String UPDATE_ERROR = "error.version.update";

    @Override
    public ProductVersionE createVersion(ProductVersionE versionE) {
        ProductVersionDO version = versionConverter.entityToDo(versionE);
        if(versionMapper.insertSelective(version) != 1){
            throw new CommonException(INSERT_ERROR);
        }
        return versionConverter.doToEntity(versionMapper.selectByPrimaryKey(version.getVersionId()));
    }

    @Override
    public Boolean deleteVersion(ProductVersionE versionE) {
        ProductVersionDO version = versionConverter.entityToDo(versionE);
        if(versionMapper.delete(version) != 1){
            throw new CommonException(DELETE_ERROR);
        }
        return true;
    }

    @Override
    public ProductVersionE updateVersion(ProductVersionE versionE, List<String> fieldList) {
        ProductVersionDO version = versionConverter.entityToDo(versionE);
        OptionalHelper.optional(fieldList);
        if(versionMapper.updateOptional(version) != 1){
            throw new CommonException(UPDATE_ERROR);
        }
        return versionConverter.doToEntity(versionMapper.selectByPrimaryKey(version.getVersionId()));
    }

    @Override
    public Boolean issueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDO> versionIssues) {
        versionMapper.issueToDestination(projectId, targetVersionId, versionIssues);
        return true;
    }

    @Override
    public Boolean releaseVersion(Long projectId, Long versionId, Date releaseDate) {
        versionMapper.releaseVersion(projectId, versionId, releaseDate);
        return true;
    }

    @Override
    public ProductVersionE updateVersion(ProductVersionE versionE) {
        ProductVersionDO version = versionConverter.entityToDo(versionE);
        if(versionMapper.updateByPrimaryKeySelective(version) != 1){
            throw new CommonException(UPDATE_ERROR);
        }
        return versionConverter.doToEntity(versionMapper.selectByPrimaryKey(version.getVersionId()));
    }

    @Override
    public int deleteByVersionIds(Long projectId, List<Long> versionIds) {
        return versionMapper.deleteByVersionIds(projectId, versionIds);
    }

}
