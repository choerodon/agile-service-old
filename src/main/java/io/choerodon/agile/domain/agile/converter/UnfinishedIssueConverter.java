package io.choerodon.agile.domain.agile.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.UnfinishedIssueVO;
import io.choerodon.agile.infra.dataobject.UnfinishedIssueDO;
import io.choerodon.core.convertor.ConvertorI;

/**
 * Creator: changpingshi0213@gmail.com
 * Date:  17:24 2018/8/28
 * Description:
 */
@Component
public class UnfinishedIssueConverter implements ConvertorI<Object, UnfinishedIssueDO, UnfinishedIssueVO> {

    @Override
    public UnfinishedIssueVO doToDto(UnfinishedIssueDO unfinishedIssueDO) {
        UnfinishedIssueVO unfinishedIssueVO = new UnfinishedIssueVO();
        BeanUtils.copyProperties(unfinishedIssueDO, unfinishedIssueVO);
        return unfinishedIssueVO;
    }
}
