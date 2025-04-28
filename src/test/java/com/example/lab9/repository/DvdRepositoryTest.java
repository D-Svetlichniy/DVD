package com.example.lab9.repository;

import com.example.lab9.model.Dvd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DvdRepositoryTest {

    @Autowired
    private DvdRepository dvdRepository;

    @Test
    public void testFindByTitleContainingIgnoreCase() {
        // Arrange
        Dvd dvd1 = new Dvd();
        dvd1.setTitle("Matrix");
        dvd1.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd1.setQuantity(2);
        dvd1.setAvailableQuantity(2);
        dvdRepository.save(dvd1);

        Dvd dvd2 = new Dvd();
        dvd2.setTitle("Matrix Reloaded");
        dvd2.setRentalRatePerDay(new BigDecimal("3.99"));
        dvd2.setQuantity(1);
        dvd2.setAvailableQuantity(1);
        dvdRepository.save(dvd2);

        Dvd dvd3 = new Dvd();
        dvd3.setTitle("Titanic");
        dvd3.setRentalRatePerDay(new BigDecimal("1.99"));
        dvd3.setQuantity(3);
        dvd3.setAvailableQuantity(3);
        dvdRepository.save(dvd3);

        // Act
        List<Dvd> result = dvdRepository.findByTitleContainingIgnoreCase("matrix");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getTitle().equals("Matrix")));
        assertTrue(result.stream().anyMatch(d -> d.getTitle().equals("Matrix Reloaded")));
    }

    @Test
    public void testFindByGenreIgnoreCase() {
        // Arrange
        Dvd dvd1 = new Dvd();
        dvd1.setTitle("Matrix");
        dvd1.setGenre("Sci-Fi");
        dvd1.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd1.setQuantity(2);
        dvd1.setAvailableQuantity(2);
        dvdRepository.save(dvd1);

        Dvd dvd2 = new Dvd();
        dvd2.setTitle("Avatar");
        dvd2.setGenre("sci-fi");
        dvd2.setRentalRatePerDay(new BigDecimal("3.99"));
        dvd2.setQuantity(1);
        dvd2.setAvailableQuantity(1);
        dvdRepository.save(dvd2);

        Dvd dvd3 = new Dvd();
        dvd3.setTitle("Titanic");
        dvd3.setGenre("Drama");
        dvd3.setRentalRatePerDay(new BigDecimal("1.99"));
        dvd3.setQuantity(3);
        dvd3.setAvailableQuantity(3);
        dvdRepository.save(dvd3);

        // Act
        List<Dvd> result = dvdRepository.findByGenreIgnoreCase("sci-fi");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getTitle().equals("Matrix")));
        assertTrue(result.stream().anyMatch(d -> d.getTitle().equals("Avatar")));
    }

    @Test
    public void testFindAllAvailable() {
        // Arrange
        Dvd dvd1 = new Dvd();
        dvd1.setTitle("Matrix");
        dvd1.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd1.setQuantity(2);
        dvd1.setAvailableQuantity(2);
        dvdRepository.save(dvd1);

        Dvd dvd2 = new Dvd();
        dvd2.setTitle("Avatar");
        dvd2.setRentalRatePerDay(new BigDecimal("3.99"));
        dvd2.setQuantity(1);
        dvd2.setAvailableQuantity(0);
        dvdRepository.save(dvd2);

        // Act
        List<Dvd> result = dvdRepository.findAllAvailable();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
    }

    @Test
    public void testSearchByKeyword() {
        // Arrange
        Dvd dvd1 = new Dvd();
        dvd1.setTitle("Matrix");
        dvd1.setDirector("Wachowski");
        dvd1.setGenre("Sci-Fi");
        dvd1.setRentalRatePerDay(new BigDecimal("2.99"));
        dvd1.setQuantity(2);
        dvd1.setAvailableQuantity(2);
        dvdRepository.save(dvd1);

        Dvd dvd2 = new Dvd();
        dvd2.setTitle("Avatar");
        dvd2.setDirector("James Cameron");
        dvd2.setGenre("Sci-Fi");
        dvd2.setRentalRatePerDay(new BigDecimal("3.99"));
        dvd2.setQuantity(1);
        dvd2.setAvailableQuantity(1);
        dvdRepository.save(dvd2);

        Dvd dvd3 = new Dvd();
        dvd3.setTitle("Titanic");
        dvd3.setDirector("James Cameron");
        dvd3.setGenre("Drama");
        dvd3.setRentalRatePerDay(new BigDecimal("1.99"));
        dvd3.setQuantity(3);
        dvd3.setAvailableQuantity(3);
        dvdRepository.save(dvd3);

        // Act - поиск по режиссеру
        List<Dvd> result1 = dvdRepository.searchByKeyword("cameron");

        // Assert
        assertEquals(2, result1.size());

        // Act - поиск по жанру
        List<Dvd> result2 = dvdRepository.searchByKeyword("sci-fi");

        // Assert
        assertEquals(2, result2.size());
    }
}