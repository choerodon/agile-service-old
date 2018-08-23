package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.WorkLogDTO
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.WorkLogMapper
import io.choerodon.core.convertor.ConvertHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class WorkLogControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private WorkLogMapper workLogMapper

    @Autowired
    private IssueMapper issueMapper

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    @Shared
    def projectId = 1L

    @Shared
    def workLogDTOList = []

    def setup() {
        given:
        UserDO userDO = new UserDO()
        userDO.setRealName("管理员")
        userRepository.queryUserNameByOption(*_) >> userDO
        userRepository.queryUsersMap(*_) >> new HashMap<Long, UserMessageDO>()
    }

    def "createWorkLog"() {
        given:
        WorkLogDTO workLogDTO = new WorkLogDTO()
        workLogDTO.projectId = projectId
        def workTime = 3
        def issueId = issueMapper.selectAll().get(0).issueId
        when:
        def now = System.currentTimeMillis()
        def nowDate = new Date(now - now % 1000)
        workLogDTO.issueId = issueId
        workLogDTO.startDate = nowDate
        workLogDTO.workTime = workTime
        def workLogDTOEntity = new HttpEntity<>(workLogDTO)
        def result = restTemplate.exchange("/v1/projects/$projectId/work_log",
                HttpMethod.POST,
                workLogDTOEntity,
                WorkLogDTO.class
        )
        then:
        result.statusCode.is2xxSuccessful()
        result.body.workTime == workTime
        result.body.startDate == nowDate
        result.body.issueId == issueId
        and:
        workLogDTOList << ConvertHelper.convert(workLogMapper.selectByPrimaryKey(result.body.logId), WorkLogDTO)
    }

    def "updateWorkLog"() {
        given:
        WorkLogDTO workLogDTO = new WorkLogDTO()
        workLogDTO.projectId = projectId
        when:
        workLogDTO.issueId = workLog.issueId
        workLogDTO.startDate = workLog.startDate
        workLogDTO.workTime = workLog.workTime + 1
        workLogDTO.logId = workLog.logId
        workLogDTO.objectVersionNumber = workLog.objectVersionNumber
        def workLogDTOEntity = new HttpEntity<>(workLogDTO)
        def result = restTemplate.exchange("/v1/projects/$projectId/work_log/${workLog.logId}",
                HttpMethod.PATCH,
                workLogDTOEntity,
                WorkLogDTO.class
        )
        then:
        result.statusCode.is2xxSuccessful()
        result.body.workTime == workLog.workTime + 1
        result.body.startDate == workLog.startDate
        result.body.issueId == workLog.issueId
        result.body.logId == workLog.logId
        and:
        workLog.workTime++
        where:
        workLog << workLogDTOList
    }

    def "queryWorkLogById"() {
        when:
        def result = restTemplate.getForEntity("/v1/projects/$projectId/work_log/${workLog.logId}", WorkLogDTO)
        then:
        result.statusCode.is2xxSuccessful()
        result.body.logId == workLog.logId
        result.body.issueId == workLog.issueId
        result.body.workTime == workLog.workTime
        result.body.startDate == workLog.startDate
        where:
        workLog << workLogDTOList
    }

    def "queryWorkLogListByIssueId"() {
        given:
        def issueId = issueMapper.selectAll().get(0).issueId
        when:
        def result = restTemplate.exchange("/v1/projects/{projectId}/work_log/issue/{issue}",
                HttpMethod.GET,
                null,
                Object.class,
                projectId,
                issueId
        )
        then:
        result.statusCode.is2xxSuccessful()
        result.body.content.size() > 0
    }

    def "deleteWorkLog"() {
        when:
        restTemplate.delete("/v1/projects/$projectId/work_log/$logId")
        then:
        workLogMapper.selectByPrimaryKey(logId) == null
        where:
        logId << workLogDTOList.logId
    }
}
