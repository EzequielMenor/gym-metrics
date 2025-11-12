package com.ezequiel.repository;

import com.ezequiel.model.Entrenamiento;

import java.util.List;

public interface EntrenamientoRepositorio {
    List<Entrenamiento> obtenerTodos();

    void guardar(Entrenamiento entrenamiento);
}
