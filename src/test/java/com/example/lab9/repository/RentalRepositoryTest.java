package com.example.lab9.repository;

import com.example.lab9.model.Dvd;
import com.example.lab9.model.Rental;
import com.example.lab9.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private DvdRepository dvdRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Dvd testDvd1;
    private Dvd testDvd2;

    @BeforeEach
    public void setup() {
        // Очистка данных
        rentalRepository.deleteAll();
        dvdRepository.deleteAll();
        userRepository.deleteAll();

        // Создание тестовых пользователей
        testUser1 = new User();
        testUser1.setName("Test User 1");
        testUser1.setEmail("test1@example.com");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setName("Test User 2");
        testUser2.setEmail("test2@example.com");
        testUser2 = userRepository.save(testUser2);

        // Создание тестовых DVD
        testDvd1 = new Dvd();
        testDvd1.setTitle("Test DVD 1");
        testDvd1.setRentalRatePerDay(new BigDecimal("2.99"));
        testDvd1.setQuantity(2);
        testDvd1.setAvailableQuantity(2);
        testDvd1 = dvdRepository.save(testDvd1);

        testDvd2 = new Dvd();
        testDvd2.setTitle("Test DVD 2");
        testDvd2.setRentalRatePerDay(new BigDecimal("3.99"));
        testDvd2.setQuantity(1);
        testDvd2.setAvailableQuantity(1);
        testDvd2 = dvdRepository.save(testDvd2);
    }

    @Test
    public void testFindByUserIdOrderByRentalDateDesc() {
        // Arrange
        Rental rental1 = new Rental();
        rental1.setUser(testUser1);
        rental1.setDvd(testDvd1);
        rental1.setRentalDate(LocalDate.now().minusDays(2));
        rental1.setDueDate(LocalDate.now().plusDays(5));
        rental1.setTotalCost(new BigDecimal("14.95"));
        rental1.setReturned(false);
        rentalRepository.save(rental1);

        Rental rental2 = new Rental();
        rental2.setUser(testUser1);
        rental2.setDvd(testDvd2);
        rental2.setRentalDate(LocalDate.now());
        rental2.setDueDate(LocalDate.now().plusDays(7));
        rental2.setTotalCost(new BigDecimal("27.93"));
        rental2.setReturned(false);
        rentalRepository.save(rental2);

        Rental rental3 = new Rental();
        rental3.setUser(testUser2);
        rental3.setDvd(testDvd1);
        rental3.setRentalDate(LocalDate.now().minusDays(1));
        rental3.setDueDate(LocalDate.now().plusDays(6));
        rental3.setTotalCost(new BigDecimal("20.93"));
        rental3.setReturned(false);
        rentalRepository.save(rental3);

        // Act
        List<Rental> result = rentalRepository.findByUserIdOrderByRentalDateDesc(testUser1.getId());

        // Assert
        assertEquals(2, result.size());
        assertEquals(LocalDate.now(), result.get(0).getRentalDate());
        assertEquals(LocalDate.now().minusDays(2), result.get(1).getRentalDate());
    }

    @Test
    public void testFindByIsReturnedFalse() {
        // Arrange
        Rental rental1 = new Rental();
        rental1.setUser(testUser1);
        rental1.setDvd(testDvd1);
        rental1.setRentalDate(LocalDate.now().minusDays(2));
        rental1.setDueDate(LocalDate.now().plusDays(5));
        rental1.setTotalCost(new BigDecimal("14.95"));
        rental1.setReturned(false);
        rentalRepository.save(rental1);

        Rental rental2 = new Rental();
        rental2.setUser(testUser2);
        rental2.setDvd(testDvd2);
        rental2.setRentalDate(LocalDate.now().minusDays(5));
        rental2.setDueDate(LocalDate.now().minusDays(1));
        rental2.setReturnDate(LocalDate.now().minusDays(2));
        rental2.setTotalCost(new BigDecimal("19.95"));
        rental2.setReturned(true);
        rentalRepository.save(rental2);

        // Act
        List<Rental> result = rentalRepository.findByIsReturnedFalse();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser1.getId(), result.get(0).getUser().getId());
        assertEquals(testDvd1.getId(), result.get(0).getDvd().getId());
    }

    @Test
    public void testFindOverdueRentals() {
        // Arrange
        Rental rental1 = new Rental();
        rental1.setUser(testUser1);
        rental1.setDvd(testDvd1);
        rental1.setRentalDate(LocalDate.now().minusDays(10));
        rental1.setDueDate(LocalDate.now().minusDays(3));
        rental1.setTotalCost(new BigDecimal("14.95"));
        rental1.setReturned(false);
        rentalRepository.save(rental1);

        Rental rental2 = new Rental();
        rental2.setUser(testUser2);
        rental2.setDvd(testDvd2);
        rental2.setRentalDate(LocalDate.now().minusDays(2));
        rental2.setDueDate(LocalDate.now().plusDays(5));
        rental2.setTotalCost(new BigDecimal("19.95"));
        rental2.setReturned(false);
        rentalRepository.save(rental2);

        // Act
        List<Rental> result = rentalRepository.findOverdueRentals();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser1.getId(), result.get(0).getUser().getId());
        assertEquals(testDvd1.getId(), result.get(0).getDvd().getId());
        assertTrue(result.get(0).getDueDate().isBefore(LocalDate.now()));
    }

    @Test
    public void testFindRentalsBetweenDates() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(1);

        Rental rental1 = new Rental();
        rental1.setUser(testUser1);
        rental1.setDvd(testDvd1);
        rental1.setRentalDate(LocalDate.now().minusDays(3));
        rental1.setDueDate(LocalDate.now().plusDays(4));
        rental1.setTotalCost(new BigDecimal("14.95"));
        rental1.setReturned(false);
        rentalRepository.save(rental1);

        Rental rental2 = new Rental();
        rental2.setUser(testUser2);
        rental2.setDvd(testDvd2);
        rental2.setRentalDate(LocalDate.now());
        rental2.setDueDate(LocalDate.now().plusDays(7));
        rental2.setTotalCost(new BigDecimal("19.95"));
        rental2.setReturned(false);
        rentalRepository.save(rental2);

        // Act
        List<Rental> result = rentalRepository.findRentalsBetweenDates(startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser1.getId(), result.get(0).getUser().getId());
        assertEquals(testDvd1.getId(), result.get(0).getDvd().getId());
    }
}