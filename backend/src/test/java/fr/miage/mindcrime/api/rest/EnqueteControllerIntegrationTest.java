package fr.miage.mindcrime.api.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EnqueteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generePersisteEtConsulteEnqueteSansDivulguerLeCoupable() throws Exception {
        MvcResult partieRes = mockMvc.perform(post("/api/v1/parties"))
                .andExpect(status().isCreated())
                .andReturn();

        String partieId = objectMapper.readTree(partieRes.getResponse().getContentAsString())
                .get("partieId").asText();

        MvcResult enqueteRes = mockMvc.perform(post("/api/v1/parties/" + partieId + "/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seed\": 42, \"nombreSuspects\": 5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partieId").value(partieId))
                .andExpect(jsonPath("$.seed").value(42))
                .andExpect(jsonPath("$.suspects.length()").value(5))
                .andExpect(jsonPath("$.indicesVisibles.length()").isNotEmpty())
                .andExpect(jsonPath("$.statutUnicite").value("COUPABLE_UNIQUE"))
                .andReturn();

        String body = enqueteRes.getResponse().getContentAsString();
        assertFalse(body.contains("coupable"), "La reponse API ne doit jamais exposer le coupable");
        assertFalse(body.contains("veriteTerrain"), "La reponse API ne doit jamais exposer la verite terrain");

        JsonNode json = objectMapper.readTree(body);
        String enqueteId = json.get("id").asText();

        mockMvc.perform(get("/api/v1/parties/" + partieId + "/enquetes/" + enqueteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(enqueteId))
                .andExpect(jsonPath("$.seed").value(42))
                .andExpect(jsonPath("$.suspects.length()").value(5));
    }

    @Test
    void retourne400ProblemJsonSiNombreSuspectsInvalide() throws Exception {
        MvcResult partieRes = mockMvc.perform(post("/api/v1/parties"))
                .andExpect(status().isCreated())
                .andReturn();
        String partieId = objectMapper.readTree(partieRes.getResponse().getContentAsString())
                .get("partieId").asText();

        mockMvc.perform(post("/api/v1/parties/" + partieId + "/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seed\": 42, \"nombreSuspects\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void retourne404ProblemJsonSiPartieInexistante() throws Exception {
        mockMvc.perform(post("/api/v1/parties/PARTIE-INCONNUE/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seed\": 42, \"nombreSuspects\": 4}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void retourne404ProblemJsonSiEnqueteInexistante() throws Exception {
        MvcResult partieRes = mockMvc.perform(post("/api/v1/parties"))
                .andExpect(status().isCreated())
                .andReturn();
        String partieId = objectMapper.readTree(partieRes.getResponse().getContentAsString())
                .get("partieId").asText();

        mockMvc.perform(get("/api/v1/parties/" + partieId + "/enquetes/ENQ-INEXISTANTE"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }
}