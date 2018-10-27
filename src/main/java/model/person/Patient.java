package model.person;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "PERSONS")
@DiscriminatorValue("1")
public class Patient extends Person {

    public Patient() {}

    public Patient(String name, LocalDate dob, String idcard) {
        super(name, dob, idcard);
    }
}
