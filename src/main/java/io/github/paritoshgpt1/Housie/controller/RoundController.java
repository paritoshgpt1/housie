package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.dto.ClaimDto;
import io.github.paritoshgpt1.Housie.model.Claim;
import io.github.paritoshgpt1.Housie.model.Round;
import io.github.paritoshgpt1.Housie.repository.ClaimRepository;
import io.github.paritoshgpt1.Housie.repository.RoundRepository;
import io.github.paritoshgpt1.Housie.wrapper.TicketWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RestController
public class RoundController {

	private final RoundRepository roundRepository;
	private final ClaimRepository claimRepository;
	private final TicketWrapper ticketWrapper;

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

	@GetMapping("/ticket-details")
	public Object ticketDetails(
			@RequestParam(name="ticketNumber") Integer ticketNumber,
			@RequestParam(name="roundNumber") Integer roundNumber
	) {
		Map<String, Object> ticketDetails = ticketWrapper.ticketDetails(ticketNumber);
		Map<String, Object> response = new HashMap<>(ticketDetails);
		Round round = roundRepository.findRoundById(roundNumber);
		if (round == null) return null;
		int[] roundNumbers = Arrays.stream(round.getNumbers().split(",")).mapToInt(Integer::parseInt).toArray();
		response.put("roundNumbers", roundNumbers);
		return response;
	}

	@PostMapping("/claims")
	public void saveClaim(ClaimDto claimDto) {

		Claim claim = Claim.builder()
				.name(claimDto.getName())
				.ticket(ticketWrapper.getTicket(claimDto.getTicketId()))
				.build();
		claimRepository.save(claim);

	}

}
