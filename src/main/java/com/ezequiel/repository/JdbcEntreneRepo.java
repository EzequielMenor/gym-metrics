package com.ezequiel.repository;

import com.ezequiel.model.Entrenamiento;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JdbcEntreneRepo implements EntrenamientoRepositorio {
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
        } catch (ParseException e) {
            System.err.println("Error al parsear la fecha: " + fechaString + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Entrenamiento> obtenerTodos() {
        List<Entrenamiento> entrenamientos = new ArrayList<>();
        String sql = "SELECT * FROM entrenamientos";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Entrenamiento ent = new Entrenamiento();

                Timestamp ts = rs.getTimestamp("start_time");
                ent.setStartTime((ts != null) ? ts.toString() : null);

                Timestamp tsEnd = rs.getTimestamp("end_time");
                ent.setEndTime((tsEnd != null) ? tsEnd.toString() : null);

                ent.setTitle(rs.getString("title"));
                ent.setDescription(rs.getString("description"));
                ent.setExerciseTitle(rs.getString("exercise_title"));
                ent.setSupersetId(rs.getString("superset_id"));
                ent.setExerciseNotes(rs.getString("exercise_notes"));
                ent.setSetType(rs.getString("set_type"));

                ent.setWeightKg(rs.getDouble("weight_kg"));
                ent.setReps(rs.getInt("reps"));
                ent.setSetIndex(rs.getInt("set_index"));
                ent.setDistanceKm(rs.getDouble("distance_km"));
                ent.setDurationSeconds(rs.getDouble("duration_seconds"));
                ent.setRpe(rs.getDouble("rpe"));

                entrenamientos.add(ent);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar o consultar la BBDD: " + e.getMessage());
            e.printStackTrace();
        }
        return entrenamientos;
    }

    @Override
    public void guardar(Entrenamiento entrenamiento) {
        String sql = "INSERT INTO entrenamientos (" +
                "title, start_time, end_time, description, exercise_title, " +
                "superset_id, exercise_notes, set_index, set_type, weight_kg, " +
                "reps, distance_km, duration_seconds, rpe" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1. title (VARCHAR)
            ps.setString(1, entrenamiento.getTitle());

            // 2. start_time (TIMESTAMP)
            ps.setTimestamp(2, convertToTimestamp(entrenamiento.getStartTime()));

            // 3. end_time (TIMESTAMP)
            ps.setTimestamp(3, convertToTimestamp(entrenamiento.getEndTime()));

            // 4. description (TEXT)
            ps.setString(4, entrenamiento.getDescription());

            // 5. exercise_title (VARCHAR)
            ps.setString(5, entrenamiento.getExerciseTitle());

            // 6. superset_id (VARCHAR)
            ps.setString(6, entrenamiento.getSupersetId());

            // 7. exercise_notes (TEXT)
            ps.setString(7, entrenamiento.getExerciseNotes());

            // 8. set_index (INTEGER)
            ps.setInt(8, entrenamiento.getSetIndex());

            // 9. set_type (VARCHAR)
            ps.setString(9, entrenamiento.getSetType());

            // 10. weight_kg (REAL/DOUBLE)
            ps.setDouble(10, entrenamiento.getWeightKg());

            // 11. reps (INTEGER)
            ps.setInt(11, entrenamiento.getReps());

            // 12. distance_km (REAL/DOUBLE)
            ps.setDouble(12, entrenamiento.getDistanceKm());

            // 13. duration_seconds (REAL/DOUBLE)
            ps.setDouble(13, entrenamiento.getDurationSeconds());

            // 14. rpe (REAL/DOUBLE)
            ps.setDouble(14, entrenamiento.getRpe());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar el entrenamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
