package com.clausetrail.repository;

import com.clausetrail.model.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId, Sort sort);
    List<Notification> findByUserIdAndReadFalse(String userId, Sort sort);
    long countByUserIdAndReadFalse(String userId);
}
