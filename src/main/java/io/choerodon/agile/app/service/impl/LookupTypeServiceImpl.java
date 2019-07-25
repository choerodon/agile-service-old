package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.LookupTypeVO;
import io.choerodon.agile.app.service.LookupTypeService;
import io.choerodon.agile.infra.mapper.LookupTypeMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Autowired
    private LookupTypeMapper lookupTypeMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<LookupTypeVO> listLookupType(Long project) {
        return modelMapper.map(lookupTypeMapper.selectAll(), new TypeToken<List<LookupTypeVO>>(){}.getType());
    }

}