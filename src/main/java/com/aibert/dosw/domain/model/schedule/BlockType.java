package com.aibert.dosw.domain.model.schedule;

/**
 * Clasificación semántica de un bloque de tiempo.
 *
 * <p>
 * AIB-25 RN-01: Solo los bloques ACADEMICO son elegibles para asignación de
 * tareas.
 * Los bloques PERSONAL, DESCANSO y SOCIAL son protegidos incondicionalmente.
 * </p>
 */
public enum BlockType {

    /**
     * Bloque de estudio académico. Único tipo elegible para asignación de tareas.
     */
    ACADEMICO,

    /**
     * Actividad personal (cita médica, trámite, etc.). Protegido — no recibe
     * tareas.
     */
    PERSONAL,

    /** Tiempo de descanso, sueño, ocio. Protegido — no recibe tareas. */
    DESCANSO,

    /**
     * Actividades sociales (reuniones familiares, amigos). Protegido — no recibe
     * tareas.
     */
    SOCIAL,

    /** Clase, taller o evento académico programado. */
    CLASE,

    /** Otro tipo de bloqueo genérico. */
    OTRO
}
