package com.nabagagem.connectbe;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SampleJpaTest extends BaseJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void test() {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk());
    }

}
