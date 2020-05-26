package at.htl.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StudentEndpointTest {

    @ConfigProperty(name = "keycloak.url")
    String keycloakURL;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String credential;

    String userToken;
    String adminToken;

    @BeforeEach
    void setup() {
        // Only get tokens once
        // Workaround, because @BeforeAll needs static method where @ConfigProperty is not working...
        if (userToken != null) {
            return;
        }

        RestAssured.baseURI = keycloakURL;
        Response response = given().urlEncodingEnabled(true).auth().preemptive().basic("quarkus-client", credential)
                .param("grant_type", "password")
                .param("client_id", "quarkus-client")
                .param("username", "susi")
                .param("password", "passme")
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .post("/auth/realms/quarkus-realm/protocol/openid-connect/token")
                .then().statusCode(200).extract().response();

        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
        JsonObject object = jsonReader.readObject();
        userToken = object.getString("access_token");

        response = given().urlEncodingEnabled(true).auth().preemptive().basic("quarkus-client", credential)
                .param("grant_type", "password")
                .param("client_id", "quarkus-client")
                .param("username", "max")
                .param("password", "passme")
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .post("/auth/realms/quarkus-realm/protocol/openid-connect/token")
                .then().statusCode(200).extract().response();

        jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
        object = jsonReader.readObject();
        adminToken = object.getString("access_token");

        RestAssured.baseURI = "http://localhost:8081";
    }

    @Test
    void getAll() {
        given()
                .auth().preemptive().oauth2(userToken)
                .when().get("/students")
                .then()
                .statusCode(200)
                .body("$.size()", is(4));
    }

    @Test
    void get() {
        given()
                .auth().preemptive().oauth2(userToken)
                .when().get("/students/it0001")
                .then()
                .statusCode(200)
                .body(containsString("Muster"));
    }

    @Test
    void createAndDelete() {
        JsonObject tt = Json.createObjectBuilder()
                .add("userid", "if1001")
                .add("firstname", "Tom")
                .add("lastname", "Tester")
                .build();

        given().contentType(MediaType.APPLICATION_JSON)
                .auth().preemptive().oauth2(adminToken)
                .body(tt.toString())
                .when()
                .post("/students")
                .then()
                .statusCode(201)
                .header("Location", containsString("/students/if1001"));

        given()
                .auth().preemptive().oauth2(userToken)
                .when().get("/students/if1001")
                .then()
                .statusCode(200)
                .body(containsString("Tester"));

        given().when()
                .auth().preemptive().oauth2(adminToken)
                .delete("students/if1001")
                .then()
                .statusCode(204);
    }

    @Test
    void update() {
        JsonObject mm = Json.createObjectBuilder()
                .add("userid", "it0001")
                .add("firstname", "Max2")
                .add("lastname", "Muster2")
                .build();
        given()
                .auth().preemptive().oauth2(adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mm.toString())
                .when().put("/students")
                .then()
                .statusCode(204);

        given()
                .auth().preemptive().oauth2(userToken)
                .when().get("/students/it0001")
                .then()
                .statusCode(200)
                .body(containsString("Muster2"));

        mm = Json.createObjectBuilder()
                .add("userid", "it0001")
                .add("firstname", "Max")
                .add("lastname", "Muster")
                .build();
        given()
                .auth().preemptive().oauth2(adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mm.toString())
                .when().put("/students")
                .then()
                .statusCode(204);
    }

}