package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.config.CustomConfig;
import io.github.paritoshgpt1.Housie.dto.ErrorPage;
import io.github.paritoshgpt1.Housie.dto.PlayerDetailsForm;
import io.github.paritoshgpt1.Housie.dto.PlayerForm;
import io.github.paritoshgpt1.Housie.model.Organizer;
import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.repository.OrganizerRepository;
import io.github.paritoshgpt1.Housie.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static io.github.paritoshgpt1.Housie.util.Constants.TICKET_URL;

@Controller
@AllArgsConstructor
public class OrganizerController {

	private final OrganizerRepository organizerRepository;
	private final PlayerRepository playerRepository;
	private final CustomConfig customConfig;

	@GetMapping("/organizer")
	public String organizer(Model model) {
		model.addAttribute("organizer", new Organizer());
		return "organizer";
	}

	@PostMapping("/organizer")
	public String organizerDetails(@ModelAttribute Organizer organizer, Model model) {
		Organizer organizerDb = organizerRepository.findByCode(organizer.getCode().toLowerCase());
		if (organizerDb == null) {
			ErrorPage error = new ErrorPage();
			error.setHeading("You do not have access!");
			error.setDescription("Please contact Paritosh to get your organizer code");
			model.addAttribute("customError", error);
			return "error";
		}
		PlayerForm playerForm = new PlayerForm();
		playerForm.setOrganizerCode(organizerDb.getCode());
		model.addAttribute("playerForm", playerForm);
		model.addAttribute("organizer", organizerDb);
		return "organizer-details";
	}

	@PostMapping("/player-details")
	public String playerDetails(@ModelAttribute PlayerForm playerForm, Model model) {
		Organizer organizerDb = organizerRepository.findByCode(playerForm.getOrganizerCode());
		model.addAttribute("organizer", organizerDb);
		model.addAttribute("playerForm", playerForm);

		PlayerDetailsForm playerDetailsForm = new PlayerDetailsForm();
		List<Player> playerList = new ArrayList<>(playerForm.getCount());
		playerDetailsForm.setPlayers(playerList);

		model.addAttribute("playerDetailsForm", playerDetailsForm);

		return "player-details";
	}

	@SneakyThrows
	@PostMapping("/create-players")
	public String createPlayers(@ModelAttribute PlayerDetailsForm playerDetailsForm, Model model) {
		List<String> urls = new ArrayList<>();
		for(Player player: playerDetailsForm.getPlayers()) {
			// Maximum 6 tickets for each player
			if (player.getTickets() > 6) {
				player.setTickets(6);
			}

			// Generate a unique player code with a few retries in case of race conditions
			int attempts = 0;
			boolean saved = false;
			while (!saved && attempts < 10) {
				attempts++;
				String candidate = generateUniquePlayerCode();
				player.setCode(candidate);
				try {
					playerRepository.save(player);
					saved = true;
				} catch (DataIntegrityViolationException ex) {
					// Likely a unique constraint violation on code, retry with a new code
				}
			}

			urls.add(generateUrl(player.getCode()));
		}
		model.addAttribute("players", playerDetailsForm.getPlayers());
		model.addAttribute("urls", urls);
		return "create-players";
	}

	private String generateUniquePlayerCode() {
		String code = RandomStringUtils.randomAlphanumeric(10);
		while (playerRepository.findPlayerByCode(code) != null) {
			code = RandomStringUtils.randomAlphanumeric(10);
		}
		return code;
	}

	private String generateUrl(String code) {
		UriComponents uriComponents =
				UriComponentsBuilder.newInstance()
						.scheme(customConfig.getScheme())
						.host(customConfig.getHost())
						.path(TICKET_URL)
						.queryParam("code", code)
						.build();
		return uriComponents.toUriString();
	}
}
