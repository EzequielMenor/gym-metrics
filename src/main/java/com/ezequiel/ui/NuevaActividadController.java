package com.ezequiel.ui;

import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.EntrenamientoRepositorio;
import com.ezequiel.repository.JdbcEntreneRepo;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NuevaActividadController {

    @FXML
    private TextField txtEjercicio;
    @FXML
    private TextField txtDistancia;
    @FXML
    private TextField txtDuracion;
    @FXML
    private DatePicker datePickerFecha;

    private EntrenamientoRepositorio repo = new JdbcEntreneRepo();

    public void guardarActividad() {
        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setExerciseTitle(txtEjercicio.getText());
        entrenamiento.setDistanceKm(Double.parseDouble(txtDistancia.getText()));
        entrenamiento.setDurationSeconds(Double.parseDouble(txtDuracion.getText()) * 60);
        entrenamiento.setWeightKg(0);

        if (datePickerFecha.getValue() != null) {
            String formattedDate = datePickerFecha.getValue().atStartOfDay().format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", new Locale("es", "ES")));
            entrenamiento.setStartTime(formattedDate);
        }

        repo.guardar(entrenamiento);
        
        // Close the window after saving
        txtEjercicio.getScene().getWindow().hide();
    }
}
