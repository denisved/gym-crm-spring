package org.gymcrm.workload.bdd.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.gymcrm.workload.WorkloadApplication;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CucumberContextConfiguration
@SpringBootTest(classes = WorkloadApplication.class, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
public class ComponentConfig {

    @MockBean
    private TrainerWorkloadRepository trainerWorkloadRepository;

}