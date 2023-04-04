package com.example.app.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long _id;

    private String name;
    private String password;
    private String email;
    private String phonenum;
    private String mostFreCity;
    private String carEge;
    private String carBrand;
    private String carType;
    private String gasType;
    private String purpose;
}
