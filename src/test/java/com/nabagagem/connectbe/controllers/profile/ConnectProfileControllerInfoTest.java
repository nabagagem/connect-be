package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.BaseJpaTest;
import com.nabagagem.connectbe.JsonDataTestUtil;
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
class ConnectProfileControllerInfoTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void updatePersonalInfo() {
        UUID id = UUID.randomUUID();
        String expectedContent = JsonDataTestUtil.loadJsonFromFile("json/personal-info.json");
        mockMvc.perform(put("/api/v1/profile/{id}/info", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expectedContent)
        ).andExpect(status().isOk());

        String content = mockMvc.perform(get("/api/v1/profile/{id}/info", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThatJson(content).isEqualTo(expectedContent);
    }
}