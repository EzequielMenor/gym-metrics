package com.ezequiel.ui;

import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.CsvEntrenamientoRepositorio;
import com.ezequiel.repository.EntrenamientoRepositorio;
import com.ezequiel.repository.JdbcEntreneRepo;

import java.util.List;

public class MigracionDatos {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO MIGRACIÓN DE DATOS CSV A POSTGRESQL ---");

        EntrenamientoRepositorio repoFuente = new CsvEntrenamientoRepositorio();
        EntrenamientoRepositorio repoDestino = new JdbcEntreneRepo();

        System.out.println("Leyendo datos del CSV...");
        List<Entrenamiento> datosCSV = repoFuente.obtenerTodos();
        System.out.println("Se han leido " + datosCSV.size() + " registros");

        System.out.println("Inciando escritura en la BBDD...");
        int contador = 0;
        for (Entrenamiento ent : datosCSV) {
            repoDestino.guardar(ent);
            contador++;
            if (contador % 100 == 0){
                System.out.println("Se han escrito " + contador + " registros");
            }
        }
        System.out.println("Migración Completada! " + contador + " registros");
    }
}
