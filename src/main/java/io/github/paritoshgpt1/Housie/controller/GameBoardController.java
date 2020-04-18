package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Dividend;
import io.github.paritoshgpt1.Housie.model.Round;
import io.github.paritoshgpt1.Housie.repository.DividendRepository;
import io.github.paritoshgpt1.Housie.repository.RoundRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class GameBoardController {

	private final RoundRepository roundRepository;
	private final DividendRepository dividendRepository;

	@GetMapping("/game-board")
	public String gameBoard(
			@RequestParam(name="code", required = false) String code,
			Model model
	) {
		// Check if any round without any numbers exist
		Round round = roundRepository.findFirstByNumbersOrderByCreatedAtDesc(null);
		if (round == null) {
			round = roundRepository.save(new Round());
		}
		model.addAttribute("round", round);
		return "gameboard.html";
	}

	@GetMapping("/dividends")
	public String dividends(Model model) {
		Iterable<Dividend> dividends = dividendRepository.findByOrderById();
		model.addAttribute("dividends", IterableUtils.toList(dividends));
		return "dividends.html";
	}
}
