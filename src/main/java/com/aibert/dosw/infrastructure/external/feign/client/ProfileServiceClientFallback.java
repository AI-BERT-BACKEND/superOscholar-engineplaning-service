package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback para ProfileServiceClient.
 * Se activa cuando profile-service no está disponible.
 * Devuelve horario vacío para evitar NullPointerException en los casos de uso.
 */
@Component
@Slf4j
public class ProfileServiceClientFallback implements ProfileServiceClient {

    @Override
    public List<DailySchedule> getWeeklySchedule(String studentId) {
        log.warn("profile-service no disponible al obtener horario semanal del estudiante '{}'.", sl(studentId));
        return Collections.emptyList();
    }

    @Override
    public List<UnavailableBlock> getUnavailableBlocks(String studentId) {
        log.warn("profile-service no disponible al obtener bloques no disponibles del estudiante '{}'.", sl(studentId));
        return Collections.emptyList();
    }

    @Override
    public int getDailyMaxMinutes(String studentId) {
        log.warn("profile-service no disponible al obtener límite diario del estudiante '{}'. Usando 240 min.",
                sl(studentId));
        return 240;
    }

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}
