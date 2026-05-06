package com.aibert.dosw.domain.model.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmptyContextClassesTest {

    @Test
    void shouldInstantiateEmptyContextClasses() {
        assertNotNull(new UserProfile());
        assertNotNull(new RebalanceResult());
    }
}
