package com.scmt;

import com.scmt.core.common.redis.RedisTemplateHelper;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Exrickx
 */
@EnableJpaAuditing
// 启用缓存
@EnableCaching
// 启用异步
@EnableAsync
// 启用自带定时任务
@EnableScheduling
// 启用Admin监控
@EnableAdminServer
// Activiti5.22需要排除
@SpringBootApplication
@RestController
public class ScmtApplication {
    @Autowired
    private RedisTemplateHelper redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ScmtApplication.class, args);
    }


    @Bean
    public Connector httpConnector(){
        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8888);
        connector.setSecure(false);
        connector.setRedirectPort(8888);
        return connector;
    }



    @Bean
    public void deleteRedis() {
        redisTemplate.deleteByPattern("user:" + "*");
        redisTemplate.deleteByPattern("permission::userMenuList:*");
        redisTemplate.deleteByPattern("userRole:" + "*");
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setProperty("relaxedQueryChars", "|{}[]");
            }
        });
        return factory;
    }


    /**
     * 访问首页提示
     *
     * @return /
     */
    @GetMapping("/")
    public String index() {
        return "service started successfully";
    }
}
