package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.LookupTypeVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.infra.dataobject.LookupTypeDO;
import io.choerodon.agile.domain.agile.entity.LookupTypeE;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Component
public class LookupTypeConverter implements ConvertorI<LookupTypeE, LookupTypeDO, LookupTypeVO> {

    @Override
    public LookupTypeE dtoToEntity(LookupTypeVO lookupTypeVO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeVO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeE doToEntity(LookupTypeDO lookupTypeDO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeDO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeVO entityToDto(LookupTypeE lookupTypeE) {
        LookupTypeVO lookupTypeVO = new LookupTypeVO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeVO);
        return lookupTypeVO;
    }

    @Override
    public LookupTypeDO entityToDo(LookupTypeE lookupTypeE) {
        LookupTypeDO lookupTypeDO = new LookupTypeDO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeDO);
        return lookupTypeDO;
    }

    @Override
    public LookupTypeVO doToDto(LookupTypeDO lookupTypeDO) {
        LookupTypeVO lookupTypeVO = new LookupTypeVO();
        BeanUtils.copyProperties(lookupTypeDO, lookupTypeVO);
        return lookupTypeVO;
    }

    @Override
    public LookupTypeDO dtoToDo(LookupTypeVO lookupTypeVO) {
        LookupTypeDO lookupTypeDO = new LookupTypeDO();
        BeanUtils.copyProperties(lookupTypeVO, lookupTypeDO);
        return lookupTypeDO;
    }
}