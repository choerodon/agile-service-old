package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.WorkLogDTO
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.WorkLogMapper
import io.choerodon.core.convertor.ConvertHelper
import org.mockito.Matchers
import org.mockito.Mockito
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
    @Qualifier("userRepository")
    private UserRepository userRepository

    @Shared
    def projectId = 1L

    @Shared
    def workLogDTOList = []

    def setup() {

        given:
        UserDO userDO = new UserDO()
        userDO.setRealName("管理员")
        Mockito.when(userRepository.queryUserNameByOption(Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(userDO)
        Mockito.when(userRepository.queryUsersMap(Matchers.any(List.class), Matchers.anyBoolean())).thenReturn(new HashMap<Long, UserMessageDO>())
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
        def resultSuccess = restTemplate.exchange("/v1/projects/$projectId/work_log",
                HttpMethod.POST,
                workLogDTOEntity,
                WorkLogDTO.class
        )
        workLogDTO.issueId = Integer.MAX_VALUE
        workLogDTOEntity = new HttpEntity<>(workLogDTO)
        def resultFailure = restTemplate.exchange("/v1/projects/$projectId/work_log",
                HttpMethod.POST,
                workLogDTOEntity,
                String.class
        )

        then:
        resultSuccess.statusCode.is2xxSuccessful()
        resultSuccess.body.workTime == workTime
        resultSuccess.body.startDate == nowDate
        resultSuccess.body.issueId == issueId

        resultFailure.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
        exceptionInfo.get("failed").toString() == "true"

        and:
        workLogDTOList << ConvertHelper.convert(workLogMapper.selectByPrimaryKey(resultSuccess.body.logId), WorkLogDTO)
    }

    def "updateWorkLog"() {

        given:
        WorkLogDTO workLogDTO = new WorkLogDTO()
        workLogDTO.projectId = projectId
        def workLogDTOEntity = new HttpEntity<>(workLogDTO)
        def resultFailure = restTemplate.exchange("/v1/projects/{projectId}/work_log/{logId}",
                HttpMethod.PATCH,
                workLogDTOEntity,
                String.class,
                projectId,
                workLog.logId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.logId.isNull"

        when:
        workLogDTO.issueId = workLog.issueId
        workLogDTO.startDate = workLog.startDate
        workLogDTO.workTime = workLog.workTime + 1
        workLogDTO.logId = workLog.logId
        workLogDTO.objectVersionNumber = workLog.objectVersionNumber
        workLogDTOEntity = new HttpEntity<>(workLogDTO)
        def resultSuccess = restTemplate.exchange("/v1/projects/{projectId}/work_log/{logId}",
                HttpMethod.PATCH,
                workLogDTOEntity,
                WorkLogDTO.class,
                projectId,
                workLog.logId
        )

        then:
        resultSuccess.statusCode.is2xxSuccessful()
        resultSuccess.body.workTime == workLog.workTime + 1
        resultSuccess.body.startDate == workLog.startDate
        resultSuccess.body.issueId == workLog.issueId
        resultSuccess.body.logId == workLog.logId
        workLog.workTime++

        where:
        workLog << workLogDTOList
    }

    def "queryWorkLogById"() {

        given:
        def resultFailure = restTemplate.getForEntity("/v1/projects/$projectId/work_log/${Integer.MAX_VALUE}", String)
        assert resultFailure.statusCode.is5xxServerError()
        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("error").toString() == "Internal Server Error"

        when:
        def resultSuccess = restTemplate.getForEntity("/v1/projects/$projectId/work_log/${workLog.logId}", WorkLogDTO)

        then:
        resultSuccess.statusCode.is2xxSuccessful()
        resultSuccess.body.logId == workLog.logId
        resultSuccess.body.issueId == workLog.issueId
        resultSuccess.body.workTime == workLog.workTime
        resultSuccess.body.startDate == workLog.startDate

        where:
        workLog << workLogDTOList
    }

    def "queryWorkLogListByIssueId"() {

        given:
        def issueId = issueMapper.selectAll().get(0).issueId

        when:
        def resultSuccess = restTemplate.exchange("/v1/projects/{projectId}/work_log/issue/{issue}",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
                issueId
        )
        def resultFailure = restTemplate.exchange("/v1/projects/{projectId}/work_log/issue/{issue}",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
                Integer.MAX_VALUE
        )

        then:
        resultSuccess.statusCode.is2xxSuccessful()
        resultSuccess.body.size() > 0

        resultFailure.statusCode.is2xxSuccessful()
        resultFailure.body.size() == 0

    }

    def "deleteWorkLog"() {

        given:
        restTemplate.delete("/v1/projects/$projectId/work_log/${Integer.MAX_VALUE}")

        when:
        restTemplate.delete("/v1/projects/$projectId/work_log/$logId")

        then:
        workLogMapper.selectByPrimaryKey(logId) == null

        where:
        logId << workLogDTOList.logId
    }
}
