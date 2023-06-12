package at.htl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Student extends PanacheEntityBase {
    @Id
    public String userid;

    public String firstname;
    public String lastname;
}
