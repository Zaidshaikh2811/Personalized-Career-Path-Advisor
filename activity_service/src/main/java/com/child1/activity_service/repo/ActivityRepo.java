package com.child1.activity_service.repo;


import com.child1.activity_service.Model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepo extends MongoRepository<Activity, Long> {







}
