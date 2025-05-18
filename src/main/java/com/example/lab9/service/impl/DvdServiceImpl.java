package com.example.lab9.service.impl;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.exception.ResourceNotFoundException;
import com.example.lab9.model.Dvd;
import com.example.lab9.repository.DvdRepository;
import com.example.lab9.service.DvdService;
import com.example.lab9.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DvdServiceImpl implements DvdService {

    private final DvdRepository dvdRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DvdServiceImpl(DvdRepository dvdRepository, ObjectMapper objectMapper) {
        this.dvdRepository = dvdRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findAll() {
        return dvdRepository.findAll().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DvdDto> findById(Long id) {
        return dvdRepository.findById(id)
                .map(objectMapper::toDto);
    }

    @Override
    public DvdDto save(DvdDto dvdDto) {
        Dvd dvd = objectMapper.toEntity(dvdDto);

        // Если это новый DVD, устанавливаем доступное количество равным общему количеству
        if (dvd.getId() == null && dvd.getAvailableQuantity() == null) {
            dvd.setAvailableQuantity(dvd.getQuantity());
        }

        dvd = dvdRepository.save(dvd);
        return objectMapper.toDto(dvd);
    }

    @Override
    public void deleteById(Long id) {
        if (!dvdRepository.existsById(id)) {
            throw new ResourceNotFoundException("DVD", "id", id);
        }
        dvdRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findByTitleContaining(String title) {
        return dvdRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findByGenre(String genre) {
        return dvdRepository.findByGenreIgnoreCase(genre).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findByDirectorContaining(String director) {
        return dvdRepository.findByDirectorContainingIgnoreCase(director).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findAllAvailable() {
        return dvdRepository.findAllAvailable().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> findAllUnavailable() {
        return dvdRepository.findAllUnavailable().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DvdDto> searchByKeyword(String keyword) {
        return dvdRepository.searchByKeyword(keyword).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }
}