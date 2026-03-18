package ru.dgorokhov;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.dgorokhov.config.AppConfig;
import ru.dgorokhov.config.ServerProperties;

@Slf4j
public class Main {

    public static void main(String[] args) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        Tomcat tomcat = new Tomcat();
        Context tomcatContext = tomcat.addContext("", null);
        Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet);
        tomcatContext.addServletMappingDecoded("/", "dispatcher");

        int port = ServerProperties.getPortFromYamlConfig("application.yaml");
        tomcat.getConnector().setPort(port);

        try {
            tomcat.start();
            log.info("Tomcat server started on port {}", port);
        } catch (LifecycleException e) {
            log.error("Failed to start Tomcat server on port {}", port);
            throw new RuntimeException(e);
        }
    }

}
