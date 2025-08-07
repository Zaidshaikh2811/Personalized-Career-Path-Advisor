package com.child1.salon.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "salon")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Salon name is required")
    @Size(max = 100, message = "Salon name must be at most 100 characters")
    private String name;

    @ElementCollection
    private List<@NotBlank(message = "Image URL cannot be blank") String> images;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
    private String phoneNumber;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 100, message = "Website URL must be at most 100 characters")
    private String website;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;

    @Column(name = "owner_id")
    @NotNull(message = "Owner ID is required")
    private Long ownerId;



    @NotNull(message = "Open time is required")
    private LocalDateTime openTime;

    @NotNull(message = "Close time is required")
    private LocalDateTime closeTime;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;



}
