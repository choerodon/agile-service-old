package io.choerodon.agile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 解决userDetail拿不到用户信息
 * @author dinghuang123@gmail.com
 * @since 2018/8/21
 */
@Configuration
class GlobalAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    private TestAuthenticationProvider testAuthenticationProvider

    @Override
    void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(testAuthenticationProvider)
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
