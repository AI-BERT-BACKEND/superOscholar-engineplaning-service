package com.aibert.dosw.infrastructure.external.feign.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceClientContractTest {

    @Test
    void shouldDeclareExpectedFeignClientMetadata() {
        FeignClient feignClient = TaskServiceClient.class.getAnnotation(FeignClient.class);
        assertNotNull(feignClient);
        assertEquals("task-service", feignClient.name());
        assertEquals("${feign.task-service.url}", feignClient.url());
    }

    @Test
    void shouldMatchTaskEndpointsContract() throws NoSuchMethodException {
        Method getByStudent = TaskServiceClient.class.getMethod("getTasksByStudent", String.class);
        Method patchTask = TaskServiceClient.class.getMethod(
                "patchTask",
                String.class,
                com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest.class);
        Method patchStatus = TaskServiceClient.class.getMethod(
                "patchTaskStatus",
                String.class,
                com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest.class);
        Method getById = TaskServiceClient.class.getMethod("getTaskById", String.class);

        assertHasPath(getByStudent.getAnnotation(GetMapping.class).value(), "/api/tasks/student/{studentId}");
        assertHasPath(patchTask.getAnnotation(PatchMapping.class).value(), "/api/tasks/{taskId}");
        assertHasPath(patchStatus.getAnnotation(PatchMapping.class).value(), "/api/tasks/{taskId}/status");
        assertHasPath(getById.getAnnotation(GetMapping.class).value(), "/api/tasks/{taskId}");
    }

    private void assertHasPath(String[] values, String expectedPath) {
        assertTrue(Arrays.asList(values).contains(expectedPath),
                "Expected path not found: " + expectedPath);
    }
}
