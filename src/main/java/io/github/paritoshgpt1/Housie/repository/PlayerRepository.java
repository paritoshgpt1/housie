package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findPlayerByCode(String code);
}