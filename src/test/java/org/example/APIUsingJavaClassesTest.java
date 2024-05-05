package org.example;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class APIUsingJavaClassesTest {

    public static final String URL = "http://jsonplaceholder.typicode.com/posts";
    public static final String GET_JSON_SCHEMA_PATH = "src/test/resources/PostsGetJsonSchema.json";
    public static final String POST_JSON_SCHEMA_PATH = "src/test/resources/PostsPostJsonSchema.json";
    public static final String PUT_JSON_SCHEMA_PATH = "src/test/resources/PostsPutJsonSchema.json";
    public static final String DELETE_JSON_SCHEMA_PATH = "src/test/resources/PostsDeleteJsonSchema.json";

    @Test
    public void testValidateStatusCodeForGetRequest() {
        get(URL)
                .then()
                .statusCode(200);
    }

    @Test
    public void testValidateHeaderForGetRequest() {
        ExtractableResponse<Response> response = get(URL)
                .then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchema(new File(GET_JSON_SCHEMA_PATH)))
                .extract();

        Assert.assertTrue("Header does header with required type", response.headers().hasHeaderWithName("Content-Type"));
        Assert.assertEquals("Header with \"Content-Type\" name does not contain valid value",
                "application/json; charset=utf-8", response.headers().get("Content-Type").getValue());
    }

    @Test
    public void testValidateBodyLengthForGetRequest() {
        Post[] responseBody = get(URL)
                .then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchema(new File(GET_JSON_SCHEMA_PATH)))
                .extract().as(Post[].class);

        Assert.assertEquals("The response does not have 100 values", 100, responseBody.length);
    }

    @Test
    public void testCreateNewPost() {
        Post post = new Post();
        post.setTitle("foo");
        post.setBody("bar");
        post.setUserId(1);
        RequestBody requestBody = new RequestBody();
        requestBody.setData(post);
        System.out.println(requestBody);

        ExtractableResponse<Response> response = given()
                .log().all()
                .header("Content-Type", ContentType.JSON)
                .body(requestBody)
                .when()
                .post(URL)
                .then()
                .assertThat()
                .body(matchesJsonSchema(new File(POST_JSON_SCHEMA_PATH)))
                .extract();

        Assert.assertEquals("Status code is not 201", 201, response.statusCode());
        Assert.assertFalse("Response body is empty", response.body().asString().isEmpty());
        System.out.println(response.body().jsonPath().getInt("id"));
    }

    @Test
    public void testUpdatePost() {
        Post post = new Post();
        post.setTitle("foo");
        post.setBody("bar");
        post.setUserId(1);
        post.setId(1);
        RequestBody requestBody = new RequestBody();
        requestBody.setData(post);

        ExtractableResponse<Response> response = given()
                .log().all()
                .header("Content-Type", ContentType.JSON)
                .body(requestBody)
                .when()
                .put(URL + "/1")
                .then()
                .assertThat()
                .body(matchesJsonSchema(new File(PUT_JSON_SCHEMA_PATH)))
                .extract();

        Assert.assertEquals("Status code is not 200", 200, response.statusCode());
        Assert.assertEquals("Id value in response body is not 1", 1, response.body().jsonPath().getInt("id"));
    }

    @Test
    public void testDeletePost() {
        ExtractableResponse<Response> response = given()
                .log().all()
                .when()
                .delete(URL + "/1")
                .then()
                .assertThat()
                .body(matchesJsonSchema(new File(DELETE_JSON_SCHEMA_PATH)))
                .extract();

        Assert.assertEquals("Status code is not 200", 200, response.statusCode());
    }

}
