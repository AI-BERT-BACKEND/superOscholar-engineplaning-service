package com.aibert.dosw.domain.model.context;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteRiskCalculatorTest {

    @Test
    void shouldCalculateRiskCorrectly() {
        NoteRiskCalculator.CutProgress c1 = new NoteRiskCalculator.CutProgress(5.0, 30.0);
        NoteRiskCalculator.CutProgress c2 = new NoteRiskCalculator.CutProgress(5.0, 30.0);
        NoteRiskCalculator.CutProgress c3 = new NoteRiskCalculator.CutProgress(0.0, 40.0);

        double required = NoteRiskCalculator.calculateRequiredGrade(List.of(c1, c2, c3));
        assertEquals(0.0, required); // Already passed with 3.0 accumulated

        assertEquals(NoteRiskCalculator.RiskLevel.LOW, NoteRiskCalculator.calculateRiskLevel(required));
        assertEquals(NoteRiskCalculator.RiskLevel.CRITICAL, NoteRiskCalculator.calculateRiskLevel(4.5));
        assertEquals(NoteRiskCalculator.RiskLevel.MODERATE, NoteRiskCalculator.calculateRiskLevel(3.5));
    }
}
