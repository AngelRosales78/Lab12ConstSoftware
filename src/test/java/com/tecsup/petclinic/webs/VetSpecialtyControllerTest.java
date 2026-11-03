package com.tecsup.petclinic.webs;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Ángel, A+B: HTTP simulado, controlador/servicio/repositorio/H2 reales y rollback por prueba. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class VetSpecialtyControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private EntityManager em;
    @Autowired private JdbcTemplate jdbc;

    private void flushAndClear() { em.flush(); em.clear(); }

    private int count(int vet, int specialty) {
        flushAndClear();
        return jdbc.queryForObject("select count(*) from vet_specialties where vet_id=? and specialty_id=?",
                Integer.class, vet, specialty);
    }

    @Test
    void testAssignSpecialtyToVet() throws Exception {
        assertEquals(0, count(1, 1));
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON)
                .content("{\"vetId\":1,\"specialtyId\":1}"))
                .andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(1)).andExpect(jsonPath("$.specialtyId").value(1))
                .andExpect(jsonPath("$.yearsExperience").value(0)).andExpect(jsonPath("$.primarySpecialty").value(false));
        assertEquals(1, count(1, 1));
        mvc.perform(get("/vets/1/specialties")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void testRemoveSpecialtyFromVet() throws Exception {
        assertEquals(1, count(2, 1));
        mvc.perform(delete("/vet-specialties/2/1")).andExpect(status().isOk());
        assertEquals(0, count(2, 1));
        assertEquals(1, count(5, 1));
        assertEquals(1, jdbc.queryForObject("select count(*) from vets where id=2", Integer.class));
        assertEquals(1, jdbc.queryForObject("select count(*) from specialties where id=1", Integer.class));
        mvc.perform(get("/vets/2/specialties")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAssignDuplicate_ShouldFail() throws Exception {
        String body = "{\"vetId\":1,\"specialtyId\":1}";
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        flushAndClear();
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("ya esta asignada")));
        assertEquals(1, count(1, 1));
        // Un duplicado de una fila semilla tampoco sobrescribe sus metadatos.
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON)
                .content("{\"vetId\":2,\"specialtyId\":1}"))
                .andExpect(status().isConflict());
        flushAndClear();
        assertEquals(8, jdbc.queryForObject("select years_experience from vet_specialties where vet_id=2 and specialty_id=1", Integer.class));
        assertTrue(jdbc.queryForObject("select is_primary from vet_specialties where vet_id=2 and specialty_id=1", Boolean.class));
    }

    @Test
    void testFindSpecialtiesByVet() throws Exception {
        flushAndClear();
        mvc.perform(get("/vets/3/specialties")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 3)))
                .andExpect(jsonPath("$[*].name", contains("surgery", "dentistry")));
    }

    @Test
    void testFindVetsBySpecialty() throws Exception {
        flushAndClear();
        mvc.perform(get("/specialties/1/vets")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 5)))
                .andExpect(jsonPath("$[*].lastName", contains("Leary", "Stevens")));
    }

    @Test
    void testFindSpecialtiesByVet_Empty() throws Exception {
        assertEquals(0, count(1, 1));
        mvc.perform(get("/vets/1/specialties")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
