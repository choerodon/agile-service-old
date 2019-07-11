package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.LabelIssueRelVO;
import io.choerodon.agile.infra.dataobject.LabelIssueRelDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.domain.agile.entity.LabelIssueRelE;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@Component
public class LabelIssueRelConverter implements ConvertorI<LabelIssueRelE, LabelIssueRelDTO, LabelIssueRelVO> {

    @Override
    public LabelIssueRelE dtoToEntity(LabelIssueRelVO labelIssueRelVO) {
        LabelIssueRelE labelIssueRelE = new LabelIssueRelE();
        BeanUtils.copyProperties(labelIssueRelVO, labelIssueRelE);
        return labelIssueRelE;
    }

    @Override
    public LabelIssueRelE doToEntity(LabelIssueRelDTO labelIssueRelDTO) {
        LabelIssueRelE labelIssueRelE = new LabelIssueRelE();
        BeanUtils.copyProperties(labelIssueRelDTO, labelIssueRelE);
        return labelIssueRelE;
    }

    @Override
    public LabelIssueRelVO entityToDto(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelVO labelIssueRelVO = new LabelIssueRelVO();
        BeanUtils.copyProperties(labelIssueRelE, labelIssueRelVO);
        return labelIssueRelVO;
    }

    @Override
    public LabelIssueRelDTO entityToDo(LabelIssueRelE labelIssueRelE) {
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
        BeanUtils.copyProperties(labelIssueRelE, labelIssueRelDTO);
        return labelIssueRelDTO;
    }

    @Override
    public LabelIssueRelVO doToDto(LabelIssueRelDTO labelIssueRelDTO) {
        LabelIssueRelVO labelIssueRelVO = new LabelIssueRelVO();
        BeanUtils.copyProperties(labelIssueRelDTO, labelIssueRelVO);
        return labelIssueRelVO;
    }

    @Override
    public LabelIssueRelDTO dtoToDo(LabelIssueRelVO labelIssueRelVO) {
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO();
        BeanUtils.copyProperties(labelIssueRelVO, labelIssueRelDTO);
        return labelIssueRelDTO;
    }
}