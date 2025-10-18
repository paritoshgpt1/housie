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
        // Index on foreign keys for faster joins/filters
        try {
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_players_organizer_id ON players(organizer_id)");
        } catch (Exception e) {
            log.warn("Could not create index on players.organizer_id: {}", e.getMessage());
        }
    }

    @PostConstruct
    public void migratePlayerOrganizerConstraint() {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS app_migrations (name varchar(255) primary key, applied_at timestamp DEFAULT now())");
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM app_migrations WHERE name='players_fk_organizer_v1'",
                    Integer.class
            );
            if (count != null && count == 0) {
                // Ensure organizer_id column exists
                try {
                    jdbcTemplate.execute("ALTER TABLE players ADD COLUMN IF NOT EXISTS organizer_id integer");
                } catch (Exception e) {
                    log.warn("Could not add players.organizer_id (may already exist): {}", e.getMessage());
                }

                // Clean existing dependent data to avoid FK/NOT NULL violations
                try { jdbcTemplate.execute("DELETE FROM claims"); } catch (Exception e) { log.warn("Delete claims failed: {}", e.getMessage()); }
                try { jdbcTemplate.execute("DELETE FROM tickets"); } catch (Exception e) { log.warn("Delete tickets failed: {}", e.getMessage()); }
                try { jdbcTemplate.execute("DELETE FROM players"); } catch (Exception e) { log.warn("Delete players failed: {}", e.getMessage()); }

                // Make organizer_id mandatory and add FK with RESTRICT on delete
                try {
                    jdbcTemplate.execute("ALTER TABLE players ALTER COLUMN organizer_id SET NOT NULL");
                } catch (Exception e) {
                    log.warn("Set NOT NULL on players.organizer_id failed: {}", e.getMessage());
                }
                try {
                    jdbcTemplate.execute("ALTER TABLE players ADD CONSTRAINT IF NOT EXISTS fk_players_organizer FOREIGN KEY (organizer_id) REFERENCES organizers(id) ON DELETE RESTRICT");
                } catch (Exception e) {
                    log.warn("Add FK players.organizer_id failed: {}", e.getMessage());
                }

                // Record migration
                try {
                    jdbcTemplate.execute("INSERT INTO app_migrations(name) VALUES ('players_fk_organizer_v1')");
                } catch (Exception e) {
                    log.warn("Record migration failed (may already be recorded): {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Migration check failed: {}", e.getMessage());
        }
    }
}
