package com.aibert.dosw.infrastructure.external.feign.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcademicServiceClientContractTest {

    @Test
    void shouldDeclareExpectedFeignClientMetadata() {
        FeignClient feignClient = AcademicServiceClient.class.getAnnotation(FeignClient.class);
        assertNotNull(feignClient);
        assertEquals("academic-service", feignClient.name());
        assertEquals("${feign.academic-service.url}", feignClient.url());
    }

    @Test
    void shouldMatchAcademicWeightEndpointContract() throws NoSuchMethodException {
        Method method = AcademicServiceClient.class.getMethod("getAcademicWeight", String.class, String.class);
        GetMapping mapping = method.getAnnotation(GetMapping.class);

        assertNotNull(mapping);
        assertTrue(Arrays.asList(mapping.value()).contains("/api/v1/academic/weight"));
    }
}
