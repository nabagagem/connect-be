package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.BaseJpaTest;
import com.nabagagem.connectbe.JsonDataTestUtil;
import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.WorkingMode;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Sql("classpath:sql/setup-profile.sql")
class ProfileSearchControllerTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void list() {
        String payload = mockMvc.perform(get("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(payload)
                .when(Option.IGNORING_ARRAY_ORDER)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(JsonDataTestUtil.loadJsonFromFile("json/profile-search.json"));
    }

    @Test
    @SneakyThrows
    void filterByExpression() {
        String payload = mockMvc.perform(get("/api/v1/profile")
                        .queryParam("searchExpression", "i")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(payload)
                .when(Option.IGNORING_ARRAY_ORDER)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(JsonDataTestUtil.loadJsonFromFile("json/profile-filter-expression.json"));
    }

    @Test
    @SneakyThrows
    void filterByCategoryAndMode() {
        String payload = mockMvc.perform(get("/api/v1/profile")
                        .queryParam("category", JobCategory.BEAUTY_CARE.toString())
                        .queryParam("workingMode", WorkingMode.REMOTE.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(payload)
                .when(Option.IGNORING_ARRAY_ORDER)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(JsonDataTestUtil.loadJsonFromFile("json/profile-filter-category.json"));
    }
}