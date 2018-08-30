package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ProjectInfoDTO
import io.choerodon.agile.infra.dataobject.ProjectInfoDO
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
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

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: scp
 * Date:  9:16 2018/8/28
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ProjectInfoControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    ProjectInfoMapper projectInfoMapper

    @Shared
    def projectId = 1

    def 'updateProjectInfo'() {
        given: '链接issueDTO对象'
        ProjectInfoDO projectInfoDO = projectInfoMapper.selectByPrimaryKey(1L)
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO()
        projectInfoDTO.projectId = projectInfoDO.projectId
        projectInfoDTO.infoId = projectInfoDO.infoId
        projectInfoDTO.defaultAssigneeId = projectInfoDO.defaultAssigneeId
        projectInfoDTO.defaultPriorityCode = projectInfoDO.defaultPriorityCode
        projectInfoDTO.projectCode = 'AGTest'
        projectInfoDTO.objectVersionNumber=projectInfoDO.objectVersionNumber
        projectInfoDTO.creationDate=projectInfoDO.creationDate
        projectInfoDTO.defaultAssigneeType=projectInfoDO.defaultAssigneeType
        HttpEntity<ProjectInfoDTO> requestEntity = new HttpEntity<ProjectInfoDTO>(projectInfoDTO, null)


        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/project_info',
                HttpMethod.PUT,
                requestEntity,
                ProjectInfoDTO.class,
                projectId
        )
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        ProjectInfoDTO result = entity.body

        expect: '设置期望值'
        result.projectId == projectInfoDTO.projectId
        result.projectCode == 'AGTest'
    }


    def 'queryProjectInfoByProjectId'() {
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/project_info', ProjectInfoDTO, projectIds)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        ProjectInfoDTO projectInfoDTO = entity.body

        and: '设置值'
        projectInfoDTO.projectCode == expectObject

        where: '期望值'
        projectIds | expectObject
        1L         | 'AGTest'
        10L        | null

    }

}
