package com.nabagagem.connectbe;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("SqlDialectInspection")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ContextConfiguration(initializers = BaseJpaTest.DockerPostgreDataSourceInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ConnectBeApplication.class)
@Transactional
@Commit
@WithMockUser
@AutoConfigureMockMvc
public abstract class BaseJpaTest {
    private static final String CONTAINER_IMAGE = "maven.kriegerit.de:18444/library/postgres:13.2";
    public static final PostgreSQLContainer<?> postgreDBContainer = new PostgreSQLContainer(DockerImageName.parse(CONTAINER_IMAGE)
            .asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("pitch")
            .withUsername("pitch")
            .withPassword("pitch");

    static {
        postgreDBContainer.withReuse(true);
        postgreDBContainer.start();
    }

    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreDBContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreDBContainer.getUsername(),
                    "spring.datasource.password=" + postgreDBContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
