package com.nabagagem.connectbe.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nabagagem.connectbe.BaseJpaTest;
import com.nabagagem.connectbe.JsonDataTestUtil;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ConnectProfileControllerSkillTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    @WithMockUser("2c38e708-6d85-4a39-8008-754cee8821cd")
    void updateSkills() {
        UUID id = UUID.fromString("2c38e708-6d85-4a39-8008-754cee8821cd");
        String expected = JsonDataTestUtil.loadJsonFromFile("json/skills.json");
        mockMvc.perform(put("/api/v1/profile/{id}/skills", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expected)
        ).andExpect(status().isOk());

        String content = mockMvc.perform(get("/api/v1/profile/{id}/skills", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThatJson(content)
                .whenIgnoringPaths("[*].id")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expected);

        ArrayNode jsonObject = (ArrayNode) objectMapper.readTree(content);
        mockMvc.perform(patch("/api/v1/profile/{id}/skills/{skillId}", id, jsonObject.get(0).get("id").asText())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                            {"top":  true}
                        """)
        ).andExpect(status().isOk());

        String patched = mockMvc.perform(get("/api/v1/profile/{id}/skills", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThatJson(patched)
                .whenIgnoringPaths("[*].id")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(JsonDataTestUtil.loadJsonFromFile("json/skills-changed.json"));

    }
}