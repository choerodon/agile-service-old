package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ProductVersionCreateDTO
import io.choerodon.agile.api.dto.ProductVersionDetailDTO
import io.choerodon.agile.infra.dataobject.ProductVersionDO
import io.choerodon.agile.infra.mapper.ProductVersionMapper
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

    @Shared
    def versionName = "version_test" + System.currentTimeMillis()

    @Shared
    def versionName2 = "version_test2"

    @Shared
    def projectId = 1L

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
        entity.body.name == result

        where:
        projectId | startDate                           | releaseDate                         | name         | result
        1L        | null                                | null                                | versionName  | versionName
        1L        | null                                | null                                | null         | null
        1L        | StringToDate("2018-08-22 00:00:00") | StringToDate("2018-08-21 00:00:00") | versionName2 | null
        1L        | null                                | null                                | versionName  | null
    }

    def 'updateVersion'() {
        given:
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = versionName
        productVersionDO.projectId = projectId
        ProductVersionDO result = productVersionMapper.selectOne(productVersionDO)
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
        given:
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = versionName
        productVersionDO.projectId = projectId
        ProductVersionDO result = productVersionMapper.selectOne(productVersionDO)

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

    def 'deleteVersion'() {
        given:
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = versionName
        productVersionDO.projectId = projectId
        ProductVersionDO result = productVersionMapper.selectOne(productVersionDO)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}",
                HttpMethod.DELETE,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                result.getVersionId())

        then:
        entity.statusCode.is2xxSuccessful()
        ProductVersionDO deleteDO = productVersionMapper.selectOne(productVersionDO)
        deleteDO == null
    }


}
