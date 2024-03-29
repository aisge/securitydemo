package at.htl.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class StudentEndpointTest {


    @Test
    void getAll() {
        given()
                .when().get("/students")
                .then()
                .statusCode(200)
                .body("$.size()", is(4));
    }

    @Test
    void get() {
        given()
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
                .body(tt.toString())
                .when()
                .post("/students")
                .then()
                .statusCode(201)
                .header("Location", containsString("/students/if1001"));

        given()
                .when().get("/students/if1001")
                .then()
                .statusCode(200)
                .body(containsString("Tester"));

        given().when()
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(mm.toString())
                .when().put("/students")
                .then()
                .statusCode(204);

        given()
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(mm.toString())
                .when().put("/students")
                .then()
                .statusCode(204);
    }

}