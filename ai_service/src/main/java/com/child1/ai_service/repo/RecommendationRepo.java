package com.child1.ai_service.repo;


import com.child1.ai_service.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepo  extends MongoRepository<Recommendation, String> {


     List<Recommendation> findByUserId(String userId);
     List<Recommendation> findByActivityId(String activityId);

}
