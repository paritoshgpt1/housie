package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Dividend;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DividendRepository extends CrudRepository<Dividend, Integer> {
    Iterable<Dividend> findByOrderById();
}