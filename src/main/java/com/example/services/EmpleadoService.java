package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.entities.Empleado;

public interface EmpleadoService {

    Page<Empleado> findAll(Pageable pageable);

    List<Empleado> findAll(Sort sort);

    List<Empleado> findAll();

    Empleado findById(int id);

    Empleado save(Empleado empleado);

    void delete(int id);
}