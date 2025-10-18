package io.github.paritoshgpt1.Housie.controller;

import io.github.paritoshgpt1.Housie.model.Organizer;
import io.github.paritoshgpt1.Housie.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final OrganizerRepository organizerRepository;

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
}

