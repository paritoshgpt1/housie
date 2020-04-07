package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Round;
import io.github.paritoshgpt1.Housie.repository.RoundRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@AllArgsConstructor
@RestController
public class RoundController {

	private final RoundRepository roundRepository;

	@PostMapping(value = "/test", produces = "application/json")
	public void test(Integer number, Integer roundNumber) {
		System.out.println(number);
		System.out.println(roundNumber);
		Round round = roundRepository.findRoundById(roundNumber);
		System.out.println("Round: " + round);
		String newNumberList = round.getNumbers();
		if (newNumberList == null) {
			newNumberList = "";
		}
		newNumberList += number + ",";
		round.setNumbers(newNumberList);
		roundRepository.save(round);
	}

}
