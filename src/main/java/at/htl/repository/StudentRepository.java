package at.htl.repository;

import at.htl.model.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class StudentRepository implements PanacheRepositoryBase<Student, String> {
}
