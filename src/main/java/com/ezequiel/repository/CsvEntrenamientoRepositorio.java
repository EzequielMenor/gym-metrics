package com.ezequiel.repository;

import com.ezequiel.model.Entrenamiento;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

public class CsvEntrenamientoRepositorio implements EntrenamientoRepositorio {
    private final String CSV_FILE = "workout_data.csv";

    @Override
    public List<Entrenamiento> obtenerTodos() {
        try (Reader reader = new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(CSV_FILE))) {

            return new CsvToBeanBuilder<Entrenamiento>(reader).withType(Entrenamiento.class).build().parse();

        } catch (Exception e) {
            System.err.println("Error grave al leer el archivo CSV: " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public void guardar(Entrenamiento entrenamiento) {
        System.err.println("No implementado");
    }
}