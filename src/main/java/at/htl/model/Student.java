package at.htl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Student extends PanacheEntityBase {
    @Id
    public String userid;

    public String firstname;
    public String lastname;
}
