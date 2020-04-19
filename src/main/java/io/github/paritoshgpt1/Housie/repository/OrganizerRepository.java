package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Organizer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends CrudRepository<Organizer, Integer> {
    Organizer findByCode(String code);
}