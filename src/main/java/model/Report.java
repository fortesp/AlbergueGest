package model;

import model.person.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "REPORTS")
public class Report implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="id_person", nullable=false)
    private Employee employee;
    private boolean detailed;
    private LocalDate date;

    public Report() { }

    public Report(Employee employee, LocalDate date) {

        this.employee = employee;
        this.date = date;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
