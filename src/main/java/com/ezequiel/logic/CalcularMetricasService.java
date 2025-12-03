package com.ezequiel.logic;

import com.ezequiel.model.Entrenamiento;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CalcularMetricasService {

    public double encontrarPesoMaximo(List<Entrenamiento> entrenamientoList, String nombreEjercicio) {
        return entrenamientoList.stream().filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio)).mapToDouble(Entrenamiento::getWeightKg).max().orElse(0.0);
    }

    public List<String> getEjerciciosUnicos(List<Entrenamiento> entrenamientos) {
        return entrenamientos.stream().map(Entrenamiento::getExerciseTitle).distinct().collect(Collectors.toList());
    }

    public Map<String, Double> getEjercicioPorFecha(List<Entrenamiento> entrenamientos, String nombreEjercicio) {
        return entrenamientos.stream()
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                .filter(e -> e.getStartTime() != null)
                .collect(Collectors.groupingBy(
                        Entrenamiento::getStartTime,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Entrenamiento::getWeightKg)),
                                optionalEntrenamiento -> optionalEntrenamiento
                                        .map(Entrenamiento::getWeightKg)
                                        .orElse(0.0)
                        )
                ));
    }

    /**
     * Calcula el 1RM (One-Rep Max) estimado para un ejercicio.
     * Utiliza la fórmula de Epley.
     */
    public double encontrar1RMEstimado(List<Entrenamiento> entrenamientos, String nombreEjercicio) {
        return entrenamientos.stream()
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                .mapToDouble(e -> e.getWeightKg() * (1 + (e.getReps() / 30.0)))
                .max()
                .orElse(0.0);
    }

    /**
     * Encuentra el set con el mayor volumen (Peso * Reps)
     * y lo devuelve como un String formateado (ej. "40kg x 10 reps").
     */
    public String encontrarMejorSetPorVolumen(List<Entrenamiento> entrenamientos, String nombreEjercicio) {
        return entrenamientos.stream()
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                .max(Comparator.comparingDouble(e -> e.getWeightKg() * e.getReps()))
                .map(e -> String.format("%.1f kg x %d reps", e.getWeightKg(), e.getReps()))
                .orElse("N/A");
    }

    /**
     * Calcula el 1RM (One-Rep Max) estimado por fecha para un ejercicio.
     * Utiliza la fórmula de Epley y agrupa por fecha, tomando el máximo 1RM para cada día.
     */
    public Map<String, Double> get1RMEstimadoPorFecha(List<Entrenamiento> entrenamientos, String nombreEjercicio) {
        return entrenamientos.stream()
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                .filter(e -> e.getStartTime() != null)
                .collect(Collectors.groupingBy(
                        Entrenamiento::getStartTime,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(e -> e.getWeightKg() * (1 + (e.getReps() / 30.0)))),
                                optionalEntrenamiento -> optionalEntrenamiento
                                        .map(e -> e.getWeightKg() * (1 + (e.getReps() / 30.0)))
                                        .orElse(0.0)
                        )
                ));
    }
}

