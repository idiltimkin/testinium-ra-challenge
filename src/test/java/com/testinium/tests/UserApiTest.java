package com.testinium.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class UserApiTest {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void list_users_at_second_page() {
        when()
                .get("/api/users?page={page}", 2)
                .prettyPeek()
                .then().statusCode(200)
                .body("page", equalTo(2))
                .body("total_pages", equalTo(2))
                .body("total", equalTo(12))
                .body("data", hasSize(6));
    }

    @Test
    void get_user_details() {
        Integer userId = get("/api/users?page={page}", 2)
                .prettyPeek()
                .jsonPath()
                .getInt("data[0].id");


        when()
                .get("/api/users/{userId}", userId)
                .prettyPeek()
                .then().statusCode(200)
                .body("data.id", equalTo(userId))
                .body("data.email", equalTo("michael.lawson@reqres.in"))
                .body("data.first_name", equalTo("Michael"))
                .body("data.last_name", equalTo("Lawson"));
    }

    @Test
    void return_404_for_unknown_user() {
        when()
                .get("/api/users/{userId}", "dummy")
                .prettyPeek()
                .then().statusCode(404);
    }

    @Test
    void delete_user() {
        when()
                .delete("/api/users/{userId}",2)
                .prettyPeek()
                .then().statusCode(204);
    }

    @Test
    void login_successfully() {
        given()
                .contentType(ContentType.JSON)
                .body(new LoginForm("eve.holt@reqres.in", "cityslicka"))
                .when()
                .post("/api/login")
                .prettyPeek()
                .then().statusCode(200)
                .body("token", equalTo("QpwL5tke4Pnpja7X4"));
    }

}

class LoginForm {
    public String email;
    public String password;

    public LoginForm(String email, String password) {
        this.email = email;
        this.password = password;
    }
}