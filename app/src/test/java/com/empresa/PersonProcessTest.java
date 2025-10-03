package com.empresa;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "server.port=0" })
public class PersonProcessTest {

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
    RestAssured.baseURI = "http://localhost";
  }

  @Test
  public void testHealthEndpoint() {
    given()
        .when()
        .get("/actuator/health")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"));
  }

  @Test
  public void testCreateAdultPerson() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"John Doe\",\"age\":25,\"email\":\"john@example.com\"}")
        .when()
        .post("/persons")
        .then()
        .statusCode(201)
        .body("person.name", equalTo("John Doe"))
        .body("person.age", equalTo(25))
        .body("person.adult", equalTo(true))
        .body("person.status", equalTo("APPROVED"));
  }

  @Test
  public void testCreateMinorPerson() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"Jane Smith\",\"age\":15,\"email\":\"jane@example.com\"}")
        .when()
        .post("/persons")
        .then()
        .statusCode(201)
        .body("person.name", equalTo("Jane Smith"))
        .body("person.age", equalTo(15))
        .body("person.adult", equalTo(false))
        .body("person.status", equalTo("DENIED"));
  }

  @Test
  public void testCreatePersonAtBoundary() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"Mike Wilson\",\"age\":18,\"email\":\"mike@example.com\"}")
        .when()
        .post("/persons")
        .then()
        .statusCode(201)
        .body("person.name", equalTo("Mike Wilson"))
        .body("person.age", equalTo(18))
        .body("person.adult", equalTo(true))
        .body("person.status", equalTo("APPROVED"));
  }

  @Test
  public void testGetAllPersons() {
    // Create some persons first
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"Test Person 1\",\"age\":25,\"email\":\"test1@example.com\"}")
        .when()
        .post("/persons");

    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"Test Person 2\",\"age\":16,\"email\":\"test2@example.com\"}")
        .when()
        .post("/persons");

    // Verify we can get all persons
    given()
        .when()
        .get("/persons")
        .then()
        .statusCode(200)
        .body("$", hasSize(greaterThanOrEqualTo(2)));
  }

  @Test
  public void testDMNDirectEndpoint() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"Person\":{\"name\":\"Direct DMN Test\",\"age\":30}}")
        .when()
        .post("/persons/persons")
        .then()
        .statusCode(200)
        .body("PersonEligibility", equalTo("APPROVED"));
  }

  @Test
  public void testDMNDirectEndpointMinor() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"Person\":{\"name\":\"Minor DMN Test\",\"age\":16}}")
        .when()
        .post("/persons/persons")
        .then()
        .statusCode(200)
        .body("PersonEligibility", equalTo("DENIED"));
  }
}