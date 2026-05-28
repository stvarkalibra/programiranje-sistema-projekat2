package org.raflab.avanturaservice.controllerLayer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
public class InstanceController {

    @Value("${spring.application.name:avantura-service}")
    private String serviceName;

    @Value("${instance.id:${HOSTNAME:avantura-local}}")
    private String instanceId;

    @GetMapping("/api/adventures/instance")
    public Map<String, String> getInstanceInfo() throws Exception {
        return Map.of(
                "service", serviceName,
                "instanceId", instanceId,
                "host", InetAddress.getLocalHost().getHostName()
        );
    }
}