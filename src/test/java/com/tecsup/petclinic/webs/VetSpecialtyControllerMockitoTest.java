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
}
