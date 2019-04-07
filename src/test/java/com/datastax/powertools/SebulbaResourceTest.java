package com.datastax.powertools;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SebulbaResourceTest {

    @Test
    public void testEventEndpoint() {
        String uuid = UUID.randomUUID().toString();
        given()
                .pathParam("name", uuid)
                .when().get("/sebulba/event/{name}")
                .then()
                .statusCode(200)
                .body(is("event saved for: " + uuid));
    }

}