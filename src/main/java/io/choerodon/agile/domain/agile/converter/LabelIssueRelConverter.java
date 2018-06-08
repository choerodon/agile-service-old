package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.infra.dataobject.LabelIssueRelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.LabelIssueRelDTO;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@Component
public class LabelIssueRelConverter implements ConvertorI<LabelIssueRelE, LabelIssueRelDO, LabelIssueRelDTO> {

    @Override
    public LabelIssueRelE dtoToEntity(LabelIssueRelDTO labelIssueRelDTO) {
        LabelIssueRelE labelIssueRelE = new LabelIssueRelE();
        BeanUtils.copyProperties(labelIssueRelDTO, labelIssueRelE);
        return labelIssueRelE;
    }

    @Override
    public LabelIssueRelE doToEntity(LabelIssueRelDO labelIssueRelDO) {
        LabelIssueRelE labelIssueRelE = new LabelIssueRelE();
        BeanUtils.copyProperties(labelIssueRelDO, labelIssueRelE);
        return labelIssueRelE;
    }

    @Override
    public LabelIssueRelDTO entityToDto(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
        BeanUtils.copyProperties(labelIssueRelE, labelIssueRelDTO);
        return labelIssueRelDTO;
    }

    @Override
    public LabelIssueRelDO entityToDo(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO();
        BeanUtils.copyProperties(labelIssueRelE, labelIssueRelDO);
        return labelIssueRelDO;
    }

    @Override
    public LabelIssueRelDTO doToDto(LabelIssueRelDO labelIssueRelDO) {
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
        BeanUtils.copyProperties(labelIssueRelDO, labelIssueRelDTO);
        return labelIssueRelDTO;
    }

    @Override
    public LabelIssueRelDO dtoToDo(LabelIssueRelDTO labelIssueRelDTO) {
        LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO();
        BeanUtils.copyProperties(labelIssueRelDTO, labelIssueRelDO);
        return labelIssueRelDO;
    }
}