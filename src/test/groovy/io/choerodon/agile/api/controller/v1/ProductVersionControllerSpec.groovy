package io.choerodon.agile.api.controller.v1

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

        when:HttpEntity<ProductVersionCreateDTO> productVersionCreateDTOHttpEntity = new HttpEntity<>(productVersionCreateDTO);
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
        projectId   |   startDate  | releaseDate  |   name        |      result
        1L    |   null | null     |       versionName |      versionName
        1L    | null | null  |       null        |      null
        1L    | StringToDate("2018-08-22 00:00:00") | StringToDate("2018-08-21 00:00:00") | versionName2 | null
        1L    | null | null | versionName | null
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
