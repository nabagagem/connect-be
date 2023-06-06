package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.BaseJpaTest;
import com.nabagagem.connectbe.JsonDataTestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser("2c38e708-6d85-4a39-8008-754cee8821cd")
class ConnectProfileControllerGetProfileTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    @SneakyThrows
    void emptyProfile() {
        String content = mockMvc.perform(get("/api/v1/profile/{id}", UUID.fromString("2c38e708-6d85-4a39-8008-754cee8821cd")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(content)
                .isEqualToIgnoringNewLines("""
                        {"id":null,"personalInfo":null,"averageStars":null,"skills":[],
                        "certifications":[],"profileMetrics":null,
                        "bio":null,"availabilities":{},"myRating":null,"lastRatings":[]}             
                        """);
    }

    @SneakyThrows
    @Test
    @Sql({"classpath:sql/setup-profile.sql", "classpath:sql/set-email.sql"})
    @WithMockUser("2eb0b7be-6bf5-47f8-8a51-3a19e14f290d")
    void fullProfile() {
        String content = mockMvc.perform(get("/api/v1/profile/{id}", "ricardobaumann"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(content)
                .whenIgnoringPaths("$.id", "$.profileMetrics.lastActivity")
                .isEqualTo(JsonDataTestUtil.loadJsonFromFile("json/profile.json"));
    }
}