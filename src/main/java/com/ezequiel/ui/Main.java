package com.ezequiel.ui;

import com.ezequiel.logic.CalcularMetricasService;
import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.CsvEntrenamientoRepositorio;
import com.ezequiel.repository.EntrenamientoRepositorio;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntrenamientoRepositorio entrenamientoRepositorio = new CsvEntrenamientoRepositorio();
        List<Entrenamiento> entrenamientos = entrenamientoRepositorio.obtenerTodos();
        System.out.println("Todos los entranaminetos: " + entrenamientos.size());

        if (!entrenamientos.isEmpty()){
            System.out.println("Primer Ejercicio: " + entrenamientos.get(0).getExerciseTitle());

            CalcularMetricasService service = new CalcularMetricasService();
            String ejercicioBuscado = "Press de Banca (Barra)";
            double maxPeso = service.encontrarPesoMaximo(entrenamientos, ejercicioBuscado);
            System.out.println("Peso máximo en " + ejercicioBuscado + ": " + maxPeso);
        }else {
            System.out.println("No se encontraron entrenamientos. ¿Está el archivo 'workout_data.csv' en 'src/main/resources'?");
        }

    }
}
