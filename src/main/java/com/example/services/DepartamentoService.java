package com.example.services;

import java.util.List;

import com.example.entities.Departamento;

public interface DepartamentoService {

    List<Departamento> findAll();

    Departamento findById(int id);

    Departamento save(Departamento departamento);

    void delete(int id);
}
