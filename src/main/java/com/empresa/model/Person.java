package com.empresa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    
    private String id;
    private String name;
    private String email;
    private Integer age;
    private String status;
    
    public Person(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.status = "PENDING";
    }
}