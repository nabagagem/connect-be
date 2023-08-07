package com.nabagagem.connectbe.pocgeo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.LineNumberReader;

@Slf4j
@Component
@AllArgsConstructor
public class InitCities implements CommandLineRunner {
    private final CityRepository cityRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting loading");
        cityRepository.deleteAll();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader("/home/ricardo/Downloads/DE/DE.txt"));
        while (lineNumberReader.ready()) {
            String[] parts = lineNumberReader.readLine().split("\\t");
            cityRepository.save(City.builder()
                    .lat(Double.valueOf(parts[4]))
                    .lng(Double.valueOf(parts[5]))
                    .name(parts[2])
                    .region(parts[8])
                    .build());
        }
        log.info("Finished loading {}", cityRepository.count());
    }
}
