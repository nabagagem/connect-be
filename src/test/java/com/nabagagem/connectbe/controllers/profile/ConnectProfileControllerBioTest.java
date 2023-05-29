package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.BaseJpaTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ConnectProfileControllerBioTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void updateBio() {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/api/v1/profile/{id}/bio", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "description": "alterei a descricao",
                              "professionalRecord": "e tambem o record"
                            }
                        """)
        ).andExpect(status().isOk());

        String content = mockMvc.perform(get("/api/v1/profile/{id}/bio", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(content).isEqualTo("""
                {
                  "description": "alterei a descricao",
                  "professionalRecord": "e tambem o record"
                }
                """);
    }
}