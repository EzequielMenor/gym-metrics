package com.ezequiel.repository;

import com.ezequiel.model.Entrenamiento;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JdbcEntreneRepo implements EntrenamientoRepositorio{
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/gym_metric_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "test";

    private Timestamp convertToTimestamp(String fechaString) {
        if (fechaString == null || fechaString.isEmpty()) {
            return null;
        }
        try {
            Locale locale = new Locale("es", "ES");
            DateFormatSymbols symbols = new DateFormatSymbols(locale);
            String[] shortMonths = {
                    "ene", "feb", "mar", "abr", "may", "jun",
                    "jul", "ago", "sep", "oct", "nov", "dic"
            };
            symbols.setShortMonths(shortMonths);
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy, HH:mm", locale);
            formatter.setDateFormatSymbols(symbols);

            java.util.Date utilDate = formatter.parse(fechaString);
            return new Timestamp(utilDate.getTime());
        }catch (ParseException e) {
            System.err.println("Error al parsear la fecha: " + fechaString + " - " + e.getMessage());
            return null;
        }
    }


    @Override
    public List<Entrenamiento> obtenerTodos() {
        List<Entrenamiento> entrenamientos = new ArrayList<>();
        String sql = "SELECT * FROM entrenamientos";
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Entrenamiento ent = new Entrenamiento();
                Timestamp ts = rs.getTimestamp("start_time");
                ent.setStartTime( (ts != null) ? ts.toString() : null );

                ts = rs.getTimestamp("end_time");
                ent.setEndTime( (ts != null) ? ts.toString() : null );

                ent.setSetType(rs.getString("set_type"));
                ent.setExerciseTitle(rs.getString("exercise_title"));
                ent.setWeightKg(rs.getDouble("weight_kg"));
                ent.setReps(rs.getInt("reps"));

                entrenamientos.add(ent);
            }
        }catch (SQLException e ){
            System.err.println("Error al conectar o consultar la BBDD: " + e.getMessage());
            e.printStackTrace();
        }
        return entrenamientos;
    }


    @Override
    public void guardar(Entrenamiento entrenamiento) {
        String sql = "INSERT INTO entrenamientos (start_time, end_time, set_type, weight_kg, reps, exercise_title) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, convertToTimestamp(entrenamiento.getStartTime()));
            ps.setTimestamp(2, convertToTimestamp(entrenamiento.getEndTime()));
            ps.setString(3, entrenamiento.getSetType());
            ps.setDouble(4, entrenamiento.getWeightKg());
            ps.setInt(5, entrenamiento.getReps());
            ps.setString(6, entrenamiento.getExerciseTitle());

            ps.executeUpdate();
        }catch (SQLException e ){
            System.err.println("Error al guardar el entrenamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
