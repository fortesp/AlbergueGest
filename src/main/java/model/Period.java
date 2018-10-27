package model;

import model.person.Companion;
import model.person.Patient;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "PERIODS")
public class Period implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "id_companion", nullable = true)
    private Companion companion;

    private LocalDate checkin;
    private LocalDate checkout;

    private String obs;

    public Period() {
    }

    public Period(Patient patient, LocalDate checkin) {

        this.patient = patient;
        this.checkin = checkin;
    }

    public Period(Patient patient, Companion companion, LocalDate checkin) {

        this.patient = patient;
        this.companion = companion;
        this.checkin = checkin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Companion getCompanion() {
        return companion;
    }

    public void setCompanion(Companion companion) {
        this.companion = companion;
    }

    public LocalDate getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String toString() {

        return "[" + getId() + "]" + getPatient().toString();
    }
}

