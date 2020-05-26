package at.htl.endpoint;

import at.htl.model.Student;
import at.htl.repository.StudentRepository;
import io.quarkus.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("students")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@Authenticated
public class StudentEndpoint {

    @Inject
    StudentRepository studentRepository;

    @GET
    @RolesAllowed({"user", "admin"})
    public List<Student> getAll() {
        return studentRepository.findAll().list();
    }

    @GET
    @Path("/{userid}")
    @RolesAllowed({"user", "admin"})
    public Student getStudent(@PathParam("userid") String userid) {
        return studentRepository.findById(userid);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Student s) {
        studentRepository.persist(s);
        // return Response.status(201).build();
        return Response.created(URI.create("/students/" + s.userid)).build();
    }

    @PUT
    @RolesAllowed("admin")
    public Response update(Student s) {
        Student sOld = studentRepository.findById(s.userid);
        sOld.lastname = s.lastname;
        sOld.firstname = s.firstname;

        return Response.status(204).build();
    }

    @DELETE
    @Path("/{userid}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("userid") String userid) {
        studentRepository.deleteById(userid);
        return Response.status(204).build();
    }
}
