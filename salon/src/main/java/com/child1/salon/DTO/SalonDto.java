package com.child1.salon.DTO;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class SalonDto {

    private Long id;

    private String name;

    private List<String> images ;
    private String address;
    private String phoneNumber;

    private String description;

    private String email;

    private String website;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private Long ownerId;

    private LocalDateTime openTime;
    private LocalDateTime closeTime;



}
