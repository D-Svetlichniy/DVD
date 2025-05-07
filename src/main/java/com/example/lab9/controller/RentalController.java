package com.example.lab9.controller;

import com.example.lab9.dto.RentalCreateDto;
import com.example.lab9.dto.RentalDto;
import com.example.lab9.service.DvdService;
import com.example.lab9.service.RentalService;
import com.example.lab9.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final DvdService dvdService;
    private final UserService userService;

    @Autowired
    public RentalController(RentalService rentalService, DvdService dvdService, UserService userService) {
        this.rentalService = rentalService;
        this.dvdService = dvdService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllRentals(Model model) {
        model.addAttribute("rentals", rentalService.findAll());
        return "rental/list";
    }

    @GetMapping("/active")
    public String getActiveRentals(Model model) {
        model.addAttribute("rentals", rentalService.findAllActive());
        model.addAttribute("activeOnly", true);
        return "rental/list";
    }

    @GetMapping("/returned")
    public String getReturnedRentals(Model model) {
        model.addAttribute("rentals", rentalService.findAllReturned());
        model.addAttribute("returnedOnly", true);
        return "rental/list";
    }

    @GetMapping("/overdue")
    public String getOverdueRentals(Model model) {
        model.addAttribute("rentals", rentalService.findOverdueRentals());
        model.addAttribute("overdueOnly", true);
        return "rental/list";
    }

    @GetMapping("/new")
    public String showRentForm(Model model, @RequestParam(required = false) Long dvdId) {
        RentalCreateDto rentalCreateDto = new RentalCreateDto();

        if (dvdId != null) {
            rentalCreateDto.setDvdId(dvdId);
        }

        model.addAttribute("rental", rentalCreateDto);
        model.addAttribute("availableDvds", dvdService.findAllAvailable());
        model.addAttribute("users", userService.findAll());
        return "rental/form";
    }

    @PostMapping("/new")
    public String rentDvd(@Valid @ModelAttribute("rental") RentalCreateDto rentalCreateDto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("availableDvds", dvdService.findAllAvailable());
            model.addAttribute("users", userService.findAll());
            return "rental/form";
        }

        try {
            RentalDto rentalDto = rentalService.rentDvd(rentalCreateDto);
            redirectAttributes.addFlashAttribute("successMessage", "DVD rented successfully!");
            return "redirect:/rentals";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error renting DVD: " + e.getMessage());
            return "redirect:/rentals/new";
        }
    }

    @GetMapping("/return/{id}")
    public String showReturnForm(@PathVariable Long id, Model model) {
        RentalDto rentalDto = rentalService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rental Id: " + id));

        if (rentalDto.isReturned()) {
            throw new IllegalStateException("DVD has already been returned");
        }

        model.addAttribute("rental", rentalDto);
        return "rental/return";
    }

    @PostMapping("/return/{id}")
    public String returnDvd(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rentalService.returnDvd(id);
            redirectAttributes.addFlashAttribute("successMessage", "DVD returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error returning DVD: " + e.getMessage());
        }
        return "redirect:/rentals";
    }

    @GetMapping("/details/{id}")
    public String showRentalDetails(@PathVariable Long id, Model model) {
        RentalDto rentalDto = rentalService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rental Id: " + id));
        model.addAttribute("rental", rentalDto);
        return "rental/details";
    }
}