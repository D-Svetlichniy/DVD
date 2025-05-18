package com.example.lab9.service;

import com.example.dvdrentalservice.dto.DvdDto;

import java.util.List;

public interface DvdService extends BaseService<DvdDto, Long> {

    List<DvdDto> findByTitleContaining(String title);

    List<DvdDto> findByGenre(String genre);

    List<DvdDto> findByDirectorContaining(String director);

    List<DvdDto> findAllAvailable();

    List<DvdDto> findAllUnavailable();

    List<DvdDto> searchByKeyword(String keyword);
}