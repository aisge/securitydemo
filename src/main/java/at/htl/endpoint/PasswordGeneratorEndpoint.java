package at.htl.endpoint;

import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.spec.EncryptablePasswordSpec;
import org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec;

import javax.annotation.security.DenyAll;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Base64;

@Path("passwords")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@DenyAll
public class PasswordGeneratorEndpoint {

    static final Provider ELYTRON_PROVIDER = new WildFlyElytronPasswordProvider();

    @GET
    @Path("/{password}")
    public String getStudent(@PathParam("password") String password) throws Exception {
        PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, ELYTRON_PROVIDER);

        int iterationCount = 10;

        byte[] salt = new byte[BCryptPassword.BCRYPT_SALT_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        IteratedSaltedPasswordAlgorithmSpec iteratedAlgorithmSpec = new IteratedSaltedPasswordAlgorithmSpec(iterationCount, salt);
        EncryptablePasswordSpec encryptableSpec = new EncryptablePasswordSpec(password.toCharArray(), iteratedAlgorithmSpec);

        BCryptPassword original = (BCryptPassword) passwordFactory.generatePassword(encryptableSpec);

        byte[] hash = original.getHash();

        Base64.Encoder encoder = Base64.getEncoder();
        JsonObject result = Json.createObjectBuilder()
                .add("salt", encoder.encodeToString(salt))
                .add("hash", encoder.encodeToString(hash))
                .build();

        return result.toString();
    }

}
