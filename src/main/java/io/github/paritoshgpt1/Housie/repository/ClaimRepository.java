package io.github.paritoshgpt1.Housie.repository;

import io.github.paritoshgpt1.Housie.model.Claim;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends PagingAndSortingRepository<Claim, Integer> {
}
