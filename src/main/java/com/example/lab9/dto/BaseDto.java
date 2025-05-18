package com.example.lab9.dto;

import lombok.Data;

@Data
public abstract class BaseDto {
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}