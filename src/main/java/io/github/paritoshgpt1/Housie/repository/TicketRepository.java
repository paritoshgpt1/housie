package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.model.Ticket;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, Integer> {
    List<Ticket> findAllByPlayer(Player player);
}
