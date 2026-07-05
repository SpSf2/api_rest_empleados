package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.TelefonoDao;
import com.example.entities.Telefono;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelefonoServiceImpl implements TelefonoService {

    private final TelefonoDao telefonoDao;

    @Override
    public List<Telefono> findAll() {
        return telefonoDao.findAll();
    }

    @Override
    public Telefono findById(int id) {
        return telefonoDao.findById(id).orElse(null);
    }

    @Override
    public Telefono save(Telefono telefono) {
        return telefonoDao.save(telefono);
    }

    @Override
    public void delete(int id) {
        telefonoDao.deleteById(id);
    }
}