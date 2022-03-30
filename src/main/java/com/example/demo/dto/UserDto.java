package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String employeeNumber;
    private String bloodGroup;
    private String password;
}
