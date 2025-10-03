package com.empresa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.model.Person;
import com.empresa.service.PersonService;

@RestController
@RequestMapping("/persons")
public class PersonController {

  @Autowired
  private PersonService personService;

  @PostMapping
  public ResponseEntity<Map<String, Object>> createPerson(@RequestBody Map<String, Object> request) {
    // Soportar tanto formato directo como formato anidado
    Map<String, Object> personData = request.containsKey("person") ? (Map<String, Object>) request.get("person")
        : request;

    Person person = new Person();
    person.setName((String) personData.get("name"));
    person.setAge((Integer) personData.get("age"));
    person.setEmail((String) personData.getOrDefault("email", ""));

    Person createdPerson = personService.createPerson(person);

    // Determine if person is adult based on DMN rules
    boolean isAdult = createdPerson.getAge() >= 18;
    createdPerson.setStatus(isAdult ? "APPROVED" : "DENIED");

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of(
            "person", Map.of(
                "id", createdPerson.getId(),
                "name", createdPerson.getName(),
                "age", createdPerson.getAge(),
                "adult", isAdult,
                "status", createdPerson.getStatus())));
  }

  @GetMapping
  public ResponseEntity<List<Person>> getAllPersons() {
    return ResponseEntity.ok(personService.getAllPersons());
  }

  @PostMapping("/persons")
  public ResponseEntity<Map<String, String>> evaluateDMN(@RequestBody Map<String, Object> request) {
    Map<String, Object> personData = (Map<String, Object>) request.get("Person");

    String name = (String) personData.get("name");
    Integer age = (Integer) personData.get("age");

    String result = personService.evaluateDMN(name, age);

    return ResponseEntity.ok(Map.of(
        "PersonEligibility", result));
  }
}