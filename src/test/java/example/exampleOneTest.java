package example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;

public class exampleOneTest {


    @Test
    public void shouldTest01() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        String body = "{\n" +
                "  \"petId\": 1,\n" +
                "  \"quantity\": 1,\n" +
                "  \"shipDate\": \"2021-03-13T13:35:01.615+0000\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";

        JSONObject jsonBody = new JSONObject(body);

        Response response = given()
                .contentType("application/json")
                .body(body)
                .post("/store/order")
                .then()
                .extract()
                .response();

        assertThat("Status code kontrolu 200 olmali", response.getStatusCode(), equalTo(200));
        assertThat("id kontrolu ", response.getBody().jsonPath().getString("id"), not((equalTo(null))));
        assertThat("status kontrolu ", response.getBody().jsonPath().getString("status"), equalTo(jsonBody.get("status").toString()));

        String id = response.getBody().jsonPath().getString("id");

        response =get("/store/order/" + id);

        assertThat("Status code kontrolu 200 olmali", response.getStatusCode(), equalTo(200));
        assertThat("id kontrolu ", response.getBody().jsonPath().getString("id"), equalTo(id));
        assertThat("status kontrolu ", response.getBody().jsonPath().getString("status"), equalTo(jsonBody.get("status").toString()));

        response = given().delete("/store/order/" + id)
                .then()
                .extract()
                .response();

        assertThat("Status code kontrolu 200 olmali", response.getStatusCode(), equalTo(200));
        assertThat("id kontrolu ", response.getBody().jsonPath().getString("message"), equalTo(id));

        response =get("/store/order/" + id);

        String expectedMessage = "Order not found";

        assertThat("Status code kontrolu 404 olmali", response.getStatusCode(), equalTo(404));
        assertThat("mesaj kontrolu ", response.getBody().jsonPath().getString("message"), equalTo(expectedMessage));
    }

    private Response get(String endpoint){
        return given()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

}
