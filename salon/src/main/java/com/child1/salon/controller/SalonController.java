package com.child1.salon.controller;

import com.child1.salon.DTO.SalonDto;
import com.child1.salon.modal.Salon;
import com.child1.salon.service.SalonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salon")
@AllArgsConstructor
public class SalonController {

    private SalonService salonService;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Salon!";
    }

    @GetMapping()
    public ResponseEntity<List<Salon>> getAllSalon() {
        System.out.println("Fetching all salons");
        List<Salon> salons = salonService.getAllSalons();
        if (salons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salons);

    }

    @PostMapping("/add")
    public ResponseEntity<String> addSalon(@Valid @RequestBody Salon salon) {


        salonService.addSalon(salon);
        return ResponseEntity.ok("Salon added successfully");
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateSalon(@PathVariable Long id, @Valid @RequestBody Salon salon) {
        salonService.updateSalon(salon,id);
        return ResponseEntity.ok("Salon updated successfully");
    }


}
