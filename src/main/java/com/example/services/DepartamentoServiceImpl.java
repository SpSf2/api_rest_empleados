package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.DepartamentoDao;
import com.example.entities.Departamento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoDao departamentoDao;

    @Override
    public List<Departamento> findAll() {
        return departamentoDao.findAll();
    }

    @Override
    public Departamento findById(int id) {
        return departamentoDao.findById(id).orElse(null);
    }

    @Override
    public Departamento save(Departamento departamento) {
        return departamentoDao.save(departamento);
    }

    @Override
    public void delete(int id) {
        departamentoDao.deleteById(id);
    }
}