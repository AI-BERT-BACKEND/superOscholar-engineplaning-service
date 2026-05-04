package com.aibert.dosw.domain.model.balance;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DifferentialBalanceTest {

    @Test
    void shouldComputeOverloadedStatus() {
        DifferentialBalance balance = DifferentialBalance.of(LocalDate.now(), 5.0, 4.5); // 90%
        assertTrue(balance.isOverloaded());
        assertFalse(balance.hasFreeTime());
    }

    @Test
    void shouldComputeFreeStatus() {
        DifferentialBalance balance = DifferentialBalance.of(LocalDate.now(), 5.0, 0.5); // 10%
        assertFalse(balance.isOverloaded());
        assertTrue(balance.hasFreeTime());
    }
    
    @Test
    void shouldReduceAvailability() {
        DifferentialBalance balance = DifferentialBalance.of(LocalDate.now(), 5.0, 2.0);
        DifferentialBalance updated = balance.reduceAvailability(1.0);
        assertEquals(3.0, updated.getScheduledHours());
    }
}
