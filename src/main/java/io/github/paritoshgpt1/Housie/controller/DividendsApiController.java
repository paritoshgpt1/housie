package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Dividend;
import io.github.paritoshgpt1.Housie.repository.DividendRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dividends")
public class DividendsApiController {

    private final DividendRepository dividendRepository;

    @GetMapping
    public Iterable<Dividend> list() {
        return dividendRepository.findByOrderById();
    }
}

