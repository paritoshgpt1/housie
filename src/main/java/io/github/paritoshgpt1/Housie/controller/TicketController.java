package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.util.Tambola;
import io.github.paritoshgpt1.Housie.util.TambolaTicket;
import io.github.paritoshgpt1.Housie.wrapper.TicketWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@AllArgsConstructor
public class TicketController {

	private final TicketWrapper ticketWrapper;
    private final io.github.paritoshgpt1.Housie.util.PdfService pdfService;


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
		model.addAttribute("code", code);
		return "tickets";
	}

    @GetMapping(value = "/generate-tickets.pdf")
    public org.springframework.http.ResponseEntity<byte[]> generateTicketsPdf(
            @RequestParam(name = "code") String code,
            org.springframework.web.context.request.WebRequest webRequest
    ) throws Exception {
		Player player = ticketWrapper.findPlayerByCode(code);
		if (player == null) {
			return org.springframework.http.ResponseEntity.status(404).body(null);
		}
		io.github.paritoshgpt1.Housie.util.TambolaTicket[] tickets = ticketWrapper.checkAndGetTickets(player);
		if (tickets == null || tickets.length == 0) {
			tickets = io.github.paritoshgpt1.Housie.util.Tambola.generateTickets(player.getTickets());
			tickets = ticketWrapper.saveTickets(player, tickets);
		}

		java.util.Map<String, Object> vars = new java.util.HashMap<>();
		vars.put("tickets", tickets);
		vars.put("name", player.getName());
		vars.put("code", code);

        String html = pdfService.renderHtml("tickets-pdf", vars);
        byte[] pdf = pdfService.renderPdf(html);
        return org.springframework.http.ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=Housie-Tickets-" + code + ".pdf")
                .body(pdf);
    }

    @GetMapping(value = "/generate-tickets.png")
    public org.springframework.http.ResponseEntity<byte[]> generateTicketsPng(
            @RequestParam(name = "code") String code
    ) throws Exception {
        Player player = ticketWrapper.findPlayerByCode(code);
        if (player == null) {
            return org.springframework.http.ResponseEntity.status(404).body(null);
        }
        io.github.paritoshgpt1.Housie.util.TambolaTicket[] tickets = ticketWrapper.checkAndGetTickets(player);
        if (tickets == null || tickets.length == 0) {
            tickets = io.github.paritoshgpt1.Housie.util.Tambola.generateTickets(player.getTickets());
            tickets = ticketWrapper.saveTickets(player, tickets);
        }

        java.util.Map<String, Object> vars = new java.util.HashMap<>();
        vars.put("tickets", tickets);
        vars.put("name", player.getName());
        vars.put("code", code);

        String html = pdfService.renderHtml("tickets-pdf", vars);
        byte[] pdf = pdfService.renderPdf(html);
        byte[] png = pdfService.pdfToPng(pdf);
        return org.springframework.http.ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Content-Disposition", "attachment; filename=Housie-Tickets-" + code + ".png")
                .body(png);
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

    @GetMapping("/tickets-print")
    public String ticketsPrint(@RequestParam(name = "code") String code, Model model) {
        Player player = ticketWrapper.findPlayerByCode(code);
        if (player == null) {
            return "error";
        }
        TambolaTicket[] tickets = ticketWrapper.checkAndGetTickets(player);
        if (tickets == null || tickets.length == 0) {
            tickets = Tambola.generateTickets(player.getTickets());
            tickets = ticketWrapper.saveTickets(player, tickets);
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("name", player.getName());
        model.addAttribute("code", code);
        return "tickets-print";
    }

}
