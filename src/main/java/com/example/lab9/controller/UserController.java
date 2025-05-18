package com.example.lab9.controller;

import com.example.lab9.dto.UserDto;
import com.example.lab9.service.RentalService;
import com.example.lab9.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RentalService rentalService;

    @Autowired
    public UserController(UserService userService, RentalService rentalService) {
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @GetMapping
    public String getAllUsers(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("users", userService.findByNameContaining(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("users", userService.findAll());
        }
        return "user/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("isNew", true);
        return "user/form";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") UserDto userDto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/form";
        }

        try {
            // Используем явное приведение типов, если необходимо
            userService.save(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "User successfully added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserDto userDto = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Id: " + id));
        model.addAttribute("user", userDto);
        model.addAttribute("isNew", false);
        return "user/form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserDto userDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/form";
        }

        try {
            // Устанавливаем ID напрямую, так как метод setId может быть унаследован от BaseDto
            userDto.setId(id);
            userService.save(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "User successfully updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User successfully deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/details/{id}")
    public String showUserDetails(@PathVariable Long id, Model model) {
        UserDto userDto = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Id: " + id));
        model.addAttribute("user", userDto);
        model.addAttribute("rentals", rentalService.findByUserId(id));
        return "user/details";
    }
}