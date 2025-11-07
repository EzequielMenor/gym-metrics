package com.ezequiel.model;

import com.opencsv.bean.CsvBindByName;

public class Entrenamiento {
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


    public String getExerciseTitle() {
        return exerciseTitle;
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
