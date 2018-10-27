package model;

import model.person.Person;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "SERVINGS")
public class Serving implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="id_person", nullable=false)
    private Person person;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="id_meal", nullable=false)
    private Meal meal;
    private LocalDate date;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {

        return getMeal().getName();
    }
}
