package com.nabagagem.connectbe.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.JsonDataTestUtil;
import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.AvailabilityCommand;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.BioCommand;
import com.nabagagem.connectbe.domain.profile.CertificationsCommand;
import com.nabagagem.connectbe.domain.profile.PatchSkillCommand;
import com.nabagagem.connectbe.domain.profile.PatchSkillPayload;
import com.nabagagem.connectbe.domain.profile.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillCommand;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.domain.rating.ProfileRatingPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfileService;
import com.nabagagem.connectbe.services.profile.SlugService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ConnectProfileController.class)
@WithMockUser
class ConnectProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService mockProfileService;
    @MockBean
    private SlugService mockSlugService;
    @MockBean
    private ProfileAuthService mockProfileAuthService;
    @MockBean
    private LoginHelper mockLoginHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID loggedUserId = UUID.fromString("203be338-947d-4581-a0dc-0ee149676d77");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(loggedUserId);

        // Configure LoginHelper.loggedUser(...).
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.of(loggedUserId));

        // Configure ProfileService.getProfile(...).
        MoneyAmount amountPerHour = new MoneyAmount();
        amountPerHour.setAmount(BigDecimal.ONE);
        amountPerHour.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final ProfilePayload profilePayload = new ProfilePayload(
                UUID.fromString("eedb663e-f9a3-4573-908c-3d56980ff455"),
                UUID.fromString("d35f7cde-7e0d-409a-9ed8-fd6e6d96e9a7"),
                PersonalInfo.builder()
                        .publicName("public name")
                        .workingMode(WorkingMode.ONSITE)
                        .amountPerHour(amountPerHour)
                        .profileCategory(JobCategory.IT)
                        .profession("pet sitter")
                        .highlightTitle("best pet sitter")
                        .available(true)
                        .city("berlin")
                        .email("mail@mail.com")
                        .build(), 0.0,
                Set.of(new SkillReadPayload(UUID.fromString("a43738a3-9757-45ae-b471-740b7fe8367c"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), new ProfileMetrics(0L, 0L, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("1bf52fe7-dac7-4513-9c45-1dd69188bcfa"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)),
                List.of(new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("6d9bc4d2-cd44-4843-846f-892d63a0a600"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))), null);
        when(mockProfileService.getProfile(loggedUserId,
                loggedUserId)).thenReturn(profilePayload);

        // Configure ProfileAuthService.isAllowedOn(...).
        when(mockProfileAuthService.isAllowedOn(
                profilePayload))
                .thenReturn(profilePayload);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo(
                JsonDataTestUtil.loadJsonFromFile("json/profile.json")
        );
    }

    @Test
    void testUpdatePersonalInfo() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        MoneyAmount amountPerHour = new MoneyAmount();
        amountPerHour.setAmount(BigDecimal.ONE);
        amountPerHour.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        PersonalInfo personalInfo = PersonalInfo.builder()
                .publicName("public name")
                .slug("slug")
                .workingMode(WorkingMode.ONSITE)
                .amountPerHour(amountPerHour)
                .profileCategory(JobCategory.IT)
                .profession("pet sitter")
                .highlightTitle("best pet sitter")
                .available(true)
                .city("berlin")
                .email("mail@mail.com")
                .build();

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/v1/profile/{id}/info", "id")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(personalInfo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).updateInfo(
                new PersonalInfoCommand(uuid,
                        personalInfo));
    }

    @Test
    void testGetInfo() throws Exception {
        // Setup
        MoneyAmount amountPerHour = new MoneyAmount();
        amountPerHour.setAmount(BigDecimal.ONE);
        amountPerHour.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        PersonalInfo personalInfo = PersonalInfo.builder()
                .publicName("public name")
                .slug("slug")
                .workingMode(WorkingMode.ONSITE)
                .amountPerHour(amountPerHour)
                .profileCategory(JobCategory.IT)
                .profession("pet sitter")
                .highlightTitle("best pet sitter")
                .available(true)
                .city("berlin")
                .email("mail@mail.com")
                .build();

        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockProfileService.getInfo(uuid))
                .thenReturn(personalInfo);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/info", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"publicName":"public name","slug":"slug","profession":"pet sitter",
                "highlightTitle":"best pet sitter","profileCategory":"IT",
                "otherCategory":null,"workingMode":"ONSITE","city":"berlin",
                "publicProfile":null,"available":true,"tags":null,
                "amountPerHour":{"amount":1,"currency":"EUR"},"email":"mail@mail.com","enableMessageEmail":null}
                """);
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testUpdateSkills() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        Set<SkillPayload> skills = Set.of(new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));
        SkillCommand skillCommand = new SkillCommand(uuid,
                skills);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/v1/profile/{id}/skills", "id")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(skills))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).updateSkills(
                skillCommand);
    }

    @Test
    void testPatchSkill() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        PatchSkillPayload patchSkillPayload = new PatchSkillPayload(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        patch("/api/v1/profile/{id}/skills/{skillId}", "id", "a0549535-94f9-4fa3-8ced-ebdab7dbad1d")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(patchSkillPayload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).patchSkill(
                new PatchSkillCommand(uuid,
                        UUID.fromString("a0549535-94f9-4fa3-8ced-ebdab7dbad1d"), patchSkillPayload));
    }

    @Test
    void testGetSkills() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Configure ProfileService.getSkills(...).
        final Set<SkillReadPayload> skillReadPayloads = Set.of(
                new SkillReadPayload(UUID.fromString("bcee0d1d-57a6-48e2-8905-6ac702d8fcc4"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false)));
        when(mockProfileService.getSkills(uuid))
                .thenReturn(skillReadPayloads);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/skills", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                [{"id":"bcee0d1d-57a6-48e2-8905-6ac702d8fcc4","name":"name","certifications":0,"level":"ONE_2_THREE","top":false}]
                """);
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testGetSkills_ProfileServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockProfileService.getSkills(uuid))
                .thenReturn(Collections.emptySet());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/skills", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testUpdateCertifications() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        Set<CertificationPayload> certifications = Set.of(new CertificationPayload("title", 2020));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/v1/profile/{id}/certifications", "id")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(certifications))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).updateCertifications(
                new CertificationsCommand(uuid,
                        certifications));
    }

    @Test
    void testGetCertifications() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Configure ProfileService.getCertifications(...).
        final Set<CertificationPayload> certificationPayloads = Set.of(new CertificationPayload("title", 2020));
        when(mockProfileService.getCertifications(uuid))
                .thenReturn(certificationPayloads);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/certifications", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("[{\"title\":\"title\",\"year\":2020}]");
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testGetCertifications_ProfileServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockProfileService.getCertifications(uuid))
                .thenReturn(Collections.emptySet());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/certifications", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testUpdateAvailability() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        Map<DayOfWeek, AvailabilityType> availabilities = Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/v1/profile/{id}/availability", "id")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(availabilities))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).updateAvailability(
                new AvailabilityCommand(uuid,
                        availabilities));
    }

    @Test
    void testGetAvailability() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Configure ProfileService.getAvailabilities(...).
        final Map<DayOfWeek, AvailabilityType> dayOfWeekAvailabilityTypeMap = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));
        when(mockProfileService.getAvailabilities(uuid))
                .thenReturn(dayOfWeekAvailabilityTypeMap);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/availability", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"FRIDAY\":\"MORNING\"}");
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testUpdateBio() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);
        ProfileBio profileBio = ProfileBio.builder()
                .description("bio description")
                .professionalRecord("bio professional record")
                .build();

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/v1/profile/{id}/bio", "id")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(profileBio))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfileService).updateBio(
                new BioCommand(uuid, profileBio));
    }

    @Test
    void testGetBio() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Configure ProfileService.getProfileBio(...).
        final Optional<ProfileBio> profileBio = Optional.of(
                ProfileBio.builder()
                        .professionalRecord("prof")
                        .description("desc")
                        .build());
        when(mockProfileService.getProfileBio(uuid))
                .thenReturn(profileBio);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/bio", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("{\"description\":\"desc\",\"professionalRecord\":\"prof\"}");
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }

    @Test
    void testGetBio_ProfileServiceReturnsAbsent() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("45c51b24-f733-4641-97c5-d5c725e3499a");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockProfileService.getProfileBio(uuid))
                .thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/bio", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
    }
}
