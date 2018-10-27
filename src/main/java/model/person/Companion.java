package model.person;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "PERSONS")
@DiscriminatorValue("2")
public class Companion extends Person {

    public Companion() {}

    public Companion(String name) {

        this(name, null, null);
    }

    public Companion(String name, LocalDate dob, String idcard) {
        super(name, dob, idcard);
    }
}
