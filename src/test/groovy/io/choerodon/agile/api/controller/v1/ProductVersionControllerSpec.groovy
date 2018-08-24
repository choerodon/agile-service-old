package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ProductVersionCreateDTO
import io.choerodon.agile.api.dto.ProductVersionDetailDTO
import io.choerodon.agile.api.dto.ProductVersionReleaseDTO
import io.choerodon.agile.infra.dataobject.ProductVersionDO
import io.choerodon.agile.infra.dataobject.VersionIssueRelDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.ProductVersionMapper
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper
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

import java.text.SimpleDateFormat

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ProductVersionControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    ProductVersionMapper productVersionMapper

    @Autowired
    VersionIssueRelMapper versionIssueRelMapper

    @Autowired
    IssueMapper issueMapper

    @Shared
    def versionName = "version_test" + System.currentTimeMillis()

    @Shared
    def versionName2 = "version_test2"

    @Shared
    def versionName3 = "version_test3" + System.currentTimeMillis()

    @Shared
    def projectId = 1L

    @Shared
    ProductVersionDO result

    @Shared
    ProductVersionDO result2

    private Date StringToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.parse(str)
    }

    def 'createVersion'() {
        given:
        ProductVersionCreateDTO productVersionCreateDTO = new ProductVersionCreateDTO()
        productVersionCreateDTO.projectId = projectId
        productVersionCreateDTO.name = name
        productVersionCreateDTO.startDate = startDate
        productVersionCreateDTO.releaseDate = releaseDate

        when:
        HttpEntity<ProductVersionCreateDTO> productVersionCreateDTOHttpEntity = new HttpEntity<>(productVersionCreateDTO);
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version",
                HttpMethod.POST,
                productVersionCreateDTOHttpEntity,
                ProductVersionDetailDTO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        expect:
        entity.body.name == resultName

        where:
        projectId | startDate                           | releaseDate                         | name         | resultName
        1L        | null                                | null                                | versionName  | versionName
        1L        | null                                | null                                | null         | null
        1L        | StringToDate("2018-08-22 00:00:00") | StringToDate("2018-08-21 00:00:00") | versionName2 | null
        1L        | null                                | null                                | versionName  | null
        1L        | null                                | null                                | versionName3 | versionName3
    }

    def 'select product version'() {
        given:
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = versionName
        productVersionDO.projectId = projectId
        result = productVersionMapper.selectOne(productVersionDO)
        productVersionDO.name = versionName3
        result2 = productVersionMapper.selectOne(productVersionDO)
        result2.statusCode = "released"
        productVersionMapper.updateByPrimaryKey(result2)
        // 初始化version、issue关联关系
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO()
        versionIssueRelDO.projectId = projectId
        versionIssueRelDO.issueId = 2L
        versionIssueRelDO.versionId = result.versionId
        versionIssueRelDO.relationType = "fix"
        versionIssueRelMapper.insert(versionIssueRelDO)
    }

    def 'updateVersion'() {
        given:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("versionId", result.getVersionId())
        jsonObject.put("description", versionName)
        jsonObject.put("name", result.getName())
        jsonObject.put("projectId", result.getProjectId())

        when:
        jsonObject.put("objectVersionNumber", result.objectVersionNumber)
        HttpEntity<JSONObject> jsonObjectHttpEntity = new HttpEntity<>(jsonObject)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}",
                HttpMethod.PUT,
                jsonObjectHttpEntity,
                ProductVersionDetailDTO.class,
                projectId,
                result.getVersionId())

        jsonObject.put("objectVersionNumber", 0L)
        HttpEntity<JSONObject> changeObject = new HttpEntity<>(jsonObject)
        def entityException = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}",
                HttpMethod.PUT,
                changeObject,
                String.class,
                projectId,
                result.getVersionId())

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.description == versionName
        JSONObject exceptionInfo = JSONObject.parse(entityException.body)
        exceptionInfo.get("failed").toString() == "true"


    }

    def 'checkName'() {
        when:
        def entityTrue = restTemplate.exchange("/v1/projects/{project_id}/product_version/{name}/check",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                versionName)
        def entityFalse = restTemplate.exchange("/v1/projects/{project_id}/product_version/{name}/check",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                versionName2)

        then:
        entityTrue.statusCode.is2xxSuccessful()
        entityTrue.body == true

        entityFalse.statusCode.is2xxSuccessful()
        entityFalse.body == false

    }

    def 'queryVersionByProjectId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() > 0
    }

    def 'queryVersionByVersionId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/detail",
                HttpMethod.GET,
                new HttpEntity<>(),
                ProductVersionDetailDTO.class,
                projectId,
                result.versionId)
        def entityNull = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/detail",
                HttpMethod.GET,
                new HttpEntity<>(),
                ProductVersionDetailDTO.class,
                projectId,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == versionName
        entityNull.statusCode.is5xxServerError()
    }

    def 'releaseVersion'() {
        given:
        ProductVersionReleaseDTO productVersionReleaseDTO = new ProductVersionReleaseDTO()
        productVersionReleaseDTO.projectId = projectId
        productVersionReleaseDTO.releaseDate = new Date()
        productVersionReleaseDTO.versionId = result.versionId
        productVersionReleaseDTO.targetVersionId = result2.versionId

        when:
        HttpEntity<ProductVersionReleaseDTO> httpEntity = new HttpEntity<>(productVersionReleaseDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/release",
                HttpMethod.POST,
                httpEntity,
                ProductVersionDetailDTO.class,
                projectId)
        and:
        // 设置查询versionIssueRel条件
        VersionIssueRelDO search = new VersionIssueRelDO()
        search.versionId = result.versionId
        List<VersionIssueRelDO> relsOrigin = versionIssueRelMapper.select(search)
        search.versionId = result2.versionId
        List<VersionIssueRelDO> relsNew = versionIssueRelMapper.select(search)


        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.statusCode == "released"
        relsOrigin.size() == 1
        relsNew.size() == 1
        relsNew.get(0).issueId == 2L
    }

    def 'releaseVersion unSuccess'() {
        given:
        ProductVersionReleaseDTO productVersionReleaseDTO = new ProductVersionReleaseDTO()
        productVersionReleaseDTO.projectId = projectId
        productVersionReleaseDTO.releaseDate = new Date()
        productVersionReleaseDTO.versionId = result2.versionId

        when:
        HttpEntity<ProductVersionReleaseDTO> httpEntity = new HttpEntity<>(productVersionReleaseDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/release",
                HttpMethod.POST,
                httpEntity,
                String.class,
                projectId)


        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
    }

    def 'deleteVersion'() {
        given:
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = versionName3
        productVersionDO.projectId = projectId

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}",
                HttpMethod.DELETE,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                result2.getVersionId())

        then:
        entity.statusCode.is2xxSuccessful()
        ProductVersionDO deleteDO = productVersionMapper.selectOne(productVersionDO)
        deleteDO == null
    }


}
