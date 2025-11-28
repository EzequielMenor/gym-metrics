package com.ezequiel.ui;

import com.ezequiel.logic.CalcularMetricasService;
import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.EntrenamientoRepositorio;
import com.ezequiel.repository.JdbcEntreneRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private LineChart<String, Number> chartProgreso;
    @FXML
    private ComboBox<String> cmbEjercicios;
    @FXML
    private CategoryAxis ejeXFechas;
    @FXML
    private NumberAxis ejeYPeso;
    @FXML
    private Label lblEst1RM;
    @FXML
    private Label lblMaxPeso;
    @FXML
    private Label lblMaxVolumenSet;
    @FXML
    private ComboBox<String> cmbSetType;

    List<Entrenamiento> entrenamientos;
    CalcularMetricasService service;

    public void initialize() {
        EntrenamientoRepositorio repo = new JdbcEntreneRepo();
        this.entrenamientos = repo.obtenerTodos();
        this.service = new CalcularMetricasService();

        List<String> ejerciciosUnicos = service.getEjerciciosUnicos(entrenamientos);
        ObservableList<String> observableList = FXCollections.observableList(ejerciciosUnicos);
        cmbEjercicios.setItems(observableList);

        ObservableList<String> tiposSet = FXCollections.observableArrayList(
                "Todos", "normal", "warmup", "failure", "drop_set");
        cmbSetType.setItems(tiposSet);
        cmbSetType.setValue("Todos");

        if (!entrenamientos.isEmpty()) {
            cmbEjercicios.setValue(observableList.get(0));
            actualizarMetricas();
        }

        ejeYPeso.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return String.format("%.1f kg", number.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
    }

    public void actualizarMetricas() {
        String ejercicioSeleccionado = cmbEjercicios.getValue();
        String tipoSetSeleccionado = cmbSetType.getValue();
        if (ejercicioSeleccionado == null || tipoSetSeleccionado == null || this.entrenamientos == null)
            return;

        List<Entrenamiento> listaFiltrada;

        if (tipoSetSeleccionado.equals("Todos")) {
            listaFiltrada = this.entrenamientos;
        } else {
            listaFiltrada = this.entrenamientos.stream()
                    .filter(e -> e.getSetType().equalsIgnoreCase(tipoSetSeleccionado))
                    .collect(Collectors.toList());
        }

        Map<String, Double> datosProgreso = service.getEjercicioPorFecha(listaFiltrada, ejercicioSeleccionado);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(ejercicioSeleccionado);

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");

        for (Map.Entry<String, Double> entry : datosProgreso.entrySet()) {
            String fechaFea = entry.getKey();
            Double peso = entry.getValue();
            String fechaBonita;

            try {
                Date date = parser.parse(fechaFea);
                fechaBonita = formatter.format(date);
            } catch (ParseException e) {
                fechaBonita = fechaFea;
                e.printStackTrace();
            }
            series.getData().add(new XYChart.Data<>(fechaBonita, peso));
        }

        chartProgreso.getData().clear();
        chartProgreso.getData().add(series);

        double maxPeso = this.service.encontrarPesoMaximo(listaFiltrada, ejercicioSeleccionado);
        lblMaxPeso.setText(String.format("%.1f kg", maxPeso));

        double est1RM = this.service.encontrar1RMEstimado(listaFiltrada, ejercicioSeleccionado);
        lblEst1RM.setText(String.format("%.1f kg", est1RM));

        String maxVolumen = this.service.encontrarMejorSetPorVolumen(listaFiltrada, ejercicioSeleccionado);
        lblMaxVolumenSet.setText(maxVolumen);
    }
}
