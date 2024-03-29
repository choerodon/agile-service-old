package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.*
import io.choerodon.agile.app.service.ProductVersionService
import io.choerodon.agile.infra.dataobject.ProductVersionDTO
import io.choerodon.agile.infra.dataobject.VersionIssueRelDTO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.ProductVersionMapper
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper
import com.github.pagehelper.PageInfo
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
    ProductVersionService productVersionService

    @Autowired
    IssueMapper issueMapper

    @Shared
    def versionName = "version_test" + System.currentTimeMillis()

    @Shared
    def versionName2 = "version_test2" + System.currentTimeMillis()

    @Shared
    def versionName3 = "version_test3" + System.currentTimeMillis()

    @Shared
    def versionMergeName = "version_merge" + System.currentTimeMillis()

    @Shared
    def projectId = 1L

    @Shared
    ProductVersionDTO result

    @Shared
    ProductVersionDTO result2

    @Shared
    ProductVersionDTO result3

    private Date StringToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.parse(str)
    }

    def 'createVersion'() {
        given:
        ProductVersionCreateVO productVersionCreateDTO = new ProductVersionCreateVO()
        productVersionCreateDTO.projectId = projectId
        productVersionCreateDTO.name = name
        productVersionCreateDTO.startDate = startDate
        productVersionCreateDTO.expectReleaseDate = expectReleaseDate

        when:
        HttpEntity<ProductVersionCreateVO> productVersionCreateDTOHttpEntity = new HttpEntity<>(productVersionCreateDTO);
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version",
                HttpMethod.POST,
                productVersionCreateDTOHttpEntity,
                ProductVersionDetailVO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        expect:
        entity.body.name == resultName

        where:
        projectId | startDate                           | expectReleaseDate                   | name             | resultName
        1L        | null                                | null                                | versionName      | versionName
        1L        | null                                | null                                | null             | null
        1L        | StringToDate("2018-08-22 00:00:00") | StringToDate("2018-08-21 00:00:00") | versionName2     | null
        1L        | null                                | null                                | versionName      | null
        1L        | null                                | null                                | versionName3     | versionName3
        1L        | null                                | null                                | versionMergeName | versionMergeName
    }

    def 'select product version'() {
        given:
        ProductVersionDTO productVersionDO = new ProductVersionDTO()
        productVersionDO.name = versionName
        productVersionDO.projectId = projectId
        result = productVersionMapper.selectOne(productVersionDO)
        productVersionDO.name = versionName3
        result2 = productVersionMapper.selectOne(productVersionDO)
        result2.statusCode = "released"
        productVersionMapper.updateByPrimaryKey(result2)
        productVersionDO.name = versionMergeName
        result3 = productVersionMapper.selectOne(productVersionDO)

        // 初始化version、issue关联关系
        VersionIssueRelDTO versionIssueRelDO = new VersionIssueRelDTO()
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
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/update/{versionId}",
                HttpMethod.PUT,
                jsonObjectHttpEntity,
                ProductVersionDetailVO.class,
                projectId,
                result.getVersionId())

        jsonObject.put("objectVersionNumber", 0L)
        HttpEntity<JSONObject> changeObject = new HttpEntity<>(jsonObject)
        def entityException = restTemplate.exchange("/v1/projects/{project_id}/product_version/update/{versionId}",
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
        exceptionInfo.get("code").toString() == "error.version.update"

    }

    def 'checkName'() {
        when:
        def entityTrue = restTemplate.exchange("/v1/projects/{project_id}/product_version/check?name={name}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                versionName)
        def entityFalse = restTemplate.exchange("/v1/projects/{project_id}/product_version/check?name={name}",
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
                ProductVersionDetailVO.class,
                projectId,
                result.versionId)
        def entityNull = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/detail",
                HttpMethod.GET,
                new HttpEntity<>(),
                ProductVersionDetailVO.class,
                projectId,
                0L)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == versionName
        entityNull.statusCode.is2xxSuccessful()
    }

    def 'releaseVersion'() {
        given:
        ProductVersionReleaseVO productVersionReleaseDTO = new ProductVersionReleaseVO()
        productVersionReleaseDTO.projectId = projectId
        productVersionReleaseDTO.releaseDate = new Date()
        productVersionReleaseDTO.versionId = result.versionId
        productVersionReleaseDTO.targetVersionId = result2.versionId

        when:
        HttpEntity<ProductVersionReleaseVO> httpEntity = new HttpEntity<>(productVersionReleaseDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/release",
                HttpMethod.POST,
                httpEntity,
                ProductVersionDetailVO.class,
                projectId)
        and:
        // 设置查询versionIssueRel条件
        VersionIssueRelDTO search = new VersionIssueRelDTO()
        search.versionId = result.versionId
        List<VersionIssueRelDTO> relsOrigin = versionIssueRelMapper.select(search)
        search.versionId = result2.versionId
        List<VersionIssueRelDTO> relsNew = versionIssueRelMapper.select(search)


        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.statusCode == "released"
        relsOrigin.size() == 0
        relsNew.size() == 1
        relsNew.get(0).issueId == 2L
    }

    def 'releaseVersion unSuccess'() {
        given:
        ProductVersionReleaseVO productVersionReleaseDTO = new ProductVersionReleaseVO()
        productVersionReleaseDTO.projectId = projectId
        productVersionReleaseDTO.releaseDate = new Date()
        productVersionReleaseDTO.versionId = result2.versionId

        when:
        HttpEntity<ProductVersionReleaseVO> httpEntity = new HttpEntity<>(productVersionReleaseDTO)
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

    def 'revokeReleaseVersion'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/revoke_release",
                HttpMethod.POST,
                new HttpEntity<>(),
                ProductVersionDetailVO.class,
                projectId,
                result.versionId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.statusCode == "version_planning"
    }

    def 'revokeReleaseVersion unSuccess'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/revoke_release",
                HttpMethod.POST,
                new HttpEntity<>(),
                String.class,
                projectId,
                result.versionId)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.productVersion.revokeRelease"
    }

    def 'archivedVersion'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/archived",
                HttpMethod.POST,
                new HttpEntity<>(),
                ProductVersionDetailVO.class,
                projectId,
                result.versionId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.statusCode == "archived"
    }

    def 'archivedVersion unSuccess'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/archived",
                HttpMethod.POST,
                new HttpEntity<>(),
                String.class,
                projectId,
                result.versionId)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.productVersion.archived"
    }

    def 'revokeArchivedVersion'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/revoke_archived",
                HttpMethod.POST,
                new HttpEntity<>(),
                ProductVersionDetailVO.class,
                projectId,
                result.versionId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.statusCode == "version_planning"
    }

    def 'mergeVersion'() {
        given:
        ProductVersionMergeVO productVersionMergeDTO = new ProductVersionMergeVO()
        List<Long> sourceVersionIds = new ArrayList<>()
        sourceVersionIds.add(result2.versionId)
        productVersionMergeDTO.sourceVersionIds = sourceVersionIds
        productVersionMergeDTO.targetVersionId = result3.versionId

        when:
        HttpEntity<ProductVersionMergeVO> productVersionMergeDTOHttpEntity = new HttpEntity<>(productVersionMergeDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/merge",
                HttpMethod.POST,
                productVersionMergeDTOHttpEntity,
                Boolean.class,
                projectId)

        and:
        // 设置查询versionIssueRel条件
        VersionIssueRelDTO search = new VersionIssueRelDTO()
        search.versionId = result2.versionId
        List<VersionIssueRelDTO> relsOrigin = versionIssueRelMapper.select(search)
        search.versionId = result3.versionId
        List<VersionIssueRelDTO> relsNew = versionIssueRelMapper.select(search)


        then:
        entity.statusCode.is2xxSuccessful()
        relsOrigin.size() == 0
        relsNew.size() == 1
        relsNew.get(0).issueId == 2L

    }

    def 'listByOptions'() {
        given:
        Map<String, Object> searchParamMap = new HashMap<>()
        Map<String, Object> map = new HashMap<>()
        map.put("name", result.name)
        searchParamMap.put("searchArgs", map)
        searchParamMap.put("advancedSearchArgs", map)
        searchParamMap.put("content", "")
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(searchParamMap)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/versions?page=0&size=10",
                HttpMethod.POST,
                httpEntity,
                PageInfo,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<ProductVersionPageVO> list = entity.body.getList()

        expect: "设置期望值"
        list.size() > 0
    }

    def 'queryVersionStatisticsByVersionId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}",
                HttpMethod.GET,
                null,
                ProductVersionStatisticsVO.class,
                projectId,
                versionId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        ProductVersionStatisticsVO productVersionStatisticsDTO = entity.body

        expect: "设置期望值"
        productVersionStatisticsDTO.projectId == projectId
        productVersionStatisticsDTO.statusCode == statusCode

        where: '比较期望值'
        versionId        | statusCode
        1L               | "version_planning"
        result.versionId | "version_planning"
    }

    def 'queryReleaseMessageByVersionId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/plan_names",
                HttpMethod.GET,
                null,
                VersionMessageVO.class,
                projectId,
                1)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        VersionMessageVO versionMessageDTO = entity.body

        expect: "设置期望值"
        versionMessageDTO.fixIssueCount > 0
        versionMessageDTO.versionNames.size() > 0

    }

    def 'queryDeleteMessageByVersionId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/names",
                HttpMethod.GET,
                null,
                VersionMessageVO.class,
                projectId,
                1)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        VersionMessageVO VersionMessageDTO = entity.body

        expect: "设置期望值"
        VersionMessageDTO.agileIssueCount > 0
        VersionMessageDTO.versionNames.size() > 0

    }

    def 'queryNameByOptions'() {
        given:
        List<String> list = new ArrayList<>()
        list.add("version_planning")
        HttpEntity<List<String>> requestEntity = new HttpEntity<>(list, null)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/names",
                HttpMethod.POST,
                requestEntity,
                List,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<ProductVersionNameVO> listresult = entity.body

        expect: "设置期望值"
        listresult.size() == expectCount
        listresult.get(0).statusCode == statusCode

        where: '对比结果'
        versionId         || expectCount | statusCode
        result.versionId  || 3           | "version_planning"
        result2.versionId || 3           | "version_planning"
    }

    def 'listByProjectId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/versions",
                HttpMethod.GET,
                null,
                List,
                projectIds)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<ProductVersionVO> result = entity.body

        expect: "设置期望值"
        result.size() == expectCount
        try {
            result.get(0).statusCode == statusCode
        } catch (Exception e) {
            statusCode = Exception
        }


        where: '对比结果'
        projectIds || expectCount | statusCode
        1L         || 3           | "version_planning"
        2L         || 0           | Exception
    }

    def 'listIds'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/ids",
                HttpMethod.GET,
                null,
                List,
                projectIds)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<Long> result = entity.body

        expect: "设置期望值"
        result.size() == expectCount

        where: '对比结果'
        projectIds || expectCount
        1L         || 3
        2L         || 3
    }

    def 'dragVersion'() {
        given:
        VersionSequenceVO versionSequenceDTO = new VersionSequenceVO()
        versionSequenceDTO.versionId = result.versionId
        versionSequenceDTO.afterSequence = 1
        HttpEntity<VersionSequenceVO> requestEntity = new HttpEntity<>(versionSequenceDTO, null)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/drag",
                HttpMethod.PUT,
                requestEntity,
                ProductVersionPageVO,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        ProductVersionPageVO productVersionPageDTO = entity.body

        expect: "设置期望值"
        productVersionPageDTO.statusCode == "version_planning"
        productVersionPageDTO.sequence == 2
    }

    def 'queryByCategoryCode'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/issue_count",
                HttpMethod.GET,
                null,
                VersionIssueCountVO,
                projectId,
                1)

        then:
        entity.statusCode.is2xxSuccessful()

    }

    def 'queryByVersionIdAndStatusCode'() {
        given:
        SearchVO searchDTO = new SearchVO()
        HttpEntity<SearchVO> requestEntity = new HttpEntity<>(searchDTO)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/{versionId}/issues?organizationId={organizationId}&statusCode={statusCode}",
                HttpMethod.POST,
                requestEntity,
                List,
                projectId,
                versionId,
                1L,
                "todo")

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<IssueListVO> list = entity.body

        expect: "设置期望值"
        list.size() == expectCount

        where: '对比结果'
        versionId | statusCode || expectCount
        1L        | 'todo'     || 1
    }

    def 'queryProjectIdByVersionId'() {

        when: '根据versionId查询projectId,测试项目修数据用，其它勿调用'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/product_version/{versionId}/project_id", Long, projectId, result3.versionId)

        then:
        entity.statusCode.is2xxSuccessful()

        expect: "设置期望值"
        entity.body == projectId

    }

    def 'deleteVersion'() {
        given:
        ProductVersionDTO productVersionDO = new ProductVersionDTO()
        productVersionDO.name = versionMergeName
        productVersionDO.projectId = projectId
        VersionIssueRelDTO versionIssueRelDO = new VersionIssueRelDTO()
        versionIssueRelDO.projectId = projectId
        versionIssueRelDO.versionId = result.versionId

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/product_version/delete/{versionId}",
                HttpMethod.DELETE,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                result3.getVersionId())
        versionIssueRelMapper.delete(versionIssueRelDO)

        then:
        entity.statusCode.is2xxSuccessful()
        ProductVersionDTO deleteDO = productVersionMapper.selectOne(productVersionDO)
        deleteDO == null
        versionIssueRelMapper.selectAll().size() == 1
    }
}
