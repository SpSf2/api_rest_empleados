package com.example.services;

import java.util.List;

import com.example.entities.Telefono;

public interface TelefonoService {

    List<Telefono> findAll();

    Telefono findById(int id);

    Telefono save(Telefono telefono);

    void delete(int id);
}