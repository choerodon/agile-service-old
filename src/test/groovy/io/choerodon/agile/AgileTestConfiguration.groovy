package io.choerodon.agile

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.app.service.ProductVersionService
import io.choerodon.agile.app.service.impl.IssueServiceImpl
import io.choerodon.agile.app.service.impl.ProductVersionServiceImpl
import io.choerodon.agile.domain.agile.event.ProjectEvent
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.*
import io.choerodon.agile.infra.mapper.*
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.convertor.ApplicationContextHelper
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.event.producer.execute.EventProducerTemplate
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/1
 */
@TestConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(LiquibaseConfig)
class AgileTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ApplicationContextHelper applicationContextHelper;

    @Value('${choerodon.oauth.jwt.key:choerodon}')
    String key

    @Value('${spring.datasource.url}')
    String dataBaseUrl

    @Value('${spring.datasource.username}')
    String dataBaseUsername

    @Value('${spring.datasource.password}')
    String dataBasePassword

    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    LiquibaseExecutor liquibaseExecutor

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    ProjectInfoMapper projectInfoMapper

    @Autowired
    private IssueMapper issueMapper

    @Autowired
    private AuthenticationManager authenticationManager

    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper

    @Autowired
    private SprintMapper sprintMapper

    @Autowired
    private IssueComponentMapper issueComponentMapper

    @Autowired
    private IssueLabelMapper issueLabelMapper

    @Autowired
    private ProductVersionMapper productVersionMapper

    @Bean("mockUserRepository")
    @Primary
    UserRepository userRepository() {
        detachedMockFactory.Mock(UserRepository)
    }

    @Bean("issueService")
    @Primary
    IssueService issueService() {
        new IssueServiceImpl(detachedMockFactory.Mock(SagaClient))
    }

    @Bean("productVersionService")
    @Primary
    ProductVersionService productVersionService() {
        new ProductVersionServiceImpl(detachedMockFactory.Mock(SagaClient))
    }

    @Bean("mockEventProducerTemplate")
    @Primary
    EventProducerTemplate eventProducerTemplate() {
        detachedMockFactory.Mock(EventProducerTemplate)
    }

    final ObjectMapper objectMapper = new ObjectMapper()

    @Bean
    KafkaTemplate kafkaTemplate() {
        detachedMockFactory.Mock(KafkaTemplate)
    }

    @Bean
    @Qualifier("cacheManager")
    @Primary
    RedisCacheManager cacheManager() {
        detachedMockFactory.Mock(RedisCacheManager)
    }

    @PostConstruct
    void init() {
        //初始化表，有些初始化表Groovy在H2database中需要修改，所以拷贝了groovy脚本并修改，然后修改yml配置中的初始化脚本路径
        liquibaseExecutor.execute()
        initSqlFunction()
        setTestRestTemplateJWT()
        applicationContextHelper.setApplicationContext(applicationContext)
    }

    void initSqlFunction() {
        //连接H2数据库
        Class.forName("org.h2.Driver")
        Connection conn = DriverManager.
                getConnection(dataBaseUrl, dataBaseUsername, dataBasePassword)
        Statement stat = conn.createStatement()
        //创建 SQL的IF函数，用JAVA的方法代替函数
        stat.execute("CREATE ALIAS IF NOT EXISTS IF FOR \"io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil.ifFunction\"")
        stat.execute("CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR \"io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil.dataFormatFunction\"")
        stat.close()
        conn.close()
    }


    private void setTestRestTemplateJWT() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('JWT_Token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
        initProjectData()
        initUserDetail()
    }

    void initUserDetail() {
        //手动设置一个userDetail
        def authRequest = new UsernamePasswordAuthenticationToken("admin", "admin")
        def authentication = authenticationManager.authenticate(authRequest)
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    static String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        defaultUserDetails.setUserId(0L)
        defaultUserDetails.setOrganizationId(0L)
        defaultUserDetails.setLanguage('zh_CN')
        defaultUserDetails.setTimeZone('CCT')
        String jwtToken = null
        try {
            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return jwtToken
    }

    private void initProjectData() {
        initProject()
        initIssues()
        initSprint()
        initVersion()
        initComponent()
        initLabel()
    }

    private void initComponent() {
        IssueComponentDO issueComponentDO = new IssueComponentDO()
        issueComponentDO.projectId = 1L
        issueComponentDO.name = "测试模块"
        issueComponentDO.description = "测试模块描述"
        issueComponentDO.managerId = 1L
        issueComponentDO.defaultAssigneeRole = "模块负责人"
        issueComponentMapper.insert(issueComponentDO)
    }

    private void initLabel() {
        IssueLabelDO issueLabelDO = new IssueLabelDO()
        issueLabelDO.projectId = 1L
        issueLabelDO.labelName = "测试标签"
        issueLabelMapper.insert(issueLabelDO)
    }

    private void initProject() {
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.setProjectId(1L)
        projectEvent.setProjectCode("AGILE")
        projectEvent.setProjectName("agile")
        String data = JSON.toJSONString(projectEvent)
        agileEventHandler.handleProjectInitByConsumeSagaTask(data)
    }

    private void initIssues() {
        IssueDO epicIssue = new IssueDO()
        epicIssue.issueId = 1L
        epicIssue.issueNum = 1
        epicIssue.projectId = 1L
        epicIssue.priorityCode = 'high'
        epicIssue.reporterId = 1L
        epicIssue.statusId = 1L
        epicIssue.typeCode = 'issue_epic'
        epicIssue.summary = 'epic-test'
        issueMapper.insert(epicIssue)

        IssueDO story = new IssueDO()
        story.projectId = 1L
        story.typeCode = 'story'
        story.statusId = 1L
        story.reporterId = 1L
        story.priorityCode = 'high'
        story.issueNum = 2
        story.issueId = 2L
        story.summary = 'story-test'
        story.epicId = 1L
        story.storyPoints = 6
        //设置rank值
        story.rank = '0|c00000:'
        issueMapper.insert(story)

        ProjectInfoDO projectInfoDO = projectInfoMapper.selectByPrimaryKey(1L)
        projectInfoDO.setIssueMaxNum(2)
        projectInfoMapper.updateByPrimaryKeySelective(projectInfoDO)
    }

    private void initSprint() {
        SprintDO sprintDO = new SprintDO()
        sprintDO.sprintId = 1L
        sprintDO.projectId = 1L
        sprintDO.sprintName = 'sprint-test'
        sprintDO.statusCode = 'sprint_planning'
        sprintMapper.insert(sprintDO)
        IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO()
        issueSprintRelDO.sprintId = 1L
        issueSprintRelDO.issueId = 2L
        issueSprintRelDO.projectId = 1L
        issueSprintRelMapper.insert(issueSprintRelDO)
    }

    private void initVersion() {
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.projectId = 1L
        productVersionDO.name = "v1.0.0"
        productVersionDO.statusCode = 'version_planning'
        productVersionDO.versionId = 1L
        productVersionMapper.insert(productVersionDO)
        VersionIssueRelDO versionIssueRelDO = new VersionIssueRelDO()
        versionIssueRelDO.projectId = 1L
        versionIssueRelDO.issueId = 2L
        versionIssueRelDO.versionId = 1L
        versionIssueRelDO.relationType = "fix"
        versionIssueRelMapper.insert(versionIssueRelDO)
    }

}
