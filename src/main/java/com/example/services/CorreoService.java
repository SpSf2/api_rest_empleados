package com.example.services;

import java.util.List;

import com.example.entities.Correo;

public interface CorreoService {

    List<Correo> findAll();

    Correo findById(int id);

    Correo save(Correo correo);

    void delete(int id);
}