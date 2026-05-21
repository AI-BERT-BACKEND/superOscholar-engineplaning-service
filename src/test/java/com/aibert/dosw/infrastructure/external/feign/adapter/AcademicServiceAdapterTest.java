package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.infrastructure.external.feign.client.AcademicServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.AcademicApiResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.AcademicWeightResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicServiceAdapterTest {

    @Mock
    private AcademicServiceClient academicServiceClient;

    @InjectMocks
    private AcademicServiceAdapter adapter;

    @Test
    void shouldReturnAcademicWeight() {
        AcademicApiResponse response = new AcademicApiResponse("ok",
                AcademicWeightResponse.builder().academicWeight(0.8).build());
        when(academicServiceClient.getAcademicWeight("stu-1", "subj-1")).thenReturn(response);

        Optional<AcademicWeight> result = adapter.getAcademicWeight("stu-1", "subj-1");

        assertTrue(result.isPresent());
        assertEquals("subj-1", result.get().getSubjectId());
        assertEquals(0.8, result.get().getValue());
    }

    @Test
    void shouldReturnEmptyWhenSubjectIdIsNull() {
        Optional<AcademicWeight> result = adapter.getAcademicWeight("stu-1", null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(academicServiceClient);
    }

    @Test
    void shouldReturnEmptyWhenSubjectIdIsBlank() {
        Optional<AcademicWeight> result = adapter.getAcademicWeight("stu-1", "   ");

        assertTrue(result.isEmpty());
        verifyNoInteractions(academicServiceClient);
    }

    @Test
    void shouldReturnEmptyWhenClientReturnsNull() {
        when(academicServiceClient.getAcademicWeight("stu-1", "subj-2")).thenReturn(null);

        Optional<AcademicWeight> result = adapter.getAcademicWeight("stu-1", "subj-2");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenDataIsNull() {
        when(academicServiceClient.getAcademicWeight("stu-1", "subj-3"))
                .thenReturn(new AcademicApiResponse("ok", null));

        Optional<AcademicWeight> result = adapter.getAcademicWeight("stu-1", "subj-3");

        assertTrue(result.isEmpty());
    }
}
