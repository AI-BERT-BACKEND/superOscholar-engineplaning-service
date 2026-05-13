package com.aibert.dosw.domain.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Niveles de prioridad para una tarea académica según el requerimiento R14.
 *
 * <ul>
 *   <li>ALTA     — score ≥ 70 (incluye tareas con deadline &lt;24h escaladas automáticamente)</li>
 *   <li>MEDIA    — score 40–69</li>
 *   <li>BAJA     — score &lt; 40</li>
 *   <li>CRITICA  — alias interno para deadline &lt;24h; se proyecta como ALTA al cliente</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum TaskPriority {
    /** Deadline en menos de 24h — se muestra como ALTA al cliente con score máximo. */
    CRITICA(70.0, "ALTA"),
    /** Score ≥ 70. */
    ALTA(70.0, "ALTA"),
    /** Score 40–69. */
    MEDIA(40.0, "MEDIA"),
    /** Score < 40. */
    BAJA(0.0, "BAJA");

    private final double minimumScore;
    /** Etiqueta que se expone en la respuesta de la API. */
    private final String label;
}
