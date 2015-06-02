/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.queryrouter.security;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.redis.RedisAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.cache.util.ApplicationContextHelper;

/**
 * 
 * 平台服务入口 提供脱离tomcat容器提供queryservice的能力
 * 
 * @author 罗文磊
 * @version 1.0.0.1
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
        RedisAutoConfiguration.class })
@ImportResource({ "conf/applicationContext-cache.xml", "applicationContext-queryrouter.xml" })
public class QueryRouterApplication4Enterprise extends SpringBootServletInitializer {
    
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(QueryRouterApplication4Enterprise.class);
    
    /*
     * 设置gzip压缩
     */
    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer servletContainer) {
                ((TomcatEmbeddedServletContainerFactory) servletContainer)
                        .addConnectorCustomizers(new TomcatConnectorCustomizer() {
                            @Override
                            public void customize(Connector connector) {
                                AbstractHttp11Protocol<?> httpProtocol = (AbstractHttp11Protocol<?>) connector
                                        .getProtocolHandler();
                                httpProtocol.setCompression("on");
                                httpProtocol.setCompressionMinSize(256);
                                String mimeTypes = httpProtocol.getCompressableMimeTypes();
                                String mimeTypesWithJson = mimeTypes + ","
                                        + MediaType.APPLICATION_JSON_VALUE;
                                httpProtocol.setCompressableMimeTypes(mimeTypesWithJson);
                            }
                        });
            }
        };
    }
    
    /**
     *
     * 注册全局权限认证服务过滤器
     * 
     * @return FilterRegistrationBean
     *
     */
    @Bean
    public FilterRegistrationBean securityFilterRegistrationBean() {
        SecurityFilter contextFilter = new SecurityFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(contextFilter);
        String urlPattern = "/queryrouter/*";
        registrationBean.addUrlPatterns(urlPattern);
        registrationBean.setOrder(2);
        return registrationBean;
    }
    
    /**
     * 程序入口
     * 
     * @param args
     *            外部参数
     */
    public static void main(String[] args) {
        initAuthStrategyRepositry();
        ConfigurableApplicationContext context = SpringApplication.run(
                QueryRouterApplication4Enterprise.class, args);
        ApplicationContextHelper.setContext(context);
    }
    
    /**
     * 初始化授权策略策略库
     */
    private static void initAuthStrategyRepositry() {
        InputStream input = null;
        try {
            String configFile = System.getProperty("biplatform.auth.server");
            if (StringUtils.isEmpty(configFile)) {
                logger.error("please set -Dbiplatform.auth.server=XXX for jvm params");
                logger.error("can not provider auth server file, application will exit");
                System.exit(-1);
            }
            input = new FileInputStream(configFile);
            Properties properties = new Properties();
            properties.load(input);
            properties.stringPropertyNames().forEach(key -> {
                String strategy = properties.getProperty(key);
                try {
                    JSONObject json = new JSONObject(strategy);
                    SecurityFilter.REPOSITORY.put(key, json);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    System.exit(-1);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(-1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(QueryRouterApplication4Enterprise.class);
    }
    
}
