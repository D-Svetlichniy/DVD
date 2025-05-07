package com.example.lab9.controller;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.dto.RentalCreateDto;
import com.example.lab9.dto.RentalDto;
import com.example.lab9.dto.UserDto;
import com.example.lab9.service.DvdService;
import com.example.lab9.service.RentalService;
import com.example.lab9.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private DvdService dvdService;

    @MockBean
    private UserService userService;

    private RentalDto sampleRental;
    private DvdDto sampleDvd;
    private UserDto sampleUser;

    @BeforeEach
    public void setUp() {
        sampleUser = new UserDto();
        sampleUser.setId(1L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");

        sampleDvd = new DvdDto();
        sampleDvd.setId(1L);
        sampleDvd.setTitle("Matrix");
        sampleDvd.setRentalRatePerDay(new BigDecimal("2.99"));
        sampleDvd.setQuantity(3);
        sampleDvd.setAvailableQuantity(2);

        sampleRental = new RentalDto();
        sampleRental.setId(1L);
        sampleRental.setDvdId(1L);
        sampleRental.setDvdTitle("Matrix");
        sampleRental.setUserId(1L);
        sampleRental.setUserName("John Doe");
        sampleRental.setRentalDate(LocalDate.now());
        sampleRental.setDueDate(LocalDate.now().plusDays(3));
        sampleRental.setTotalCost(new BigDecimal("8.97"));
        sampleRental.setReturned(false);
    }

    @Test
    public void testGetAllRentals() throws Exception {
        when(rentalService.findAll()).thenReturn(Arrays.asList(sampleRental));

        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/list"))
                .andExpect(model().attributeExists("rentals"))
                .andExpect(model().attribute("rentals", Arrays.asList(sampleRental)));
    }

    @Test
    public void testGetActiveRentals() throws Exception {
        when(rentalService.findAllActive()).thenReturn(Arrays.asList(sampleRental));

        mockMvc.perform(get("/rentals/active"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/list"))
                .andExpect(model().attributeExists("rentals"))
                .andExpect(model().attribute("rentals", Arrays.asList(sampleRental)))
                .andExpect(model().attribute("activeOnly", true));
    }

    @Test
    public void testGetReturnedRentals() throws Exception {
        sampleRental.setReturned(true);
        sampleRental.setReturnDate(LocalDate.now());
        when(rentalService.findAllReturned()).thenReturn(Arrays.asList(sampleRental));

        mockMvc.perform(get("/rentals/returned"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/list"))
                .andExpect(model().attributeExists("rentals"))
                .andExpect(model().attribute("rentals", Arrays.asList(sampleRental)))
                .andExpect(model().attribute("returnedOnly", true));
    }

    @Test
    public void testGetOverdueRentals() throws Exception {
        when(rentalService.findOverdueRentals()).thenReturn(Arrays.asList(sampleRental));

        mockMvc.perform(get("/rentals/overdue"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/list"))
                .andExpect(model().attributeExists("rentals"))
                .andExpect(model().attribute("rentals", Arrays.asList(sampleRental)))
                .andExpect(model().attribute("overdueOnly", true));
    }

    @Test
    public void testShowRentForm() throws Exception {
        when(dvdService.findAllAvailable()).thenReturn(Arrays.asList(sampleDvd));
        when(userService.findAll()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/rentals/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/form"))
                .andExpect(model().attributeExists("rental"))
                .andExpect(model().attributeExists("availableDvds"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testShowRentFormWithDvdId() throws Exception {
        when(dvdService.findAllAvailable()).thenReturn(Arrays.asList(sampleDvd));
        when(userService.findAll()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/rentals/new").param("dvdId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/form"))
                .andExpect(model().attributeExists("rental"))
                .andExpect(model().attributeExists("availableDvds"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testRentDvd() throws Exception {
        when(rentalService.rentDvd(any(RentalCreateDto.class))).thenReturn(sampleRental);

        mockMvc.perform(post("/rentals/new")
                        .param("dvdId", "1")
                        .param("userId", "1")
                        .param("rentalDays", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rentals"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowReturnForm() throws Exception {
        when(rentalService.findById(1L)).thenReturn(Optional.of(sampleRental));

        mockMvc.perform(get("/rentals/return/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/return"))
                .andExpect(model().attributeExists("rental"));
    }

    @Test
    public void testReturnDvd() throws Exception {
        when(rentalService.returnDvd(1L)).thenReturn(sampleRental);

        mockMvc.perform(post("/rentals/return/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rentals"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    public void testShowRentalDetails() throws Exception {
        when(rentalService.findById(1L)).thenReturn(Optional.of(sampleRental));

        mockMvc.perform(get("/rentals/details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rental/details"))
                .andExpect(model().attributeExists("rental"));
    }
}