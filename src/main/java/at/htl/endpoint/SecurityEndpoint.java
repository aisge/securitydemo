package at.htl.endpoint;

import at.htl.model.LoginData;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

@Path("/security")
public class SecurityEndpoint {

    // Demo-User: username -> {password, rollen}. In echt: DB + Passwort-Hash!
    private static final Map<String, String> PASSWORDS = Map.of(
            "max", "passme",
            "susi", "passme");
    private static final Map<String, Set<String>> ROLES = Map.of(
            "max", Set.of("admin", "user"),
            "susi", Set.of("user"));

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(LoginData data) {
        if (data != null && data.password() != null
                && data.password().equals(PASSWORDS.get(data.username()))) {
            return Response.ok(generateToken(data.username(), ROLES.get(data.username()))).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    /**
     * Signiert mit dem Key aus smallrye.jwt.sign.key.location,
     * Gültigkeit aus smallrye.jwt.new-token.lifespan.
     */
    private String generateToken(String username, Set<String> roles) {
        return Jwt.issuer(issuer)
                .upn(username)
                .groups(roles)              // Rollen für @RolesAllowed
                .claim(Claims.birthdate.name(), "2001-02-13")
                .sign();
    }

    @GET
    @Path("info")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String getInfo(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName(); // upn
        boolean hasJWT = jwt.getRawToken() != null;
        return String.format("user: %s, isSecure: %s, hasJWT: %s, groups: %s",
                name, ctx.isSecure(), hasJWT, jwt.getGroups());
    }
}
