package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import io.github.paritoshgpt1.Housie.model.Organizer;

@Repository
public interface PlayerRepository extends PagingAndSortingRepository<Player, Long> {
    Player findPlayerByCode(String code);
    Page<Player> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Player> findByOrganizer(Organizer organizer, Pageable pageable);
    Page<Player> findByOrganizerAndNameContainingIgnoreCase(Organizer organizer, String name, Pageable pageable);
}
