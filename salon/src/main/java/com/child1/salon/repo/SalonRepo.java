package com.child1.salon.repo;


import com.child1.salon.modal.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface SalonRepo extends JpaRepository<Salon,Integer> {


     Salon findByName(String name);
        Salon findByEmail(String email);





}
