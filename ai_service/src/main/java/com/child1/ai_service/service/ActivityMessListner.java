package com.child1.ai_service.service;


import com.child1.ai_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessListner {



    @RabbitListener(queues = "activity-queue")
    public void receiveMessage(Activity activity) {
        System.out.println("Received message: " + activity);
        log.info("Received message: {}", activity);
    }

}
