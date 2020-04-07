package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.util.Tambola;
import io.github.paritoshgpt1.Housie.util.TambolaTicket;
import io.github.paritoshgpt1.Housie.wrapper.TicketWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@AllArgsConstructor
public class TicketController {

	private final TicketWrapper ticketWrapper;


	@GetMapping("/generate-tickets")
	public String generateTickets(
			@RequestParam(name="code") String code,
			Model model
	) {
		Player player = ticketWrapper.findPlayerByCode(code);
		if (player == null) {
			return "error";
		}

		// Fetch tickets, if tickets already exist
		TambolaTicket[] tickets = ticketWrapper.checkAndGetTickets(player);
		// If tickets do not exist, generate new tickets
		if (tickets == null || tickets.length == 0) {
			tickets = Tambola.generateTickets(player.getTickets());
			tickets = ticketWrapper.saveTickets(player, tickets);
		}
		System.out.println(Arrays.toString(tickets));
		model.addAttribute("tickets", tickets);
		model.addAttribute("name", player.getName());
		return "tickets";
	}

	@GetMapping("/welcome")
	public String welcome(Model model) {
		return "welcome";
	}

	@GetMapping("/ticket-input")
	public String ticketInput(@RequestParam(name="count") Integer count, Model model) {
		TambolaTicket[] tickets = Tambola.generateTickets(count);
		model.addAttribute("count", count);
		return "ticket-input";
	}

}
