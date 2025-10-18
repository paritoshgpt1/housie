package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Organizer;
import io.github.paritoshgpt1.Housie.model.Player;
import io.github.paritoshgpt1.Housie.model.Claim;
import io.github.paritoshgpt1.Housie.model.Ticket;
import io.github.paritoshgpt1.Housie.repository.OrganizerRepository;
import io.github.paritoshgpt1.Housie.repository.PlayerRepository;
import io.github.paritoshgpt1.Housie.repository.ClaimRepository;
import io.github.paritoshgpt1.Housie.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import io.github.paritoshgpt1.Housie.model.Round;
import io.github.paritoshgpt1.Housie.repository.RoundRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final OrganizerRepository organizerRepository;
    private final PlayerRepository playerRepository;
    private final ClaimRepository claimRepository;
    private final TicketRepository ticketRepository;
    private final RoundRepository roundRepository;

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/validators";
    }

    @GetMapping("/organizers/new")
    public String newOrganizerForm(Model model) {
        if (!model.containsAttribute("organizer")) {
            model.addAttribute("organizer", new Organizer());
        }
        return "admin-organizer-new";
    }

    @PostMapping("/organizers")
    public String createOrganizer(@ModelAttribute Organizer organizer, Model model) {
        String code = organizer.getCode() != null ? organizer.getCode().trim().toLowerCase() : "";
        String name = organizer.getName() != null ? organizer.getName().trim() : "";
        if (code.isEmpty() || name.isEmpty()) {
            model.addAttribute("error", "Name and Code are required");
            model.addAttribute("organizer", organizer);
            return "admin-organizer-new";
        }
        if (organizerRepository.findByCode(code) != null) {
            model.addAttribute("error", "An organizer with this code already exists");
            model.addAttribute("organizer", organizer);
            return "admin-organizer-new";
        }
        organizer.setCode(code);
        Organizer saved = organizerRepository.save(organizer);
        model.addAttribute("success", "Organizer created successfully");
        model.addAttribute("createdOrganizer", saved);
        model.addAttribute("organizer", new Organizer());
        return "admin-organizer-new";
    }

    @GetMapping("/organizers")
    public String listOrganizers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Organizer> organizers = organizerRepository.findAll(pageable);
        model.addAttribute("page", organizers);
        model.addAttribute("items", organizers.getContent());
        model.addAttribute("activePage", "organizers-list");
        return "admin-organizers";
    }

    @GetMapping("/players")
    public String listPlayers(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "org", required = false) String orgCode,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Player> players;
        Organizer selectedOrg = null;
        if (orgCode != null && !orgCode.trim().isEmpty()) {
            selectedOrg = organizerRepository.findByCode(orgCode.trim().toLowerCase());
        }
        if (selectedOrg != null && q != null && !q.trim().isEmpty()) {
            players = playerRepository.findByOrganizerAndNameContainingIgnoreCase(selectedOrg, q.trim(), pageable);
        } else if (selectedOrg != null) {
            players = playerRepository.findByOrganizer(selectedOrg, pageable);
        } else if (q != null && !q.trim().isEmpty()) {
            players = playerRepository.findByNameContainingIgnoreCase(q.trim(), pageable);
        } else {
            players = playerRepository.findAll(pageable);
        }
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("org", orgCode == null ? "" : orgCode);
        model.addAttribute("page", players);
        model.addAttribute("items", players.getContent());
        model.addAttribute("activePage", "players");
        // Provide organizer list for filter dropdown
        model.addAttribute("organizersAll", organizerRepository.findAll());
        return "admin-players";
    }

    @GetMapping("/claims")
    public String listClaims(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Claim> claims = claimRepository.findAll(pageable);
        model.addAttribute("page", claims);
        model.addAttribute("items", claims.getContent());
        model.addAttribute("activePage", "claims");
        return "admin-claims";
    }

    @GetMapping("/tickets")
    public String listTickets(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Ticket> tickets = ticketRepository.findAll(pageable);
        model.addAttribute("page", tickets);
        model.addAttribute("items", tickets.getContent());
        model.addAttribute("activePage", "tickets");
        return "admin-tickets";
    }

    @GetMapping("/rounds")
    public String listRounds(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Round> rounds = roundRepository.findAll(pageable);
        model.addAttribute("page", rounds);
        model.addAttribute("items", rounds.getContent());
        model.addAttribute("activePage", "rounds");
        return "admin-rounds";
    }
}
