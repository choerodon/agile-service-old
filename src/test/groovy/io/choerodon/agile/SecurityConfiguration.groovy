package io.choerodon.agile;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 解决开启h2控制台登录后的跨域问题
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/21
 */
@Configuration
@Order(1)
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().disable()
    }
}