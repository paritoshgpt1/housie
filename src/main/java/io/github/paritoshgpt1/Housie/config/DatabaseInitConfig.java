package io.github.paritoshgpt1.Housie.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitConfig.class);

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initIndexes() {
        // Supports case-insensitive contains search on players.name in Postgres
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm");
        } catch (Exception e) {
            log.warn("Could not create pg_trgm extension (may lack privileges): {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_players_name_trgm ON players USING gin (lower(name) gin_trgm_ops)");
        } catch (Exception e) {
            log.warn("Could not create GIN trigram index on players.name: {}", e.getMessage());
        }
    }
}

