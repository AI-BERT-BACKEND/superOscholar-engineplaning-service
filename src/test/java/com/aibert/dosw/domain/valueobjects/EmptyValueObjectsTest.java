package com.aibert.dosw.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmptyValueObjectsTest {

    @Test
    void shouldInstantiateEmptyValueObjects() {
        assertNotNull(new UserId());
        assertNotNull(AcademicWeight.of("test", 0.5));
    }
}
