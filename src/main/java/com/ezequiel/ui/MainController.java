package com.ezequiel.ui;

import com.ezequiel.logic.CalcularMetricasService;
import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.CsvEntrenamientoRepositorio;
import com.ezequiel.repository.EntrenamientoRepositorio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class MainController {

    @FXML
    private Label lblResultado;

    public void initialize() {
        EntrenamientoRepositorio repo = new CsvEntrenamientoRepositorio();
        List<Entrenamiento> entrenamientos = repo.obtenerTodos();

        if (!entrenamientos.isEmpty()) {
            CalcularMetricasService service = new CalcularMetricasService();
            String ejercicioBuscado = "Press de Banca (Barra)";
            double maxPeso = service.encontrarPesoMaximo(entrenamientos, ejercicioBuscado);

            lblResultado.setText("Peso maximo de " + ejercicioBuscado + ": " + maxPeso);
        } else {
            lblResultado.setText("No se encontro ese entrenamiento");
        }
    }
}
