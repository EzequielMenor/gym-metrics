package com.ezequiel.model;

import com.opencsv.bean.CsvBindByName;

public class Entrenamiento {
    @CsvBindByName(column = "title")
    private String title;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "superset_id")
    private String supersetId;

    @CsvBindByName(column = "exercise_notes")
    private String exerciseNotes;

    @CsvBindByName(column = "set_index")
    private int setIndex;

    @CsvBindByName(column = "distance_km")
    private double distanceKm;

    @CsvBindByName(column = "duration_seconds")
    private double durationSeconds;

    @CsvBindByName(column = "rpe")
    private double rpe;

    @CsvBindByName(column = "start_time")
    private String startTime;

    @CsvBindByName(column = "end_time")
    private String endTime;

    @CsvBindByName(column = "set_type")
    private String setType;

    @CsvBindByName(column = "weight_kg")
    private double weightKg;

    @CsvBindByName(column = "reps")
    private int reps;

    @CsvBindByName(column = "exercise_title")
    private String exerciseTitle;

    public Entrenamiento() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSupersetId() {
        return supersetId;
    }

    public String getExerciseNotes() {
        return exerciseNotes;
    }

    public int getSetIndex() {
        return setIndex;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public double getRpe() {
        return rpe;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSetType() {
        return setType;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public int getReps() {
        return reps;
    }

    public String getExerciseTitle() {
        return exerciseTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSupersetId(String supersetId) {
        this.supersetId = supersetId;
    }

    public void setExerciseNotes(String exerciseNotes) {
        this.exerciseNotes = exerciseNotes;
    }

    public void setSetIndex(int setIndex) {
        this.setIndex = setIndex;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public void setDurationSeconds(double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setRpe(double rpe) {
        this.rpe = rpe;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setExerciseTitle(String exerciseTitle) {
        this.exerciseTitle = exerciseTitle;
    }
}
