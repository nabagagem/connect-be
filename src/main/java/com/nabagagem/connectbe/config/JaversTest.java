package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.entities.Certification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class JaversTest implements CommandLineRunner {
    private final Javers javers;

    @Override
    public void run(String... args) throws Exception {
        Changes changes = javers.findChanges(QueryBuilder.byClass(Certification.class).build());
        String content = javers.getJsonConverter().toJson(changes);
        log.info("Changes: {}", content);
    }
}
