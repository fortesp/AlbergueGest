package model.person;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "PERSONS")
@DiscriminatorValue("3")
public class Employee extends Person {

    public Employee() {}

    public Employee(String name) {}

    public Employee(String name, LocalDate dob, String idcard) {
        super(name, dob, idcard);
    }
}
