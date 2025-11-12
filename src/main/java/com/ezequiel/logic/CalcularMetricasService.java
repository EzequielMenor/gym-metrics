package com.ezequiel.logic;

import com.ezequiel.model.Entrenamiento;

import java.util.List;

public class CalcularMetricasService {

    public double encontrarPesoMaximo(List<Entrenamiento> entrenamientoList, String nombreEjercicio) {
        return entrenamientoList.stream().filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio)).mapToDouble(Entrenamiento::getWeightKg).max().orElse(0.0);

    }
}
