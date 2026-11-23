package at.htl.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StudentEndpointTest {

    // Holt Tokens vom Keycloak, den Dev Services für die Tests starten
    // (Realm aus quarkus-realm.json, Client/Secret aus application.properties)
    static final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private RequestSpecification asUser() {
        return given().auth().oauth2(keycloakClient.getAccessToken("susi", "passme", "quarkus-client"));
    }

    private RequestSpecification asAdmin() {
        return given().auth().oauth2(keycloakClient.getAccessToken("max", "passme", "quarkus-client"));
    }

    @Test
    void getAllWithoutLogin_401() {
        given()
                .when().get("/students")
                .then()
                .statusCode(401);
    }

    @Test
    void getAllWithInvalidToken_401() {
        given().auth().oauth2("kein.gueltiges.token")
                .when().get("/students")
                .then()
                .statusCode(401);
    }

    @Test
    void getAll() {
        asUser()
                .when().get("/students")
                .then()
                .statusCode(200)
                .body("$.size()", is(4));
    }

    @Test
    void get() {
        asUser()
                .when().get("/students/it0001")
                .then()
                .statusCode(200)
                .body(containsString("Muster"));
    }

    @Test
    void createAsUser_403() {
        asUser()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"userid": "if1002", "firstname": "Tom", "lastname": "Tester"}
                        """)
                .when().post("/students")
                .then()
                .statusCode(403);
    }

    @Test
    void createAndDelete() {
        asAdmin()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"userid": "if1001", "firstname": "Tom", "lastname": "Tester"}
                        """)
                .when().post("/students")
                .then()
                .statusCode(201)
                .header("Location", containsString("/students/if1001"));

        asUser()
                .when().get("/students/if1001")
                .then()
                .statusCode(200)
                .body(containsString("Tester"));

        asAdmin()
                .when().delete("/students/if1001")
                .then()
                .statusCode(204);
    }

    @Test
    void update() {
        asAdmin()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"userid": "it0001", "firstname": "Max2", "lastname": "Muster2"}
                        """)
                .when().put("/students")
                .then()
                .statusCode(204);

        asUser()
                .when().get("/students/it0001")
                .then()
                .statusCode(200)
                .body(containsString("Muster2"));

        asAdmin()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"userid": "it0001", "firstname": "Max", "lastname": "Muster"}
                        """)
                .when().put("/students")
                .then()
                .statusCode(204);
    }
}
