package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.BaseJpaTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
class AccountRepoTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testCreate() {

    }
}