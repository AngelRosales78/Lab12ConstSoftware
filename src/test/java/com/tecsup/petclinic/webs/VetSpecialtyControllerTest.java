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


    // Diego, C: especialidad principal; controlador y H2 reales.
    private boolean primary(int vet, int specialty) {
        flushAndClear();
        return jdbc.queryForObject("select is_primary from vet_specialties where vet_id=? and specialty_id=?",
                Boolean.class, vet, specialty);
    }

    @Test
    void testSetPrimarySpecialty() throws Exception {
        assertFalse(primary(3, 3));
        mvc.perform(put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(3)).andExpect(jsonPath("$.specialtyId").value(3))
                .andExpect(jsonPath("$.primarySpecialty").value(true));
        assertTrue(primary(3, 3));
    }

    @Test
    void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {
        assertTrue(primary(3, 2));
        boolean otherVetPrimary = primary(2, 1);
        mvc.perform(put("/vet-specialties/3/3/primary")).andExpect(status().isOk())
                .andExpect(jsonPath("$.primarySpecialty").value(true));
        assertFalse(primary(3, 2));
        assertTrue(primary(3, 3));
        assertEquals(otherVetPrimary, primary(2, 1));
        assertEquals(1, jdbc.queryForObject(
                "select count(*) from vet_specialties where vet_id=3 and is_primary=true", Integer.class));
    }

    @Test
    void testSetPrimarySpecialty_NotAssigned() throws Exception {
        mvc.perform(put("/vet-specialties/3/1/primary"))
                .andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", not(emptyString())));
        assertTrue(primary(3, 2));
        assertFalse(primary(3, 3));
        assertEquals(0, count(3, 1));
    }

    // Diego, D: filtro, experiencia y certificacion.
    @Test
    void testFindVetsByMinExperience() throws Exception {
        // Agrega un candidato real bajo el limite para demostrar que el filtro lo excluye.
        mvc.perform(post("/vet-specialties").contentType(MediaType.APPLICATION_JSON)
                .content("{\"vetId\":1,\"specialtyId\":1}")).andExpect(status().isCreated());
        mvc.perform(put("/vet-specialties/1/1/experience").contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearsExperience\":6}")).andExpect(status().isOk());
        flushAndClear();
        mvc.perform(get("/specialties/1/vets").param("minExperience", "7"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[*].id", contains(2, 5)));
        mvc.perform(get("/specialties/1/vets").param("minExperience", "8"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].id", contains(2)));
        mvc.perform(get("/specialties/1/vets").param("minExperience", "9"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/specialties/1/vets")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", contains(1, 2, 5)));
    }

    @Test
    void testUpdateYearsExperience() throws Exception {
        mvc.perform(put("/vet-specialties/2/1/experience").contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearsExperience\":9}"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vetId").value(2)).andExpect(jsonPath("$.specialtyId").value(1))
                .andExpect(jsonPath("$.yearsExperience").value(9)).andExpect(jsonPath("$.primarySpecialty").value(true))
                .andExpect(jsonPath("$.certificationDate").value("2015-06-15"));
        flushAndClear();
        assertEquals(9, jdbc.queryForObject(
                "select years_experience from vet_specialties where vet_id=2 and specialty_id=1", Integer.class));
        mvc.perform(get("/specialties/1/vets").param("minExperience", "9"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].id", contains(2)));
    }

    @Test
    void testUpdateCertificationDate() throws Exception {
        mvc.perform(put("/vet-specialties/2/1/certification").contentType(MediaType.APPLICATION_JSON)
                .content("{\"certificationDate\":\"2024-02-29\"}"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.certificationDate").value("2024-02-29"))
                .andExpect(jsonPath("$.yearsExperience").value(8)).andExpect(jsonPath("$.primarySpecialty").value(true));
        flushAndClear();
        assertEquals(java.time.LocalDate.of(2024, 2, 29), jdbc.queryForObject(
                "select certification_date from vet_specialties where vet_id=2 and specialty_id=1", java.sql.Date.class).toLocalDate());
    }

    @Test
    void testUpdateYearsExperience_Invalid() throws Exception {
        for (String body : java.util.List.of("{}", "{\"yearsExperience\":-1}")) {
            mvc.perform(put("/vet-specialties/2/1/experience").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        flushAndClear();
        assertEquals(8, jdbc.queryForObject(
                "select years_experience from vet_specialties where vet_id=2 and specialty_id=1", Integer.class));
    }

    @Test
    void testUpdateCertificationDate_Invalid() throws Exception {
        for (String body : java.util.List.of("{}", "{\"certificationDate\":\"fecha-invalida\"}")) {
            mvc.perform(put("/vet-specialties/2/1/certification").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        flushAndClear();
        assertEquals(java.time.LocalDate.of(2015, 6, 15), jdbc.queryForObject(
                "select certification_date from vet_specialties where vet_id=2 and specialty_id=1", java.sql.Date.class).toLocalDate());
    }

    @Test
    void testFindVetsByMinExperience_Invalid() throws Exception {
        mvc.perform(get("/specialties/1/vets").param("minExperience", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/specialties/1/vets").param("minExperience", "texto")).andExpect(status().isBadRequest());
    }
}
