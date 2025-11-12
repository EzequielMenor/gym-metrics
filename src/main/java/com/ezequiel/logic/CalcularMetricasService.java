package com.ezequiel.logic;

import com.ezequiel.model.Entrenamiento;

import java.util.List;
import java.util.stream.Collectors;

public class CalcularMetricasService {

    public double encontrarPesoMaximo(List<Entrenamiento> entrenamientoList, String nombreEjercicio) {
        return entrenamientoList.stream().filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio)).mapToDouble(Entrenamiento::getWeightKg).max().orElse(0.0);
    }

    public List<String> getEjerciciosUnicos(List<Entrenamiento> entrenamientos) {
        return entrenamientos.stream().map(Entrenamiento::getExerciseTitle).distinct().collect(Collectors.toList());
    }

}
