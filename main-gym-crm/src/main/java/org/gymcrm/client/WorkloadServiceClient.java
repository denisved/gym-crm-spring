package org.gymcrm.client;

import org.gymcrm.config.FeignClientConfig; 
import org.gymcrm.dto.WorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "trainer-workload-service", configuration = FeignClientConfig.class)
public interface WorkloadServiceClient {

    @PostMapping("/api/v1/trainer-workload")
    void updateWorkload(@RequestBody WorkloadRequest request);

}