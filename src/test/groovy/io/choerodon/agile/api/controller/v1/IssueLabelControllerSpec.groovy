package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueLabelDTO
import io.choerodon.agile.infra.dataobject.LabelIssueRelDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.LabelIssueRelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  9:06 2018/8/28
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueLabelControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueMapper issueMapper

    @Autowired
    LabelIssueRelMapper labelIssueRelMapper

    @Shared
    def realConut = 0

//    def 'listIssueLinkByIssueId'() {
//        when: '发送请求'
//        realConut = 0
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_labels', List, projectIds)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//
//        and: '设置值'
//        List<IssueLabelDTO> result = entity.body
//        if (result.size() == 0) {
////            IssueDO issueDO = new IssueDO()
////            issueDO.projectId = projectIds
////            List<IssueDO> listIssueDOResult = issueMapper.select(issueDO)
////            if (listIssueDOResult.size() != 0) {
////                for (int i = 0; i < listIssueDOResult.size(); i++) {
//            LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO()
//            LabelIssueRelDO.labelName = '测试标签'
//            if (labelIssueRelMapper.selectOne(labelIssueRelDO) != null) {
//                realConut = realConut + 1
//            }
////                }
////            }
//        } else {
//            realConut = result.size()
//        }
//
//        and: '设置期望值'
//        realConut == expectConut
//
//        where: '给定参数'
//        projectIds | expectConut
//        1L         | 1
//        2L         | 0
//        20L        | 0
//
//    }

}
