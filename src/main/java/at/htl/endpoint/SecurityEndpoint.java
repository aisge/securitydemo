package at.htl.endpoint;

import at.htl.model.LoginData;
import at.htl.util.TokenUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Path("/security")
@RequestScoped
public class SecurityEndpoint {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String iss;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginData data) {
        try {
            if ("aisge".equals(data.getUsername()) && "geheim".equals(data.getPassword())) {
                return Response.ok(generateToken(data.getUsername())).build();
            }
        } catch (Exception e) {
            Logger.getAnonymousLogger().info(e.toString());
            e.printStackTrace();
        }
        return Response.status(401).build();
    }

    private String generateToken(String username) throws Exception {
        Map<String, Long> timeClaims = new HashMap<>();
        timeClaims.put(Claims.exp.name(), TokenUtils.currentTimeInSecs() + 120l);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.iss.name(), iss);
        claims.put(Claims.upn.name(), username);
        claims.put(Claims.groups.name(), Set.of(new String[]{"user"}));

        return TokenUtils.generateTokenString(claims, timeClaims);
    }

    @GET
    @Path("info")
    @PermitAll
    public Response getInfo(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName();
        boolean hasJWT = jwt.getClaimNames() != null;
        String result = String.format("user: %s, isSecure: %s, hasJWT: %s", name, ctx.isSecure(), hasJWT);
        return Response.ok(result).build();
    }
}
