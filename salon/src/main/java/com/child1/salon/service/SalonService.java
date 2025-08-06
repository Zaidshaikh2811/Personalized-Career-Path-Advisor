package com.child1.salon.service;

import com.child1.salon.DTO.SalonDto;
import com.child1.salon.modal.Salon;
import com.child1.salon.repo.SalonRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class SalonService    {

    private SalonRepo salonRepo;
    public List<Salon>   getAllSalons() {
        return salonRepo.findAll();
    }

    public void addSalon(Salon salon) {
        salonRepo.save(salon);
    }
    public Salon getSalonByName(String name) {
        return salonRepo.findByName(name);
    }
    public Salon getSalonByEmail(String email) {
        return salonRepo.findByEmail(email);
    }
    public void deleteSalonById(Integer id) {
        salonRepo.deleteById(id);
    }
    public Salon getSalonById(Integer id) {
        return salonRepo.findById(id).orElse(null);
    }
    public void updateSalon(Salon salon , Long id) {

        if (salon == null || salon.getId() == null || !salon.getId().equals(id)) {
            throw new IllegalArgumentException("Salon ID mismatch or salon is null");
        }

        Salon existingSalon = salonRepo.findById(Math.toIntExact(salon.getId())).orElse(null);
        if (existingSalon != null && existingSalon.getId().equals(salon.getId())) {
            existingSalon.setName(salon.getName());
            existingSalon.setAddress(salon.getAddress());
            existingSalon.setPhoneNumber(salon.getPhoneNumber());
            existingSalon.setDescription(salon.getDescription());
            existingSalon.setEmail(salon.getEmail());
            existingSalon.setWebsite(salon.getWebsite());
            existingSalon.setCity(salon.getCity());
            existingSalon.setState(salon.getState());
            existingSalon.setZipCode(salon.getZipCode());
            existingSalon.setCountry(salon.getCountry());
            existingSalon.setOpenTime(salon.getOpenTime());
            existingSalon.setCloseTime(salon.getCloseTime());
            salonRepo.save(existingSalon);
        }
    }




}
