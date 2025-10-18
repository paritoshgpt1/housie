package io.github.paritoshgpt1.Housie.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paritoshgpt1.Housie.model.Dividend;
import io.github.paritoshgpt1.Housie.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DividendsJsonSeeder implements CommandLineRunner {

    private final DividendRepository dividendRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) {
        try {
            ClassPathResource resource = new ClassPathResource("dividends.json");
            if (!resource.exists()) {
                log.warn("dividends.json not found on classpath; skipping seeding");
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                List<Dividend> items = objectMapper.readValue(is, new TypeReference<List<Dividend>>() {});
                for (Dividend incoming : items) {
                    if (incoming.getCode() == null || incoming.getCode().trim().isEmpty()) continue;
                    Dividend existing = dividendRepository.findByCode(incoming.getCode());
                    if (existing == null) {
                        Dividend toSave = Dividend.builder()
                                .name(incoming.getName())
                                .code(incoming.getCode())
                                .description(incoming.getDescription())
                                .image(incoming.getImage())
                                .build();
                        dividendRepository.save(toSave);
                    } else {
                        existing.setName(incoming.getName());
                        existing.setDescription(incoming.getDescription());
                        existing.setImage(incoming.getImage());
                        dividendRepository.save(existing);
                    }
                }
                log.info("Dividends JSON seeding completed (upsert by code)");
            }
        } catch (Exception e) {
            log.error("Failed to seed dividends from JSON", e);
        }
    }
}

