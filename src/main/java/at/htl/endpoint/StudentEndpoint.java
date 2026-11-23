package at.htl.endpoint;

import at.htl.model.Student;
import at.htl.repository.StudentRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.net.URI;
import java.util.List;

@Path("students")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class StudentEndpoint {

    @Inject
    StudentRepository studentRepository;

    @GET
    @RolesAllowed("user")
    public List<Student> getAll() {
        return studentRepository.listAll();
    }

    @GET
    @Path("/{userid}")
    @RolesAllowed("user")
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
    @RolesAllowed("admin")
    @Path("/{userid}")
    public Response delete(@PathParam("userid") String userid) {
        studentRepository.deleteById(userid);
        return Response.status(204).build();
    }
}
