package com.empresa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.empresa.model.Person;

@Service
public class PersonService {

  private List<Person> persons = new ArrayList<>();

  public Person createPerson(Person person) {
    // Simulate DMN decision based on age
    person.setId(UUID.randomUUID().toString());
    person.setStatus(person.getAge() >= 18 ? "APPROVED" : "DENIED");
    persons.add(person);
    return person;
  }

  public List<Person> getAllPersons() {
    return new ArrayList<>(persons);
  }

  public String evaluateDMN(String name, Integer age) {
    return age >= 18 ? "APPROVED" : "DENIED";
  }
}