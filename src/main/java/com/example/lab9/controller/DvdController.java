package com.example.lab9.controller;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.service.DvdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dvds")
public class DvdController {

    private final DvdService dvdService;

    @Autowired
    public DvdController(DvdService dvdService) {
        this.dvdService = dvdService;
    }

    @GetMapping
    public String getAllDvds(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("dvds", dvdService.searchByKeyword(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("dvds", dvdService.findAll());
        }
        return "dvd/list";
    }

    @GetMapping("/available")
    public String getAvailableDvds(Model model) {
        model.addAttribute("dvds", dvdService.findAllAvailable());
        model.addAttribute("availableOnly", true);
        return "dvd/list";
    }

    @GetMapping("/unavailable")
    public String getUnavailableDvds(Model model) {
        model.addAttribute("dvds", dvdService.findAllUnavailable());
        model.addAttribute("unavailableOnly", true);
        return "dvd/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("dvd", new DvdDto());
        model.addAttribute("isNew", true);
        return "dvd/form";
    }

    @PostMapping("/add")
    public String addDvd(@Valid @ModelAttribute("dvd") DvdDto dvdDto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "dvd/form";
        }

        dvdService.save(dvdDto);
        redirectAttributes.addFlashAttribute("successMessage", "DVD successfully added!");
        return "redirect:/dvds";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DvdDto dvdDto = dvdService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid DVD Id: " + id));
        model.addAttribute("dvd", dvdDto);
        model.addAttribute("isNew", false);
        return "dvd/form";
    }

    @PostMapping("/edit/{id}")
    public String updateDvd(@PathVariable Long id,
                            @Valid @ModelAttribute("dvd") DvdDto dvdDto,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "dvd/form";
        }

        dvdDto.setId(id);
        dvdService.save(dvdDto);
        redirectAttributes.addFlashAttribute("successMessage", "DVD successfully updated!");
        return "redirect:/dvds";
    }

    @GetMapping("/delete/{id}")
    public String deleteDvd(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dvdService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "DVD successfully deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete DVD: " + e.getMessage());
        }
        return "redirect:/dvds";
    }

    @GetMapping("/details/{id}")
    public String showDvdDetails(@PathVariable Long id, Model model) {
        DvdDto dvdDto = dvdService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid DVD Id: " + id));
        model.addAttribute("dvd", dvdDto);
        return "dvd/details";
    }
}