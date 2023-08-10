import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class APITests {
    private final int unexistringPetId = 232323493;
    Integer id = 1220;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/";
    }

    @Test
    public void petNotFoundTestWithAssert() {
        baseURI += "pet/" + unexistringPetId;
        requestSpecification = given();
        Response response = requestSpecification.get();
        System.out.println("Response: " + response.asPrettyString());
        assertEquals(404, response.statusCode(), "Не тот status code");
        assertEquals("HTTP/1.1 404 Not Found", response.statusLine(), "Не корректная status line");
        assertEquals("Pet not found", response.jsonPath().get("message"), "Не то сообщение об ошибке");
    }

    @Test
    public void petNotFoundTest() {
        baseURI += "pet/" + unexistringPetId;
        requestSpecification = given();
        Response response = requestSpecification.get();
        System.out.println("Response: " + response.asPrettyString());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
        validatableResponse.statusLine("HTTP/1.1 404 Not Found");
        validatableResponse.body("message", equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Пример get запроса")
    public void petNotFoundTest_BDD() {
        given().when()
                .get(baseURI + "pet/{id}", unexistringPetId)
                .then().statusCode(404)
                .log().all()
                .statusLine("HTTP/1.1 404 Not Found")
                .body("type", equalTo("error"))
                .body("message", equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Пример post запроса")
    public void newPetTest() {
        String name = "dogg";
        String status = "sold";

        Map<String, String> request = new HashMap<>();
        request.put("id", id.toString());
        request.put("name", name);
        request.put("status", status);

        given().contentType("application/json")
                .body(request)
                .when()
                .post(baseURI + "pet/")
                .then()
                .log().all()
                .assertThat()
                .time(lessThan(3000L))
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(name))
                .body("status", equalTo(status));
    }
    @Test
    @DisplayName("Пример delete запроса после создания питомца")
    public void checkDelTest(){
        delete(baseURI + "pet/" + id)
                .then().statusCode(200)
                .log().all()
                .assertThat()
                .body("type", equalTo("unknown"));
    }
}
