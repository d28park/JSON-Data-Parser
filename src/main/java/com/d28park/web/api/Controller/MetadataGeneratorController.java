package com.d28park.web.api.Controller;

import java.util.concurrent.atomic.AtomicLong;

import com.d28park.web.api.MetadataGenerator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataGeneratorController {

    private final AtomicLong counter = new AtomicLong();

    // curl -i http://localhost:8080/api/metadataGenerator
    @RequestMapping(
            value = "/api/metadataGenerator"
            // method = RequestMethod.POST,
            // produces = "application/json"
    )
    public MetadataGenerator metadataGenerator(
        @RequestParam(value = "name", defaultValue = "World") String name) {
            return new MetadataGenerator(counter.incrementAndGet(), name);
    }
}