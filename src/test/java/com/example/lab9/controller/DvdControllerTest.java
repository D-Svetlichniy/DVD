package com.example.lab9.controller;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.service.DvdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DvdController.class)
public class DvdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DvdService dvdService;

    private DvdDto sampleDvd;

    @BeforeEach
    public void setUp() {
        sampleDvd = new DvdDto();
        sampleDvd.setId(1L);
        sampleDvd.setTitle("Matrix");
        sampleDvd.setDirector("Wachowski");
        sampleDvd.setReleaseYear(1999);
        sampleDvd.setGenre("Sci-Fi");
        sampleDvd.setRentalRatePerDay(new BigDecimal("2.99"));
        sampleDvd.setQuantity(3);
        sampleDvd.setAvailableQuantity(2);
    }

    @Test
    public void testGetAllDvds() throws Exception {
        when(dvdService.findAll()).thenReturn(Arrays.asList(sampleDvd));

        mockMvc.perform(get("/dvds"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/list"))
                .andExpect(model().attributeExists("dvds"))
                .andExpect(model().attribute("dvds", Arrays.asList(sampleDvd)));
    }

    @Test
    public void testGetAllDvdsWithSearch() throws Exception {
        when(dvdService.searchByKeyword(anyString())).thenReturn(Arrays.asList(sampleDvd));

        mockMvc.perform(get("/dvds").param("search", "Matrix"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/list"))
                .andExpect(model().attributeExists("dvds"))
                .andExpect(model().attribute("dvds", Arrays.asList(sampleDvd)))
                .andExpect(model().attribute("search", "Matrix"));
    }

    @Test
    public void testGetAvailableDvds() throws Exception {
        when(dvdService.findAllAvailable()).thenReturn(Arrays.asList(sampleDvd));

        mockMvc.perform(get("/dvds/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/list"))
                .andExpect(model().attributeExists("dvds"))
                .andExpect(model().attribute("dvds", Arrays.asList(sampleDvd)))
                .andExpect(model().attribute("availableOnly", true));
    }

    @Test
    public void testGetUnavailableDvds() throws Exception {
        sampleDvd.setAvailableQuantity(0);
        when(dvdService.findAllUnavailable()).thenReturn(Arrays.asList(sampleDvd));

        mockMvc.perform(get("/dvds/unavailable"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/list"))
                .andExpect(model().attributeExists("dvds"))
                .andExpect(model().attribute("dvds", Arrays.asList(sampleDvd)))
                .andExpect(model().attribute("unavailableOnly", true));
    }

    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/dvds/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/form"))
                .andExpect(model().attributeExists("dvd"))
                .andExpect(model().attribute("isNew", true));
    }

    @Test
    public void testAddDvd() throws Exception {
        when(dvdService.save(any(DvdDto.class))).thenReturn(sampleDvd);

        mockMvc.perform(post("/dvds/add")
                        .param("title", "Matrix")
                        .param("director", "Wachowski")
                        .param("releaseYear", "1999")
                        .param("genre", "Sci-Fi")
                        .param("rentalRatePerDay", "2.99")
                        .param("quantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dvds"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        when(dvdService.findById(1L)).thenReturn(Optional.of(sampleDvd));

        mockMvc.perform(get("/dvds/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/form"))
                .andExpect(model().attributeExists("dvd"))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    public void testUpdateDvd() throws Exception {
        when(dvdService.save(any(DvdDto.class))).thenReturn(sampleDvd);

        mockMvc.perform(post("/dvds/edit/1")
                        .param("title", "Matrix Reloaded")
                        .param("director", "Wachowski")
                        .param("releaseYear", "2003")
                        .param("genre", "Sci-Fi")
                        .param("rentalRatePerDay", "3.99")
                        .param("quantity", "2")
                        .param("availableQuantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dvds"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowDvdDetails() throws Exception {
        when(dvdService.findById(1L)).thenReturn(Optional.of(sampleDvd));

        mockMvc.perform(get("/dvds/details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("dvd/details"))
                .andExpect(model().attributeExists("dvd"));
    }

    @Test
    public void testDeleteDvd() throws Exception {
        mockMvc.perform(get("/dvds/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dvds"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}