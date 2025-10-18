package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Organizer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends PagingAndSortingRepository<Organizer, Integer> {
    Organizer findByCode(String code);
}
