package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.BaseJpaTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class AccountResourceTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testCreate() {
        
    }
}