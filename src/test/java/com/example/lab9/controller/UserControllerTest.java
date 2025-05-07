package com.example.lab9.controller;

import com.example.lab9.dto.UserDto;
import com.example.lab9.service.RentalService;
import com.example.lab9.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RentalService rentalService;

    private UserDto sampleUser;

    @BeforeEach
    public void setUp() {
        sampleUser = new UserDto();
        sampleUser.setId(1L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setPhone("+380501234567");
        sampleUser.setAddress("123 Main St");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", Arrays.asList(sampleUser)));
    }

    @Test
    public void testGetAllUsersWithSearch() throws Exception {
        when(userService.findByNameContaining(anyString())).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/users").param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", Arrays.asList(sampleUser)))
                .andExpect(model().attribute("search", "John"));
    }

    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/users/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("isNew", true));
    }

    @Test
    public void testAddUser() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/users/add")
                        .param("name", "John Doe")
                        .param("email", "john@example.com")
                        .param("phone", "+380501234567")
                        .param("address", "123 Main St"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    public void testUpdateUser() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/users/edit/1")
                        .param("name", "John Smith")
                        .param("email", "john@example.com")
                        .param("phone", "+380501234567")
                        .param("address", "456 New St"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(get("/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowUserDetails() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(rentalService.findByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("rentals"));
    }
}
