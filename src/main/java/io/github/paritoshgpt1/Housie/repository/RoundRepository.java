package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Round;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends CrudRepository<Round, Long> {
    Round findRoundById(Integer id);
    Round findRoundByIdAndNumbers(Integer id, String numbers);
    Round findRoundByNumbers(String numbers);
    Round findFirstByNumbersOrderByCreatedAtDesc(String numbers);
}