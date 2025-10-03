package com.empresa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.empresa.model.Person;
import com.empresa.service.PersonService;

@SpringBootTest(classes = Application.class)
public class PersonDecisionTest {

  @Autowired
  private PersonService personService;

  @BeforeEach
  public void setUp() {
    // Reset service state if needed
  }

  @Test
  public void testAdultPersonApproval() {
    Person person = new Person();
    person.setName("Adult Test");
    person.setAge(25);
    person.setEmail("adult@test.com");

    Person result = personService.createPerson(person);

    assertNotNull(result.getId());
    assertEquals("Adult Test", result.getName());
    assertEquals(25, result.getAge());
    assertEquals("APPROVED", result.getStatus());
  }

  @Test
  public void testMinorPersonDenial() {
    Person person = new Person();
    person.setName("Minor Test");
    person.setAge(16);
    person.setEmail("minor@test.com");

    Person result = personService.createPerson(person);

    assertNotNull(result.getId());
    assertEquals("Minor Test", result.getName());
    assertEquals(16, result.getAge());
    assertEquals("DENIED", result.getStatus());
  }

  @Test
  public void testBoundaryAge18Approval() {
    Person person = new Person();
    person.setName("Boundary Test");
    person.setAge(18);
    person.setEmail("boundary@test.com");

    Person result = personService.createPerson(person);

    assertNotNull(result.getId());
    assertEquals("Boundary Test", result.getName());
    assertEquals(18, result.getAge());
    assertEquals("APPROVED", result.getStatus());
  }

  @Test
  public void testBoundaryAge17Denial() {
    Person person = new Person();
    person.setName("Minor Boundary Test");
    person.setAge(17);
    person.setEmail("minor.boundary@test.com");

    Person result = personService.createPerson(person);

    assertNotNull(result.getId());
    assertEquals("Minor Boundary Test", result.getName());
    assertEquals(17, result.getAge());
    assertEquals("DENIED", result.getStatus());
  }

  @Test
  public void testDMNEvaluationDirectApproved() {
    String result = personService.evaluateDMN("Test Person", 25);
    assertEquals("APPROVED", result);
  }

  @Test
  public void testDMNEvaluationDirectDenied() {
    String result = personService.evaluateDMN("Test Minor", 16);
    assertEquals("DENIED", result);
  }

  @Test
  public void testDMNEvaluationBoundary() {
    String resultApproved = personService.evaluateDMN("Test Boundary Adult", 18);
    String resultDenied = personService.evaluateDMN("Test Boundary Minor", 17);

    assertEquals("APPROVED", resultApproved);
    assertEquals("DENIED", resultDenied);
  }

  @Test
  public void testPersonServiceStorage() {
    // Create multiple persons
    Person person1 = new Person();
    person1.setName("Person 1");
    person1.setAge(25);
    person1.setEmail("person1@test.com");

    Person person2 = new Person();
    person2.setName("Person 2");
    person2.setAge(16);
    person2.setEmail("person2@test.com");

    personService.createPerson(person1);
    personService.createPerson(person2);

    // Verify they are stored
    var allPersons = personService.getAllPersons();
    assertTrue(allPersons.size() >= 2);

    // Verify the persons exist in the list
    boolean foundAdult = allPersons.stream()
        .anyMatch(p -> "Person 1".equals(p.getName()) && "APPROVED".equals(p.getStatus()));
    boolean foundMinor = allPersons.stream()
        .anyMatch(p -> "Person 2".equals(p.getName()) && "DENIED".equals(p.getStatus()));

    assertTrue(foundAdult, "Adult person should be found with APPROVED status");
    assertTrue(foundMinor, "Minor person should be found with DENIED status");
  }
}