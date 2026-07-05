package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.CorreoDao;
import com.example.entities.Correo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements CorreoService {

    private final CorreoDao correoDao;

    @Override
    public List<Correo> findAll() {
        return correoDao.findAll();
    }

    @Override
    public Correo findById(int id) {
        return correoDao.findById(id).orElse(null);
    }

    @Override
    public Correo save(Correo correo) {
        return correoDao.save(correo);
    }

    @Override
    public void delete(int id) {
        correoDao.deleteById(id);
    }
}