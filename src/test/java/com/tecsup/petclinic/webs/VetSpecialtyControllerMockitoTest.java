package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.DuplicateVetSpecialtyException;
import com.tecsup.petclinic.services.VetSpecialtyService;
import com.tecsup.petclinic.util.VetSpecialtyObjectCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Ángel, A+B: contrato web con servicio simulado; no escribe en H2. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class VetSpecialtyControllerMockitoTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private VetSpecialtyService service;

    @Test
    void testAssignSpecialtyToVet() throws Exception {
        when(service.assignSpecialtyToVet(1, 1)).thenReturn(VetSpecialtyObjectCreator.assignment(1, 1));
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON)
                .content("{\"vetId\":1,\"specialtyId\":1}"))
                .andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(1)).andExpect(jsonPath("$.specialtyId").value(1))
                .andExpect(jsonPath("$.yearsExperience").value(0)).andExpect(jsonPath("$.primarySpecialty").value(false));
        verify(service).assignSpecialtyToVet(1, 1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testRemoveSpecialtyFromVet() throws Exception {
        doNothing().when(service).removeSpecialtyFromVet(2, 1);
        mvc.perform(delete("/vet-specialties/2/1")).andExpect(status().isOk());
        verify(service).removeSpecialtyFromVet(2, 1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testAssignDuplicate_ShouldFail() throws Exception {
        when(service.assignSpecialtyToVet(1, 1))
                .thenReturn(VetSpecialtyObjectCreator.assignment(1, 1))
                .thenThrow(new DuplicateVetSpecialtyException(new VetSpecialtyId(1, 1)));
        String body = "{\"vetId\":1,\"specialtyId\":1}";
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("ya esta asignada")));
        verify(service, times(2)).assignSpecialtyToVet(1, 1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindSpecialtiesByVet() throws Exception {
        when(service.findSpecialtiesByVet(3)).thenReturn(List.of(
                VetSpecialtyObjectCreator.specialty(2, "surgery"), VetSpecialtyObjectCreator.specialty(3, "dentistry")));
        mvc.perform(get("/vets/3/specialties")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 3)))
                .andExpect(jsonPath("$[*].name", contains("surgery", "dentistry")));
        verify(service).findSpecialtiesByVet(3);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindVetsBySpecialty() throws Exception {
        when(service.findVetsBySpecialty(1)).thenReturn(List.of(
                VetSpecialtyObjectCreator.vet(2, "Helen", "Leary"), VetSpecialtyObjectCreator.vet(5, "Henry", "Stevens")));
        mvc.perform(get("/specialties/1/vets")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 5)))
                .andExpect(jsonPath("$[*].lastName", contains("Leary", "Stevens")));
        verify(service).findVetsBySpecialty(1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindSpecialtiesByVet_Empty() throws Exception {
        when(service.findSpecialtiesByVet(1)).thenReturn(List.of());
        mvc.perform(get("/vets/1/specialties")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        verify(service).findSpecialtiesByVet(1);
        verifyNoMoreInteractions(service);
    }


    // Diego C+D: contrato HTTP y delegacion; la persistencia se prueba en la otra clase.
    @Test
    void testSetPrimarySpecialty() throws Exception {
        when(service.setPrimarySpecialty(3, 3)).thenReturn(VetSpecialtyObjectCreator.assignment(3, 3, true, 5, null));
        mvc.perform(put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(3)).andExpect(jsonPath("$.specialtyId").value(3))
                .andExpect(jsonPath("$.primarySpecialty").value(true));
        verify(service).setPrimarySpecialty(3, 3);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {
        // Con un mock solo se valida el contrato y la delegacion de ambos cambios.
        when(service.setPrimarySpecialty(3, 2)).thenReturn(VetSpecialtyObjectCreator.assignment(3, 2, true, 11, null));
        when(service.setPrimarySpecialty(3, 3)).thenReturn(VetSpecialtyObjectCreator.assignment(3, 3, true, 5, null));
        mvc.perform(put("/vet-specialties/3/2/primary")).andExpect(status().isOk())
                .andExpect(jsonPath("$.specialtyId").value(2)).andExpect(jsonPath("$.primarySpecialty").value(true));
        mvc.perform(put("/vet-specialties/3/3/primary")).andExpect(status().isOk())
                .andExpect(jsonPath("$.specialtyId").value(3)).andExpect(jsonPath("$.primarySpecialty").value(true));
        org.mockito.InOrder order = inOrder(service);
        order.verify(service).setPrimarySpecialty(3, 2);
        order.verify(service).setPrimarySpecialty(3, 3);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testSetPrimarySpecialty_NotAssigned() throws Exception {
        when(service.setPrimarySpecialty(3, 1)).thenThrow(
                new com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException(new VetSpecialtyId(3, 1)));
        mvc.perform(put("/vet-specialties/3/1/primary")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", not(emptyString())));
        verify(service).setPrimarySpecialty(3, 1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindVetsByMinExperience() throws Exception {
        when(service.findVetsByMinExperience(1, 7)).thenReturn(List.of(
                VetSpecialtyObjectCreator.vet(2, "Helen", "Leary"), VetSpecialtyObjectCreator.vet(5, "Henry", "Stevens")));
        mvc.perform(get("/specialties/1/vets").param("minExperience", "7"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 5)));
        verify(service).findVetsByMinExperience(1, 7);
        verify(service, never()).findVetsBySpecialty(anyInt());
        verifyNoMoreInteractions(service);
    }

    @Test
    void testUpdateYearsExperience() throws Exception {
        when(service.updateYearsExperience(2, 1, 9)).thenReturn(VetSpecialtyObjectCreator.assignment(
                2, 1, true, 9, java.time.LocalDate.of(2015, 6, 15)));
        mvc.perform(put("/vet-specialties/2/1/experience").contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearsExperience\":9}"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(2)).andExpect(jsonPath("$.specialtyId").value(1))
                .andExpect(jsonPath("$.yearsExperience").value(9)).andExpect(jsonPath("$.certificationDate").value("2015-06-15"));
        verify(service).updateYearsExperience(2, 1, 9);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testUpdateCertificationDate() throws Exception {
        java.time.LocalDate date = java.time.LocalDate.of(2024, 2, 29);
        when(service.updateCertificationDate(2, 1, date)).thenReturn(VetSpecialtyObjectCreator.assignment(2, 1, true, 8, date));
        mvc.perform(put("/vet-specialties/2/1/certification").contentType(MediaType.APPLICATION_JSON)
                .content("{\"certificationDate\":\"2024-02-29\"}"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.certificationDate").value("2024-02-29"))
                .andExpect(jsonPath("$.yearsExperience").value(8));
        verify(service).updateCertificationDate(2, 1, date);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testUpdateYearsExperience_Invalid() throws Exception {
        mvc.perform(put("/vet-specialties/2/1/experience").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
        when(service.updateYearsExperience(2, 1, -1)).thenThrow(new IllegalArgumentException("Experiencia negativa"));
        mvc.perform(put("/vet-specialties/2/1/experience").contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearsExperience\":-1}")).andExpect(status().isBadRequest());
        verify(service).updateYearsExperience(2, 1, -1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testUpdateCertificationDate_Invalid() throws Exception {
        when(service.updateCertificationDate(2, 1, null)).thenThrow(new IllegalArgumentException("Fecha requerida"));
        mvc.perform(put("/vet-specialties/2/1/certification").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verify(service).updateCertificationDate(2, 1, null);
        mvc.perform(put("/vet-specialties/2/1/certification").contentType(MediaType.APPLICATION_JSON)
                .content("{\"certificationDate\":\"fecha-invalida\"}")).andExpect(status().isBadRequest());
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindVetsByMinExperience_Invalid() throws Exception {
        when(service.findVetsByMinExperience(1, -1)).thenThrow(new IllegalArgumentException("Experiencia negativa"));
        mvc.perform(get("/specialties/1/vets").param("minExperience", "-1")).andExpect(status().isBadRequest());
        verify(service).findVetsByMinExperience(1, -1);
        mvc.perform(get("/specialties/1/vets").param("minExperience", "texto")).andExpect(status().isBadRequest());
        verifyNoMoreInteractions(service);
    }
}
