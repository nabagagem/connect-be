package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchParams;
import com.nabagagem.connectbe.domain.profile.TopSkillPayload;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.services.profile.ProfileSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileSearchController.class)
@WithMockUser("4cba124d-551f-4688-ab76-edbbf69d5d02")
class ProfileSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileSearchService mockProfileSearchService;

    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testList() throws Exception {
        // Setup
        // Configure ProfileSearchService.searchFor(...).
        final Page<ProfileSearchItemPayload> profileSearchItemPayloads = new PageImpl<>(
                List.of(new ProfileSearchItemPayload(null,
                        Set.of(new TopSkillPayload("level", "name")))));
        ProfileSearchParams searchParams = new ProfileSearchParams(Set.of(JobCategory.IT), Set.of(WorkingMode.REMOTE), "cachorro");
        when(mockProfileSearchService.searchFor(
                eq(searchParams),
                any(Pageable.class)))
                .thenReturn(profileSearchItemPayloads);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile")
                        .param("category", JobCategory.IT.toString())
                        .param("workingMode", WorkingMode.REMOTE.toString())
                        .param("searchExpression", "cachorro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[{"topSkills":[{"level":"level","name":"name"}]}],
                "pageable":"INSTANCE","last":true,"totalElements":1,"totalPages":1,"size":1,"number":0,
                "sort":{"empty":true,"sorted":false,"unsorted":true},"first":true,"numberOfElements":1,"empty":false}
                """);
    }

    @Test
    void testList_ProfileSearchServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockProfileSearchService.searchFor(
                any(ProfileSearchParams.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[],"pageable":"INSTANCE","totalElements":0,
                "totalPages":1,"last":true,"size":0,"number":0,
                "sort":{"empty":true,"sorted":false,"unsorted":true},
                "numberOfElements":0,"first":true,"empty":true}
                """);
    }
}
