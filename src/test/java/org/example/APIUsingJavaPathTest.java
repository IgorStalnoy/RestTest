package org.example;

import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class APIUsingJavaPathTest {
    public static final String URL = "http://jsonplaceholder.typicode.com/posts";

    @Test
    public void testValidateStatusCodeForGetRequest() {
        get(URL)
                .then()
                .statusCode(200);
    }

    @Test
    public void testValidateBodyForGetRequestSecond() {
        get(URL)
                .then()
                .statusCode(200)
                .header("Content-Type", notNullValue())
                .headers("Content-Type", equalTo("application/json; charset=utf-8"));
    }

    @Test
    public void testValidateBodyLengthForGetRequest() {
        get(URL)
                .then()
                .statusCode(200)
                .body("size()", equalTo(100));
    }

    @Test
    public void testCreateNewPost() {
        String requestBody = "{ \"data\": { \"userId\": 1, \"title\": \"foo\", \"body\": \"bar\" } }";
        int idValue = given()
                .header("Content-Type", ContentType.JSON)
                .body(requestBody)
                .when()
                .post(URL)
                .then()
                .assertThat()
                .statusCode(201)
                .body(notNullValue())
                .extract()
                .path("id");
        System.out.println("Id value = " + idValue);
    }

    @Test
    public void testUpdatePost() {
        String requestBody = "{ \"data\": { \"userId\": 1, \"title\": \"foo\", \"body\": \"bar\" } }";
        given()
                .header("Content-Type", ContentType.JSON)
                .body(requestBody)
                .when()
                .put(URL + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    public void testDeletePost() {
        given()
                .when()
                .delete(URL + "/1")
                .then()
                .statusCode(200);
    }
}
