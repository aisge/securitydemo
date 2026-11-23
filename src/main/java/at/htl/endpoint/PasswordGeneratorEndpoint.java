package at.htl.endpoint;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.spec.EncryptablePasswordSpec;
import org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec;

import java.security.Provider;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * Hilfs-Endpoint zum Erzeugen von bcrypt-Hashes für die Tabelle quarkus_user.
 * NUR ZUM ÜBEN: Passwörter gehören nie in eine URL (Logs, Browser-History)!
 * In echten Projekten diesen Endpoint entfernen.
 */
@Path("passwords")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class PasswordGeneratorEndpoint {

    static final Provider ELYTRON_PROVIDER = new WildFlyElytronPasswordProvider();

    @GET
    @Path("/{password}")
    public Map<String, String> hashPassword(@PathParam("password") String password) throws Exception {
        PasswordFactory passwordFactory =
                PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, ELYTRON_PROVIDER);

        int iterationCount = 10;

        byte[] salt = new byte[BCryptPassword.BCRYPT_SALT_SIZE];
        new SecureRandom().nextBytes(salt);

        IteratedSaltedPasswordAlgorithmSpec iteratedAlgorithmSpec =
                new IteratedSaltedPasswordAlgorithmSpec(iterationCount, salt);
        EncryptablePasswordSpec encryptableSpec =
                new EncryptablePasswordSpec(password.toCharArray(), iteratedAlgorithmSpec);

        BCryptPassword original = (BCryptPassword) passwordFactory.generatePassword(encryptableSpec);

        Base64.Encoder encoder = Base64.getEncoder();
        return Map.of(
                "salt", encoder.encodeToString(salt),
                "hash", encoder.encodeToString(original.getHash()),
                "iteration_count", String.valueOf(iterationCount));
    }
}
