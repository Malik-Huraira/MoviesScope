package com.moviescope.controller;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/api/hazelcast")
public class HazelcastTestController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/test")
    public String testHazelcast() {
        ConcurrentMap<String, String> map = hazelcastInstance.getMap("test-map");
        map.put("test-key", "test-value-" + System.currentTimeMillis());
        return "Hazelcast is working! Stored value: " + map.get("test-key");
    }
    
    @GetMapping("/info")
    public Map<String, Object> getHazelcastInfo() {
        return Map.of(
            "instanceName", hazelcastInstance.getName(),
            "clusterSize", hazelcastInstance.getCluster().getMembers().size(),
            "isRunning", hazelcastInstance.getLifecycleService().isRunning()
        );
    }
}