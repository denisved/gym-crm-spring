package org.gymcrm.workload.bdd.integration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.gymcrm.workload.WorkloadApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = WorkloadApplication.class, properties = {
        "eureka.client.enabled=false" 
})
public class IntegrationConfig {

}