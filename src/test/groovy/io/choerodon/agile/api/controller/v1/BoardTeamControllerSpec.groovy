package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.BoardTeamDTO
import io.choerodon.agile.api.vo.BoardTeamUpdateDTO
import io.choerodon.agile.api.vo.ProgramBoardFilterDTO
import io.choerodon.agile.api.vo.ProgramBoardInfoDTO
import io.choerodon.agile.app.service.BoardFeatureService
import io.choerodon.agile.infra.dataobject.ArtDTO
import io.choerodon.agile.infra.dataobject.PiDTO
import io.choerodon.agile.infra.dataobject.SprintDTO
import io.choerodon.agile.infra.mapper.ArtMapper
import io.choerodon.agile.infra.mapper.PiMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author shinan.chen
 * @since 2019/6/4
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class BoardTeamControllerSpec extends Specification {

    @Autowired
    private ArtMapper artMapper
    @Autowired
    private PiMapper piMapper
    @Autowired
    private SprintMapper sprintMapper
    @Autowired
    TestRestTemplate restTemplate
    @Autowired
    BoardFeatureService boardFeatureService
    @Shared
    Long programId = 1L
    @Shared
    Long artId
    @Shared
    Long piId
    @Shared
    Long sprintId
    @Shared
    ProgramBoardInfoDTO infoDTO

    def url = '/v1/projects/{project_id}/board_team'

    def initProgram(){
        ArtDTO art = new ArtDTO()
        art.enabled = true
        art.name = '火车1'
        art.code = 'huoche1'
        art.statusCode = 'doing'
        art.programId = programId
        artMapper.insert(art)
        PiDTO pi = new PiDTO()
        pi.programId = programId
        pi.code = 'pi'
        pi.name = 'pi'
        pi.artId = art.id
        pi.statusCode = "doing"
        piMapper.insert(pi)
        SprintDTO sprint = new SprintDTO()
        sprint.statusCode = "sprint_planning"
        sprint.projectId = programId
        sprint.piId = pi.id
        sprint.sprintName = 'sprint'
        sprintMapper.insert(sprint)
        artId = art.id
        piId = pi.id
        sprintId = sprint.sprintId
    }

    def setup() {
        println "执行初始化"
        initProgram()
        ProgramBoardFilterDTO filter = new ProgramBoardFilterDTO()
        filter.onlyDependFeature = false
        filter.onlyOtherTeamDependFeature = false
        infoDTO = boardFeatureService.queryBoardInfo(programId, filter)
    }

    def cleanup() {
        artMapper.deleteByPrimaryKey(artId)
        piMapper.deleteByPrimaryKey(piId)
        sprintMapper.deleteByPrimaryKey(sprintId)
    }

    def "update"() {
        given: '准备'
        BoardTeamUpdateDTO update = new BoardTeamUpdateDTO()
        update.before = true
        update.outsetId = 1L
        update.objectVersionNumber = 1L

        when: '移动公告板团队'
        HttpEntity<BoardTeamUpdateDTO> httpEntity = new HttpEntity<>(update)
        def entity = restTemplate.exchange(url + "/{boardTeamId}", HttpMethod.PUT, httpEntity, BoardTeamDTO.class, programId, 2L)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        expRequest | expResponse
        true       | true
    }
}