package com.example.lab9.repository;

import com.example.lab9.model.Dvd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DvdRepository extends JpaRepository<Dvd, Long> {

    List<Dvd> findByTitleContainingIgnoreCase(String title);

    List<Dvd> findByGenreIgnoreCase(String genre);

    List<Dvd> findByDirectorContainingIgnoreCase(String director);

    @Query("SELECT d FROM Dvd d WHERE d.availableQuantity > 0")
    List<Dvd> findAllAvailable();

    @Query("SELECT d FROM Dvd d WHERE d.availableQuantity = 0")
    List<Dvd> findAllUnavailable();

    @Query("SELECT d FROM Dvd d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.director) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Dvd> searchByKeyword(String keyword);
}