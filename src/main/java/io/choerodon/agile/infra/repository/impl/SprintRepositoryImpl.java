package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.domain.agile.repository.SprintRepository;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.SprintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class SprintRepositoryImpl implements SprintRepository {

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private SprintConverter sprintConverter;
    @Autowired
    private RedisUtil redisUtil;

    private static final String INSERT_ERROR = "error.sprint.insert";
    private static final String DELETE_ERROR = "error.sprint.delete";
    private static final String UPDATE_ERROR = "error.sprint.update";


    @Override
    public SprintE createSprint(SprintE sprintE) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.insertSelective(sprintDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return sprintConverter.doToEntity(sprintMapper.selectByPrimaryKey(sprintDO.getSprintId()));
    }

    @Override
    public SprintE updateSprint(SprintE sprintE) {
        redisUtil.deleteRedisCache(new String[]{"Agile:BurnDownCoordinate" + sprintE.getProjectId() + ':' + sprintE.getSprintId() + ':' + "*",
                "BurnDownReport" + sprintE.getProjectId() + ':' + sprintE.getSprintId() + ':' + "*"});
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.updateByPrimaryKeySelective(sprintDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return sprintConverter.doToEntity(sprintMapper.selectByPrimaryKey(sprintDO.getSprintId()));
    }

    @Override
    public Boolean deleteSprint(SprintE sprintE) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        if (sprintMapper.delete(sprintDO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return true;
    }
}
