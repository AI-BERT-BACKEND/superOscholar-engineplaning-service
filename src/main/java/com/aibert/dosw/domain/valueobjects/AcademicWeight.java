package com.aibert.dosw.domain.valueobjects;

import lombok.Getter;

@Getter
public final class AcademicWeight {

    private final String subjectId;
    private final double value; // 0.0 - 1.0

    private AcademicWeight(String subjectId, double value) {
        this.subjectId = subjectId;
        this.value = Math.min(Math.max(value, 0.0), 1.0);
    }

    public static AcademicWeight of(String subjectId, double value) {
        return new AcademicWeight(subjectId, value);
    }
}
