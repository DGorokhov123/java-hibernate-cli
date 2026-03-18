package ru.dgorokhov.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class ServerProperties {

    private static final int DEFAULT_SERVER_PORT = 9090;

    public static int getPortFromYamlConfig(String configFileName) {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new ClassPathResource(configFileName));
            factory.afterPropertiesSet();
            String portProperty = factory.getObject().getProperty("server.port");
            int port = Integer.parseInt(portProperty);
            log.info("Tomcat server port {} is loaded successfully from {}", port, configFileName);
            return port;
        } catch (Exception e) {
            log.warn("Failed loading Tomcat server port from {}, using default value {}", configFileName, DEFAULT_SERVER_PORT);
            return DEFAULT_SERVER_PORT;
        }
    }

}
