package com.example.covidmonitoringapp.data.model;

public class PatientDetails {
    private int id;
    private String name;
    private Double heart_rate;
    private Double respiratory_rate;
    private double fever_value;
    private boolean fever;
    private boolean nausea;
    private boolean headache;
    private boolean diarrhea;
    private boolean soar_throat;
    private boolean muscle_ache;
    private boolean lsat;
    private boolean cough;
    private boolean sob;
    private boolean feeling_tired;;

    public PatientDetails(String name, Double heart_rate, Double respiratory_rate, double fever_value, boolean fever, boolean nausea, boolean headache, boolean diarrhea, boolean soar_throat, boolean muscle_ache, boolean lsat, boolean cough, boolean sob, boolean feeling_tired) {

        this.name = name;
        this.heart_rate = heart_rate;
        this.respiratory_rate = respiratory_rate;
        this.fever_value = fever_value;
        this.fever = fever;
        this.nausea = nausea;
        this.headache = headache;
        this.diarrhea = diarrhea;
        this.soar_throat = soar_throat;
        this.muscle_ache = muscle_ache;
        this.lsat = lsat;
        this.cough = cough;
        this.sob = sob;
        this.feeling_tired = feeling_tired;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getHeart_rate() {
        return heart_rate;
    }

    public Double getRespiratory_rate() {
        return respiratory_rate;
    }

    public double getFever_value() {
        return fever_value;
    }

    public boolean isFever() {
        return fever;
    }

    public boolean isNausea() {
        return nausea;
    }

    public boolean isHeadache() {
        return headache;
    }

    public boolean isDiarrhea() {
        return diarrhea;
    }

    public boolean isSoar_throat() {
        return soar_throat;
    }

    public boolean isMuscle_ache() {
        return muscle_ache;
    }

    public boolean isLsat() {
        return lsat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeart_rate(Double heart_rate) {
        this.heart_rate = heart_rate;
    }

    public void setRespiratory_rate(Double respiratory_rate) {
        this.respiratory_rate = respiratory_rate;
    }

    public void setFever_value(int fever_value) {
        this.fever_value = fever_value;
    }

    public void setFever(boolean fever) {
        this.fever = fever;
    }

    public void setNausea(boolean nausea) {
        this.nausea = nausea;
    }

    public void setHeadache(boolean headache) {
        this.headache = headache;
    }

    public void setDiarrhea(boolean diarrhea) {
        this.diarrhea = diarrhea;
    }

    public void setSoar_throat(boolean soar_throat) {
        this.soar_throat = soar_throat;
    }

    public void setMuscle_ache(boolean muscle_ache) {
        this.muscle_ache = muscle_ache;
    }

    public void setLsat(boolean lsat) {
        this.lsat = lsat;
    }

    public void setCough(boolean cough) {
        this.cough = cough;
    }

    public void setSob(boolean sob) {
        this.sob = sob;
    }

    public void setFeeling_tired(boolean feeling_tired) {
        this.feeling_tired = feeling_tired;
    }

    public boolean isCough() {
        return cough;
    }

    public boolean isSob() {
        return sob;
    }

    public boolean isFeeling_tired() {
        return feeling_tired;
    }
}
