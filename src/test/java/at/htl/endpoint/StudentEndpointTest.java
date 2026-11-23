package at.htl.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StudentEndpointTest {

    static String userToken;
    static String adminToken;

    // Tokens nur einmal holen. @BeforeAll geht hier nicht: dort ist
    // RestAssured noch nicht auf die laufende Test-App konfiguriert.
    @BeforeEach
    void login() {
        if (userToken == null) {
            userToken = login("susi", "passme");
            adminToken = login("max", "passme");
        }
    }

    static String login(String username, String password) {
        return given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(username, password))
                .when().post("/security/login")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private RequestSpecification asUser() {
        return given().auth().oauth2(userToken);
    }

    private RequestSpecification asAdmin() {
        return given().auth().oauth2(adminToken);
    }

    @Test
    void loginWithWrongPassword_401() {
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"username": "susi", "password": "falsch"}
                        """)
                .when().post("/security/login")
                .then()
                .statusCode(401);
    }

    @Test
    void info() {
        asUser()
                .when().get("/security/info")
                .then()
                .statusCode(200)
                .body(containsString("user: susi"))
                .body(containsString("hasJWT: true"));
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
