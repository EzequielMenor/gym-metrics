package com.ezequiel.ui;

import com.ezequiel.logic.CalcularMetricasService;
import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.CsvEntrenamientoRepositorio;
import com.ezequiel.repository.EntrenamientoRepositorio;
import com.ezequiel.repository.JdbcEntreneRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;

public class MainController {

    @FXML
    private Label lblResultado;
    @FXML
    private ComboBox<String> cmbEjercicios;

    List<Entrenamiento> entrenamientos;
    CalcularMetricasService service;

    public void initialize() {
        EntrenamientoRepositorio repo = new JdbcEntreneRepo();
        this.entrenamientos = repo.obtenerTodos();
        this.service = new CalcularMetricasService();

        List<String> ejerciciosUnicos = service.getEjerciciosUnicos(entrenamientos);
        ObservableList<String> observableList = FXCollections.observableList(ejerciciosUnicos);
        cmbEjercicios.setItems(observableList);

        if (!entrenamientos.isEmpty()) {
            cmbEjercicios.setValue(observableList.get(0));
            actualizarMetricas();
        } else {
            lblResultado.setText("No se encontro ese entrenamiento");
        }
    }

    public void actualizarMetricas(){
        String ejercicioSeleccionado = cmbEjercicios.getValue();

        if (ejercicioSeleccionado != null && this.entrenamientos != null) {
            double maxPeso = this.service.encontrarPesoMaximo(this.entrenamientos, ejercicioSeleccionado);
            lblResultado.setText("Peso máximo de " + ejercicioSeleccionado + " : " + maxPeso + " kg");
        }
    }
}
