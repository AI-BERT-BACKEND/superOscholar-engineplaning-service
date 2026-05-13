package com.aibert.dosw.domain.model.schedule;

/**
 * Clasificación semántica de un bloque de tiempo no disponible.
 *
 * <p>Per R16 RN-04 y R17 RN-03: los bloques PERSONAL, DESCANSO y SOCIAL
 * nunca pueden recibir tareas académicas, incluso si hay tareas sin asignar.</p>
 */
public enum BlockType {

    /** Actividad personal (cita médica, trámite, etc.). */
    PERSONAL,

    /** Tiempo de descanso, sueño, ocio. */
    DESCANSO,

    /** Actividades sociales (reuniones familiares, amigos). */
    SOCIAL,

    /** Clase, taller o evento académico programado. */
    CLASE,

    /** Otro tipo de bloqueo genérico. Las tareas SÍ pueden redistribuirse en bloques OTRO si hay capacidad. */
    OTRO
}
