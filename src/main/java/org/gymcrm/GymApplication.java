package org.gymcrm;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.gymcrm.config.AppConfig;
import org.gymcrm.config.WebConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

@Slf4j
public class GymApplication {

    public static void main(String[] args) {
        log.info("Starting pure Spring Web application with Embedded Tomcat...");

        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            String docBase = new File(".").getAbsolutePath();
            Context context = tomcat.addContext("", docBase);

            AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
            springContext.register(AppConfig.class, WebConfig.class);
            springContext.setServletContext(context.getServletContext());

            DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);
            Tomcat.addServlet(context, "dispatcher", dispatcherServlet).setLoadOnStartup(1);
            context.addServletMappingDecoded("/", "dispatcher");

            tomcat.start();
            log.info("Embedded Tomcat server started successfully on http://localhost:8080");

            tomcat.getServer().await();

        } catch (Exception e) {
            log.error("Critical error during application startup", e);
            System.exit(1);
        }
    }
}