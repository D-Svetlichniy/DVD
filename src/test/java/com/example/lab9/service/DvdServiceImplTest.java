package com.example.lab9.service;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.model.Dvd;
import com.example.lab9.repository.DvdRepository;
import com.example.lab9.service.impl.DvdServiceImpl;
import com.example.lab9.util.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DvdServiceImplTest {

    @Mock
    private DvdRepository dvdRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DvdServiceImpl dvdService;

    private Dvd dvd;
    private DvdDto dvdDto;

    @BeforeEach
    public void setUp() {
        dvd = new Dvd();
        dvd.setId(1L);
        dvd.setTitle("Matrix");
        dvd.setDirector("Wachowski");
        dvd.setReleaseYear(1999);
        dvd.setGenre("Sci-Fi");
        dvd.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd.setDescription("A computer programmer discovers a dystopian world");
        dvd.setQuantity(3);
        dvd.setAvailableQuantity(2);

        dvdDto = new DvdDto();
        dvdDto.setId(1L);
        dvdDto.setTitle("Matrix");
        dvdDto.setDirector("Wachowski");
        dvdDto.setReleaseYear(1999);
        dvdDto.setGenre("Sci-Fi");
        dvdDto.setRentalRatePerDay(new BigDecimal("2.99"));
        dvdDto.setDescription("A computer programmer discovers a dystopian world");
        dvdDto.setQuantity(3);
        dvdDto.setAvailableQuantity(2);
    }

    @Test
    public void testFindAll() {
        // Arrange
        List<Dvd> dvds = Arrays.asList(dvd);
        when(dvdRepository.findAll()).thenReturn(dvds);
        when(objectMapper.toDto(any(Dvd.class))).thenReturn(dvdDto);

        // Act
        List<DvdDto> result = dvdService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
        verify(dvdRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        // Arrange
        when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
        when(objectMapper.toDto(dvd)).thenReturn(dvdDto);

        // Act
        Optional<DvdDto> result = dvdService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Matrix", result.get().getTitle());
        verify(dvdRepository, times(1)).findById(1L);
    }

    @Test
    public void testSave() {
        // Arrange
        when(objectMapper.toEntity(dvdDto)).thenReturn(dvd);
        when(dvdRepository.save(dvd)).thenReturn(dvd);
        when(objectMapper.toDto(dvd)).thenReturn(dvdDto);

        // Act
        DvdDto result = dvdService.save(dvdDto);

        // Assert
        assertEquals("Matrix", result.getTitle());
        verify(dvdRepository, times(1)).save(dvd);
    }

    @Test
    public void testSaveNewDvd() {
        // Arrange
        dvd.setId(null);
        dvdDto.setId(null);
        dvdDto.setAvailableQuantity(null);

        when(objectMapper.toEntity(dvdDto)).thenReturn(dvd);
        when(dvdRepository.save(dvd)).thenReturn(dvd);
        when(objectMapper.toDto(dvd)).thenReturn(dvdDto);

        // Act
        DvdDto result = dvdService.save(dvdDto);

        // Assert
        verify(dvdRepository, times(1)).save(dvd);
    }

    @Test
    public void testFindByTitleContaining() {
        // Arrange
        List<Dvd> dvds = Arrays.asList(dvd);
        when(dvdRepository.findByTitleContainingIgnoreCase("Matrix")).thenReturn(dvds);
        when(objectMapper.toDto(any(Dvd.class))).thenReturn(dvdDto);

        // Act
        List<DvdDto> result = dvdService.findByTitleContaining("Matrix");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
        verify(dvdRepository, times(1)).findByTitleContainingIgnoreCase("Matrix");
    }

    @Test
    public void testFindAllAvailable() {
        // Arrange
        List<Dvd> dvds = Arrays.asList(dvd);
        when(dvdRepository.findAllAvailable()).thenReturn(dvds);
        when(objectMapper.toDto(any(Dvd.class))).thenReturn(dvdDto);

        // Act
        List<DvdDto> result = dvdService.findAllAvailable();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
        verify(dvdRepository, times(1)).findAllAvailable();
    }
}