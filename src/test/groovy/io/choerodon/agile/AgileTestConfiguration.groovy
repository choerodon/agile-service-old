package io.choerodon.agile

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.app.service.impl.IssueServiceImpl
import io.choerodon.agile.domain.agile.event.ProjectEvent
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.event.producer.execute.EventProducerTemplate
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import org.springframework.test.context.ActiveProfiles
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct
import java.sql.*

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

    @PostConstruct
    void init() {
        //初始化表，有些初始化表Groovy在H2database中需要修改，所以拷贝了groovy脚本并修改，然后修改yml配置中的初始化脚本路径
        liquibaseExecutor.execute()
        setTestRestTemplateJWT()
        // 创建数据库函数（用于解决项目Mysql的函数在H2数据库中不存在）
        initSqlFunction()
        //todo 初始化项目，有待解决，目前是在每个测试方法执行前执行一次（可以通过SQL执行初始化项目）
//        initProject()
        //线程休眠后，可以通过http://ip:port/h2-console管理内存数据库，端口号可以根据日志查看
//        Thread.sleep(6000000000)
    }

    void initSqlFunction() {
        //连接H2数据库
        Class.forName("org.h2.Driver")
        Connection conn = DriverManager.
                getConnection(dataBaseUrl, dataBaseUsername, dataBasePassword)
        Statement stat = conn.createStatement()
        //创建 SQL的IF函数，用JAVA的方法代替函数
        stat.execute("CREATE ALIAS IF NOT EXISTS IF FOR \"io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil.ifFunction\"")
        stat.close()
        conn.close()
    }

    void initProject() {
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.setProjectId(1L)
        projectEvent.setProjectCode("AG")
        String data = JSON.toJSONString(projectEvent)
        agileEventHandler.handleProjectInitByConsumeSagaTask(data)
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


}
