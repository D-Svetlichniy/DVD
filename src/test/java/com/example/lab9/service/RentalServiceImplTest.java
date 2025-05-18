package com.example.lab9.service;

import com.example.lab9.dto.RentalCreateDto;
import com.example.lab9.dto.RentalDto;
import com.example.lab9.exception.BusinessException;
import com.example.lab9.exception.ResourceNotFoundException;
import com.example.lab9.model.Dvd;
import com.example.lab9.model.Rental;
import com.example.lab9.model.User;
import com.example.lab9.repository.DvdRepository;
import com.example.lab9.repository.RentalRepository;
import com.example.lab9.repository.UserRepository;
import com.example.lab9.service.impl.RentalServiceImpl;
import com.example.lab9.util.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private DvdRepository dvdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Dvd dvd;
    private User user;
    private Rental rental;
    private RentalDto rentalDto;
    private RentalCreateDto rentalCreateDto;

    @BeforeEach
    public void setUp() {
        dvd = new Dvd();
        dvd.setId(1L);
        dvd.setTitle("Matrix");
        dvd.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd.setQuantity(3);
        dvd.setAvailableQuantity(2);

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        rental = new Rental();
        rental.setId(1L);
        rental.setDvd(dvd);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(3));
        rental.setTotalCost(new BigDecimal("8.97"));
        rental.setReturned(false);

        rentalDto = new RentalDto();
        rentalDto.setId(1L);
        rentalDto.setDvdId(1L);
        rentalDto.setDvdTitle("Matrix");
        rentalDto.setUserId(1L);
        rentalDto.setUserName("John Doe");
        rentalDto.setRentalDate(LocalDate.now());
        rentalDto.setDueDate(LocalDate.now().plusDays(3));
        rentalDto.setTotalCost(new BigDecimal("8.97"));
        rentalDto.setReturned(false);

        rentalCreateDto = new RentalCreateDto();
        rentalCreateDto.setDvdId(1L);
        rentalCreateDto.setUserId(1L);
        rentalCreateDto.setRentalDays(3);
    }

    @Test
    public void testFindAll() {
        // Arrange
        List<Rental> rentals = Arrays.asList(rental);
        when(rentalRepository.findAll()).thenReturn(rentals);
        when(objectMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        // Act
        List<RentalDto> result = rentalService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getDvdTitle());
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(objectMapper.toDto(rental)).thenReturn(rentalDto);

        // Act
        Optional<RentalDto> result = rentalService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Matrix", result.get().getDvdTitle());
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    public void testRentDvd_Success() {
        // Arrange
        when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(objectMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        // Act
        RentalDto result = rentalService.rentDvd(rentalCreateDto);

        // Assert
        assertEquals("Matrix", result.getDvdTitle());
        assertEquals("John Doe", result.getUserName());
        verify(dvdRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(dvdRepository, times(1)).save(dvd);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    public void testRentDvd_DvdNotFound() {
        // Arrange
        when(dvdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.rentDvd(rentalCreateDto));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    public void testRentDvd_UserNotFound() {
        // Arrange
        when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.rentDvd(rentalCreateDto));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    public void testRentDvd_NoDvdAvailable() {
        // Arrange
        dvd.setAvailableQuantity(0);
        when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));

        // Act & Assert
        assertThrows(BusinessException.class, () -> rentalService.rentDvd(rentalCreateDto));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    public void testReturnDvd_Success() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(objectMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        // Act
        RentalDto result = rentalService.returnDvd(1L);

        // Assert
        assertTrue(rental.isReturned());
        assertNotNull(rental.getReturnDate());
        assertEquals(3, dvd.getAvailableQuantity());
        verify(rentalRepository, times(1)).findById(1L);
        verify(dvdRepository, times(1)).save(dvd);
        verify(rentalRepository, times(1)).save(rental);
    }

    @Test
    public void testReturnDvd_RentalNotFound() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.returnDvd(1L));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    public void testReturnDvd_AlreadyReturned() {
        // Arrange
        rental.setReturned(true);
        rental.setReturnDate(LocalDate.now().minusDays(1));
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        // Act & Assert
        assertThrows(BusinessException.class, () -> rentalService.returnDvd(1L));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    public void testFindByUserId() {
        // Arrange
        List<Rental> rentals = Arrays.asList(rental);
        when(rentalRepository.findByUserIdOrderByRentalDateDesc(1L)).thenReturn(rentals);
        when(objectMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        // Act
        List<RentalDto> result = rentalService.findByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getDvdTitle());
        verify(rentalRepository, times(1)).findByUserIdOrderByRentalDateDesc(1L);
    }

    @Test
    public void testFindAllActive() {
        // Arrange
        List<Rental> rentals = Arrays.asList(rental);
        when(rentalRepository.findByIsReturnedFalse()).thenReturn(rentals);
        when(objectMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        // Act
        List<RentalDto> result = rentalService.findAllActive();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getDvdTitle());
        verify(rentalRepository, times(1)).findByIsReturnedFalse();
    }
}